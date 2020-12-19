/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.interpreter;

import jall.exceptions.IdentifierNotInScope;
import jall.exceptions.InterpreterErrorException;
import jall.inference.Constant;
import jall.inference.Function;
import jall.inference.Predicate;
import jall.inference.Variable;
import jall.lang.ErrorReporter;
import jall.lang.ErrorReporter.ErrorType;
import jall.lang.Token;
import jall.lang.ast.Identifier;
import jall.lang.ast.Program;
import jall.lang.ast.Visitor;
import jall.lang.ast.command.*;
import jall.lang.ast.declaration.*;
import jall.lang.ast.expression.*;
import jall.lang.ast.literal.*;
import jall.lang.ast.operator.ArithmeticOperator;
import jall.lang.ast.operator.AssignOperator;
import jall.lang.ast.operator.ComparisonOperator;
import jall.lang.ast.operator.LogicalOperator;
import jall.lang.ast.sentence.*;
import jall.lang.interpreter.entities.Entity;
import jall.lang.interpreter.entities.Reference;
import jall.lang.standard.Environment;
import jall.lang.type.*;

public class Interpreter implements Runnable {

    private ErrorReporter err = null;
    private Program program = null;
    private Machine machine = null;
    public Interpreter(Program program, ErrorReporter err) {
        this.err = err;
        this.program = program;
        machine = new Machine();
    }

    @Override
    public void run() {
        machine.run();
    }

    private enum MachineState {
        Running,
        Return,
        Break,
        Stopped,
        Resume
    }

    public class Machine implements Visitor {

        private Memory memory = null;
        private MachineState state = null;
        private StateMachine stateMachine = null;
        private ObjectLiteral returnRegister = null;

        public Machine() {
            this.memory = new Memory();
            this.stateMachine = new StateMachine();
            returnRegister = new ObjectLiteral();
        }

        private void throwError(Exception e, Token token) {
            String msg = e.getMessage();
            if (token != null)
                msg = "Line " + err.getTokenLine(token) + " near '" + token.getText() + "'. " + msg;
            err.log(new ErrorReporter.Error(msg, ErrorType.Contextual, e));
            throw new InterpreterErrorException(msg);
        }

        private void throwError(String msg, Token token) {
            if (token != null)
                msg = "Line " + err.getTokenLine(token) + " near '" + token.getText() + "'. " + msg;
            err.log(new ErrorReporter.Error(msg, ErrorType.Contextual));
            throw new InterpreterErrorException(msg);
        }

        public void run() {
            onRunning();
            onMachineStop();
        }

        // Command
        @Override
        public Object visit(Program program, Object args) {
            program.getCommand().accept(this, args);
            return null;
        }

        @Override
        public Object visit(AssignCommand assignCommand, Object args) {

            int depth = 0;

            // Gets Name expression
            NameExpression nameExpression = assignCommand.getNameExpressions()[depth];

            // Gets the entity to be assign to and the literal from expression
            Entity entity = (Entity) nameExpression.getIdentifier().accept(this, args);
            Literal expressionLiteral = (Literal) assignCommand.getExpression().accept(this, null);

            // Gets Literal Literal
            Literal literal = entity.getLiteral();

            if (nameExpression.getType() == Expression.Type.Call) {
                CallExpression callExpression = (CallExpression) nameExpression;
                literal = (Literal) entity.getLiteral().accept(this, callExpression.getArgsExpressions());
            }

            // Evaluate expression for indices on the left hand side
            IntLiteral[] indices = new IntLiteral[nameExpression.getIndexExpressions().length];
            for (int i = 0; i < indices.length; i++)
                indices[i] = (IntLiteral) nameExpression.getIndexExpressions()[i].accept(this, null);

            // Gets literal, if array
            if (literal != null && literal.getType() == Literal.Type.Array)
                literal = (Literal) literal.accept(this, indices);

            if (depth == assignCommand.getNameExpressions().length - 1) {
                Reference reference = (Reference) entity;
                reference.assign(expressionLiteral, indices);
            } else
                visitStruct(assignCommand, depth + 1, (StructLiteral) literal, expressionLiteral);

            return null;

        }

        private Object visitStruct(AssignCommand assignCommand, int depth, StructLiteral parentLiteral, Literal expressionLiteral) {

            if (depth >= assignCommand.getNameExpressions().length)
                return null;

            // Gets Name expression
            NameExpression nameExpression = assignCommand.getNameExpressions()[depth];

            // Gets entity
            Entity entity = parentLiteral.getScope().retrieve(nameExpression.getIdentifier());

            // Gets entity Literal
            Literal literal = entity.getLiteral();

            if (nameExpression.getType() == Expression.Type.Call) {
                CallExpression callExpression = (CallExpression) nameExpression;
                ProcedureFunctionDeclaration procDeclaration = (ProcedureFunctionDeclaration) callExpression.getIdentifier().getDeclaration();
                literal = callMethod(procDeclaration, parentLiteral, callExpression.getArgsExpressions(), callExpression.getIdentifier());
            }

            IntLiteral[] indices = new IntLiteral[nameExpression.getIndexExpressions().length];
            for (int i = 0; i < indices.length; i++)
                indices[i] = (IntLiteral) nameExpression.getIndexExpressions()[i].accept(this, null);

            // Gets literal, if array
            if (literal != null && literal.getType() == Literal.Type.Array)
                literal = (Literal) literal.accept(this, indices);

            if (depth == assignCommand.getNameExpressions().length - 1) {
                Reference reference = (Reference) entity;
                reference.assign(expressionLiteral, indices);
            } else
                visitStruct(assignCommand, depth + 1, (StructLiteral) literal, expressionLiteral);

            return null;
        }

        @Override
        public Object visit(AssignDeclarationCommand command, Object args) {

            // Declares variable
            command.getDeclaration().accept(this, null);

            Entity entity = (Entity) command.getDeclaration().getIdentifier().accept(this, args);
            Literal literal = (Literal) command.getExpression().accept(this, args);

            entity.assign(literal);

            return null;
        }

        @Override
        public Object visit(CallCommand callCommand, Object args) {

            Literal literal = (Literal) callCommand.getNameExpressions()[0].accept(this, null);

            if (literal != null && literal.getType() == Literal.Type.Struct)
                return evaluate(callCommand.getNameExpressions(), 1, (StructLiteral) literal);

            return literal;

        }

        @Override
        public Object visit(DeclarationCommand command, Object args) {
            command.getDeclaration().accept(this, null);
            return null;
        }

        @Override
        public Object visit(ReturnCommand returnCommand, Object args) {
            if (returnCommand.getExpression() != null) {
                Literal literal = (Literal) returnCommand.getExpression().accept(this, args);
                returnRegister.setValue(literal);
            }
            onReturnExecution();
            return null;
        }

        @Override
        public Object visit(SequentialCommand command, Object args) {
            command.getLeft().accept(this, args);
            if (state == MachineState.Break || state == MachineState.Return)
                return null;
            else
                command.getRight().accept(this, args);
            return null;
        }

        @Override
        public Object visit(IfCommand ifCommand, Object args) {

            BooleanLiteral literal = new BooleanLiteral(true);

            // If there is an expression defined (not else command)
            if (ifCommand.getExpression() != null)
                literal = (BooleanLiteral) ifCommand.getExpression().accept(this, null);

            if (literal.getValue()) {
                memory.openScope(false);
                ifCommand.getCommand().accept(this, null);
                memory.closeScope();
            } else if (ifCommand.getElseCommand() != null)
                ifCommand.getElseCommand().accept(this, null);

            return null;

        }

        @Override
        public Object visit(WhileCommand whileCommand, Object args) {

            // Checks expression type
            BooleanLiteral literal = (BooleanLiteral) whileCommand.getExpression().accept(this, null);

            while (literal.getValue()) {
                memory.openScope(false);
                whileCommand.getCommand().accept(this, whileCommand);

                // Checks execution breaks, if occurred sequential
                // command will be responsible for stop execution in between.
                if (state == MachineState.Break) {
                    onResume();
                    memory.closeScope();
                    break;
                } else if (state == MachineState.Return) {
                    memory.closeScope();
                    break;
                } else
                    literal = (BooleanLiteral) whileCommand.getExpression().accept(this, null);

                memory.closeScope();
            }

            return null;
        }

        @Override
        public Object visit(ForCommand forCommand, Object args) {

            // Executes init Command
            memory.openScope(false);
            forCommand.getInitCommand().accept(this, null);

            // Evaluates stop condition expression
            BooleanLiteral literal = (BooleanLiteral) forCommand.getExpression().accept(this, null);

            while (literal.getValue()) {

                memory.openScope(false);

                // Executes body command
                if (forCommand.getBodyCommand() != null)
                    forCommand.getBodyCommand().accept(this, forCommand);

                // Executes incrementCommand
                forCommand.getIncrementCommand().accept(this, null);

                // Checks execution breaks, if occurred sequential
                // command will be responsible for stop execution in between.
                if (state == MachineState.Break) {
                    onResume();
                    memory.closeScope();
                    break;
                } else if (state == MachineState.Return) {
                    memory.closeScope();
                    break;
                } else
                    literal = (BooleanLiteral) forCommand.getExpression().accept(this, null);

                memory.closeScope();
            }

            memory.closeScope();

            return null;
        }

        @Override
        public Object visit(BreakCommand command, Object args) {
            onBreakExecution();
            return null;
        }

        @Override
        public Object visit(StandardProcedureCommand command, Object args) {
            Environment.execute(command, this, returnRegister);
            return null;
        }

        // Identifiers
        @Override
        public Object visit(Identifier identifier, Object args) {
            Entity entity = null;
            try {
                entity = memory.fetch(identifier);
            } catch (IdentifierNotInScope e) {
                throwError(e, identifier.getToken());
            }
            return entity;
        }

        // Operators
        @Override
        public Object visit(ArithmeticOperator operator, Object args) {
            return operator.getDeclaration();
        }

        @Override
        public Object visit(AssignOperator operator, Object args) {
            return operator.getDeclaration();
        }

        @Override
        public Object visit(ComparisonOperator operator, Object args) {
            return operator.getDeclaration();
        }

        @Override
        public Object visit(LogicalOperator operator, Object args) {
            return operator.getDeclaration();
        }

        // Declarations
        @Override
        public Object visit(SequentialDeclaration declaration, Object args) {
            declaration.left.accept(this, null);
            declaration.right.accept(this, null);
            return null;
        }

        @Override
        public Object visit(ReferenceDeclaration declaration, Object args) {
            // Creates new entity
            Entity reference = Entity.createEntity(declaration.getIdentifier());

            // Stores reference
            memory.store(declaration.getIdentifier(), reference);

            if (!declaration.getTypeDenoter().isComplex())
                reference.assign(Literal.createLiteral(declaration));

            return null;
        }

        @Override
        public Object visit(ProcedureFunctionDeclaration declaration, Object args) {
            return declareAndCreateLiteral(declaration, args);
        }

        @Override
        public Object visit(StructDeclaration declaration, Object args) {

            // Creates entity if a reference as declared with it
            if (declaration.getIdentifier() != null) {

                // Creates new entity
                Entity reference = Entity.createEntity(declaration.getIdentifier());

                // Stores reference
                memory.store(declaration.getIdentifier(), reference);

            }

            return null;
        }

        @Override
        public Object visit(SentenceDeclaration declaration, Object args) {
            return declareAndCreateLiteral(declaration, args);
        }

        @Override
        public Object visit(RuleDeclaration declaration, Object args) {
            return declareAndCreateLiteral(declaration, args);
        }

        @Override
        public Object visit(FactDeclaration declaration, Object args) {
            return declareAndCreateLiteral(declaration, args);
        }

        @Override
        public Object visit(FunctionDeclaration declaration, Object args) {
            return declareAndCreateLiteral(declaration, args);
        }

        @Override
        public Object visit(TypeDeclaration declaration, Object args) {
            return null;
        }

        // Expressions
        @Override
        public Object visit(BinaryExpression expression, Object args) {
            // Gets types of the two child expressions
            Literal right = (Literal) expression.getRight().accept(this, null);
            Literal left = (Literal) expression.getLeft().accept(this, null);

            // Gets return type of expression
            Literal result = expression.getOperator().evaluate(left, right);

            return result;

        }

        @Override
        public Object visit(BooleanExpression expression, Object args) {
            return expression.getLiteral();
        }

        @Override
        public Object visit(CallExpression callExpression, Object args) {

            // Gets declaration
            Declaration declaration = callExpression.getIdentifier().getDeclaration();
            Entity entity = null;
            Literal literal = null;

            // When it is not a method
            if (declaration.getParent() == null) {

                // If the call expression is from a procedure, validates arguments (may be from a variable)
                if (declaration.getType() == Declaration.DeclarationType.ProcedureFunction) {
                    ProcedureFunctionDeclaration procDeclaration = (ProcedureFunctionDeclaration) declaration;
                    validateArguments(callExpression.getIdentifier(), procDeclaration.getArgsDeclarations(), callExpression.getArgsExpressions());
                }

                // Fetch entity
                entity = memory.fetch(callExpression.getIdentifier());

                // Calls procedure if referencing a procedure literal
                literal = (Literal) entity.getLiteral().accept(this, callExpression.getArgsExpressions());

            } else {
                Entity struct = memory.fetch(new Identifier("this"));
                literal = callMethod((ProcedureFunctionDeclaration) declaration, (StructLiteral) struct.getLiteral(), callExpression.getArgsExpressions(), callExpression.getIdentifier());
            }

            return literal;
        }

        @Override
        public Object visit(DereferenceExpression expression, Object args) {

            // Gets the highest level reference and enters its identifier
            StructLiteral structLiteral = (StructLiteral) expression.getNameExpressions()[0].accept(this, null);

            // if complex visits children, know it is a complex type as it is a dereference
            return evaluate(expression.getNameExpressions(), 1, structLiteral);

        }

        @Override
        public Object visit(DoubleExpression expression, Object args) {
            return expression.getLiteral();
        }

        @Override
        public Object visit(IntegerExpression expression, Object args) {
            return expression.getLiteral();
        }

        @Override
        public Object visit(StringExpression expression, Object args) {
            return expression.getLiteral();
        }

        @Override
        public Object visit(UnaryExpression expression, Object args) {

            // Visits operator
            expression.getOperator().accept(this, null);

            // Gets literal and evaluates with operator
            Literal literal = (Literal) expression.getExpression().accept(this, null);
            Literal result = expression.getOperator().evaluate(literal);

            return result;

        }

        @Override
        public Object visit(ReferenceExpression expression, Object args) {

            Entity entity = (Entity) expression.getIdentifier().accept(this, args);
            Literal literal = entity.getLiteral();

            // Evaluate expression if it is an array
            if (expression.getIndexExpressions().length > 0) {
                IntLiteral[] indices = new IntLiteral[expression.getIndexExpressions().length];
                for (int i = 0; i < indices.length; i++)
                    indices[i] = (IntLiteral) expression.getIndexExpressions()[i].accept(this, null);

                if (literal.getType() == Literal.Type.Object) {
                    ObjectLiteral obj = (ObjectLiteral) literal;
                    literal = obj.getValue();
                }

                ArrayLiteral arrLiteral = (ArrayLiteral) literal;
                literal = arrLiteral.getElementLiteral(indices);
            }

            return literal;
        }

        @Override
        public Object visit(VoidExpression expression, Object args) {
            return null;
        }

        @Override
        public Object visit(NullExpression expression, Object args) {
            return expression.getLiteral();
        }

        /**
         * Returns struct or array literal.
         *
         * @param newExpression
         * @param args
         * @return
         */
        @Override
        public Object visit(NewExpression newExpression, Object args) {

            Literal literal = null;

            TypeDenoter typeDenoter = newExpression.getIdentifier().getDeclaration().getTypeDenoter();

            if (!newExpression.isArray()) {

                // Gets prototype type denoter
                StructDeclaration typeDeclaration = (StructDeclaration) typeDenoter.getIdentifier().getDeclaration();
                StructTypeDenoter structTypeDenoter = (StructTypeDenoter) typeDeclaration.getTypeDenoter();

                // Creates literal from type denoter
                literal = Literal.createLiteral(structTypeDenoter);

                // If constructor exists, calls it
                for (Declaration fieldDeclaration : typeDeclaration.getFieldDeclarations()) {
                    if (fieldDeclaration.getIdentifier().getText().equals(structTypeDenoter.getIdentifier().getText()) &&
                            fieldDeclaration.getType() == Declaration.DeclarationType.ProcedureFunction) {

                        // Gets procedure declaration
                        ProcedureFunctionDeclaration procDeclaration = (ProcedureFunctionDeclaration) fieldDeclaration;

                        // Executes constructor
                        callMethod(procDeclaration, (StructLiteral) literal, newExpression.getExpressionsArgs(), newExpression.getIdentifier());

                        break;

                    }
                }

            } else {

                // Evaluates Expressions
                IntLiteral[] sizes = new IntLiteral[newExpression.getExpressionsArgs().length];
                for (int i = 0; i < sizes.length; i++)
                    sizes[i] = (IntLiteral) newExpression.getExpressionsArgs()[i].accept(this, null);

                literal = ArrayLiteral.createLiteral(typeDenoter, sizes);

            }

            return literal;

        }

        // Literals
        @Override
        public Object visit(BooleanLiteral literal, Object args) {
            return literal;
        }

        @Override
        public Object visit(DoubleLiteral literal, Object args) {
            return literal;
        }

        @Override
        public Object visit(IntLiteral literal, Object args) {
            return literal;
        }

        @Override
        public Object visit(StringLiteral literal, Object args) {
            return literal;
        }

        @Override
        public Object visit(ArrayLiteral arrLiteral, Object args) {
            Literal literal = arrLiteral;
            if (args != null && (args instanceof IntLiteral[])) {
                IntLiteral[] indices = (IntLiteral[]) args;
                if (indices.length > 0)
                    literal = arrLiteral.getElementLiteral(indices);
            }
            return literal;
        }

        @Override
        public Object visit(StructLiteral structLiteral, Object args) {
            Literal literal = structLiteral;
            if (args != null && (args instanceof Identifier)) {
                Identifier fieldIdentifier = (Identifier) args;
                literal = structLiteral.getScope().retrieve(fieldIdentifier).getLiteral();
            }
            return literal;
        }

        @Override
        public Object visit(ObjectLiteral literal, Object args) {
            return literal.getValue();
        }

        @Override
        public Object visit(SentenceLiteral literal, Object args) {
            return literal;
        }

        @Override
        public Object visit(FunctionLiteral literal, Object args) {
            return literal;
        }

        @Override
        public Object visit(ProcedureFunctionLiteral procedure, Object argsExpressions) {
            Literal literal = null;
            // Call back case
            if (argsExpressions != null) {
                Expression[] expressionArgs = (Expression[]) argsExpressions;
                Literal[] args = evaluate(expressionArgs);
                literal = callProcedure(procedure, args);
            } else
                literal = procedure;
            return literal;
        }

        @Override
        public Object visit(NullLiteral literal, Object args) {
            return literal;
        }

        // Terms
        @Override
        public Object visit(Constant term, Object args) {
            return term;
        }

        @Override
        public Object visit(Function term, Object args) {
            return term;
        }

        @Override
        public Object visit(Predicate predicate, Object args) {
            return predicate.getValue();
        }

        @Override
        public Object visit(Variable term, Object args) {
            return term;
        }

        // Sentences
        @Override
        public Object visit(BinarySentence sentence, Object args) {
            return sentence;
        }

        @Override
        public Object visit(BindingSentence sentence, Object args) {
            return sentence;
        }

        @Override
        public Object visit(BooleanSentence sentence, Object args) {
            return sentence;
        }

        @Override
        public Object visit(Rule sentence, Object args) {
            return sentence;
        }

        @Override
        public Object visit(ParenSentence sentence, Object args) {
            return sentence;
        }

        @Override
        public Object visit(QuantifiedSentence sentence, Object args) {
            return sentence;
        }

        @Override
        public Object visit(NegationSentence sentence, Object args) {
            return sentence;
        }

        // Types
        @Override
        public Object visit(ArrayTypeDenoter typeDenoter, Object args) {
            return typeDenoter.getType();
        }

        @Override
        public Object visit(BooleanTypeDenoter typeDenoter, Object args) {
            return typeDenoter.getType();
        }

        @Override
        public Object visit(DoubleTypeDenoter typeDenoter, Object args) {
            return typeDenoter.getType();
        }

        @Override
        public Object visit(FactTypeDenoter typeDenoter, Object args) {
            return typeDenoter.getType();
        }

        @Override
        public Object visit(IntTypeDenoter typeDenoter, Object args) {
            return typeDenoter.getType();
        }

        @Override
        public Object visit(ObjectTypeDenoter typeDenoter, Object args) {
            return typeDenoter.getType();
        }

        @Override
        public Object visit(RuleTypeDenoter typeDenoter, Object args) {
            return typeDenoter.getType();
        }

        @Override
        public Object visit(SentenceTypeDenoter typeDenoter, Object args) {
            return typeDenoter.getType();
        }

        @Override
        public Object visit(StringTypeDenoter typeDenoter, Object args) {
            return typeDenoter.getType();
        }

        @Override
        public Object visit(StructTypeDenoter typeDenoter, Object args) {
            return typeDenoter.getType();
        }

        @Override
        public Object visit(VoidTypeDenoter typeDenoter, Object args) {
            return typeDenoter.getType();
        }

        @Override
        public Object visit(FunctionTypeDenoter typeDenoter, Object args) {
            return typeDenoter.getType();
        }

        // Support methods

        /**
         * Evaluates an array of name expressions recursively. Returns last literal.
         */
        private Literal evaluate(NameExpression[] nameExpressions, int depth, StructLiteral parentLiteral) {

            if (depth >= nameExpressions.length)
                return null;

            Literal literal = null;

            // Gets expression from array
            NameExpression nameExpression = nameExpressions[depth];

            // Checks if it is a call expression
            if (nameExpression.getType() == Expression.Type.Call) {

                // Gets pointer to call expression
                CallExpression callExpression = (CallExpression) nameExpression;

                // Gets procedure declaration
                ProcedureFunctionDeclaration procDeclaration = (ProcedureFunctionDeclaration) callExpression.getIdentifier().getDeclaration();

                // Executes procedure
                literal = callMethod(procDeclaration, parentLiteral, callExpression.getArgsExpressions(), callExpression.getIdentifier());

            } else
                literal = parentLiteral.getScope().retrieve(nameExpression.getIdentifier()).getLiteral();

            // Evaluates array dereference
            IntLiteral[] indices = new IntLiteral[nameExpression.getIndexExpressions().length];
            for (int i = 0; i < indices.length; i++)
                indices[i] = (IntLiteral) nameExpression.getIndexExpressions()[i].accept(this, null);

            if (literal != null)
                literal = (Literal) literal.accept(this, indices);

            if (depth == nameExpressions.length - 1)
                return literal;
            else
                return evaluate(nameExpressions, depth + 1, (StructLiteral) literal);

        }

        /**
         * Executes a procedure given expressions, object structure and identifier
         *
         * @param procDeclaration
         * @param objectStruct
         * @param argExpressions
         * @param callerIdentifier
         * @return
         */
        private Literal callMethod(ProcedureFunctionDeclaration procDeclaration, StructLiteral objectStruct, Expression[] argExpressions, Identifier callerIdentifier) {
            // Evaluates expressions
            Literal[] args = evaluate(argExpressions);
            return callMethod(procDeclaration, objectStruct, args, callerIdentifier);
        }

        /**
         * Executes a procedure given literal args, object structure and identifier
         *
         * @param procDeclaration
         * @param objectStruct
         * @param args
         * @param callerIdentifier
         * @return
         */
        public Literal callMethod(ProcedureFunctionDeclaration procDeclaration, StructLiteral objectStruct, Literal[] args, Identifier callerIdentifier) {

            Literal literal = null;

            // Validates expression with arguments
            validateArguments(callerIdentifier, procDeclaration.getArgsDeclarations(), args);

            // Open scope
            memory.openScope(false);

            // Initializes procedure
            initializeProcedure(procDeclaration.getArgsDeclarations(), args);

            // Opens struct scope
            Scope scope = objectStruct.getScope();
            scope.setLocked(true);
            memory.insertScope(objectStruct.getScope(), memory.openScopes() - 1);

            // Executes command
            returnRegister.setValue(null);
            if (procDeclaration.getCommand() != null)
                procDeclaration.getCommand().accept(this, null);
            literal = returnRegister.getValue();

            // Closes scopes
            memory.closeScope();
            memory.closeScope();
            scope.setLocked(false);

            // Resumes Engine
            onResume();

            return literal;

        }

        /**
         * Calls procedures
         *
         * @param procedure
         * @param args
         * @return
         */
        public Literal callProcedure(ProcedureFunctionLiteral procedure, Literal[] args) {


            //Sets return register to null
            returnRegister.setValue(null);

            // Validates expression with arguments
            validateArguments(procedure.getArgsDeclarations(), args);

            // Open scope
            memory.openScope(true);

            // Initializes procedure
            initializeProcedure(procedure.getArgsDeclarations(), args);

            // Executes command
            if (procedure.getCommand() != null)
                procedure.getCommand().accept(this, null);

            // Closes scope
            memory.closeScope();

            // Resumes in case of returns
            onResume();

            // Returns register value
            return returnRegister.getValue();

        }

        private Literal[] evaluate(Expression[] expressions) {
            Literal[] literals = new Literal[expressions.length];

            for (int i = 0; i < expressions.length; i++)
                literals[i] = (Literal) expressions[i].accept(this, null);

            return literals;
        }

        private void initializeProcedure(Declaration[] argsDeclarations, Literal[] literals) {

            Declaration argDeclaration = null;
            Reference reference = null;
            Literal literal = null;

            for (int i = 0; i < literals.length; i++) {

                // Child reference
                argDeclaration = argsDeclarations[i];

                // Creates reference
                reference = (Reference) Entity.createEntity(argDeclaration.getIdentifier());

                // Evaluates expression and assigns literal
                literal = literals[i];
                if (literal != null)
                    literal = (Literal) literal.accept(this, null);

                reference.assign(literal);

                // Stores reference
                memory.store(argDeclaration.getIdentifier(), reference);

            }
        }

        /**
         * Throws Runtime exception if not validated
         *
         * @param identifier
         * @param argsDeclarations
         * @param expressions
         */
        private void validateArguments(Identifier identifier, Declaration[] argsDeclarations, Literal[] literals) {
            // Checks if the number of arguments match the number of parameters
            if (argsDeclarations.length != literals.length)
                throwError("The number of literals do not match the number of arguments.", identifier.getToken());
        }

        /**
         * Throws Runtime exception if not validated
         *
         * @param identifier
         * @param argsDeclarations
         * @param expressions
         */
        private void validateArguments(Identifier identifier, Declaration[] argsDeclarations, Expression[] expressions) {
            // Checks if the number of arguments match the number of parameters
            if (argsDeclarations.length != expressions.length)
                throwError("The number of expressions do not match the number of arguments.", identifier.getToken());
        }

        /**
         * Throws Runtime exception if not validated
         *
         * @param identifier
         * @param argsDeclarations
         * @param expressions
         */
        private void validateArguments(Declaration[] argsDeclarations, Literal[] expressions) {
            // Checks if the number of arguments match the number of parameters
            if (argsDeclarations.length != expressions.length)
                throw new InterpreterErrorException("The number of literals do not match the number of arguments.");
        }

        private Object declareAndCreateLiteral(Declaration declaration, Object args) {
            // Creates entity
            Entity entity = Entity.createEntity(declaration.getIdentifier());
            memory.store(declaration.getIdentifier(), entity);

            // Creates literal for entity
            entity.assign(Literal.createLiteral(declaration));

            // Visits identifier
            declaration.getIdentifier().accept(this, null);

            return null;
        }

        // Getters and setters
        public Memory getMemory() {
            return memory;
        }

        // Event handlers
        private void onRunning() {
            state = MachineState.Running;
            stateMachine.onStateChange();
        }

        private void onBreakExecution() {
            state = MachineState.Break;
            stateMachine.onStateChange();
        }

        private void onReturnExecution() {
            state = MachineState.Return;
            stateMachine.onStateChange();
        }

        private void onMachineStop() {
            state = MachineState.Stopped;
            stateMachine.onStateChange();
        }

        private void onResume() {
            state = MachineState.Resume;
            stateMachine.onStateChange();
        }

        private class StateMachine {
            public void onStateChange() {
                switch (state) {
                    case Running:
                        handleRunning();
                        break;
                    case Break:
                        handleBreak();
                        break;
                    case Return:
                        handleReturn();
                        break;
                    case Stopped:
                        handleStopped();
                        break;
                    case Resume:
                        handleResume();
                    default:
                }
            }

            private void handleRunning() {
                visit(program, null);
            }

            private void handleBreak() {
            }

            private void handleReturn() {
            }

            private void handleStopped() {
            }

            private void handleResume() {
                state = MachineState.Running;
            }

        }

    }

}

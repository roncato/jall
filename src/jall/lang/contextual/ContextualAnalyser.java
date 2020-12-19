/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.contextual;

import jall.exceptions.ContextualErrorExpression;
import jall.exceptions.IdentifierAlreadyDeclaredException;
import jall.exceptions.IdentifierNotDeclaredException;
import jall.inference.Constant;
import jall.inference.Function;
import jall.inference.Predicate;
import jall.inference.Variable;
import jall.lang.ErrorReporter;
import jall.lang.ErrorReporter.ErrorType;
import jall.lang.JallLexicon;
import jall.lang.Token;
import jall.lang.Token.TokenType;
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
import jall.lang.standard.Environment;
import jall.lang.type.*;

public class ContextualAnalyser implements Visitor {

    private ErrorReporter err = null;
    private IdentificationTable idTable = null;
    private Program program = null;

    public ContextualAnalyser(Program program, ErrorReporter err) {
        this.err = err;
        this.program = program;
        this.idTable = new IdentificationTable();
        try {
            establishStdEnvironment();
        } catch (ClassNotFoundException e) {
            throwError(e, new Token("Standard Environment", TokenType.EOT, -1));
        }
    }

    private void throwError(String msg, Token token) {
        if (token != null)
            msg = "Line " + err.getTokenLine(token) + " near '" + token.getText() + "'. " + msg;
        err.log(new ErrorReporter.Error(msg, ErrorType.Contextual));
        throw new ContextualErrorExpression(msg);
    }

    private void throwError(Exception e, Token token) {
        String msg = e.getMessage();
        if (token != null)
            msg = "Line " + err.getTokenLine(token) + " near '" + token.getText() + "'. " + msg;
        err.log(new ErrorReporter.Error(msg, ErrorType.Contextual, e));
        throw new ContextualErrorExpression(msg);
    }

    private void establishStdEnvironment() throws ClassNotFoundException {
        Declaration[] declarations = Environment.getDeclarations();
        if (declarations.length > 0) {
            Command left = new DeclarationCommand(declarations[0]);
            for (int i = 1; i < declarations.length; i++)
                left = new SequentialCommand(left, new DeclarationCommand(declarations[i]));
            program.setCommand(new SequentialCommand(left, program.getCommand()));
        }
    }

    public void analyse() {
        visit(program, null);
    }

    // Commands
    @Override
    public Object visit(Program program, Object args) {
        idTable.openScope();
        program.getCommand().accept(this, args);
        idTable.closeScope();
        return null;
    }

    @Override
    public Object visit(AssignCommand assignCommand, Object args) {

        if (!assignCommand.isComplex()) {

            // Visits identifier
            assignCommand.getNameExpressions()[0].accept(this, args);

            // Gets declaration
            Declaration declaration = idTable.retrieve(assignCommand.getNameExpressions()[0].getIdentifier()).getDeclaration();

            // Checks if it is a constant
            if (declaration.isFinal())
                throwError("Final reference assignment.", assignCommand.getNameExpressions()[0].getIdentifier().getToken());

            TypeDenoter refType = declaration.getTypeDenoter();
            TypeDenoter eType = (TypeDenoter) assignCommand.getExpression().accept(this, null);

            if (!TypeDenoter.equivalent(refType, eType))
                throwError("Assignment with inconsistent type.", assignCommand.getNameExpressions()[0].getIdentifier().getToken());

        } else
            visitStruct(assignCommand, 0, null);

        return null;
    }

    private Object visitStruct(AssignCommand command, int depth, Declaration parentDeclaration) {

        if (depth >= command.getNameExpressions().length)
            return null;

        Declaration declaration = null;
        Identifier identifier = command.getNameExpressions()[depth].getIdentifier();

        // If it is the upper level struct
        if (parentDeclaration == null)
            declaration = identifier.setDeclaration(idTable.retrieve(identifier).getDeclaration());
            // Gets the declaration stored in the inner map
        else {

            // Gets type denoter of the parent declaration
            TypeDenoter parentType = parentDeclaration.getTypeDenoter();

            // If array, gets element type denoter
            if (parentDeclaration.getTypeDenoter().getType() == TypeDenoter.Type.Array) {
                ArrayTypeDenoter arrTypeDenoter = (ArrayTypeDenoter) parentDeclaration.getTypeDenoter();
                parentType = arrTypeDenoter.getElementTypeDenoter();
            }

            StructTypeDenoter structParentType = (StructTypeDenoter) parentType;
            declaration = identifier.setDeclaration(structParentType.getDeclarations().get(identifier));

        }

        // Checks for invalid declaration
        if (declaration == null)
            throwError("Identifier not declared.", identifier.getToken());

        // Gets type denoter of the declaration
        TypeDenoter typeDenoter = declaration.getTypeDenoter();

        // If array, gets element type denoter
        if (declaration.getTypeDenoter().getType() == TypeDenoter.Type.Array) {
            ArrayTypeDenoter arrTypeDenoter = (ArrayTypeDenoter) declaration.getTypeDenoter();
            typeDenoter = arrTypeDenoter.getElementTypeDenoter();
        }

        // If last one checks for type
        if (depth == command.getNameExpressions().length - 1) {
            TypeDenoter refType = typeDenoter;
            TypeDenoter eType = (TypeDenoter) command.getExpression().accept(this, null);
            if (!TypeDenoter.equivalent(refType, eType))
                throwError("Assignment with inconsistent type.", identifier.getToken());
        }

        // Recurses in case of a structure type
        else if (typeDenoter.getType() == TypeDenoter.Type.Struct)
            visitStruct(command, depth + 1, declaration);


        return null;
    }

    @Override
    public Object visit(AssignDeclarationCommand command, Object args) {

        command.getDeclaration().accept(this, null);
        TypeDenoter dType = command.getDeclaration().getTypeDenoter();

        // Visits its type
        dType.accept(this, null);

        TypeDenoter eType = (TypeDenoter) command.getExpression().accept(this, args);

        // Visits its type
        eType.accept(this, null);

        if (!TypeDenoter.equivalent(dType, eType))
            throwError("Type error exception.", command.getDeclaration().getIdentifier().getToken());

        return null;
    }

    @Override
    public Object visit(CallCommand callCommand, Object args) {

        Expression expression = callCommand.getNameExpressions()[0];
        TypeDenoter typeDenoter = (TypeDenoter) expression.accept(this, null);

        if (typeDenoter.getType() == TypeDenoter.Type.Array) {
            ArrayTypeDenoter arrTypeDenoter = (ArrayTypeDenoter) typeDenoter;
            typeDenoter = arrTypeDenoter.getElementTypeDenoter();
        }

        if (typeDenoter.getType() == TypeDenoter.Type.Struct)
            visitStruct(callCommand, (StructTypeDenoter) typeDenoter, 1);
        else if (callCommand.getNameExpressions().length > 1)
            throwError("Cannot dereference primitive type.", callCommand.getNameExpressions()[0].getIdentifier().getToken());

        return null;
    }

    private Object visitStruct(CallCommand callCommand, StructTypeDenoter parentTypeDenoter, int depth) {

        // Gets next name epression and visits it
        NameExpression nameExpression = callCommand.getNameExpressions()[depth];
        nameExpression.accept(this, parentTypeDenoter);

        // Gets identifier and type denoter
        Identifier identifier = nameExpression.getIdentifier();
        Declaration declaration = identifier.setDeclaration(parentTypeDenoter.getDeclarations().get(identifier));

        if (declaration == null)
            throwError("Identifier not declared.", identifier.getToken());

        // Gets type denoter
        TypeDenoter typeDenoter = declaration.getTypeDenoter();

        if (typeDenoter.getType() == TypeDenoter.Type.Array) {
            ArrayTypeDenoter arrTypeDenoter = (ArrayTypeDenoter) typeDenoter;
            typeDenoter = arrTypeDenoter.getElementTypeDenoter();
        }

        if (typeDenoter.getType() == TypeDenoter.Type.Struct && depth < callCommand.getNameExpressions().length - 1)
            visitStruct(callCommand, (StructTypeDenoter) typeDenoter, depth + 1);
        else if (depth < callCommand.getNameExpressions().length - 1)
            throwError("Cannot dereference primitive type.", identifier.getToken());

        return null;

    }


    @Override
    public Object visit(DeclarationCommand command, Object args) {
        command.getDeclaration().accept(this, null);
        return null;
    }

    @Override
    public Object visit(ReturnCommand command, Object args) {
        if (command.getExpression() != null)
            command.getExpression().setTypeDenoter((TypeDenoter) command.getExpression().accept(this, args));
        return command.getExpression();
    }

    @Override
    public Object visit(SequentialCommand command, Object args) {
        command.getLeft().accept(this, args);
        command.getRight().accept(this, args);
        return null;
    }

    @Override
    public Object visit(IfCommand command, Object args) {

        //Opens Scope
        idTable.openScope();

        // If there is an expression defined (not else command)
        if (command.getExpression() != null) {
            TypeDenoter eType = (TypeDenoter) command.getExpression().accept(this, null);

            if (!TypeDenoter.equivalent(eType.getType(), TypeDenoter.Type.Boolean))
                throwError("If command expression must be of type: " + TypeDenoter.Type.Boolean.toString(), null);
        }

        // Visits command
        command.getCommand().accept(this, args);

        //Closes Scope
        idTable.closeScope();

        // If else command exists
        if (command.getElseCommand() != null)
            command.getElseCommand().accept(this, null);

        return null;

    }

    @Override
    public Object visit(WhileCommand command, Object args) {

        //Opens Scope
        idTable.openScope();

        // Checks expression type
        TypeDenoter eType = (TypeDenoter) command.getExpression().accept(this, null);

        if (!(eType.getType() == TypeDenoter.Type.Boolean))
            throwError("While command expression must be of type: " + TypeDenoter.Type.Boolean.toString(), null);

        // Visits command
        command.getCommand().accept(this, null);

        //Closes Scope
        idTable.closeScope();

        return null;
    }

    @Override
    public Object visit(ForCommand command, Object args) {
        //Opens Scope
        idTable.openScope();

        // Visits init command
        if (command.getInitCommand() != null)
            command.getInitCommand().accept(this, null);

        // Checks expression type
        TypeDenoter eType = (TypeDenoter) command.getExpression().accept(this, null);

        if (!(eType.getType() == TypeDenoter.Type.Boolean))
            throwError("For command expression must be of type: " + TypeDenoter.Type.Boolean.toString(), null);

        // Visits commands
        if (command.getBodyCommand() != null)
            command.getBodyCommand().accept(this, null);

        if (command.getIncrementCommand() != null)
            command.getIncrementCommand().accept(this, null);

        //Closes Scope
        idTable.closeScope();

        return null;
    }

    @Override
    public Object visit(BreakCommand command, Object args) {

        Command loopCommand = findLoopCommand(command);

        if (loopCommand == null)
            throwError("Break command must be inside a loop command.", null);

        return null;
    }

    @Override
    public Object visit(StandardProcedureCommand command, Object args) {
        return null;
    }

    /**
     * Finds last return command
     *
     * @param command
     * @return
     */
    public ReturnCommand findReturnCommand(Command command) {
        if (command.getType() == Command.Type.Sequential)
            return findReturnCommand(((SequentialCommand) command).getRight());
        else if (command.getType() == Command.Type.Return)
            return (ReturnCommand) command;
        else
            return null;
    }

    // Identifiers
    @Override
    public Object visit(Identifier identifier, Object args) {
        Declaration declaration = null;
        try {
            declaration = identifier.setDeclaration(idTable.retrieve(identifier).getDeclaration());
        } catch (IdentifierNotDeclaredException e) {
            throwError(e, identifier.getToken());
        }
        return declaration;
    }

    private Command findLoopCommand(Command command) {
        if (command == null)
            return null;
        else if (command.getType() == Command.Type.While || command.getType() == Command.Type.For)
            return command;
        else
            return findLoopCommand(command.getParent());
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
    public Object visit(RuleDeclaration declaration, Object args) {
        try {
            // Visits its type
            declaration.getTypeDenoter().accept(this, null);

            // Registers reference to the table
            idTable.enter(declaration.getIdentifier(), new Attribute(declaration));
            declaration.getIdentifier().accept(this, null);
        } catch (IdentifierAlreadyDeclaredException e) {
            throwError(e, declaration.getIdentifier().getToken());
        }
        return null;
    }

    @Override
    public Object visit(ReferenceDeclaration declaration, Object args) {
        try {

            // Visits its type
            declaration.getTypeDenoter().accept(this, null);

            // Registers reference to the table
            idTable.enter(declaration.getIdentifier(), new Attribute(declaration));
            declaration.getIdentifier().accept(this, null);


        } catch (IdentifierAlreadyDeclaredException e) {
            throwError(e, declaration.getIdentifier().getToken());
        }
        return null;
    }

    @Override
    public Object visit(SentenceDeclaration declaration, Object args) {
        try {
            // Visits its type
            declaration.getTypeDenoter().accept(this, null);

            // Registers reference to the table
            idTable.enter(declaration.getIdentifier(), new Attribute(declaration));
            declaration.getIdentifier().accept(this, null);
        } catch (IdentifierAlreadyDeclaredException e) {
            throwError(e, declaration.getIdentifier().getToken());
        }
        return null;
    }

    @Override
    public Object visit(SequentialDeclaration declaration, Object args) {
        declaration.left.accept(this, null);
        declaration.right.accept(this, null);
        return null;
    }

    @Override
    public Object visit(StructDeclaration declaration, Object args) {

        // Registers type identifier
        idTable.enter(declaration.getTypeDenoter().getIdentifier(), new Attribute(declaration));
        StructTypeDenoter typeDenoter = (StructTypeDenoter) declaration.getTypeDenoter();

        // Visits type denoter
        typeDenoter.accept(this, null);

        // Struct declaration, may consist with a variable
        if (declaration.getIdentifier() != null) {
            // Registers identifier
            idTable.enter(declaration.getIdentifier(), new Attribute(declaration));
            declaration.getIdentifier().accept(this, null);
        }

        // Opens procedure scope and registers its arguments declarations
        idTable.openScope();

        // Adds the "this identifier to scope"
        idTable.enter(new Identifier(new Token(JallLexicon.THIS_IDENTIFIER, Token.TokenType.Identifier, declaration.getTypeDenoter().getIdentifier().getToken().getStartPosition())), new Attribute(declaration));

        // Loops through child declarations
        for (Declaration fieldDeclaration : declaration.getFieldDeclarations()) {

            // Visits file declaration
            fieldDeclaration.accept(this, declaration);

            // Adds them to declarations map
            typeDenoter.getDeclarations().put(fieldDeclaration.getIdentifier(), fieldDeclaration);


        }
        idTable.closeScope();

        return null;
    }

    /**
     * Visits a procedure declaration. The args argument will be parent declaration when applicable.
     */
    @Override
    public Object visit(ProcedureFunctionDeclaration procedureDeclaration, Object args) {

        // Parent declaration
        Declaration parent = (Declaration) args;

        // Validates type in context
        if (procedureDeclaration.isDeclaredTypeLess() && (args == null || (args != null && !isConstructor(procedureDeclaration, parent))))
            throwError("Only constructors can be declared typeless.", procedureDeclaration.getIdentifier().getToken());

        // Visits its type
        procedureDeclaration.getTypeDenoter().accept(this, null);

        // Enters procedure identifier into higher scope
        idTable.enter(procedureDeclaration.getIdentifier(), new Attribute(procedureDeclaration));

        // Needs to visit to interpreter knows this identifier declaration
        procedureDeclaration.getIdentifier().accept(this, null);

        // Opens procedure scope and registers its arguments declarations
        idTable.openScope();
        for (Declaration childDeclaration : procedureDeclaration.getArgsDeclarations())
            childDeclaration.accept(this, null);

        // Visits return command, last one need to be a return command that returns the expression
        if (procedureDeclaration.getCommand() != null) {

            // Visits command and finds
            procedureDeclaration.getCommand().accept(this, null);

            if (procedureDeclaration.getCommand().getType() != Command.Type.StandardProcedure &&
                    !isConstructor(procedureDeclaration, parent)) {
                ReturnCommand returnCommand = findReturnCommand(procedureDeclaration.getCommand());
                if (returnCommand != null) {
                    Expression expression = (Expression) returnCommand.accept(this, args);

                    if (!TypeDenoter.equivalent(procedureDeclaration.getTypeDenoter(), expression.getTypeDenoter()))
                        throwError("Function '" + procedureDeclaration.getIdentifier().getText() + "' must return a result of type " + procedureDeclaration.getTypeDenoter() + ".", procedureDeclaration.getIdentifier().getToken());
                } else if (procedureDeclaration.getTypeDenoter().getType() != TypeDenoter.Type.Void)
                    throwError("Function '" + procedureDeclaration.getIdentifier().getText() + "' must return a result of type " + procedureDeclaration.getTypeDenoter() + ".", procedureDeclaration.getIdentifier().getToken());
            }
        }

        idTable.closeScope();

        return null;
    }

    private boolean isConstructor(ProcedureFunctionDeclaration procedureDeclaration, Declaration parent) {
        return parent != null && procedureDeclaration.getIdentifier().equals(parent.getTypeDenoter().getIdentifier()) &&
                procedureDeclaration.getTypeDenoter().getType() == TypeDenoter.Type.Struct;
    }

    @Override
    public Object visit(FactDeclaration declaration, Object args) {
        try {
            // Visits its type
            declaration.getTypeDenoter().accept(this, null);

            // Registers reference to the table
            idTable.enter(declaration.getIdentifier(), new Attribute(declaration));
            declaration.getIdentifier().accept(this, null);
        } catch (IdentifierAlreadyDeclaredException e) {
            throwError(e, declaration.getIdentifier().getToken());
        }
        return null;
    }

    @Override
    public Object visit(FunctionDeclaration declaration, Object args) {
        try {
            // Visits its type
            declaration.getTypeDenoter().accept(this, null);

            // Registers reference to the table
            idTable.enter(declaration.getIdentifier(), new Attribute(declaration));
            declaration.getIdentifier().accept(this, null);
        } catch (IdentifierAlreadyDeclaredException e) {
            throwError(e, declaration.getIdentifier().getToken());
        }
        return null;
    }

    @Override
    public Object visit(TypeDeclaration declaration, Object args) {
        idTable.enter(declaration.getIdentifier(), new Attribute(declaration));
        declaration.getIdentifier().accept(this, null);
        return null;
    }

    // Expressions
    @Override
    public Object visit(BinaryExpression expression, Object args) {
        // Gets types of the two child expressions
        TypeDenoter type1 = (TypeDenoter) expression.getLeft().accept(this, null);
        TypeDenoter type2 = (TypeDenoter) expression.getRight().accept(this, null);

        // Visits operator
        expression.getOperator().accept(this, null);

        // Gets return type of expression
        TypeDenoter typeDenoter = expression.getOperator().results(type1, type2);

        if (typeDenoter == null)
            throwError("Operator '" + expression.getOperator() + "' does not accept operand types", null);

        return typeDenoter;

    }

    @Override
    public Object visit(BooleanExpression expression, Object args) {
        return expression.setTypeDenoter(TypeDenoter.createTypeDenoter(TypeDenoter.Type.Boolean));
    }

    @Override
    public Object visit(CallExpression expression, Object args) {

        // Gets declaration
        Declaration declaration = null;

        if (args != null && args instanceof StructTypeDenoter) {
            StructTypeDenoter parentTypeDenoter = (StructTypeDenoter) args;
            declaration = parentTypeDenoter.getDeclarations().get(expression.getIdentifier());
        } else
            declaration = (Declaration) expression.getIdentifier().accept(this, args);

        // If the call expression if from a procedure
        if (declaration.getType() == Declaration.DeclarationType.ProcedureFunction) {
            ProcedureFunctionDeclaration procDeclaration = (ProcedureFunctionDeclaration) declaration;
            for (int i = 0; i < expression.getArgsExpressions().length; i++) {

                TypeDenoter dType = procDeclaration.getArgsDeclarations()[i].getTypeDenoter();
                TypeDenoter eType = (TypeDenoter) expression.getArgsExpressions()[i].accept(this, null);

                if (!TypeDenoter.equivalent(dType, eType))
                    throwError("Type error exception.", procDeclaration.getArgsDeclarations()[i].getIdentifier().getToken());

            }
        }
        //TODO Throws error for dynamic binding.
        //else
        //throwError("Dynamic binding not allowed.", declaration.getIdentifier().getToken());

        return expression.setTypeDenoter(declaration.getTypeDenoter());
    }

    @Override
    public Object visit(DereferenceExpression expression, Object args) {

        // Gets the highest level reference and enters its identifier
        Declaration declaration = (Declaration) expression.getNameExpressions()[0].getIdentifier().accept(this, null);

        // Visites Children there is always at least two identifiers
        visitStruct(expression, 1, declaration);

        // Returns last and checks if there as a dereference of a simple type
        Identifier lastExpressionIdentifier = expression.getNameExpressions()[expression.getNameExpressions().length - 1].getIdentifier();
        if (lastExpressionIdentifier.getDeclaration() == null)
            throwError("Cannot dereference primitive type.", lastExpressionIdentifier.getToken());

        return lastExpressionIdentifier.getDeclaration().getTypeDenoter();
    }

    public Object visitStruct(DereferenceExpression expression, int depth, Declaration parentDeclaration) {

        if (depth >= expression.getNameExpressions().length)
            return null;

        // Gets parent type Denoter
        StructTypeDenoter structTypeDenoter = null;
        TypeDenoter parentTypeDenoter = parentDeclaration.getTypeDenoter();

        if (parentTypeDenoter.getType() == TypeDenoter.Type.Struct)
            structTypeDenoter = (StructTypeDenoter) parentDeclaration.getTypeDenoter();
        else if (parentDeclaration.getTypeDenoter().getType() == TypeDenoter.Type.Array)
            structTypeDenoter = (StructTypeDenoter) ((ArrayTypeDenoter) parentTypeDenoter).getElementTypeDenoter();

        // Gets identifier
        Identifier identifier = expression.getNameExpressions()[depth].getIdentifier();
        Declaration declaration = identifier.setDeclaration(structTypeDenoter.getDeclarations().get(identifier));

        // Checks if has not been declared
        if (declaration == null)
            throwError("Identifier '" + identifier + "' has not been declared inside the structure.", identifier.getToken());

        // Gets type denoter of the declaration
        TypeDenoter typeDenoter = declaration.getTypeDenoter();

        // If array, gets element type denoter
        if (declaration.getTypeDenoter().getType() == TypeDenoter.Type.Array) {
            ArrayTypeDenoter arrTypeDenoter = (ArrayTypeDenoter) declaration.getTypeDenoter();
            typeDenoter = arrTypeDenoter.getElementTypeDenoter();
        }

        if (typeDenoter.getType() == TypeDenoter.Type.Struct)
            visitStruct(expression, depth + 1, declaration);

        return null;
    }

    @Override
    public Object visit(DoubleExpression expression, Object args) {
        return expression.setTypeDenoter(TypeDenoter.createTypeDenoter(TypeDenoter.Type.Double));
    }

    @Override
    public Object visit(IntegerExpression expression, Object args) {
        return expression.setTypeDenoter(TypeDenoter.createTypeDenoter(TypeDenoter.Type.Integer));
    }

    @Override
    public Object visit(StringExpression expression, Object args) {
        return expression.setTypeDenoter(TypeDenoter.createTypeDenoter(TypeDenoter.Type.String));
    }

    @Override
    public Object visit(UnaryExpression expression, Object args) {

        // Visits operator
        expression.getOperator().accept(this, null);

        TypeDenoter type = (TypeDenoter) expression.getExpression().accept(this, null);

        return type;

    }

    @Override
    public Object visit(ReferenceExpression expression, Object args) {

        Declaration declaration = (Declaration) expression.getIdentifier().accept(this, args);
        TypeDenoter typeDenoter = declaration.getTypeDenoter();

        // Evaluate expression if it is an array
        if (expression.getIndexExpressions().length > 0 && declaration.getTypeDenoter().getType() == TypeDenoter.Type.Array) {
            ArrayTypeDenoter arrTypeDenoter = (ArrayTypeDenoter) declaration.getTypeDenoter();
            if (arrTypeDenoter.getDimensions() >= expression.getIndexExpressions().length)
                typeDenoter = (arrTypeDenoter).getElementTypeDenoter();
        }

        return expression.setTypeDenoter(typeDenoter);
    }

    @Override
    public Object visit(VoidExpression expression, Object args) {
        return expression.setTypeDenoter(TypeDenoter.createTypeDenoter(TypeDenoter.Type.Void));
    }

    @Override
    public Object visit(NewExpression newExpression, Object args) {

        Declaration declaration = (Declaration) newExpression.getIdentifier().accept(this, args);
        TypeDenoter typeDenoter = declaration.getTypeDenoter().clone();

        if (typeDenoter.getType() == TypeDenoter.Type.Array) {
            for (int i = 0; i < newExpression.getExpressionsArgs().length; i++) {
                TypeDenoter eType = (TypeDenoter) newExpression.getExpressionsArgs()[i].accept(this, null);
                if (eType.getType() != TypeDenoter.Type.Integer)
                    throwError("Dereference array expression must evaluate to an integer.", newExpression.getIdentifier().getToken());
            }
            ArrayTypeDenoter arrTypeDenoter = new ArrayTypeDenoter();
            arrTypeDenoter.setElementTypeDenoter(typeDenoter);
            arrTypeDenoter.setDimensions(newExpression.getExpressionsArgs().length);
            typeDenoter = arrTypeDenoter;
        }

        // Sets new expression type denoter
        newExpression.setTypeDenoter(typeDenoter);

        return typeDenoter;
    }

    @Override
    public Object visit(NullExpression expression, Object args) {
        return expression.setTypeDenoter(TypeDenoter.createTypeDenoter(TypeDenoter.Type.Struct));
    }

    // Literals
    @Override
    public Object visit(BooleanLiteral literal, Object args) {
        return literal.getValue();
    }

    @Override
    public Object visit(DoubleLiteral literal, Object args) {
        return literal.getValue();
    }

    @Override
    public Object visit(IntLiteral literal, Object args) {
        return literal.getValue();
    }

    @Override
    public Object visit(StringLiteral literal, Object args) {
        return literal.getValue();
    }

    @Override
    public Object visit(ArrayLiteral literal, Object args) {
        return literal.getValue();
    }

    @Override
    public Object visit(StructLiteral literal, Object args) {
        return literal.getScope();
    }

    @Override
    public Object visit(ObjectLiteral literal, Object args) {
        return literal.getValue();
    }

    @Override
    public Object visit(SentenceLiteral literal, Object args) {
        return literal.getValue();
    }

    @Override
    public Object visit(FunctionLiteral literal, Object args) {
        return literal.getValue();
    }

    @Override
    public Object visit(ProcedureFunctionLiteral literal, Object args) {
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
        typeDenoter.getElementTypeDenoter().accept(this, null);
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

        Identifier typeIdentifier = typeDenoter.getIdentifier();

        // This may be true for standard method declarations
        if (typeIdentifier != null) {
            // Visits type denoter identifier
            typeIdentifier.accept(this, null);

            // Gets its declaration
            Declaration declaration = typeIdentifier.getDeclaration();

            // Gets type declaration, as this may be getting a constructor
            StructDeclaration typeDeclaration = findStructDeclaration(typeDenoter, declaration);

            // Replaces by typeDeclaration
            typeIdentifier.setDeclaration(typeDeclaration);

            // Adds to this type
            for (Declaration fieldDeclaration : typeDeclaration.getFieldDeclarations())
                typeDenoter.getDeclarations().put(fieldDeclaration.getIdentifier(), fieldDeclaration);

        }
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
    private StructDeclaration findStructDeclaration(StructTypeDenoter typeDenoter, Declaration declaration) {
        if (declaration.getType() == Declaration.DeclarationType.Struct)
            return (StructDeclaration) declaration;
        else
            return findStructDeclaration(typeDenoter, declaration.getTypeDenoter().getIdentifier().getDeclaration());
    }


}

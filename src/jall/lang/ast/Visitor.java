/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast;

import jall.inference.Constant;
import jall.inference.Function;
import jall.inference.Predicate;
import jall.inference.Variable;
import jall.lang.ast.command.*;
import jall.lang.ast.declaration.*;
import jall.lang.ast.expression.*;
import jall.lang.ast.literal.*;
import jall.lang.ast.operator.ArithmeticOperator;
import jall.lang.ast.operator.AssignOperator;
import jall.lang.ast.operator.ComparisonOperator;
import jall.lang.ast.operator.LogicalOperator;
import jall.lang.ast.sentence.*;
import jall.lang.type.*;


/**
 * Sepecification for a visitor
 *
 * @author Lucas
 */
public interface Visitor {

    //Visit Programs
    Object visit(Program visitable, Object args);

    //Visit Commands
    Object visit(AssignCommand visitable, Object args);

    Object visit(AssignDeclarationCommand visitable, Object args);

    Object visit(CallCommand visitable, Object args);

    Object visit(DeclarationCommand visitable, Object args);

    Object visit(ReturnCommand visitable, Object args);

    Object visit(SequentialCommand visitable, Object args);

    Object visit(IfCommand visitable, Object args);

    Object visit(WhileCommand visitable, Object args);

    Object visit(ForCommand forCommand, Object args);

    Object visit(BreakCommand visitable, Object args);

    Object visit(StandardProcedureCommand visitable, Object args);

    //Visit Identifiers
    Object visit(Identifier visitable, Object args);

    // Operators
    Object visit(ArithmeticOperator visitable, Object args);

    Object visit(AssignOperator visitable, Object args);

    Object visit(ComparisonOperator visitable, Object args);

    Object visit(LogicalOperator visitable, Object args);

    //Visit Declarations
    Object visit(RuleDeclaration visitable, Object args);

    Object visit(ReferenceDeclaration visitable, Object args);

    Object visit(SentenceDeclaration visitable, Object args);

    Object visit(SequentialDeclaration visitable, Object args);

    Object visit(StructDeclaration visitable, Object args);

    Object visit(ProcedureFunctionDeclaration visitable, Object args);

    Object visit(FactDeclaration visitable, Object args);

    Object visit(FunctionDeclaration visitable, Object args);

    Object visit(TypeDeclaration typeDeclaration, Object args);

    //Visit Expressions
    Object visit(BinaryExpression visitable, Object args);

    Object visit(BooleanExpression visitable, Object args);

    Object visit(CallExpression visitable, Object args);

    Object visit(DereferenceExpression visitable, Object args);

    Object visit(DoubleExpression visitable, Object args);

    Object visit(IntegerExpression visitable, Object args);

    Object visit(StringExpression visitable, Object args);

    Object visit(UnaryExpression visitable, Object args);

    Object visit(ReferenceExpression visitable, Object args);

    Object visit(VoidExpression visitable, Object args);

    Object visit(NewExpression newExpression, Object args);

    Object visit(NullExpression nullExpression, Object args);

    //Literals
    Object visit(BooleanLiteral visitable, Object args);

    Object visit(DoubleLiteral visitable, Object args);

    Object visit(IntLiteral visitable, Object args);

    Object visit(StringLiteral visitable, Object args);

    Object visit(ArrayLiteral visitable, Object args);

    Object visit(StructLiteral visitable, Object args);

    Object visit(ObjectLiteral visitable, Object args);

    Object visit(SentenceLiteral visitable, Object args);

    Object visit(FunctionLiteral functionLiteral, Object args);

    Object visit(ProcedureFunctionLiteral procedureFunctionLiteral, Object args);

    Object visit(NullLiteral nullLiteral, Object args);

    //Terms Those are declared on the fly
    Object visit(Constant visitable, Object args);

    Object visit(Function visitable, Object args);

    Object visit(Predicate visitable, Object args);

    Object visit(Variable visitable, Object args);

    //Visit Parse FOL Sentences
    Object visit(BinarySentence visitable, Object args);

    Object visit(BindingSentence visitable, Object args);

    Object visit(BooleanSentence visitable, Object args);

    Object visit(Rule visitable, Object args);

    Object visit(ParenSentence visitable, Object args);

    Object visit(QuantifiedSentence visitable, Object args);

    Object visit(NegationSentence visitable, Object args);

    // Visit types
    Object visit(ArrayTypeDenoter visitable, Object args);

    Object visit(BooleanTypeDenoter visitable, Object args);

    Object visit(DoubleTypeDenoter visitable, Object args);

    Object visit(FactTypeDenoter visitable, Object args);

    Object visit(IntTypeDenoter visitable, Object args);

    Object visit(ObjectTypeDenoter visitable, Object args);

    Object visit(RuleTypeDenoter visitable, Object args);

    Object visit(SentenceTypeDenoter visitable, Object args);

    Object visit(StringTypeDenoter visitable, Object args);

    Object visit(StructTypeDenoter visitable, Object args);

    Object visit(VoidTypeDenoter visitable, Object args);

    Object visit(FunctionTypeDenoter visitable, Object args);

}

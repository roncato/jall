/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.operator;

import jall.lang.Token;
import jall.lang.ast.Symbol;
import jall.lang.ast.declaration.Declaration;
import jall.lang.ast.literal.Literal;
import jall.lang.type.TypeDenoter;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public abstract class Operator extends Symbol {

    public static final String[] ARITHMETIC =
            {
                    "+",
                    "-",
                    "*",
                    "/"
            };
    public static final String[] ASSIGN =
            {
                    "="
            };
    public static final String[] COMPARISON =
            {
                    "==",
                    "!=",
                    "<",
                    ">",
                    "<=",
                    ">=",
            };
    public static final String[] LOGICAL =
            {
                    "=>",
                    "<=>",
                    "&",
                    "|",
                    "\\/",
                    "/\\"
            };
    public static final String[] NEGATION =
            {
                    "!",
                    "~"
            };
    public static final String[] UNARY =
            {
                    "-",
                    "+",
                    "!",
                    "~"
            };
    protected Declaration declaration = null;
    protected Token token = null;
    protected String text = null;

    public static Operator createOperator(Token token) {
        Operator operator = null;
        if (isArithmeticOperator(token.getText()))
            operator = new ArithmeticOperator(token.getText());
        if (isComparisonOperator(token.getText()))
            operator = new ComparisonOperator(token.getText());
        if (isLogicalperator(token.getText()) || isNegationOperator(token.getText()))
            operator = new LogicalOperator(token.getText());
        operator.token = token;
        return operator;
    }

    public static List<String[]> getOperators() {
        List<String[]> arrays = new ArrayList<String[]>();
        arrays.add(ARITHMETIC);
        arrays.add(ASSIGN);
        arrays.add(COMPARISON);
        arrays.add(LOGICAL);
        arrays.add(NEGATION);
        return arrays;
    }

    public static boolean isOperator(String text) {
        List<String[]> arrays = getOperators();
        for (String[] array : arrays) {
            if (jall.util.Arrays.isInArray(text, array))
                return true;
        }
        return false;
    }

    public static boolean isArithmeticOperator(String text) {
        return jall.util.Arrays.isInArray(text, ARITHMETIC);
    }

    public static boolean isAssignOperator(String text) {
        return jall.util.Arrays.isInArray(text, ASSIGN);
    }

    public static boolean isComparisonOperator(String text) {
        return jall.util.Arrays.isInArray(text, COMPARISON);
    }

    public static boolean isLogicalperator(String text) {
        return jall.util.Arrays.isInArray(text, LOGICAL);
    }

    public static boolean isUnaryOperator(String text) {
        return jall.util.Arrays.isInArray(text, UNARY);
    }

    public static boolean isBinaryOperator(String text) {
        return jall.util.Arrays.isInArray(text, ARITHMETIC) ||
                jall.util.Arrays.isInArray(text, COMPARISON) ||
                jall.util.Arrays.isInArray(text, LOGICAL);
    }

    public static boolean isNegationOperator(String text) {
        return jall.util.Arrays.isInArray(text, NEGATION);
    }

    public static OperatorType getOperatorType(String text) {
        if (jall.util.Arrays.isInArray(text, ARITHMETIC))
            return OperatorType.Arithmetic;
        else if (jall.util.Arrays.isInArray(text, ASSIGN))
            return OperatorType.Assign;
        else if (jall.util.Arrays.isInArray(text, COMPARISON))
            return OperatorType.Comparison;
        else if (jall.util.Arrays.isInArray(text, LOGICAL))
            return OperatorType.Logical;
        else if (jall.util.Arrays.isInArray(text, NEGATION))
            return OperatorType.Negation;
        return null;
    }

    public abstract TypeDenoter results(TypeDenoter left, TypeDenoter right);

    public abstract Literal evaluate(Literal left, Literal right);

    public abstract Literal evaluate(Literal literal);

    @Override
    public String toString() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (o instanceof Operator) {
            Operator that = (Operator) o;
            equals = that.text.equals(this.text);
        }
        return equals;
    }

    public boolean isUnaryOperator() {
        return isUnaryOperator(text);
    }

    @Override
    public abstract Operator clone();

    public Declaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(Declaration declaration) {
        this.declaration = declaration;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public enum OperatorType {
        Arithmetic,
        Assign,
        Comparison,
        Logical,
        Negation
    }


}

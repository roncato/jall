/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.expression;

import jall.lang.ast.Visitor;
import jall.lang.ast.operator.Operator;


/**
 *
 */
public class BinaryExpression extends Expression {

    private Expression left = null;
    private Expression right = null;
    private Operator operator = null;

    public BinaryExpression(Expression left, Expression right, Operator operator) {
        this.setLeft(left);
        this.setRight(right);
        this.setOperator(operator);
        type = Type.Binary;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " " + operator.toString() + " " + right.toString() + ")";
    }

    public Expression getLeft() {
        return left;
    }

    public void setLeft(Expression left) {
        this.left = left;
    }

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }


}

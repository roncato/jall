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
public class UnaryExpression extends jall.lang.ast.expression.Expression {

    private Operator operator = null;
    private Expression expression = null;

    public UnaryExpression(Operator operator, Expression expression) {
        this.operator = operator;
        this.expression = expression;
        type = Type.Unary;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public String toString() {
        return operator.toString() + "(" + expression.toString() + ")";
    }

    public Expression getExpression() {
        return expression;
    }

    public Operator getOperator() {
        return operator;
    }

}

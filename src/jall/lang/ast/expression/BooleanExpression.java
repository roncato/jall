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
import jall.lang.ast.literal.BooleanLiteral;


/**
 * @author Lucas Batista
 */
public class BooleanExpression extends jall.lang.ast.expression.Expression {

    private BooleanLiteral literal = null;

    public BooleanExpression(BooleanLiteral literal) {
        this.literal = literal;
        type = Type.Boolean;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    public BooleanLiteral getLiteral() {
        return literal;
    }

    @Override
    public String toString() {
        return literal.toString();
    }

}

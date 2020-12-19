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
import jall.lang.ast.literal.IntLiteral;

public class IntegerExpression extends Expression {

    private IntLiteral literal = null;

    public IntegerExpression(IntLiteral literal) {
        this.literal = literal;
        type = Type.Integer;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public String toString() {
        return literal.toString();
    }

    public IntLiteral getLiteral() {
        return literal;
    }

}

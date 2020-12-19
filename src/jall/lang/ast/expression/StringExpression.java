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
import jall.lang.ast.literal.StringLiteral;

public class StringExpression extends Expression {

    private StringLiteral literal = null;

    public StringExpression(StringLiteral literal) {
        this.literal = literal;
        type = Type.String;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public String toString() {
        return literal.toString();
    }

    public StringLiteral getLiteral() {
        return literal;
    }

}

/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.literal;

import jall.lang.ast.Visitor;
import jall.lang.type.TypeDenoter;


/**
 *
 */
public class BooleanLiteral extends jall.lang.ast.literal.Literal {
    private boolean value = false;

    public BooleanLiteral(boolean value) {
        this.value = value;
        this.type = Type.Boolean;
    }

    @Override
    public BooleanLiteral clone() {
        return new BooleanLiteral(value);
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Literal literal) {
        switch (literal.type) {
            case Boolean:
                BooleanLiteral bLiteral = (BooleanLiteral) literal;
                this.value = bLiteral.getValue();
                break;
            case Integer:
                IntLiteral iLiteral = (IntLiteral) literal;
                this.value = iLiteral.getValue() > 0;
                break;
            case Double:
                DoubleLiteral dLiteral = (DoubleLiteral) literal;
                this.value = dLiteral.getValue() > 0;
                break;
            case Object:
                ObjectLiteral objectLiteral = (ObjectLiteral) literal;
                literal = objectLiteral.getValue();
                this.setValue(literal);
                break;
            default:
                throw new IllegalArgumentException("Cannot assign literal type '" + literal.type.toString() + "' to boolean.");
        }
    }

    @Override
    public Literal convert(TypeDenoter typeDenoter) {
        Literal literal = null;
        switch (typeDenoter.getType()) {
            case Boolean:
                literal = this;
                break;
            case Integer:
                if (value)
                    literal = new IntLiteral(1);
                else
                    literal = new IntLiteral(0);
                break;
            case String:
                if (value)
                    literal = new StringLiteral("true");
                else
                    literal = new StringLiteral("false");
                break;
            case Object:
                literal = new ObjectLiteral(this);
                break;
        }
        return literal;
    }

}

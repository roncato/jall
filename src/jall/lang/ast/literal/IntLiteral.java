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

public class IntLiteral extends Literal {

    private int value = 0;

    public IntLiteral(int value) {
        this.value = value;
        this.type = Type.Integer;
    }

    @Override
    public IntLiteral clone() {
        return new IntLiteral(value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    public int getValue() {
        return value;
    }

    @Override
    public void setValue(Literal literal) {
        switch (literal.type) {
            case Boolean:
                BooleanLiteral bLiteral = (BooleanLiteral) literal;
                if (bLiteral.getValue())
                    value = 1;
                else
                    value = 0;
                break;
            case Integer:
                IntLiteral iLiteral = (IntLiteral) literal;
                value = iLiteral.getValue();
                break;
            case Double:
                DoubleLiteral dLiteral = (DoubleLiteral) literal;
                value = (int) dLiteral.getValue();
                break;
            case Null:
                value = 0;
                break;
            default:
                throw new IllegalArgumentException("Cannot assign literal type '" + literal.type.toString() + "' to double.");
        }
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public Literal convert(TypeDenoter typeDenoter) {
        Literal literal = null;

        switch (typeDenoter.getType()) {
            case Boolean:
                if (value == 0)
                    literal = new BooleanLiteral(false);
                else
                    literal = new BooleanLiteral(true);
                break;
            case Integer:
                literal = clone();
                break;
            case Double:
                literal = new DoubleLiteral(value);
                break;
            case String:
                literal = new StringLiteral(Integer.toString(value));
                break;
            case Object:
                literal = new ObjectLiteral(clone());
                break;
            default:
                throw new IllegalArgumentException("Cannot convert integer literal to '" + typeDenoter.getType().toString() + "'.");
        }
        return literal;
    }

}

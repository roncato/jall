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
public class StringLiteral extends jall.lang.ast.literal.Literal {

    private String value = null;

    public StringLiteral(String value) {
        this.value = value;
        this.type = Type.String;
    }

    @Override
    public StringLiteral clone() {
        return new StringLiteral(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    public String getValue() {
        return value;
    }

    @Override
    public void setValue(Literal literal) {
        switch (literal.type) {
            case Object:
                ObjectLiteral obj = (ObjectLiteral) literal;
                setValue(obj.getValue());
                break;
            case String:
                StringLiteral that = (StringLiteral) literal;
                this.value = that.value;
                break;
            case Integer:
                IntLiteral intLiteral = (IntLiteral) literal;
                this.value = Integer.toString(intLiteral.getValue());
                break;
            case Double:
                IntLiteral dbLiteral = (IntLiteral) literal;
                this.value = Double.toString(dbLiteral.getValue());
                break;
            case Boolean:
                BooleanLiteral bLiteral = (BooleanLiteral) literal;
                if (bLiteral.getValue())
                    value = "true";
                else
                    value = "false";
                break;
            case Null:
                value = "";
                break;
            default:
                throw new IllegalArgumentException("Cannot assign literal type '" + literal.type.toString() + "' to string.");
        }
    }

    @Override
    public Literal convert(TypeDenoter typeDenoter) {
        Literal literal = null;

        switch (typeDenoter.getType()) {
            case Boolean:
                if (value == "")
                    literal = new BooleanLiteral(false);
                else
                    literal = new BooleanLiteral(true);
                break;
            case Integer:
                if (isInteger())
                    literal = new IntLiteral(Integer.parseInt(value));
                break;
            case Double:
                if (isDouble())
                    literal = new DoubleLiteral(Double.parseDouble(value));
                break;
            case String:
                literal = clone();
                break;
            case Object:
                literal = new ObjectLiteral(this);
                break;
            default:
                throw new IllegalArgumentException("Cannot convert string literal to '" + typeDenoter.getType().toString() + "'.");
        }
        return literal;
    }

    public boolean isDouble() {
        boolean equals = false;
        try {
            Double.parseDouble(value);
            equals = true;
        } catch (Exception e) {
        }
        return equals;
    }

    public boolean isInteger() {
        boolean equals = false;
        try {
            Integer.parseInt(value);
            equals = true;
        } catch (Exception e) {
        }
        return equals;
    }

}

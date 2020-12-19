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

public class ObjectLiteral extends Literal {

    private Literal value = null;

    public ObjectLiteral(Literal value) {
        setValue(value);
        type = Type.Object;
    }

    public ObjectLiteral() {
        type = Type.Object;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public ObjectLiteral clone() {
        ObjectLiteral clone = new ObjectLiteral();
        if (value != null)
            clone.value = value.clone();
        return clone;
    }

    public Literal getValue() {
        return value;
    }

    @Override
    public void setValue(Literal literal) {
        if (literal != null)
            value = literal.clone();
        else
            value = literal;
    }

    @Override
    public Literal convert(TypeDenoter typeDenoter) {
        return value.convert(typeDenoter);
    }

    @Override
    public String toString() {
        if (value != null)
            return value.toString();
        else
            return "null";
    }

}

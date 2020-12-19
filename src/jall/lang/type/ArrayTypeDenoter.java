/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.type;

import jall.lang.ast.Visitor;

public class ArrayTypeDenoter extends TypeDenoter {

    private int dimensions = 1;
    private TypeDenoter elementTypeDenoter = null;
    public ArrayTypeDenoter() {
        type = Type.Array;
    }

    public static boolean equivalent(TypeDenoter type1, TypeDenoter type2) {
        boolean equivalent = false;
        switch (type1.getType()) {
            case Array:
                switch (type2.getType()) {
                    case Array:
                        equivalent = equivalent(((ArrayTypeDenoter) type1).elementTypeDenoter.getType(), ((ArrayTypeDenoter) type2).elementTypeDenoter.getType());
                        equivalent &= ((ArrayTypeDenoter) type1).dimensions == ((ArrayTypeDenoter) type2).dimensions;
                        break;
                    default:
                        equivalent = equivalent(((ArrayTypeDenoter) type1).elementTypeDenoter.getType(), type2.getType());
                }
                break;
            default:
                switch (type2.getType()) {
                    case Array:
                        equivalent = equivalent(type1.getType(), ((ArrayTypeDenoter) type2).elementTypeDenoter.getType());
                        break;
                    default:
                        equivalent = equivalent(type1.getType(), type1.getType());
                }
        }
        return equivalent;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public String toString() {
        String text = identifier.getText();
        for (int i = 0; i < dimensions; i++)
            text += "[]";
        return text;
    }

    public TypeDenoter getElementTypeDenoter() {
        return elementTypeDenoter;
    }

    public void setElementTypeDenoter(TypeDenoter elementTypeDenoter) {
        this.elementTypeDenoter = elementTypeDenoter;
    }

    public int getDimensions() {
        return dimensions;
    }

    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }
}

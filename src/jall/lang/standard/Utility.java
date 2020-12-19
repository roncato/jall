/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.standard;

import jall.lang.ast.literal.*;

public class Utility {

    public StringLiteral toString(ObjectLiteral literal) {
        return new StringLiteral(literal.getValue().toString());
    }

    public ObjectLiteral clone(ObjectLiteral literal) {
        return new ObjectLiteral(literal.getValue().clone());
    }

    public void exit(IntLiteral literal) {
        System.exit(literal.getValue());
    }

    public StringLiteral toUpperCase(StringLiteral literal) {
        return new StringLiteral(literal.getValue().toUpperCase());
    }

    public StringLiteral toLowerCase(StringLiteral literal) {
        return new StringLiteral(literal.getValue().toLowerCase());
    }

    public IntLiteral length(ObjectLiteral literal) {
        IntLiteral length = null;
        Literal value = literal.getValue();
        switch (value.getType()) {
            case String:
                StringLiteral str = (StringLiteral) value;
                length = new IntLiteral(str.getValue().length());
                break;
            case Array:
                ArrayLiteral arr = (ArrayLiteral) value;
                length = new IntLiteral(arr.getValue().size());
                break;
            case Struct:
                StructLiteral struct = (StructLiteral) value;
                length = new IntLiteral(struct.getScope().size());
                break;
        }
        return length;
    }

}

/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.expression;

import jall.lang.type.TypeDenoter;


/**
 *
 */
public abstract class Expression extends jall.lang.ast.Symbol {

    protected Type type = null;
    protected TypeDenoter typeDenoter = null;

    public TypeDenoter getTypeDenoter() {
        return typeDenoter;
    }

    public TypeDenoter setTypeDenoter(TypeDenoter type) {
        return this.typeDenoter = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public abstract String toString();

    public enum Type {
        Binary,
        Boolean,
        Call,
        Dereference,
        Double,
        Integer,
        New,
        Reference,
        String,
        Unary,
        Void,
        Null
    }

}

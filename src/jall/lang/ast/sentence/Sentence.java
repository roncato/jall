/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.sentence;

import jall.lang.ast.Symbol;


/**
 *
 */
public abstract class Sentence extends Symbol {

    protected Type type = null;

    public Type getType() {
        return type;
    }

    @Override
    public abstract Sentence clone();

    public enum Type {
        Binary,
        Binding,
        Boolean,
        Negation,
        Paren,
        Quantified,
        Rule,
        Predicate
    }

}

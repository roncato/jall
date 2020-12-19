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
import jall.lang.ast.sentence.Sentence;
import jall.lang.type.TypeDenoter;

public class SentenceLiteral extends Literal {

    private Sentence value = null;

    public SentenceLiteral(Sentence value) {
        this.value = value;
        type = Type.Sentence;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public SentenceLiteral clone() {
        return new SentenceLiteral(value.clone());
    }

    public Sentence getValue() {
        return value;
    }

    @Override
    public void setValue(Literal literal) {
        // TODO Auto-generated method stub

    }

    public void setValue(Sentence value) {
        this.value = value;
    }

    @Override
    public Literal convert(TypeDenoter typeDenoter) {
        return null;
    }

    @Override
    public String toString() {
        return value.toString();
    }

}

/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.sentence;

import jall.lang.ast.Visitor;


/**
 *
 */
public class ParenSentence extends ComplexSentence {

    private Sentence sentence = null;

    public ParenSentence(Sentence sentence) {
        this.sentence = sentence;
        type = Type.Paren;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public Sentence clone() {
        return new ParenSentence(sentence.clone());
    }

    public Sentence getSentence() {
        return sentence;
    }

    public void setSentence(Sentence sentence) {
        this.sentence = sentence;
    }

}

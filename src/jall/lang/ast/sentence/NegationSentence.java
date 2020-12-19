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
public class NegationSentence extends ComplexSentence {

    private Sentence sentence = null;

    public NegationSentence(Sentence sentence) {
        this.sentence = sentence;
        this.type = Type.Negation;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public NegationSentence clone() {
        return new NegationSentence(sentence.clone());
    }

    public Sentence getSentence() {
        return sentence;
    }

}

/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.sentence;

import jall.inference.Variable;
import jall.lang.ast.Visitor;


/**
 *
 */
public class QuantifiedSentence extends ComplexSentence {

    private Variable[] vars = null;
    private Sentence sentence = null;
    private Quantifier quantifier = null;
    public QuantifiedSentence(Variable[] vars, Sentence sentence, Quantifier quantifier) {
        this.vars = vars;
        this.sentence = sentence;
        type = Type.Quantified;
        this.quantifier = quantifier;
    }

    public static Quantifier getQuantifier(String text) {
        Quantifier quantifier = null;
        if (text.equals("forall"))
            quantifier = Quantifier.Universal;
        else if (text.equals("exists"))
            quantifier = Quantifier.Existential;
        return quantifier;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    public Quantifier setQuantifier(Quantifier quantifier) {
        return this.quantifier = quantifier;
    }

    public Quantifier getQuantifier() {
        return quantifier;
    }

    @Override
    public QuantifiedSentence clone() {
        return new QuantifiedSentence(vars.clone(), sentence.clone(), quantifier);
    }

    public Variable[] getVars() {
        return vars;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public enum Quantifier {
        Universal,
        Existential
    }

}

/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.sentence;

import jall.inference.Term;
import jall.lang.ast.Visitor;
import jall.lang.ast.operator.Operator;

public class BindingSentence extends AtomicSentence {

    private Term term1 = null;
    private Term term2 = null;
    private Operator operator = null;

    public BindingSentence(Term term1, Term term2,
                           Operator operator) {
        this.term1 = term1;
        this.term2 = term2;
        this.operator = operator;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public BindingSentence clone() {
        return new BindingSentence(term1.clone(), term2.clone(), operator.clone());
    }

    @Override
    public String toString() {
        return "{" + term1.toString() + " " + operator.toString() + " " + term2.toString() + "}";
    }

    public Term getVariable1() {
        return term1;
    }

    public Term getTerm2() {
        return term2;
    }

    public Operator getOperator() {
        return operator;
    }

}

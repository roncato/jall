/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.inference;

import jall.lang.ast.Identifier;
import jall.lang.ast.Visitor;
import jall.lang.ast.literal.BooleanLiteral;
import jall.lang.ast.sentence.AtomicSentence;

public class Predicate extends AtomicSentence {

    private Identifier identifier = null;
    private Term[] terms = null;
    private BooleanLiteral value = null;

    public Predicate(Identifier identifier, Term[] terms) {
        this.identifier = identifier;
        this.setTerms(terms);
        this.setValue(new BooleanLiteral(false));
        this.type = Type.Predicate;
    }

    private Predicate() {

    }

    @Override
    public String toString() {
        String txt = "";
        for (Term term : terms)
            txt += term.toString() + ", ";
        if (txt.length() > 2)
            txt = identifier.getText() + "(" + txt.substring(0, txt.length() - 2) + ")";
        else
            txt = identifier.toString();
        return txt;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public Predicate clone() {
        Predicate clone = new Predicate(identifier.clone(), terms.clone());
        clone.value = value.clone();
        return clone;
    }

    public boolean isFact() {
        boolean ground = true;
        for (Term term : terms)
            ground &= term.isGround();
        return ground;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public Term[] getTerms() {
        return terms;
    }

    public void setTerms(Term[] terms) {
        this.terms = terms;
    }

    public BooleanLiteral getValue() {
        return value;
    }

    public void setValue(BooleanLiteral value) {
        this.value = value;
    }

    public Predicate substitute(SubstitutionSet theta) {
        Predicate predicate = new Predicate();
        predicate.identifier = this.identifier.clone();
        predicate.terms = this.terms.clone();
        for (Substitution subst : theta) {
            for (int i = 0; i < terms.length; i++) {
                if (terms[i].equals(subst.substituted))
                    predicate.terms[i] = subst.substitutor;
            }
        }
        return predicate;
    }

    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (o instanceof Predicate) {
            Predicate that = (Predicate) o;
            if (that.identifier.equals(this.identifier) && this.terms.length == that.terms.length) {
                equals = true;
                for (int i = 0; i < this.terms.length; i++)
                    equals = this.terms[i].equals(that.terms[i]);
            }
        }
        return equals;
    }

}

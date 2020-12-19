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

public class Function extends Term {

    private Term[] terms = null;

    public Function(Identifier identifier, Term[] terms) {
        this.setIdentifier(identifier);
        this.setTerms(terms);
        this.type = Type.Function;
    }

    private Function() {

    }

    public Function substitute(SubstitutionSet theta) {
        Function function = new Function();
        function.identifier = this.identifier.clone();
        function.terms = this.terms.clone();
        for (Substitution subst : theta) {
            for (int i = 0; i < terms.length; i++) {
                if (terms[i].equals(subst.substituted))
                    function.terms[i] = subst.substitutor;
            }
        }
        return function;
    }

    @Override
    public String toString() {
        String txt = "";
        for (Term term : terms)
            txt += term.toString() + ", ";
        if (txt.length() > 2)
            txt = getIdentifier().getText() + "(" + txt.substring(0, txt.length() - 2) + ")";
        else
            txt = getIdentifier().toString();
        return txt;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public Function clone() {
        return new Function(getIdentifier().clone(), terms.clone());
    }

    @Override
    public boolean isGround() {
        boolean ground = true;
        for (Term term : terms)
            ground &= term.isGround();
        return ground;
    }

    public Term[] getTerms() {
        return terms;
    }

    public void setTerms(Term[] terms) {
        this.terms = terms;
    }

    @Override
    public int hashCode() {
        int hashCode = identifier.hashCode() + type.hashCode();
        for (Term term : terms)
            hashCode += term.hashCode();
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (o instanceof Function) {
            Function that = (Function) o;
            equals = identifier.getText().equals(that.identifier.getText());
            if (equals && this.terms.length == that.terms.length) {
                for (int i = 0; i < terms.length; i++)
                    equals &= this.terms[i].equals(that.terms[i]);
            }
        }
        return equals;
    }

}

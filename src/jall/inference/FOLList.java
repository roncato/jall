/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.inference;

import java.util.LinkedList;

public class FOLList {

    private Predicate predicate = null;
    private LinkedList<Term> terms = null;

    public FOLList(Predicate predicate) {
        this.predicate = predicate;
        buildList();
    }

    private FOLList(LinkedList<Term> terms) {
        this.terms = terms;
    }

    private FOLList() {
        terms = new LinkedList<Term>();
    }

    public void buildList() {
        LinkedList<Term> list = new LinkedList<Term>();
        for (Term term : predicate.getTerms())
            list.add(term);
        this.terms = list;
    }

    public boolean isAtom() {
        return terms.size() <= 1;
    }

    public boolean isVariable() {
        return terms.size() == 1 && terms.get(0).getType() == Term.Type.Variable;
    }

    public boolean isFunction() {
        return terms.size() == 1 && terms.get(0).getType() == Term.Type.Function;
    }

    public boolean isConstant() {
        return terms.size() == 1 && terms.get(0).getType() == Term.Type.Constant;
    }

    public boolean isPredicate() {
        return terms.size() == 0 && predicate != null;
    }

    public FOLList first() {
        FOLList folList = null;
        if (predicate != null) {
            folList = new FOLList();
            folList.predicate = predicate;
        } else {
            folList = new FOLList();
            folList.terms.add(terms.get(0));
        }
        return folList;
    }

    public FOLList remaining() {

        FOLList folList = null;
        if (predicate != null)
            folList = new FOLList(new LinkedList<Term>(terms));
        else {
            LinkedList<Term> remaining = new LinkedList<Term>(terms.subList(1, terms.size()));
            folList = new FOLList(remaining);
        }
        return folList;
    }

    public Variable asVariable() {
        Variable variable = null;
        if (isVariable())
            variable = (Variable) terms.get(0);
        return variable;
    }

    public boolean occurs(Variable variable) {
        boolean occurs = false;
        for (Term term : terms) {
            if (term.equals(variable)) {
                occurs = true;
                break;
            }
        }
        return occurs;
    }

    public Substitution substitute(Variable variable) {
        return new Substitution(terms.get(0), variable);
    }

    public FOLList apply(SubstitutionSet theta) {
        FOLList list = clone();

        for (Substitution subst : theta) {
            int index = terms.indexOf((subst.substituted));
            if (index > 0)
                list.terms.set(index, subst.substitutor);
        }

        return list;
    }

    @Override
    public boolean equals(Object o) {
        boolean equals = false;

        if (o instanceof FOLList) {
            FOLList that = (FOLList) o;
            if (this.terms.size() == that.terms.size()) {
                equals = true;
                for (int i = 0; i < this.terms.size(); i++)
                    equals = this.terms.get(i).equals(that.terms.get(i));
                if (this.predicate != null && that.predicate != null)
                    equals = this.predicate.getIdentifier().equals(that.predicate.getIdentifier());
            }
        }

        return equals;
    }

    @Override
    public String toString() {
        String text = "";
        if (predicate != null)
            text = predicate.getIdentifier().toString() + " ";
        for (Term term : terms)
            text += term.toString() + " ";
        if (text.length() > 0)
            text = text.substring(0, text.length() - 1);
        return text;
    }

    @Override
    public FOLList clone() {
        FOLList clone = new FOLList();
        clone.predicate = this.predicate;
        for (Term term : terms)
            clone.terms.add(term);
        return clone;
    }

}

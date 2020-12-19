/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.inference;

import jall.inference.Term.Type;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SubstitutionSet implements Iterable<Substitution> {

    private Set<Substitution> set = null;

    public SubstitutionSet() {
        set = new HashSet<Substitution>();
    }

    /**
     * Composes substitutions set theta1 and theta2
     *
     * @param theta1
     * @param theta2
     * @return
     */
    public static SubstitutionSet compose(SubstitutionSet theta1, SubstitutionSet theta2) {
        SubstitutionSet composition = new SubstitutionSet();

        // Adds set one and substitute terms if applicable by subst in theta2

        for (Substitution subst : theta1) {
            if (subst.substitutor.getType() == Type.Function) {
                Function f = (Function) (subst.substitutor);
                f = f.substitute(theta2);
                subst.substitutor = f;
            } else if (subst.substitutor.getType() == Type.Variable) {
                for (Substitution subst2 : theta2) {
                    if (subst.substitutor.equals(subst2.substituted))
                        subst.substitutor = subst2.substitutor;
                }
            }
            composition.add(subst);
        }

        // Adds set theta2 if variable not present
        for (Substitution subst : theta2) {
            if (!theta1.constainsSubstituted(subst))
                composition.add(subst);
        }
        return composition;
    }

    public static SubstitutionSet union(SubstitutionSet theta1, SubstitutionSet theta2) {
        SubstitutionSet composition = new SubstitutionSet();
        composition.set.addAll(theta1.set);
        composition.set.addAll(theta2.set);
        return composition;
    }

    public boolean constainsSubstituted(Substitution subst) {
        boolean contains = false;
        for (Substitution s : set) {
            if (s.substituted.equals(subst.substituted)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    public void add(Substitution subst) {
        set.add(subst);
    }

    public int size() {
        return set.size();
    }

    @Override
    public Iterator<Substitution> iterator() {
        return set.iterator();
    }

    @Override
    public String toString() {
        return set.toString();
    }

}

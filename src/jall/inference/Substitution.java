/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.inference;

public class Substitution {
    public Term substitutor = null;
    public Term substituted = null;

    public Substitution(Term substitutor, Term substituted) {
        this.substitutor = substitutor;
        this.substituted = substituted;
    }

    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (o instanceof Substitution) {
            Substitution that = (Substitution) o;
            equals = this.substitutor.equals(that.substitutor) && this.substituted.equals(that.substituted);
        }
        return equals;
    }

    @Override
    public String toString() {
        return substitutor.toString() + "/" + substituted.toString();
    }

    @Override
    public int hashCode() {
        return substitutor.hashCode() / substituted.hashCode();
    }

}

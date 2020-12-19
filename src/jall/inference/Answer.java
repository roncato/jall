/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.inference;

public class Answer {

    public Predicate query = null;
    public SubstitutionSet theta = null;

    public Answer(Predicate query, SubstitutionSet theta) {
        this.query = query;
        this.theta = theta;
    }

    @Override
    public String toString() {
        String thetaString = "FAILURE";
        if (theta != null)
            thetaString = theta.toString();
        return query.toString() + "," + thetaString;
    }

}

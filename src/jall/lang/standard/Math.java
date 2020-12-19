/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.standard;

import jall.lang.ast.literal.DoubleLiteral;

public class Math {

    public DoubleLiteral max(DoubleLiteral ref1, DoubleLiteral ref2) {
        double result = 0;
        result = java.lang.Math.max(ref1.getValue(), ref2.getValue());
        return new DoubleLiteral(result);
    }

}

/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.exceptions;

public class ContextualErrorExpression extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ContextualErrorExpression(String msg) {
        super(msg);
    }

}

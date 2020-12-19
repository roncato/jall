/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.interpreter.entities;

import jall.lang.ast.literal.Literal;

public class ProcedureFunction extends Entity {

    public ProcedureFunction(jall.lang.ast.Identifier Identifier) {
        super(Identifier);
        type = Type.ProcedureFunction;
    }

    @Override
    public void assign(Literal literal) {
        if (literal.getType() == Literal.Type.ProcedureFunction)
            this.literal = literal;
        else
            throw new IllegalArgumentException("Cannot assing literal type '" + literal.getType() + "' to procedure entity.");
    }

}

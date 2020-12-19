/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.interpreter.entities;

import jall.lang.ast.Identifier;
import jall.lang.ast.literal.IntLiteral;
import jall.lang.ast.literal.Literal;

/**
 * Symbolizes a reference or pointer to a variable.
 *
 * @author Lucas
 */
public class Reference extends Entity {

    public Reference(Identifier identifier) {
        super(identifier);
        type = Type.Reference;
    }

    public void assign(Literal literal, IntLiteral[] indices) {
        if (literal == null)
            this.literal = null;
        else if (this.literal == null) {
            if (literal.getType() == Literal.Type.Array || literal.getType() == Literal.Type.Struct)
                this.literal = literal;
            else
                this.literal = literal.clone();
        } else if (this.literal.getType() == Literal.Type.Null)
            this.literal = literal;
        else
            this.literal.setValue(literal, indices);
    }

    @Override
    public void assign(Literal literal) {
        if (literal == null)
            this.literal = null;
        else if (this.literal == null) {
            if (literal.getType() == Literal.Type.Array || literal.getType() == Literal.Type.Struct)
                this.literal = literal;
            else
                this.literal = literal.clone();
        } else
            this.literal.setValue(literal);
    }

}

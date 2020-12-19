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
import jall.lang.ast.literal.Literal;

public class Constant extends Term {

    public Literal literal = null;

    public Constant(Identifier identifier) {
        this.setIdentifier(identifier);
        this.type = Type.Constant;
    }

    public Constant(Literal literal) {
        this.literal = literal;
        this.identifier = new Identifier(literal.toString());
        this.type = Type.Constant;
    }

    @Override
    public String toString() {
        return getIdentifier().getText();
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public Constant clone() {
        return new Constant(getIdentifier().clone());
    }

    @Override
    public boolean isGround() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (o instanceof Constant) {
            Constant that = (Constant) o;
            equals = identifier.getText().equals(that.identifier.getText());
        }
        return equals;
    }


    @Override
    public int hashCode() {
        return identifier.hashCode() + type.hashCode();
    }

}

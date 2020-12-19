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

public class Variable extends Term {

    public Variable(Identifier identifier) {
        this.setIdentifier(identifier);
        this.type = Type.Variable;
    }

    @Override
    public String toString() {
        return "?" + getIdentifier().toString();
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public Variable clone() {
        Variable clone = new Variable(getIdentifier().clone());
        return clone;
    }

    @Override
    public boolean isGround() {
        return false;
    }


    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (o instanceof Variable) {
            Variable that = (Variable) o;
            equals = identifier.getText().equals(that.identifier.getText());
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode() + type.hashCode();
    }

}

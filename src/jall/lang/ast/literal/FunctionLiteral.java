/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.literal;

import jall.inference.Function;
import jall.lang.ast.Visitor;
import jall.lang.type.TypeDenoter;

public class FunctionLiteral extends Literal {

    private Function value = null;

    public FunctionLiteral(Function function) {
        this.value = function;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, null);
    }

    @Override
    public FunctionLiteral clone() {
        return new FunctionLiteral(value.clone());
    }

    @Override
    public Literal convert(TypeDenoter typeDenoter) {
        // TODO Auto-generated method stub
        return null;
    }

    public Function setValue(Function value) {
        return this.value = value;
    }

    public Function getValue() {
        return value;
    }

    @Override
    public void setValue(Literal literal) {
        if (literal.type == Type.Function) {
            FunctionLiteral that = (FunctionLiteral) literal;
            value = that.value;
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }

}

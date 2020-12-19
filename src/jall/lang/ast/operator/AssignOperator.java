/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.operator;

import jall.lang.ast.Visitor;
import jall.lang.ast.literal.Literal;
import jall.lang.type.TypeDenoter;
import jall.lang.type.TypeDenoter.Type;

public class AssignOperator extends Operator {

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public TypeDenoter results(TypeDenoter left, TypeDenoter right) {
        TypeDenoter typeDenoter = null;
        if (TypeDenoter.equivalent(left, right))
            typeDenoter = left;
        else if (left.getType() == Type.Object)
            typeDenoter = TypeDenoter.createTypeDenoter(Type.Object);

        return typeDenoter;
    }

    @Override
    public Literal evaluate(Literal left, Literal right) {
        return null;
    }

    @Override
    public Literal evaluate(Literal literal) {
        return null;
    }

    @Override
    public AssignOperator clone() {
        return new AssignOperator();
    }


}

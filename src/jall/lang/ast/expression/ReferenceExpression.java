/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.expression;

import jall.lang.ast.Identifier;
import jall.lang.ast.Visitor;

/**
 * @author Lucas Batista
 */
public class ReferenceExpression extends NameExpression {

    public ReferenceExpression(Identifier identifier, Expression[] indexExpressions) {
        super(identifier, indexExpressions);
        type = Type.Reference;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public String toString() {
        String indexStr = "";
        for (Expression indexExpression : getIndexExpressions())
            indexStr += "[" + indexExpression.toString() + "]";
        return identifier.toString() + indexStr;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

}

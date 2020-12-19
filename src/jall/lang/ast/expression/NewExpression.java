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

public class NewExpression extends Expression {

    private Identifier identifier = null;
    private Expression[] expressionsArgs = null;
    private boolean isArray = false;

    public NewExpression(Identifier identifier, Expression[] expressions, boolean isArray) {
        this.identifier = identifier;
        this.expressionsArgs = expressions;
        this.isArray = isArray;
        type = Type.New;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, null);
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Expression[] getExpressionsArgs() {
        return expressionsArgs;
    }

    public boolean isArray() {
        return isArray;
    }

    @Override
    public String toString() {
        String expTxt = "";
        if (isArray) {
            for (Expression expression : expressionsArgs)
                expTxt += "[" + expression.toString() + "]";
        } else {
            expTxt += "";
            for (Expression expression : expressionsArgs)
                expTxt += expression.toString() + ", ";
            if (expTxt.length() > 0)
                expTxt = expTxt.substring(0, expTxt.length() - 2);
            expTxt = "(" + expTxt + ")";
        }
        return "new " + identifier.getText() + expTxt;
    }

}

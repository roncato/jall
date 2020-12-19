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

public class CallExpression extends NameExpression {

    private Expression[] argsExpressions = null;

    public CallExpression(Identifier identifier, Expression[] expressions, Expression[] indexExpressions) {
        super(identifier, indexExpressions);
        this.argsExpressions = expressions;
        type = Type.Call;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    public Expression[] getArgsExpressions() {
        return argsExpressions;
    }

    @Override
    public String toString() {
        String args = "";
        String indexStr = "";
        for (Expression expression : argsExpressions)
            args += expression.toString() + ", ";
        if (args.length() > 0)
            args = args.substring(0, args.length() - 2);
        for (Expression indexExpression : getIndexExpressions())
            indexStr += "[" + indexExpression.toString() + "]";
        return identifier.toString() + "(" + args + ")" + indexStr;
    }

}

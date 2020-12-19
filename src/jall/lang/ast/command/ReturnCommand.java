/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.command;

import jall.lang.ast.Visitor;
import jall.lang.ast.expression.Expression;

public class ReturnCommand extends Command {

    private Expression expression = null;

    public ReturnCommand(Expression expression) {
        this.expression = expression;
        this.type = Type.Return;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        String expStr = expression != null ? " " + expression.toString() : "";
        return "return" + expStr + ";";
    }

}

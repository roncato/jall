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


/**
 *
 */
public class WhileCommand extends Command {

    private Expression expression = null;
    private Command command = null;

    public WhileCommand(Expression expression, Command command) {
        this.expression = expression;
        this.command = command;
        this.type = Type.While;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    public Expression getExpression() {
        return expression;
    }

    public Command getCommand() {
        return command;
    }


    @Override
    public String toString() {
        String txt = "";
        if (expression != null)
            txt += expression.toString();
        txt = "while (" + txt + ")";
        txt += "{";
        if (command != null)
            txt += command.toString();
        txt += "}";
        return txt;
    }
}

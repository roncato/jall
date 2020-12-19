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

public class ForCommand extends Command {

    private Command initCommand = null;
    private Expression expression = null;
    private Command incrementCommand = null;
    private Command bodyCommand = null;

    public ForCommand(Command initCommand, Expression expression, Command incrementCommand,
                      Command bodyCommand) {
        this.initCommand = initCommand;
        this.expression = expression;
        this.incrementCommand = incrementCommand;
        this.bodyCommand = bodyCommand;
        type = Type.For;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, null);
    }

    public Command getInitCommand() {
        return initCommand;
    }

    public Expression getExpression() {
        return expression;
    }

    public Command getIncrementCommand() {
        return incrementCommand;
    }

    public Command getBodyCommand() {
        return bodyCommand;
    }

    @Override
    public String toString() {
        String txt = "";
        if (initCommand != null)
            txt += initCommand.toString();
        txt += "; ";
        if (expression != null)
            txt += expression.toString();
        txt += "; ";
        if (incrementCommand != null)
            txt += incrementCommand.toString();
        txt = "for (" + txt + ")";
        txt += "{";
        if (bodyCommand != null)
            txt += bodyCommand.toString();
        txt += "}";
        return txt;
    }

}

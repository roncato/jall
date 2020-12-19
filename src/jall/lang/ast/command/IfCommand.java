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
public class IfCommand extends Command {
    private Expression expression = null;
    private Command command = null;
    private IfCommand elseCommand = null;

    public IfCommand(Expression expression, Command command) {
        this.expression = expression;
        this.command = command;
        this.type = Type.If;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public IfCommand getElseCommand() {
        return elseCommand;
    }

    public void setElseCommand(IfCommand elseCommand) {
        this.elseCommand = elseCommand;
    }

    @Override
    public String toString() {
        String elseStr = elseCommand != null ? " else " + elseCommand.toString() : "";
        return "if (" + expression.toString() + ")" + command.toString() + elseStr;
    }

}

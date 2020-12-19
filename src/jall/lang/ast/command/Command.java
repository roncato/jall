/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.command;


/**
 *
 */
public abstract class Command extends jall.lang.ast.Symbol {

    protected Type type = null;
    protected Command parent = null;

    public Type getType() {
        return type;
    }

    public Command getParent() {
        return parent;
    }

    public void setParent(Command parent) {
        this.parent = parent;
    }

    @Override
    public abstract String toString();

    public enum Type {
        Assign,
        AssignDeclaration,
        Break,
        Call,
        Declaration,
        If,
        Return,
        Sequential,
        While,
        For,
        StandardProcedure
    }

}

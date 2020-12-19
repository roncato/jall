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

public class SequentialCommand extends Command {
    private Command left = null;
    private Command right = null;

    public SequentialCommand(Command left, Command right) {
        this.left = left;
        this.right = right;
        this.type = Type.Sequential;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    public Command getLeft() {
        return left;
    }

    public Command getRight() {
        return right;
    }

    @Override
    public String toString() {
        return left.toString() + ";" + right.toString();
    }

}

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
import jall.lang.ast.expression.NameExpression;


/**
 * A call command node.
 *
 * @author Lucas
 */
public class CallCommand extends Command {

    private NameExpression[] nameExpressions = null;

    public CallCommand(NameExpression[] nameExpressions) {
        this.nameExpressions = nameExpressions;
        this.type = Type.Call;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    public NameExpression[] getNameExpressions() {
        return nameExpressions;
    }

    @Override
    public String toString() {
        String txt = "";
        for (NameExpression expression : nameExpressions)
            txt += expression.toString() + ".";
        if (txt.length() >= 0)
            txt = txt.substring(0, txt.length() - 1);
        return txt;
    }

}

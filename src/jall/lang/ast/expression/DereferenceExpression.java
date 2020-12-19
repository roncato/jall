/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.expression;

import jall.lang.ast.Visitor;


/**
 *
 */
public class DereferenceExpression extends jall.lang.ast.expression.Expression {

    private NameExpression[] nameExpressions = null;

    public DereferenceExpression(NameExpression[] nameExpressions) {
        this.nameExpressions = nameExpressions;
        type = Type.Dereference;
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
        for (NameExpression nameExpression : nameExpressions)
            txt += nameExpression.toString() + ".";
        if (txt.length() > 0)
            txt = txt.substring(0, txt.length() - 1);
        return txt;
    }
}

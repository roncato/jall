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
import jall.lang.ast.expression.NameExpression;


/**
 *
 */
public class AssignCommand extends Command {

    private NameExpression[] nameExpressions = null;
    private Expression expression = null;

    public AssignCommand(NameExpression[] nameExpressions, Expression expression) {
        this.nameExpressions = nameExpressions;
        this.expression = expression;
        this.type = Type.Assign;
    }


    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    public boolean isComplex() {
        return nameExpressions.length > 1;
    }

    public Expression getExpression() {
        return expression;
    }

    public NameExpression[] getNameExpressions() {
        return nameExpressions;
    }

    @Override
    public String toString() {
        String refStr = "";
        for (NameExpression nameExpression : nameExpressions)
            refStr += nameExpression.toString() + ".";
        refStr = refStr.substring(0, refStr.length() - 1);
        return refStr + " = " + expression.toString();
    }

}

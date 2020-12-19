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
import jall.lang.ast.declaration.Declaration;
import jall.lang.ast.expression.Expression;


/**
 *
 */
public class AssignDeclarationCommand extends Command {

    private Declaration declaration = null;
    private Expression expression = null;

    public AssignDeclarationCommand(Declaration declaration, Expression expression) {
        this.declaration = declaration;
        this.expression = expression;
        this.type = Type.AssignDeclaration;
    }


    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    public Declaration getDeclaration() {
        return declaration;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return declaration.toString() + " = " + expression.toString();
    }

}

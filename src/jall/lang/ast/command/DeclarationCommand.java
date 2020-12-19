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


/**
 *
 */
public class DeclarationCommand extends Command {

    private Declaration declaration = null;

    public DeclarationCommand(Declaration declaration) {
        this.declaration = declaration;
        this.type = Type.Declaration;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    public Declaration getDeclaration() {
        return declaration;
    }

    @Override
    public String toString() {
        return declaration.toString();
    }
}

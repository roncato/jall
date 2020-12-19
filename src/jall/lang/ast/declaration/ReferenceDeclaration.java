/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.declaration;

import jall.lang.ast.Identifier;
import jall.lang.ast.Visitor;
import jall.lang.type.TypeDenoter;


/**
 *
 */
public class ReferenceDeclaration extends jall.lang.ast.declaration.Declaration {

    public ReferenceDeclaration(TypeDenoter typeDenoter, Identifier identifier) {
        this.typeDenoter = typeDenoter;
        this.identifier = identifier;
        this.type = DeclarationType.Reference;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public String toString() {
        return typeDenoter.toString() + " " + identifier.getText();
    }

}

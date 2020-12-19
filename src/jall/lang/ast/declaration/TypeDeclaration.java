/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.declaration;

import jall.lang.ast.Visitor;
import jall.lang.type.TypeDenoter;

public class TypeDeclaration extends Declaration {

    public TypeDeclaration(TypeDenoter typeDenoter) {
        this.typeDenoter = typeDenoter;
        this.identifier = typeDenoter.getIdentifier();
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, null);
    }

    @Override
    public String toString() {
        return "typedef " + identifier.getText();
    }

}

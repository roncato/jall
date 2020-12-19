/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.contextual;

import jall.lang.ast.declaration.Declaration;

public class Attribute {

    private Declaration declaration = null;

    public Attribute(Declaration declaration) {
        this.declaration = declaration;
    }

    public Declaration getDeclaration() {
        return declaration;
    }

    @Override
    public String toString() {
        return declaration.toString();
    }

}

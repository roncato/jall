/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.type;

import jall.lang.ast.Identifier;
import jall.lang.ast.Visitor;
import jall.lang.ast.declaration.Declaration;

import java.util.HashMap;
import java.util.Map;

public class StructTypeDenoter extends TypeDenoter {

    private Map<Identifier, Declaration> fieldsDeclarations = null;

    public StructTypeDenoter() {
        type = Type.Struct;
        fieldsDeclarations = new HashMap<Identifier, Declaration>();
    }

    public Map<Identifier, Declaration> getDeclarations() {
        return fieldsDeclarations;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

}

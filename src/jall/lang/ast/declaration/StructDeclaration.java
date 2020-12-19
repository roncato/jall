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
public class StructDeclaration extends jall.lang.ast.declaration.Declaration {

    private Declaration[] fieldDeclarations = null;

    public StructDeclaration(TypeDenoter typeDenoter, Declaration[] innerDeclarations,
                             Identifier identifier) {
        this.typeDenoter = typeDenoter;
        this.fieldDeclarations = innerDeclarations;
        this.identifier = identifier;
        this.type = DeclarationType.Struct;
        init();
    }

    private void init() {
        assignFieldsParent();
    }

    private void assignFieldsParent() {
        for (Declaration fieldDeclaration : fieldDeclarations)
            fieldDeclaration.parent = this;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    public Declaration[] getFieldDeclarations() {
        return fieldDeclarations;
    }

    @Override
    public String toString() {
        String txt = "";
        for (Declaration fieldDeclaration : fieldDeclarations)
            txt += fieldDeclaration.toString() + ",";
        if (txt.length() > 0)
            txt = txt.substring(0, txt.length() - 1);
        return "{" + txt + "}";
    }

}

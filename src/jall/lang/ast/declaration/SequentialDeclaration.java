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


/**
 *
 */
public class SequentialDeclaration extends jall.lang.ast.declaration.Declaration {

    public Declaration left = null;
    public Declaration right = null;

    public SequentialDeclaration(Declaration left, Declaration right) {
        this.left = left;
        this.right = right;
        this.type = DeclarationType.Sequential;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }


    @Override
    public String toString() {
        return left.toString() + right.toString();
    }
}
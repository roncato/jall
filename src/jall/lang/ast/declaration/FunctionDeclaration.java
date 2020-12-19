/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.declaration;

import jall.inference.Constant;
import jall.inference.Function;
import jall.lang.ast.Visitor;
import jall.lang.type.TypeDenoter;


/**
 *
 */
public class FunctionDeclaration extends jall.lang.ast.declaration.Declaration {

    private Function function = null;
    private Constant constant = null;

    public FunctionDeclaration(Function function, Constant constant, TypeDenoter typeDenoter) {
        this.function = function;
        this.constant = constant;
        this.typeDenoter = typeDenoter;
        this.identifier = function.getIdentifier();
        this.type = DeclarationType.Function;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    public Function getFunction() {
        return function;
    }

    public Constant getConstant() {
        return constant;
    }


    @Override
    public String toString() {
        return function.toString() + " := " + constant.toString();
    }
}

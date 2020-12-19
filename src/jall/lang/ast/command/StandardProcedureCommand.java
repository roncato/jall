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
import jall.lang.ast.declaration.ProcedureFunctionDeclaration;

public class StandardProcedureCommand extends Command {

    private ProcedureFunctionDeclaration declaration = null;
    private String nameSpace = null;

    public StandardProcedureCommand(String nameSpace) {
        this.nameSpace = nameSpace;
        type = Type.StandardProcedure;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    public ProcedureFunctionDeclaration getDeclaration() {
        return declaration;
    }

    public ProcedureFunctionDeclaration setDeclaration(ProcedureFunctionDeclaration declaration) {
        return this.declaration = declaration;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    @Override
    public String toString() {
        return declaration.toString();
    }

}

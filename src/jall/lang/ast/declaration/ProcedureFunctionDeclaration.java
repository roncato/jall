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
import jall.lang.ast.command.Command;
import jall.lang.type.TypeDenoter;


/**
 *
 */
public class ProcedureFunctionDeclaration extends jall.lang.ast.declaration.Declaration {

    private ReferenceDeclaration[] argsDeclarations = null;
    private Command command = null;
    private boolean declaredTypeLess = false;

    public ProcedureFunctionDeclaration(TypeDenoter typeDenoter,
                                        Identifier identifier, ReferenceDeclaration[] argsDeclarations,
                                        Command command, boolean typeLess) {
        this.typeDenoter = typeDenoter;
        this.identifier = identifier;
        this.argsDeclarations = argsDeclarations;
        this.command = command;
        this.type = DeclarationType.ProcedureFunction;
        this.declaredTypeLess = typeLess;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    public ReferenceDeclaration[] getArgsDeclarations() {
        return argsDeclarations;
    }

    public Command getCommand() {
        return command;
    }

    @Override
    public String toString() {
        String text = "";
        for (Declaration argDeclaration : argsDeclarations)
            text += argDeclaration.toString() + ", ";
        if (text.length() > 0)
            text = text.substring(0, text.length() - 2);
        text = typeDenoter.toString() + " " + identifier.toString() + "(" + text + ")";
        return text;
    }

    public boolean isDeclaredTypeLess() {
        return declaredTypeLess;
    }

}

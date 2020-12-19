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
import jall.lang.type.TypeDenoter;


/**
 *
 */
public abstract class Declaration extends jall.lang.ast.Symbol {

    protected Identifier identifier = null;
    protected TypeDenoter typeDenoter = null;
    protected DeclarationType type = null;
    protected boolean isStandard = false;
    protected boolean isFinal = false;
    protected Declaration parent = null;

    public TypeDenoter getTypeDenoter() {
        return typeDenoter;
    }

    public DeclarationType getType() {
        return type;
    }

    public boolean isStandard() {
        return isStandard;
    }

    public void setStandard(boolean isStandard) {
        this.isStandard = isStandard;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    @Override
    public abstract String toString();

    public Declaration getParent() {
        return parent;
    }

    public enum DeclarationType {
        Fact,
        Function,
        ProcedureFunction,
        Reference,
        Rule,
        Sentence,
        Struct,
        Sequential
    }
}

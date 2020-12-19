/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.interpreter.entities;

import jall.lang.ast.Identifier;
import jall.lang.ast.literal.Literal;

public abstract class Entity {

    protected Identifier identifier = null;
    protected Literal literal = null;
    protected Type type = null;
    public Entity(Identifier Identifier) {
        this.identifier = Identifier;
    }

    public static Entity createEntity(Identifier identifier) {
        Entity entity = null;
        switch (identifier.getDeclaration().getType()) {
            case Fact:
            case Function:
            case Rule:
            case Sentence:
            case Reference:
            case Struct:
                entity = new Reference(identifier);
                break;
            case ProcedureFunction:
                entity = new ProcedureFunction(identifier);
                break;
            case Sequential:
                break;
        }
        return entity;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Literal getLiteral() {
        return literal;
    }

    public Literal setLiteral(Literal literal) {
        return this.literal = literal;
    }

    @Override
    public String toString() {
        String strLiteral = Literal.toJSON(literal);
        return "\"" + identifier.getText() + "\":" + strLiteral;
    }

    public abstract void assign(Literal literal);

    public enum Type {
        ProcedureFunction,
        Reference,
        SentenceReference
    }

}

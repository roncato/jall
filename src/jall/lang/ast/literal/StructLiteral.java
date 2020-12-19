/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.literal;

import jall.lang.JallLexicon;
import jall.lang.Token;
import jall.lang.ast.Identifier;
import jall.lang.ast.Visitor;
import jall.lang.ast.declaration.Declaration;
import jall.lang.ast.declaration.StructDeclaration;
import jall.lang.interpreter.Scope;
import jall.lang.interpreter.entities.Entity;
import jall.lang.interpreter.entities.Reference;
import jall.lang.type.TypeDenoter;

public class StructLiteral extends Literal {

    private Scope scope = null;

    public StructLiteral() {
        scope = new Scope();
        type = Type.Struct;
    }

    public static Literal createLiteral(StructDeclaration declaration) {
        StructLiteral struct = new StructLiteral();

        // Creates this reference in the scope
        buildThisIdentifier(struct, declaration);

        // Creates fields literal
        for (Declaration fieldDeclaration : declaration.getFieldDeclarations()) {
            Literal fieldLiteral = null;

            // If child difference than a struct
            if ((fieldDeclaration.getTypeDenoter().getType() != TypeDenoter.Type.Struct ||
                    fieldDeclaration.getType() == Declaration.DeclarationType.ProcedureFunction) &&
                    fieldDeclaration.getTypeDenoter().getType() != TypeDenoter.Type.Array)
                fieldLiteral = Literal.createLiteral(fieldDeclaration);

            Entity entity = Entity.createEntity(fieldDeclaration.getIdentifier());
            entity.setLiteral(fieldLiteral);
            struct.scope.enter(fieldDeclaration.getIdentifier(), entity);

        }
        return struct;
    }

    private static void buildThisIdentifier(StructLiteral struct, StructDeclaration declaration) {
        Identifier thisIdentifier = new Identifier(new Token(JallLexicon.THIS_IDENTIFIER, Token.TokenType.Identifier, declaration.getTypeDenoter().getIdentifier().getToken().getStartPosition()));
        thisIdentifier.setDeclaration(declaration);
        Reference thisReference = new Reference(thisIdentifier);
        thisReference.setLiteral(struct);
        struct.scope.enter(thisIdentifier, thisReference);
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public StructLiteral clone() {
        StructLiteral clone = new StructLiteral();
        clone.scope = scope.clone();
        return clone;
    }

    @Override
    public void setValue(Literal literal) {

        switch (literal.type) {
            case Struct:
                StructLiteral that = (StructLiteral) literal;
                this.scope = that.scope;
                break;
            case Object:
                ObjectLiteral objectLiteral = (ObjectLiteral) literal;
                literal = objectLiteral.getValue();
                setValue(literal);
                break;
            default:
                throw new IllegalArgumentException("Cannot assign literal type '" + literal.type.toString() + "' to struct.");
        }

    }

    public Scope getScope() {
        return scope;
    }

    @Override
    public Literal convert(TypeDenoter typeDenoter) {
        Literal literal = null;
        switch (typeDenoter.getType()) {
            case Object:
                literal = new ObjectLiteral(this);
                break;
            case Struct:
                literal = this;
                break;
            case Array:
                ArrayLiteral arr = new ArrayLiteral();
                arr.getValue().add(this);
                literal = arr;
                break;
            case Boolean:
                literal = new BooleanLiteral(true);
                break;
            case String:
                literal = new StringLiteral(this.toString());
                break;
        }
        return literal;
    }

    @Override
    public String toString() {
        String txt = "";
        for (Identifier identifier : scope.getIdentifiers()) {
            Entity entity = scope.retrieve(identifier);
            if (entity.getLiteral() != this && !identifier.getText().equals(JallLexicon.THIS_IDENTIFIER))
                txt += entity.toString() + ", ";
        }
        if (txt.length() > 2)
            txt = txt.substring(0, txt.length() - 2);
        return "{" + txt + "}";
    }

}

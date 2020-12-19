/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast;

import jall.lang.Token;
import jall.lang.Token.TokenType;
import jall.lang.ast.declaration.Declaration;


/**
 *
 */
public class Identifier extends jall.lang.ast.Symbol implements Comparable<Identifier> {

    private Token token = null;
    private Declaration declaration = null;
    public Identifier(Token token) {
        this.token = token;
    }

    public Identifier(String name) {
        token = new Token(name, TokenType.Identifier);
    }

    public String getText() {
        return token.getText();
    }

    @Override
    public String toString() {
        return token.getText();
    }

    @Override
    public int hashCode() {
        return token.getText().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (o instanceof Identifier) {
            Identifier that = (Identifier) o;
            equals = that.token.getText().equals(this.token.getText());
        }
        return equals;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public Identifier clone() {
        Identifier clone = new Identifier(token.clone());
        return clone;
    }

    public Token getToken() {
        return token;
    }

    public Declaration setDeclaration(Declaration declaration) {
        this.declaration = declaration;
        return declaration;
    }

    public Declaration getDeclaration() {
        return declaration;
    }

    @Override
    public int compareTo(Identifier that) {
        return that.token.getText().compareTo(this.token.getText());
    }

    public enum Type {
        This,
        Name
    }

}

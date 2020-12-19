/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.declaration;

import jall.inference.Predicate;
import jall.inference.Term;
import jall.lang.Token;
import jall.lang.Token.TokenType;
import jall.lang.ast.Identifier;
import jall.lang.ast.Visitor;
import jall.lang.type.TypeDenoter;


/**
 *
 */
public class FactDeclaration extends jall.lang.ast.declaration.Declaration {

    private Predicate fact = null;

    public FactDeclaration(Predicate fact, TypeDenoter typeDenoter) {
        this.fact = fact;
        this.typeDenoter = typeDenoter;
        String text = getFactIdentifierText(fact);
        this.identifier = new Identifier(new Token(text, TokenType.Identifier, fact.getIdentifier().getToken().getStartPosition()));
        this.type = DeclarationType.Fact;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    private String getFactIdentifierText(Predicate fact) {
        String txt = fact.getIdentifier().toString();
        for (Term term : fact.getTerms()) {
            txt += term.getIdentifier().getText();
        }
        return txt;
    }

    @Override
    public String toString() {
        return "{" + getIdentifier().getText() + ": " + typeDenoter.toString() + ", " + fact.toString() + "}";
    }

    public Predicate getFact() {
        return fact;
    }

}

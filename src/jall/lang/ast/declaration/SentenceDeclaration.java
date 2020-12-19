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
import jall.lang.ast.sentence.Sentence;
import jall.lang.type.TypeDenoter;


/**
 *
 */
public class SentenceDeclaration extends jall.lang.ast.declaration.Declaration {

    public Sentence sentence = null;

    public SentenceDeclaration(Identifier identifier, Sentence sentence, TypeDenoter typeDenoter) {
        this.identifier = identifier;
        this.sentence = sentence;
        this.typeDenoter = typeDenoter;
        this.type = DeclarationType.Sentence;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public String toString() {
        return "{" + getIdentifier().toString() + ": " + typeDenoter.toString() + ", " + sentence.toString() + "}";
    }

}

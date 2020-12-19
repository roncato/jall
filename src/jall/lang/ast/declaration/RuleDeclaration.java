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
import jall.lang.ast.sentence.Rule;
import jall.lang.type.TypeDenoter;


/**
 *
 */
public class RuleDeclaration extends jall.lang.ast.declaration.Declaration {

    private Rule rule = null;

    public RuleDeclaration(Rule rule, TypeDenoter typeDenoter) {
        this.rule = rule;
        this.typeDenoter = typeDenoter;
        this.identifier = rule.getIdentifier();
        this.type = DeclarationType.Rule;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public String toString() {
        return "{" + getIdentifier().toString() + ": " + typeDenoter.toString() + ", " + rule.toString() + "}";
    }

    public Rule getRule() {
        return rule;
    }

}

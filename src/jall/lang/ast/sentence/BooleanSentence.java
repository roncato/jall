/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.sentence;

import jall.lang.ast.Visitor;
import jall.lang.ast.literal.BooleanLiteral;

public class BooleanSentence extends AtomicSentence {

    private BooleanLiteral value = null;

    public BooleanSentence(BooleanLiteral value) {
        this.value = value;
        this.type = Type.Boolean;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public BooleanSentence clone() {
        return new BooleanSentence(value.clone());
    }


    public BooleanLiteral getValue() {
        return value;
    }

}

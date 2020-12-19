/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.type;

import jall.lang.ast.Visitor;

public class BooleanTypeDenoter extends TypeDenoter {

    public BooleanTypeDenoter() {
        type = Type.Boolean;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

}

/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.standard;

import jall.lang.ast.literal.*;

public class Array {

    public IntLiteral arrayLen(ArrayLiteral array) {
        return new IntLiteral(array.getValue().size());
    }

    public BooleanLiteral arrayAdd(ArrayLiteral array, ObjectLiteral object) {
        return new BooleanLiteral(array.getValue().add(object.getValue()));
    }

    public void arrayInsertAt(ArrayLiteral array, ObjectLiteral object, IntLiteral index) {
        array.getValue().add(index.getValue(), object.getValue());
    }

    public ObjectLiteral arrayDeleteAt(ArrayLiteral array, IntLiteral index) {
        return new ObjectLiteral(array.getValue().remove(index.getValue()));
    }

    public void arrayClear(ArrayLiteral array) {
        array.getValue().clear();
    }

    public void arraySwap(ArrayLiteral array, IntLiteral pos1, IntLiteral pos2) {
        Literal literal1 = array.getValue().get(pos1.getValue());
        Literal literal2 = array.getValue().get(pos2.getValue());
        array.getValue().add(pos1.getValue(), literal2);
        array.getValue().remove(pos2.getValue() + 1);
        array.getValue().add(pos2.getValue(), literal1);
        array.getValue().remove(pos1.getValue() + 1);
    }

}

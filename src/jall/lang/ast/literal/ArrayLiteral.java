/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.literal;

import jall.lang.ast.Visitor;
import jall.lang.type.TypeDenoter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArrayLiteral extends Literal implements Iterable<Literal> {

    private List<Literal> value = null;

    public ArrayLiteral() {
        value = new ArrayList<Literal>();
        type = Type.Array;
    }

    public static ArrayLiteral createLiteral(TypeDenoter elementTypeDenoter, IntLiteral[] sizes) {
        ArrayLiteral arrayLiteral = new ArrayLiteral();
        createChildLiteral(elementTypeDenoter, sizes, 0, arrayLiteral);
        return arrayLiteral;
    }

    private static void createChildLiteral(TypeDenoter elementTypeDenoter, IntLiteral[] sizes, int depth, ArrayLiteral parentLiteral) {
        if (depth < sizes.length - 1) {
            for (int i = 0; i < sizes[depth].getValue(); i++) {
                ArrayLiteral literal = new ArrayLiteral();
                createChildLiteral(elementTypeDenoter, sizes, depth + 1, literal);
                parentLiteral.value.add(literal);
            }
        } else {
            for (int i = 0; i < sizes[depth].getValue(); i++) {
                Literal elementLiteral = null;
                if (elementTypeDenoter.getType() != TypeDenoter.Type.Struct)
                    elementLiteral = Literal.createLiteral(elementTypeDenoter);
                parentLiteral.value.add(elementLiteral);
            }
        }
    }

    public void setElementValue(Literal literal, IntLiteral[] indices) {
        if (indices.length > 0) {
            Literal elementLiteral = getElementLiteral(indices);
            if (elementLiteral == null)
                setElementLiteral(indices, literal);
            else
                elementLiteral.setValue(literal);
        } else
            setValue(literal);
    }

    public Literal getElementLiteral(IntLiteral[] indices) {
        return getElementLiteral(this, indices, 0);
    }

    private Literal getElementLiteral(ArrayLiteral parentLiteral, IntLiteral[] indices, int depth) {

        Literal literal = parentLiteral.getValue().get(indices[depth].getValue());
        if (depth < indices.length - 1)
            literal = getElementLiteral((ArrayLiteral) literal, indices, depth + 1);

        return literal;
    }

    public Literal setElementLiteral(IntLiteral[] indices, Literal elementLiteral) {
        return setElementLiteral(this, indices, 0, elementLiteral);
    }

    private Literal setElementLiteral(ArrayLiteral parentLiteral, IntLiteral[] indices, int depth, Literal elementLiteral) {
        Literal literal = parentLiteral.getValue().get(indices[depth].getValue());
        if (depth < indices.length - 1)
            return setElementLiteral((ArrayLiteral) literal, indices, depth + 1, elementLiteral);
        else
            return parentLiteral.value.set(indices[depth].getValue(), elementLiteral);
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public ArrayLiteral clone() {
        ArrayLiteral clone = new ArrayLiteral();
        for (Literal literal : value) {
            if (literal != null)
                literal = literal.clone();
            clone.value.add(literal);
        }
        return clone;
    }

    public List<Literal> getValue() {
        return value;
    }

    @Override
    public void setValue(Literal literal) {

        switch (literal.type) {
            case Array:
                ArrayLiteral that = (ArrayLiteral) literal;
                this.value = that.value;
                break;
            case Object:
                ObjectLiteral objectLiteral = (ObjectLiteral) literal;
                literal = objectLiteral.getValue();
                setValue(literal);
                break;
            default:
                throw new IllegalArgumentException("Cannot assign literal type '" + literal.type.toString() + "' to array.");
        }

    }

    public int size() {
        return value.size();
    }

    @Override
    public Literal convert(TypeDenoter typeDenoter) {
        Literal literal = null;
        switch (typeDenoter.getType()) {
            case Array:
                literal = this;
                break;
            case String:
                literal = new StringLiteral(toString());
                break;
            case Object:
                literal = new ObjectLiteral(this);
                break;
        }
        return literal;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public Iterator<Literal> iterator() {
        return value.iterator();
    }

}

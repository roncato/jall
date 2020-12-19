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
import jall.lang.ast.operator.LogicalOperator;


/**
 *
 */
public class BinarySentence extends Sentence {

    private Sentence left = null;
    private Sentence right = null;
    private LogicalOperator operator = null;

    public BinarySentence(Sentence left, Sentence right, LogicalOperator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
        type = Type.Binary;
    }

    @Override
    public String toString() {
        String txt = left.toString();
        txt += " " + operator.toString();
        txt += " " + right.toString();
        return txt;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public BinarySentence clone() {
        return new BinarySentence(left.clone(), right.clone(), operator.clone());
    }


    public Sentence getLeft() {
        return left;
    }

    public Sentence getRight() {
        return right;
    }

    public LogicalOperator getOperator() {
        return operator;
    }


}

/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.operator;

import jall.exceptions.InterpreterErrorException;
import jall.lang.ast.Visitor;
import jall.lang.ast.literal.BooleanLiteral;
import jall.lang.ast.literal.Literal;
import jall.lang.type.TypeDenoter;
import jall.lang.type.TypeDenoter.Type;


/**
 *
 */
public class LogicalOperator extends Operator {

    public LogicalOperator(String text) {
        this.setText(text);
    }

    public BooleanLiteral evaluate(BooleanLiteral left, BooleanLiteral right) {
        boolean result = false;
        if (isAND())
            result = left.getValue() && right.getValue();
        else if (isOR())
            result = left.getValue() || right.getValue();
        else if (isNOT())
            result = !right.getValue();
        return new BooleanLiteral(result);
    }

    public BooleanLiteral evaluate(BooleanLiteral literal) {
        boolean result = false;
        if (isNOT())
            result = !literal.getValue();
        return new BooleanLiteral(result);
    }

    /**
     *
     */
    public boolean isAND() {
        return getText().equals(LOGICAL[2]);
    }

    /**
     *
     */
    public boolean isOR() {
        return getText().equals(LOGICAL[3]);
    }

    /**
     *
     */
    public boolean isNOT() {
        return getText().equals(NEGATION[0]);
    }

    /**
     *
     */
    public boolean isImply() {
        return getText().equals(LOGICAL[0]);
    }

    /**
     *
     */
    public boolean isBiConditional() {
        return getText().equals(LOGICAL[1]);
    }


    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }


    @Override
    public TypeDenoter results(TypeDenoter left, TypeDenoter right) {
        TypeDenoter.Type type = null;

        if (!isNOT()) {
            if (left.getType() == right.getType() && left.getType() == Type.Boolean)
                type = Type.Boolean;
        } else {
            if (right.getType() == Type.Boolean)
                type = Type.Boolean;
        }

        return TypeDenoter.createTypeDenoter(type);
    }


    @Override
    public Literal evaluate(Literal left, Literal right) {
        Literal literal = null;
        switch (left.getType()) {
            case Boolean:
                switch (right.getType()) {
                    case Boolean:
                        literal = evaluate((BooleanLiteral) left, (BooleanLiteral) right);
                        break;
                }
                break;
            default:
                throw new InterpreterErrorException("Inconsistent operand type.");
        }
        return literal;
    }

    @Override
    public Literal evaluate(Literal literal) {
        Literal result = null;
        switch (literal.getType()) {
            case Boolean:
                result = evaluate((BooleanLiteral) literal);
                break;
            default:
                throw new InterpreterErrorException("Inconsistent operand type.");
        }
        return result;
    }


    @Override
    public LogicalOperator clone() {
        return new LogicalOperator(getText());
    }


}

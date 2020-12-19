/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.operator;

import jall.lang.ast.Visitor;
import jall.lang.ast.literal.DoubleLiteral;
import jall.lang.ast.literal.IntLiteral;
import jall.lang.ast.literal.Literal;
import jall.lang.ast.literal.StringLiteral;
import jall.lang.type.TypeDenoter;
import jall.lang.type.TypeDenoter.Type;


/**
 *
 */
public class ArithmeticOperator extends Operator {

    public ArithmeticOperator(String text) {
        this.setText(text);
    }

    public DoubleLiteral evaluate(DoubleLiteral left, DoubleLiteral right) {
        double result = 0;
        if (getText().equals("+"))
            result = left.getValue() + right.getValue();
        else if (getText().equals("-"))
            result = left.getValue() - right.getValue();
        else if (getText().equals("*"))
            result = left.getValue() * right.getValue();
        else if (getText().equals("/"))
            result = left.getValue() / right.getValue();
        return new DoubleLiteral(result);
    }

    public IntLiteral evaluate(IntLiteral left, IntLiteral right) {
        int result = 0;
        if (getText().equals("+"))
            result = left.getValue() + right.getValue();
        else if (getText().equals("-"))
            result = left.getValue() - right.getValue();
        else if (getText().equals("*"))
            result = left.getValue() * right.getValue();
        else if (getText().equals("/"))
            result = left.getValue() / right.getValue();
        return new IntLiteral(result);
    }

    public DoubleLiteral evaluate(DoubleLiteral left, IntLiteral right) {
        double result = 0;
        if (getText().equals("+"))
            result = left.getValue() + right.getValue();
        else if (getText().equals("-"))
            result = left.getValue() - right.getValue();
        else if (getText().equals("*"))
            result = left.getValue() * right.getValue();
        else if (getText().equals("/"))
            result = left.getValue() / right.getValue();
        return new DoubleLiteral(result);
    }

    public DoubleLiteral evaluate(IntLiteral left, DoubleLiteral right) {
        double result = 0;
        if (getText().equals("+"))
            result = left.getValue() + right.getValue();
        else if (getText().equals("-"))
            result = left.getValue() - right.getValue();
        else if (getText().equals("*"))
            result = left.getValue() * right.getValue();
        else if (getText().equals("/"))
            result = left.getValue() / right.getValue();
        return new DoubleLiteral(result);
    }

    public StringLiteral evaluate(StringLiteral left, DoubleLiteral right) {
        return new StringLiteral(left.getValue() + right.getValue());
    }

    public StringLiteral evaluate(StringLiteral left, IntLiteral right) {
        return new StringLiteral(left.getValue() + right.getValue());
    }

    public StringLiteral evaluate(StringLiteral left, StringLiteral right) {
        return new StringLiteral(left.getValue() + right.getValue());
    }

    public StringLiteral evaluate(DoubleLiteral left, StringLiteral right) {
        return new StringLiteral(left.getValue() + right.getValue());
    }

    public StringLiteral evaluate(IntLiteral left, StringLiteral right) {
        return new StringLiteral(left.getValue() + right.getValue());
    }

    public DoubleLiteral evaluate(DoubleLiteral literal) {
        DoubleLiteral result = null;
        if (getText().equals("+"))
            result = literal;
        else if (getText().equals("-"))
            result = new DoubleLiteral(-literal.getValue());
        return result;
    }

    public IntLiteral evaluate(IntLiteral literal) {
        IntLiteral result = null;
        if (getText().equals("+"))
            result = literal;
        else if (getText().equals("-"))
            result = new IntLiteral(-literal.getValue());
        return result;
    }

    public int evaluate(int right) {
        return -right;
    }

    public double evaluate(double right) {
        return -right;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public TypeDenoter results(TypeDenoter left, TypeDenoter right) {
        TypeDenoter.Type type = null;
        switch (left.getType()) {
            case Integer:
                switch (right.getType()) {
                    case Integer:
                        type = Type.Integer;
                        break;
                    case Double:
                        type = Type.Double;
                        break;
                    case Object:
                        type = Type.Object;
                        break;
                    case String:
                        type = Type.String;
                        break;
                }
                break;
            case Double:
                switch (right.getType()) {
                    case Integer:
                        type = Type.Double;
                        break;
                    case Double:
                        type = Type.Double;
                        break;
                    case Object:
                        type = Type.Object;
                        break;
                    case String:
                        type = Type.String;
                        break;
                }
                break;
            case String:
                switch (right.getType()) {
                    case Integer:
                        type = Type.String;
                        break;
                    case Double:
                        type = Type.String;
                        break;
                    case Object:
                        type = Type.String;
                        break;
                    case String:
                        type = Type.String;
                        break;
                }
                break;
            case Object:
                switch (right.getType()) {
                    case Integer:
                        type = Type.Object;
                        break;
                    case Double:
                        type = Type.Object;
                        break;
                    case Object:
                        type = Type.Object;
                        break;
                    case String:
                        type = Type.String;
                        break;
                }
            default:
        }
        return TypeDenoter.createTypeDenoter(type);
    }

    @Override
    public Literal evaluate(Literal left, Literal right) {
        Literal literal = null;
        switch (left.getType()) {
            case Double:
                switch (right.getType()) {
                    case Double:
                        literal = evaluate((DoubleLiteral) left, (DoubleLiteral) right);
                        break;
                    case Integer:
                        literal = evaluate((DoubleLiteral) left, (IntLiteral) right);
                        break;
                    case String:
                        literal = evaluate((DoubleLiteral) left, (StringLiteral) right);
                        break;
                }
                break;
            case Integer:
                switch (right.getType()) {
                    case Double:
                        literal = evaluate((IntLiteral) left, (DoubleLiteral) right);
                        break;
                    case Integer:
                        literal = evaluate((IntLiteral) left, (IntLiteral) right);
                        break;
                    case String:
                        literal = evaluate((IntLiteral) left, (StringLiteral) right);
                        break;
                }
                break;
            case String:
                switch (right.getType()) {
                    case Double:
                        literal = evaluate((StringLiteral) left, (DoubleLiteral) right);
                        break;
                    case Integer:
                        literal = evaluate((StringLiteral) left, (IntLiteral) right);
                        break;
                    case String:
                        literal = evaluate((StringLiteral) left, (StringLiteral) right);
                        break;
                }
                break;
        }
        return literal;
    }

    @Override
    public Literal evaluate(Literal literal) {
        Literal result = null;
        switch (literal.getType()) {
            case Double:
                result = evaluate((DoubleLiteral) literal);
                break;
            case Integer:
                result = evaluate((IntLiteral) literal);
                break;
        }
        return result;
    }

    @Override
    public ArithmeticOperator clone() {
        return new ArithmeticOperator(getText());
    }


}

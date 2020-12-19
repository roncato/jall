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
import jall.lang.ast.literal.*;
import jall.lang.type.StringTypeDenoter;
import jall.lang.type.TypeDenoter;
import jall.lang.type.TypeDenoter.Type;


/**
 *
 */
public class ComparisonOperator extends Operator {

    public ComparisonOperator(String text) {
        this.setText(text);
    }

    public Literal evaluate(IntLiteral left, IntLiteral right) {
        boolean equals = false;
        if (getText().equals("=="))
            equals = left.getValue() == right.getValue();
        else if (getText().equals("!="))
            equals = left.getValue() != right.getValue();
        else if (getText().equals("<"))
            equals = left.getValue() < right.getValue();
        else if (getText().equals(">"))
            equals = left.getValue() > right.getValue();
        else if (getText().equals(">="))
            equals = left.getValue() >= right.getValue();
        else if (getText().equals("<="))
            equals = left.getValue() <= right.getValue();
        return new BooleanLiteral(equals);
    }

    public Literal evaluate(IntLiteral left, DoubleLiteral right) {
        boolean equals = false;
        if (getText().equals("=="))
            equals = left.getValue() == right.getValue();
        else if (getText().equals("!="))
            equals = left.getValue() != right.getValue();
        else if (getText().equals("<"))
            equals = left.getValue() < right.getValue();
        else if (getText().equals(">"))
            equals = left.getValue() > right.getValue();
        else if (getText().equals(">="))
            equals = left.getValue() >= right.getValue();
        else if (getText().equals("<="))
            equals = left.getValue() <= right.getValue();
        return new BooleanLiteral(equals);
    }

    public Literal evaluate(DoubleLiteral left, IntLiteral right) {
        boolean equals = false;
        if (getText().equals("=="))
            equals = left.getValue() == right.getValue();
        else if (getText().equals("!="))
            equals = left.getValue() != right.getValue();
        else if (getText().equals("<"))
            equals = left.getValue() < right.getValue();
        else if (getText().equals(">"))
            equals = left.getValue() > right.getValue();
        else if (getText().equals(">="))
            equals = left.getValue() >= right.getValue();
        else if (getText().equals("<="))
            equals = left.getValue() <= right.getValue();
        return new BooleanLiteral(equals);
    }

    public Literal evaluate(DoubleLiteral left, DoubleLiteral right) {
        boolean equals = false;
        if (getText().equals("=="))
            equals = left.getValue() == right.getValue();
        else if (getText().equals("!="))
            equals = left.getValue() != right.getValue();
        else if (getText().equals("<"))
            equals = left.getValue() < right.getValue();
        else if (getText().equals(">"))
            equals = left.getValue() > right.getValue();
        else if (getText().equals(">="))
            equals = left.getValue() >= right.getValue();
        else if (getText().equals("<="))
            equals = left.getValue() <= right.getValue();
        return new BooleanLiteral(equals);
    }

    public Literal evaluate(StructLiteral left, StructLiteral right) {
        boolean equals = false;
        if (getText().equals("=="))
            equals = left.equals(right);
        else if (getText().equals("!="))
            equals = !left.equals(right);
        return new BooleanLiteral(equals);
    }

    public Literal evaluate(StructLiteral left, NullLiteral right) {
        boolean equals = false;
        if (getText().equals("=="))
            equals = left == null;
        else if (getText().equals("!="))
            equals = left != null;
        return new BooleanLiteral(equals);
    }

    public Literal evaluate(NullLiteral left, StructLiteral right) {
        boolean equals = false;
        if (getText().equals("=="))
            equals = right == null;
        else if (getText().equals("!="))
            equals = right != null;
        return new BooleanLiteral(equals);
    }

    public Literal evaluate(NullLiteral left, NullLiteral right) {
        boolean equals = false;
        if (getText().equals("=="))
            equals = true;
        else if (getText().equals("!="))
            equals = false;
        return new BooleanLiteral(equals);
    }

    public Literal evaluate(StringLiteral left, StringLiteral right) {
        boolean equals = false;
        if (getText().equals("=="))
            equals = left.getValue().equals(right.getValue());
        else if (getText().equals("!="))
            equals = !left.getValue().equals(right.getValue());
        else if (getText().equals("<"))
            equals = left.getValue().compareTo(right.getValue()) < 0;
        else if (getText().equals(">"))
            equals = left.getValue().compareTo(right.getValue()) > 0;
        else if (getText().equals(">="))
            equals = left.getValue().compareTo(right.getValue()) >= 0;
        else if (getText().equals("<="))
            equals = left.getValue().compareTo(right.getValue()) <= 0;
        return new BooleanLiteral(equals);
    }

    @Override
    public Literal evaluate(Literal literal) {
        return null;
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
                    case Double:
                    case Object:
                        type = Type.Boolean;
                        break;
                }
                break;
            case Double:
                switch (right.getType()) {
                    case Integer:
                    case Double:
                    case Object:
                        type = Type.Boolean;
                        break;
                }
                break;
            case Object:
                type = Type.Boolean;
                break;
            case Struct:
                switch (right.getType()) {
                    case Integer:
                    case Double:
                    case Struct:
                    case Object:
                        type = Type.Boolean;
                        break;
                }
                break;
            case String:
                switch (right.getType()) {
                    case Integer:
                    case Double:
                    case String:
                    case Object:
                        type = Type.Boolean;
                        break;
                }
                break;
        }
        return TypeDenoter.createTypeDenoter(type);
    }

    @Override
    public Literal evaluate(Literal left, Literal right) {
        Literal literal = null;

        if (left == null)
            left = new NullLiteral();

        if (right == null)
            right = new NullLiteral();

        switch (left.getType()) {
            case Double:
                switch (right.getType()) {
                    case Double:
                        literal = evaluate((DoubleLiteral) left, (DoubleLiteral) right);
                        break;
                    case Integer:
                        literal = evaluate((DoubleLiteral) left, (IntLiteral) right);
                        break;
                    case Object:
                        literal = evaluate(left, right.convert(new StringTypeDenoter()));
                        break;
                    case String:
                        literal = evaluate((StringLiteral) left.convert(new StringTypeDenoter()), (StringLiteral) right);
                        break;
                    default:
                        throw new InterpreterErrorException("Inconsistent operand type.");
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
                    case Object:
                        literal = evaluate(left, right.convert(new StringTypeDenoter()));
                        break;
                    case String:
                        literal = evaluate((StringLiteral) left.convert(new StringTypeDenoter()), (StringLiteral) right);
                        break;
                    default:
                        throw new InterpreterErrorException("Inconsistent operand type.");
                }
                break;
            case Struct:
                switch (right.getType()) {
                    case Struct:
                        literal = evaluate((StructLiteral) left, (StructLiteral) right);
                        break;
                    case Null:
                        literal = evaluate((StructLiteral) left, (NullLiteral) right);
                        break;
                    case String:
                        literal = evaluate((StringLiteral) left.convert(new StringTypeDenoter()), (StringLiteral) right);
                        break;
                    case Object:
                        literal = evaluate(left, right.convert(new StringTypeDenoter()));
                        break;
                    default:
                        throw new InterpreterErrorException("Inconsistent operand type.");
                }
                break;
            case Null:
                switch (right.getType()) {
                    case Struct:
                        literal = evaluate((NullLiteral) left, (StructLiteral) right);
                        break;
                    case Null:
                        literal = evaluate((NullLiteral) left, (NullLiteral) right);
                        break;
                    case Object:
                        literal = evaluate(left, right.convert(new StringTypeDenoter()));
                        break;
                    default:
                        throw new InterpreterErrorException("Inconsistent operand type.");
                }
                break;
            case String:
                switch (right.getType()) {
                    case String:
                        literal = evaluate((StringLiteral) left, (StringLiteral) right);
                        break;
                    case Integer:
                    case Double:
                    case Object:
                    case Struct:
                        literal = evaluate((StringLiteral) left, (StringLiteral) right.convert(new StringTypeDenoter()));
                        break;
                    default:
                        throw new InterpreterErrorException("Inconsistent operand type.");
                }
                break;
            default:
                throw new InterpreterErrorException("Inconsistent operand type.");
        }
        return literal;
    }

    @Override
    public ComparisonOperator clone() {
        return new ComparisonOperator(getText());
    }


}

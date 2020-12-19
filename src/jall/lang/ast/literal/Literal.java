/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.ast.literal;

import jall.lang.ast.declaration.*;
import jall.lang.type.TypeDenoter;
import jall.util.Utility;


/**
 *
 */
public abstract class Literal extends jall.lang.ast.Symbol {

    protected Type type = null;

    public static Literal createLiteral(Declaration declaration) {
        Literal literal = null;
        switch (declaration.getType()) {
            case Fact:
                FactDeclaration factDeclaration = (FactDeclaration) declaration;
                literal = new SentenceLiteral(factDeclaration.getFact());
                break;
            case Rule:
                RuleDeclaration ruleDeclaration = (RuleDeclaration) declaration;
                literal = new SentenceLiteral(ruleDeclaration.getRule());
                break;
            case Sentence:
                SentenceDeclaration sentenceDeclaration = (SentenceDeclaration) declaration;
                literal = new SentenceLiteral(sentenceDeclaration.sentence);
                break;
            case Function:
                FunctionDeclaration functionDeclaration = (FunctionDeclaration) declaration;
                literal = new FunctionLiteral(functionDeclaration.getFunction());
                break;
            case ProcedureFunction:
                ProcedureFunctionDeclaration procedureFunctionDeclaration = (ProcedureFunctionDeclaration) declaration;
                literal = new ProcedureFunctionLiteral(procedureFunctionDeclaration.getCommand(), procedureFunctionDeclaration.getArgsDeclarations());
                break;
            case Reference:
                literal = createLiteral(declaration.getTypeDenoter());
                break;
            case Struct:
                literal = StructLiteral.createLiteral((StructDeclaration) declaration);
                break;
            default:
        }
        return literal;
    }

    public static Literal createLiteral(TypeDenoter typeDenoter) {
        Literal literal = null;
        switch (typeDenoter.getType()) {
            case Array:
                literal = new ArrayLiteral();
                break;
            case Boolean:
                literal = new BooleanLiteral(false);
                break;
            case Double:
                literal = new DoubleLiteral(0);
                break;
            case Integer:
                literal = new IntLiteral(0);
                break;
            case Rule:
                break;
            case Fact:
                break;
            case Sentence:
                break;
            case Function:
                break;
            case String:
                literal = new StringLiteral("");
                break;
            case Struct:
                literal = StructLiteral.createLiteral((StructDeclaration) typeDenoter.getIdentifier().getDeclaration());
                break;
            case Object:
                literal = new ObjectLiteral(null);
                break;
            case Void:
                break;
        }
        return literal;
    }

    public static String toJSON(Literal literal) {
        String strLiteral = "";
        if (literal != null && literal.getType() == Literal.Type.Object)
            literal = ((ObjectLiteral) literal).getValue();
        if (literal != null) {
            strLiteral = literal.toString();
            switch (literal.getType()) {
                case String:
                    strLiteral = "\"" + Utility.escapeJSON(strLiteral) + "\"";
                case ProcedureFunction:
                case Struct:
                case Array:
                    break;
                default:
                    strLiteral = Utility.escapeJSON(strLiteral);
            }
        } else
            strLiteral = "\"undefined\"";
        return strLiteral;
    }

    public abstract void setValue(Literal literal);

    @Override
    public abstract Literal clone();

    public Type getType() {
        return type;
    }

    public abstract Literal convert(TypeDenoter typeDenoter);

    @Override
    public abstract String toString();

    public void setValue(Literal literal, IntLiteral[] indices) {
        if (indices.length > 0 && type == Type.Array) {
            ArrayLiteral arrLiteral = (ArrayLiteral) this;
            arrLiteral.setElementValue(literal, indices);
        } else
            setValue(literal);
    }

    public enum Type {
        Boolean,
        Double,
        Integer,
        String,
        Array,
        Struct,
        Object,
        Sentence,
        Void,
        Function,
        ProcedureFunction,
        Null
    }

}

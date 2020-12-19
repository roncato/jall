/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang;

import jall.lang.ast.operator.Operator;


/**
 *
 */
public class Token {

    private final String text;
    private final TokenType type;
    private final SourceFile sourceFile;
    private int startPosition = -1;
    public Token(String text, TokenType type, int startPosition, SourceFile sourceFile) {
        this.text = text;
        this.type = type;
        this.startPosition = startPosition;
        this.sourceFile = sourceFile;
    }
    public Token(String text, TokenType type, int startPosition) {
        this.text = text;
        this.type = type;
        this.startPosition = startPosition;
        this.sourceFile = null;
    }
    public Token(String text, TokenType type) {
        this.text = text;
        this.type = type;
        this.sourceFile = null;
    }

    public static TokenType getTokenType(String text, Lexicon lex) {
        if (text.equals("?"))
            return TokenType.QuestionMark;
        else if (text.equals("("))
            return TokenType.LeftParen;
        else if (text.equals(")"))
            return TokenType.RightParen;
        else if (text.equals("["))
            return TokenType.LeftSquareBracket;
        else if (text.equals("]"))
            return TokenType.RightSquareBracket;
        else if (text.equals("{"))
            return TokenType.LeftCurl;
        else if (text.equals("}"))
            return TokenType.RightCurl;
        else if (text.equals(";"))
            return TokenType.SemiComma;
        else if (text.equals(","))
            return TokenType.Comma;
        else if (text.equals(":"))
            return TokenType.Colon;
        else if (text.equals("."))
            return TokenType.Period;
        else if (text.equals("struct"))
            return TokenType.Struct;
        else if (text.equals("final"))
            return TokenType.Final;
        else if (text.equals("new"))
            return TokenType.New;
        else if (text.equals("function"))
            return TokenType.function;
        else if (text.equals("return"))
            return TokenType.Return;
        else if (text.equals("if"))
            return TokenType.If;
        else if (text.equals("else"))
            return TokenType.Else;
        else if (text.equals("while"))
            return TokenType.While;
        else if (text.equals("for"))
            return TokenType.For;
        else if (text.equals("break"))
            return TokenType.Break;
        else if (text.equals("Rule"))
            return TokenType.Rule;
        else if (text.equals("Fact"))
            return TokenType.Fact;
        else if (text.equals("Function"))
            return TokenType.Function;
        else if (text.equals("Sentence"))
            return TokenType.Sentence;
        else if (text.equals("forall") || text.equals("exists"))
            return TokenType.Quantifier;
        else if (text.equals("="))
            return TokenType.Assign;
        else if (text.equals(":="))
            return TokenType.Becomes;
        else if (Operator.isNegationOperator(text))
            return TokenType.NegationOperator;
        else if (Operator.isOperator(text))
            return TokenType.Operator;
        else if (lex.isInteger(text))
            return TokenType.IntegerLiteral;
        else if (lex.isNumber(text))
            return TokenType.DoubleLiteral;
        else if (lex.isStringLiteral(text))
            return TokenType.StringLiteral;
        else if (lex.isBooleanLiteral(text))
            return TokenType.BooleanLiteral;
        else if (lex.isNullLiteral(text))
            return TokenType.NullLiteral;
        else if (lex.isPreprocessorPrefix(text))
            return TokenType.Preprocessor;
        else
            return TokenType.Identifier;
    }

    public static TokenType getEOTTokenType() {
        return TokenType.EOT;
    }

    public String getText() {
        return text;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public TokenType getType() {
        return type;
    }

    public int getLine() {
        if (sourceFile != null)
            return sourceFile.getLine(startPosition);
        else
            return -1;
    }

    public SourceFile getSourceFile() {
        return sourceFile;
    }

    @Override
    public String toString() {
        String fileText = sourceFile != null ? sourceFile.toString() : "";
        return "{Text: " + text + ", Type: " + type.toString() + ", StartPosition: " + startPosition + ", File: " + fileText + "}";
    }

    @Override
    public Token clone() {
        return new Token(text, type, startPosition, sourceFile);
    }

    public enum TokenType {
        QuestionMark,            // ?
        LeftParen,                // (
        RightParen,                // )
        LeftSquareBracket,        // [
        RightSquareBracket,        // ]
        LeftCurl,                // {
        RightCurl,                // }
        SemiComma,                // ;
        Comma,                    // ,
        Period,                    // .
        Colon,                    // :
        Assign,                    // =
        Becomes,                // :=
        Struct,                    // struct
        New,                    // New
        Void,                    // void
        Return,                    // Return
        function,                // function
        If,                        // if
        Else,                    // else
        While,                    // while
        For,                    // for
        Break,                    // break
        Rule,                    // Rule
        Fact,                    // Fact
        Function,                // Function
        Sentence,                // Sentence
        Final,                    // final
        NegationOperator,        // ! | ~
        Operator,                // <operator>
        IntegerLiteral,            // <integerliteral>
        DoubleLiteral,            // <numberliteral>
        StringLiteral,            // <stringliteral>
        BooleanLiteral,            // <booleanliteral>
        NullLiteral,            // <booleanliteral>
        Quantifier,                // <quantifier>
        Identifier,                // <indentifier>
        Preprocessor,            // <preprocessor>
        EOT                        // <EOT>
    }

}

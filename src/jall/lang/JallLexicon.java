/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang;

public final class JallLexicon extends Lexicon {

    public static final String THIS_IDENTIFIER = "this";

    protected static final String[] TERMINAL_SYMBOLS =
            {
                    "(",
                    ")",
                    "{",
                    "}",
                    ";",
                    ",",
                    ".",
                    "=>",
                    "<=>",
                    "&",
                    "|",
                    "\\/",
                    "/\\",
                    "!",
                    "~",
                    "?",
                    "==",
                    "!=",
                    "<",
                    ">",
                    "<=",
                    ">=",
                    "true",
                    "false",
                    "struct",
                    "Fact",
                    "Rule",
                    "Function",
                    "Sentence",
                    "forall",
                    "exists",
                    "return",
                    "*",
                    "/",
                    "+",
                    "-",
                    ":=",
                    "[",
                    "]",
                    "function",
                    "if",
                    "else",
                    "while",
                    "double",
                    "string",
                    "int",
                    "boolean",
                    "break",
                    "new",
                    "void"
            };

    protected static final String[] PUNCTUATOR_SYMBOLS =
            {
                    "(",
                    ")",
                    "{",
                    "}",
                    ";",
                    ",",
                    ".",
                    "=>",
                    "<=>",
                    "&",
                    "|",
                    "!",
                    "\\/",
                    "/\\",
                    "~",
                    "?",
                    "==",
                    "!=",
                    "<",
                    ">",
                    "<=",
                    ">=",
                    "*",
                    "/",
                    "+",
                    "-",
                    ":=",
                    "[",
                    "]",
                    "="
            };

    public JallLexicon() {
        terminalSymbols = TERMINAL_SYMBOLS;
        punctuator = PUNCTUATOR_SYMBOLS;
    }
}

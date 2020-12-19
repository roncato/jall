/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang;

public abstract class Lexicon {

    protected String[] terminalSymbols = null;
    protected String[] punctuator = null;

    protected String[] booleanLiterals = {"true", "false"};
    protected String[] operatorPrecedence = {"~", "==", "!=", ">", ">=", "<", "<=", "&", "/\\",
            "\\/", "|", "=>", "<=>", "*", "/", "+", "-"};
    protected String multiLineCommentStart = "/*";
    protected String multiLineCommentEnd = "*/";
    protected String lineComment = "//";
    protected String stringDelimiterType1 = "\"";
    protected String stringDelimiterType2 = "'";
    protected String decimalDelimiter = ".";
    protected String preprocessorPrefix = "#";

    public boolean isTerminalSymbol(String text) {
        return jall.util.Arrays.isInArray(text, terminalSymbols);
    }

    public boolean isPartOfInArray(String text) {
        return jall.util.Arrays.isPartOfInArray(text, terminalSymbols, 1);
    }

    public boolean isDelimiter(String text) {
        return jall.util.Arrays.isInArray(text, punctuator) || isWhiteSpace(text);
    }

    public boolean isPartOfDelimiter(String text) {
        return jall.util.Arrays.isPartOfInArray(text, punctuator, 1);
    }

    public boolean isWhiteSpace(String str) {
        return str.matches("\\s+$");
    }

    public boolean isInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isNumber(String text) {
        try {
            Float.parseFloat(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isBooleanLiteral(String text) {
        for (String str : booleanLiterals) {
            if (text.equals(str))
                return true;
        }
        return false;
    }

    public boolean isEndOfLine(String text) {
        return text.equals("\n");
    }

    public boolean isMultiLineCommentStart(String text) {
        return text.equals(multiLineCommentStart);
    }

    public boolean isMultiLineCommentEnd(String text) {
        return text.equals(multiLineCommentEnd);
    }

    public boolean isLineComment(String text) {
        return text.equals(lineComment);
    }

    public boolean isCharacter(String text) {
        return text.matches("[A-Za-z]");
    }

    public boolean isStringDelimiterType1(String text) {
        return text.equals(stringDelimiterType1);
    }

    public boolean isStringDelimiterType2(String text) {
        return text.equals(stringDelimiterType2);
    }

    public String getMultiLineCommentStart() {
        return multiLineCommentStart;
    }

    public String getMultiLineCommentEnd() {
        return multiLineCommentEnd;
    }

    public String getLineComment() {
        return lineComment;
    }

    public boolean isStringLiteral(String text) {
        String start1 = text.substring(0, stringDelimiterType1.length());
        String end1 = text.substring(text.length() - stringDelimiterType1.length());

        String start2 = text.substring(0, stringDelimiterType2.length());
        String end2 = text.substring(text.length() - stringDelimiterType2.length());

        return (start1.equals(stringDelimiterType1) && end1.equals(stringDelimiterType1)) ||
                (start2.equals(stringDelimiterType2) && end2.equals(stringDelimiterType2));
    }

    public boolean isDecimalDelimiter(String txt) {
        return decimalDelimiter.equals(txt);
    }

    public String[] getTerminalSymbols() {
        return terminalSymbols;
    }

    public boolean isTrueBooleanLiteral(String text) {
        return booleanLiterals[0].equals(text);
    }

    public boolean isFalseBooleanLiteral(String text) {
        return booleanLiterals[1].equals(text);
    }

    public boolean isNullLiteral(String text) {
        return text.equals("null");
    }

    public String[] getOperatorPrecedence() {
        return operatorPrecedence;
    }


    public boolean isPreprocessorPrefix(String text) {
        return text.substring(0, preprocessorPrefix.length()).equals(preprocessorPrefix);
    }

    public String getPreprocessorPrefix() {
        return preprocessorPrefix;
    }

    public String getStringFromStringLiteral(String text) {
        String rtnText = "";
        if (isStringDelimiterType1(text.substring(0, stringDelimiterType1.length())))
            rtnText = text.substring(stringDelimiterType1.length(), text.length() - stringDelimiterType1.length());
        else if (isStringDelimiterType2(text.substring(0, stringDelimiterType2.length())))
            rtnText = text.substring(stringDelimiterType2.length(), text.length() - stringDelimiterType2.length());
        return rtnText;
    }

}

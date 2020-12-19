/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang;

import jall.lang.Token.TokenType;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class Scanner {

    private SourceFile sourceFile = null;
    private Lexicon lexicon = null;
    private List<Token> tokens = null;
    private List<String> buffer = null;

    public Scanner(SourceFile sourceFile, Lexicon lexicon) {
        this.sourceFile = sourceFile;
        this.lexicon = lexicon;
        init();
    }

    private void init() {
        buffer = new ArrayList<String>();
        tokens = new ArrayList<Token>();
    }

    /**
     * Scans source file and breaks it into tokens.
     */
    public void scan() {

        String buffered = null;
        String current = null;

        while (true) {

            // Consumes and checks for end of file
            consume();
            if (sourceFile.isEOT())
                break;

            // Gets buffered content and current character
            buffered = buffered();
            current = sourceFile.current();

            // Consumes look ahead for delimiter such as operator with more than one character.
            if (lexicon.isPartOfDelimiter(buffered)) {
                while (!lexicon.isWhiteSpace(current) && lexicon.isPartOfDelimiter(current)) {
                    consume();
                    buffered = buffered();
                    current = sourceFile.current();
                    if (sourceFile.isEOT() || current == null)
                        break;
                }
                tokens.add(createToken(buffered));
                flush();
            }
            // String delimiters.
            else if (lexicon.isStringDelimiterType1(buffered)) {
                while (!lexicon.isStringDelimiterType1(current)) {
                    buffer.add(current = sourceFile.next());
                    buffered = buffered();
                    current = sourceFile.current();
                    if (sourceFile.isEOT() || current == null)
                        break;
                }
                buffer.add(current = sourceFile.next());
                buffered = buffered();
                tokens.add(createToken(buffered));
                flush();
            } else if ((lexicon.isDecimalDelimiter(current) || lexicon.isDecimalDelimiter(buffered)) && ((lexicon.isNumber(current) || lexicon.isNumber(buffered))))
                continue;
            else if (lexicon.isDelimiter(buffered) && !lexicon.isPartOfDelimiter(buffered)) {
                tokens.add(createToken(buffered));
                flush();
            } else if (lexicon.isStringDelimiterType2(buffered)) {
                while (!lexicon.isStringDelimiterType2(current)) {
                    buffer.add(current = sourceFile.next());
                    buffered = buffered();
                    current = sourceFile.current();
                    if (sourceFile.isEOT() || current == null)
                        break;
                }
                buffer.add(current = sourceFile.next());
                buffered = buffered();
                tokens.add(createToken(buffered));
                flush();
            } else if (lexicon.isDelimiter(current) && !lexicon.isPartOfDelimiter(current)) {
                tokens.add(createToken(buffered));
                flush();
            } else if (!lexicon.isDelimiter(buffered) && (lexicon.isPartOfDelimiter(current) || lexicon.isDelimiter(current))) {
                tokens.add(createToken(buffered));
                flush();
            }
        }
        // Consumes left over and get buffered content
        consume();
        buffered = buffered();

        if (buffered.length() > 0)
            tokens.add(new Token(buffered, Token.getTokenType(buffered, lexicon), sourceFile.getCurrentPosition() - buffered.length()));

        TokenType type = Token.getEOTTokenType();
        tokens.add(new Token(type.toString(), type, sourceFile.getCurrentPosition()));
    }

    /**
     * Consumes a character from the source file and adds to the buffer.
     * White spaces and comments are consumed but not buffered.
     */
    private void consume() {

        String current = null;
        String next = null;
        String ahead = null;

        // Gets next character
        current = sourceFile.next();

        // Consumes white spaces
        current = consumeWhiteSpace(current);
        if (current == null)
            return;

        // Consumes comments
        ahead = sourceFile.peek(lexicon.getMultiLineCommentStart().length() - 2);
        next = current + ahead;
        while (ahead != null && (lexicon.isMultiLineCommentStart(next) || lexicon.isLineComment(next))) {

            // Consumes multi-line comments
            current = consumeMultiLineComments(current);
            if (current == null)
                return;

            // Consumes white spaces
            current = consumeWhiteSpace(current);
            if (current == null)
                return;

            // Consumes line comments
            current = consumeLineComments(current);
            if (current == null)
                return;

            // Consumes white spaces
            current = consumeWhiteSpace(current);
            if (current == null)
                return;

            ahead = sourceFile.peek(lexicon.getMultiLineCommentStart().length() - 2);
            next = current + ahead;

        }

        // Checks white spaces
        current = consumeWhiteSpace(current);
        if (current == null)
            return;

        // Adds to buffer
        buffer.add(current);

    }

    private Token createToken(String buffered) {
        return new Token(buffered, Token.getTokenType(buffered, lexicon), sourceFile.getCurrentPosition() - buffered.length(), sourceFile);
    }

    private String consumeMultiLineComments(String current) {
        String next = null;
        String ahead = null;

        if (current == null)
            return null;

        ahead = sourceFile.peek(lexicon.getMultiLineCommentStart().length() - 2);
        next = current + ahead;

        if (ahead != null && lexicon.isMultiLineCommentStart(next)) {

            for (int i = 0; i < lexicon.getMultiLineCommentEnd().length(); i++)
                current = sourceFile.next();

            while (true) {

                ahead = sourceFile.peek(lexicon.getMultiLineCommentStart().length() - 2);
                next = current + ahead;

                // Check end of text
                if (ahead == null)
                    break;

                // Check end of text
                if (lexicon.isMultiLineCommentEnd(next)) {
                    for (int i = 0; i < lexicon.getMultiLineCommentEnd().length(); i++)
                        current = sourceFile.next();
                    break;
                }
                current = sourceFile.next();
            }
        }
        return current;
    }

    private String consumeLineComments(String current) {
        String next = null;
        String ahead = null;

        if (current == null)
            return null;

        ahead = sourceFile.peek(lexicon.getLineComment().length() - 2);
        next = current + ahead;

        if (ahead != null && lexicon.isLineComment(next)) {
            while (true) {

                current = sourceFile.next();

                // Check end of text
                if (current == null)
                    break;

                if (lexicon.isEndOfLine(current)) {
                    current = sourceFile.next();
                    break;
                }

            }
        }
        return current;
    }

    private String consumeWhiteSpace(String current) {

        if (current == null)
            return null;

        // Checks white spaces
        if (lexicon.isWhiteSpace(current)) {
            while (true) {
                current = sourceFile.next();
                if (current == null || !lexicon.isWhiteSpace(current))
                    break;
            }
        }
        return current;
    }

    private String buffered() {
        String str = "";
        for (String s : buffer)
            str += s;
        return str;
    }

    private String flush() {
        String str = lookBufferedString();
        buffer.clear();
        return str;
    }

    private String lookBufferedString() {
        String str = "";
        for (String s : buffer) {
            str += s;
        }
        return str;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public SourceFile getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(SourceFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    public int getTokenLine(Token token) {
        return sourceFile.getLine(token.getStartPosition());
    }

    public Lexicon getLexicon() {
        return lexicon;
    }

}

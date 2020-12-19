/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Provides interface for error reporting
 */
public class ErrorReporter {

    private PrintStream out = System.err;

    private List<Error> errors = null;
    private SourceFile sourceFile = null;
    public ErrorReporter(SourceFile sf) {
        errors = new ArrayList<Error>();
        this.sourceFile = sf;
    }

    public ErrorReporter(PrintStream out) {
        errors = new ArrayList<Error>();
        this.out = out;
    }

    public void log(Error err) {
        errors.add(err);
    }

    public void println(Error err) {
        out.println(err.toString());
    }

    public void printAll() {
        for (Error err : errors) {
            out.println(err.toString());
        }
    }

    public List<Error> getErrors() {
        return errors;
    }

    public int getTokenLine(Token token) {
        return sourceFile.getLine(token.getStartPosition());
    }

    public enum ErrorType {Exception, Parsing, Contextual, Unknown}

    public static final class Error {

        private String message = null;
        private ErrorType type = null;
        private Exception ex = null;

        public Error(String text, ErrorType type) {
            this.message = text;
            this.type = type;
        }

        public Error(String text, ErrorType type, Exception ex) {
            this(text, type);
            this.ex = ex;
        }

        public String getText() {
            return message;
        }

        public ErrorType getType() {
            return type;
        }

        public Object getException() {
            return ex;
        }

        public String toString() {
            return type.toString() + " error: " + message;
        }

    }

}

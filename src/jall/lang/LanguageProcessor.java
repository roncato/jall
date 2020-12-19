/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang;

import jall.Jall;
import jall.lang.ast.AbstractSyntaxTree;
import jall.lang.ast.Program;
import jall.lang.contextual.ContextualAnalyser;
import jall.lang.interpreter.Interpreter;

import java.io.IOException;
import java.util.Date;


/**
 *
 */
public class LanguageProcessor implements Runnable {

    private Program program = null;
    private Parser parser = null;
    private ErrorReporter err = null;
    private ContextualAnalyser analyser = null;
    private Interpreter interpreter = null;
    private SourceFile sourceFile = null;
    private PreProcessor preProcessor = null;


    public LanguageProcessor(SourceFile file) {
        preProcessor = new PreProcessor(sourceFile = file, new JallLexicon());
    }

    @Override
    public void run() {
        // Gets times
        long[] times = new long[3];
        Date dt1 = new Date();

        // Parses
        parser = new Parser(preProcessor, err = new ErrorReporter(sourceFile));
        try {
            program = parser.parse();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        err.printAll();

        // Calculate parser time
        Date dt2 = new Date();
        long diff = dt2.getTime() - dt1.getTime();
        times[0] = diff;
        dt1 = new Date();

        // Analyzes
        analyser = new ContextualAnalyser(program, err);
        analyser.analyse();
        err.printAll();

        // Calculate parser analyser time
        dt2 = new Date();
        diff = dt2.getTime() - dt1.getTime();
        times[1] = diff;
        dt1 = new Date();

        // Interpreters
        interpreter = new Interpreter(program, err);
        interpreter.run();
        err.printAll();

        // Calculates interpreter/execution time
        dt2 = new Date();
        diff = dt2.getTime() - dt1.getTime();
        times[2] = diff;
        dt1 = new Date();

        if (Jall.getGlobal("DebugInfo").equals("1")) {
            String text = "Parser time [ms]: " + times[0];
            text += "\nAnalyser time [ms]: " + times[1];
            text += "\nExecution time [ms]: " + times[2];
            text += "\nTotal time [ms]: " + (times[0] + times[1] + times[2]);
            System.out.println(text);
        }
    }

    public ErrorReporter getErrorReporter() {
        return err;
    }

    public AbstractSyntaxTree getAst() {
        return program;
    }


}

/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall;

import jall.lang.LanguageProcessor;
import jall.lang.SourceFile;
import jall.lang.SourceFile.SourceOption;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Entry point for Jall language
 */
public class Jall {

    private static LanguageProcessor languageProcessor = null;
    private static Map<String, String> args = null;
    private static String[] aArgs = null;
    private static Map<String, String> globals = null;

    /**
     *
     */
    public static void main(String[] args) {
        aArgs = args;
        try {
            init();
            run();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void init() throws Exception {
        initArgs();
        initGlobals();
    }

    private static void initGlobals() {
        globals = new TreeMap<String, String>();
        globals.put("DebugInfo", "1");
    }

    private static void run() throws Exception {
        runMethod(args);
    }

    private static void initArgs() {
        args = new HashMap<String, String>();
        for (int i = 0; i < aArgs.length; i++) {
            String[] arg = aArgs[i].split("=");
            if (arg.length > 1)
                args.put(arg[0], arg[1]);
            else
                args.put(arg[0], null);
        }
    }

    private static void runMethod(Map<String, String> args) throws Exception {
        if (!args.containsKey("method"))
            throw new InvalidParameterException("A run method was not specified.");


        String method = args.get("method");

        if (method.equals(RunMethods.FILE)) {
            runFile(args);
        }

    }

    private static void runFile(Map<String, String> args) throws IOException {
        String fileName = args.get("filename");
        SourceFile sf = new SourceFile(fileName, SourceOption.File);
        (languageProcessor = new LanguageProcessor(sf)).run();
    }

    public static LanguageProcessor getLanguageProcessor() {
        return languageProcessor;
    }

    public static String getGlobal(String globalName) {
        return globals.get(globalName);
    }

    public static String setGlobal(String globalName, String globalValue) {
        return globals.put(globalName, globalValue);
    }

    private static class RunMethods {
        public static final String FILE = "file";
    }

}

/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.util;

import jall.lang.Token;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Utility {
    public static String escapeJSON(String str) {
        str = str.replace("\"", "\\\"");
        return str;
    }

    public static void writeToFile(String fileFullName, String text) {
        File file = new File(fileFullName);
        try {
            FileWriter fOut = new FileWriter(file);
            fOut.write(text);
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void writeToFile(String fileFullName, List<Token> tokens) {
        String text = "";
        for (int i = 0; i < tokens.size(); i++)
            text += tokens.get(i).getText();
        writeToFile(fileFullName, text);
    }

    public static void removeFromList(List<?> list, int start, int end) {
        int i = start;
        while (i <= end) {
            list.remove(start);
            i++;
        }
    }

}

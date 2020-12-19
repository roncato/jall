/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang;

import java.io.*;
import java.security.InvalidParameterException;


/**
 * Provides interface for file source operation.
 *
 * @author Lucas Batista
 */
public class SourceFile {

    private File file = null;

    private String source = null;
    private int currPosition = 0;
    public SourceFile(String arg, SourceOption option) throws IOException {
        switch (option) {
            case File:
                file = new File(arg);
                setSourceFile(file);
                break;
            case Source:
                source = arg;
                break;
        }

    }

    private void setSourceFile(File file) throws IOException {
        source = "";
        String line = null;

        FileInputStream fs = new FileInputStream(file);
        BufferedReader fIn = new BufferedReader(new InputStreamReader(fs));

        while ((line = fIn.readLine()) != null)
            source += line + "\n";

        fIn.close();
    }

    public void rewind() {
        currPosition = 0;
    }

    public String next() {
        if (currPosition + 1 < source.length())
            return source.charAt(currPosition++) + "";
        else
            return null;
    }

    public String current() {
        return source.charAt(currPosition) + "";
    }

    public String previous() {
        if (currPosition - 1 >= 0)
            return source.charAt(currPosition--) + "";
        else
            return null;
    }

    public String peek(int offSet) {
        String str = "";
        if (currPosition + offSet < source.length()) {
            for (int i = 0; i <= offSet; i++)
                str += source.charAt(currPosition + i) + "";
        }
        return str;
    }

    public String lookAt(int position) {
        if (position < source.length() && position >= 0)
            return source.charAt(Math.max(source.length() - 1, position)) + "";
        else
            return null;
    }

    public void jumpTo(int position) {
        if (position < source.length() && position >= 0)
            currPosition = position;
        else
            throw new InvalidParameterException("Inexistent position: " + position + ".");
    }

    public int getCurrentPosition() {
        return currPosition;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isEOT() {
        return currPosition >= source.length() - 1;
    }

    public String[] getLines() {
        return source.split("\n");
    }

    public int getLine(int startPosition) {
        int line = 1;
        for (int i = 0; i < source.length(); i++) {
            char ch = source.charAt(i);
            if (i == startPosition)
                return line;
            if (ch == '\n')
                line++;

        }
        return -1;
    }

    @Override
    public String toString() {
        return file.toString();
    }

    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (o instanceof SourceFile) {
            SourceFile that = (SourceFile) o;
            equals = file.toString().equals(that.toString());
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return file.toString().hashCode();
    }

    // Enumerators
    public enum SourceOption {File, Source}

}

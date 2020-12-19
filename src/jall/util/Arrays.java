/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.util;

public final class Arrays {
    public static final boolean isInArray(String text, String[] array) {
        for (String str : array) {
            if (text.equals(str))
                return true;
        }
        return false;
    }

    public static final boolean isPartOfInArray(String text, String[] array, int threshold) {
        for (String str : array) {
            if (str.substring(0, Math.min(text.length(), str.length())).equals(text) && str.length() > threshold)
                return true;
        }
        return false;
    }

}

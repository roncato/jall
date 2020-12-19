/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.util;


import java.util.List;

public class ListIterator<T> {

    private List<T> list = null;
    private int currIndex = 0;

    public ListIterator(List<T> list) {
        this.list = list;
    }

    public T next() {
        if (currIndex < list.size())
            return list.get(currIndex++);
        else
            return null;
    }

    public T current() {
        return list.get(currIndex);
    }

    public void rewind() {
        currIndex = 0;
    }

    public boolean hasNext() {
        return currIndex < list.size();
    }

    public T previous() {
        if (currIndex > 0)
            return list.get(currIndex--);
        else
            return null;
    }

    public T getBack(int offset) {
        currIndex -= offset;
        if (currIndex >= 0)
            return list.get(currIndex);
        else
            return null;
    }

    public T goFoward(int offset) {
        currIndex += offset;
        if (currIndex < list.size())
            return list.get(currIndex);
        else
            return null;
    }

    public T peek(int offset) {
        if ((currIndex + offset) < list.size() && (currIndex + offset) >= 0)
            return list.get(currIndex + offset);
        else
            return null;
    }

    @Override
    public String toString() {
        return list.toString();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

}

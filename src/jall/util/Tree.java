/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.util;

import java.util.ArrayList;
import java.util.List;

public class Tree<T> {
    private List<Tree<T>> children = null;
    private T object = null;

    public Tree(T object) {
        this.object = object;
        children = new ArrayList<Tree<T>>();
    }

    public final void add(T object) {
        addChild(new Tree<T>(object));
    }

    private final void addChild(Tree<T> child) {
        children.add(child);
    }

    public final void remove(T object) {
        Tree<T> node = getChild(object);
        children.remove(node);
    }

    public final Tree<T> getChild(T object) {
        for (Tree<T> node : children) {
            if (this.object.equals(object))
                return node;
        }
        return null;
    }

    public final List<Tree<T>> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return children.isEmpty();
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

}

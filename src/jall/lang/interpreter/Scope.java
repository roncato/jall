/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.interpreter;

import jall.exceptions.IdentifierAlreadyInScope;
import jall.exceptions.IdentifierNotInScope;
import jall.lang.ast.Identifier;
import jall.lang.interpreter.entities.Entity;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Scope {

    private Map<Identifier, Entity> map = null;
    private boolean locked = false;

    public Scope() {
        map = new TreeMap<Identifier, Entity>();
    }

    public Scope(boolean locked) {
        map = new TreeMap<Identifier, Entity>();
        this.locked = locked;
    }

    public void enter(Identifier id, Entity entity) throws IdentifierAlreadyInScope {
        if (contains(id))
            throw new IdentifierAlreadyInScope("Identifier '" + id + "' is alredy in the memory.");
        map.put(id, entity);
    }

    public Entity retrieve(Identifier id) throws IdentifierNotInScope {
        return map.get(id);

    }

    public Entity remove(Identifier id) {
        return map.remove(id);
    }

    public boolean contains(Identifier id) {
        return map.containsKey(id);
    }

    public void clear() {
        map.clear();
    }

    @Override
    public String toString() {
        return map.toString();
    }

    @Override
    public Scope clone() {
        Scope clone = new Scope();
        for (Identifier identifier : map.keySet())
            clone.map.put(identifier, map.get(identifier));
        return clone;
    }

    public Set<Identifier> getIdentifiers() {
        return map.keySet();
    }

    public int size() {
        return map.size();
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

}

/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.contextual;

import jall.exceptions.IdentifierAlreadyDeclaredException;
import jall.exceptions.IdentifierNotDeclaredException;
import jall.lang.ast.Identifier;

import java.util.HashMap;
import java.util.Map;

public class Scope {

    private Map<Identifier, Attribute> map = null;

    public Scope() {
        map = new HashMap<Identifier, Attribute>();
    }

    public void enter(Identifier id, Attribute attr) throws IdentifierAlreadyDeclaredException {
        if (contains(id))
            throw new IdentifierAlreadyDeclaredException("Identifier '" + id + "' has already been declared.");
        map.put(id, attr);
    }

    public Attribute retrieve(Identifier id) throws IdentifierNotDeclaredException {
        if (!contains(id))
            throw new IdentifierNotDeclaredException("Identifier '" + id + "' not declared.");
        return map.get(id);
    }

    public Attribute remove(Identifier id) {
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

}

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

import java.util.ArrayList;
import java.util.List;

public class IdentificationTable {

    private List<Scope> scopes = null;

    public IdentificationTable() {
        scopes = new ArrayList<Scope>();
    }

    public void enter(Identifier id, Attribute attr) throws IdentifierAlreadyDeclaredException {
        Scope scope = peek();

        if (scope != null)
            scope.enter(id, attr);
    }

    public Attribute retrieve(Identifier id) throws IdentifierNotDeclaredException {
        Attribute attr = null;

        for (int i = scopes.size() - 1; i >= 0; i--) {
            Scope scope = scopes.get(i);
            if (scope.contains(id)) {
                attr = scope.retrieve(id);
                break;
            }
        }

        if (attr == null)
            throw new IdentifierNotDeclaredException("Identifier '" + id + "' not declared.");

        return attr;
    }

    public void openScope() {
        scopes.add(new Scope());
    }

    public void closeScope() {
        pop();
    }

    private Scope pop() {
        Scope scope = null;
        if (scopes.size() > 0)
            scope = scopes.remove(scopes.size() - 1);
        return scope;
    }

    private Scope peek() {
        Scope scope = null;
        if (scopes.size() > 0)
            scope = scopes.get(scopes.size() - 1);
        return scope;
    }

}

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

import java.util.ArrayList;
import java.util.List;

public class Memory {
    private List<Scope> scopes = null;
    private Scope global = null;

    public Memory() {
        scopes = new ArrayList<Scope>();
        global = new Scope();
    }

    public Entity store(Identifier id, Entity entity) throws IdentifierAlreadyInScope {

        Scope scope = null;

        if (scopes.size() == 0)
            scope = global;
        else
            scope = peek();

        if (scope != null)
            scope.enter(id, entity);

        return entity;
    }

    public Entity fetch(Identifier id) throws IdentifierNotInScope {
        Entity entity = null;

        // Tries every unlocked scope
        for (int i = scopes.size() - 1; i >= 0; i--) {
            Scope scope = scopes.get(i);
            entity = scope.retrieve(id);
            if (scope.isLocked())
                break;
            if (entity != null)
                break;
        }

        // Tries global
        if (entity == null)
            entity = global.retrieve(id);

        if (entity == null)
            throw new IdentifierNotInScope("Identifier '" + id + "' not in memory.");

        return entity;
    }

    public void openScope(boolean lock) {
        Scope scope = new Scope(lock);
        scopes.add(scope);
    }

    public void openScope(Scope scope) {
        scopes.add(scope);
    }

    public void closeScope() {
        pop();
    }

    public int openScopes() {
        return scopes.size();
    }

    public void insertScope(Scope scope, int index) {
        scopes.add(index, scope);
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

    @Override
    public String toString() {
        String txt = "";
        for (Scope scope : scopes)
            txt += "{" + scope.toString() + "}, ";
        if (txt.length() > 0)
            txt = txt.substring(0, txt.length() - 1);

        return txt;
    }

    public Scope getGlobal() {
        return global;
    }

}

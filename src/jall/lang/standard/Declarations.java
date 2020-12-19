/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.standard;

import jall.lang.Token;
import jall.lang.Token.TokenType;
import jall.lang.ast.Identifier;
import jall.lang.ast.command.StandardProcedureCommand;
import jall.lang.ast.declaration.Declaration;
import jall.lang.ast.declaration.ProcedureFunctionDeclaration;
import jall.lang.ast.declaration.ReferenceDeclaration;
import jall.lang.ast.declaration.TypeDeclaration;
import jall.lang.type.ArrayTypeDenoter;
import jall.lang.type.TypeDenoter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class Declarations {

    static final short DUMMY_TOKEN_LOCATION = -1;

    static Declaration[] getDeclarations() throws ClassNotFoundException {
        List<Declaration> declarations = new ArrayList<Declaration>();
        declarations.addAll(getStandardProcedures());

        Declaration[] aDeclarations = new Declaration[declarations.size()];
        return declarations.toArray(aDeclarations);
    }

    private static Collection<Declaration> getStandardProcedures() throws ClassNotFoundException {

        List<Declaration> declarations = new ArrayList<Declaration>();

        declarations.addAll(getProcedures("Utility"));
        declarations.addAll(getProcedures("Array"));
        declarations.addAll(getProcedures("Math"));
        declarations.addAll(getProcedures("IO"));
        declarations.addAll(getProcedures("FOL"));
        declarations.addAll(Special.getSpecialDeclarations());
        declarations.addAll(getTypes());

        return declarations;
    }

    private static Collection<Declaration> getProcedures(String nameSpace) throws ClassNotFoundException {

        List<Declaration> declarations = new ArrayList<Declaration>();
        ProcedureFunctionDeclaration declaration = null;
        TypeDenoter typeDenoter = null;
        Identifier identifier = null;
        ReferenceDeclaration[] argsDeclarations = null;
        StandardProcedureCommand command = null;
        Class<?>[] parameterTypes = null;

        // Gets IO class and methods
        Class<?> classe = Class.forName("jall.lang.standard." + nameSpace);
        Method[] methods = classe.getMethods();

        for (Method method : methods) {

            Class<?> declaringClass = method.getDeclaringClass();

            if (!declaringClass.getSimpleName().equals(nameSpace))
                continue;

            typeDenoter = TypeDenoter.createTypeDenoterFromJavaType(method.getReturnType().getName(), method.getReturnType().isArray() ? 1 : 0);
            identifier = getMethodIdentifier(method);

            if (method.getReturnType().isArray())
                ((ArrayTypeDenoter) typeDenoter).setDimensions(1);


            parameterTypes = method.getParameterTypes();
            argsDeclarations = new ReferenceDeclaration[parameterTypes.length];

            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                TypeDenoter argTypeDenoter = TypeDenoter.createTypeDenoterFromJavaType(parameterType.getSimpleName(), parameterType.isArray() ? 1 : 0);
                argsDeclarations[i] = new ReferenceDeclaration(argTypeDenoter, new Identifier("arg" + i));
            }

            command = new StandardProcedureCommand(nameSpace);
            declaration = new ProcedureFunctionDeclaration(typeDenoter, identifier, argsDeclarations, command, false);
            declaration.setStandard(true);
            command.setDeclaration(declaration);
            declarations.add(declaration);

        }

        // print
        return declarations;
    }

    private static Collection<Declaration> getTypes() {

        String[] types = {"boolean", "double", "int", "string", "object", "void", "Fact", "Rule", "Sentence"};

        List<Declaration> declarations = new ArrayList<Declaration>();

        TypeDeclaration declaration = null;
        TypeDenoter typeDenoter = null;
        Identifier identifier = null;

        for (String typeName : types) {
            identifier = new Identifier(new Token(typeName, TokenType.Identifier, -1));
            typeDenoter = TypeDenoter.createTypeDenoter(identifier, 0);
            declaration = new TypeDeclaration(typeDenoter);
            declarations.add(declaration);
        }

        return declarations;

    }

    private static Identifier getMethodIdentifier(Method method) {
        String methodSignature = method.getName();
        return new Identifier(methodSignature);
    }

}

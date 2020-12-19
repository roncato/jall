/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.standard;

import jall.lang.ast.Identifier;
import jall.lang.ast.command.StandardProcedureCommand;
import jall.lang.ast.declaration.Declaration;
import jall.lang.ast.literal.Literal;
import jall.lang.ast.literal.ObjectLiteral;
import jall.lang.interpreter.Interpreter;
import jall.lang.type.TypeDenoter;

import java.lang.reflect.Method;

public class Environment {

    public static Declaration[] getDeclarations() throws ClassNotFoundException {
        return Declarations.getDeclarations();
    }

    public static void execute(StandardProcedureCommand command, Interpreter.Machine machine, ObjectLiteral returnRegister) {

        Literal result = null;
        String nameSpace = command.getNameSpace();
        Declaration[] argsDeclarations = command.getDeclaration().getArgsDeclarations();
        Class<?>[] parameterTypes = new Class<?>[argsDeclarations.length];
        Object[] literals = new Literal[argsDeclarations.length];

        // Parameters
        for (int i = 0; i < argsDeclarations.length; i++) {
            Literal literal = machine.getMemory().fetch(argsDeclarations[i].getIdentifier()).getLiteral();
            if (literal != null && literal.getType() == Literal.Type.Object) {
                ObjectLiteral obj = (ObjectLiteral) literal;
                literal = obj.getValue();
            }
            literals[i] = literal;
            parameterTypes[i] = literals[i].getClass();
        }

        // Checks if it is a special procedure
        if (isSpecialProcedure(command))
            Special.invoke(command, machine, returnRegister, (Literal[]) literals);
        else {
            try {

                // Gets class by name space
                Class<?> classe = Class.forName("jall.lang.standard." + nameSpace);

                // Gets method
                Method method = null;

                try {
                    method = classe.getMethod(command.getDeclaration().getIdentifier().getText(), parameterTypes);
                } catch (NoSuchMethodException e) {
                    method = getMethod(classe, command.getDeclaration().getIdentifier(), parameterTypes);
                    convert(method, (Literal[]) literals);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }

                try {

                    // Instantiates object
                    Object obj = classe.newInstance();

                    // Invokes method
                    result = (Literal) method.invoke(obj, literals);

                    // Stores return value
                    returnRegister.setValue(result);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private static boolean isSpecialProcedure(StandardProcedureCommand command) {
        return command.getNameSpace().equals("Special");
    }

    private static Method getMethod(Class<?> classe, Identifier identifier, Class<?>[] jallParameters) {
        Method method = null;
        Method[] methods = classe.getMethods();

        for (Method methodi : methods) {
            Class<?>[] javaParameters = methodi.getParameterTypes();
            if (methodi.getName().equals(identifier.getText()) && javaParameters.length == jallParameters.length) {
                boolean equals = true;
                for (int i = 0; i < javaParameters.length; i++) {
                    Class<?> jallParameter = jallParameters[i];
                    Class<?> javaParameter = javaParameters[i];
                    TypeDenoter jallTypeDenoter = TypeDenoter.createTypeDenoterFromJavaType(jallParameter.getSimpleName(), jallParameter.isArray() ? 1 : 0);
                    TypeDenoter javaTypeDenoter = TypeDenoter.createTypeDenoterFromJavaType(javaParameter.getSimpleName(), javaParameter.isArray() ? 1 : 0);
                    equals &= TypeDenoter.equivalent(javaTypeDenoter, jallTypeDenoter);
                }
                if (equals) {
                    method = methodi;
                    break;
                }
            }
        }

        return method;
    }

    private static void convert(Method method, Literal[] literals) {

        Class<?>[] javaParameters = method.getParameterTypes();
        for (int i = 0; i < javaParameters.length; i++) {
            Class<?> javaParameter = javaParameters[i];
            TypeDenoter javaTypeDenoter = TypeDenoter.createTypeDenoterFromJavaType(javaParameter.getSimpleName(), javaParameter.isArray() ? 1 : 0);
            literals[i] = literals[i].convert(javaTypeDenoter);
        }
    }

}

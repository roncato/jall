/*
 * Copyright (C) 2013 Lucas Batista.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package jall.lang.type;

import jall.lang.ast.Identifier;
import jall.lang.ast.Symbol;

public abstract class TypeDenoter extends Symbol {

    protected Identifier identifier = null;
    protected Type type = null;

    public static boolean equivalent(TypeDenoter type1, TypeDenoter type2) {
        boolean equivalent = false;
        if (type1.type == Type.Array || type2.type == Type.Array)
            equivalent = ArrayTypeDenoter.equivalent(type1, type2);
        else
            equivalent = equivalent(type1.getType(), type2.getType());
        return equivalent;
    }

    public static boolean equivalent(Type type1, Type type2) {
        boolean equals = false;

        switch (type1) {
            case Integer:
                switch (type2) {
                    case Integer:
                    case Object:
                        equals = true;
                }
                break;
            case Double:
                switch (type2) {
                    case Integer:
                    case Double:
                    case Object:
                        equals = true;
                }
                break;
            case Object:
                equals = true;
                break;
            case String:
                switch (type2) {
                    case Integer:
                    case Double:
                    case String:
                    case Object:
                        equals = true;
                }
                break;
            default:
                if (type2 != Type.Object)
                    equals = type1.equals(type2);
                else
                    equals = true;
        }
        return equals;
    }

    public static TypeDenoter createTypeDenoter(Identifier identifier, int dimensions) {
        TypeDenoter typeDenoter = createTypeDenoter(identifier.getText(), dimensions);
        if (dimensions > 0)
            ((ArrayTypeDenoter) typeDenoter).setElementTypeDenoter(createTypeDenoter(identifier, 0));
        typeDenoter.identifier = identifier;
        return typeDenoter;
    }

    public static TypeDenoter createTypeDenoterFromJavaType(String name, int dimensions) {
        name = factorNameforJavaType(name);
        TypeDenoter typeDenoter = createTypeDenoter(name, dimensions);
        return typeDenoter;
    }

    public static TypeDenoter createTypeDenoter(String typeName, int dimensions) {
        //Identifier identifier = new Identifier(typeName);
        TypeDenoter typeDenoter = null;
        if (dimensions > 0) {
            typeDenoter = new ArrayTypeDenoter();
            ((ArrayTypeDenoter) typeDenoter).setElementTypeDenoter(createTypeDenoter(typeName, 0));
            ((ArrayTypeDenoter) typeDenoter).setDimensions(dimensions);
        } else if (typeName.equals("boolean"))
            typeDenoter = new BooleanTypeDenoter();
        else if (typeName.equals("double"))
            typeDenoter = new DoubleTypeDenoter();
        else if (typeName.equals("object"))
            typeDenoter = new ObjectTypeDenoter();
        else if (typeName.equals("string"))
            typeDenoter = new StringTypeDenoter();
        else if (typeName.equals("void"))
            typeDenoter = new VoidTypeDenoter();
        else if (typeName.equals("int"))
            typeDenoter = new IntTypeDenoter();
        else if (typeName.equals("array")) {
            typeDenoter = new ArrayTypeDenoter();
            ((ArrayTypeDenoter) typeDenoter).setElementTypeDenoter(new ObjectTypeDenoter());
        } else if (typeName.equals("Fact"))
            typeDenoter = new FactTypeDenoter();
        else if (typeName.equals("Function"))
            typeDenoter = new SentenceTypeDenoter();
        else if (typeName.equals("Rule"))
            typeDenoter = new RuleTypeDenoter();
        else if (typeName.equals("Sentence"))
            typeDenoter = new SentenceTypeDenoter();
        else if (typeName.equals("procedurefunction"))
            typeDenoter = new ObjectTypeDenoter();
        else
            typeDenoter = new StructTypeDenoter();
		/*if (!typeName.equals("struct") && !typeName.equals("array"))
			typeDenoter.identifier = identifier;*/
        return typeDenoter;
    }

    public static TypeDenoter createTypeDenoter(Type type) {
        TypeDenoter typeDenoter = null;

        switch (type) {
            case Array:
                typeDenoter = new ArrayTypeDenoter();
                break;
            case Boolean:
                typeDenoter = new BooleanTypeDenoter();
                break;
            case Double:
                typeDenoter = new DoubleTypeDenoter();
                break;
            case Fact:
                typeDenoter = new FactTypeDenoter();
                break;
            case Integer:
                typeDenoter = new IntTypeDenoter();
                break;
            case Rule:
                typeDenoter = new RuleTypeDenoter();
                break;
            case Sentence:
                typeDenoter = new SentenceTypeDenoter();
                break;
            case String:
                typeDenoter = new StringTypeDenoter();
                break;
            case Struct:
                typeDenoter = new StructTypeDenoter();
                break;
            case Object:
                typeDenoter = new ObjectTypeDenoter();
                break;
            case Void:
                typeDenoter = new VoidTypeDenoter();
                break;
            case Function:
                typeDenoter = new FunctionTypeDenoter();
                break;
        }

        return typeDenoter;
    }

    public static String factorNameforJavaType(String name) {
        name = name.replaceAll("Literal", "").toLowerCase();
        name = name.replaceAll(";", "");

        int index = name.length() - 1;
        for (index = name.length() - 1; index >= 0; index--) {
            if (name.charAt(index) == '.')
                break;
        }

        name = name.substring(index + 1);

        return name;
    }

    public Type getType() {
        return type;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return identifier.getText();
    }

    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (o instanceof TypeDenoter) {
            TypeDenoter that = (TypeDenoter) o;
            equals = this.type == that.type;
        }
        return equals;
    }

    @Override
    public TypeDenoter clone() {
        TypeDenoter clone = createTypeDenoter(this.type);
        clone.identifier = this.identifier.clone();
        return clone;
    }

    public boolean isComplex() {
        return type == Type.Array || type == Type.Struct;
    }

    public enum Type {
        Array,
        Boolean,
        Double,
        Fact,
        Integer,
        Rule,
        Sentence,
        String,
        Struct,
        Object,
        Void,
        Function
    }

}

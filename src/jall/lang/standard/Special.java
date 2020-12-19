package jall.lang.standard;

import jall.lang.ast.Identifier;
import jall.lang.ast.command.StandardProcedureCommand;
import jall.lang.ast.declaration.Declaration;
import jall.lang.ast.declaration.ProcedureFunctionDeclaration;
import jall.lang.ast.declaration.ReferenceDeclaration;
import jall.lang.ast.literal.*;
import jall.lang.interpreter.Interpreter.Machine;
import jall.lang.interpreter.entities.Entity;
import jall.lang.type.StringTypeDenoter;
import jall.lang.type.StructTypeDenoter;
import jall.lang.type.TypeDenoter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Special {

    public static void invoke(StandardProcedureCommand command, Machine machine, ObjectLiteral returnRegister, Literal[] args) {
        // Gets parameters
        String procName = command.getDeclaration().getIdentifier().getToken().getText();
        Object[] argsObjects = new Object[4];
        argsObjects[0] = command;
        argsObjects[1] = machine;
        argsObjects[2] = returnRegister;
        argsObjects[3] = args;

        Class<?>[] parameterTypes = new Class<?>[4];
        parameterTypes[0] = command.getClass();
        parameterTypes[1] = machine.getClass();
        parameterTypes[2] = returnRegister.getClass();
        parameterTypes[3] = args.getClass();

        try {
            Class<?> classe = Special.class;
            Method method = classe.getMethod(procName, parameterTypes);
            Object obj = classe.newInstance();
            method.invoke(obj, argsObjects);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Declaration> getSpecialDeclarations() {
        List<Declaration> declarations = new ArrayList<Declaration>();

        Identifier identifier = null;
        ProcedureFunctionDeclaration declaration = null;
        TypeDenoter returnType = null;
        ReferenceDeclaration[] argsDeclarations = null;
        TypeDenoter argType = null;
        StandardProcedureCommand command = null;

        // Call procedure
        identifier = new Identifier("call");
        returnType = TypeDenoter.createTypeDenoter("object", 0);
        argsDeclarations = new ReferenceDeclaration[2];
        argType = TypeDenoter.createTypeDenoter("string", 0);
        argsDeclarations[0] = new ReferenceDeclaration(argType, new Identifier("arg0"));
        argType = TypeDenoter.createTypeDenoter("object", 1);
        argsDeclarations[1] = new ReferenceDeclaration(argType, new Identifier("arg1"));
        command = new StandardProcedureCommand("Special");
        declaration = new ProcedureFunctionDeclaration(returnType, identifier, argsDeclarations, command, false);
        command.setDeclaration(declaration);
        declarations.add(declaration);

        // Call Method
        identifier = new Identifier("callMethod");
        returnType = TypeDenoter.createTypeDenoter("object", 0);
        argsDeclarations = new ReferenceDeclaration[3];
        argType = TypeDenoter.createTypeDenoter("struct", 0);
        argsDeclarations[0] = new ReferenceDeclaration(argType, new Identifier("arg0"));
        argType = TypeDenoter.createTypeDenoter("string", 0);
        argsDeclarations[1] = new ReferenceDeclaration(argType, new Identifier("arg1"));
        argType = TypeDenoter.createTypeDenoter("object", 1);
        argsDeclarations[2] = new ReferenceDeclaration(argType, new Identifier("arg2"));
        command = new StandardProcedureCommand("Special");
        declaration = new ProcedureFunctionDeclaration(returnType, identifier, argsDeclarations, command, false);
        command.setDeclaration(declaration);
        declarations.add(declaration);

        return declarations;
    }

    public static void call(StandardProcedureCommand command, Machine machine, ObjectLiteral returnRegister, Literal[] args) {
        StringLiteral procName = (StringLiteral) args[0].convert(new StringTypeDenoter());
        Entity entity = machine.getMemory().fetch(new Identifier(procName.getValue()));

        Literal[] procArgsLiterals = null;

        switch (args[1].getType()) {
            case Array:
                ArrayLiteral procArgs = (ArrayLiteral) args[1];
                procArgsLiterals = new Literal[procArgs.getValue().size()];
                for (int i = 0; i < procArgsLiterals.length; i++)
                    procArgsLiterals[i] = procArgs.getValue().get(i);
                break;
            case Null:
                procArgsLiterals = new Literal[0];
                break;
            default:
                procArgsLiterals = new Literal[1];
                procArgsLiterals[0] = args[1];
        }

        Literal returnLiteral = machine.callProcedure((ProcedureFunctionLiteral) entity.getLiteral(), procArgsLiterals);

        returnRegister.setValue(returnLiteral);
    }

    public static void callMethod(StandardProcedureCommand command, Machine machine, ObjectLiteral returnRegister, Literal[] args) {

        // Gets object
        StructLiteral objectStruct = (StructLiteral) args[0].convert(new StructTypeDenoter());

        // Gets procedure Name
        StringLiteral procName = (StringLiteral) args[1].convert(new StringTypeDenoter());
        Entity entity = objectStruct.getScope().retrieve(new Identifier(procName.getValue()));

        Literal[] procArgsLiterals = null;

        switch (args[2].getType()) {
            case Array:
                ArrayLiteral procArgs = (ArrayLiteral) args[2];
                procArgsLiterals = new Literal[procArgs.getValue().size()];
                for (int i = 0; i < procArgsLiterals.length; i++)
                    procArgsLiterals[i] = procArgs.getValue().get(i);
                break;
            case Null:
                procArgsLiterals = new Literal[0];
                break;
            default:
                procArgsLiterals = new Literal[1];
                procArgsLiterals[0] = args[1];
        }

        Literal returnLiteral = machine.callMethod((ProcedureFunctionDeclaration) entity.getIdentifier().getDeclaration(), objectStruct, procArgsLiterals, command.getDeclaration().getIdentifier());

        returnRegister.setValue(returnLiteral);
    }

}

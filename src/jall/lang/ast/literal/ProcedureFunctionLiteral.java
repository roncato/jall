package jall.lang.ast.literal;

import jall.lang.ast.Visitor;
import jall.lang.ast.command.Command;
import jall.lang.ast.declaration.Declaration;
import jall.lang.type.TypeDenoter;
import jall.util.Utility;

public class ProcedureFunctionLiteral extends Literal {

    private Declaration[] argsDeclarations = null;
    private Command command = null;

    public ProcedureFunctionLiteral(Command command, Declaration[] argsDeclarations) {
        this.command = command;
        this.argsDeclarations = argsDeclarations;
        type = Type.ProcedureFunction;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public void setValue(Literal literal) {
        if (literal.getType() == Type.ProcedureFunction)
            command = ((ProcedureFunctionLiteral) literal).command;
        else
            throw new IllegalArgumentException("Cannot assign literal type '" + literal.type.toString() + "' to procedure.");
    }

    @Override
    public Literal clone() {
        return new ProcedureFunctionLiteral(command, argsDeclarations);
    }

    @Override
    public Literal convert(TypeDenoter typeDenoter) {
        Literal literal = null;
        switch (typeDenoter.getType()) {
            case Object:
                literal = new ObjectLiteral(this);
                break;
        }
        return literal;
    }

    @Override
    public String toString() {
        String txt = "";
        if (argsDeclarations != null) {
            for (Declaration argDeclaration : argsDeclarations) {
                txt += "{\"name\":\"" + argDeclaration.getIdentifier().toString() + "\",";
                txt += "\"type\":\"" + argDeclaration.getTypeDenoter().toString() + "\"},";
            }

            if (txt.length() > 0)
                txt = txt.substring(0, txt.length() - 1);
        }
        String commandStr = command != null ? Utility.escapeJSON(command.toString()) : "";
        txt = "{\"arguments\":[" + txt + "],\"command\":\"" + commandStr + "\"}";
        return txt;
    }

    public Declaration[] getArgsDeclarations() {
        return argsDeclarations;
    }

    public Command getCommand() {
        return command;
    }

}

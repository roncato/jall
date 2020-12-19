package jall.lang.ast.literal;

import jall.lang.ast.Visitor;
import jall.lang.type.TypeDenoter;

public class NullLiteral extends Literal {

    public NullLiteral() {
        type = Type.Null;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public void setValue(Literal literal) {
        throw new IllegalAccessError("Cannot set literal null.");
    }

    @Override
    public Literal clone() {
        return new NullLiteral();
    }

    @Override
    public Literal convert(TypeDenoter typeDenoter) {
        Literal literal = null;
        switch (typeDenoter.getType()) {
            case String:
                literal = new StringLiteral("");
                break;
            case Integer:
                literal = new IntLiteral(0);
                break;
            case Double:
                literal = new DoubleLiteral(0);
                break;
            case Boolean:
                literal = new BooleanLiteral(false);
                break;
            case Object:
                literal = new ObjectLiteral(this);
                break;
            default:
                literal = new NullLiteral();
        }
        return literal;
    }


    @Override
    public String toString() {
        return "null";
    }

}

package jall.lang.ast.expression;

import jall.lang.ast.Visitor;
import jall.lang.ast.literal.NullLiteral;

public class NullExpression extends Expression {

    private NullLiteral literal = null;

    public NullExpression() {
        literal = new NullLiteral();
        type = Type.Null;
    }

    @Override
    public Object accept(Visitor visitor, Object args) {
        return visitor.visit(this, args);
    }

    @Override
    public String toString() {
        return "null";
    }


    public Object getLiteral() {
        return literal;
    }

}

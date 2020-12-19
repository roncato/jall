package jall.lang.ast.expression;

import jall.lang.ast.Identifier;

public abstract class NameExpression extends Expression {

    protected Identifier identifier = null;
    protected Expression[] indexExpressions = null;

    public NameExpression(Identifier identifier, Expression[] indexExpressions) {
        this.identifier = identifier;
        this.indexExpressions = indexExpressions;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public abstract String toString();

    public Expression[] getIndexExpressions() {
        return indexExpressions;
    }

    public void setIndexExpressions(Expression[] indexExpressions) {
        this.indexExpressions = indexExpressions;
    }

}

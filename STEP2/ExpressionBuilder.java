package STEP2;

public class ExpressionBuilder extends AbstractBuilder {
    public String exprString;

    public ExpressionBuilder(String expr) {
        exprString = expr;
    }

    public Exp getExpression() {
        try {
            RDParser p = new RDParser(exprString);
            return p.callExpr();
        } catch (Exception e) {
            return null;
        }
    }
}

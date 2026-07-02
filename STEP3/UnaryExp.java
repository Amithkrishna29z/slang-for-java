package STEP3;

public class UnaryExp extends Exp {
    private Exp ex1;
    private Operator op;

    public UnaryExp(Exp ex1, Operator op) {
        this.ex1 = ex1;
        this.op = op;
    }

    @Override
    public double evaluate(RuntimeContext cont) {
        switch (op) {
            case PLUS:
                return ex1.evaluate(cont);
            case MINUS:
                return -ex1.evaluate(cont);
            default:
                return Double.NaN;
        }
    }
}

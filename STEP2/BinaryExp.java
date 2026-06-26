package STEP2;

public class BinaryExp extends Exp {
    private Exp ex1, ex2;
    private Operator op;

    public BinaryExp(Exp ex1, Exp ex2, Operator op) {
        this.ex1 = ex1;
        this.ex2 = ex2;
        this.op = op;
    }

    @Override
    public double evaluate(RuntimeContext cont) {
        switch (op) {
            case PLUS:
                return ex1.evaluate(cont) + ex2.evaluate(cont);
            case MINUS:
                return ex1.evaluate(cont) - ex2.evaluate(cont);
            case DIV:
                return ex1.evaluate(cont) / ex2.evaluate(cont);
            case MUL:
                return ex1.evaluate(cont) * ex2.evaluate(cont);
            default:
                return Double.NaN;
        }
    }
}

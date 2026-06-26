package STEP2;

public class NumericConstant extends Exp {

    private double value;

    public NumericConstant(double value) {
        this.value=value;
    }

    @Override
    public double evaluate(RuntimeContext cont) {
        return value;
    }
}

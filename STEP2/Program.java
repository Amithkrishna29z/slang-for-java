package STEP2;

public class Program {
    public static void main(String[] args) {
        ExpressionBuilder b = new ExpressionBuilder("-2*(-3+3)");
        Exp e = b.getExpression();
        System.out.println(e.evaluate(null));

        try {
            System.in.read();
        } catch (Exception ex) {

        }
    }
}

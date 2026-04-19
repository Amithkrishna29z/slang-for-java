package STEP1;

public class Program {
    public static void main(String[] args) {
        // AST for 5*10
        Exp e = new BinaryExp(new NumericConstant(5), new NumericConstant(10), Operator.MUL);
        System.out.println(e.evaluate(null));

        // AST for -(10 + (30 + 50))
        e = new UnaryExp(
                new BinaryExp(new NumericConstant(10),
                        new BinaryExp(new NumericConstant(30), new NumericConstant(50), Operator.PLUS), Operator.PLUS),
                Operator.MINUS);

        System.out.println(e.evaluate(null));

        try {
            System.in.read();
        } catch (Exception ex) {

        }
    }

}

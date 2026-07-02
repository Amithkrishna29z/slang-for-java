package STEP3;

public class PrintStatement extends Stmt {
    private Exp ex;

    public PrintStatement(Exp ex) {
        this.ex=ex;
    }

    @Override
    public boolean execute(RuntimeContext con) {
        double a = ex.evaluate(con);
        System.out.println(a);
        return true;
    }
}

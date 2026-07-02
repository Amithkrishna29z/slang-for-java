package STEP3;

public class PrintLineStatement extends Stmt {
    private Exp ex;

    public PrintLineStatement(Exp ex) {
        this.ex = ex;
    }

    @Override
    public boolean execute(RuntimeContext con) {
        double a = ex.evaluate(con);
        System.out.println(a);
        return true;
    }
}
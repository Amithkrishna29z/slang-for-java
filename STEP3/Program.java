package STEP3;

import java.util.ArrayList;

public class Program {

    static void testFirstScript() {
        String a = "PRINTLINE 2*10;"+"\r\n"+ "PRINTLINE 10;\r\n PRINT 2*10;\r\n";
        RDParser p = new RDParser(a);
        ArrayList<Stmt> arr = p.parse();
        for(Stmt s:arr) {
            s.execute(null);
        }
    }

    static void testSecondScript() {
        String a = "PRINTLINE -2*10;" + "\r\n" + "PRINTLINE -10*-1;\r\n PRINT 2*10;\r\n";
        RDParser p = new RDParser(a);
        ArrayList<Stmt> arr = p.parse();
        for (Stmt s : arr) {
            s.execute(null);
        }
    }

    public static void main(String[] args) {
        testFirstScript();
        // testSecondScript();

        try {
            System.in.read();
        } catch (Exception ex) {

        }
    }
}

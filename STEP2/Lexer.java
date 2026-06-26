package STEP2;

public class Lexer {
    private String iExpr;
    private int index;
    private int length;
    private double number;

    public Lexer(String expr) {
        iExpr = expr;
        length = iExpr.length();
        index = 0;
    }

    public Token getToken() {
        Token tok = Token.ILLEGAL_TOKEN;

        while (index < length && (iExpr.charAt(index) == ' ' || iExpr.charAt(index) == '\t'))
            index++;

        if (index == length)
            return Token.TOK_NULL;

        switch (iExpr.charAt(index)) {
            case '+':
                tok = Token.TOK_PLUS;
                index++;
                break;
            case '-':
                tok = Token.TOK_SUB;
                index++;
                break;
            case '/':
                tok = Token.TOK_DIV;
                index++;
                break;
            case '*':
                tok = Token.TOK_MUL;
                index++;
                break;
            case '(':
                tok = Token.TOK_OPAREN;
                index++;
                break;
            case ')':
                tok = Token.TOK_CPAREN;
                index++;
                break;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': {
                String str = "";
                while (index < length && Character.isDigit(iExpr.charAt(index))) {
                    str += iExpr.charAt(index);
                    index++;
                }
                number = Double.parseDouble(str);
                tok = Token.TOK_DOUBLE;
                break;
            }
            default:
                System.out.println("Error While Analyzing Tokens");
                throw new RuntimeException();
        }
        return tok;
    }

    public double getNumber() {
        return number;
    }
}

package STEP3;

public class Lexer {
    private String exp;
    private int index;
    private int lengthString;
    private double currNum;
    private ValueTable[] val;
    private String lastStr;

    public Lexer(String exp) {
        this.exp = exp;
        this.lengthString = exp.length();
        this.index = 0;

        val = new ValueTable[2];
        val[0] = new ValueTable(Token.TOK_PRINT, "PRINT");
        val[1] = new ValueTable(Token.TOK_PRINTLN, "PRINTLINE");
    }

    public double getNumber() {
        return currNum;
    }

    public Token getToken() {

        while (true) {
            Token tok = Token.ILLEGAL_TOKEN;

            while (index < lengthString && (exp.charAt(index) == ' ' || exp.charAt(index) == '\t'))
                index++;

            if (index == lengthString)
                return Token.TOK_NULL;

            char c = exp.charAt(index);
            switch (c) {
                case '\r':
                case '\n':
                    index++;
                    continue;
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
                case ';':
                    tok = Token.TOK_SEMI;
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
                    while (index < lengthString && Character.isDigit(exp.charAt(index))) {
                        str += exp.charAt(index);
                        index++;
                    }
                    currNum = Double.parseDouble(str);
                    tok = Token.TOK_DOUBLE;
                    break;
                }
                default: {
                    if (Character.isLetter(c)) {
                        String tem = "" + c;
                        index++;
                        while (index < lengthString
                                && (Character.isLetterOrDigit(exp.charAt(index)) || exp.charAt(index) == '_')) {
                            tem += exp.charAt(index);
                            index++;
                        }
                        tem = tem.toUpperCase();

                        for (int i = 0; i < val.length; i++) {
                            if (val[i].value.compareTo(tem) == 0) {
                                return val[i].tok;
                            }
                        }

                        lastStr = tem;
                        return Token.TOK_UNQUOTED_STRING;
                    } else {
                        System.out.println("Error While Analyzing Tokens");
                        throw new RuntimeException();
                    }
                }
            }
            return tok;
        }

    }
}
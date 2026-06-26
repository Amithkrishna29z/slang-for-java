package STEP2;

public class RDParser extends Lexer {
    private Token currentToken;

    public RDParser(String str) {
        super(str);
    }

    public Exp callExpr() {
        currentToken = getToken();
        return expr();
    }

    public Exp expr() {
        Token lToken;
        Exp retValue = term();
        while (currentToken == Token.TOK_PLUS || currentToken == Token.TOK_SUB) {
            lToken = currentToken;
            currentToken = getToken();
            Exp e1 = expr();
            retValue = new BinaryExp(retValue, e1, lToken == Token.TOK_PLUS ? Operator.PLUS : Operator.MINUS);
        }
        return retValue;
    }

    public Exp term() {
        Token lToken;
        Exp retValue = factor();
        while (currentToken == Token.TOK_MUL || currentToken == Token.TOK_DIV) {
            lToken = currentToken;
            currentToken = getToken();
            Exp e1 = term();
            retValue = new BinaryExp(retValue, e1, lToken==Token.TOK_MUL? Operator.MUL:Operator.DIV);
        }
        return retValue;
    }

    public Exp factor() {
        Token lToken;
        Exp retValue = null;
        if(currentToken==Token.TOK_DOUBLE) {
            retValue = new NumericConstant(getNumber());
            currentToken = getToken();
        } else if(currentToken == Token.TOK_OPAREN) {
            currentToken = getToken();
            retValue = expr();
            if(currentToken!=Token.TOK_CPAREN) {
                System.out.println("Missing Closing parenthesis");
                throw new RuntimeException();
            }
            currentToken = getToken();
        }
        else if(currentToken == Token.TOK_PLUS || currentToken == Token.TOK_SUB) {
            lToken = currentToken;
            currentToken = getToken();
            retValue = factor();
            retValue = new UnaryExp(retValue, lToken == Token.TOK_PLUS ? Operator.PLUS : Operator.MINUS);
        } else {
            System.out.println("Illegal Token");
            throw new RuntimeException();
        }
        return retValue;
    }
}

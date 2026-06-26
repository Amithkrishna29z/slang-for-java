package STEP2;

public enum Token {
    ILLEGAL_TOKEN(-1),
    TOK_PLUS(1),
    TOK_MUL(2),
    TOK_DIV(3),
    TOK_SUB(4),
    TOK_OPAREN(5),
    TOK_CPAREN(6),
    TOK_DOUBLE(7),
    TOK_NULL(8);

    private final int value;

    Token(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

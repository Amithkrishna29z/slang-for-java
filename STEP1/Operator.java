package STEP1;

public enum Operator {
    ILLEGAL(-1),
    PLUS(0),
    MINUS(1),
    DIV(2),
    MUL(3);

    private final int value;

    Operator(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
package io.github.toandv.expressions;

public enum Operator {

    ADD("+"), MINUS("-"), DIVIDE("/"), MULTIPLE("*");
    String name;

    private Operator(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Operator getOp(String name) {
        switch (name) {
        case "+":
            return ADD;
        case "-":
            return MINUS;
        case "*":
            return MULTIPLE;
        case "/":
            return DIVIDE;
        }
        return null;
    }
}
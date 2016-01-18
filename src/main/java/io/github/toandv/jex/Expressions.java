package io.github.toandv.jex;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Expressions {

    static final Set<String> OPS = new HashSet<>(4);
    static final Set<String> PARATHESES = new HashSet<>(2);

    static {
        OPS.add("+");
        OPS.add("-");
        OPS.add("*");
        OPS.add("/");
        PARATHESES.add("(");
        PARATHESES.add(")");
    }

    public static boolean isMatched(List<String> infix) {
        Deque<String> stack = new ArrayDeque<>();
        for (String item : infix) {
            if (isOpenParenthesis(item)) {
                stack.push(item);
            }
            if (isCloseParenthesis(item)) {
                if (stack.isEmpty()) {
                    return false;
                } else {
                    if (!isOpenParenthesis(stack.pop())) {
                        return false;
                    }
                }
            }
        }
        return stack.isEmpty();
    }

    public static List<String> normalize(List<String> infix) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < infix.size(); i++) {
            String item = infix.get(i);
            if (isOperand(item)) {
                result.add(item);
            } else if (isOperator(item)) {
                List<String> ops = new ArrayList<>();
                ops.add(item);
                for (int j = i + 1; j < infix.size(); j++) {
                    String item1 = infix.get(j);
                    if (!isOperator(item1)) {
                        i = j - 1;
                        break;
                    } else {
                        ops.add(item1);
                    }
                }
                String op = compact(ops);
                result.add(op);
            } else {
                result.add(item);
            }
        }
        return result;
    }

    private static String compact(List<String> ops) {
        int add = 0;
        int minus = 0;
        int mul = 0;
        int div = 0;
        for (String op : ops) {
            if ("+".equals(op)) {
                add++;
            }
            if ("-".equals(op)) {
                minus++;
            }
            if ("*".equals(op)) {
                mul++;

            }

            if ("/".equals(op)) {
                div++;
            }
        }
        if (((add > 0 || minus > 0) && (mul > 0 || div > 0)) || mul > 1 || div > 1 || mul + div > 1) {
            throw new IllegalArgumentException("invalid ops" + ops);
        }

        if (div == 1) {
            return "/";
        }

        if (mul == 1) {
            return "*";
        }

        if (minus % 2 != 0) {
            return "-";
        }

        if (add > 0) {
            return "+";
        }

        throw new IllegalArgumentException("invalid ops" + ops);

    }

    // first, make sure infix is  valid, space-free
    public static List<String> toPostfix(List<String> infix) {
        List<String> postfix = new ArrayList<>(infix.size());

        // use ArrayDeque instead

        Deque<String> operators = new ArrayDeque<>(infix.size());

        for (String item : infix) {

            if (isOperand(item)) {
                postfix.add(item);
            }

            if (isOpenParenthesis(item)) {
                operators.push(item);
            }

            if (isCloseParenthesis(item)) {
                while (!operators.isEmpty()) {
                    String topOp = operators.pop();
                    if (!isOpenParenthesis(topOp)) {
                        postfix.add(topOp);
                    } else {
                        break;
                    }
                }
            }

            if (isOperator(item)) {
                if (operators.isEmpty()) {
                    // mutate operators
                    operators.push(item);
                } else {
                    while (!operators.isEmpty()) {
                        String topOP = operators.pop();
                        if (isOpenParenthesis(topOP)) {
                            operators.push(topOP);
                            break;
                        }

                        if (isOperator(topOP)) {
                            if (lefthasHigherPrecedence(item, topOP)) {
                                operators.push(topOP);
                                break;
                            } else {
                                postfix.add(topOP);
                            }
                        }
                    }
                    operators.push(item);
                }

            }
        }

        while (!operators.isEmpty()) {
            postfix.add(operators.pop());
        }

        return postfix;
    }

    public static int evaluate(List<String> infix) {

        Deque<String> operands = new ArrayDeque<>();

        for (String item : infix) {
            if (isOperand(item)) {
                operands.push(item);
            } else {
                if (operands.size() >= 2) {
                    String right = operands.pop();
                    String left = operands.pop();
                    operands.push(apply(item, left, right));
                }
            }
        }
        return Integer.parseInt(operands.pop());
    }

    private static String apply(String operator, String left, String right) {
        int a = Integer.parseInt(left);
        int b = Integer.parseInt(right);
        switch (operator) {
        case "+":
            return a + b + "";
        case "-":
            return a - b + "";
        case "*":
            return a * b + "";
        case "/":
            return a / b + "";
        }
        return null;
    }

    public static Node buildTree(List<String> postfix) {

        Deque<Node> nodes = new ArrayDeque<>();

        for (String item : postfix) {
            if (isOperand(item)) {
                nodes.push(new Node(item));
            } else {
                Node right = nodes.pop();
                Node left = nodes.pop();
                nodes.push(new Node(item, left, right));
            }
        }

        return nodes.pop();
    }

    public static void inOrder(Node root) {
        if (root != null) {
            inOrder(root.getLeft());
            System.out.print(root);
            inOrder(root.getRight());
        }
    }

    public static void preOrder(Node root) {
        if (root != null) {
            System.out.print(root);
            preOrder(root.getLeft());
            preOrder(root.getRight());
        }
    }

    public static void postOrder(Node root) {
        if (root != null) {
            postOrder(root.getLeft());
            postOrder(root.getRight());
            System.out.print(root);
        }
    }

    public static void main(String[] args) {
        List<String> infix = Arrays.asList("1", "+", "-", "2", "*", "(", "3", "-", "4", ")");
        System.out.println(normalize(infix));
        System.out.println(isMatched(infix));

    }

    static boolean isOperand(String item) {
        return !OPS.contains(item) && !PARATHESES.contains(item);
    }

    static boolean isOperator(String item) {
        return OPS.contains(item);
    }

    static boolean isOpenParenthesis(String parenthesis) {
        return "(".equals(parenthesis);
    }

    static boolean isCloseParenthesis(String parenthesis) {
        return ")".equals(parenthesis);
    }

    // compare if left operator has higher precedence than right's
    static boolean lefthasHigherPrecedence(String left, String right) {
        return (("*".equals(left) || "/".equals(left)) && ("+".equals(right)) || "-".equals(right));
    }
}

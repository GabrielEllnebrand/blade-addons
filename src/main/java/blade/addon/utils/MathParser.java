package blade.addon.utils;

import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is purely made for the searchbar.
 * The parser is based on Shunting Yard and Reverse Polish Notation.
 * Yes this is commented more than other files, but this was more fun,
 * so I actually felt like documenting it.
 */
public class MathParser {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("^(?<num>-?\\d+)(?<decimal>\\.\\d+)?(?<unit>[bBmMkK])?");
    private static final Pattern TOKEN_PATTERN = Pattern.compile("[()+\\-*/^]");

    /**
     * Parses an expression and returns the value.
     * @param string The expression to parse
     * @return The value if it's a valid expression, otherwise NaN
     */
    public static double parseExpression(String string) {
        if (string == null) return Double.NaN;

        ArrayList<String> tokens;
        try {
            tokens = toTokens(string);
        } catch (IllegalArgumentException e) {
            return Double.NaN;
        }

        if (tokens.isEmpty()) return Double.NaN;

        if (tokens.size() == 1) {
            try {
                //incase it's just a number
                return tokenToNum(tokens.getFirst());
            } catch (NumberFormatException ignored) {
                return Double.NaN;
            }
        }

        ArrayList<String> rpnTokens = toRPN(tokens);

        if (rpnTokens == null) {
            return Double.NaN;
        }
        try {
            return parseRPN(rpnTokens);
        } catch (IllegalArgumentException e) {
            return Double.NaN;
        }
    }

    /**
     * Converts a String to a number.
     * Supports orders of magnitudes like b, m and k.
     * It also supports negative and decimal numbers.
     * If it can't convert the number,
     * {@code NumberFormatException} will be thrown.
     *
     * @param token The token to convert to a number
     * @return The converted number
     */
    private static double tokenToNum(String token) {
        Matcher matcher = NUMBER_PATTERN.matcher(token);
        if (!matcher.find()) {
            throw new NumberFormatException();
        }

        String num = matcher.group("num");
        String decimal = matcher.group("decimal");
        String unit = matcher.group("unit");

        double value;

        if (decimal != null) {
            value = Double.parseDouble(num + decimal);
        } else {
            value = Double.parseDouble(num);
        }


        if (unit != null) {
            switch (unit) {
                case "b", "B":
                    value *= 1_000_000_000;
                    break;
                case "m", "M":
                    value *= 1000_000;
                    break;
                case "k", "K":
                    value *= 1000;
                    break;
            }
        }

        return value;
    }


    /**
     * Parses an RPN token, Will throw an {@code IllegalArgumentException} when handling an invalid token.
     * @param stack the stack to keep track of previously handled tokens
     * @param token The token to parse
     */
    private static void handleTokenRPN(Stack<Double> stack, String token) {
        try {
            double value = tokenToNum(token);
            stack.push(value);
            return;
        } catch (NumberFormatException ignored) {
        }

        if (stack.size() < 2) {
            if (stack.size() == 1) {
                if (token.equals("-")) {
                    //edge case to make for example -(2+3) negative
                    stack.push(stack.pop() * -1);
                    return;
                }
            }

            throw new IllegalArgumentException("Invalid token");
        }


        double right = stack.pop();
        double left = stack.pop();

        switch (token) {
            case "+":
                stack.push(left + right);
                break;
            case "-":
                stack.push(left - right);
                break;
            case "*":
                stack.push(left * right);
                break;
            case "/":
                stack.push(left / right);
                break;
            case "^":
                stack.push(Math.pow(left, right));
                break;
            default:
                throw new IllegalArgumentException("Invalid operator");
        }
    }

    /**
     * Parses an expression of tokens in RPN.
     * Reference: <a href="https://inspirnathan.com/posts/150-reverse-polish-notation-evaluator-in-javascript/">reverse-polish-notation-evaluator-in-javascript</a>
     * @param tokens The tokens to parse
     * @return The parsed value, {@code Double.NaN} if its invalid
     */
    private static double parseRPN(ArrayList<String> tokens) {
        Stack<Double> stack = new Stack<>();

        for (String token : tokens) {
            handleTokenRPN(stack, token);
        }

        if (stack.isEmpty()) return Double.NaN;
        return stack.pop();
    }

    private static int getPrecedence(String token) {
        return switch (token) {
            case "^" -> 4;
            case "*", "/" -> 3;
            case "+", "-" -> 2;
            default -> -1;
        };
    }

    private static String getAssociative(String token) {
        return switch (token) {
            case "^" -> "right";
            case "*", "/", "+", "-" -> "left";
            default -> "";
        };
    }

    private static boolean isANumber(String token) {
        Matcher matcher = NUMBER_PATTERN.matcher(token);
        return matcher.find();
    }

    private static boolean addToken(ArrayList<String> output, Stack<String> stack, String token) {
        if (isANumber(token)) {
            output.add(token);
            return true;
        } else if (getPrecedence(token) != -1) {
            if (stack.isEmpty()) {
                stack.push(token);
                return true;
            }

            String top = stack.peek();

            while (!top.equals("(") && (getPrecedence(top) > getPrecedence(token)
                    || (getPrecedence(top) == getPrecedence(token) && getAssociative(token).equals("left")))
            ) {
                output.add(stack.pop());
                if (stack.isEmpty()) break;
                top = stack.peek();
            }
            stack.push(token);
            return true;

        } else if (token.equals("(")) {
            stack.push(token);
            return true;
        } else if (token.equals(")")) {
            String top = stack.peek();
            while (!top.equals("(")) {
                if (stack.isEmpty()) return false;
                output.add(stack.pop());
                if (stack.isEmpty()) return false;
                top = stack.peek();
            }

            if (stack.isEmpty() || !stack.peek().equals("(")) return false;
            stack.pop();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Converts a list of tokens to a new in RPN
     * Refrence: <a href="https://inspirnathan.com/posts/151-shunting-yard-algorithm-in-javascript/">151-shunting-yard-algorithm-in-javascript</a>
     * @param tokens The tokens to convert
     * @return The list of tokens in RPN
     */
    private static ArrayList<String> toRPN(ArrayList<String> tokens) {
        ArrayList<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        for (String token : tokens) {
            if (!addToken(output, stack, token)) {
                return null;
            }
        }

        while (!stack.isEmpty()) {
            String token = stack.pop();
            if (token.equals("(")) return null;
            output.add(token);
        }

        return output;
    }

    /**
     * Split an expression string to an ArrayList of tokens.
     * Throws an {@code IllegalArgumentException} when an invalid token is read.
     *
     * @param string The string to split into token
     * @return The list of tokens
     */
    private static ArrayList<String> toTokens(String string) {
        ArrayList<String> output = new ArrayList<>();
        String copiedString = string.replaceAll(" ", "");

        //to handle binary subtraction not getting confused
        //with unary subtraction
        boolean wasPrevNum = false;

        while (!copiedString.isEmpty()) {
            Matcher matcher = NUMBER_PATTERN.matcher(copiedString);
            if (!wasPrevNum && matcher.find()) {
                String token = matcher.group();
                int index = copiedString.indexOf(token);
                if (index == 0) {
                    copiedString = copiedString.substring(token.length());
                    output.add(token);
                    wasPrevNum = true;
                    continue;
                }
            }

            matcher = TOKEN_PATTERN.matcher(copiedString);
            if (matcher.find()) {
                String token = matcher.group();
                int index = copiedString.indexOf(token);
                if (index == 0) {
                    copiedString = copiedString.substring(token.length());
                    output.add(token);
                    wasPrevNum = false;
                    continue;
                }
            }

            throw new IllegalArgumentException("String: " + string + " has invalid token, ended at: " + copiedString);
        }

        return output;
    }

}

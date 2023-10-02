package main;

import java.util.Scanner;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;

/**
 * Класс "ExpressionEvaluator" представляет собой программу для вычисления математических выражений,
 * содержащих цифры, скобки, операции "+, -, *, /" и переменные, значения которых задает пользователь
 * Выражения для вычисления вводятся пользователем и результат выводится в консоль.
 */
public class ExpressionEvaluator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Input expression: ");
            String expression = scanner.nextLine();

            if (expression == null || expression.isEmpty())
                throw new IllegalArgumentException("Error: Expression is empty.");

            Map<String, Double> variables = new HashMap<>(); //контейнер для хранения значения переменных
            double result = EvaluateExpression(expression, variables, scanner);

            System.out.println("Result: " + result);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    /**
     * Метод EvaluateExpression вычисляет результат математического выражения.
     *
     * @param expression Входное математическое выражение.
     * @param variables  Контейнер для хранения значений переменных.
     * @param scanner    Объект Scanner для ввода данных пользователя.
     * @return Результат вычисления выражения.
     */
    public static double EvaluateExpression(String expression, Map<String, Double> variables, Scanner scanner) {
        expression = expression.replaceAll("\\s+", ""); //удаляем все лишние пробелы из выражения

        Stack<Double> operandStack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();

        int i = 0;

        while (i < expression.length()) {
            char curChar = expression.charAt(i);

            // Если текущий символ - цифра
            if (Character.isDigit(curChar) || (curChar == '-' && (i == 0 || "+-*/(".contains(String.valueOf(expression.charAt(i - 1)))))) {
                StringBuilder numBuilder = new StringBuilder();
                // Считываем все символы, формируя числовую строку (включая точку для десятичных чисел)
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) ||
                        expression.charAt(i) == '.' || (expression.charAt(i) == '-' && numBuilder.length() == 0))) {
                    numBuilder.append(expression.charAt(i));
                    i++;
                }
                // Преобразуем числовую строку в double и помещаем в стек операндов
                operandStack.push(Double.parseDouble(numBuilder.toString()));
            }
            // Если текущий символ - буква (переменная)
            else if (Character.isLetter(curChar)) {
                StringBuilder varBuilder = new StringBuilder();
                // Считываем буквы, формируя имя переменной
                while (i < expression.length() && Character.isLetter(expression.charAt(i))) {
                    varBuilder.append(expression.charAt(i));
                    i++;
                }
                String variableName = varBuilder.toString();
                // Если переменная не существует, запрашиваем у пользователя её значение
                if (!variables.containsKey(variableName)) {
                    System.out.print("Input a value of variable " + variableName + ": ");
                    double variableValue = scanner.nextDouble();
                    scanner.nextLine();
                    variables.put(variableName, variableValue);
                }
                operandStack.push(variables.get(variableName));
            }
            // Если текущий символ - оператор (+, -, *, /)
            else if ("+-*/".contains(String.valueOf(curChar))) {
                // Обрабатываем приоритетность операторов
                while (!operatorStack.isEmpty() && HasHigherPrecedence(String.valueOf(curChar), operatorStack.peek()))
                    Evaluate(operandStack, operatorStack);
                operatorStack.push(String.valueOf(curChar));
                i++;
            } else if (curChar == '(') {
                operatorStack.push(String.valueOf(curChar));
                i++;
            } else if (curChar == ')') {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("("))
                    Evaluate(operandStack, operatorStack);
                if (!operatorStack.isEmpty() && operatorStack.peek().equals("("))
                    operatorStack.pop();
                else
                    throw new IllegalArgumentException("Error: Inconsistent brackets.");
                i++;
            } else
                throw new IllegalArgumentException("Error: Invalid character - " + curChar);
        }

        while (!operatorStack.isEmpty())
            Evaluate(operandStack, operatorStack);

        if (operandStack.size() == 1)
            return operandStack.pop();
        else
            throw new IllegalArgumentException("Error: Invalid expression.");
    }

    /**
     * Метод HasHigherPrecedence проверяет, имеет ли оператор op1 менее высокий приоритет, чем оператор op2.
     *
     * @param op1 Первый оператор.
     * @param op2 Второй оператор.
     * @return True, если op1 имеет менее высокий или равный приоритет, в противном случае false.
     */
    private static boolean HasHigherPrecedence(String op1, String op2) {
        int precedence1 = GetOperatorPrecedence(op1);
        int precedence2 = GetOperatorPrecedence(op2);
        return precedence1 <= precedence2;
    }

    /**
     * Метод GetOperatorPrecedence возвращает уровень приоритета оператора.
     *
     * @param operator Оператор, для которого нужно определить приоритет.
     * @return Уровень приоритета оператора.
     */
    private static int GetOperatorPrecedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            default:
                return 0;
        }
    }

    /**
     * Метод Evaluate выполняет операцию над двумя операндами и оператором.
     *
     * @param operandStack  Стек операндов.
     * @param operatorStack Стек операторов.
     */
    private static void Evaluate(Stack<Double> operandStack, Stack<String> operatorStack) {
        if (operatorStack.isEmpty() || operandStack.size() < 2)
            throw new IllegalArgumentException("Error: Not enough operators or operands.");

        String operator = operatorStack.pop();
        double operand2 = operandStack.pop();
        double operand1 = operandStack.pop();
        double result = PerformOperation(operand1, operand2, operator);
        operandStack.push(result);
    }

    /**
     * Метод PerformOperation выполняет математическую операцию на основе предоставленного оператора.
     *
     * @param operand1 Первый операнд.
     * @param operand2 Второй операнд.
     * @param operator Оператор для применения к операндам.
     * @return Результат операции.
     */
    private static double PerformOperation(double operand1, double operand2, String operator) {
        switch (operator) {
            case "+":
                return operand1 + operand2;
            case "-":
                return operand1 - operand2;
            case "*":
                return operand1 * operand2;
            case "/":
                if (operand2 != 0)
                    return operand1 / operand2;
                else
                    throw new ArithmeticException("Error: Division by zero.");
            default:
                throw new IllegalArgumentException("Error: Invalid operator.");
        }
    }
}
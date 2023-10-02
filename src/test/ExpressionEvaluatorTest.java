package test;

import main.ExpressionEvaluator;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

class ExpressionEvaluatorTest {
    private Map<String, Double> variables;
    private Scanner scanner;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        variables = new HashMap<>();
        scanner = new Scanner(System.in);
    }

    @org.junit.jupiter.api.Test
    void testEvaluateValidExpression(){
        String expression = "4 - 5 * 8";
        double result = ExpressionEvaluator.EvaluateExpression(expression, variables, scanner);
        assertEquals(-36.0, result, 0.001);
    }

    @org.junit.jupiter.api.Test
    void testEvaluateValidExpressionWithVariables(){
        String expression = "5 + x * y - x";
        variables.put("x", 2.0);
        variables.put("y", 10.0);
        double result = ExpressionEvaluator.EvaluateExpression(expression, variables, scanner);
        assertEquals(23.0, result, 0.01);
    }

    @org.junit.jupiter.api.Test
    void testEvaluateValidExpressionWithBrackets(){
        String expression = "8 / 4 * ( 20 - (x - 8 * y))";
        variables.put("x", 10.0);
        variables.put("y", 2.0);
        double result = ExpressionEvaluator.EvaluateExpression(expression, variables, scanner);
        assertEquals(52.0, result, 0.01);
    }

    @org.junit.jupiter.api.Test
    void testEvaluateValidExpressionWithDoubleDivide(){
        String expression = "3 / 5";
        double result = ExpressionEvaluator.EvaluateExpression(expression, variables, scanner);
        assertEquals(0.6, result, 0.01);
    }

    @org.junit.jupiter.api.Test
    void testEvaluateValidExpressionWithNegativeNumbers(){
        String expression = "5 - (-6 * 2)";
        double result = ExpressionEvaluator.EvaluateExpression(expression, variables, scanner);
        assertEquals(17, result, 0.01);
    }

    @org.junit.jupiter.api.Test
    void testEvaluateExpressionWithInconsistentBrackets(){
        String expression1 = "4 * )2 + 7)";
        assertThrows(IllegalArgumentException.class, () -> ExpressionEvaluator.EvaluateExpression(expression1, variables, scanner));

        String expression2 = "4 * ((2 + 7)";
        assertThrows(IllegalArgumentException.class, () -> ExpressionEvaluator.EvaluateExpression(expression2, variables, scanner));
    }

    @org.junit.jupiter.api.Test
    void testEvaluateInvalidExpression(){
        String expression = "4 * + 5";
        assertThrows(IllegalArgumentException.class, () -> ExpressionEvaluator.EvaluateExpression(expression, variables, scanner));
    }

    @org.junit.jupiter.api.Test
    void testEvaluateExpressionWithDivisionByZero(){
        String expression = "1 / 0";
        assertThrows(ArithmeticException.class, () -> ExpressionEvaluator.EvaluateExpression(expression, variables, scanner));
    }

    @org.junit.jupiter.api.Test
    void testEvaluateExpressionWithEmptyExpression(){
        String expression = "\n";
        assertThrows(IllegalArgumentException.class, ()->ExpressionEvaluator.EvaluateExpression(expression, variables, scanner));
    }

}
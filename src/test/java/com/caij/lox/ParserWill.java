package com.caij.lox;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ParserWill {

    @Test
    public void parse_to_literal() {
        new ExpressionVerifier(
                new Token(TokenType.TRUE, "true", true, 1),
                eofOn(2)
        ).producesExpression("true");

        new ExpressionVerifier(
                new Token(TokenType.FALSE, "false", false, 1),
                eofOn(2)
        ).producesExpression("false");

        new ExpressionVerifier(
                new Token(TokenType.NIL, "nil", null, 1),
                eofOn(2)
        ).producesExpression("nil");
    }

    @Test
    public void parse_to_unary() {
        new ExpressionVerifier(
                new Token(TokenType.MINUS, "-", "-", 1),
                new Token(TokenType.NUMBER, "1", 1, 2),
                eofOn(3)
        ).producesExpression("(- 1)");

        new ExpressionVerifier(
                new Token(TokenType.BANG, "!", "!", 1),
                new Token(TokenType.TRUE, "true", true, 2),
                eofOn(3)
        ).producesExpression("(! true)");
    }

    @Test
    public void parse_to_grouping() {
        new ExpressionVerifier(
                new Token(TokenType.LEFT_PAREN, "(", "(", 1),
                new Token(TokenType.NUMBER, "1", 1, 2),
                new Token(TokenType.PLUS, "+", "+", 3),
                new Token(TokenType.NUMBER, "2", 2, 4),
                new Token(TokenType.RIGHT_PAREN, ")", ")", 5),
                eofOn(6)
        ).producesExpression("(group (+ 1 2))");
    }

    @Test
    public void parse_to_binary() {
        new ExpressionVerifier(
                new Token(TokenType.NUMBER, "1", 1, 1),
                new Token(TokenType.STAR, "*", "*", 2),
                new Token(TokenType.NUMBER, "2", 2, 3),
                eofOn(4)
        ).producesExpression("(* 1 2)");

        new ExpressionVerifier(
                new Token(TokenType.NUMBER, "100", 100, 1),
                new Token(TokenType.LESS, "<", "<", 2),
                new Token(TokenType.NUMBER, "2", 2, 3),
                eofOn(4)
        ).producesExpression("(< 100 2)");

        new ExpressionVerifier(
                new Token(TokenType.TRUE, "true", true, 1),
                new Token(TokenType.BANG_EQUAL, "!=", "!=", 2),
                new Token(TokenType.FALSE, "false", false, 3),
                eofOn(4)
        ).producesExpression("(!= true false)");
    }

    @Test
    public void return_null_and_print_error_if_parse_error_encountered() {
        new ExpressionVerifier(eofOn(1))
                .returnsNull()
                .reportedTheFollowingError("[line 1] Error at end: Expect expression.\n");

        new ExpressionVerifier(
                new Token(TokenType.LEFT_PAREN, "(", "(", 1),
                new Token(TokenType.NUMBER, "1", "1", 2),
                eofOn(3))
                .returnsNull()
                .reportedTheFollowingError("[line 3] Error at end: Expect ')' after expression.\n");
    }

    private class ExpressionVerifier {

        private final Token[] tokens;
        private final ByteArrayOutputStream redirectedConsoleOutput;
        private final PrintStream oldConsole;

        ExpressionVerifier(Token... tokens) {
            this.tokens = tokens;

            redirectedConsoleOutput = new ByteArrayOutputStream();
            oldConsole = System.out;
            redirectSystemErrors();
        }

        void producesExpression(String expectedExpression) {
            final Expr expression = new Parser(asList(tokens)).parse();

            final String visited = new AstPrinter().print(expression);
            assertThat(visited).isEqualTo(expectedExpression);
            assertThat(visited).isNotNull();
        }

        ExpressionVerifier returnsNull() {
            assertThat(new Parser(asList(tokens)).parse()).isNull();
            return this;
        }

        void reportedTheFollowingError(String expectedConsoleOutput) {
            assertThat(redirectedConsoleOutput.toString()).isEqualTo(expectedConsoleOutput);
            resetBackToConsole();
        }

        private void redirectSystemErrors() {
            PrintStream ps = new PrintStream(redirectedConsoleOutput);
            System.setErr(ps);
        }

        private void resetBackToConsole() {
            System.out.flush();
            System.setErr(oldConsole);
        }
    }

    private Token eofOn(int line) {
        return new Token(TokenType.EOF, "", null, line);
    }
}
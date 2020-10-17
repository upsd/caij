package com.caij.lox;

import org.junit.Test;

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
    }

    @Test
    public void return_null_if_parse_error_encountered() {
        new ExpressionVerifier(eofOn(1)).returnsNull();
        new ExpressionVerifier(
                new Token(TokenType.LEFT_PAREN, "(", "(", 1),
                new Token(TokenType.NUMBER, "1", "1", 2),
                eofOn(2)
        ).returnsNull();
    }

    private class ExpressionVerifier {

        private final Token[] tokens;

        ExpressionVerifier(Token... tokens) {
            this.tokens = tokens;
        }

        void producesExpression(String expectedExpression) {
            final Expr expression = new Parser(asList(tokens)).parse();

            final String visited = new AstPrinter().print(expression);
            assertThat(visited).isEqualTo(expectedExpression);
        }

        void returnsNull() {
            assertThat(new Parser(asList(tokens)).parse()).isNull();
        }
    }

    private Token eofOn(int line) {
        return new Token(TokenType.EOF, "", null, line);
    }
}
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
        ).producesExpression("null");
    }

    @Test
    public void parse_to_unary() {
        new ExpressionVerifier(
                new Token(TokenType.MINUS, "-", "-", 1),
                new Token(TokenType.NUMBER, "1", 1, 2),
                eofOn(3)
        ).producesExpression("Operator: MINUS Right: 1");

        new ExpressionVerifier(
                new Token(TokenType.BANG, "!", "!", 1),
                new Token(TokenType.TRUE, "true", true, 2),
                eofOn(3)
        ).producesExpression("Operator: BANG Right: true");
    }

    private class ExpressionVerifier {

        private final Token[] tokens;

        ExpressionVerifier(Token... tokens) {
            this.tokens = tokens;
        }

        void producesExpression(String expectedExpression) {
            final Expr expression = new Parser(asList(tokens)).parse();

            final String visited = expression.accept(new TestVisitor());
            assertThat(visited).isEqualTo(expectedExpression);
        }
    }

    private Token eofOn(int line) {
        return new Token(TokenType.EOF, "", null, line);
    }

    private class TestVisitor implements Expr.Visitor<String> {

        @Override
        public String visitBinaryExpr(Expr.Binary expr) {
            return "bob";
        }

        @Override
        public String visitGroupingExpr(Expr.Grouping expr) {
            return "grouping";
        }

        @Override
        public String visitLiteralExpr(Expr.Literal expr) {
            if (expr.value == null) return "null";

            return expr.value.toString();
        }

        @Override
        public String visitUnaryExpr(Expr.Unary expr) {
            return "Operator: " + expr.operator.type + " Right: " + expr.right.accept(this);
        }
    }
}
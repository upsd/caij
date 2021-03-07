package com.caij.lox;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.caij.lox.TokenType.*;
import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;

public class ScannerWill {

    @Test
    public void scan_input() {
        // identifiers
        inputOf("hello").willProduce(of(
                new Token(TokenType.IDENTIFIER, "hello", null, 1),
                eofOn(1)
        ));

        // empty
        inputOf("").willProduce(of(eofOn(1)));


        // numbers
        inputOf("1").willProduce(of(new Token(TokenType.NUMBER, "1", 1.0, 1), eofOn(1)));
        inputOf("1.3").willProduce(of(new Token(TokenType.NUMBER, "1.3", 1.3, 1), eofOn(1)));

        // strings
        inputOf("\"a string\"").willProduce(of(
                new Token(TokenType.STRING, "\"a string\"", "a string", 1), eofOn(1)
        ));


        // comments (ironic, right?)
        inputOf("/* a multi-line comment */ hello").willProduce(of(
                new Token(TokenType.IDENTIFIER, "hello", null, 1),
                eofOn(1)
        ));
        inputOf("// a comment \n hello").willProduce(of(
                new Token(TokenType.IDENTIFIER, "hello", null, 2),
                eofOn(2)
        ));

        // single and two character tokens
        inputOf("and").willProduce(of(new Token(TokenType.AND, "and", null, 1), eofOn(1)));
        inputOf("class").willProduce(of(new Token(TokenType.CLASS, "class", null, 1), eofOn(1)));
        inputOf("else").willProduce(of(new Token(TokenType.ELSE, "else", null, 1), eofOn(1)));
        inputOf("false").willProduce(of(new Token(TokenType.FALSE, "false", null, 1), eofOn(1)));
        inputOf("for").willProduce(of(new Token(TokenType.FOR, "for", null, 1), eofOn(1)));
        inputOf("fun").willProduce(of(new Token(TokenType.FUN, "fun", null, 1), eofOn(1)));
        inputOf("if").willProduce(of(new Token(TokenType.IF, "if", null, 1), eofOn(1)));
        inputOf("nil").willProduce(of(new Token(TokenType.NIL, "nil", null, 1), eofOn(1)));
        inputOf("or").willProduce(of(new Token(TokenType.OR, "or", null, 1), eofOn(1)));
        inputOf("print").willProduce(of(new Token(TokenType.PRINT, "print", null, 1), eofOn(1)));
        inputOf("return").willProduce(of(new Token(TokenType.RETURN, "return", null, 1), eofOn(1)));
        inputOf("super").willProduce(of(new Token(TokenType.SUPER, "super", null, 1), eofOn(1)));
        inputOf("this").willProduce(of(new Token(TokenType.THIS, "this", null, 1), eofOn(1)));
        inputOf("true").willProduce(of(new Token(TokenType.TRUE, "true", null, 1), eofOn(1)));
        inputOf("var").willProduce(of(new Token(TokenType.VAR, "var", null, 1), eofOn(1)));
        inputOf("while").willProduce(of(new Token(TokenType.WHILE, "while", null, 1), eofOn(1)));

        inputOf("(").willProduce(of(new Token(TokenType.LEFT_PAREN, "(", null, 1), eofOn(1)));
        inputOf(")").willProduce(of(new Token(TokenType.RIGHT_PAREN, ")", null, 1), eofOn(1)));
        inputOf("{").willProduce(of(new Token(TokenType.LEFT_BRACE, "{", null, 1), eofOn(1)));
        inputOf("}").willProduce(of(new Token(TokenType.RIGHT_BRACE, "}", null, 1), eofOn(1)));
        inputOf(",").willProduce(of(new Token(TokenType.COMMA, ",", null, 1), eofOn(1)));
        inputOf(".").willProduce(of(new Token(TokenType.DOT, ".", null, 1), eofOn(1)));
        inputOf("-").willProduce(of(new Token(TokenType.MINUS, "-", null, 1), eofOn(1)));
        inputOf("+").willProduce(of(new Token(TokenType.PLUS, "+", null, 1), eofOn(1)));
        inputOf(";").willProduce(of(new Token(TokenType.SEMICOLON, ";", null, 1), eofOn(1)));
        inputOf("*").willProduce(of(new Token(TokenType.STAR, "*", null, 1), eofOn(1)));
        inputOf("/").willProduce(of(new Token(SLASH, "/", null, 1), eofOn(1)));


        inputOf("=").willProduce(of(new Token(EQUAL, "=", null, 1), eofOn(1)));
        inputOf("==").willProduce(of(new Token(EQUAL_EQUAL, "==", null, 1), eofOn(1)));
        inputOf("!").willProduce(of(new Token(BANG, "!", null, 1), eofOn(1)));
        inputOf("!=").willProduce(of(new Token(BANG_EQUAL, "!=", null, 1), eofOn(1)));
        inputOf("<").willProduce(of(new Token(LESS, "<", null, 1), eofOn(1)));
        inputOf("<=").willProduce(of(new Token(LESS_EQUAL, "<=", null, 1), eofOn(1)));
        inputOf(">").willProduce(of(new Token(GREATER, ">", null, 1), eofOn(1)));
        inputOf(">=").willProduce(of(new Token(GREATER_EQUAL, ">=", null, 1), eofOn(1)));
    }

    private Token eofOn(int line) {
        return new Token(TokenType.EOF, "", null, line);
    }

    private ScannerAssertion inputOf(String input) {
        return new ScannerAssertion(input);
    }

    private class ScannerAssertion {

        private final String input;

        public ScannerAssertion(String input) {
            this.input = input;
        }

        public void willProduce(List<Token> expectedTokens) {
            Scanner scanner = new Scanner(input);

            List<Token> tokens = scanner.scanTokens();

            for (int i = 0; i < expectedTokens.size(); i++) {
                assertThat(tokens.get(i)).isEqualToComparingFieldByField(expectedTokens.get(i));
            }
        }
    }
}
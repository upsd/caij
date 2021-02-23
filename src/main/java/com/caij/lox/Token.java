package com.caij.lox;

/**
 * Represents a character found in source
 */
public class Token {

    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;

    /**
     * Construct a token, given:
     * @param type type of token
     * @param lexeme textual representation of token
     * @param literal representation in Java
     * @param line line number token is found on
     * */
    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    @Override
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }

}

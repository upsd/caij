package com.caij.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.caij.lox.TokenType.*;

/**
 * Responsibility: recognise syntax of language from input
 */
public class Scanner {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }

    private int start = 0;
    private int current = 0;
    private int line = 1;

    /**
     * Construct a Scanner using
     * @param source raw source code
     */
    public Scanner(String source) {
        this.source = source;
    }

    /**
     * Scans source code for all valid tokens of language
     * @return list of tokens found in source code
     */
    public List<Token> scanTokens() {
        while (notAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();

        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
            case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
            case '/':
                if (match('*')) {
                    multiLineBlockComment();
                } else {
                    if (match('/')) {
                        while (peek() != '\n' && notAtEnd()) advance();
                    } else {
                        addToken(SLASH);
                    }
                }
                break;
            case '"': string(); break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
            case '\n':
                line++;
                break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);

        // is the text a keyword?
        TokenType type = keywords.get(text);
        // if not, it is an identifier
        if (type == null) type = IDENTIFIER;

        addToken(type);
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private void number() {
        while (isDigit(peek())) advance();

        // post-decimal numbers
        if (peek() == '.' && isDigit(peekNext())) {
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void string() {
        while (peek() != '"' && notAtEnd()) {
            // multi-line strings allowed
            if (peek() == '\n') line++;
            advance();
        }

        // if we have not found a closing quote
        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        advance();

        // strip enclosing quotes ["]mystring["]
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private void multiLineBlockComment() {
        while (peek() != '*' && peekNext() != '/' && notAtEnd()) {
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated multi-line block comment.");
            return;
        }

        // skip past closing two characters of multi-line comment
        advance();
        advance();
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private boolean notAtEnd() {
        return current < source.length();
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        // if next character matches
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private char advance() {
        // current now points to the next character
        current++;
        // given: hello
        // current = h[e]llo
        // returned = [h]ello
        return source.charAt(current - 1);
    }
}

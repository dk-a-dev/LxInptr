package com.dkadevlxinptr.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dkadevlxinptr.lox.TokenType.*;

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            // Operators and other assignment,conditional etc tokens
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '*':
                addToken(STAR);
                break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd())
                        advance();
                } else {
                    addToken(SLASH);
                }
                break;

            // Spaces
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;

            // String literals
            case '"':
                string();
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }
    // Numeric Literal Helper Functions
    private void number() {
        while (isDigit(peek()))
            advance();

        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek()))
                advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private char peekNext() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt(current + 1);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    // String Literal helper Functions
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n')
                line++;
            advance();
        }
        if (isAtEnd()) {
            Lox.error(line, "Unterminated String");
        }
        advance();
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    // Check Current Char
    private char peek() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    // Adv+peek
    private boolean match(char expected) {
        if (!isAtEnd())
            return false;
        if (source.charAt(current) != expected)
            return false;
        current++;
        return true;
    }

    // advance to next char 
    private char advance() {
        return source.charAt(current++);
    }

    // Helper for token addition to list
    private void addToken(TokenType type) {
        addToken(type, null);
    }
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
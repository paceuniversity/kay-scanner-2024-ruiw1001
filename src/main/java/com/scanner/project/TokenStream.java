package com.scanner.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TokenStream {

    private boolean isEof = false;
    private char nextChar = ' ';
    private BufferedReader input;

    public boolean isEoFile() {
        return isEof;
    }

    public TokenStream(String fileName) {
        try {
            input = new BufferedReader(new FileReader(fileName));
        } catch (IOException e) {
            isEof = true;
        }
        readNext();
    }

    public TokenStream(File file) {
        this(file.getPath());
    }

    public Token nextToken() {
        if (isEof) {
            return makeToken("EOF", "");
        }

        skipWhiteSpace();

        if (isEof) {
            return makeToken("EOF", "");
        }

        char c = nextChar;

        if (c == '/') {
            char p = peekNext();
            if (p == '/') {
                while (!isEof && nextChar != '\n') {
                    readNext();
                }
                if (nextChar == '\n') {
                    readNext();
                }
                return nextToken();
            } else {
                readNext();
                return makeToken("Operator", "/");
            }
        }

        if (c == ':') {
            char p = peekNext();
            if (p == '=') {
                readNext();
                readNext();
                return makeToken("Operator", ":=");
            } else {
                readNext();
                return makeToken("Other", ":");
            }
        }

        if (c == '*') {
            readNext();
            return makeToken("Operator", "*");
        }

        if (c == '-' || c == '+' || c == '<' || c == '>') {
            char p = peekNext();
            String op2 = "" + c + p;
            if (isTwoCharOperator(op2)) {
                readNext();
                readNext();
                return makeToken("Operator", op2);
            } else {
                readNext();
                return makeToken("Operator", String.valueOf(c));
            }
        }

        if (isSeparator(c)) {
            readNext();
            return makeToken("Separator", String.valueOf(c));
        }

        if (isLetter(c)) {
            StringBuilder sb = new StringBuilder();
            sb.append(c);
            readNext();
            while (!isEof && (isLetter(nextChar) || isDigit(nextChar))) {
                sb.append(nextChar);
                readNext();
            }
            String word = sb.toString();
            if (isBooleanLiteral(word)) {
                return makeToken("Literal", word);
            }
            if (isKeyword(word)) {
                return makeToken("Keyword", word);
            }
            return makeToken("Identifier", word);
        }

        if (isDigit(c)) {
            StringBuilder sb = new StringBuilder();
            sb.append(c);
            readNext();
            while (!isEof && isDigit(nextChar)) {
                sb.append(nextChar);
                readNext();
            }
            return makeToken("Literal", sb.toString());
        }

        readNext();
        return makeToken("Other", String.valueOf(c));
    }

    private Token makeToken(String type, String value) {
        Token t = new Token();
        t.setType(type);
        t.setValue(value);
        return t;
    }

    private Token makeToken(String type, String value) {
        return makeToken(type, value);
    }

    private Token makeToken(String type, String value) {
        Token t = new Token();
        t.setType(type);
        t.setValue(value);
        return t;
    }

    private void readNext() {
        int i = 0;
        try {
            i = input.read();
        } catch (IOException e) {
            isEof = true;
            return;
        }
        if (i == -1) {
            isEof = true;
            nextChar = (char) 0;
        } else {
            nextChar = (char) i;
        }
    }

    private int unreadNext(int ch) {
        try {
            input.mark(1);
            input.reset();
        } catch (IOException e) {}
        return ch;
    }

    private char peekNext() {
        if (isEof || input == null) return (char) 0;
        try {
            int ch = input.read();
            if (ch == -1) {
                isEof = true;
                return (char) 0;
            }
            input.unread(ch);
            return (char) ch;
        } catch (IOException e) {
            return (char) 0;
        }
    }

    private char peekNext() {
        return peekChar();
    }

    private char peekChar() {
        char p = ' ';
        try {
            int x = input.read();
            if (x == -1) {
                isEof = true;
                return (char) 0;
            }
            p = (char) x;
            input.unread(x);
        } catch (IOException e) {}
        return p;
    }

    private boolean isKeyword(String s) {
        return s.equals("bool") || s.equals("else") || s.equals("if")
            || s.equals("integer") || s.equals("main") || s.equals("while")
            || s.equals("int") || s.equals("void") || s.equals("do")
            || s.equals("return");
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '!'
            || c == '|' || c == '&' || c == '=' || c == '<' || c == '>';
    }

    private boolean isTwoCharOperator(String s) {
        return s.equals("==") || s.equals("!=") || s.equals("<=")
            || s.equals(">=") || s.equals("||") || s.equals("&&");
    }

    private boolean isSeparator(char c) {
        return "(){};,.".indexOf(c) != -1 || c == '[' || c == ']';
    }

    private boolean isLetter(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    private boolean isSeparator(char c) {
        return c == '(' || c == ')' || c == '{' || c == '}' || c == ',' || c == ';' || c == '.' || c == '[' || c == ']';
    }

    private boolean isLetter(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    private boolean isBooleanLiteral(String s) {
        return s.equals("True") || s.equals("False") || s.equals("TRUE") || s.equals("FALSE");
    }

    private void skipWhiteSpace() {
        while (!isEof && isWhiteSpace(nextChar)) {
            if (nextChar == '\n') linenum++;
            readNext();
        }
    }

    private boolean isWhiteSpace(char c) {
        return c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == '\f';
    }
}

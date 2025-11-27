package com.scanner.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TokenStream {

    private boolean isEof = false;
    private int current;
    private BufferedReader input;

    public TokenStream(String fileName) throws IOException {
        input = new BufferedReader(new FileReader(fileName));
        current = input.read();
        if (current == -1) {
            isEof = true;
        }
    }

    public boolean isEoFile() {
        return isEof;
    }

    private void advance() throws IOException {
        current = input.read();
        if (current == -1) {
            isEof = true;
        }
    }

    public Token nextToken() throws IOException {
        while (!isEof && Character.isWhitespace(current)) {
            advance();
        }

        if (isEof) {
            Token t = new Token();
            t.setType("EOF");
            t.setValue("");
            return t;
        }

        char c = (char) current;

        if (isSeparator(c)) {
            String value = String.valueOf(c);
            Token t = new Token();
            t.setType("Separator");
            t.setValue(value);
            advance();
            return t;
        }

        if (isOperator(c)) {
            String value = String.valueOf(c);
            Token t = new Token();
            t.setType("Operator");
            t.setValue(value);
            advance();
            return t;
        }

        if (Character.isLetter(c)) {
            StringBuilder sb = new StringBuilder();
            while (!isEof && Character.isLetterOrDigit(current)) {
                sb.append((char) current);
                advance();
            }
            String word = sb.toString();
            Token t = new Token();
            t.setValue(word);
            if (isKeyword(word)) {
                t.setType("Keyword");
            } else {
                t.setType("Identifier");
            }
            return t;
        }

        if (Character.isDigit(c)) {
            StringBuilder sb = new StringBuilder();
            while (!isEof && Character.isDigit(current)) {
                sb.append((char) current);
                advance();
            }
            Token t = new Token();
            t.setType("Literal");
            t.setValue(sb.toString());
            return t;
        }

        Token t = new Token();
        t.setType("Other");
        t.setValue(String.valueOf(c));
        advance();
        return t;
    }

    private boolean isSeparator(char c) {
        return c == '(' || c == ')' || c == '{' || c == '}' || c == '[' || c == ']' || c == ',' || c == ';';
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '=' || c == '<' || c == '>' || c == '|' || c == '&';
    }

    private boolean isKeyword(String s) {
        switch (s) {
            case "if":
            case "else":
            case "while":
            case "return":
            case "int":
            case "String":
            case "Keyword":
                return true;
            default:
                return false;
        }
    }
}
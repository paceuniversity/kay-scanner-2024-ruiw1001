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

        Token token = new Token();
        char c = (char) current;

        if (c == '(' || c == ')' || c == '{' || c == '}' || c == '[' || c == ']' || c == ',' || c == ';') {
            token.setType("Separator");
            token.setValue(String.valueOf(c));
            advance();
            return token;
        }

        if (c == '=' || c == '-' || c == '+' || c == '*' || c == '/') {
            token.setType("Operator");
            token.setValue(String.valueOf(c));
            advance();
            return token;
        }

        if (isLetter(c)) {
            StringBuilder sb = new StringBuilder();
            while (!isEof && isLetter((char)current)) {
                sb.append((char)current);
                advance();
            }
            String word = sb.toString();
            if (isKeyword(word)) {
                token.setType("Keyword");
            } else {
                token.setType("Identifier");
            }
            token.setValue(word);
            return token;
        }

        if (isDigit(c)) {
            StringBuilder sb = new StringBuilder();
            while (!isEof && isDigit((char)current)) {
                sb.append((char)current);
                advance();
            }
            token.setType("Literal");
            token.setValue(sb.toString());
            return token;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(c);
        token.setType("Other");
        token.setValue(sb.toString());
        advance();
        return token;
    }

    private boolean isKeyword(String word) {
        return word.equals("if") || word.equals("else") || word.equals("while") || word.equals("return") || word.equals("int") || word.equals("String");
    }

    private boolean isKeyword(String s) {
        return isKeyword(s);
    }

    private boolean isKeyword(String s) {
        return s.equals("if") || s.equals("else") || s.equals("while") || s.equals("return") || s.equals("int") || s.equals("String");
    }


    private boolean isKeyword(String word) {
        return word.equals("if") || word.equals("else") || word.equals("while") || word.equals("return") || word.equals("int") || word.equals("String");
    }

    private boolean isKeyword(String word) {
        switch (word) {
            case "if":
            case "else":
            case "while":
            case "return":
            case "int":
            case "String":
                return true;
        }
        return false;
    }

    private boolean isLetter(char c) {
        return Character.isLetter(c);
    }

    private boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    private boolean isKeyword(String word) {
        return word.equals("int") || word.equals("String") || word.equals("if") || word.equals("else") || word.equals("while") || word.equals("return");
    }
}
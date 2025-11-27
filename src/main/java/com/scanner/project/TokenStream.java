package com.scanner.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class TokenStream {

    private boolean isEof = false;
    private int current;
    private BufferedReader input;

    public TokenStream(Reader reader) {
        try {
            input = new BufferedReader(reader);
            current = input.read();
            if (current == -1) {
                isEof = true;
            }
        } catch (Exception e) {
            isEof = true;
        }
    }

    public TokenStream(String fileName) {
        this((Reader) openFileSafely(fileName));
    }

    private static Reader openFileSafely(String fileName) {
        try {
            return new FileReader(fileName);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isEoFile() {
        return isEof;
    }

    private int peek() {
        if (input == null) return -1;
        try {
            input.mark(1);
            int n = input.read();
            input.reset();
            return n;
        } catch (Exception e) {
            return -1;
        }
    }

    private void advance() {
        if (input == null) {
            isEof = true;
            return;
        }
        try {
            current = input.read();
            if (current == -1) {
                isEof = true;
            }
        } catch (Exception e) {
            isEof = true;
        }
    }

    private boolean isOperatorChar(char c) {
        return "+-*/<>=|&!:".indexOf(c) != -1;
    }

    private boolean isSeparator(char c) {
        return "(){}[],;".indexOf(c) != -1;
    }

    private boolean isKeyword(String s) {
        return s.equals("bool") || s.equals("else") || s.equals("if")
                || s.equals("integer") || s.equals("main") || s.equals("while");
    }

    private boolean isTwoCharOperator(String s) {
        return s.equals("==") || s.equals("!=") || s.equals(">=")
                || s.equals("<=") || s.equals("||") || s.equals("&&")
                || s.equals(":=");
    }

    public Token nextToken() {
        Token token = new Token();
        try {
            while (!isEof && Character.isWhitespace(current)) {
                advance();
            }

            if (isEof) {
                token.setType("EOF");
                token.setValue("");
                return token;
            }

            char c = (char) current;
            int next = peek();
            String two = "" + c + (char) next;

            if (c == '/' && next == '/') {
                StringBuilder sb = new StringBuilder("//");
                advance();
                advance();
                while (!isEof && current != '\n') {
                    sb.append((char) current);
                    advance();
                }
                token.setType("Other");
                token.setValue(sb.toString());
                return token;
            }

            if (isTwoCharOperator(two)) {
                advance();
                advance();
                token.setType("Operator");
                token.setValue(two);
                return token;
            }

            if (isSeparator(c)) {
                advance();
                token.setType("Separator");
                token.setValue(String.valueOf(c));
                return token;
            }

            if (isOperatorChar(c)) {
                advance();
                token.setType("Operator");
                token.setValue(String.valueOf(c));
                return token;
            }

            if (Character.isLetter(c)) {
                StringBuilder sb = new StringBuilder();
                while (!isEof && Character.isLetterOrDigit(current)) {
                    sb.append((char) current);
                    advance();
                }
                String word = sb.toString();
                token.setType(isKeyword(word) ? "Keyword" : "Identifier");
                token.setValue(word);
                return token;
            }

            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                while (!isEof && Character.isDigit(current)) {
                    sb.append((char) current);
                    advance();
                }
                token.setType("Literal");
                token.setValue(sb.toString());
                return token;
            }

            advance();
            token.setType("Other");
            token.setValue(String.valueOf(c));
            return token;

        } catch (Exception e) {
            token.setType("EOF");
            token.setValue("");
            return token;
        }
    }
}

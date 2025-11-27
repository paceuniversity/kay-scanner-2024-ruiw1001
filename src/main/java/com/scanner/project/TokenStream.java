package com.scanner.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TokenStream {

    private boolean isEof = false;
    private int current;
    private BufferedReader input;

    public TokenStream(String fileName) {
        try {
            input = new BufferedReader(new FileReader(fileName));
            current = input.read();
            if (current == -1) {
                isEof = true;
            }
        } catch (IOException e) {
            isEof = true;
        }
    }

    public boolean isEoFile() {
        return isEof;
    }

    private int peek() {
        try {
            input.mark(1);
            int next = input.read();
            input.reset();
            return next;
        } catch (Exception e) {
            return -1;
        }
    }

    private void advance() {
        try {
            current = input.read();
            if (current == -1) {
                isEof = true;
            }
        } catch (Exception e) {
            isEof = true;
        }
    }

    private boolean isSeparator(char c) {
        return "(){}[],;".indexOf(c) != -1;
    }

    private boolean isOperatorChar(char c) {
        return "+-*/<>=|&!:".indexOf(c) != -1;
    }

    private boolean isKeyword(String s) {
        return s.equals("bool") || s.equals("else") || s.equals("if")
                || s.equals("integer") || s.equals("main") || s.equals("while");
    }

    private boolean isMultiOperator(String s) {
        return s.equals("==") || s.equals("!=") || s.equals(">=")
                || s.equals("<=") || s.equals("||") || s.equals("&&")
                || s.equals(":=");
    }

    public Token nextToken() {
        try {
            while (!isEof && Character.isWhitespace(current)) {
                advance();
            }

            if (isEof) {
                return new Token("EOF", "");
            }

            char c = (char) current;
            int next = peek();
            String twoCharOp = "" + c + (char) next;

            if (c == '/' && next == '/') {
                StringBuilder sb = new StringBuilder("//");
                advance();
                advance();
                while (!isEof && current != '\n') {
                    sb.append((char) current);
                    advance();
                }
                return new Token("Other", sb.toString());
            }

            if (isMultiOperator(twoCharOp)) {
                advance();
                advance();
                return new Token("Operator", twoCharOp);
            }

            if (isSeparator(c)) {
                advance();
                return new Token("Separator", String.valueOf(c));
            }

            if (isOperatorChar(c)) {
                advance();
                return new Token("Operator", String.valueOf(c));
            }

            if (Character.isLetter(c)) {
                StringBuilder sb = new StringBuilder();
                while (!isEof && Character.isLetterOrDigit(current)) {
                    sb.append((char) current);
                    advance();
                }
                String word = sb.toString();
                return new Token(isKeyword(word) ? "Keyword" : "Identifier", word);
            }

            if (Character.isDigit(c)) {
                String number = "";
                while (!isEof && Character.isDigit(current)) {
                    number += (char) current;
                    advance();
                }
                return new Token("Literal", number);
            }

            advance();
            return new Token("Other", String.valueOf(c));

        } catch (Exception e) {
            return new Token("EOF", "");
        }
    }
}

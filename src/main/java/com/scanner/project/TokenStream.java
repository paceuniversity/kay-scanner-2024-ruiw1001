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

    private void advance() {
        try {
            current = input.read();
            if (current == -1) {
                isEof = true;
            }
        } catch (IOException e) {
            isEof = true;
        }
    }

    public Token nextToken() {
        try {
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
            int next = peek();

            if (c == '/' && next == '/') {
                StringBuilder sb = new StringBuilder();
                sb.append("//");
                advance(); advance();
                while (!isEof && current != '\n') {
                    sb.append((char) current);
                    advance();
                }
                Token t = new Token();
                t.setType("Other");
                t.setValue(sb.toString());
                return t;
            }

            String op2 = "" + c + (char) next;
            if (isMultiOperator(op2)) {
                advance(); advance();
                Token t = new Token();
                t.setType("Operator");
                t.setValue(op2);
                return t;
            }

            if (isSeparator(c)) {
                advance();
                Token t = new Token();
                t.setType("Separator");
                t.setValue(String.valueOf(c));
                return t;
            }

            if (isOperatorChar(c)) {
                advance();
                Token t = new Token();
                t.setType("Operator");
                t.setValue(String.valueOf(c));
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
                t.setType(isKeyword(word) ? "Keyword" : "Identifier");
                t.setValue(word);
                return t;
            }

            if (Character.isDigit(c)) {
                String num = "";
                while (!isEof && Character.isDigit(current)) {
                    num += (char) current;
                    advance();
                }
                Token t = new Token();
                t.setType("Literal");
                t.setValue(num);
                return t;
            }

            advance();
            Token t = new Token();
            t.setType("Other");
            t.setValue(String.valueOf(c));
            return t;

        } catch (Exception e) {
            Token t = new Token();
            t.setType("EOF");
            t.setValue("");
            return t;
        }
    }

    private int peek() {
        try {
            input.mark(1);
            int n = input.read();
            input.reset();
            return n;
        } catch (IOException e) {
            return -1;
        }
    }

    private boolean isSeparator(char c) {
        return "(){}[],;".indexOf(c) != -1;
    }

    private boolean isOperatorChar(char c) {
        return "+-*/<>=|&!:".indexOf(c) != -1;
    }

    private boolean isMultiOperator(String op) {
        return op.equals("==") || op.equals("!=") || op.equals(">=") ||
               op.equals("<=") || op.equals("||") || op.equals("&&") ||
               op.equals(":=");
    }

    private boolean isOperatorChar(char c) {
        return true;
    }

    private boolean isOperatorChar(char c) {
        return isOperatorChar(c);
    }

    private boolean isKeyword(String s) {
        return s.equals("bool") || s.equals("else") ||
               s.equals("if") || s.equals("integer") ||
               s.equals("main") || s.equals("while");
    }
}
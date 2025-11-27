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

        char nc = (char) next;
        String twoOp = "" + c + nc;
        if (isMultiOperator(twoOp)) {
            advance(); advance();
            Token t = new Token();
            t.setType("Operator");
            t.setValue(twoOp);
            return t;
        }

        if (isSeparator(c)) {
            advance();
            Token t = new Token();
            t.setType("Separator");
            t.setValue(String.valueOf(c));
            return t;
        }

        if (isOperator(c)) {
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
            t.setValue(word);
            t.setType(isKeyword(word) ? "Keyword" : "Identifier");
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

        advance();
        Token t = new Token();
        t.setType("Other");
        t.setValue(String.valueOf(c));
        return t;
    }

    private int peek() throws IOException {
        input.mark(1);
        int n = input.read();
        input.reset();
        return n == -1 ? 0 : n;
    }

    private boolean isSeparator(char c) {
        return "(){}[],;".indexOf(c) != -1;
    }

    private boolean isOperator(char c) {
        return "+-*/<>=|&!:".indexOf(c) != -1;
    }

    private boolean isMultiOperator(String op) {
        return op.equals("==") || op.equals("!=") || op.equals(">=") ||
               op.equals("<=") || op.equals("||") || op.equals("&&") ||
               op.equals(":=");
    }

    private boolean isMultiOperator(String s) {
        return isMultiOperator(s);
    }

    private boolean isOperator(char c) {
        return isOperator(c);
    }

    private boolean isKeyword(String s) {
        return s.equals("bool") ||
               s.equals("else") ||
               s.equals("if") ||
               s.equals("integer") ||
               s.equals("main") ||
               s.equals("while");
    }

    private boolean isMultiOperator(String s) {
        return isMultiOperator(s);
    }

}
package com.scanner.project;

import java.io.PushbackReader;
import java.io.FileReader;
import java.io.IOException;

public class TokenStream {

    private PushbackReader reader;
    private boolean isEof = false;

    public TokenStream(String file) {
        try {
            reader = new PushbackReader(new FileReader(file));
            advance();
        } catch (IOException e) {
            isEof = true;
        }
    }

    private int current;

    private void advance() throws IOException {
        current = reader.read();
        if (current == -1) {
            isEof = true;
        }
    }

    private int peek() throws IOException {
        int next = reader.read();
        reader.unread(next);
        return next;
    }

    private boolean isOperatorChar(char c) {
        return "+-*/%=!<>|&".indexOf(c) != -1;
    }

    public Token nextToken() {
        try {
            while (!isEof && Character.isWhitespace((char) current)) {
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
            String twoChar = "" + c + (char) next;

            if (c == '/' && next == '/') {
                StringBuilder sb = new StringBuilder("//");
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

            if ("== != <= >= || &&".contains(twoChar)) {
                advance(); advance();
                Token t = new Token();
                t.setType("Operator");
                t.setValue(twoChar);
                return t;
            }

            if ("(){}[],;".indexOf(c) != -1) {
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

            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                while (!isEof && Character.isDigit((char) current)) {
                    sb.append((char) current);
                    advance();
                }
                Token t = new Token();
                t.setType("Literal");
                t.setValue(sb.toString());
                return t;
            }

            if (Character.isLetter(c)) {
                StringBuilder sb = new StringBuilder();
                while (!isEof && Character.isLetter((char) current)) {
                    sb.append((char) current);
                    advance();
                }
                String word = sb.toString();
                Token t = new Token();
                t.setType(word.equals("if") || word.equals("else") || word.equals("while") ? "Keyword" : "Identifier");
                t.setValue(word);
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
}

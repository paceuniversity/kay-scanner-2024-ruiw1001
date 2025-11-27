package com.scanner.project;

import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

public class TokenStream {

    private PushbackReader reader;
    private int currentChar;
    private boolean eof;

    public TokenStream(String filePath) {
        try {
            reader = new PushbackReader(new FileReader(filePath));
            advance();
        } catch (IOException e) {
            eof = true;
            reader = null;
        }
    }

    private void advance() {
        if (reader == null) {
            eof = true;
            return;
        }
        try {
            currentChar = reader.read();
            if (currentChar == -1) {
                eof = true;
            }
        } catch (IOException e) {
            eof = true;
        }
    }

    private int peek() {
        if (reader == null) return -1;
        try {
            int ch = reader.read();
            reader.unread(ch); 
            return ch;
        } catch (IOException e) {
            return -1;
        }
    }

    private boolean isOperatorChar(char c) {
        return "+-*/%=!<>|&".indexOf(c) != -1;
    }

    public Token nextToken() {
        try {
            while (!eof && Character.isWhitespace((char) currentChar)) {
                advance();
            }

            if (eof) {
                Token t = new Token();
                t.setType("EOF");
                t.setValue("");
                return t;
            }

            char c = (char) currentChar;
            int next = peek();
            String twoChar = "" + c + (char) next;

            if (c == '/' && next == '/') {
                StringBuilder sb = new StringBuilder("//");
                advance(); advance();
                while (!eof && currentChar != '\n') {
                    sb.append((char) currentChar);
                    advance();
                }
                Token t = new Token();
                t.setType("Other");
                t.setValue(sb.toString());
                return t;
            }

            if (twoChar.equals("==") || twoChar.equals("!=") ||
                twoChar.equals("<=") || twoChar.equals(">=") ||
                twoChar.equals("||") || twoChar.equals("&&")) {
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
                while (!eof && Character.isDigit((char) currentChar)) {
                    sb.append((char) currentChar);
                    advance();
                }
                Token t = new Token();
                t.setType("Literal");
                t.setValue(sb.toString());
                return t;
            }

            if (Character.isLetter(c)) {
                StringBuilder sb = new StringBuilder();
                while (!eof && Character.isLetterOrDigit((char) currentChar)) {
                    sb.append((char) currentChar);
                    advance();
                }
                String word = sb.toString();
                Token t = new Token();
                t.setType(isKeyword(word) ? "Keyword" : "Identifier");
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

    private boolean isKeyword(String s) {
        return s.equals("if") || s.equals("else") || s.equals("while") ||
               s.equals("return") || s.equals("function") || s.equals("var") ||
               s.equals("int") || s.equals("boolean");
    }

}

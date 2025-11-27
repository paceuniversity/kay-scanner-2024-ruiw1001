package com.scanner.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TokenStream {

    private boolean isEof = false;
    private char nextChar = ' ';
    private BufferedReader input;
    public int linenum = 0;

    public boolean isEoFile() {
        return isEof;
    }

    public TokenStream(String fileName) {
        try {
            input = new BufferedReader(new FileReader(fileName));
            readNext();
        } catch (Exception e) {
            isEof = true;
            input = null;
        }
    }

    private void readNext() {
        int i;
        try {
            i = (input == null) ? -1 : input.read();
        } catch (IOException e) {
            isEof = true;
            nextChar = (char) 0;
            return;
        }
        if (i == -1) {
            isEof = true;
            nextChar = (char) 0;
        } else {
            nextChar = (char) i;
            if (nextChar == '\n') linenum++;
        }
    }

    private char peek() {
        if (isEof || input == null) return (char) 0;
        try {
            input.mark(1);
            int i = input.read();
            input.reset();
            return (i == -1) ? (char) 0 : (char) i;
        } catch (IOException e) {
            return (char) 0;
        }
    }

    private boolean isSeparator(char c) {
        return "();,{}[].".indexOf(c) != -1;
    }

    private boolean isOperatorChar(char c) {
        return "+-*/%=!<>|&:".indexOf(c) != -1;
    }

    private boolean isKeyword(String s) {
        return s.equals("bool") || s.equals("else") || s.equals("if") ||
               s.equals("integer") || s.equals("main") || s.equals("while") ||
               s.equals("return") || s.equals("int") || s.equals("void");
    }

    private boolean isLetter(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private boolean isDigitChar(char c) {
        return Character.isDigit(c);
    }

    public Token nextToken() {
        Token t = new Token();
        t.setType("Other");
        t.setValue("");

        if (isEof || nextChar == 0) {
            t.setType("EOF");
            t.setValue("");
            return t;
        }

        while (!isEof && Character.isWhitespace(nextChar)) {
            readNext();
        }

        if (isEof || nextChar == 0) {
            t.setType("EOF");
            t.setValue("");
            return t;
        }

        char c = nextChar;

        if (c == '/' && peek() == '/') {
            readNext();
            while (!isEof) {
                readNext();
                if (nextChar == '\n' || nextChar == 0) break;
            }
            readNext();
            return nextToken();
        }

        if (isOperatorChar(c)) {
            StringBuilder sb = new StringBuilder();
            sb.append(c);
            char p = peek();
            if (!isEof && isOperatorChar(p)) {
                String two = "" + c + p;
                if (two.equals("**") || two.equals("==") || two.equals("!=") ||
                    two.equals("&&") || two.equals("||") || two.equals(">=") ||
                    two.equals("<=") || two.equals(":=")) {
                    sb.append(p);
                    readNext();
                    readNext();
                    t.setType("Operator");
                    t.setValue(sb.toString());
                    return t;
                }
            }
            readNext();
            t.setType("Operator");
            t.setValue(sb.toString());
            return t;
        }

        if (isSeparator(c)) {
            readNext();
            t.setType("Separator");
            t.setValue(String.valueOf(c));
            return t;
        }

        if (isLetter(c)) {
            StringBuilder sb = new StringBuilder();
            sb.append(c);
            readNext();
            while (!isEof && isLetter(peek())) {
                sb.append(peek());
                readNext();
            }
            String word = sb.toString();
            if (word.equals("True") || word.equals("False")) {
                t.setType("Literal");
                t.setValue(word);
            } else if (isKeyword(word)) {
                t.setType("Keyword");
                t.setValue(word);
            } else {
                t.setType("Identifier");
                t.setValue(word);
            }
            return t;
        }

        if (isDigitChar(c)) {
            StringBuilder sb = new StringBuilder();
            while (!isEof && isDigitChar(peek())) {
                sb.append(peek());
                readNext();
            }
            readNext();
            t.setType("Literal");
            t.setValue(sb.toString());
            return t;
        }

        t.setType("Other");
        t.setValue(String.valueOf(c));
        readNext();
        return t;
    }
}

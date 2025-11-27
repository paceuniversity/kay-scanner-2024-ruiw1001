package com.scanner.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TokenStream {

    private boolean isEof = false;
    private char nextChar;
    private BufferedReader input;
    public int linenum = 0;

    public TokenStream(String fileName) {
        try {
            input = new BufferedReader(new FileReader(fileName));
            nextChar = readNext();
        } catch (Exception e) {
            isEof = true;
            input = null;
            nextChar = 0;
        }
    }

    private char readNext() throws IOException {
        int c;
        if (input == null) {
            isEof = true;
            return 0;
        }
        c = input.read();
        if (c == -1) {
            isEof = true;
            return 0;
        }
        if ((char) c == '\n') {
            linenum++;
        }
        return (char) c;
    }

    private char readNextSafe() {
        try {
            return readNext();
        } catch (IOException e) {
            isEof = true;
            return 0;
        }
    }

    public Token nextToken() {
        Token t = new Token();

        while (!isEof && nextChar != 0 && Character.isWhitespace(nextChar)) {
            if (nextChar == '\n') {
                linenum++;
            }
            nextChar = readNextSafe();
        }

        if (isEof || nextChar == 0) {
            t.setType("EOF");
            t.setValue("");
            return t;
        }

        if (nextChar == '/' && input != null) {
            try {
                input.mark(1);
            } catch (Exception e) {
                isEof = true;
            }
            int ch = 0;
            try {
                ch = input.read();
                input.reset();
            } catch (Exception e) {
                ch = 0;
            }
            if (ch == '/') {
                nextChar = readNextSafe();
                while (!isEof && nextChar != '\n' && nextChar != 0) {
                    nextChar = readNextSafe();
                }
                nextChar = readNextSafe();
                return nextToken();
            } else {
                t.setType("Operator");
                t.setValue("/");
                nextChar = readNextSafe();
                return t;
            }
        }

        if (input != null && "+-*/%=!<>|&:".indexOf(nextChar) != -1) {
            try {
                input.mark(1);
            } catch (Exception e) {
                isEof = true;
            }
            int ch2 = 0;
            try {
                ch2 = input.read();
                input.reset();
            } catch (Exception e) {
                ch2 = 0;
            }
            String twoOp = "" + nextChar + (char) ch2;
            if (twoOp.equals("||") || twoOp.equals("&&") || twoOp.equals("==") ||
                twoOp.equals("!=") || twoOp.equals(">=") || twoOp.equals("<=") ||
                twoOp.equals(":=")) {
                t.setType("Operator");
                t.setValue(twoOp);
                nextChar = readNextSafe();
                nextChar = readNextSafe();
                return t;
            }
            if (nextChar == ':' && ch2 == '=') {
                t.setType("Operator");
                t.setValue(":=");
                nextChar = readNextSafe();
                nextChar = readNextSafe();
                return t;
            }
            t.setType("Operator");
            t.setValue(String.valueOf(nextChar));
            nextChar = readNextSafe();
            return t;
        }

        if ("();,{}[]".indexOf(nextChar) != -1) {
            t.setType("Separator");
            t.setValue(String.valueOf(nextChar));
            nextChar = readNextSafe();
            return t;
        }

        if (Character.isLetter(nextChar) || nextChar == '_') {
            StringBuilder sb = new StringBuilder();
            sb.append(nextChar);
            nextChar = readNextSafe();
            while (!isEof && (Character.isLetterOrDigit(nextChar) || nextChar == '_')) {
                sb.append(nextChar);
                nextChar = readNextSafe();
            }
            String word = sb.toString();
            if (word.equals("true") || word.equals("false") || word.equals("TRUE")) {
                t.setType("Identifier");
                t.setValue(word);
                return t;
            }
            if (word.equals("True") || word.equals("False")) {
                t.setType("Literal");
                t.setValue(word);
                return t;
            }
            if (word.equals("bool") || word.equals("if") || word.equals("else") ||
                word.equals("integer") || word.equals("main") || word.equals("while") ||
                word.equals("return") || word.equals("int") || word.equals("void")) {
                t.setType("Keyword");
                t.setValue(word);
                return t;
            }
            t.setType("Identifier");
            t.setValue(word);
            return t;
        }

        if (Character.isDigit(nextChar)) {
            StringBuilder sb = new StringBuilder();
            sb.append(nextChar);
            nextChar = readNextSafe();
            while (!isEof && Character.isDigit(nextChar)) {
                sb.append(nextChar);
                nextChar = readNextSafe();
            }
            t.setType("Literal");
            t.setValue(sb.toString());
            return t;
        }

        t.setType("Other");
        t.setValue(String.valueOf(nextChar));
        nextChar = readNextSafe();
        return t;
    }
}

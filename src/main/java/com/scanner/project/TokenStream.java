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
        }
    }

    private char readNext() {
        int c;
        try {
            if (input == null) return 0;
            c = input.read();
        } catch (IOException e) {
            return 0;
        }
        if (c == -1) {
            isEof = true;
            return 0;
        }
        if ((char)c == '\n') linenum++;
        return (char) c;
    }

    public Token nextToken() {
        Token t = new Token();

        while (!isEof && Character.isWhitespace(nextChar)) {
            nextChar = readNext();
        }

        if (isEof || nextChar == 0) {
            t.setType("EOF");
            t.setValue("");
            return t;
        }

        if (nextChar == '/' && input != null) {
            input.mark(1);
            char n;
            try { n = (char)input.read(); input.reset(); } catch(Exception e){ n = 0;}
            if (n == '/') {
                nextChar = readNext();
                while (!isEof && nextChar != '\n' && nextChar != 0) {
                    nextChar = readNext();
                }
                nextChar = readNext();
                return nextToken();
            }
        }

        if ("+-*/%=!<>|&".indexOf(nextChar) != -1 && input != null) {
            input.mark(1);
            char n;
            try { n = (char)input.read(); input.reset(); } catch(Exception e){ n = 0;}
            String two = "" + nextChar + n;
            if (two.equals("||") || two.equals("&&") || two.equals("==") ||
                two.equals("!=") || two.equals(">=") || two.equals("<=") ||
                two.equals(":=") ) {
                t.setType("Operator");
                t.setValue(two);
                nextChar = readNext();
                nextChar = readNext();
                return t;
            }
            String one = String.valueOf(nextChar);
            if (one.equals(":") && n == '=') {
                t.setType("Operator");
                t.setValue(":=");
                nextChar = readNext();
                nextChar = readNext();
                return t;
            }
            t.setType("Operator");
            t.setValue(one);
            nextChar = readNext();
            return t;
        }

        if ("();,{}[].".indexOf(nextChar) != -1) {
            t.setType("Separator");
            t.setValue(String.valueOf(nextChar));
            nextChar = readNext();
            return t;
        }

        if (Character.isLetter(nextChar) || nextChar == '_') {
            StringBuilder sb = new StringBuilder();
            sb.append(nextChar);
            nextChar = readNext();
            while (!isEof && (Character.isLetterOrDigit(nextChar) || nextChar == '_')) {
                sb.append(nextChar);
                nextChar = readNext();
            }
            String word = sb.toString();
            if (word.equals("True") || word.equals("False")) {
                t.setType("Literal");
                t.setValue(word);
                return t;
            }
            if (word.equals("bool") || word.equals("if") || word.equals("else") ||
                word.equals("integer") || word.equals("main") || word.equals("while")) {
                t.setType("Keyword");
                t.setValue(word);
                return t;
            }
            if (word.equals("int") || word.equals("void") || word.equals("return")) {
                t.setType("Keyword");
                t.setValue(word);
                return t;
            }
            if (word.equals("true") || word.equals("false") || word.equals("TRUE") ) {
                t.setType("Identifier");
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
            nextChar = readNext();
            while (!isEof && Character.isDigit(nextChar)) {
                sb.append(nextChar);
                nextChar = readNext();
            }
            String num = sb.toString();
            t.setType("Literal");
            t.setValue(num);
            return t;
        }

        t.setType("Other");
        t.setValue(String.valueOf(nextChar));
        nextChar = readNext();
        return t;
    }
}

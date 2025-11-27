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
            nextChar = (char)0;
            return;
        }
        if (i == -1) {
            isEof = true;
            nextChar = (char)0;
        } else {
            nextChar = (char)i;
            if (nextChar == '\n') linenum++;
        }
    }

    private char peek() {
        if (isEof || input == null) return (char)0;
        try {
            input.mark(1);
            int i = input.read();
            input.reset();
            return (i == -1) ? (char)0 : (char)i;
        } catch (IOException e) {
            return (char)0;
        }
    }

    private void skipLine() {
        int i;
        while (!isEof && nextChar != '\n') {
            try {
                i = (input == null) ? -1 : input.read();
                if (i == -1) { isEof = true; break; }
                nextChar = (char)i;
            } catch (IOException e) {
                isEof = true;
                break;
            }
        }
        readNext();
    }

    private boolean isSeparator(char c) {
        return "();,{}[].".indexOf(c) != -1;
    }

    private boolean isOperatorChar(char c) {
        return "+-*/%=!<>|&:".indexOf(c) != -1;
    }

    private boolean isTwoCharOperator(String op) {
        return op.equals("==") || op.equals("!=") || op.equals("<=") ||
               op.equals(">=") || op.equals("&&") || op.equals("||") ||
               op.equals("**") || op.equals(":=");
    }

    private boolean isKeyword(String s) {
        return s.equals("bool") || s.equals("else") || s.equals("if") ||
               s.equals("integer") || s.equals("main") || s.equals("while") ||
               s.equals("return") || s.equals("int") || s.equals("void");
    }

    private boolean isLetter(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    private boolean isEndOfToken(char c) {
        return (isEof || Character.isWhitespace(c) || isOperatorChar(c) || isSeparator(c));
    }

    private boolean isWhiteSpace(char c) {
        return Character.isWhitespace(c);
    }

    public Token nextToken() {
        Token t = new Token();
        t.setType("Other");
        t.setValue("");

        skipWhiteSpace();
        if (isEof || nextChar == 0) {
            t.setType("EOF");
            t.setValue("");
            return t;
        }

        if (nextChar == '/' && peek() == '/') {
            skipLine();
            return nextToken();
        }

        if (isOperatorChar(nextChar)) {
            StringBuilder sb = new StringBuilder();
            sb.append(nextChar);

            char p = peek();
            if (!isEof && isOperatorChar(p)) {
                String op2 = "" + nextChar + p;
                if (isTwoCharOperator(op2)) {
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

        if (isSeparator(nextChar)) {
            char sep = nextChar;
            readNext();
            t.setType("Separator");
            t.setValue(String.valueOf(sep));
            return t;
        }

        if (isLetter(nextChar)) {
            StringBuilder sb = new StringBuilder();
            sb.append(nextChar);
            readNext();
            while (!isEof) {
                char p = peek();
                if (isLetter(p) || isDigit(p)) {
                    sb.append(p);
                    readNext();
                } else {
                    break;
                }
            }
            String word = sb.toString();
            if (isKeyword(word)) {
                t.setType("Keyword");
                t.setValue(word);
            } else if (word.equals("True") || word.equals("False")) {
                t.setType("Literal");
                t.setValue(word);
            } else {
                t.setType("Identifier");
                t.setValue(word);
            }
            return t;
        }

        if (isDigit(nextChar)) {
            StringBuilder sb = new StringBuilder();
            while (!isEof && Character.isDigit(peek())) {
                char p = peek();
                if (Character.isDigit(p)) {
                    sb.append(p);
                    readNext();
                } else {
                    break;
                }
            }
            readNext();
            t.setType("Literal");
            t.setValue(sb.toString());
            return t;
        }

        t.setType("Other");
        t.setValue(String.valueOf(nextChar));
        readNext();
        return t;
    }
}

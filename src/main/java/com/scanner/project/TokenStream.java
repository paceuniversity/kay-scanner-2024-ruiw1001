package com.scanner.project;

import java.io.*;
import java.util.*;

public class TokenStream {

    private boolean isEof = false;
    private int current = 0;
    private PushbackReader reader;

    public TokenStream(String fileName) {
        try {
            reader = new PushbackReader(new FileReader(fileName));
            advance();
        } catch (Exception e) {
            isEof = true;
        }
    }

    public boolean isEoFile() {
        return isEof;
    }

    private int readChar() {
        try {
            int c = reader.read();
            if (c == -1) isEof = true;
            return c;
        } catch (Exception e) {
            isEof = true;
            return -1;
        }
    }

    private void advance() {
        try {
            current = reader.read();
            if (current == -1) isEof = true;
        } catch (Exception e) {
            isEof = true;
            current = -1;
        }
    }

    private int peek() {
        try {
            int c = reader.read();
            reader.unread(c);
            return c;
        } catch (Exception e) {
            return -1;
        }
    }

    private boolean isOperatorChar(char c) {
        return "+-*/<>=|&!:".indexOf(c) != -1;
    }

    private boolean isSeparator(char c) {
        return "(){}[],;".indexOf(c) != -1;
    }

    private boolean isKeyword(String s) {
        return Set.of("bool","else","if","integer","main","while").contains(s);
    }

    private boolean isTwoCharOperator(String s) {
        return Set.of("==","!=","<=",">=","||","&&",":=").contains(s);
    }

    public Token nextToken() {
        Token t = new Token();

        try {
            while (!isEof && Character.isWhitespace((char)current)) {
                advance();
            }

            if (isEof || current == -1) {
                t.setType("EOF");
                t.setValue("");
                return t;
            }

            char ch = (char)current;
            int next = peek();
            String two = "" + ch + (char)next;

            if (ch == '/' && next == '/') {
                StringBuilder sb = new StringBuilder("//");
                advance(); advance();
                int x;
                while (!isEof && (x = readChar()) != -1 && x != '\n') {
                    sb.append((char)x);
                }
                t.setType("Other");
                t.setValue(sb.toString());
                return t;
            }

            if (isTwoCharOperator(two)) {
                advance(); advance();
                t.setType("Operator");
                t.setValue(two);
                return t;
            }

            if (isSeparator(ch)) {
                advance();
                t.setType("Separator");
                t.setValue(String.valueOf(ch));
                return t;
            }

            if (isOperatorChar(ch)) {
                advance();
                t.setType("Operator");
                t.setValue(String.valueOf(ch));
                return t;
            }

            if (Character.isLetter(ch)) {
                StringBuilder sb = new StringBuilder();
                sb.append(ch);
                int x;
                while (!isEof && (x = readChar()) != -1 && Character.isLetterOrDigit((char)x)) {
                    sb.append((char)x);
                }
                String word = sb.toString();
                t.setType(isKeyword(word) ? "Keyword" : "Identifier");
                t.setValue(word);
                return t;
            }

            if (Character.isDigit(ch)) {
                String num = "";
                int x = current;
                while (!isEof && x != -1 && Character.isDigit((char)x)) {
                    num += (char)x;
                    x = readChar();
                }
                t.setType("Literal");
                t.setValue(num);
                return t;
            }

            advance();
            t.setType("Other");
            t.setValue(String.valueOf(ch));
            return t;

        } catch (Exception e) {
            t.setType("EOF");
            t.setValue("");
            return t;
        }
    }
}

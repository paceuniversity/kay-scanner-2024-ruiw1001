package com.scanner.project;

import java.io.*;
import java.util.*;

public class TokenStream {

    private boolean isEof = false;
    private int current;
    private PushbackReader reader;

    public TokenStream(String fileName) {
        try {
            reader = new PushbackReader(new FileReader(fileName));
            advance();
        } catch (Exception e) {
            isEof = true;
        }
    }

    private void advance() {
        try {
            current = reader.read();
            if (current == -1) {
                isEof = true;
            }
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

    public boolean isEoFile() {
        return isEof;
    }

    public Token nextToken() {
        Token t = new Token();
        try {
            while (!isEof && Character.isWhitespace(current)) {
                advance();
            }

            if (isEof || current == -1) {
                t.setType("EOF");
                t.setValue("");
                return t;
            }

            char c = (char) current;
            int next = peek();
            String two = "" + c + (char) next;

            if (c == '/' && next == '/') {
                StringBuilder sb = new StringBuilder();
                sb.append("//");
                advance(); advance();
                while (!isEof && current != '\n' && current != -1) {
                    sb.append((char)current);
                    advance();
                }
                t.setType("Other");
                t.setValue(sb.toString());
                return t;
            }

            Set<String> multiOps = Set.of("==","!=","<=",">=","||","&&",":=");
            if (multiOps.contains(two)) {
                advance(); advance();
                t.setType("Operator");
                t.setValue(two);
                return t;
            }

            if ("(){}[],;".indexOf(c) != -1) {
                advance();
                t.setType("Separator");
                t.setValue(String.valueOf(c));
                return t;
            }

            if ("+-*/<>=|&!:".indexOf(c) != -1) {
                advance();
                t.setType("Operator");
                t.setValue(String.valueOf(c));
                return t;
            }

            if (Character.isLetter(c)) {
                StringBuilder sb = new StringBuilder();
                while (!isEof && Character.isLetterOrDigit(current)) {
                    sb.append((char)current);
                    advance();
                }
                String word = sb.toString();
                t.setType(isKeyword(word) ? "Keyword" : "Identifier");
                t.setValue(word);
                return t;
            }

            if (Character.isDigit(c)) {
                String num = "";
                while (!isEof && Character.isDigit(current)) {
                    num += (char)current;
                    advance();
                }
                t.setType("Literal");
                t.setValue(num);
                return t;
            }

            advance();
            t.setType("Other");
            t.setValue(String.valueOf(c));
            return t;

        } catch (Exception e) {
            t.setType("EOF");
            t.setValue("");
            return t;
        }
    }

    private boolean isKeyword(String s) {
        return Set.of("bool","else","if","integer","main","while").contains(s);
    }
}

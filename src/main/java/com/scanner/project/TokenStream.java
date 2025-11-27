package com.scanner.project;

import java.io.*;
import java.util.*;

public class TokenStream {

    private boolean isEof = false;
    private PushbackReader reader;

    public TokenStream(String fileName) {
        try {
            reader = new PushbackReader(new FileReader(fileName));
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

    private int peek() {
        try {
            int c = reader.read();
            reader.unread(c);
            return c;
        } catch (Exception e) {
            return -1;
        }
    }

    public Token nextToken() {
        Token t = new Token();

        int c;
        while (!isEof && (c = readChar()) != -1 && Character.isWhitespace((char)c)) {}

        if (isEof) {
            t.setType("EOF");
            t.setValue("");
            return t;
        }

        char ch = (char)c;
        int next = peek();
        String two = "" + ch + (char)next;

        if (ch == '/' && next == '/') {
            reader.unread('/');
            StringBuilder sb = new StringBuilder();
            int x;
            while (!isEof && (x = readChar()) != '\n' && x != -1) {
                sb.append((char)x);
            }
            t.setType("Other");
            t.setValue(sb.toString());
            return t;
        }

        if (Set.of("==","!=","<=",">=","||","&&",":=").contains(two)) {
            readChar();
            t.setType("Operator");
            t.setValue(two);
            return t;
        }

        if ("(){}[],;".indexOf(ch) != -1) {
            t.setType("Separator");
            t.setValue(String.valueOf(ch));
            return t;
        }

        if ("+-*/<>=|&!:".indexOf(ch) != -1) {
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
            StringBuilder sb = new StringBuilder();
            sb.append(ch);
            int x;
            while (!isEof && (x = readChar()) != -1 && Character.isDigit((char)x)) {
                sb.append((char)x);
            }
            t.setType("Literal");
            t.setValue(sb.toString());
            return t;
        }

        t.setType("Other");
        t.setValue(String.valueOf(ch));
        return t;
    }

    private boolean isKeyword(String s) {
        return Set.of("bool","else","if","integer","main","while").contains(s);
    }

    public static void main(String[] args) {
        System.out.println("TokenStream loaded");
    }
}

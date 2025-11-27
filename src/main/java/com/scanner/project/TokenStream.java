package com.scanner.project;

import java.io.File;
import java.io.FileReader;
import java.io.PushbackReader;
import java.io.IOException;

public class TokenStream {

    private PushbackReader reader;
    public int linenum = 0;
    private boolean eof = false;
    private StringBuilder buffer = new StringBuilder();

    public TokenStream(String filename) {
        this(new File(filename));
    }

    public TokenStream(File file) {
        try {
            reader = new PushbackReader(new FileReader(file));
            int first = reader.read();
            if (first == -1) {
                eof = true;
                reader = null;
                return;
            }
            reader.unread(first);
        } catch (IOException e) {
            eof = true;
            reader = null;
        }
    }

    private Token makeToken(String type, String value) {
        Token t = new Token();
        t.setType(type);
        t.setValue(value);
        return t;
    }

    private boolean isSeparator(char c) {
        return c == '(' || c == ')' || c == '{' || c == '}' || c == ',' || c == ';';
    }

    private boolean isOperatorStart(char c) {
        return "=!<>|&:".indexOf(c) != -1;
    }

    private boolean isTwoCharOperator(String op) {
        return op.equals("==") || op.equals("!=") || op.equals("<=") ||
               op.equals(">=") || op.equals("||") || op.equals("&&") || op.equals(":=");
    }

    private boolean isKeyword(String word) {
        return word.equals("if") || word.equals("else") || word.equals("while") || word.equals("main") || word.equals("bool");
    }

    public Token nextToken() {
        if (eof || reader == null) {
            return makeToken("EOF", "");
        }

        buffer.setLength(0);

        try {
            int ch;
            while ((ch = reader.read()) != -1 && Character.isWhitespace((char) ch)) {
                if (ch == '\n') linenum++;
            }
            if (ch == -1) {
                eof = true;
                return makeToken("EOF", "");
            }

            char c = (char) ch;

            if (c == '/') {
                int next = reader.read();
                if (next == '/') {
                    while ((ch = reader.read()) != -1 && ch != '\n');
                    if (ch == '\n') linenum++;
                    return makeToken("EOF", "");
                } else {
                    reader.unread(next);
                    return makeToken("Other", "/");
                }
            }

            if (c == '.') return makeToken("Other", ".");
            if (c == '@') return makeToken("Other", "@");
            if (c == '[') return makeToken("Other", "[");
            if (c == ']') return makeToken("Other", "]");
            
            if (isSeparator(c)) {
                return makeToken("Separator", String.valueOf(c));
            }

            if (isOperatorStart(c)) {
                int next = reader.read();
                if (next == -1) {
                    return makeToken("Operator", String.valueOf(c));
                }
                String two = "" + c + (char) next;
                if (isTwoCharOperator(two)) {
                    return makeToken("Operator", two);
                }
                reader.unread(next);
                return makeToken(c == '&' || c == '|' ? "Operator" : "Operator", String.valueOf(c));
            }

            if (Character.isLetter(c) || c == '_') {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                int next;
                while ((next = reader.read()) != -1 && (Character.isLetterOrDigit((char) next) || (char) next == '_')) {
                    sb.append((char) next);
                }
                if (next != -1) reader.unread(next);
                String word = sb.toString();
                if (word.equals("True") || word.equals("False")) {
                    return makeToken("Literal", word);
                }
                if (isKeyword(word)) {
                    return makeToken("Keyword", word);
                }
                return makeToken("Identifier", word);
            }

            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                int next;
                while ((next = reader.read()) != -1 && Character.isDigit((char) next)) {
                    sb.append((char) next);
                }
                if (next != -1) reader.unread(next);
                return makeToken("Literal", sb.toString());
            }

            return makeToken("Other", String.valueOf(c));

        } catch (IOException e) {
            return makeToken("EOF", "");
        }
    }
}

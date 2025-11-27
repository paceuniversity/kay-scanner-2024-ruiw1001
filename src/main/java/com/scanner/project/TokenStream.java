package com.scanner.project;

import java.io.File;
import java.io.FileReader;
import java.io.PushbackReader;
import java.io.IOException;

public class TokenStream {

    private PushbackReader reader;
    private boolean eof = false;
    public int linenum = 0;

    public TokenStream(String filename) {
        this(new File(filename));
    }

    public TokenStream(File file) {
        try {
            reader = new PushbackReader(new FileReader(file));
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
        return "=!<>|&".indexOf(c) != -1;
    }

    private boolean isOperator(String s) {
        return s.equals("+") || s.equals("-") || s.equals("<") || s.equals(">") ||
               s.equals("<=") || s.equals(">=") || s.equals("==") || s.equals("!=") ||
               s.equals("&&") || s.equals("||") || s.equals(":=") || s.equals("=");
    }

    public Token nextToken() {
        if (eof || reader == null) {
            return makeToken("EOF", "");
        }

        try {
            int ch;

            while ((ch = reader.read()) != -1 && Character.isWhitespace((char)ch)) {
                if (ch == '\n') linenum++;
            }

            if (ch == -1) {
                eof = true;
                return makeToken("EOF", "");
            }

            char c = (char)ch;

            if (c == '/') {
                int n = reader.read();
                if (n == '/') {
                    while ((ch = reader.read()) != -1 && ch != '\n');
                    linenum++;
                    return makeToken("EOF", "");
                } else {
                    reader.unread(n);
                    return makeToken("Other", "/");
                }
            }

            if (isSeparator(c)) {
                return makeToken("Separator", String.valueOf(c));
            }

            if (isOperatorStart(c)) {
                int n = reader.read();
                if (n == -1) {
                    return makeToken("Operator", String.valueOf(c));
                }
                String two = "" + c + (char)n;
                if (isOperator(two)) {
                    return makeToken("Operator", two);
                }
                reader.unread(n);
                return makeToken("Operator", String.valueOf(c));
            }

            if (Character.isLetter(c) || c == '_') {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                int n;
                while ((n = reader.read()) != -1 && (Character.isLetterOrDigit((char)n) || (char)n=='_')) {
                    sb.append((char)n);
                }
                if (n != -1) reader.unread(n);
                String word = sb.toString();

                if (word.equals("True") || word.equals("False")) {
                    return makeToken("Literal", word);
                }

                if (isOperator(word)) {
                    return makeToken("Operator", word);
                }

                if (word.equals("if") || word.equals("else") || word.equals("while") ||
                    word.equals("main") || word.equals("bool") || word.equals("integer") ||
                    word.equals("void") || word.equals("return")) {
                    return makeToken("Keyword", word);
                }

                return makeToken("Identifier", word);
            }

            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                int n;
                while ((n = reader.read()) != -1 && Character.isDigit((char)n)) {
                    sb.append((char)n);
                }
                if (n != -1) reader.unread(n);
                return makeToken("Literal", sb.toString());
            }

            return makeToken("Other", String.valueOf(c));

        } catch (IOException e) {
            return makeToken("EOF", "");
        }
    }
}

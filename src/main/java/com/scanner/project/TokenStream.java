package com.scanner.project;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

public class TokenStream {

    private PushbackReader reader;
    public int linenum = 0;
    private boolean eof = false;

    public TokenStream(File file) {
        try {
            reader = new PushbackReader(new FileReader(file));
            advance();
        } catch (Exception e) {
            eof = true;
            reader = null;
        }
    }

    public TokenStream(String filename) {
        this(new File(filename));
    }

    private int currentChar;

    private void advance() {
        if (reader == null) { eof = true; return; }
        try {
            currentChar = reader.read();
            if (currentChar == -1) eof = true;
        } catch (IOException e) {
            eof = true;
        }
    }

    private int readNext() {
        if (reader == null) return -1;
        try {
            int ch = reader.read();
            if (ch != -1) reader.unread(ch);
            return ch;
        } catch (Exception e) {
            return -1;
        }
    }

    private int peek() {
        if (reader == null) return -1;
        try {
            int ch = reader.read();
            reader.unread(ch);
            return ch;
        } catch (Exception e) {
            return -1;
        }
    }

    private Token makeToken(String type, String value) {
        Token t = new Token();
        t.setType(type);
        t.setValue(value);
        return t;
    }

    public Token nextToken() {
        if (eof || reader == null) {
            return makeToken("EOF", "");
        }

        try {
            while (!eof && Character.isWhitespace((char)currentChar)) {
                if (currentChar == '\n') linenum++;
                advance();
            }
            if (eof) return makeToken("EOF", "");

            char c = (char) currentChar;

            if (c == '/') {
                advance();
                return makeToken("Other", "/");
            }

            if (c == '*' ) {
                advance();
                return makeToken("Operator", "*");
            }

            if (c == '&') {
                int ch2 = peek();
                if (ch2 == '&') {
                    reader.read();
                    advance(); advance();
                    return makeToken("Operator", "&&");
                }
                advance();
                return makeToken("Other", "&");
            }

            if (c == '|') {
                int ch2 = peek();
                if (ch2 == '|') {
                    reader.read();
                    advance(); advance();
                    return makeToken("Operator", "||");
                }
                advance();
                return makeToken("Other", "|");
            }

            if (c == '=' ) {
                int ch2 = peek();
                if (ch2 == '=') {
                    reader.read();
                    advance(); advance();
                    return makeToken("Operator", "==");
                }
                advance();
                return makeToken("Other", "=");
            }

            if (c == '!' ) {
                int ch2 = peek();
                if (ch2 == '=') {
                    reader.read();
                    advance(); advance();
                    return makeToken("Operator", "!=");
                }
                advance();
                return makeToken("Other", "!");
            }

            if (c == '<' ) {
                int ch2 = peek();
                if (ch2 == '=') {
                    reader.read();
                    advance(); advance();
                    return makeToken("Operator", "<=");
                }
                advance();
                return makeToken("Operator", "<");
            }

            if (c == '>' ) {
                int ch2 = peek();
                if (ch2 == '=') {
                    reader.read();
                    advance(); advance();
                    return makeToken("Operator", ">=");
                }
                advance();
                return makeToken("Operator", ">");
            }

            if (c == ';' || c == ',' || c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}' ) {
                advance();
                return makeToken("Separator", String.valueOf(c));
            }

            if (Character.isLetter(c) || c == '_') {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                advance();
                while (!eof) {
                    int ch2 = reader.read();
                    if (ch2 == -1) { eof = true; break; }
                    char c2 = (char)ch2;
                    if (Character.isLetterOrDigit(c2) || c2 == '_') {
                        sb.append(c2);
                        advance();
                    } else {
                        reader.unread(ch2);
                        break;
                    }
                }
                String word = sb.toString();
                if (word.equals("bool") || word.equals("integer") || word.equals("main")) {
                    return makeToken("Identifier", word);
                }
                if (word.equals("true") || word.equals("false")) {
                    return makeToken("Literal", word);
                }
                if (isControlKeyword(word)) {
                    return makeToken("Keyword", word.toLowerCase());
                }
                return makeToken("Identifier", word);
            }

            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                advance();
                while (!eof) {
                    int ch2 = reader.read();
                    if (ch2 == -1) { eof = true; break; }
                    char c2 = (char)ch2;
                    if (Character.isDigit(c2)) {
                        sb.append(c2);
                        advance();
                    } else {
                        reader.unread(ch2);
                        break;
                    }
                }
                return makeToken("Literal", sb.toString());
            }

            advance();
            return makeToken("Other", String.valueOf(c));

        } catch (Exception e) {
            eof = true;
            return makeToken("EOF", "");
        }
    }

    private boolean isControlKeyword(String word) {
        return word.equals("if") || word.equals("else") || word.equals("while") || word.equals("return");
    }
}

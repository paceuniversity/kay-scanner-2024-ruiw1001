package com.scanner.project;

import java.io.File;
import java.io.FileReader;
import java.io.PushbackReader;
import java.io.IOException;

public class TokenStream {

    private PushbackReader reader;
    public int linenum = 0;
    private boolean eof = false;
    private int currentChar;

    public TokenStream(File file) {
        try {
            reader = new PushbackReader(new FileReader(file));
        } catch (Exception e) {
            eof = true;
            reader = null;
            currentChar = -1;
        }
        advance();
    }

    public TokenStream(String filename) {
        this(new File(filename));
    }

    private void advance() {
        if (reader == null) {
            currentChar = -1;
            eof = true;
            return;
        }
        try {
            currentChar = reader.read();
            if (currentChar == -1) {
                eof = true;
            }
        } catch (IOException e) {
            currentChar = -1;
            eof = true;
        }
    }

    private int peek() {
        if (reader == null) return -1;
        try {
            int ch = reader.read();
            reader.unread(ch);
            return ch;
        } catch (IOException e) {
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
        if (eof || currentChar == -1) {
            return makeToken("EOF", "");
        }

        try {
            while (!eof && currentChar != -1 && Character.isWhitespace((char) currentChar)) {
                if (currentChar == '\n') {
                    linenum++;
                }
                advance();
            }

            if (eof || currentChar == -1) {
                return makeToken("EOF", "");
            }

            char c = (char) currentChar;
            int p = peek();

            if (c == '/' && p == '/') {
                reader.read();
                while (!eof) {
                    int cc = reader.read();
                    if (cc == -1) { eof = true; break; }
                    if (cc == '\n') { linenum++; break; }
                }
                advance();
                return nextToken();
            }

            if (isOperatorChar(c)) {
                if (isTwoCharOperatorStart(c, p)) {
                    String op = "" + c + (char) reader.read();
                    advance();
                    advance();
                    return makeToken("Operator", op);
                }
                advance();
                return makeToken("Operator", String.valueOf(c));
            }

            if (isSeparator(c)) {
                advance();
                return makeToken("Other", String.valueOf(c));
            }

            if (Character.isLetter(c) || c == '_') {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                advance();

                while (!eof) {
                    int cc = reader.read();
                    if (cc == -1) { eof = true; break; }
                    char cc2 = (char) cc;
                    if (Character.isLetterOrDigit(cc2) || cc2 == '_') {
                        sb.append(cc2);
                        advance();
                    } else {
                        reader.unread(cc);
                        break;
                    }
                }

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
                advance();

                while (!eof) {
                    int cc = reader.read();
                    if (cc == -1) { eof = true; break; }
                    char c2 = (char) cc;
                    if (Character.isDigit(c2)) {
                        sb.append(c2);
                        advance();
                    } else {
                        reader.unread(cc);
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

    private boolean isSeparator(char c) {
        return "(){}[],.;".indexOf(c) != -1;
    }

    private boolean isOperatorChar(char c) {
        return "+-*/%!=<>:&|".indexOf(c) != -1 || c == '/';
    }

    private boolean isTwoCharOperatorStart(char c, int p) {
        if (c == '&' && p == '&') return true;
        if (c == '|' && p == '|') return true;
        if (c == '=' && p == '=') return true;
        if (c == '!' && p == '=') return true;
        if (c == '<' && p == '=') return true;
        if (c == '>' && p == '=') return true;
        if (c == ':' && p == '=') return true;
        return false;
    }

    private boolean isKeyword(String word) {
        return word.equals("bool") ||
               word.equals("integer") ||
               word.equals("main") ||
               word.equals("while") ||
               word.equals("if") ||
               word.equals("else") ||
               word.equals("void");
    }
}

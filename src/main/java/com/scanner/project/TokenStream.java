package com.scanner.project;

import java.io.File;
import java.io.FileReader;
import java.io.PushbackReader;
import java.io.IOException;

public class TokenStream {

    private PushbackReader reader;
    public int linenum = 0;
    private boolean eof = false;

    public TokenStream(File inputFile) {
        try {
            reader = new PushbackReader(new FileReader(inputFile));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Token nextToken() {
        if (eof) {
            return makeToken("EOF", "");
        }

        try {
            int ch;
            while (true) {
                ch = reader.read();
                if (ch == -1) {
                    eof = true;
                    return makeToken("EOF", "");
                }
                if (ch == '\n') {
                    linenum++;
                }
                if (!Character.isWhitespace(ch)) {
                    break;
                }
            }

            char c = (char) ch;

            if (c == '/') {
                int next = reader.read();
                if (next == '/') {
                    while (true) {
                        int cc = reader.read();
                        if (cc == -1) {
                            eof = true;
                            return makeToken("EOF", "");
                        }
                        if (cc == '\n') {
                            linenum++;
                            break;
                        }
                    }
                    return nextToken();
                } else {
                    if (next != -1) {
                        reader.unread(next);
                    }
                    return makeToken("Operator", "/");
                }
            }

            if (isSeparator(c)) {
                return makeToken("Separator", String.valueOf(c));
            }

            if (isOperatorChar(c)) {
                int next = reader.read();
                if (next != -1) {
                    char c2 = (char) next;
                    String two = "" + c + c2;
                    if (isTwoCharOperator(two)) {
                        return makeToken("Operator", two);
                    } else {
                        reader.unread(next);
                    }
                }
                return makeToken("Operator", String.valueOf(c));
            }

            if (Character.isLetter(c) || c == '_') {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                while (true) {
                    int nx = reader.read();
                    if (nx == -1) break;
                    char cc = (char) nx;
                    if (Character.isLetterOrDigit(cc) || cc == '_') {
                        sb.append(cc);
                    } else {
                        reader.unread(nx);
                        break;
                    }
                }
                String word = sb.toString();

                if (isKeyword(word)) {
                    return makeToken("Keyword", word);
                } else if (isBooleanLiteral(word)) {
                    return makeToken("Literal", word);
                } else {
                    return makeToken("Identifier", word);
                }
            }

            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                while (true) {
                    int nx = reader.read();
                    if (nx == -1) break;
                    char cc = (char) nx;
                    if (Character.isDigit(cc)) {
                        sb.append(cc);
                    } else {
                        reader.unread(nx);
                        break;
                    }
                }
                return makeToken("Literal", sb.toString());
            }

            return makeToken("Other", String.valueOf(c));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Token makeToken(String type, String value) {
        Token t = new Token();
        t.setType(type);
        t.setValue(value);
        return t;
    }

    private Token makeEOF() {
        return makeToken("EOF", "");
    }

    private Token OtherToken(String v) {
        return makeToken("Other", v);
    }

    private Token makeToken(String v) { 
        return OtherToken(v);
    }

    private boolean isSeparator(char c) {
        return c == '(' || c == ')' || c == '{' || c == '}' ||
               c == ';' || c == ',' || c == '.';
    }

    private boolean isOperatorChar(char c) {
        return c == '+' || c == '-' || c == '*' || c == '%' ||
               c == '=' || c == '<' || c == '>' || c == '!' ||
               c == '&' || c == '|';
    }

    private boolean isTwoCharOperator(String op) {
        return op.equals("==") || op.equals("!=") ||
               op.equals(">=") || op.equals("<=") ||
               op.equals("&&") || op.equals("||");
    }

    private boolean isKeyword(String word) {
        return word.equals("if") ||
               word.equals("else") ||
               word.equals("while") ||
               word.equals("bool") ||
               word.equals("integer") ||
               word.equals("int") ||
               word.equals("void") ||
               word.equals("main");
    }

    private boolean isBooleanLiteral(String v) {
        return v.equals("True") || v.equals("False");
    }
}

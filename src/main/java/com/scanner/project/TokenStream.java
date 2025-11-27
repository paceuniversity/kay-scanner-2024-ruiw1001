package com.scanner.project;

import java.io.File;
import java.io.FileReader;
import java.io.PushbackReader;
import java.io.IOException;

public class TokenStream {

    private PushbackReader reader;
    public int linenum = 1;
    private boolean eof = false;

    public TokenStream(String filename) {
        this(new File(filename));
    }

    public TokenStream(File file) {
        try {
            reader = new PushbackReader(new FileReader(file));
        } catch (Exception e) {
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

    public Token nextToken() {
        if (eof) {
            return makeToken("EOF", "");
        }

        try {
            int ch = reader.read();
            if (ch == -1) {
                eof = true;
                return makeToken("EOF", "");
            }

            char c = (char) ch;

            if (c == '\n') {
                linenum++;
                return nextToken();
            }

            if (Character.isWhitespace(c)) {
                return nextToken();
            }

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
                }
                if (next != -1) reader.unread(next);
                return makeToken("Operator", "/");
            }

            if (c == '*') {
                int next = reader.read();
                if (next == '*') {
                    return makeToken("Operator", "**");
                }
                if (next != -1) reader.unread(next);
                return makeToken("Operator", "*");
            }

            if ("=<>!&|".indexOf(c) != -1) {
                int next = reader.read();
                if (next != -1) {
                    char c2 = (char) next;
                    String two = "" + c + c2;
                    if (two.equals("==") || two.equals("!=") || two.equals(">=") ||
                        two.equals("<=") || two.equals("&&") || two.equals("||")) {
                        return makeToken("Operator", two);
                    }
                    reader.unread(next);
                }
                return makeToken("Operator", String.valueOf(c));
            }

            if ("+-/%".indexOf(c) != -1) {
                return makeToken("Operator", String.valueOf(c));
            }

            if (c == '{' || c == '}' || c == '(' || c == ')' ||
                c == ';' || c == ',' || c == '[' || c == ']' ||
                c == '.' || c == '@' || c == '&' || c == '|' ||
                c == ':' || c == '\\') {
                return makeToken("Other", String.valueOf(c));
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
                }

                if (word.equals("true") || word.equals("false")) {
                    return makeToken("Identifier", word);
                }

                if (isBooleanLiteral(word)) {
                    return makeToken("Literal", word);
                }

                return makeToken("Identifier", word);
            }

            return makeToken("Other", String.valueOf(c));

        } catch (IOException e) {
            eof = true;
            return makeToken("EOF", "");
        }
    }

    private boolean isKeyword(String word) {
        return word.equals("bool") ||
               word.equals("else") ||
               word.equals("if") ||
               word.equals("integer") ||
               word.equals("main") ||
               word.equals("while") ||
               word.equals("int") ||
               word.equals("void");
    }

    private boolean isBooleanLiteral(String word) {
        return word.equals("True") ||
               word.equals("False");
    }
    
    private boolean isBooleanLiteralExact(String s){
        return s.equals("True")||s.equals("False");
    }
}

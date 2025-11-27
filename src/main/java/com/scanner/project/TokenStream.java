package com.scanner.project;

import java.io.File;
import java.io.FileReader;
import java.io.PushbackReader;
import java.io.IOException;
import java.io.FileNotFoundException;

public class TokenStream {

    private PushbackReader reader;
    public int linenum = 0;
    private boolean eof = false;

    public TokenStream(String filename) {
        this(new File(filename));
    }

    public TokenStream(File inputFile) {
        try {
            reader = new PushbackReader(new FileReader(inputFile));
            advance();
        } catch (FileNotFoundException e) {
            eof = true;
            reader = null;
        } catch (IOException e) {
            eof = true;
        }
    }

    private int currentChar;

    private void advance() {
        if (reader == null) {
            eof = true;
            return;
        }
        try {
            currentChar = reader.read();
            if (currentChar == -1) {
                eof = true;
            }
        } catch (IOException e) {
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

    private boolean isSeparator(char c) {
        return c == '(' || c == ')' || c == '{' || c == '}' ||
               c == '[' || c == ']' || c == ',' || c == ';' || c == '.';
    }

    private boolean isOperatorChar(char c) {
        return "+-*/%=!<>|&".indexOf(c) != -1;
    }

    private boolean isTwoCharOperator(String op) {
        return op.equals("==") || op.equals("!=") ||
               op.equals("<=") || op.equals(">=") ||
               op.equals("||") || op.equals("&&");
    }

    private boolean isKeyword(String word) {
        return word.equals("if") ||
               word.equals("else") ||
               word.equals("while") ||
               word.equals("return") ||
               word.equals("function") ||
               word.equals("var") ||
               word.equals("int") ||
               word.equals("boolean") ||
               word.equals("True") || word.equals("False") ||
               word.equals("main") || word.equals("void") || word.equals("integer");
    }

    public Token nextToken() {
        if (eof) {
            return makeToken("EOF", "");
        }

        try {
            while (!eof && (currentChar == '\n' || Character.isWhitespace((char) currentChar))) {
                if (currentChar == '\n') linenum++;
                advance();
            }

            if (eof) {
                return makeToken("EOF", "");
            }

            char c = (char) currentChar;
            int next = peek();
            String twoChar = "" + c + (char) next;

            if (c == '/' && next == '/') {
                reader.read();
                advance();
                while (!eof) {
                    int ch = reader.read();
                    if (ch == -1) {
                        eof = true;
                        break;
                    }
                    if (ch == '\n') {
                        linenum++;
                        break;
                    }
                }
                return nextToken();
            }

            if (isOperatorChar(c)) {
                if (next != -1 && isTwoCharOperator(twoChar)) {
                    reader.read();
                    advance(); advance();
                    return makeToken("Operator", twoChar);
                } else {
                    advance();
                    return makeToken("Operator", String.valueOf(c));
                }
            }

            if (isSeparator(c)) {
                advance();
                return makeToken("Separator", String.valueOf(c));
            }

            if (Character.isLetter(c) || c == '_') {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                advance();
                while (!eof) {
                    int ch = reader.read();
                    if (ch == -1) {
                        eof = true;
                        break;
                    }
                    char cc = (char) ch;
                    if (Character.isLetterOrDigit(cc) || cc == '_') {
                        sb.append(cc);
                        advance();
                    } else {
                        reader.unread(ch);
                        break;
                    }
                }
                String word = sb.toString();
                if (isKeyword(word)) {
                    if (word.equals("True") || word.equals("False")) {
                        return makeToken("Literal", word);
                    }
                    return makeToken("Keyword", word);
                }
                return makeToken("Identifier", word);
            }

            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                advance();
                while (!eof) {
                    int ch = reader.read();
                    if (ch == -1) {
                        eof = true;
                        break;
                    }
                    char cc = (char) ch;
                    if (Character.isDigit(cc)) {
                        sb.append(cc);
                        advance();
                    } else {
                        reader.unread(ch);
                        break;
                    }
                }
                return makeToken("Literal", sb.toString());
            }

            advance();
            return makeToken("Other", String.valueOf(c));

        } catch (Exception e) {
            Token t = new Token();
            t.setType("EOF");
            t.setValue("");
            return t;
        }
    }
}

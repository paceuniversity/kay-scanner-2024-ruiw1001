package com.scanner.project;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TokenStream {

    private BufferedReader reader;
    private boolean isEof = false;
    private int lineNumber = 1;

    public int getLineNumber() {
        return lineNumber;
    }

    public TokenStream(String fileName) {
        try {
            this.reader = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private char readChar() {
        try {
            int nextCharInAscii = reader.read();
            if (nextCharInAscii == -1) {
                isEof = true;
                return '\0';
            }
            char nextChar = (char) nextCharInAscii;
            if (nextChar == '\n') {
                lineNumber++;
            }
            return nextChar;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void skipWhiteSpace() {
        char currChar = ' ';
        try {
            reader.mark(1);
            int currCharInAscii = reader.read();
            if (currCharInAscii == -1) {
                isEof = true;
                return;
            }
            currChar = (char) currCharInAscii;

            while (isWhiteSpace(currChar) || isEndOfLine(currChar)) {
                if (currChar == '\n') {
                    lineNumber++;
                }
                reader.mark(1);
                currCharInAscii = reader.read();
                if (currCharInAscii == -1) {
                    isEof = true;
                    return;
                }
                currChar = (char) currCharInAscii;
            }

            reader.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Token nextToken() {
        Token currToken = new Token();
        currToken.setType("Other");
        currToken.setValue("");

        if (isEof) {
            currToken.setType("EOF");
            return currToken;
        }

        skipWhiteSpace();

        char nextChar = readChar();

        while (!isEof && (isWhiteSpace(nextChar) || isEndOfLine(nextChar) || isCommentChar(nextChar))) {

            if (isCommentChar(nextChar)) {
                while (!isEof && !isEndOfLine(nextChar)) {
                    nextChar = readChar();
                }
            } else {
                break;
            }
            nextChar = readChar();
        }

        if (isEof) {
            currToken.setType("EOF");
            return currToken;
        }

        currToken.setValue(currToken.getValue() + nextChar);

        char currChar = readChar();
        skipWhiteSpace();

        if (Character.isLetter(nextChar)) {
            while (!isEof && (Character.isLetterOrDigit(currChar) || currChar == '_')) {
                currToken.setValue(currToken.getValue() + currChar);
                currChar = readChar();
            }

            currToken.setType("Identifier");

            if (isKeyword(currToken.getValue())) {
                currToken.setType("Keyword");
            } else if (currToken.getValue().equals("True")
                    || currToken.getValue().equals("False")) {
                currToken.setType("Literal");
            }
        }
        else if (Character.isDigit(nextChar)) {
            while (!isEof && Character.isDigit(currChar)) {
                currToken.setValue(currToken.getValue() + currChar);
                currChar = readChar();
            }
            currToken.setType("Literal");
        }
        else if (isSeparator(nextChar)) {
            currToken.setType("Separator");
        }
        else if (isOperator(nextChar)) {
            currToken.setType("Operator");

            switch (nextChar) {

                case '+':
                case '-':
                case '*':
                case '/':
                    break;

                case '<':
                    if (currChar == '=') {
                        currToken.setValue(currToken.getValue() + currChar);
                        currChar = readChar();
                    }
                    break;

                case '>':
                    if (currChar == '=') {
                        currToken.setValue(currToken.getValue() + currChar);
                        currChar = readChar();
                    }
                    break;

                case '=':
                    if (currChar == '=') {
                        currToken.setValue(currToken.getValue() + currChar);
                        currChar = readChar();
                    } else {
                        currToken.setType("Other");
                    }
                    break;

                case ':':
                    if (currChar == '=') {
                        currToken.setValue(currToken.getValue() + currChar);
                        currChar = readChar();
                    } else {
                        currToken.setType("Other");
                    }
                    break;

                case '!':
                    if (currChar == '=') {
                        currToken.setValue(currToken.getValue() + currChar);
                        currChar = readChar();
                    }
                    break;

                case '&':
                    if (currChar == '&') {
                        currToken.setValue(currToken.getValue() + currChar);
                        currChar = readChar();
                    } else {
                        currToken.setType("Other");
                    }
                    break;

                case '|':
                    if (currChar == '|') {
                        currToken.setValue(currToken.getValue() + currChar);
                        currChar = readChar();
                    } else {
                        currToken.setType("Other");
                    }
                    break;

                default:
                    currToken.setType("Other");
            }
        }
        else {
            while (!isEof && !isEndOfToken(currChar)) {
                currToken.setValue(currToken.getValue() + currChar);
                currChar = readChar();
            }
        }

        return currToken;
    }

    private boolean isSeparator(char currChar) {
        if (currChar == '(' || currChar == ')' ||
            currChar == '{' || currChar == '}' ||
            currChar == ',' || currChar == ';') {
            return true;
        } else {
            return false;
        }
    }

    private boolean isOperator(char currChar) {
        if (currChar == '+' || currChar == '-' || currChar == '*' ||
            currChar == '/' || currChar == '<' || currChar == '>' ||
            currChar == ':' || currChar == '!' || currChar == '=' ||
            currChar == '&' || currChar == '|') {
            return true;
        } else {
            return false;
        }
    }

    private boolean isCommentChar(char currChar) {
        return currChar == '/';
    }

    private boolean isEndOfToken(char currChar) {
        if (isWhiteSpace(currChar) || isSeparator(currChar) ||
            isOperator(currChar) || isCommentChar(currChar) ||
            isEndOfLine(currChar)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isEndOfLine(char currChar) {
        if (currChar == '\n' || currChar == '\r') {
            return true;
        } else {
            return false;
        }
    }

    private boolean isWhiteSpace(char currChar) {
        if (currChar == ' ' || currChar == '\t' || currChar == '\f') {
            return true;
        } else {
            return false;
        }
    }

    private boolean isKeyword(String s) {
        if (s.equals("bool") || s.equals("integer") ||
            s.equals("if")   || s.equals("else")    ||
            s.equals("main") || s.equals("while")) {
            return true;
        } else {
            return false;
        }
    }
}

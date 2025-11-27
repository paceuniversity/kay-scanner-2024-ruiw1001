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
        }

        nextInternal(); 
    }

    public TokenStream(String filename) {
        this(new File(filename));
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

    private int nextInternal() {
        if (eof || reader == null) { eof = true; return -1; }
        try {
            currentChar = reader.read();
            if (currentChar == -1) eof = true;
        } catch (IOException e) {
            eof = true;
            currentChar = -1;
        }
        return currentChar;
    }

    private int nextInternal() { return nextInternal(); }

    public Token nextToken() {
        try {
            while (!eof && Character.isWhitespace((char)currentChar)) {
                if (currentChar == '\n') linenum++;
                nextInternal();
            }

            if (eof || currentChar == -1) {
                return makeToken("EOF","");
            }

            char c = (char)currentChar;
            int p = peek();

            if (c == '/' && p == '/') {
                while (!eof) {
                    int cc = reader.read();
                    if (cc == -1) { eof = true; break; }
                    if (cc == '\n') { linenum++; break; }
                }
                nextInternal();
                return nextToken();
            }

            if (isOperatorChar(c)) {
                if (isOperatorCharTwo(c,p)) {
                    String op = ""+c+(char)reader.read();
                    nextInternal();
                    nextInternal();
                    return makeToken("Operator",op);
                }
                nextInternal();
                return makeToken("Operator",String.valueOf(c));
            }

            if (isSeparator(c)) {
                nextInternal();
                return makeToken("Other",String.valueOf(c));
            }

            if (Character.isLetter(c) || c == '_') {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                nextInternal();

                while (!eof) {
                    int cc = reader.read();
                    if (cc == -1) { eof=true; break; }
                    char cc2=(char)cc;
                    if (Character.isLetterOrDigit(cc2)||cc2=='_') {
                        sb.append(cc2);
                        nextInternal();
                    } else {
                        reader.unread(cc);
                        break;
                    }
                }

                String word = sb.toString();

                if (isKeyword(word)) {
                    return makeToken("Keyword",word);
                }
                if (word.equals("True")||word.equals("False")) {
                    return makeToken("Literal",word);
                }
                if (Character.isUpperCase(word.charAt(0))&&(word.length()>1||Character.isAlphabetic(word.charAt(0)))) {
                    boolean allUpper=true;
                    for (int i=0;i<word.length();i++){
                        if(!Character.isUpperCase(word.charAt(i))){
                            allUpper=false;break;
                        }
                    }
                    if (allUpper&&word.length()>1&&!word.equals("TRUE")){
                        return makeToken("Literal",word);
                    }
                }
                return makeToken("Identifier",word);
            }

            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                nextInternal();

                while (!eof) {
                    int cc = reader.read();
                    if (cc == -1) { eof=true; break; }
                    char c2=(char)cc;
                    if (Character.isDigit(c2)) {
                        sb.append(c2);
                        nextInternal();
                    } else {
                        reader.unread(cc);
                        break;
                    }
                }

                String num=sb.toString();
                return makeToken("Literal",num);
            }

            nextInternal();
            return makeToken("Other",String.valueOf(c));

        } catch (Exception e) {
            eof=true;
            return makeToken("EOF","");
        }
    }

    private boolean isSeparator(char c) {
        return "(){}[],.;".indexOf(c)!=-1;
    }

    private boolean isOperatorChar(char c) {
        return "!<>=+-*/%|&:".indexOf(c)!=-1 || c=='/';
    }
    
    private boolean isOperatorStart(char c,int p){
        if(c=='/'&&p=='=') return false;
        return true;
    }
    
    private boolean isOperatorChar(char c,int p){
        if(c=='/'&& p=='*'){
            return false;
        }
        return true;
    }
    
    private boolean isOperatorCharTwo(char c,int p){
        if(c=='&'){
            if (p=='&'){
                return true;
            }
            return false;
        }
        if(c=='|'){
            if(p == '|'){
                return true;
            }
            return false;
        }
        if(c=='='&& p=='='){
            return true;
        }

        if(c=='!'&& p=='='){
            return true;
        }

        if(c=='>'){
            if(p >'='){
                return true;
            }
            return false;
        }

        if(c=='<'){
            if(p =='='){
                return true;
            }
            return false;
        }

        if(c==':'&& p=='='){
            return true;
        }

        return false;
    }

    private boolean isKeyword(String word){
        return word.equals("bool") ||
               word.equals("integer")||
               word.equals("main")||
               word.equals("while")||
               word.equals("if")||
               word.equals("else")||
               word.equals("void");
    }

    private boolean isSeparator(char c){
        return "(){}[],.;".indexOf(c)!=-1;
    }

    private boolean isOperatorStart(char c,int p){
        if (c=='/'&& p=='=') return false;
        return true;
    }

package com.kaylerrenslow.parser;

import com.kaylerrenslow.parser.tokenizer.Token;
import com.kaylerrenslow.parser.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TokenStream implements Iterable<Token> {
    
    private final List<Token> tokens;
    private int position;
    
    public TokenStream(String input) {
        this.tokens = new ArrayList<>();
        this.position = 0;
        tokenize(input);
    }
    
    private void tokenize(String input) {
        Tokenizer tokenizer = new Tokenizer(input);
        while (tokenizer.hasNext()) {
            Token token = tokenizer.next();
            if (token != null) {
                tokens.add(token);
            }
        }
    }
    
    public boolean hasNext() {
        return position < tokens.size();
    }
    
    public Token next() {
        if (!hasNext()) {
            return null;
        }
        return tokens.get(position++);
    }
    
    public Token peek() {
        if (!hasNext()) {
            return null;
        }
        return tokens.get(position);
    }
    
    public void reset() {
        position = 0;
    }
    
    public int getPosition() {
        return position;
    }
    
    public void setPosition(int position) {
        if (position < 0 || position > tokens.size()) {
            throw new IllegalArgumentException("Position out of bounds: " + position);
        }
        this.position = position;
    }
    
    public List<Token> getTokens() {
        return new ArrayList<>(tokens);
    }
    
    @Override
    public Iterator<Token> iterator() {
        return tokens.iterator();
    }
    
    @Override
    public String toString() {
        return "TokenStream{tokens=" + tokens + ", position=" + position + "}";
    }
}

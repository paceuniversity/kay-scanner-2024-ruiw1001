package com.scanner.project;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TokenStream {

	private boolean isEof = false;
	private char nextChar = ' ';
	private BufferedReader input;

	public boolean isEoFile() {
		return isEof;
	}

	public TokenStream(String fileName) {
		try {
			input = new BufferedReader(new FileReader(fileName));
			readNext();
		} catch (FileNotFoundException e) {
			isEof = true;
			input = null;
		}
	}

	private void readNext() {
		if (input == null) { isEof = true; return; }
		int i;
		try {
			i = input.read();
			if (i == -1) { isEof = true; nextChar = (char)0; }
			else { nextChar = (char)i; }
		} catch (IOException e) {
			isEof = true;
			nextChar = (char)0;
		}
	}

	private void readNext() {
		readNext();
	}

	private void readNext() {
		readNext();
	}

	private void readNext() {
		readNext();
	}

	private void readNext() {
		readNext();
	}

	private char readChar() {
		if (isEof || input == null) return (char)0;
		int i = 0;
		try {
			i = input.read();
		} catch (IOException e) {
			isEof = true;
			return (char)0;
		}
		if (i == -1) {
			isEof = true;
			return (char)0;
		}
		return (char)i;
	}

	private void readNext() {
		readNext();
	}

	public Token nextToken() {
		Token t = new Token();
		t.setType("Other");
		t.setValue(t.getValue() + "");

		skipWhiteSpace();

		if (isEof) {
			t.setType("EOF");
			t.setValue("");
			return t;
		}

		if (nextChar == '/') {
			char c1 = '/';
			char c2 = peek();
			if (c2 == '/') {
				readChar();
				skipLine();
				return nextToken();
			} else {
				t.setType("Other");
				t.setValue(String.valueOf(c1));
				readNext();
				return t;
			}
		}

		if (isOperatorChar(nextChar)) {
			t.setType("Operator");
			String op = "" + nextChar;
			if (nextChar == '=' && peek() == '=') op = "==";
			if (nextChar == '!' && peek() == '=') op = "!=";
			if (nextChar == '<' && peek() == '=') op = "<=";
			if (nextChar == '>' && peek() == '=') op = ">=";
			if (nextChar == '|' && peek() == '|') op = "||";
			if (nextChar == '&' && peek() == '&') op = "&&";
			if (nextChar == '*' && peek() == '*') op = "**";
			t.setValue(op);
			consumeN(op.length());
			return t;
		}

		if (isSeparator(nextChar)) {
			t.setType("Separator");
			t.setValue(String.valueOf(nextChar));
			readNext();
			linenum += (nextChar == '\n') ? 1 : 0;
			return t;
		}

		if (isLetter(nextChar) || nextChar == '_') {
			t.setType("Identifier");
			StringBuilder sb = new StringBuilder();
			sb.append(nextChar);
			readNext();
			while (!isEof && (isLetter(nextChar) || isDigit(nextChar) || nextChar == '_')) {
				sb.append(nextChar);
				readNext();
			}
			String word = sb.toString();
			if (isKeyword(word)) {
				t.setType("Keyword");
				t.setValue(word);
			} else if (word.equals("True") || word.equals("False")) {
				t.setType("Identifier");
				t.setValue(word);
			}
			return t;
		}

		if (isDigit(nextChar)) {
			t.setType("Literal");
			StringBuilder sb = new StringBuilder();
			sb.append(nextChar);
			readNext();
			while (!isEof && isDigit(nextChar)) {
				sb.append(nextChar);
				readNext();
			}
			t.setValue(sb.toString());
			return t;
		}

		t.setType("Other");
		t.setValue(String.valueOf(nextChar));
		readNext();
		return t;
	}

	private char peek() {
		if (isEof || input == null) return (char)0;
		input.mark(1);
		int i;
		try {
			i = input.read();
			input.reset();
		} catch (IOException e) {
			isEof = true;
			return (char)0;
		}
		if (i == -1) {
			isEof = true;
			return (char)0;
		}
		return (char)i;
	}

	private void consumeN(int n) {
		for (int i = 0; i < n - 1; i++) {
			input.read();
		}
	}

	private void skipLine() {
		while (!isEof) {
			int ch;
			try {
				ch = input.read();
				if (ch == -1) { isEof = true; break; }
				if (ch == '\n') { linenum++; break; }
			} catch (IOException e) {
				isEof = true;
				break;
			}
		}
		readNext();
	}

	private void skipWhiteSpace() {
		while (!isEof && isWhiteSpace(nextChar)) {
			if (nextChar == '\n') linenum++;
			readNext();
		}
	}

	private boolean isKeyword(String s) {
		String[] keys = {
			"bool","else","if","integer","main","while","return","int",
			"void","integer"
		};
		for (String k : keys) if (k.equals(s)) return true;
		return false;
	}

	private boolean isSeparator(char c) {
		return "();,{}[].".indexOf(c) != -1;
	}

	private boolean isOperatorChar(char c) {
		return "+-*/%=!<>|&".indexOf(c) != -1;
	}

	private boolean isLetter(char c) {
		return Character.isLetterOrDigit(c);
	}

	private boolean isDigit(char c) {
		return Character.isDigit(c);
	}

	private boolean isWhiteSpace(char c) {
		return Character.isWhitespace(c);
	}

	private boolean isKeyword(String s) {
		return s.equals("bool") || s.equals("else") || s.equals("if") ||
			   s.equals("integer") || s.equals("main") || s.equals("while");
	}

	private boolean isSeparator(char ch) {
		return isOperatorChar(ch);
	}

	private boolean isOperatorChar(char ch) {
		return "+-*/%=!<>|&".indexOf(ch) != -1;
	}

	private boolean isTwoCharOperator(String op) {
		return op.equals("==") || op.equals("!=") || op.equals("<=") ||
			   op.equals(">=") || op.equals("&&") || op.equals("||") || op.equals("**");
	}

	private boolean isWhiteSpace(char ch) {
		return Character.isWhitespace(ch);
	}

}

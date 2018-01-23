package model;

public enum Token {

	EMPTY, BLACK, WHITE;
	
	public Token other() {
		if (this.equals(Token.BLACK)) {
			return Token.WHITE;
		} else if (this.equals(Token.WHITE)) {
			return Token.BLACK;
		} else {
			return Token.EMPTY;
		}
	}
	
	public String toString() {
		if (this.equals(Token.BLACK)) {
			return "W";
		} else if (this.equals(Token.WHITE)) {
			return "B";
		} else {
			return "E";
		}
	}
}

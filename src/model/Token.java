package model;

public class Token {
	
	private int i;
	private int color; // empty = 0; white = 1; black = 2;
	
	public Token(int i) {
		this.i = i;
		this.color = 0;
	}
	
	public int getToken() {
		return this.color;
	}
	
	public void setToken(int color) {
		this.color = color;
	}
}

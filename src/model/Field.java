package model;

import java.util.*;

public class Field {

	private int x;
	private int y;
	private Token t;
	private Set<Field> neighbors;
	
	public Field(int x, int y) {
		this.x = x;
		this.y = y;
		this.t = Token.EMPTY;
		this.neighbors = new HashSet<>();
	}
	
	public void setToken(Token t) {
		this.t = t;
	}
	
	public Token getToken() {
		return this.t;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void addNeighbor(Field f) {
		neighbors.add(f);
	}
	
	public Set<Field> getNeighbors() {
		return this.neighbors;
	}
	
}

package model;

import java.util.*;

public class Field {

	private int i;
	private Token t;
	private Set<Field> neighbors;
	
	public Field(int i) {
		this.i = i;
		this.t = Token.EMPTY;
		this.neighbors = new HashSet<>();
	}
	
	public void setToken(Token t) {
		this.t = t;
	}
	
	public Token getToken() {
		return this.t;
	}
	
	public int getIndex() {
		return this.i;
	}
	
	public void addNeighbor(Field f) {
		neighbors.add(f);
	}
	
	public Set<Field> getNeighbors() {
		return this.neighbors;
	}
	
}

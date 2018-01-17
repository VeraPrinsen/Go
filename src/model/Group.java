package model;

import java.util.*;

public class Group {

	private Token t;
	private Set<Field> groupFields;
	private Set<Field> perimeterFields;
	
	public Group(Token t) {
		this.t = t;
		groupFields = new HashSet<>();
		perimeterFields = new HashSet<>();
	}
	
	//@ requires !getGroup().contains(f)
	public void add(Field f) {
		groupFields.add(f);
	}
	
	public Token getToken() {
		return this.t;
	}
	
	public Set<Field> getGroup() {
		return this.groupFields;
	}
	
	public Set<Field> getPerimeter() {
		return this.perimeterFields;
	}
	
	public void updatePerimeter() {
		// for each field in the group, look at all the adjacent location, if they are not the token of the group and not already in the perimeter, add it.
		for (Field f : groupFields) {
			Set<Field> neighbors = f.getNeighbors();
			for (Field n : neighbors) {
				if (!n.getToken().equals(this.t) && !perimeterFields.contains(n)) {
					perimeterFields.add(n);
				}
			}
		}
	}
}

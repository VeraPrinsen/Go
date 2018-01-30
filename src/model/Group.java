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
		f.setGroup(this);
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
	
	/**
	 * Checks if the group is captured by Token t.
	 */
	public boolean isCaptured(Token t) {
		boolean isCaptured = true;
		for (Field f : perimeterFields) {
			if (!f.getToken().equals(t)) {
				isCaptured = false;
				break;
			}
		}
		return isCaptured;
	}
	
	public String canCapture() {
		List<Field> emptyFields = new ArrayList<>();

		for (Field f : perimeterFields) {
			if (f.getToken().equals(Token.EMPTY)) {
				emptyFields.add(f);
			}
		}
		
		if (emptyFields.size() == 1) {
			return emptyFields.get(0).getX() + "_" + emptyFields.get(0).getY();
		} else {
			return "false";
		}
	}
	
	public int size() {
		return groupFields.size();
	}
}

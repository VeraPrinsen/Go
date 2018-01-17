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
		Iterator<Field> iterF = this.groupFields.iterator();
		
		// For each field in the group
		while (iterF.hasNext()) {
			Field tempF = iterF.next();
			Set<Field> neighbors = tempF.getNeighbors();
			Iterator<Field> iterN = neighbors.iterator();
			
			// look at all the adjacent locations
			while (iterN.hasNext()) {
				Field tempN = iterN.next();
				// if they are not the token of the group && not already in the perimeter, add it
				if (!tempN.getToken().equals(this.t) && !perimeterFields.contains(tempN)) {
					perimeterFields.add(tempN);
				}
			}
		}
	}
}

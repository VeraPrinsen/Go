package model;

import java.util.*;

public class Board {

	private Token[] board;
	private final int DIM;
	private int passes;
	private List<Group> groups;
	
	public Board(int DIM) {
		this.DIM = DIM;
		board = new Token[DIM*DIM];
		groups = new ArrayList<>();
		this.reset();
	}
	
	//@ ensures for all i: board[i] = Token.EMPTY;
	public void reset() {
		for (int i = 0; i < DIM*DIM; i++) {
			board[i] = new Token(i);
		}
		passes = 0;
	}
	
	public Board deepCopy() {
		Board newboard = new Board(DIM);
		for (int i = 0; i < DIM * DIM; i++) {
			newboard.setField(i, this.getField(i));
		}
		return newboard;
	}
	
	private int index(int x, int y) {
		// afhankelijk van protocol (x, y welke is rij, welke kolom. Beginnen we bij 0 of 1).
		return (DIM * x) + y;
	}

	// setField ================================================================================
	private void setField(int i, int color) {
		board[i].setToken(color);
		passes = 0;
	}
	
	public void setField(int x, int y, int color) {
		setField(index(x,y), color);
	}
	
	public void pass(Token t) {
		passes++;
	}
	
	// getField ================================================================================
	private int getField(int i) {
		return board[i].getToken();
	}
	
	public int getField(int x, int y) {
		return getField(index(x, y));
	}
	
	// Checks ===================================================================================
	public boolean isField(int index) {
		return (index < (DIM * DIM)) && (index >= 0);
	}
	
	public boolean isField(int x, int y) {
		return isField(index(x, y));
	}
	
	public boolean isEmptyField(int i) {
		return getField(i) == 0;
	}
	
	public boolean isEmptyField(int x, int y) {
		return isEmptyField(index(x, y));
	}
	
	//@ requires gameOver()
	public boolean isWinner(Token t) {
		return false;
	}
	
	public boolean gameOver() {
		return this.passes > 1;
	}
	
	// Check if groups are captured =============================================================
	
	
	// Scoring ===================================================================================
	
	
	
	// Make groups
	public void makeGroups() {
		// this should be done every time the board changes
		
		// 1) for each of the tokens (empty, white and black) and for each of the locations
		// 2) check if it is not already in a group
		// 3) if it is not, make a new group and add it
		// 4) while adding, look at the 4 adjacent locations
		// 5) if that location has a token of the same color and it is not already in the group, add it (recursive, go back to 4)
		
		// 6) Determine the perimeter (can be a new method)
		// 7) For each of the stones in the group, add the locations that are empty right beside them
	}
	
	// Check captured
	public void checkCaptured() {
		// this should be done every time the board changes, right after makeGroups()
		
		// 1) For each of the groups that is not empty
		// 2) Check all the perimeter locations for the tokens of the other color
		// 3) If each location of the perimeter is occupied by the other color, the group is captured and removed from the board.
	}
	
	public void removeGroup(Group g) {
		
	}
}

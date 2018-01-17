package model;

import java.util.*;
import boardGUI.*;

public class Board {

	private Field[] board;
	private final int DIM;
	private GOGUI gui;
	private int passes;
	private Set<Group> groups;

	public Board(int DIM, GOGUI gui) {
		this.DIM = DIM;
		this.board = new Field[DIM * DIM];
		this.gui = gui;
		this.groups = new HashSet<>();

		this.reset();
	}

	// @ ensures for all i: board[i].getToken() == Token.EMPTY;
	public void reset() {
		// Create a field for each location
		for (int i = 0; i < DIM * DIM; i++) {
			board[i] = new Field(i);
		}

		// Give each field its direct neighbors
		for (int i = 0; i < DIM; i++) {
			for (int j = 0; j < DIM; j++) {
				for (int di = -1; di <= 1; di = di + 2) {
					if ((i+di) >= 0 && (i+di) < DIM) {
						board[index(i, j)].addNeighbor(board[index(i+di, j)]);
					}
				}
				for (int dj = -1; dj <= 1; dj = dj + 2) {
					if ((j+dj) >= 0 && (j+dj) < DIM) {
						board[index(i, j)].addNeighbor(board[index(i, j+dj)]);
					}
				}
			}
		}

		// Set the amount of consecutive passes to zero
		passes = 0;
		update();
	}

	public Board deepCopy() {
		Board newboard = new Board(DIM, this.gui);
		for (int i = 0; i < DIM * DIM; i++) {
			newboard.setField(i, this.getField(i));
		}
		return newboard;
	}

	private int index(int x, int y) {
		// afhankelijk van protocol (x, y welke is rij, welke kolom. Beginnen we bij 0
		// of 1).
		return (DIM * x) + y;
	}

	// setField
	// ================================================================================
	private void setField(int i, Token t) {
		board[i].setToken(t);
		passes = 0;
		update();
	}

	public void setField(int x, int y, Token t) throws InvalidCoordinateException {
		setField(index(x, y), t);
		if (t.equals(Token.BLACK)) {
			gui.addStone(y, x, false);
		} else if (t.equals(Token.WHITE)) {
			gui.addStone(y, x, true);
		} else {
			// Cannot set an empty stone
		}
	}

	public void pass(Token t) {
		passes++;
	}

	// getField
	// ================================================================================
	private Token getField(int i) {
		return board[i].getToken();
	}

	public Token getField(int x, int y) {
		return getField(index(x, y));
	}

	// Checks
	// ===================================================================================
	public boolean isField(int index) {
		return (index < (DIM * DIM)) && (index >= 0);
	}

	public boolean isField(int x, int y) {
		return isField(index(x, y));
	}

	public boolean isEmptyField(int i) {
		return getField(i) == Token.EMPTY;
	}

	public boolean isEmptyField(int x, int y) {
		return isEmptyField(index(x, y));
	}

	// @ requires gameOver()
	public boolean isWinner(Token t) {
		return false;
	}

	public boolean gameOver() {
		return this.passes > 1;
	}

	// Check if groups are captured
	// =============================================================

	// Scoring
	// ===================================================================================

	public void update() {
		// update after a change is done to the board
		makeGroups();
		// 6) Determine the perimeter (can be a new method)
		updatePerimeters();
		// 7) For each of the stones in the group, add the locations that are empty
		// right beside them
		checkCaptured();
	}

	// Make groups ====================================================================================
	public void makeGroups() {
		// this should be done every time the board changes
		groups.clear();
		
		// 1) for each of the tokens (empty, white and black) and for each of the
		// locations
		for (Field f : board) {
			boolean inGroup = false;
			
			// 2) check if it is not already in a group
			for (Group g : groups) {
				if (g.getGroup().contains(f)) {
					inGroup = true;
					break;
				}
			}
			
			// 3) if it is not, make a new group and add it
			if (!inGroup) {
				Group newGroup = new Group(f.getToken());
				Group newGroup2 = addToGroup(newGroup, f.getIndex());
				groups.add(newGroup2);
			}
		}
	}

	// If a field is added to a group, look at the 4 adjacent neighbors and look if they have the same token.
	public Group addToGroup(Group g, int i) {
		g.add(board[i]);
		Group newgroup = null;
		
		// 4) while adding, look at the 4 adjacent locations
		Set<Field> neighbors = board[i].getNeighbors();
		
		// 5) if that location has a token of the same color and it is not already in
		// the group, add it (recursive, go back to 4)
		for (Field n : neighbors) {
			if (!g.getGroup().contains(n)) {
				newgroup = addToGroup(g, n.getIndex());
			}
		}
				
		return newgroup;
	}

	public void updatePerimeters() {
		// For each group, update the perimeter
		Iterator<Group> iterG = groups.iterator();

		while (iterG.hasNext()) {
			iterG.next().updatePerimeter();
		}
	}

	// Check captured
	public void checkCaptured() {
		// this should be done every time the board changes, right after makeGroups()

		Iterator<Group> iterG = groups.iterator();

		// 1) For each of the groups
		while (iterG.hasNext()) {
			Group tempG = iterG.next();
			boolean isCaptured = true;
			// that is not empty
			if (!tempG.getToken().equals(Token.EMPTY)) {
				Set<Field> perimeter = tempG.getPerimeter();
				Iterator<Field> iterP = perimeter.iterator();

				while (iterP.hasNext()) {
					Field tempP = iterP.next();
					if (tempP.getToken().equals(tempG.getToken()) || tempP.getToken().equals(Token.EMPTY)) {
						//
					} else {
						isCaptured = false;
						break;
					}
				}

				if (isCaptured) {
					System.out.println("Group is captured");
				}
			}
		}

		// 2) Check all the perimeter locations for the tokens of the other color
		// 3) If each location of the perimeter is occupied by the other color, the
		// group is captured and removed from the board.
	}

	public void removeGroup(Group g) {
		// Change all tokens in the group to empty and also change the group to an empty
		// group. This group then becomes the territory of the color that captured this
		// group.
	}
}

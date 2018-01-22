package model;

import java.util.*;

import boardView.*;

/**
 * This is the model that represents the board of GO.
 * 
 * @author vera.prinsen
 *
 */
// TO DO: EXCEPTION HANDLING
public class Board {

	private final int DIM;
	private Field[] board;
	private List<Group> groups;
	private int passes;

	private boolean useGUI;
	private GOGUI gui;

	public Board(int DIM, boolean useGUI) {
		this.DIM = DIM;
		this.board = new Field[DIM * DIM];
		this.useGUI = useGUI;
		if (useGUI) {
			this.gui = new GoGUIIntegrator(false, false, DIM);
		}
		this.groups = new ArrayList<>();

		try {
			this.reset();
		} catch (InvalidCoordinateException e) {
			e.printStackTrace();
		}

	}

	// RESETTERS & UPDATERS
	// =================================================================
	/**
	 * Resets the board to all empty spaces. And assigns the neighbors to all the
	 * fields.
	 */
	public void reset() throws InvalidCoordinateException {
		// Create a field for each location
		for (int i = 0; i < DIM; i++) {
			for (int j = 0; j < DIM; j++) {
				board[index(i, j)] = new Field(i, j);
			}
		}

		// Give each field its direct neighbors
		for (int i = 0; i < DIM; i++) {
			for (int j = 0; j < DIM; j++) {
				for (int di = -1; di <= 1; di = di + 2) {
					if ((i + di) >= 0 && (i + di) < DIM) {
						board[index(i, j)].addNeighbor(board[index(i + di, j)]);
					}
				}
				for (int dj = -1; dj <= 1; dj = dj + 2) {
					if ((j + dj) >= 0 && (j + dj) < DIM) {
						board[index(i, j)].addNeighbor(board[index(i, j + dj)]);
					}
				}
			}
		}

		// Set the amount of consecutive passes to zero
		passes = 0;
		update();

		if (useGUI) {
			gui.startGUI();
			gui.setBoardSize(DIM);
		}
	}

	/**
	 * If a change is made to the board, update all information.
	 */
	public void update() {
		// update after a change is done to the board
		makeGroups();
		// 6) Determine the perimeter (can be a new method)
		updatePerimeters();
		// 7) For each of the stones in the group, add the locations that are empty
		// right beside them
		checkCaptured();
	}

	/**
	 * For each group on the board, the perimeter must be determined.
	 */
	public void updatePerimeters() {
		// For each group, update the perimeter
		for (Group g : groups) {
			g.updatePerimeter();
		}
	}

	// GETTERS & SETTERS
	// ==================================================================
	public int getDIM() {
		return this.DIM;
	}

	// MISCELLANEOUS METHODS
	// ==============================================================
	/**
	 * Makes a copy of the current Board class.
	 */
	public Board deepCopy() {
		Board newboard = new Board(DIM, false);
		for (int i = 0; i < DIM * DIM; i++) {
			newboard.setField(i, this.getField(i));
		}
		return newboard;
	}

	/**
	 * Makes a copy of the current Field[] board.
	 */
	// TO DO: IMPLEMENT
	public Field[] fieldCopy() {
		return null;
	}

	/**
	 * If a player passed.
	 */
	public void pass(Token t) {
		passes++;
	}

	// INDEXERS
	// ===========================================================================
	/**
	 * From x and y coordinates to index i.
	 */
	private int index(int x, int y) {
		return (DIM * x) + y;
	}

	// GETTERS & SETTERS
	// ==============================================================================
	/**
	 * To get the token on field index i.
	 */
	private Token getField(int i) {
		return board[i].getToken();
	}

	/**
	 * To get the token on field (x, y)
	 */
	public Token getField(int x, int y) {
		return getField(index(x, y));
	}

	/**
	 * Put a token on field index i. This also resets the passes to zero and will
	 * update the boardinformation through update().
	 */
	private void setField(int i, Token t) {
		board[i].setToken(t);
		passes = 0;
		update();
	}

	/**
	 * Put a token on field (x, y). This is the method that is called from the Game
	 * Controllers.
	 */
	public void setField(int x, int y, Token t) {
		try {
			if (useGUI) {
				if (t.equals(Token.BLACK)) {
					gui.addStone(y, x, false);
				} else if (t.equals(Token.WHITE)) {
					gui.addStone(y, x, true);
				} else {
					// Cannot set an empty stone
				}
			}
			setField(index(x, y), t);
		} catch (InvalidCoordinateException e) {
			e.printStackTrace();
		}

	}

	// CHECKS
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

	/**
	 * Check for all groups, if a group is captured.
	 */
	// TO DO: FIRST CHECK ALL THE GROUPS OF THE OPPONENT
	public void checkCaptured() {
		// this should be done every time the board changes, right after makeGroups()

		// 1) For each of the groups
		for (Group g : groups) {
			boolean isCaptured = true;
			Token t = g.getToken();
			// that is not empty
			if (!t.equals(Token.EMPTY)) {
				Set<Field> perimeter = g.getPerimeter();
				// 2) Check all the perimeter locations for the tokens of the other color
				for (Field p : perimeter) {
					if (!p.getToken().equals(t.other())) {
						isCaptured = false;
						break;
					}
				}

				// 3) If each location of the perimeter is occupied by the other color, the
				// group is captured and removed from the board.
				if (isCaptured) {
					System.out.println("Group " + t.toString() + " is captured");
					removeGroup(g);
				}
			}
		}
	}

	// @ requires gameOver()
	public boolean isWinner(Token t) {
		return false;
	}

	public boolean gameOver() {
		return this.passes > 1;
	}

	// MAKE & CHANGE GROUPS
	// ==================================================================================
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
				int newGroupIndex = groups.size();
				Group newGroup = new Group(f.getToken());
				groups.add(newGroup);
				addToGroup(newGroupIndex, index(f.getX(), f.getY()));
			}
		}
	}

	// If a field is added to a group, look at the 4 adjacent neighbors and look if
	// they have the same token.
	public void addToGroup(int groupIndex, int fieldIndex) {
		// Group group = groups.get(groupIndex);
		groups.get(groupIndex).add(board[fieldIndex]);

		// 4) while adding, look at the 4 adjacent locations
		Set<Field> neighbors = board[fieldIndex].getNeighbors();

		// 5) if that location has a token of the same color and it is not already in
		// the group, add it (recursive, go back to 4)
		for (Field n : neighbors) {
			if ((n.getToken().equals(groups.get(groupIndex).getToken()))
					&& (!groups.get(groupIndex).getGroup().contains(n))) {
				addToGroup(groupIndex, index(n.getX(), n.getY()));
			}
		}
	}

	public void removeGroup(Group g) {
		// Change all tokens in the group to empty and also change the group to an empty
		// group. This group then becomes the territory of the color that captured this
		// group.
		for (Field f : g.getGroup()) {
			f.setToken(Token.EMPTY);
			if (useGUI) {
				try {
					gui.removeStone(f.getY(), f.getX());
				} catch (InvalidCoordinateException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

package model;

import java.util.*;

import boardview.*;

/**
 * This is the model that represents the board of GO.
 * 
 * @author vera.prinsen
 *
 */
public class Board {

	private final int dim;
	private Field[] board;
	private Set<String> previousBoards;
	private List<Group> groups;

	private boolean useGUI;
	private GOGUI gui;

	public Board(int dim, GOGUI gui) {
		useGUI = true;
		this.gui = gui;
		gui.startGUI();
		
		this.dim = dim;
		this.board = new Field[dim * dim];
		this.groups = new ArrayList<>();
		this.previousBoards = new HashSet<>();

		this.reset();
	}
	
	public Board(int dim) {
		this.dim = dim;
		useGUI = false;
		
		this.board = new Field[dim * dim];
		this.groups = new ArrayList<>();
		this.previousBoards = new HashSet<>();
		
		this.reset();		
	}

	// RESETTERS & UPDATERS
	// =================================================================
	/**
	 * Resets the board to all empty spaces. And assigns the neighbors to all the
	 * fields.
	 */
	private void reset() {
		// Create a field for each location
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				board[index(i, j)] = new Field(i, j);
			}
		}

		// Give each field its direct neighbors
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				for (int di = -1; di <= 1; di = di + 2) {
					if ((i + di) >= 0 && (i + di) < dim) {
						board[index(i, j)].addNeighbor(board[index(i + di, j)]);
					}
				}
				for (int dj = -1; dj <= 1; dj = dj + 2) {
					if ((j + dj) >= 0 && (j + dj) < dim) {
						board[index(i, j)].addNeighbor(board[index(i, j + dj)]);
					}
				}
			}
		}

		update(Token.EMPTY);

		if (useGUI) {		
			try {
				gui.setBoardSize(dim);
			} catch (InvalidCoordinateException e) {
				// Should not happen because it only resets the boardSize, it has nothing to do
				// with coordinates on the board... but the GUI gives this exception anyway.
			}
		}
	}

	/**
	 * If a change is made to the board, update all information.
	 */
	private void update(Token t) {
		// update after a change is done to the board
		makeGroups();
		// 6) Determine the perimeter (can be a new method)
		updatePerimeters();
		// 7) For each of the stones in the group, add the locations that are empty
		// right beside them
		if (!t.equals(Token.EMPTY)) {
			checkCaptured(t);
		}
	}

	/**
	 * For each group on the board, the perimeter must be determined.
	 */
	private void updatePerimeters() {
		// For each group, update the perimeter
		for (Group g : groups) {
			g.updatePerimeter();
		}
	}

	// GETTERS & SETTERS
	// ==================================================================
	public int getDIM() {
		return this.dim;
	}

	public Field[] getFields() {
		return board;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public int getScore(Token t) {
		int score = 0;

		for (Group g : groups) {
			if (g.getToken().equals(Token.EMPTY) && g.isCaptured(t)) {
				score = score + g.size();
			}
		}

		for (Field f : board) {
			if (f.getToken().equals(t)) {
				score++;
			}
		}

		return score;
	}
	
	public void stopGUI() {
		if (useGUI) {
			gui = null;
		}
	}
	
	public List<Field> getEmptyFields() {
		List<Field> emptyFields = new ArrayList<>();
		for (int i = 0; i < dim * dim; i++) {
			if (board[i].getToken().equals(Token.EMPTY)) {
				emptyFields.add(board[i]);
			}
		}
		
		return emptyFields;		
	}

	// MISCELLANEOUS METHODS
	// ==============================================================
	/**
	 * Make a copy of the whole Board class.
	 */
	public Board boardCopy() {
		Board newboard = new Board(dim);
		for (int i = 0; i < board.length; i++) {
			newboard.setField(i, board[i].getToken());
		}
		return newboard;
	}

	/**
	 * Makes a copy of the current Field[] board.
	 */
	public Field[] fieldCopy() {
		Field[] newfield = new Field[board.length];
		for (int i = 0; i < board.length; i++) {
			newfield[i] = new Field(board[i].getX(), board[i].getY());
			newfield[i].setToken(board[i].getToken());
		}
		return newfield;
	}
	
	/** 
	 * Makes a unique code for the current board and stones on it.
	 * In the string each black stone will be defined by "B", "W" for white and "E" for empty.
	 */
	public String fieldString() {
		String s = "";
		for (Field f : board) {
			s = s + f.getToken();
		}
		return s;
	}

	// INDEXERS
	// ===========================================================================
	/**
	 * From x and y coordinates to index i.
	 */
	private int index(int x, int y) {
		return (dim * x) + y;
	}

	// GETTERS & SETTERS
	// ==============================================================================
	private Field getBoardField(int i) {
		return board[i];
	}
	
	public Field getBoardField(int x, int y) {
		return getBoardField(index(x, y));
	}
	
	/**
	 * To get the token on field index i.
	 */
	private Token getField(int i) {
		return board[i].getToken();
	}

	/**
	 * To get the token on field (x, y).
	 */
	public Token getField(int x, int y) {
		return getField(index(x, y));
	}

	/**
	 * Put a token on field index i. This also resets the passes to zero and will
	 * update the board information through update().
	 */
	private void setField(int i, Token t) {
		board[i].setToken(t);
		update(t);
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
				}
			}
			previousBoards.add(fieldString());
			setField(index(x, y), t);
		} catch (InvalidCoordinateException e) {
			// The location will have been checked already, should not occur.
		}

	}

	/**
	 * Adds the hint indicator to the board.
	 */
	public void setHintField(int x, int y) {
		try {
			if (useGUI) {
				gui.addHintIndicator(x, y);
			}
		} catch (InvalidCoordinateException e) {
			// The location will have been checked already, should not occur.
		}
	}
	
	/**
	 * Removes the hint indicator from the board.
	 */
	public void removeHintField() {
		gui.removeHintIdicator();
	}

	// CHECKS
	// ===================================================================================
	public boolean checkMove(int x, int y, Token t) throws InvalidCoordinateException {
		if (!isField(x, y)) {
			throw new InvalidCoordinateException("The coordinates lie not on the game board.");
		} else if (!isEmptyField(x, y)) {
			throw new InvalidCoordinateException("The field on the board is not empty.");
		} else {
			Board nextBoard = boardCopy();
			nextBoard.setField(index(x, y), t);
			if (previousBoards.contains(nextBoard.fieldString()) 
					|| this.fieldString().equals(nextBoard.fieldString())) {
				throw new InvalidCoordinateException("Cannot make a move that will result in "
						+ "a boardstate that has already been there.");
			}
		}

		return true;
	}
	
	public boolean canBeCaptured(Token t) {
		for (Group g : groups) {
			if (g.getToken().equals(t)) {
				String outcome = g.canCapture();
				if (!outcome.equals("false")) {
					return true;
				}
			}
		}
		return false;
	}
	
	public List<Group> hasAreas(Token t) {
		List<Group> newlist = new ArrayList<>();
		
		for (Group g : groups) {
			if (g.getToken().equals(Token.EMPTY)) {
				if (g.isCaptured(t)) {
					newlist.add(g);
				}
			}
		}
		
		return newlist;
	}
	
	// Only for Empty groups, if isCaptured by token t
	public boolean emptyIsCaptured(Token t) {
		for (Group g : groups) {
			if (g.getToken().equals(Token.EMPTY)) {
				if (g.isCaptured(t)) {
					return true;
				}
			}
		}
		return false;
	} 

	private boolean isField(int index) {
		return (index < (dim * dim)) && (index >= 0);
	}

	public boolean isField(int x, int y) {
		return isField(index(x, y));
	}

	private boolean isEmptyField(int i) {
		return getField(i) == Token.EMPTY;
	}

	public boolean isEmptyField(int x, int y) {
		return isEmptyField(index(x, y));
	}

	/**
	 * Check for all groups, if a group is captured by the given argument token.
	 */
	private void checkCaptured(Token token) {
		// this should be done every time the board changes, right after makeGroups()

		Token[] tokenArray = {token.other(), token};

		for (Token currentToken : tokenArray) {
			for (Group g : groups) {
				boolean isCaptured = true;
				Token t = g.getToken();
				// that is not empty
				if (t.equals(currentToken)) {
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
						removeGroup(g);
					}
				}
			}
		}
	}

	// MAKE & CHANGE GROUPS
	// ==================================================================================
	private void makeGroups() {
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
	private void addToGroup(int groupIndex, int fieldIndex) {
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

	private void removeGroup(Group g) {
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

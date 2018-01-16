package model;

public class Board {

	private Token[] board;
	private final int DIM;
	
	public Board(int DIM) {
		this.DIM = DIM;
		board = new Token[DIM*DIM];
		this.reset();
	}
	
	//@ ensures for all i: board[i] = Token.EMPTY;
	public void reset() {
		for (int i = 0; i < DIM*DIM; i++) {
			board[i] = Token.EMPTY;
		}
	}
	
	private int index(int x, int y) {
		// afhankelijk van protocol (x, y welke is rij, welke kolom. Beginnen we bij 0 of 1).
		return 0;
	}
	
	// setStone ================================================================================
	private void setStone(int i, Token t) {
		
	}
	
	public void setStone(int x, int y, Token t) {
		setStone(index(x,y), t);
	}
	
	// getStone ================================================================================
	private Token getStone(int i) {
		return null;
	}
	
	public Token getStone(int x, int y) {
		return getStone(index(x, y));
	}
	
	
	
	
}

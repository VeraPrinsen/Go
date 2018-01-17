package model;

import boardGUI.*;

public class TestBoard {

	public static void main(String[] args) throws InvalidCoordinateException {
		int DIM = 9;
        Board board = new Board(DIM);
                
        board.setField(0, 1, Token.BLACK);
        board.setField(1, 1, Token.WHITE);
        board.setField(1, 0, Token.BLACK);
        board.setField(1, 2, Token.BLACK);
        board.setField(2, 1, Token.BLACK);
        
        System.out.println("Done");
    }
}

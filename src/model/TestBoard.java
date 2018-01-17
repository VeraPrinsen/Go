package model;

import boardGUI.*;

public class TestBoard {

	public static void main(String[] args) throws InvalidCoordinateException {
		int DIM = 5;
        GoGUIIntegrator gogui = new GoGUIIntegrator(true, true, DIM);
        Board board = new Board(DIM, gogui);
        
        gogui.startGUI();
        gogui.setBoardSize(DIM);
        
        board.setField(0, 0, Token.BLACK);
        board.setField(1, 0, Token.BLACK);
        board.setField(0, 1, Token.WHITE);
        board.setField(1, 1, Token.BLACK);
        
        System.out.println("Done");
    }
}

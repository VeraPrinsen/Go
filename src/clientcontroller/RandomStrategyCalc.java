package clientcontroller;

import java.util.ArrayList;
import java.util.List;

import boardview.InvalidCoordinateException;
import model.Board;
import model.Field;
import model.Group;
import model.Token;

public class RandomStrategyCalc implements Runnable {

	private Game game;
	private String bestMove;
	private String bestStrategy;

	public RandomStrategyCalc(Game game) {
		this.game = game;
		bestMove = "random";
		bestStrategy = "random";
	}

	public void run() {
		Token playerToken = game.getToken();
		Board board = game.getBoard();
		int dim = board.getDIM();
		
		if (board.fieldString().contains(Token.EMPTY.toString())) {
			boolean moveOK = false;
			
			while (!moveOK) {
				// Board has empty fields
				List<Field> emptyFields = board.getEmptyFields();
				int index = (int) Math.floor(Math.random() * emptyFields.size());
				int x = emptyFields.get(index).getX();
				int y = emptyFields.get(index).getY();
				try {
					if (board.checkMove(x, y, playerToken)) {
						bestMove = x + "_" + y;
						bestStrategy = "random / move";
						moveOK = true;
					}
				} catch (InvalidCoordinateException e) {
					// Move is not valid.
				}
			}			
		} else {
			// Board has no empty fields
			bestMove = "pass";
			bestStrategy = "random / pass";
		}
	}

	public String getBestMove() {
		return this.bestMove;
	}
	
	public String getBestStrategy() {
		return this.bestStrategy;
	}

}

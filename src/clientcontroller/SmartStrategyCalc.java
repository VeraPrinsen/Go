package clientcontroller;

import java.util.ArrayList;
import java.util.List;

import boardview.InvalidCoordinateException;
import model.Board;
import model.Field;
import model.Group;
import model.Token;

public class SmartStrategyCalc implements Runnable {

	private Game game;
	private String bestMove;
	private String bestStrategy;

	public SmartStrategyCalc(Game game) {
		this.game = game;
		bestMove = "random";
		bestStrategy = "random";
	}

	public void run() {
		Token playerToken = game.getToken();
		Board board = game.getBoard();
		int DIM = board.getDIM();
		
		// Long running work
		// 1) If the opponent has passed, and you have the higher score at the moment, also pass
		if (game.getPasses() == 1) {
			int playerScore = game.getBoard().getScore(game.getToken());
			int opponentScore = game.getBoard().getScore(game.getToken().other());

			if (playerScore > opponentScore) {
				bestMove = "pass";
				bestStrategy = "pass after opponent passed";
			}
		}

		// 2) If a group of the other token can be captured, do this
		List<Group> groups = board.getGroups();
		for (Group g : groups) {
			if (g.getToken().equals(playerToken.other())) {
				List<Field> emptyFields = new ArrayList<>();
				for (Field p : g.getPerimeter()) {
					if (p.getToken().equals(Token.EMPTY)) {
						emptyFields.add(p);
					}
				}

				if (emptyFields.size() == 1) {
					int x = emptyFields.get(0).getX();
					int y = emptyFields.get(0).getY();

					try {
						if (board.checkMove(x, y, playerToken)) {
							bestMove = x + "_" + y;
							bestStrategy = "capture a group";
						}
					} catch (InvalidCoordinateException e) {
						// Is an invalid move
					}

				}
			}
		}
		
		// 3) More strategy moves....
		

	}

	public String getBestMove() {
		return this.bestMove;
	}
	
	public String getBestStrategy() {
		return this.bestStrategy;
	}

}

package clientController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import boardView.InvalidCoordinateException;
import model.Board;
import model.Field;
import model.Group;
import model.Token;

public class SmartStrategy implements Strategy {
	
	private Player player;
	
	public SmartStrategy(Player player) {
		this.player = player;
	}

	public String sendMove() {		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
		Game game;
		game = player.getGame();
		Board board = game.getBoard();
		int DIM = board.getDIM();

		// 1) If the opponent has passed, and you have the higher score at the moment,
		// also pass
		if (game.getPasses() == 1) {
			int playerScore = game.getBoard().getScore(game.getToken());
			int opponentScore = game.getBoard().getScore(game.getToken().other());

			if (playerScore > opponentScore) {
				return "pass";
			}
		}
		
		// 2) If a group of the other token can be captured, do this
		Token playerToken = game.getToken();
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
							return x + "_" + y;
						}
					} catch (InvalidCoordinateException e) {
						// Is an invalid move
					}
				
				}
			}
		}
		
		// 3) Random, First determine which fields are empty
		List<Integer> emptyX = new ArrayList<>();
		List<Integer> emptyY = new ArrayList<>();

		for (int i = 0; i < DIM; i++) {
			for (int j = 0; j < DIM; j++) {
				if (board.isEmptyField(i, j)) {
					try {
						if (board.checkMove(i, j, playerToken)) {
							emptyX.add(i);
							emptyY.add(j);
						}
					} catch (InvalidCoordinateException e) {
						// Is an invalid move
					}		
				}
			}
		}
		if (emptyX.size() == 0) {
			return "pass";
		} else {
			int index = (int) Math.floor(Math.random() * emptyX.size());
			return emptyX.get(index) + "_" + emptyY.get(index);
		}
	}
}

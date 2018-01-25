package clientController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

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
		Game game;
		while ((game = player.getGame()) == null || (game  = player.getGame()).getBoard() == null) {
			// Wait until the game is made.
		}
		
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
		List<Group> groups = board.getGroups();
		for (Group g : groups) {
			if (g.getToken().equals(game.getToken().other())) {
				List<Field> emptyFields = new ArrayList<>();
				for (Field p : g.getPerimeter()) {
					if (p.getToken().equals(Token.EMPTY)) {
						emptyFields.add(p);
					}
				}
				if (emptyFields.size() == 1) {
					return emptyFields.get(0).getX() + "_" + emptyFields.get(0).getY();
				}
			}
		}

		// 3) Random, First determine which fields are empty
		List<Integer> emptyX = new ArrayList<>();
		List<Integer> emptyY = new ArrayList<>();

		for (int i = 0; i < DIM; i++) {
			for (int j = 0; j < DIM; j++) {
				if (board.isEmptyField(i, j)) {
					emptyX.add(i);
					emptyY.add(j);
				}
			}
		}
		int index = (int) Math.floor(Math.random() * emptyX.size());

		return emptyX.get(index) + "_" + emptyY.get(index);
	}
}

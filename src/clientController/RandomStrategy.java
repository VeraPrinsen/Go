package clientController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import model.Board;

public class RandomStrategy implements Strategy {

	private Player player;
	
	public RandomStrategy(Player player) {
		this.player = player;
	}

	public String sendMove() {
		Game game;
		while ((game = player.getGame()) == null || (game  = player.getGame()).getBoard() == null) {
			// Wait until the game is made.
		}
		
		Board board = game.getBoard();
		int DIM = board.getDIM();
		
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

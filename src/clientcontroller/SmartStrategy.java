package clientcontroller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import boardview.InvalidCoordinateException;
import model.Board;
import model.Field;
import model.Group;
import model.Token;

public class SmartStrategy implements Strategy {

	private Player player;
	private String bestMove;
	private String bestStrategy;
	private int reactionTimeAI;

	public SmartStrategy(Player player, int reactionTimeAI) {
		this.player = player;
		this.reactionTimeAI = reactionTimeAI;
	}

	public String sendMove() {
		long startTime = System.currentTimeMillis();
		
		Game game;
		game = player.getGame();
		Token playerToken = game.getToken();
		Board board = game.getBoard();
		int DIM = board.getDIM();

		SmartStrategyCalc newCalc = new SmartStrategyCalc(game);
		Thread t = new Thread(newCalc);
		long endTime = startTime + 1000 * reactionTimeAI;

		t.start(); // Kick off calculations

		while (System.currentTimeMillis() < endTime) {
		    // Still within time theshold, wait a little longer
		    try {
		         Thread.sleep(100);  // Sleep 1/2 second
		    } catch (InterruptedException e) {
		         // Someone woke us up during sleep, that's OK
		    }
		}

		t.interrupt();  // Tell the thread to stop
		    
		bestMove = newCalc.getBestMove();
		bestStrategy = newCalc.getBestStrategy();
		
		if (bestMove.equals("random")) {
			// No bestMove is saved in the AI reaction time, do a random move:
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
				bestMove = "pass";
			} else {
				int index = (int) Math.floor(Math.random() * emptyX.size());
				bestMove = emptyX.get(index) + "_" + emptyY.get(index);
			}
		} 
		
		System.out.println(bestStrategy);
		return bestMove;
	}
}

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

public class RandomStrategy implements Strategy {

	private Player player;
	private String bestMove;
	private String bestStrategy;
	private int reactionTimeAI;

	public RandomStrategy(Player player, int reactionTimeAI) {
		this.player = player;
		this.reactionTimeAI = reactionTimeAI;
	}

	public String sendMove() {
		long startTime = System.currentTimeMillis();
		long endTime = startTime + 1000 * reactionTimeAI;
				
		RandomStrategyCalc newCalc = new RandomStrategyCalc(player.getGame());
		Thread t = new Thread(newCalc, "RandomStrategyCalc");
		
		t.start(); // Kick off calculations
		while ((System.currentTimeMillis() < endTime) && t.isAlive()) {
		    // Still within time theshold, wait a little longer
		    try {
		         Thread.sleep(50L);  // Sleep 1/2 second
		    } catch (InterruptedException e) {
		         // Someone woke us up during sleep, that's OK
		    }
		}
		// t.interrupt();
		
		bestMove = newCalc.getBestMove();
		bestStrategy = newCalc.getBestStrategy();
		
		System.out.println(bestStrategy);
		return bestMove;
	}
}

package clientcontroller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import model.*;

public class ComputerPlayer implements Player {

	private ServerHandler sh;
	private Game game;
	private Strategy strategy;

	public ComputerPlayer(ServerHandler sh, int reactionTimeAI) {
		this.sh = sh;
		strategy = new SmarterStrategy(this, reactionTimeAI);
	}

	public void setGame(Game game) {
		this.game = game;
	}
	
	public Game getGame() {
		return this.game;
	}

	public void sendMove() {
		String move = strategy.sendMove();
		
		if (move.equalsIgnoreCase("pass")) {
			game.sendPass();
		} else {
			String[] coords = move.split("_");
			game.sendMove(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
		}
		
	}
}

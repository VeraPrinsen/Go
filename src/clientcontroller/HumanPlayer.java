package clientcontroller;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import general.Protocol;

public class HumanPlayer implements Player {

	private ServerHandler sh;
	private Game game;
	private Strategy hintStrategy;
	
	public HumanPlayer(ServerHandler sh) {
		this.sh = sh;
		game = null;
		hintStrategy = new SmartStrategy(this, 1);
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	public Game getGame() {
		return this.game;
	}
	
	public void sendMove() {
		// First let the hint functionality show a spot to place the stone
		String hintMove = hintStrategy.sendMove();
		
		if (hintMove.equalsIgnoreCase("pass")) {
			sh.print("The hint is to pass this round.");
		} else {
			String[] coords = hintMove.split("_");
			game.getBoard().setHintField(Integer.parseInt(coords[1]), Integer.parseInt(coords[0]));
		}
		
		boolean xOK = false;
		int x = 0;
		while (!xOK) {
			String xString = sh.readString("On what row you want to place your stone?");
			
			if (xString.equals(Protocol.Client.QUIT)) {
				sh.sendQuit();
				return;
			} else if (xString.equalsIgnoreCase("pass")) {
				game.sendPass();
				return;
			} else {
				try {
					x = Integer.parseInt(xString);
					if (x >= game.getBoard().getDIM()) {
						sh.print("The x coordinate lies not on the board.");
					} else {
						xOK = true;
					}
				} catch (NumberFormatException e) {
					sh.print("You must either pass or enter an integer.");
				}
			}
		}
		
		boolean yOK = false;
		int y = 0;
		while (!yOK) {
			String yString = sh.readString("On what column do you want to place your stone?");
			
			if (yString.equals(Protocol.Client.QUIT)) {
				sh.sendQuit();
				return;
			} else if (yString.equalsIgnoreCase("pass")) {
				game.sendPass();
				return;
			} else {
				try {
					y = Integer.parseInt(yString);
					if (y >= game.getBoard().getDIM()) {
						sh.print("The y coordinate lies not on the board.");
					} else {
						xOK = true;
					}
				} catch (NumberFormatException e) {
					sh.print("You must either pass or enter an integer.");
				}
			}
		}
		
		game.getBoard().removeHintField();
		game.sendMove(x, y);
	}
	
}

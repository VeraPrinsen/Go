package clientcontroller;

import general.Protocol;

/**
 * HumanPlayer is a Player that asks the user of the client to provide a move.
 * @author vera.prinsen
 *
 */
public class HumanPlayer implements Player {

	private ServerHandler sh;
	private Game game;
	private Strategy hintStrategy;
	
	public HumanPlayer(ServerHandler sh) {
		this.sh = sh;
		game = null;
		hintStrategy = new RandomStrategy(this, 1); // hint must be shown within 1 second
	}
	
	// GETTERS & SETTERS ================================================
	public void setGame(Game game) {
		this.game = game;
	}
	
	public Game getGame() {
		return this.game;
	}
	
	// GAME MECHANICS ====================================================
	/**
	 * When it is the HumanPlayer it's turn, this method is called.
	 * It will ask the user to make a move.
	 */
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
			} else if (xString.equals(Protocol.Client.EXIT)) {
				sh.sendQuit();
				sh.shutDown();
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
			} else if (yString.equals(Protocol.Client.EXIT)) {
				sh.sendQuit();
				sh.shutDown();
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
						yOK = true;
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

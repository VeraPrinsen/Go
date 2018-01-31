package clientcontroller;

/**
 * ComputerPlayer is a Player that determines a move automatically using a Strategy.
 * @author vera.prinsen
 *
 */
public class ComputerPlayer implements Player {

	private Game game;
	private Strategy strategy;

	public ComputerPlayer(int reactionTimeAI) {
		strategy = new SmarterStrategy(this, reactionTimeAI);
	}

	// GETTERS & SETTERS ===================================================
	public void setGame(Game game) {
		this.game = game;
	}
	
	public Game getGame() {
		return this.game;
	}

	// GAME MECHANICS ======================================================
	/**
	 * When it is the ComputerPlayer it's turn, this method is called.
	 * It will determine the best move and send it to the server.
	 */
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

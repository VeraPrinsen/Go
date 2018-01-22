package clientController;

import general.Protocol;

public class HumanPlayer implements Player {

	private ServerHandler sh;
	private Game game;
	
	public HumanPlayer(ServerHandler sh) {
		this.sh = sh;
		game = null;
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	public void sendMove() {
		int x = sh.client.readInt("On what row you want to place your stone?");
		
		// exit is typed
		if (x == -1) {
			return;
		}
		
		int y = sh.client.readInt("On what column do you want to place your stone?");
		
		// exit is typed
		if (y == -1) {
			return;
		}
		
		game.sendMove(x, y);
	}
	
}

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
		String x = sh.client.readString("On what row you want to place your stone?");
		
		if (x.equals(Protocol.Client.QUIT)) {
			return;
		} else if (x.equalsIgnoreCase("pass")) {
			game.sendPass();
			return;
		}
		
		String y = sh.client.readString("On what column do you want to place your stone?");
		
		if (y.equals(Protocol.Client.QUIT)) {
			return;
		} else if (y.equalsIgnoreCase("pass")) {
			game.sendPass();
			return;
		}
		
		game.sendMove(Integer.parseInt(x), Integer.parseInt(y));
	}
	
}

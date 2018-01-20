package serverclientconnection;

import general.Protocol;

public class HumanPlayer implements Player {

	private ServerHandler sh;
	
	public HumanPlayer(ServerHandler sh) {
		this.sh = sh;
	}
	
	public void sendMove() {
		int x = sh.client.readInt("On what row you want to place your stone?");
		int y = sh.client.readInt("On what column do you want to place your stone?");
		
		sh.makeMove(x, y);
	}
	
}

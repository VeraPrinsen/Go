package clientController;

import general.Protocol;
import model.Board;
import model.Token;

/**
 * The game controller for the client.
 * @author vera.prinsen
 *
 */
public class Game {
	
	private ServerHandler sh;
	
	private int numberPlayers;
	private final int DIM;
	private Player player;
	private String opponent;
	private Token token;
	
	private Board board;
	
	public Game(ServerHandler sh, int numberPlayers, int DIM, Player player, String opponent, String color) {
		this.sh = sh;
		
		this.numberPlayers = numberPlayers;
		this.DIM = DIM;
		this.player = player;
		this.opponent = opponent;
		
		if (color.equals(Protocol.General.BLACK)) {
			this.token = Token.BLACK;
		} else { 
			this.token = Token.WHITE;
		}
		
		board = new Board(DIM, true);
	}
	
	public void setMovePlayer(int x, int y, boolean isPlayer) {
		if (isPlayer) {
			setMove(x, y, token);
		} else {
			setMove(x, y, token.other());
		}
	}
	
	public void setMove(int x, int y, Token t) {
		board.setField(x, y, t);
	}
	
	public void sendMove(int x, int y) {
		String message = Protocol.Client.MOVE + Protocol.General.DELIMITER1 + x + Protocol.General.DELIMITER2 + y;
		sh.send(message);
	}
	
	public Board getBoard() {
		return this.board;
	}

}

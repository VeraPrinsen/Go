package clientcontroller;

import boardview.InvalidCoordinateException;
import general.Protocol;
import model.Board;
import model.Token;

/**
 * The game controller for the client.
 * 
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
	private int passes;

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
		passes = 0;
	}

	// GETTERS & SETTERS ===========================================================================
	public Board getBoard() {
		return this.board;
	}
	
	public Token getToken() {
		return this.token;
	}
	
	public int getPasses() {
		return this.passes;
	}

	// PRINTERS & SENDERS =========================================================================
	public void print(String msg) {
		sh.print(msg);
	}
	
	public void send(String msg) {
		sh.send(msg);
	}
	
	/**
	 * The methods called by the players to make a move. The client itself checks if the move is valid.
	 * If not, an error is shown on the The move is first send to
	 * the server.
	 */
	public void sendMove(int x, int y) {
		boolean validMove = false;
		try {
			validMove = board.checkMove(x, y, token);
		} catch (InvalidCoordinateException e) {
			print(e.getMessage());
		}
		
		if (validMove) {
			String message = Protocol.Client.MOVE + Protocol.General.DELIMITER1 + x + Protocol.General.DELIMITER2 + y;
			send(message);
		} else {
			player.sendMove();
		}
	}

	public void sendPass() {
		String message = Protocol.Client.MOVE + Protocol.General.DELIMITER1 + Protocol.Client.PASS;
		sh.send(message);
	}
	
	/**
	 * After the move is send to the server, when the server returns the message that
	 * the move is valid, it is made in the model through these methods.
	 */
	public void setMovePlayer(int x, int y, boolean isPlayer) {
		if (isPlayer) {
			setMove(x, y, token);
		} else {
			setMove(x, y, token.other());
		}
	}
	
	public void setMove(int x, int y, Token t) {
		board.setField(x, y, t);
		passes = 0;
	}

	public void setPass() {
		passes++;
	}

	// CHECKS ==================================================================================
	public boolean gameOver() {
		return passes > 1;
	}

}

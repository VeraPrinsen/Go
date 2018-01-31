package clientcontroller;

import boardview.GOGUI;
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
	private Player player;
	private Token token;

	private Board board;
	private int passes;
	private int moves;

	public Game(ServerHandler sh, int dim, Player player, String color, GOGUI gui) {
		this.sh = sh;
		this.player = player;

		if (color.equals(Protocol.General.BLACK)) {
			this.token = Token.BLACK;
		} else {
			this.token = Token.WHITE;
		}

		this.board = new Board(dim, gui);
		this.passes = 0;
		this.moves = dim * dim;
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
	
	public int getMoves() {
		return this.moves;
	}
	
	// PRINTERS & SENDERS =========================================================================
	/** 
	 * To print a message on the console of the client.
	 */
	public void print(String msg) {
		sh.print(msg);
	}
	
	/**
	 * To send a message to the server.
	 */
	public void send(String msg) {
		sh.send(msg);
	}
	
	/**
	 * The methods called by the players to make a move. The client checks if the move is valid.
	 * If not valid, an error is shown on the console.
	 * Before the move is set on the board, the move is send to the server and the client will
	 * await the server's confirmation.
	 */
	public void sendMove(int x, int y) {
		boolean validMove = false;
		try {
			validMove = board.checkMove(x, y, token);
		} catch (InvalidCoordinateException e) {
			print(e.getMessage());
		}
		
		if (validMove) {
			String message = Protocol.Client.MOVE + Protocol.General.DELIMITER1 + x 
					+ Protocol.General.DELIMITER2 + y;
			send(message);
		} else {
			player.sendMove();
		}
	}

	/**
	 * The method called by the players to pass instead of making a move.
	 * Before the pass is made in the game, the move is send to the server and the client will
	 * await the server's confirmation.
	 */
	public void sendPass() {
		String message = Protocol.Client.MOVE + Protocol.General.DELIMITER1 + Protocol.Client.PASS;
		sh.send(message);
	}
	
	/**
	 * After the move is send to the server, when the server returns the message that
	 * the move is valid, it is made in the model through these methods.
	 */
	public void setMovePlayer(int x, int y, boolean isPlayer) {
		moves--;
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
		return (passes > 1) || (moves <= 0);
	}

}

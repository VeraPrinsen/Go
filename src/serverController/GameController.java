package serverController;

import general.Protocol;
import model.Board;
import model.Token;

/**
 * Game controls one game between two players.
 * @author vera.prinsen
 *
 */
public class GameController implements Runnable {

	private ClientHandler[] players;
	
	private String[] playerColor;
	private Token[] playerToken;
	private int numberPlayers = 2;
	private int currentPlayer;
	private Board board;
	
	public GameController(ClientHandler ch1, ClientHandler ch2) {
		this.players = new ClientHandler[numberPlayers];
		this.playerColor = new String[numberPlayers];
		this.playerToken = new Token[numberPlayers];
		this.players[0] = ch1;
		this.players[1] = ch2;
	}
	
	// STARTERS & STOPPERS ==========================================================
	/**
	 * Before a game can start, both players have the game added to their clientHandler and the first player must choose a color and the boardsize.
	 */
	public void run() {
		// Make a game for both clientHandler on this server
		players[0].startGame(this, 0, players[1]);
		players[1].startGame(this, 1 , players[0]);
		
		// Send the first player the START command and request color and boardsize
		players[0].sendRequestSettings();
	}
	
	// GETTERS & SETTERS ==============================================================
	/**
	 * Set the boardsize after the first player has send their desires settings.
	 */
	public void setBoard(int DIM, boolean useGUI) {
		board = new Board(DIM, useGUI);
	}
	
	/**
	 * Set first and second player and color of the players after the first player has send their desired settings.
	 */
	public void setColors(ClientHandler player, String color) {
		if (player.equals(players[0])) {
			playerColor[0] = color;
			playerColor[1] = other(color);
		} else {
			playerColor[1] = color;
			playerColor[0] = other(color);
		}
		
		if (playerColor[0].equals(Protocol.General.BLACK)) {
			currentPlayer = 0;
		} else {
			currentPlayer = 1;
		}
		
		if (playerColor[0].equals(Protocol.General.BLACK)) {
			playerToken[0] = Token.BLACK;
			playerToken[1] = Token.WHITE;
		} else {
			playerToken[0] = Token.WHITE;
			playerToken[1] = Token.BLACK;
		}
	}
	
	// GAME MECHANICS =================================================================
	/**
	 * If a player makes a move, this method changes it in the board.
	 */
	public void makeMove(int x, int y, int playerNo) {
		try {
			board.setField(x, y, playerToken[playerNo]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// MISCELLANEOUS METHODS ========================================================
	/**
	 * Return the color that if the opposite of the one that was entered.
	 */
	public String other(String color) {
		if (color.equals(Protocol.General.BLACK)) {
			return Protocol.General.WHITE;
		} else
			return Protocol.General.BLACK;
	}
	
	// PRINTERS & SENDERS ===========================================================
	/**
	 * After the settings are send by the first player, this command is send to start the game for real.
	 */
	public void sendStart() {
		players[0].sendStart(numberPlayers, playerColor[0], board.getDIM(), players[currentPlayer].getName(), players[Math.abs(currentPlayer-1)].getName());
		players[1].sendStart(numberPlayers, playerColor[1], board.getDIM(), players[currentPlayer].getName(), players[Math.abs(currentPlayer-1)].getName());
		
		players[currentPlayer].sendFirst();
	}
	
	/**
	 * Send the move that player playerNo played to the other player.
	 */
	public void sendMove(int x, int y, int playerNo) {
		players[playerNo].sendValidMove(x, y);
		players[Math.abs(playerNo-1)].sendMove(x, y);
	}
	
	/**
	 * Send to the other player that player playerNo passed.
	 */
	public void sendPass(int playerNo) {
		players[Math.abs(playerNo-1)].sendPass();
	}
}

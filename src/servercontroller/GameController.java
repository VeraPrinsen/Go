package servercontroller;

import boardview.GOGUI;
import boardview.InvalidCoordinateException;
import general.Protocol;
import model.*;

/**
 * Game controls one game between two players.
 * @author vera.prinsen
 *
 */
public class GameController {

	private int numberPlayers = 2;
	private ClientHandler[] players;
	private String[] playerColor;
	private Token[] playerToken;
	private int currentPlayer = -1;
	private long startTime;
	private Board board;
	private int passes;
	private int moves;
	private boolean isRunning;
	
	public GameController(ClientHandler ch1, ClientHandler ch2) {
		this.players = new ClientHandler[numberPlayers];
		this.playerColor = new String[numberPlayers];
		this.playerToken = new Token[numberPlayers];
		this.players[0] = ch1;
		this.players[1] = ch2;
		isRunning = true;
	}
	
	// STARTERS & STOPPERS ==========================================================
	/**
	 * Before a game can start, both players have the game added to their clientHandler and the first player must choose a color and the boardsize.
	 */
	public void setup() {
		// Make a game for both clientHandler on this server
		players[0].startGame(this, 0, players[1]);
		players[1].startGame(this, 1, players[0]);
		
		// Send the first player the START command and request color and boardsize
		players[0].sendRequestSettings();
	}
	
	public void startGame() {
		new Thread(new Runnable() { 
			public void run() {
				while (isRunning) {
					System.out.print("check");
					if ((System.currentTimeMillis() - startTime)/1000 > Protocol.General.TIMEOUTSECONDS) {
						sendEnd(Protocol.Server.TIMEOUT);
					}
				}
			} 
		});
	}
	
	public void shutDown() {
		isRunning = false;
	}
	
	public void setTime() {
		startTime = System.currentTimeMillis();
	}
	
	// GETTERS & SETTERS ==============================================================
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
	
	/**
	 * Set the boardsize after the first player has send their desires settings.
	 */
	public void setBoard(int DIM) {
		board = new Board(DIM);
		passes = 0;
		moves = DIM * DIM;
	}
	
	/**
	 * Get the board.
	 */
	public Board getBoard() {
		return this.board;
	}
	
	/**
	 * Set current player.
	 */
	public void setCurrentPlayer(int no) {
		this.currentPlayer = no;
	}
	
	/**
	 * To get the currentplayer number.
	 */
	public int getCurrentPlayer() {
		return this.currentPlayer;
	}
	
	public ClientHandler getPlayer(int playerNo) {
		return players[playerNo];
	}
	
	// GAME MECHANICS =================================================================
	/**
	 * If a player passes, this method is called and no changes are made to the board.
	 */
	public void makePass(int playerNo) {
		passes++;
		sendPass(playerNo);
	}
	
	/**
	 * If a player makes a move. This method checks if the move is valid.
	 * If not, the INVALIDMOVE command is send to the client,
	 * otherwise this method changes it in the board and sends a confirmation to the players.
	 */
	public void makeMove(int x, int y, int playerNo) {
		boolean validMove = false;
		try {
			validMove = board.checkMove(x, y, playerToken[playerNo]);
		} catch (InvalidCoordinateException e) {
			players[playerNo].sendError(Protocol.Server.INVALID, e.getMessage());
		}
	
		if (validMove) {
			moves--;
			board.setField(x, y, playerToken[playerNo]);
			sendMove(x, y, playerNo);
		}
	}
	
	/**
	 * After a player passes, this is called to check if the game has finished.
	 */
	public boolean gameOver() {
		return (passes > 1) || (moves <= 0);
	}
	
	/**
	 * When the game is over, for each player the score can be determined by calling this method.
	 */
	public int score(int playerNo) {
		return board.getScore(playerToken[playerNo]);
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
		players[playerNo].sendValidPass();
		players[Math.abs(playerNo-1)].sendPass();
	}
	
	/**
	 * Send to players when the game has ended.
	 */
	public void sendEnd(String reason) {
		players[0].sendEndGame(reason, -1);
		players[1].sendEndGame(reason, -1);
	}
}

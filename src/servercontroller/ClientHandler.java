package servercontroller;

import general.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.IOException;
import java.net.Socket;

/**
 * ClientHandler starts the threads for receiving and sending information from
 * the server to this specific client.
 * 
 * @author vera.prinsen
 */
public class ClientHandler {

	private Server server;
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;

	private ClientInputHandler clientInput;
	private Thread inputThread;
	private String clientName;
	private int clientVersionNo;
	private int[] clientExtensions;

	private GameController game = null;
	private ClientHandler opponent;
	private int playerNo = -1;
	
	private boolean inLobby = false;
	private boolean inGame = false;
	private boolean isBoard = false;

	public ClientHandler(Server server, Socket sock, BufferedReader in, BufferedWriter out) {
		this.server = server;
		this.sock = sock;
		this.in = in;
		this.out = out;

		clientName = null;
		clientExtensions = new int[7];

		run();
	}

	/**
	 * Starts the threads for: receiving information from the client:
	 * ClientInputHandler
	 * 
	 * And sends the information of this client: Client name, version, extension
	 * information
	 */
	public void run() {
		clientInput = new ClientInputHandler(this, in);

		inputThread = new Thread(clientInput, "ClientInput");
		inputThread.start();

		sendVersion();
	}

	public void shutDown() {		
		server.removeFromClients(this);
		try {
			sock.close();
			in.close();
			out.close();
		} catch (IOException e) {
			print("IOException occured.");
		}
	}

	// GETTERS & SETTERS ========================================================================
	public String getName() {
		return this.clientName;
	}
	
	public ClientHandler getOpponent() {
		return opponent;
	}

	public GameController getGame() {
		return this.game;
	}

	// METHODS FOR PLAYING THE GAME =================================================== $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
	/**
	 * Start a game for this particular client. game is the game object that is
	 * used. player says if the player was the first (0) or second (1) in the game.
	 */
	public void startGame(GameController game, int playerNo, ClientHandler opponent) {
		this.game = game;
		this.playerNo = playerNo;
		this.opponent = opponent;
	}

	/**
	 * When the game is finished, this method is called to remove the game instance
	 * of this client.
	 */
	public void endGame() {
		this.game = null;
	}

	// INPUT PROCESSORS ================================================================
	/**
	 * This is what is done with the input from this particular client.
	 */
	// TO DO: CHECK FOR VALID INPUT
	public void processClientInput(String msg) {
		String[] args = msg.split("\\" + Protocol.General.DELIMITER1);

		switch (args[0]) {
		case Protocol.Client.NAME:
			clientName = args[1];
			
			int equalNames = 0;
			for (ClientHandler c : server.getClients()) {
				if (clientName.equals(c.getName())) {
					equalNames++;
				}
				
				if (equalNames > 1) {
					sendError(Protocol.Server.NAMETAKEN, "This name is already used. Choose another one.");
					return;
				}
			}
			
			try {
				clientVersionNo = Integer.parseInt(args[3]);
				clientExtensions[0] = Integer.parseInt(args[5]);
				clientExtensions[1] = Integer.parseInt(args[6]);
				clientExtensions[2] = Integer.parseInt(args[7]);
				clientExtensions[3] = Integer.parseInt(args[8]);
				clientExtensions[4] = Integer.parseInt(args[9]);
				clientExtensions[5] = Integer.parseInt(args[10]);
				clientExtensions[6] = Integer.parseInt(args[11]);
			} catch (NumberFormatException e) {
				sendError(Protocol.Server.UNKNOWN, "In the NAME command, integers are expected for the versionnumber and all extensions.");
				shutDown();
				return;
			}
			
			if (clientVersionNo != Protocol.Server.VERSIONNO) {
				sendError(Protocol.Server.INCOMPATIBLEPROTOCOL, "The server runs protocol version " + Protocol.Server.VERSIONNO + ". This is not compatible with your version no. " + clientVersionNo + ".");
				shutDown();
				return;
			}
			
			print("[" + clientName + " has entered]");
			break;

		case Protocol.Client.REQUESTGAME:
			//if (!inGame) {
				server.getGameServer().addToLobby(this);
				inLobby = true;
			//} else {
				// IS ALREADY IN A GAME
			//}
			break;

		case Protocol.Client.SETTINGS:
			String colorString = args[1];
			int boardSize = Integer.parseInt(args[2]);

			game.setBoard(boardSize, false);
			game.setColors(this, colorString);
			game.sendStart();		
			game.startGame();
			
			inGame = true;
			isBoard = true;
			break;

		case Protocol.Client.MOVE:
			if (!((game == null) || (game.getCurrentPlayer() != playerNo))) {
				
				
				if (args[1].equals(Protocol.Client.PASS)) {
					game.makePass(playerNo);
					
					if (game.gameOver()) {
						// GAME OVER
						game.setCurrentPlayer(-1);
						game.sendEnd(Protocol.Server.FINISHED);
					} else {
						// expect a move from the other player
					}
				} else {
					String[] coordinates = args[1].split(Protocol.General.DELIMITER2);
					int x = 0;
					int y = 0;
					try {
						x = Integer.parseInt(coordinates[0]);
						y = Integer.parseInt(coordinates[1]);
						game.makeMove(x, y, playerNo);
					} catch (NumberFormatException e) {
						sendError(Protocol.Server.INVALID, "Invalid coordinates, they should be integers.");
					}
					
					
				}
			} else {
				if (game == null) {
					sendError(Protocol.Server.INVALID, "A game has not started yet.");
				} else if (game.getCurrentPlayer() != playerNo) {
					sendError(Protocol.Server.INVALID, "It is not your turn.");
				} else {
					sendError(Protocol.Server.INVALID, "Invalid move.");
				}	
			}

			break;

		case Protocol.Client.QUIT:
			if (game == null) {
				// Client is in lobby
				server.getGameServer().removeFromLobby(this);
			} else if (game.getBoard() == null) {
				// Client is in game, but it has not yet started
				// Opponent does not know this, so he doesn't need to be informed.
				// But he needs to be added to the lobby again.
				opponent.endGame();
				this.endGame();
				server.getGameServer().addToLobby(opponent);
			} else {
				// Client is in game, and it is started. Opponent needs to be informed.
				opponent.sendEndGame(Protocol.Server.ABORTED, playerNo);
				this.sendEndGame(Protocol.Server.ABORTED, playerNo);
				opponent.endGame();
				this.endGame();
			}
			break;

		default:
			print(msg);
			break;
		}

	}

	// PRINTERS & SENDERS ============================================================
	/**
	 * Method called to print information on the console of the server.
	 */
	public void print(String msg) {
		server.print(msg);
	}

	/**
	 * To send a message to this specific client.
	 */
	// TO DO: EXCEPTION HANDLING
	public void send(String msg) {
		try {
			out.write(msg + Protocol.General.COMMAND_END);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * method that constructs and sends the NAME command, format: NAME <String
	 * serverName> VERSION <int versionNo> EXTENSIONS 0 0 0 0 0 0 0
	 */
	public void sendVersion() {
		String message = Protocol.Server.NAME + Protocol.General.DELIMITER1 + server.getName()
				+ Protocol.General.DELIMITER1 + Protocol.Server.VERSION + Protocol.General.DELIMITER1
				+ Protocol.Server.VERSIONNO + Protocol.General.DELIMITER1 + Protocol.Server.EXTENSIONS
				+ Protocol.General.DELIMITER1 + Extensions.chat + Protocol.General.DELIMITER1 + Extensions.challenge
				+ Protocol.General.DELIMITER1 + Extensions.leaderboard + Protocol.General.DELIMITER1 + Extensions.security
				+ Protocol.General.DELIMITER1 + Extensions.multiplayer + Protocol.General.DELIMITER1 + Extensions.simultaneous
				+ Protocol.General.DELIMITER1 + Extensions.multimoves;
		send(message);
	}

	/**
	 * method that constructs and sends the ERROR command
	 * format: ERROR <String typeOfError> <String errorMessage>
	 * typeOfError can be:
	 * 		UNKNOWNCOMMAND
	 * 		INVALIDMOVE
	 * 		NAMETAKEN
	 * 		INCOMPATIBLEPROTOCOL
	 * 		OTHER
	 */
	public void sendError(String error, String errorMessage) {
		String message = Protocol.Server.ERROR + Protocol.General.DELIMITER1 + error + Protocol.General.DELIMITER1
				+ errorMessage;
		send(message);
	}

	/**
	 * Method that constructs and sends the START command for the first player.
	 * format: START <int numberPlayers>
	 */
	public void sendRequestSettings() {
		// FOR THE DEFAULT GAME OF 2 PLAYERS
		String message = Protocol.Server.START + Protocol.General.DELIMITER1 + 2;
		send(message);
	}

	/**
	 * Method that constructs and sends the START command to both players. 
	 * format: START <int numberPlayers> <String colorPlayer> <int boardSize> <String firstPlayer> <String secondPlayer>
	 */
	public void sendStart(int numberPlayers, String color, int DIM, String currentPlayer, String otherPlayer) {
		String message = Protocol.Server.START + Protocol.General.DELIMITER1 + numberPlayers
				+ Protocol.General.DELIMITER1 + color + Protocol.General.DELIMITER1 + DIM + Protocol.General.DELIMITER1
				+ currentPlayer + Protocol.General.DELIMITER1 + otherPlayer;
		send(message);
	}

	/**
	 * Method that constructs and sends the TURN FIRST command to the first player.
	 * format: TURN <String firstPlayer> FIRST <String firstPlayer>
	 */
	public void sendFirst() {
		
		String message = Protocol.Server.TURN + Protocol.General.DELIMITER1 + clientName + Protocol.General.DELIMITER1
				+ Protocol.Server.FIRST + Protocol.General.DELIMITER1 + clientName;
		send(message);
	}

	/**
	 * Method that constructs and sends the TURN command to the next player (and the move that is done by the current player).
	 * format: TURN <String opponent / previousPlayer> x_y <String clientName / nextPlayer>
	 */
	public void sendMove(int x, int y) {
		String message = Protocol.Server.TURN + Protocol.General.DELIMITER1 + opponent.getName() + Protocol.General.DELIMITER1 + x
				+ Protocol.General.DELIMITER2 + y + Protocol.General.DELIMITER1 + clientName;
		send(message);
	}
	
	/**
	 * Method that constructs and sends the TURN command to the previous player (and the VALID move that is done by this player).
	 * format: TURN <String clientName / previousPlayer> x_y <String opponent / nextPlayer>
	 */
	public void sendValidMove(int x, int y) {
		String message = Protocol.Server.TURN + Protocol.General.DELIMITER1 + clientName + Protocol.General.DELIMITER1 + x
				+ Protocol.General.DELIMITER2 + y + Protocol.General.DELIMITER1 + opponent.getName();
		send(message);
	}

	/**
	 * Method that constructs and sends to the next player that the previous player passed.
	 * format: TURN <String opponent / previousPlayer> PASS <String clientName / nextPlayer>
	 */
	public void sendPass() {
		String message = Protocol.Server.TURN + Protocol.General.DELIMITER1 + opponent.getName() + Protocol.General.DELIMITER1
				+ Protocol.Server.PASS + Protocol.General.DELIMITER1 + clientName;
		send(message);
	}
	
	/**
	 * Method that constructs and sends to the previous player that his pass is accepted.
	 * format: TURN <String clientName / previousPlayer> PASS <String opponent / nextPlayer>
	 */
	public void sendValidPass() {
		String message = Protocol.Server.TURN + Protocol.General.DELIMITER1 + clientName + Protocol.General.DELIMITER1
				+ Protocol.Server.PASS + Protocol.General.DELIMITER1 + opponent.getName();
		send(message);
	}

	/**
	 * Method that constructs and sends the command that the game has ended, why it has ended and the end scores.
	 * format: ENDGAME <String reason> <String winningPlayer> <int score> <String losingPlayer> <int score>
	 * reason can be:
	 * 		- FINISHED (game has finished because both players passed after each other).
	 * 		- ABORTED (someone (server or client) has left the game and therefore the game stopped).
	 * 		- TIMEOUT (the currentPlayer did not make a move within the TIMEOUT time).
	 */
	public void sendEndGame(String reason, int responsiblePlayer) {
		int scorePlayer = 0;
		int scoreOpponent = 0;
		
		if (reason.equals(Protocol.Server.FINISHED)) {
			scorePlayer = game.score(playerNo);
			scoreOpponent = game.score(Math.abs(playerNo - 1));
		} else if (reason.equals(Protocol.Server.ABORTED)) {
			if (responsiblePlayer == playerNo) {
				// this player has aborted
				scorePlayer = 0;
				scoreOpponent = game.score(Math.abs(playerNo - 1));
			} else if (responsiblePlayer == Math.abs(playerNo - 1)) {
				// opponent aborted
				scorePlayer = game.score(playerNo);
				scoreOpponent = 0;
			} else if (responsiblePlayer == 2) {
				// Server aborted
				scorePlayer = game.score(playerNo);
				scoreOpponent = game.score(Math.abs(playerNo - 1));
			} else {
				
			}
		} else if (reason.equals(Protocol.Server.TIMEOUT)) {
			if (responsiblePlayer == playerNo) {
				// this player has timed out
				scorePlayer = 0;
				scoreOpponent = game.score(Math.abs(playerNo - 1));
			} else if (responsiblePlayer == Math.abs(playerNo - 1)) {
				// opponent timed out
				scorePlayer = game.score(playerNo);
				scoreOpponent = 0;
			} else {
				
			}
		} else {
			
		}
				
		// ENDGAME reden WINSPELER score VERLIESSPELER score
		String message;
		if (scorePlayer > scoreOpponent) {
			message = Protocol.Server.ENDGAME + Protocol.General.DELIMITER1 + reason + Protocol.General.DELIMITER1 + clientName + Protocol.General.DELIMITER1 + scorePlayer + Protocol.General.DELIMITER1 + opponent.getName() + Protocol.General.DELIMITER1 + scoreOpponent;
		} else  {
			message = Protocol.Server.ENDGAME + Protocol.General.DELIMITER1 + reason + Protocol.General.DELIMITER1 + opponent.getName() + Protocol.General.DELIMITER1 + scoreOpponent + Protocol.General.DELIMITER1 + clientName + Protocol.General.DELIMITER1 + scorePlayer;
		}
		
		send(message);
	}
}

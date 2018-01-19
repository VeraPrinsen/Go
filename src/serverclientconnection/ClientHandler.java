package serverclientconnection;

import general.*;
import model.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * ClientHandler starts the threads for receiving and sending information from
 * the server to this specific client.
 * 
 * @author vera.prinsen
 */
public class ClientHandler {

	private Server server;
	private BufferedReader in;
	private BufferedWriter out;
	
	private ClientInputHandler clientInput;

	private String clientName;
	private int clientVersionNo;
	private int[] clientExtensions;

	private Game game = null;
	private String opponent;
	private int playerNo = -1;

	public ClientHandler(Server server, BufferedReader in, BufferedWriter out) {
		this.server = server;
		this.in = in;
		this.out = out;

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

		Thread inputThread = new Thread(clientInput, "ClientInput");
		inputThread.start();

		sendVersion();
	}

	// GETTERS ========================================================================
	public String getName() {
		return this.clientName;
	}

	// METHODS FOR PLAYING THE GAME =================================================== $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
	/**
	 * Start a game for this particular client.
	 * 		game is the game object that is used.
	 * 		player says if the player was the first (0) or second (1) in the game.
	 */
	public void startGame(Game game, int playerNo) {
		this.game = game;
		this.playerNo = playerNo;
	}

	/**
	 * When the game is finished, this method is called to remove the game instance of this client.
	 */
	public void endGame() {
		this.game = null;
	}

	// INPUT PROCESSORS ================================================================
	/**
	 * This is what is done with the input from this particular client.
	 */
	public void processClientInput(String msg) {
		String[] args = msg.split("\\" + Character.toString(Protocol.General.DELIMITER1));
		
		switch (args[0]) {
		case Protocol.Client.NAME:
			print("NAME command ontvangen");
			
			clientName = args[1];
			clientVersionNo = Integer.parseInt(args[3]);
			clientExtensions[0] = Integer.parseInt(args[5]);
			clientExtensions[1] = Integer.parseInt(args[6]);
			clientExtensions[2] = Integer.parseInt(args[7]);
			clientExtensions[3] = Integer.parseInt(args[8]);
			clientExtensions[4] = Integer.parseInt(args[9]);
			clientExtensions[5] = Integer.parseInt(args[10]);
			clientExtensions[6] = Integer.parseInt(args[11]);

			print("[" + clientName + " has entered]");
			break;

		case Protocol.Client.REQUESTGAME:
			print("REQUESTGAME command ontvangen van " + clientName);
			server.getGameServer().addToLobby(this);
			break;

		case Protocol.Client.SETTINGS:
			print("SETTINGS command ontvangen van " + clientName);
			
			String colorString = args[1];
			int boardSize = Integer.parseInt(args[2]);

			game.setBoard(boardSize, false);
			game.setColors(this, colorString);
			game.sendStart();
			break;

		case Protocol.Client.MOVE:
			print("MOVE command ontvangen van " + clientName);
			
			if (game != null) {
				if (args[1].equals(Protocol.Client.PASS)) {
					// pass
				} else {
					String[] coordinates = args[1].split(Character.toString(Protocol.General.DELIMITER2));
					int x = Integer.parseInt(coordinates[0]);
					int y = Integer.parseInt(coordinates[1]);
					game.makeMove(x, y, playerNo);
					game.sendMove(x, y, playerNo);
				}	
			} else {
				sendError(Protocol.Server.INVALID, "A game has not yet started.");
			}
			
			break;

		case Protocol.Client.QUIT:
			print("QUIT command ontvangen van " + clientName);
			
			break;

		default:
			print("other command ontvangen van " + clientName);
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
	 * method that constructs and sends the NAME command, format:
	 * NAME <String serverName> VERSION <int versionNo> EXTENSIONS 0 0 0 0 0 0 0
	 */
	public void sendVersion() {
		String message = Protocol.Server.NAME + Protocol.General.DELIMITER1 + server.getName()
				+ Protocol.General.DELIMITER1 + Protocol.Server.VERSION + Protocol.General.DELIMITER1 + Protocol.Server.VERSIONNO
				+ Protocol.General.DELIMITER1 + Protocol.Server.EXTENSIONS + Protocol.General.DELIMITER1 + Version.chat
				+ Protocol.General.DELIMITER1 + Version.challenge + Protocol.General.DELIMITER1 + Version.leaderboard
				+ Protocol.General.DELIMITER1 + Version.security + Protocol.General.DELIMITER1 + Version.multiplayer
				+ Protocol.General.DELIMITER1 + Version.simultaneous + Protocol.General.DELIMITER1 + Version.multimoves;
		send(message);
	}

	/**
	 * method that constructs and sends the ERROR command, format:
	 * ERROR <String typeOfError> <String errorMessage>
	 */
	public void sendError(String error, String errorMessage) {
		String message = Protocol.Server.ERROR + Protocol.General.DELIMITER1 + error + Protocol.General.DELIMITER1
				+ errorMessage;
		send(message);
	}

	/**
	 * method that constructs and sends the START command for the first player, format:
	 * START <int numberPlayers>
	 */
	public void sendRequestSettings() {
		String message = Protocol.Server.START + Protocol.General.DELIMITER1 + 2; // Wat als extensie is bijgevoegd dat het met meer spelers kan?																	
		send(message);
	}

	/**
	 * method that constructs and sends the START command to both players, format:
	 * START <int numberPlayers> <String colorPlayer> <int boardSize> <String firstPlayer> <String secondPlayer>
	 */
	public void sendStart(int numberPlayers, String color, int DIM, String currentPlayer, String otherPlayer) {
		if (clientName.equals(currentPlayer)) {
			opponent = otherPlayer;
		} else {
			opponent = currentPlayer;
		}
		
		String message = Protocol.Server.START + Protocol.General.DELIMITER1 + numberPlayers
				+ Protocol.General.DELIMITER1 + color + Protocol.General.DELIMITER1 + DIM + Protocol.General.DELIMITER1
				+ currentPlayer + Protocol.General.DELIMITER1 + otherPlayer;
		send(message);
	}
	
	public void sendFirst() {
		String message = Protocol.Server.TURN + Protocol.General.DELIMITER1 + clientName + Protocol.General.DELIMITER1 + "FIRST" + Protocol.General.DELIMITER1 + clientName;
		send(message);
	}
	
	public void sendMove(int x, int y) {
		String message = Protocol.Server.TURN + Protocol.General.DELIMITER1 + opponent + Protocol.General.DELIMITER1 + x + Protocol.General.DELIMITER2 + y + Protocol.General.DELIMITER1 + clientName;
		send(message);
	}
}

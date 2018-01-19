package serverclientconnection;

import general.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 * ServerHandler starts the threads for receiving and sending information from this specific client to the server.
 * @author vera.prinsen
 */
public class ServerHandler {

	private Client client;
	private BufferedReader in;
	private BufferedWriter out;
	
	private ServerInputHandler serverInput;
	
	private String serverName;
	private int serverVersionNo;
	private int[] serverExtensions;

	public ServerHandler(Client client, BufferedReader in, BufferedWriter out) {
		this.client = client;
		this.in = in;
		this.out = out;

		serverExtensions = new int[7];
		run();
	}
	
	/**
	 * Starts the threads for:
	 * receiving information from the server: ServerInputHandler
	 * receiving information from the client TUI: ClientInput
	 * 
	 * And sends the information of the server:
	 * Client name, version, extension information
	 */
	public void run() {
		serverInput = new ServerInputHandler(this, in);

		Thread inputThread = new Thread(serverInput);
		inputThread.start();

		sendVersion();
	}

	// GETTERS ========================================================================
	public String getName() {
		return this.serverName;
	}
	
	// METHODS FOR PLAYING THE GAME ===================================================
	
	
	// INPUT PROCESSORS ===============================================================
//	/**
//	 * When the ClientInput receives information from the client TUI, it is processed here.
//	 */
//	// TO DO: EXCEPTION HANDLING 
//	// THIS CAN ALSO BE NUMBER BASED..
//	public void processClientInput(String msg) throws Exception {
//		switch (msg) {
//			case Protocol.Client.REQUESTGAME: 
//				sendRequest();
//				break;
//				
//				
//	
//			case "EXIT":
//				
//				break;
//		}
//	}
	/**
	 * When the ServerInputHandler receives information from the server, it is processed here.
	 */
	public void processServerInput(String msg) {
		String[] args = msg.split("\\" + Character.toString(Protocol.General.DELIMITER1));

		switch (args[0]) {
			case Protocol.Server.NAME:
				print("NAME command ontvangen");
		
				serverName = args[1];
				// doe iets met versienummer & extensions $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
				
				break;
	
			case Protocol.Server.START:
				print("START command ontvangen van " + serverName);
		
//				if (args.length == 2) {
//					sendSettings();
//				} else if (args.length == 6) {
//					// Game has begun
//				}
				break;
	
			case Protocol.Server.TURN:
				print("TURN command ontvangen van " + serverName);
				
				break;
				
			case Protocol.Server.ENDGAME:
				print("ENDGAME command ontvangen van " + serverName);
				
				break;
				
			case Protocol.Server.ERROR:
				print("ERROR command ontvangen van " + serverName);
				
				break;
				
			default:
				print("other command ontvangen van " + serverName);
				print(msg);
				break;
		}
	}

	// PRINTERS & SENDERS ===============================================================
	/**
	 * Method called to print information on the console of the client.
	 */
	public void print(String msg) {
		client.print(msg);
	}
	
	/**
	 * The method called when information must be send to the server.
	 */
	// TO DO: EXCEPTION HANDLING
	public void send(String msg) {
		try {
			out.write(msg + Protocol.General.COMMAND_END);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method creates the versionString which is the first thing that is send to the server after connecting to it.
	 */
	public void sendVersion() {
		String message = Protocol.Client.NAME + Protocol.General.DELIMITER1 + client.getName() + Protocol.General.DELIMITER1
				+ "VERSION" + Protocol.General.DELIMITER1 + Protocol.Client.VERSIONNO + Protocol.General.DELIMITER1
				+ Protocol.Client.EXTENSIONS + Protocol.General.DELIMITER1 + Version.chat + Protocol.General.DELIMITER1
				+ Version.challenge + Protocol.General.DELIMITER1 + Version.leaderboard + Protocol.General.DELIMITER1
				+ Version.security + Protocol.General.DELIMITER1 + Version.multiplayer + Protocol.General.DELIMITER1
				+ Version.simultaneous + Protocol.General.DELIMITER1 + Version.multimoves;
		send(message);
	}
	
	public void sendSettings() {
		boolean colorOK = false;
		String colorString = "";
		while (!colorOK) {
			int colorInt = client.readInt("What color do you want to play with? (1 - BLACK, 2 - WHITE)");
			colorString = "";
			if (colorInt == 1) {
				colorString = Protocol.General.BLACK;
				colorOK = true;
			} else if (colorInt == 2) {
				colorString = Protocol.General.WHITE;
				colorOK = true;
			} else {
				print("This is not a valid input. Enter 1 or 2 to choose your color.");
			}
		}
		
		boolean boardsizeOK = false;
		int boardSize = 0;
		while (!boardsizeOK) {
			int minBoardsize = 5;
			int maxBoardsize = 21;
			boardSize = client.readInt("What do you want to be the size of the board? (Integer between " + minBoardsize + " - " + maxBoardsize + ")");
			if ((boardSize < minBoardsize) || (boardSize > maxBoardsize)) {
				print("This is not a valid input. Enter an integer between " + minBoardsize + " and " + maxBoardsize + ".");
			} else {
				boardsizeOK = true;
			}
		}
		
		String message = Protocol.Client.SETTINGS + Protocol.General.DELIMITER1 + colorString + Protocol.General.DELIMITER1 + boardSize;
		send(message);
	}
	
	public void sendRequest() {
		String message = Protocol.Client.REQUESTGAME + Protocol.General.DELIMITER1 + 2 + Protocol.General.DELIMITER1
				+ Protocol.Client.RANDOM; // default case, no extensions
		send(message);
		client.print("Game requested. Wait for other players to connect...");
	}
}

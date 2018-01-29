package clientcontroller;

import general.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import boardview.GOGUI;
import boardview.GoGUIIntegrator;

/**
 * ServerHandler starts the threads for receiving and sending information from
 * this specific client to the server.
 * 
 * @author vera.prinsen
 */
public class ServerHandler {

	public Client client;
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;

	private ServerInputHandler serverInput;
	private String serverName;
	private int[] serverExtensions;

	private GOGUI gui;
	private Game game;
	private Player player;

	Lock lock = new ReentrantLock();
	Condition condition = lock.newCondition();

	public ServerHandler(Client client, Socket sock, BufferedReader in, BufferedWriter out) {
		this.client = client;
		this.sock = sock;
		this.in = in;
		this.out = out;

		serverExtensions = new int[7];

		this.gui = new GoGUIIntegrator(false, false, 9);
		this.game = null;

		run();
	}

	/**
	 * Starts the thread for receiving information from the server:
	 * ServerInputHandler
	 * 
	 * And sends the information of the server: Client name, version, extension
	 * information.
	 */
	public void run() {
		serverInput = new ServerInputHandler(this, in);

		Thread inputThread = new Thread(serverInput, "ServerInput");
		inputThread.start();

		sendVersion();
	}

	public void shutDown() {
		serverInput.shutDown();

		try {
			sock.close();
			in.close();
			out.close();
		} catch (IOException e) {
			print("IOException occured.");
		}

		client.shutDown();
		System.exit(0);
		
	}

	// GETTERS & SETTERS
	// ==============================================================
	public String getName() {
		return this.serverName;
	}

	public Game getGame() {
		return this.game;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	// INPUT PROCESSORS
	// ===============================================================
	/**
	 * When the ServerInputHandler receives information from the server, it is
	 * processed here.
	 */
	public void processServerInput(String msg) {
		String[] args = msg.split("\\" + Protocol.General.DELIMITER1);

		switch (args[0]) {
			case Protocol.Server.NAME:
				serverName = args[1];
				serverExtensions[0] = Integer.parseInt(args[5]);
				serverExtensions[1] = Integer.parseInt(args[6]);
				serverExtensions[2] = Integer.parseInt(args[7]);
				serverExtensions[3] = Integer.parseInt(args[8]);
				serverExtensions[4] = Integer.parseInt(args[9]);
				serverExtensions[5] = Integer.parseInt(args[10]);
				serverExtensions[6] = Integer.parseInt(args[11]);
	
				showMainMenu();
				break;
	
			case Protocol.Server.START:
				lock.lock();
	
				if (args.length == 2) {
					sendSettings();
				} else if (args.length == 6) {
					int numberPlayers = Integer.parseInt(args[1]);
					String color = args[2];
					int boardSize = Integer.parseInt(args[3]);
					String opponent;
					if (color.equals(Protocol.General.BLACK)) {
						opponent = args[5];
					} else {
						opponent = args[4];
					}
					game = new Game(this, numberPlayers, boardSize, player, opponent, color, gui);
					player.setGame(game);
					condition.signal();
				}
	
				lock.unlock();
				break;
	
			case Protocol.Server.TURN:
				lock.lock();
				if (game == null || game.getBoard() == null) {
					// wait till board is made
					try {
						condition.await();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				lock.unlock();
	
				if (args[2].equals(Protocol.Server.FIRST)) {
					player.sendMove();
				} else if (args[2].equals(Protocol.Server.PASS)) {
					if (args[1].equals(client.getName())) {
						// Server has informed us our pass was valid.
						game.setPass();
						print("You have passed.");
					} else {
						// Server informes us that our opponent has passed.
						game.setPass();
						print("Opponent has passed.");
	
						if (game.gameOver()) {
							// als 2x gepasst, gameOver() en server should send the score.
							print("GAME OVER!");
						} else {
							// Clients turn
							player.sendMove();
						}
					}
				} else {
					String[] coordinates = args[2].split(Protocol.General.DELIMITER2);
					int x = Integer.parseInt(coordinates[0]);
					int y = Integer.parseInt(coordinates[1]);
	
					if (args[1].equals(client.getName())) {
						// Server has informed us our move was valid.
						game.setMovePlayer(x, y, true);
					} else {
						// Server informes us that our opponent has played the following valid move:
						game.setMovePlayer(x, y, false);
	
						// And that it is the clients turn
						player.sendMove();
					}
				}
	
				break;
	
			case Protocol.Server.ENDGAME:
				String player1 = args[2];
				int score1 = Integer.parseInt(args[3]);
				String player2 = args[4];
				int score2 = Integer.parseInt(args[5]);
	
				String playerName;
				String opponentName;
				int playerScore;
				int opponentScore;
				if (player1.equals(client.getName())) {
					playerName = player1;
					playerScore = score1;
					opponentName = player2;
					opponentScore = score2;
				} else {
					playerName = player2;
					playerScore = score2;
					opponentName = player1;
					opponentScore = score1;
				}
	
				if (args[1].equals(Protocol.Server.FINISHED)) {
					// Game was finished
					if (playerScore > opponentScore) {
						print("");
						print("Congratulations! You won from " + opponentName + " with " 
								+ playerScore + " against " + opponentScore + ".");
					} else if (opponentScore > playerScore) {
						print("");
						print("You lost from " + opponentName + " with " + playerScore 
								+ " against " + opponentScore + ".");
					} else {
						print("");
						print("It was a draw. You and " + opponentName + " both scored " 
								+ playerScore + " points.");
					}
				} else if (args[1].equals(Protocol.Server.ABORTED)) {
					print("");
					print("The game was aborted because the server or your opponent "
							+ "has disconnected.");
					print("You scored " + playerScore + " points this game.");
				} else if (args[1].equals(Protocol.Server.TIMEOUT)) {
					print("");
					print("Someone did not respond in time.");
					print("You scored " + playerScore + " points this game.");
				} else {
					// Should not happen
				}
	
				game = null;
	
				print("");
				showMainMenu();
				break;
	
			case Protocol.Server.ERROR:
				// * format: ERROR <String typeOfError> <String errorMessage>
				// * typeOfError can be:
				// * UNKNOWNCOMMAND
				// * INVALIDMOVE
				// * NAMETAKEN
				// * INCOMPATIBLEPROTOCOL
				// * OTHER
				switch (args[1]) {
					case Protocol.Server.UNKNOWN:
						print("");
						print("Message from server: " + args[2]);
						break;
		
					case Protocol.Server.INVALID:
						print("");
						print("Message from server: " + args[2]);
						break;
		
					case Protocol.Server.NAMETAKEN:
						print("");
						print("Message from server: " + args[2]);
						shutDown();
						break;
		
					case Protocol.Server.INCOMPATIBLEPROTOCOL:
						print("");
						print("Message from server: " + args[2]);
						shutDown();
						break;
		
					case Protocol.Server.OTHER:
						print("");
						print("Message from server: " + args[2]);
						break;
		
					default:
		
						break;
				}
				break;
	
			default:
				print(msg);
				break;
		}
	}

	// PRINTERS & SENDERS
	// ===============================================================
	/**
	 * Method called to print information on the console of the client.
	 */
	public void print(String msg) {
		client.print(msg);
	}

	public String readString(String prompt) {
		return client.readString(prompt);
	}

	public int readInt(String prompt) {
		return client.readInt(prompt);
	}

	/**
	 * The method called when information must be send to the server.
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
	 * This method creates the versionString which is the first thing that is send
	 * to the server after connecting to it.
	 */
	public void sendVersion() {
		String message = Protocol.Client.NAME + Protocol.General.DELIMITER1 + client.getName()
				+ Protocol.General.DELIMITER1 + "VERSION" + Protocol.General.DELIMITER1 
				+ Protocol.Client.VERSIONNO + Protocol.General.DELIMITER1 
				+ Protocol.Client.EXTENSIONS + Protocol.General.DELIMITER1 + Extensions.chat 
				+ Protocol.General.DELIMITER1 + Extensions.challenge + Protocol.General.DELIMITER1
				+ Extensions.leaderboard + Protocol.General.DELIMITER1 + Extensions.security
				+ Protocol.General.DELIMITER1 + Extensions.multiplayer + Protocol.General.DELIMITER1
				+ Extensions.simultaneous + Protocol.General.DELIMITER1 + Extensions.multimoves;
		send(message);
	}

	/**
	 * Is shown just after the server and client have made a connection.
	 */
	public void showMainMenu() {
		print("");
		print("What do you want to do?");
		print("Request a game .............. 1");
		print("Exit ........................ 2");

		boolean optionOK = false;
		while (!optionOK) {
			int option = client.readInt("Choose an option");
			if (option == 1) {
				sendRequest();
				optionOK = true;
			} else if (option == 2 || option == -1) {
				send(Protocol.Client.EXIT);
				shutDown();
				optionOK = true;
			} else {
				print("This is an invalid option. Try again.");
			}
		}
	}

	public void sendRequest() {
		print("");
		print("Do you want to play yourself or do you want the computer play for you?");
		print("Play myself ................. 1");
		print("Let computer play ........... 2");

		boolean optionOK = false;
		while (!optionOK) {
			int option = client.readInt("Choose an option");
			if (option == -1) {
				// EXIT: Request has not yet been send, so only quiting the program is enough.
				showMainMenu();
				return;
			} else if (option == 1) {
				player = new HumanPlayer(this);
				optionOK = true;
			} else if (option == 2) {
				boolean inputOK = false;
				int reactionTimeAI = askReactionTime();
				player = new ComputerPlayer(this, reactionTimeAI);
				optionOK = true;
			} else {
				print("This is an invalid option. Try again.");
			}
		}
		setPlayer(player);

		String message = Protocol.Client.REQUESTGAME + Protocol.General.DELIMITER1 + 2 
				+ Protocol.General.DELIMITER1 + Protocol.Client.RANDOM;
		send(message);
		print("Game requested. Wait for other players to connect...");
	}

	public int askReactionTime() {
		print("");
		int reactionTime = 0;

		boolean optionOK = false;
		while (!optionOK) {
			int seconds = readInt("Within how many seconds should the computer "
					+ "have determined a move?");
			if (seconds == -1) {
				// EXIT: Request has not yet been send, so only quiting the program is enough.
				reactionTime = -1;
				optionOK = true;
			} else if (seconds > 0) {
				reactionTime = seconds;
				optionOK = true;
			} else {
				print("This is an invalid input. The time must be greater than 0 seconds.");
			}
		}

		return reactionTime;
	}

	public void sendSettings() {
		boolean colorOK = false;
		String colorString = "";
		while (!colorOK) {
			int colorInt = client.readInt("What color do you want to play with? "
					+ "(1 - BLACK, 2 - WHITE)");
			colorString = "";
			if (colorInt == -1) {
				sendQuit();
				showMainMenu();
				return;
			} else if (colorInt == 1) {
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
			int maxBoardsize = 19;
			boardSize = client.readInt("What do you want to be the size of the board? "
					+ "(Integer between " + minBoardsize	+ " - " + maxBoardsize + ")");
			if (boardSize == -1) {
				sendQuit();
				showMainMenu();
				return;
			} else if ((boardSize < minBoardsize) || (boardSize > maxBoardsize)) {
				print("This is not a valid input. Enter an integer between " + minBoardsize 
						+ " and " + maxBoardsize
						+ ".");
			} else {
				boardsizeOK = true;
			}
		}

		String message = Protocol.Client.SETTINGS + Protocol.General.DELIMITER1 + colorString
				+ Protocol.General.DELIMITER1 + boardSize;
		send(message);
		client.print("Settings send.");
	}

	public void sendQuit() {
		String message = Protocol.Client.QUIT;
		send(message);
	}
}
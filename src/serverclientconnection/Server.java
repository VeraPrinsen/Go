package serverclientconnection;

import model.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import general.Protocol;

/**
 * The file that is executed to start a server.
 * 
 * @author vera.prinsen
 */
public class Server {

	private ServerSocket ssock;
	private String serverName;
	private ServerTUI tui;
	private GameServer gameServer;

	private List<ClientHandler> clients;

	public Server() {
		serverName = "ServerVera";
		tui = new ServerTUI(this);
		gameServer = new GameServer(this);

		clients = new ArrayList<>();
	}

	// GETTERS & SETTERS
	// ================================================================
	public String getName() {
		return this.serverName;
	}

	public GameServer getGameServer() {
		return this.gameServer;
	}

	// INPUT PROCESSORS
	// =============================================================
	/**
	 * This is what is done to the input that has come from the ServerTUI.
	 */
	public void processServerInput(String msg) {
		if (msg.equalsIgnoreCase("exit")) {
			shutDown();
		} else {
			print(msg);
		}
	}

	// PRINTERS & SENDERS
	// ============================================================
	/**
	 * This method is used to print some text on the output of the server.
	 */
	public void print(String msg) {
		tui.print(msg);
	}

	/**
	 * If a message should be send to all the clients, this method is used.
	 */
	public void broadcast(String msg) {
		for (ClientHandler ch : clients) {
			ch.send(msg + "\n");
		}
	}

	// START UP AND SHUTDOWN OF THE SERVER
	// =========================================================
	/**
	 * This is what is done when the server is started: A valid portnumber is gotten
	 * from the Server TUI TO DO: FOR NOW IT IS DEFAULT 4567: see tui.getPort() The
	 * TUI and GameServer are started in new threads Server will wait here for
	 * clients to connect
	 */
	// TO DO: EXCEPTION HANDLING
	public void start() {
		boolean portOK = false;
		int port;

		while (!portOK) {
			try {
				port = tui.getPort();

				System.out.println("Trying to connect a server.");
				ssock = new ServerSocket(port);
				System.out.println("Server connected. Waiting for clients to connect...");
				portOK = true;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		Thread tuiThread = new Thread(tui, "ServerTUI");
		Thread gameThread = new Thread(gameServer, "GameServer");
		tuiThread.start();
		gameThread.start();

		try {
			while (true) {
				Socket sock = ssock.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
				ClientHandler ch = new ClientHandler(this, in, out);
				clients.add(ch);
			}
		} catch (SocketException e) {
			print("Server is closed.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shuts the server down.
	 */
	public void shutDown() {
		
		for (ClientHandler ch : clients) {
			if (ch.getGame() != null) {
				// Check if the game has started (can you calculate an endscore)
				ch.sendEndGame(Protocol.Server.ABORTED);
			}
			ch.shutDown();
		}
		
		tui.shutDown();
		gameServer.shutDown();
		
		print("GoodBye!");
		try {
			ssock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts a new server.
	 */
	// TO DO: EXCEPTION HANDLING
	public static void main(String[] args) throws Exception {
		(new Server()).start();
	}
}

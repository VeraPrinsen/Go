package serverController;

import general.*;
import netView.ServerTUI;

import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.net.SocketException;

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

	// START UP AND SHUTDOWN OF THE SERVER
	// ==============================================================================
	/**
	 * This is what is done when the server is started: A valid portnumber is asked
	 * from the Server TUI The TUI and GameServer are started in new threads Server
	 * will wait here for clients to connect.
	 */
	// TO DO: EXCEPTION HANDLING
	public void start() {
		boolean portOK = false;
		int port = 0;
		
		while (!portOK) {
			try {
				port = tui.getPort();
				print("Trying to connect the server.");
				ssock = new ServerSocket(port);
				print("Server connected. Waiting for clients to connect...");
				print("");
				portOK = true;
			} catch (BindException e) {
				print("Port " + port + " is already in use. Try another port.");
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

		print("check4");
		for (ClientHandler ch : clients) {
			if (ch.getGame() != null && ch.getGame().getBoard() != null) {
				ch.sendEndGame(Protocol.Server.ABORTED);
			}
			print("check5");
			ch.shutDown();
			print("check6");
		}

		print("GoodBye!");
		tui.shutDown();
		gameServer.shutDown();

		try {
			ssock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts a new server.
	 */
	public static void main(String[] args) {
		(new Server()).start();
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
	// =============================================================================
	/**
	 * This is what is done to the input that has come from the ServerTUI.
	 */
	public void processServerInput(String msg) {
		if (msg.equalsIgnoreCase("exit")) {
			print("check3");
			shutDown();
		} else {
			print(msg);
		}
	}

	// PRINTERS & SENDERS
	// =============================================================================
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

}
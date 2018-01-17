package server;

import model.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server extends Thread {
	
	private ServerTUI tui;
	
	private Set<Player> chSet; 
	private Set<Player> lobby;
	
	private int port;
	private ServerSocket ssock;
	private Socket sock;
	
	public Server(ServerTUI tui) {
		this.tui = tui;
		chSet = new HashSet<>();
		lobby = new HashSet<>();
		port = 0;
		ssock = null;
		sock = null;
	}
	
	public void run() {
		// Start up the server =============================================
		boolean portOK = false;
		while (!portOK) {
			String tryport = tui.readString("Give the port number you want to use");
			try {
				port = Integer.parseInt(tryport);
				ssock = new ServerSocket(port);
				tui.print("Server is now listening on port: " + port);
				portOK = true;
			} catch (NumberFormatException e) {
				tui.print("ERROR: port " + tryport + " is not an integer.");
			} catch (IOException e) {
				if (e instanceof BindException) {
					tui.print("ERROR: port " + port + " is already in use.");
				} else {
					e.printStackTrace();
				}
			} catch (IllegalArgumentException e) {
				tui.print("ERROR: port " + port + " cannot be used.");
			}
		}
		
		// Wait for clients to connect ========================================
		boolean running = true;
		while (running) {
			try {
				sock = ssock.accept();
				Player ch = new Player(this, sock);
				chSet.add(ch);
				lobby.add(ch);
				ch.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			ssock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		tui.print("Server is closed.");
		
	}
	
	public void startGame(int numberPlayers, Player player1) {
		Game newgame = new Game(numberPlayers);
		newgame.addPlayer(player1);
		lobby.remove(player1);
		
		// Ask for board size and which color he wants to play with.
		player1.send();
		int boardsize = 9;
		boolean color = true;
		
		Player player2 = null;		
		boolean player2Found = false;
		
		while (!player2Found) {
			if (!lobby.isEmpty()) {
				for (Player p : lobby) {
					p.send(); // Send request an wait for an acceptance
					
					player2 = p;
					player2Found = true;
				}
			}
		}
		
		newgame.addPlayer(player2);
		lobby.remove(player2);
		newgame.setBoard(boardsize);
		
		newgame.start();	
	}

}

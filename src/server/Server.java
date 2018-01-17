package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server extends Thread {
	
	ServerTUI tui;
	
	Set<ClientHandler> chSet; 
	Set<ClientHandler> lobby;
	
	int port;
	ServerSocket ssock;
	Socket sock;
	
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
				ClientHandler ch = new ClientHandler(ssock, sock);
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

}

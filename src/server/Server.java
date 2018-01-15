package server;

import java.io.*;
import java.net.*;

public class Server extends Thread {
	
	private int port;
	private TUI msgUI;
	
	public Server(int port, TUI msgUI) {
		this.msgUI = msgUI;
		this.port = port;
	}
	
	public void run() {
		ServerSocket ssock = null;
		Socket sock = null;

		try {
			ssock = new ServerSocket(port);

			while (true) {
				sock = ssock.accept();
				// ClientHandler ch = new ClientHandler(ssock, sock);
			}
		} catch (IOException e) {
			// server or client could not be created.
		} finally {
			try {
				ssock.close();
				sock.close();
			} catch (IOException e) {
				// server or client could not be closed.
			}
		}
	}

	public void print(String message) {
		msgUI.print(message);
	}
	
	public void broadcast(String message) {
		// tell all clients a message
	}
	

}

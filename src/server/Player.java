package server;

import java.io.*;
import java.net.*;

public class Player extends Thread {

	private Socket clientSock;
	private Server server;
	private BufferedReader in;
	private BufferedWriter out;
	
	public Player(Server server, Socket sock) throws IOException {
		this.clientSock = sock;
		this.server = server;
		
		in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(clientSock.getOutputStream()));
	}
	
	public void run() {
		readInput();
	}
	
	public void readInput() {
		try {
			String input;
			while ((input = in.readLine()) != null) {
				processInput(input);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send() {
		
	}
	
	public void processInput(String input) {
		String[] args = input.split("$");
		// Even wachten op documentje van Rosalyn
		switch (args[0]) {
			case "NAME":
				break;
			case "MOVE":
				break;
			case "REQUESTGAME":
				int nPlayers = Integer.parseInt(args[1]);
				if (args[2].equalsIgnoreCase("random")) {
					
				}
				
				break;
			default:
				break;	
		}
	}
}

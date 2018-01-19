package model;

import java.util.ArrayList;
import java.util.List;

import serverclientconnection.*;

public class GameServer implements Runnable {
	
	private Server server;
	private List<ClientHandler> lobby;
	
	public GameServer(Server server) {
		this.server = server;
		lobby = new ArrayList<>();
	}
	
	public void addToLobby(ClientHandler ch) {
		lobby.add(ch); // Thread safe maken (de variabele lobby)
		server.print(ch.getName() + " added to Lobby.");
	}
	
	public void removeFromLobby(ClientHandler ch) {
		lobby.remove(ch); // Thread safe maken (de variabele lobby)
	}
	
	public void run() {
		while (true) {
			//server.print("Lobby size: " + lobby.size());
			if (lobby.size() >= 2) {
				// start Game
				server.print("check");
				ClientHandler player1 = lobby.get(0);
				ClientHandler player2 = lobby.get(1);
				removeFromLobby(player1); // Thread safe maken (de variabele lobby)
				removeFromLobby(player2);
				new Thread((new Game(player1, player2))).start();
			}
		}
	}

}

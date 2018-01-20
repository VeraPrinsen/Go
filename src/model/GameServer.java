package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import serverclientconnection.*;

/**
 * The GameServer keeps a lobby with players that have requested a game and starts games.
 * @author vera.prinsen
 *
 */
public class GameServer implements Runnable {
	
	private Server server;
	private List<ClientHandler> lobby;
	Lock lock = new ReentrantLock();
	Condition lobbyCondition = lock.newCondition();
	
	public GameServer(Server server) {
		this.server = server;
		lobby = new ArrayList<>();
	}
	
	/**
	 * When a player sends a game request, add them to the lobby.
	 */
	// TO DO: MAKE lobby THREAD SAFE
	public void addToLobby(ClientHandler ch) {
		lock.lock();
		synchronized (lobby) {
			lobby.add(ch);
			server.print(ch.getName() + " added to Lobby.");
		}
		lobbyCondition.signal();
		lock.unlock();
	}
	
	/**
	 * When a game is started with two players, remove them from the waiting queue (lobby)
	 */
	// TO DO: MAKE lobby THREAD SAFE
	public void removeFromLobby(ClientHandler ch) {
		synchronized (lobby) {
			lobby.remove(ch);
		}
	}
	
	/**
	 * The GameServer checks the size of the lobby constantly and if there are 2 or more players, make a new game for the first 2 players. (default, with extension, this could be different)
	 */
	// TO DO: MAKE lobby THREAD SAFE
	// TO DO: EXCEPTION HANDLING
	public void run() {
		while (true) {
			lock.lock();
			if (lobby.size() >= 2) {
				synchronized (lobby) {
					ClientHandler player1 = lobby.get(0);
					ClientHandler player2 = lobby.get(1);
					removeFromLobby(player1);
					removeFromLobby(player2);
					new Thread((new Game(player1, player2)), "Game with " + player1.getName() + " and " + player2.getName()).start();
				}
			} else {
				try {
					lobbyCondition.await();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			lock.unlock();
		}
	}
}

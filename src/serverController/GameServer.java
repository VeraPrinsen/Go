package serverController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The GameServer keeps a lobby with players that have requested a game and starts games.
 * @author vera.prinsen
 *
 */
public class GameServer implements Runnable {
	
	private Server server;
	private List<ClientHandler> lobby;
	private List<GameController> games;
	private Lock lock = new ReentrantLock();
	private Condition lobbyCondition = lock.newCondition();
	private boolean isRunning;
	
	public GameServer(Server server) {
		this.server = server;
		lobby = new ArrayList<>();
		games = new ArrayList<>();
		isRunning = true;
	}
	
	public List<GameController> getGames() {
		return this.games;
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
		while (isRunning) {
			lock.lock();
			if (lobby.size() >= 2) {
				synchronized (lobby) {
					ClientHandler player1 = lobby.get(0);
					ClientHandler player2 = lobby.get(1);
					removeFromLobby(player1);
					removeFromLobby(player2);
					GameController newgame = new GameController(player1, player2);
					games.add(newgame);
					newgame.setup();
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
		server.print("GameServer closed.");
	}
	
	public void shutDown() {
		lock.lock();
		isRunning = false;
		lobbyCondition.signal();
		lock.unlock();
	}
}

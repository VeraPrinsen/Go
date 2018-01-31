package servercontroller;

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
	 * When a game is started with two players, remove them from the waiting queue (lobby).
	 */
	public void removeFromLobby(ClientHandler ch) {
		synchronized (lobby) {
			server.print(ch.getName() + " removed from Lobby.");
			lobby.remove(ch);
		}
	}
	
	public void removeGame(GameController game) {
		this.games.remove(game);
	}
	
	/**
	 * The GameServer checks the size of the lobby constantly and if there are 2 or more players, 
	 * make a new game for the first 2 players. (default, with extension, this could be different)
	 */
	public void run() {
		while (isRunning) {
			lock.lock();
			if (lobby.size() >= 2) {
				synchronized (lobby) {
					ClientHandler player1 = lobby.get(0);
					ClientHandler player2 = lobby.get(1);
					removeFromLobby(player1);
					removeFromLobby(player2);
					GameController newgame = new GameController(this, player1, player2);
					games.add(newgame);
					newgame.setup();
					server.print("A game has started between " + player1.getName() + " and " 
							+ player2.getName() + ".");
				}
			} else {
				try {
					lobbyCondition.await();
				} catch (InterruptedException e) {
					// If interrupted, the program is closing, do nothing..
				}
				
			}
			lock.unlock();
		}
	}
	
	public void shutDown() {
		lock.lock();
		isRunning = false;
		lobbyCondition.signal();
		lock.unlock();
	}
}

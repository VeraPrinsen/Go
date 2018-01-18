package model;

import serverclientconnection.*;

public class Game extends Thread {

	private final int numberOfPlayers;
	private Board board;
	private Player[] players;
	
	public Game(int numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
		this.board = null;
		players = new Player[this.numberOfPlayers];
	}
	
	public void run() {
		
	}
	
	public void setBoard(int DIM) {
		board = new Board(DIM);
	}
	
	public void setFirstPlayerColor(boolean white) {
		
	}
	
	public Board getBoard() {
		return board;
	}
	
	public void addPlayer(Player player) {
		
	}
	
	
	
}

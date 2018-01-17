package model;

public class Game {

	private final int numberOfPlayers;
	private Board board;
	private Player[] players;
	
	public Game(int numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
		this.board = null;
		players = new Player[this.numberOfPlayers];
	}
	
	public void start() {
		
	}
	
	public void setBoard(int DIM) {
		// board = new Board(DIM);
	}
	
	public Board getBoard() {
		return board;
	}
	
	public void addPlayer(Player player) {
		
	}
	
	
	
}

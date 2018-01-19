package model;

import general.Protocol;
import serverclientconnection.*;

public class Game implements Runnable {
	
	private ClientHandler[] players;
	private String[] playerColor;
	private Token[] playerToken;
	private int numberPlayers = 2;
	private int currentPlayer;
	private Board board;
	
	
	public Game(ClientHandler ch1, ClientHandler ch2) {
		players = new ClientHandler[numberPlayers];
		players[0] = ch1;
		players[1] = ch2;
	}
	
	public void run() {
		players[0].startGame(this, 0);
		players[1].startGame(this, 1);
		
		players[0].sendRequestSettings();
	}
	
	// PLAY THE GAME
	public void makeMove(int x, int y, int playerNo) {
		try {
			board.setField(x, y, playerToken[playerNo]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void setBoard(int DIM) {
		board = new Board(DIM);
	}
	
	public void setColors(ClientHandler player, String color) {
		if (player.equals(players[0])) {
			playerColor[0] = color;
			playerColor[1] = other(color);
		} else {
			playerColor[1] = color;
			playerColor[0] = other(color);
		}
		
		if (playerColor[0].equals(Protocol.General.BLACK)) {
			currentPlayer = 0;
		} else {
			currentPlayer = 1;
		}
		
		if (playerColor[0].equals(Protocol.General.BLACK)) {
			playerToken[0] = Token.BLACK;
			playerToken[1] = Token.WHITE;
		} else {
			playerToken[0] = Token.WHITE;
			playerToken[1] = Token.BLACK;
		}
	}
	
	public String other(String color) {
		if (color.equals(Protocol.General.BLACK)) {
			return Protocol.General.WHITE;
		} else
			return Protocol.General.BLACK;
	}
	
	public void sendStart() {
		players[0].sendStart(numberPlayers, playerColor[0], board.getDIM(), players[currentPlayer].getName(), players[Math.abs(currentPlayer-1)].getName());
		players[1].sendStart(numberPlayers, playerColor[1], board.getDIM(), players[currentPlayer].getName(), players[Math.abs(currentPlayer-1)].getName());
	}
}

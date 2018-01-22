package clientController;

import java.util.ArrayList;
import java.util.List;

import model.*;

public class ComputerPlayer implements Player {

	private ServerHandler sh;
	private Game game;
	
	public ComputerPlayer(ServerHandler sh) {
		this.sh = sh;
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	public void sendMove() {
		Board board = game.getBoard();
		int DIM = board.getDIM();
		
		List<Integer> emptyX = new ArrayList<>();
		List<Integer> emptyY = new ArrayList<>();
		
		for (int i = 0; i < DIM; i++) {
			for (int j = 0; j < DIM; j++) {
				if (board.isEmptyField(i, j)) {
					emptyX.add(i);
					emptyY.add(j);
				}
			}
		}
	
		int index = (int) Math.floor(Math.random() * emptyX.size());
		
		game.sendMove(emptyX.get(index), emptyY.get(index));
	}
}

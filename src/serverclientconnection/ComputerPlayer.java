package serverclientconnection;

import java.util.ArrayList;
import java.util.List;

import model.*;

public class ComputerPlayer implements Player {

	private ServerHandler sh;
	
	public ComputerPlayer(ServerHandler sh) {
		this.sh = sh;
	}
	
	public void sendMove() {
		Board board = sh.getBoard();
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
		
		sh.makeMove(emptyX.get(index), emptyY.get(index));
	}
}

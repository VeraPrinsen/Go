package clientcontroller;

import java.util.ArrayList;
import java.util.List;

import boardview.InvalidCoordinateException;
import model.Board;
import model.Field;
import model.Group;
import model.Token;

public class SmartStrategyCalc implements Runnable {

	private Game game;
	private String bestMove;
	private String bestStrategy;

	public SmartStrategyCalc(Game game) {
		this.game = game;
		bestMove = "random";
		bestStrategy = "random";
	}

	public void run() {
		Token playerToken = game.getToken();
		Board board = game.getBoard();
		int dim = board.getDIM();
		boolean foundBestMove = false;
		// Long running work
		
		// 1) If the opponent has passed, and you have the higher score at the moment, also pass
		if (!foundBestMove) {
			if (game.getPasses() == 1) {
				int playerScore = game.getBoard().getScore(game.getToken());
				int opponentScore = game.getBoard().getScore(game.getToken().other());
	
				if (playerScore > opponentScore) {
					bestMove = "pass";
					bestStrategy = "pass after opponent passed";
					foundBestMove = true;
				}
			}
		}

		// 2) If a group of the other token can be captured, do this (capture the largest group)
		if (!foundBestMove) {
			int bestMoveSize = 0;
			
			for (Group g : board.getGroups()) {
				if (g.getToken().equals(playerToken.other())) {
					String outcome = g.canCapture(playerToken);
					
					if (!outcome.equals("false")) {
						String[] coords = outcome.split("_");
						int x = Integer.parseInt(coords[0]);
						int y = Integer.parseInt(coords[1]);
						try {
							if (board.checkMove(x, y, playerToken)) {
								if (g.size() > bestMoveSize) {
									bestMove = outcome;
									bestMoveSize = g.size();
									bestStrategy = "capture a group";
									foundBestMove = true;
								}
							}
						} catch (InvalidCoordinateException e) {
													
						}
					}
				}
			}
		}
		
		// 3) Prevent the opponent from capturing a group
		if (!foundBestMove) {
			int bestMoveSize = 0;
			
			for (Group g : board.getGroups()) {
				if (g.getToken().equals(playerToken)) {
					String outcome = g.canCapture(playerToken.other());
					
					if (!outcome.equals("false")) {
						String[] coords = outcome.split("_");
						int x = Integer.parseInt(coords[0]);
						int y = Integer.parseInt(coords[1]);
						try {
							if (board.checkMove(x, y, playerToken)) {
								if (g.size() > bestMoveSize) {
									bestMove = outcome;
									bestMoveSize = g.size();
									bestStrategy = "prevent opponent from capturing a group";
									foundBestMove = true;
								}
							}
						} catch (InvalidCoordinateException e) {
							
						}
					}
				}
			}
		}
		
		// 4) Random move on an empty field, but:
		//		- Don't make the move if the opponent can than capture you
		//		- Don't make the move if it is in an empty group you already 'own'
		if (!foundBestMove) {
			List<Integer> emptyX = new ArrayList<>();
			List<Integer> emptyY = new ArrayList<>();
	
			for (int i = 0; i < dim; i++) {
				for (int j = 0; j < dim; j++) {
					if (board.isEmptyField(i, j)) {
						Board nextBoard = board.boardCopy();
						nextBoard.setField(i, j, playerToken);
						
						try {
							if (board.checkMove(i, j, playerToken) && 
									!nextBoard.canBeCaptured(playerToken) && 
									!board.getBoardField(i, j).getGroup().isCaptured(playerToken)) {
								emptyX.add(i);
								emptyY.add(j);
							}
						} catch (InvalidCoordinateException e) {
							// Is an invalid move
						}
					}
				}
			}
			if (emptyX.size() == 0 && 
					board.getScore(playerToken) > board.getScore(playerToken.other())) {
				bestMove = "pass";
				bestStrategy = "random / no capture";
				foundBestMove = true;
			} else if (emptyX.size() != 0) {
				int index = (int) Math.floor(Math.random() * emptyX.size());
				bestMove = emptyX.get(index) + "_" + emptyY.get(index);
				bestStrategy = "random / no capture";
				foundBestMove = true;
			}
		}
	}

	public String getBestMove() {
		return this.bestMove;
	}
	
	public String getBestStrategy() {
		return this.bestStrategy;
	}

}

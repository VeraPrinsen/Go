package clientcontroller;

import java.util.List;
import java.util.Set;

import boardview.InvalidCoordinateException;
import model.Board;
import model.Field;
import model.Group;
import model.Token;

public class SmarterStrategyCalc implements Runnable {

	private Game game;
	private String bestMove;
	private String bestStrategy;
	private boolean canCalculate;

	public SmarterStrategyCalc(Game game) {
		this.game = game;
		bestMove = "random";
		bestStrategy = "random";
	}

	public void run() {
		Token playerToken = game.getToken();
		Board board = game.getBoard();
		int dim = board.getDIM();

		List<Field> emptyFields = board.getEmptyFields();
		boolean foundBestMove = false;
		canCalculate = true;

		// ===============================================================================
		//
		// First: Create a random option
		//
		// ===============================================================================
		if (emptyFields.size() > 0 && canCalculate) {
			boolean moveOK = false;

			while (!moveOK) {
				int index = (int) Math.floor(Math.random() * emptyFields.size());
				int x = emptyFields.get(index).getX();
				int y = emptyFields.get(index).getY();
				try {
					if (board.checkMove(x, y, playerToken)) {
						bestMove = x + "_" + y;
						bestStrategy = "random / move";
						moveOK = true;
					}
				} catch (InvalidCoordinateException e) {
					// Move is not valid.
				}
			}
		} else {
			// Board has no empty fields
			bestMove = "pass";
			bestStrategy = "random / pass";
		}

		// ===============================================================================
		//
		// Then: A few strategy tactics from most important to least important
		//

		// ===============================================================================
		// If opponent has passed and you have the higher score at the moment, also pass
		// (and WIN!)
		// ===============================================================================
		if (!foundBestMove && canCalculate) {
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

		// ===============================================================================
		// If a group of the other token can be captured, do this (capture the largest
		// group)
		// ===============================================================================
		if (!foundBestMove && canCalculate) {
			int bestMoveSize = 0;

			if (board.canBeCaptured(playerToken.other())) {
				for (Group g : board.getGroups()) {
					if (g.getToken().equals(playerToken.other()) && canCalculate) {
						String outcome = g.canCapture();

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
								// An exception is thrown when the move is not valid,
								// then we just shouldn't do the move
							}
						}
					}
					if (!canCalculate) {
						break;
					}
				}
			}
		}

		// ===============================================================================
		// Prevent the opponent from capturing a group
		// ===============================================================================
		if (!foundBestMove && canCalculate) {
			int bestMoveSize = 0;

			if (board.canBeCaptured(playerToken)) {
				for (Group g : board.getGroups()) {
					if (g.getToken().equals(playerToken) && canCalculate) {
						String outcome = g.canCapture();

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
					if (!canCalculate) {
						break;
					}
				}
			}
		}

		// ===============================================================================
		// Check if you don't make a move in an empty group you already 'own'
		// ===============================================================================
		if (!foundBestMove && canCalculate && emptyFields.size() > 0) {
			List<Group> hasAreas = board.hasAreas(playerToken);

			for (Group g : hasAreas) {
				Set<Field> fields = g.getGroup();
				for (Field f : fields) {
					emptyFields.remove(f);
					if (!canCalculate) {
						break;
					}
				}
				if (!canCalculate) {
					break;
				}
			}

			if (emptyFields.size() == 0 
					&& (board.getScore(playerToken) > board.getScore(playerToken.other()))) {
				bestMove = "pass";
				bestStrategy = "pass";
			} else if (emptyFields.size() != 0) {
				boolean moveOK = false;
				while (!moveOK) {
					int index = (int) Math.floor(Math.random() * emptyFields.size());
					int x = emptyFields.get(index).getX();
					int y = emptyFields.get(index).getY();
					try {
						if (board.checkMove(x, y, playerToken)) {
							bestMove = emptyFields.get(index).getX() + "_" 
									+ emptyFields.get(index).getY();
							bestStrategy = "random / no owned group";
							moveOK = true;
						}
					} catch (InvalidCoordinateException e) {
						// invalid move
					}
				}
			}

		}

		// ===============================================================================
		// Check if the move allows the opponent to capture you next turn
		// ===============================================================================
		if (!foundBestMove && canCalculate) {
			if (emptyFields.size() == 0 
					&& (board.getScore(playerToken) > board.getScore(playerToken.other()))) {
				bestMove = "pass";
				bestStrategy = "random / no capture";
				foundBestMove = true;
			} else if (emptyFields.size() != 0) {
				boolean moveOK = false;
				while (!moveOK) {
					int index = (int) Math.floor(Math.random() * emptyFields.size());
					int x = emptyFields.get(index).getX();
					int y = emptyFields.get(index).getY();

					Board nextBoard = board.boardCopy();
					nextBoard.setField(x, y, playerToken);
					try {
						if (board.checkMove(x, y, playerToken) 
								&& !nextBoard.canBeCaptured(playerToken)) {
							bestMove = emptyFields.get(index).getX() + "_" 
								+ emptyFields.get(index).getY();
							bestStrategy = "random / not allowing opponent to capture you";
							moveOK = true;
						}
					} catch (InvalidCoordinateException e) {
						// invalid move
					}
				}
			}
		}

		// ===============================================================================
		// Check if the move allows the opponent to capture you next turn
		// ===============================================================================
		if (!foundBestMove && canCalculate) {
			if (board.getScore(playerToken) > (board.getScore(playerToken.other()) 
					+ (1 / 5) * dim * dim)) {
				bestMove = "pass";
				bestStrategy = "pass if you have 1/10 point more than the other player";
			}
		}
	}

	public String getBestMove() {
		return this.bestMove;
	}

	public String getBestStrategy() {
		return this.bestStrategy;
	}

	public void endCalc() {
		canCalculate = false;
	}

}

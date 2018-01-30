package clientcontroller;

import java.util.Iterator;
import java.util.List;

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

		System.out.println("Random finished.");

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

		System.out.println("Pass finished.");

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

		System.out.println("Capture finished.");

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

		System.out.println("Prevent capture finished.");

		// ===============================================================================
		// First remove non-valid moves from the emptyFields list
		// ===============================================================================
		if (!foundBestMove && canCalculate) {
			Iterator<Field> iterF1 = emptyFields.iterator();
			while (iterF1.hasNext() && canCalculate) {
				Field f = iterF1.next();
				int x = f.getX();
				int y = f.getY();
				try {
					if (board.checkMove(x, y, playerToken)) {
						// valid move, let it in the emptyFields list
					}
				} catch (InvalidCoordinateException e) {
					iterF1.remove();
				}
			}
		}

		// ===============================================================================
		// Check if you don't make a move in an empty group you already 'own'
		// ===============================================================================
		if (!foundBestMove && canCalculate) {
			if (board.emptyIsCaptured(playerToken)) {
				Iterator<Field> iterF3 = emptyFields.iterator();
				while (iterF3.hasNext() && canCalculate) {
					Field f = iterF3.next();
					int x = f.getX();
					int y = f.getY();
					Board nextBoard = board.boardCopy();
					nextBoard.setField(x, y, playerToken);

					if (board.getBoardField(x, y).getGroup().isCaptured(playerToken)) {
						iterF3.remove();
					}

					int index = (int) Math.floor(Math.random() * emptyFields.size());
					bestMove = emptyFields.get(index).getX() + "_" + emptyFields.get(index).getY();
					bestStrategy = "random / not placing in owned group";
				}
			}
		}

		System.out.println("Random / not placing in owned group finished.");

		// ===============================================================================
		// Check if the move allows the opponent to capture you next turn
		// ===============================================================================
		if (!foundBestMove && canCalculate) {
			Iterator<Field> iterF2 = emptyFields.iterator();
			while (iterF2.hasNext() && canCalculate) {
				Field f = iterF2.next();
				int x = f.getX();
				int y = f.getY();
				Board nextBoard = board.boardCopy();
				nextBoard.setField(x, y, playerToken);

				if (nextBoard.canBeCaptured(playerToken)) {
					iterF2.remove();
				}
			}

			if (emptyFields.size() == 0 && (board.getScore(playerToken) > board.getScore(playerToken.other()))) {
				bestMove = "pass";
				bestStrategy = "random / no capture";
				foundBestMove = true;
			} else if (emptyFields.size() != 0) {
				int index = (int) Math.floor(Math.random() * emptyFields.size());
				bestMove = emptyFields.get(index) + "_" + emptyFields.get(index);
				bestStrategy = "random / no capture";
				foundBestMove = true;
			}
		}

		System.out.println("Random+ finished.");
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

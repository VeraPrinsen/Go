package clientcontroller;

/**
 * SmarterStrategy is one of the strategies a computer can have.
 * @author vera.prinsen
 *
 */
public class SmarterStrategy implements Strategy {

	private Player player;
	private String bestMove;
	private int reactionTimeAI;

	public SmarterStrategy(Player player, int reactionTimeAI) {
		this.player = player;
		this.reactionTimeAI = reactionTimeAI;
	}

	/**
	 * The method that is called to determine the best move following a random strategy.
	 * After reactionTimeAI seconds the answer is gotten from the SmarterStrategyCalc,
	 * also if the calculator is not yet done with every strategy type.
	 */
	public String sendMove() {
		long startTime = System.currentTimeMillis();
		long endTime = startTime + 1000 * reactionTimeAI;
				
		SmarterStrategyCalc newCalc = new SmarterStrategyCalc(player.getGame());
		Thread t = new Thread(newCalc, "SmarterStrategyCalc");
		
		t.start(); // Kick off calculations
		while ((System.currentTimeMillis() < endTime) && t.isAlive()) {
		    // Still within time threshold, wait a little longer
		    try {
		        Thread.sleep(50L);  // Sleep 1/10 second
		    } catch (InterruptedException e) {
		         // Someone woke us up during sleep, that's OK
		    }
		}
				
		newCalc.endCalc();
		bestMove = newCalc.getBestMove();
		
		return bestMove;
	}
}

package clientcontroller;

public class SmartStrategy implements Strategy {

	private Player player;
	private String bestMove;
	private String bestStrategy;
	private int reactionTimeAI;

	public SmartStrategy(Player player, int reactionTimeAI) {
		this.player = player;
		this.reactionTimeAI = reactionTimeAI;
	}

	public String sendMove() {
		long startTime = System.currentTimeMillis();
		long endTime = startTime + 1000 * reactionTimeAI;
				
		SmartStrategyCalc newCalc = new SmartStrategyCalc(player.getGame());
		Thread t = new Thread(newCalc, "SmartStrategyCalc");
		
		t.start(); // Kick off calculations
		while ((System.currentTimeMillis() < endTime) && t.isAlive()) {
		    // Still within time theshold, wait a little longer
		    try {
		        Thread.sleep(50L);  // Sleep 1/2 second
		    } catch (InterruptedException e) {
		         // Someone woke us up during sleep, that's OK
		    }
		}
		// t.interrupt();
		
		newCalc.endCalc();
		bestMove = newCalc.getBestMove();
		bestStrategy = newCalc.getBestStrategy();
		
		System.out.println(bestStrategy);
		return bestMove;
	}
}

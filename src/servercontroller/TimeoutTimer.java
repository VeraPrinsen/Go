package servercontroller;

import general.*;

public class TimeoutTimer implements Runnable {
	
	private GameController game;
	private long startTime;
	private long endTime;
	
	public TimeoutTimer(GameController game) {
		this.game = game;
		resetTimer();
	}
	
	public void run() {
		while (System.currentTimeMillis() < endTime) {
			try {
				Thread.sleep(100L);
			} catch (InterruptedException e) {
				
			}
		}
		game.timeout();
	}
	
	public void resetTimer() {
		this.startTime = System.currentTimeMillis();
		this.endTime = startTime + Protocol.General.TIMEOUTSECONDS * 1000L;
	}

}

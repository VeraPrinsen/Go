package servercontroller;

import general.*;

public class TimeoutTimer implements Runnable {
	
	private GameController game;
	private long startTime;
	private long endTime;
	private boolean gameRunning;
	
	public TimeoutTimer(GameController game) {
		this.game = game;
		gameRunning = true;
	}
	
	public void run() {
		resetTimer();
		while (System.currentTimeMillis() < endTime && gameRunning) {
			try {
				Thread.sleep(50L);
			} catch (InterruptedException e) {
				
			}
		}
		
		if (gameRunning) {
			game.timeout();
		}
	}
	
	public void resetTimer() {
		this.startTime = System.currentTimeMillis();
		this.endTime = startTime + Protocol.General.TIMEOUTSECONDS * 1000L;
	}
	
	public void stopGame() {
		gameRunning = false;
	}

}
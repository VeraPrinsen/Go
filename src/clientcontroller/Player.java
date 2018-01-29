package clientcontroller;

public interface Player {
	
	public void sendMove();

	public void setGame(Game game);
	
	public Game getGame();
}

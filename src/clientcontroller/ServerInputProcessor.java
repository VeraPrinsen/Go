package clientcontroller;

public class ServerInputProcessor implements Runnable {
	
	private ServerHandler sh;
	private String message;
	
	public ServerInputProcessor(ServerHandler sh, String message) {
		this.sh = sh;
		this.message = message;
	}

	public void run() {
		sh.processServerInput(message);
	}
}

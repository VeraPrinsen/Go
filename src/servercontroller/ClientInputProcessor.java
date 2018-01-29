package servercontroller;

public class ClientInputProcessor implements Runnable {

	private ClientHandler ch;
	private String message;
	
	public ClientInputProcessor(ClientHandler ch, String message) {
		this.ch = ch;
		this.message = message;
	}
	
	public void run() {
		ch.processClientInput(message);
	}
}

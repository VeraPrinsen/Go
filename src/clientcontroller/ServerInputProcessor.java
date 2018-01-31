package clientcontroller;

/**
 * The ServerInputProcessor is a new thread that is created for each new input from the server.
 * @author vera.prinsen
 *
 */
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

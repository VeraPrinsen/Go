package clientController;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * The ServerInputHandler handles all the input that the client gets from the server.
 * @author vera.prinsen
 *
 */
public class ServerInputHandler implements Runnable {
	
	private ServerHandler sh;
	private BufferedReader in;
	private boolean isOpen;
	
	public ServerInputHandler(ServerHandler sh, BufferedReader in) {
		this.sh = sh;
		this.in = in;
		isOpen = true;
	}
	
	/**
	 * This checks the input stream from the server to this particular client constantly.
	 */
	// TO DO: EXCEPTION HANDLING
	public void run() {
		String msg;
		try {
			while (isOpen && (msg = in.readLine()) != null) {
				Thread newInput = new Thread(new ServerInputProcessor(sh, msg));
				newInput.setDaemon(true);
				newInput.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		// Server has disconnected
		sh.print("");
		sh.print("");
		sh.print("The server has disconnected");
		sh.client.shutDown();
	}
	
	public void shutDown() {
		isOpen = false;
	}
}

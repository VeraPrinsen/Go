package servercontroller;

import java.io.BufferedReader;
import java.io.IOException;


/**
 * The ClientInputHandler handles all the input that the server gets from this
 * specific client.
 * 
 * @author vera.prinsen
 *
 */
public class ClientInputHandler implements Runnable {

	private BufferedReader in;
	private ClientHandler ch;
	private boolean isOpen;

	public ClientInputHandler(ClientHandler ch, BufferedReader in) {
		this.ch = ch;
		this.in = in;
		isOpen = true;
	}

	/**
	 * This checks the input stream from the client to the server constantly.
	 */
	public void run() {
		String msg;
		try {
			msg = in.readLine();
			while (isOpen && msg != null) {
				Thread newInput = new Thread(new ClientInputProcessor(ch, msg), 
						"ClientInputProcessor");
				newInput.setDaemon(true);
				newInput.start();
				msg = in.readLine();
			}
		} catch (IOException e) {
			// inputStream is probably closed, do nothing here..
		}
		
		// If inputStream is closed, but server is still running, disconnect the client.
		if (isOpen) {
			ch.shutDown();
		}
	}

	public void shutDown() {
		isOpen = false;
	}
}

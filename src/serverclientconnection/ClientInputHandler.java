package serverclientconnection;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * The ClientInputHandler handles all the input that the server gets from this specific client.
 * @author vera.prinsen
 *
 */
public class ClientInputHandler implements Runnable {

	private BufferedReader in;
	private ClientHandler ch;
	
	public ClientInputHandler(ClientHandler ch, BufferedReader in) {
		this.ch = ch;
		this.in = in;
	}
	
	/**
	 * This checks the input stream from the client to the server constantly.
	 */
	// TO DO: EXCEPTION HANDLING
	public void run() {
		String msg;
		try {
			while ((msg = in.readLine()) != null) {
				ch.processClientInput(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

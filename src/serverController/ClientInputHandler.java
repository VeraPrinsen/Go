package serverController;

import java.io.BufferedReader;
import java.io.IOException;

import general.Protocol;

/**
 * The ClientInputHandler handles all the input that the server gets from this specific client.
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
	// TO DO: EXCEPTION HANDLING
	public void run() {
		String msg;
		try {
			while (isOpen && (msg = in.readLine()) != null) {
				ch.processClientInput(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// NOG EVEN NAAAR KIJKEN OF DIT GOED IS
		try {
			if (in.readLine() == null) {
				ch.getOpponent().sendEndGame(Protocol.Server.ABORTED);
				ch.shutDown();
			}
		} catch (IOException e) {
			ch.shutDown();
		}
	}
	
	public void shutDown() {
		try {
			isOpen = false;
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

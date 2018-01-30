package servercontroller;

import java.io.BufferedReader;
import java.io.IOException;

import clientcontroller.ServerInputProcessor;
import general.Protocol;

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
	// TO DO: EXCEPTION HANDLING
	public void run() {
		String msg;
		try {
			while (isOpen && (msg = in.readLine()) != null) {
				Thread newInput = new Thread(new ClientInputProcessor(ch, msg), "ClientInputProcessor");
				newInput.setDaemon(true);
				newInput.start();
			}
		} catch (IOException e) {
			
		}
		
		if (isOpen) {
			ch.shutDown();
		}
	}

	// WHEN IS THIS USED?????? AND SHOULD THE CALLER OF THIS REMOVE THE CLIENT FROM CLIENTLIST IN SERVER????
	public void shutDown() {
		isOpen = false;
	}
}

package serverController;

import java.io.BufferedReader;
import java.io.IOException;

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
				ch.processClientInput(msg);
			}
			ch.print("CLIENTINPUT OUT OF WHILE LOOP");
			// If the client disconnects, the loop will terminate and will end up here.
		} catch (IOException e) {
			ch.print("CLIENTINPUT IOEXCEPTION");
		}

//		// NOG EVEN NAAAR KIJKEN OF DIT GOED IS
//		if (isOpen) {
//			ch.getGame().sendEnd(Protocol.Server.ABORTED);
//			ch.shutDown();
//		}
//		
		ch.print("ClientInputHandler closed.");
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

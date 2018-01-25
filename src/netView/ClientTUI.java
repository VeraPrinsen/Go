package netView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import clientController.Client;
import general.Protocol;

/**
 * The ClientTUI takes care of the in- and output to and from the console.
 * 
 * @author vera.prinsen
 *
 */
public class ClientTUI implements Runnable {

	private Client client;
	private BufferedReader in;
	private boolean isOpen;

	public ClientTUI(Client client) {
		this.client = client;
		in = new BufferedReader(new InputStreamReader(System.in));
		isOpen = true;
	}

	// THIS CHECKS FOR INPUT FROM THE CLIENT ITSELF
	public void run() {
		String msg;
		try {
			while (isOpen && (msg = in.readLine()) != null) {
				// exitLock.unlock();
				if (msg.equalsIgnoreCase("exit")) {
					client.shutDown();
				} else {
					client.processClientInput(msg);
				}
				// exitLock.lock();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void print(String msg) {
		System.out.println(msg);
	}

	public void shutDown() {
		isOpen = false;
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method reads a String from the input console.
	 */
	// TO DO: EXCEPTION HANDLING
	public String readString(String prompt) {
		String msg;
		try {
			System.out.print(prompt + ": ");
			if ((msg = in.readLine()).equalsIgnoreCase("exit")) {
				return Protocol.Client.QUIT;
			} else {
				return msg;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return Protocol.Client.QUIT;
		}
	}

	/**
	 * Method reads an integer from the input console.
	 */
	public int readInt(String prompt) {
		boolean inputOK = false;
		int input = 0;
		
		while (!inputOK) {
			try {
				String inputString = readString(prompt);
				
				if (inputString.equals(Protocol.Client.QUIT)) {
					input = -1;
					inputOK = true;
				} else {
					input = Integer.parseInt(inputString);
					inputOK = true;
				}
			} catch (NumberFormatException e) {
				print("Input must be an integer.");
			}
		}
		
		return input; 
	}
}

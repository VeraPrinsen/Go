package netview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import general.Protocol;

/**
 * The ClientTUI takes care of the in- and output to and from the console.
 * 
 * @author vera.prinsen
 *
 */
public class ClientTUI {
	
	private BufferedReader in;

	public ClientTUI() {
		in = new BufferedReader(new InputStreamReader(System.in));
	}

	// STARTERS & STOPPERS ====================================================
	public void shutDown() {
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// PRINTERS & SENDERS =====================================================
	/**
	 * Prints a message on the console of this particular client.
	 */
	public void print(String msg) {
		System.out.println(msg);
	}

	/**
	 * Method reads a String from the input console.
	 */
	public String readString(String prompt) {
		String msg;
		try {
			System.out.print(prompt + ": ");
			msg = in.readLine();
			if (msg.equalsIgnoreCase("exit")) {
				return Protocol.Client.EXIT;
			} else if (msg.equalsIgnoreCase("quit")) {
				return Protocol.Client.QUIT;
			} else {
				return msg;
			}
		} catch (IOException e) {
			// Client console is disconnected:
			// send server that client is not available and will exit.
			return Protocol.Client.EXIT;
		}
	}

	/**
	 * Method reads an integer from the input console.
	 * 	(using readString(String prompt))
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
				} else if (inputString.equals(Protocol.Client.EXIT)) {
					input = -2;
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

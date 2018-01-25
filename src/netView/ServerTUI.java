package netView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import general.Protocol;
import serverController.Server;

/**
 * The ServerTUI takes care of the in- and output to and from the console.
 * 
 * @author vera.prinsen
 *
 */
public class ServerTUI implements Runnable {

	private Server server;
	private BufferedReader in;
	private boolean isOpen;

	public ServerTUI(Server server) {
		this.server = server;
		in = new BufferedReader(new InputStreamReader(System.in));
		isOpen = true;
	}

	/**
	 * This method checks the input console of the server constantly.
	 */
	// TO DO: EXCEPTION HANDLING
	public void run() {
		String msg;
		try {
			while (isOpen && (msg = in.readLine()) != null) {
				server.processServerInput(msg);
			}
		} catch (IOException e) {
			// WHAT KIND OF EXCEPTION WILL THIS BE?
			e.printStackTrace();
		}
		
		print("ServerTUI closed.");
	}

	// INPUT
	// ====================================================================================
	/**
	 * ReadString from Server TUI
	 */
	// TO DO: EXCEPTION HANDLING
	public String readString(String prompt) {
		String msg = "";
		System.out.print(prompt + ": ");
		try {
			msg = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return msg;
	}

	/**
	 * Before the TUI is started, this is called to get the port that the server
	 * must listen on.
	 */
	// TO DO: EXCEPTION HANDLING
	public int getPort() throws Exception {
		boolean portOK = false;
		int portInt = 0;

		while (!portOK) {
			String portString = readString("Enter the port you want to use");
			try {
				portInt = Integer.parseInt(portString);
				portOK = true;
			} catch (NumberFormatException e) {
				print("The input must be an integer.");
			}
		}

		return portInt;
	}

	// OUTPUT
	// ====================================================================================
	/**
	 * This method prints output on the console of the server.
	 */
	public void print(String msg) {
		System.out.println(msg);
	}

	// SHUTDOWN TUI
	// ====================================================================================
	/**
	 * This method is called when the server wants to shut down.
	 */
	// TO DO: EXCEPTION HANDLING
	public void shutDown() {
		try {
			isOpen = false;
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

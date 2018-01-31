package netview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import servercontroller.Server;

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
	public void run() {
		String msg;
		try {
			msg = in.readLine();
			while (isOpen && msg != null) {
				server.processServerInput(msg);
				msg = in.readLine();
			}
		} catch (IOException e) {
			// inputStream will be closed, program is closing, do nothing..
		}
		
	}

	// INPUT
	// ====================================================================================
	/**
	 * ReadString from Server TUI.
	 */
	public String readString(String prompt) {
		String msg = "";
		System.out.print(prompt + ": ");
		try {
			msg = in.readLine();
		} catch (IOException e) {
			// inputStream is closed, program is closing, do nothing..
		}
		return msg;
	}

	/**
	 * Before the TUI is started, this is called to get the port that the server
	 * must listen on.
	 */
	public int getPort() {
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
	public void shutDown() {
		try {
			isOpen = false;
			in.close();
		} catch (IOException e) {
			// inputStream is already closed, do nothing..
		}
	}

}

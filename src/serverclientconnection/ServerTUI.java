package serverclientconnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerTUI implements Runnable {
	
	private Server server;
	private BufferedReader in;
	
	public ServerTUI(Server server) {
		this.server = server;
		in = new BufferedReader(new InputStreamReader(System.in));
	}
	
	/**
	 * This method checks the input console of the server constantly.
	*/
	// TO DO: EXCEPTION HANDLING
	public void run() {
		String msg;
		try {
			while ((msg = in.readLine()) != null) {
				server.processServerInput(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method prints output on the console of the server.
	 */
	public void print(String msg) {
		System.out.println(msg);
	}
	
	/**
	 * Before the TUI is started, this is called to get the port that the server must listen on.
	 */
	// TO DO: NOW IT IS DEFAULT, MAKE IT CONFIGURABLE (JUST REMOVE THE COMMANDS AND REMOVE return 4567;)
	public int getPort() throws Exception {
		//print("Enter the port you want to use: ");
		//return Integer.parseInt(in.readLine());
		return 4567;
	}

}

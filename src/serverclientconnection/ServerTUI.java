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
	
	// THIS CHECKS THE INPUT CONSOLE OF THE SERVER CONSTANTLY
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
		
	// THIS IS THE OUTPUT CONSOLE OF THE SERVER
	public void print(String msg) {
		System.out.println(msg);
	}
	
	// RANDOM GETTERS THAT OTHER CLASSES USE TO GET SPECIFIC INFORMATION FROM THE CONSOLE
	public int getPort() throws Exception {
		print("Enter the port you want to use: ");
		return Integer.parseInt(in.readLine());
	}

}

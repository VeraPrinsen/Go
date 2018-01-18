package serverclientconnection;

import java.io.BufferedReader;
import java.io.IOException;

public class ClientInputHandler implements Runnable {

	private BufferedReader in;
	private ClientHandler ch;
	
	public ClientInputHandler(ClientHandler ch, BufferedReader in) {
		this.ch = ch;
		this.in = in;
	}
	
	// THIS CHECKS FOR INPUT FROM THE CLIENT TO THE SERVER
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

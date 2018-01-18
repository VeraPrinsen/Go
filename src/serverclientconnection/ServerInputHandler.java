package serverclientconnection;

import java.io.BufferedReader;
import java.io.IOException;

public class ServerInputHandler implements Runnable {
	
	private ServerHandler sh;
	private BufferedReader in;
	
	public ServerInputHandler(ServerHandler sh, BufferedReader in) {
		this.sh = sh;
		this.in = in;
	}
	
	// THIS CHECKS FOR THE INPUT FROM THE SERVER TO THE CLIENT
	public void run() {
		String msg;
		try {
			while ((msg = in.readLine()) != null) {
				sh.processServerInput(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

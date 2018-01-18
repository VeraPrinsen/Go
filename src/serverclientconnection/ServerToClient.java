package serverclientconnection;

import java.io.BufferedWriter;

public class ServerToClient implements Runnable {
	
	private ClientHandler ch;
	private BufferedWriter out;
	
	public ServerToClient(ClientHandler ch, BufferedWriter out) {
		this.ch = ch;
		this.out = out;
	}
	
	public void run() {
		
	}
	
}

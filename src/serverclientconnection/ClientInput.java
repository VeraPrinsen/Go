package serverclientconnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;

public class ClientInput implements Runnable {

	private ServerHandler sh;
	private BufferedWriter out;
	private BufferedReader in;
	
	public ClientInput(ServerHandler sh, BufferedWriter out) {
		this.sh = sh;
		this.out = out;
		in = new BufferedReader(new InputStreamReader(System.in));
	}
	
	// THIS CHECKS FOR INPUT FROM THE CLIENT ITSELF
	public void run() {
		String msg;
		try {
			while ((msg = in.readLine()) != null) {
				sh.processClientInput(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

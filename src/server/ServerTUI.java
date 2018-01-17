package server;

import java.util.Scanner;

/**
 * Makes a new server, that server will show results on the TUI.
 * TUI will ask for a port and will create a new server with that port.
 * @author vera.prinsen
 *
 */
public class ServerTUI {
	
	Scanner in;
	Server server;
	
	public static void main(String[] args) {
		(new ServerTUI()).start();		
	}
	
	public ServerTUI() {
		in = new Scanner(System.in);
		server = new Server(this);
	}

	public void start() {
		server.start();
	}
	
	// Input ========================================
	public String readString(String prompt) {
		System.out.print(prompt + ": ");
		return in.nextLine();
	}
	
	// Output =======================================
	public void print(String prompt) {
		System.out.println(prompt);
	}
}

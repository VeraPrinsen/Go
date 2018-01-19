package serverclientconnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * The file that is executed to start a client.
 * 
 * @author vera.prinsen
 */
public class Client {

	private Socket sock;
	private String clientName;
	private ClientTUI tui;
	
	ServerHandler sh;
	BufferedReader in;
	BufferedWriter out;
	
	public Client() {
		// TO DO: MAKE NAME CONFIGURABLE IN CLIENT TUI
		this.tui = new ClientTUI(this);
	}
	
	// GETTERS =================================================================
	public String getName() {
		return this.clientName;
	}
	
	// INPUT PROCESSORS ========================================================
//	/**
//	 * 
//	 */
//	public void processClientInput(String message) {
//		sh.processClientInput();
//	}
	
	// PRINTERS & SENDERS ========================================================
	/**
	 * This method is used if some information should be send to the console of this particular client.
	 */
	public void print(String msg) {
		tui.print(msg);
	}
	
	public String readString(String prompt) {
		return tui.readString(prompt);
	}
	
	public int readInt(String prompt) {
		return tui.readInt(prompt);
	}
	
	// START UP AND SHUT DOWN OF THE CLIENT ====================================================================
	/**
	 * These are the first few things that must be done when starting the client.
	 * 		Make connection to a server
	 * 		Create in- and output from and to the server
	 * 		Create serverHandler that processes this in- and output
	 */
	// TO DO: EXCEPTION HANDLING
	// TO DO: ASK FOR NAME
	// TO DO: ASK FOR HOST AND PORT
	public void start() throws Exception {
		// First: Ask for name
		clientName = readString("What is your name?");
		print("Welcome " + clientName + "!");
		
		System.out.println("Trying to connect to the server.");
		sock = new Socket("localhost", 4567);
		System.out.println("Connected to server.");
		
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		
		sh = new ServerHandler(this, in, out);
	}
	
	/**
	 * This method is used when the client must be shut down.
	 */
	// TO DO: EXCEPTION HANDLING
	public void shutDown() throws Exception {
		sock.close();
	}
	
	/**
	 * Starts a new client.
	 */
	// TO DO: EXCEPTION HANDLING
	public static void main(String[] args) throws Exception {
		(new Client()).start();
	}
}
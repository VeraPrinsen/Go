package clientController;

import general.*;
import netView.ClientTUI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The file that is executed to start a client.
 * 
 * @author vera.prinsen
 */
public class Client {

	private Socket sock;
	private String clientName;
	private ClientTUI tui;
	
	private ServerHandler sh;
	private BufferedReader in;
	private BufferedWriter out;
	
	public Client() {
		this.tui = new ClientTUI(this);
	}
	
	// GETTERS & SETTERS =================================================================	
	public String getName() {
		return this.clientName;
	}
	
	// INPUT PROCESSORS ========================================================
	/**
	 * In between the directed input, you can still give commands in the ClientTUI, this is processed here.
	 */
	public void processClientInput(String message) {
		print(message);
	}
	
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
	// TO DO: ASK FOR HOST AND PORT
	public void start() throws Exception {		
//		boolean ipOK = false;
//		InetAddress addr = null;
//		
//		while (!ipOK) {
//			String ipAddress = readString("Enter IP address of the Server host");
//			
//			try {
//	            addr = InetAddress.getByName(ipAddress);
//	            ipOK = true;
//	        } catch (UnknownHostException e) {
//	            print("ERROR: host " + ipAddress + " unknown.");
//	        }
//		}
//		
//		boolean portOK = false;
//		int port = 0;
//		
//		while (!portOK) {
//			try {
//				port = readInt("To what portnumber you want to connect to?");
//				print("");
//				print("Trying to connect to the server.");
//				sock = new Socket(addr, port);
//				print("Connected to server.");
//				print("");
//				portOK = true;
//			} catch (Exception e) {
//				print("There is nothing to connect to on port " + port + ". Try another port.");
//			}
//
//		}
		sock = new Socket("localhost", 5678);
		
		// First: Ask for name
		clientName = readString("What is your name?");
		print("");
		
		print("-----------------------");
		print("|     o-o   o-o       |");
		print("|     o     o   o     |");
		print("|     |  -o |   |     |");
		print("|     o   | o   o     |");
		print("|      o-o   o-o      |");
		print("-----------------------");
		
		print("Welcome " + clientName + "!");
		
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		
		sh = new ServerHandler(this, in, out);
	}
	
	/**
	 * This method is used when the client must be shut down.
	 */
	// TO DO: EXCEPTION HANDLING
	public void shutDown() {
		sh.sendQuit();
		
//		if (game != null) {
//			
//		}
		
		sh.shutDown();
		tui.shutDown();		
		
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Starts a new client.
	 */
	// TO DO: EXCEPTION HANDLING
	public static void main(String[] args) throws Exception {
		(new Client()).start();
	}
}
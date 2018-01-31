package clientcontroller;

import general.*;
import netview.ClientTUI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * The file that is executed to start a client.
 * 
 * @author vera.prinsen
 */
public class Client {

	private Socket sock;
	private String clientName;
	private ClientTUI tui;
	
	private BufferedReader in;
	private BufferedWriter out;
	
	public Client() {
		this.tui = new ClientTUI();
	}
	
	// GETTERS & SETTERS =================================================================	
	public String getName() {
		return this.clientName;
	}
	
	// PRINTERS & SENDERS ========================================================
	public void print(String msg) {
		tui.print(msg);
	}
	
	public String readString(String prompt) {
		return tui.readString(prompt);
	}
	
	public int readInt(String prompt) {
		return tui.readInt(prompt);
	}
	
	// START UP AND SHUT DOWN OF THE CLIENT ===========================================
	/**
	 * These are the first few things that must be done when starting the client.
	 * 		Make connection to a server
	 * 		Create in- and output from and to the server
	 * 		Create serverHandler that processes this in- and output
	 */
	public void start() {		
		boolean ipOK = false;
		InetAddress addr = null;
		
		while (!ipOK) {
			String ipAddress = readString("Enter IP address of the Server host");
			
			if (ipAddress.equalsIgnoreCase(Protocol.Client.QUIT)) {
				print("Connection will not be made. Goodbye!");
				return;
			}
			
			try {
	            addr = InetAddress.getByName(ipAddress);
	            ipOK = true;
	        } catch (UnknownHostException e) {
	            print("ERROR: host " + ipAddress + " unknown.");
	        }
		}
		
		boolean portOK = false;
		int port = 0;
		
		while (!portOK) {
			try {
				port = readInt("To what portnumber you want to connect to?");
				
				if (port == -1) {
					print("Connection will not be made. Goodbye!");
					return;
				}
				
				print("");
				print("Trying to connect to the server.");
				sock = new Socket(addr, port);
				print("Connected to server.");
				print("");
				portOK = true;
			} catch (IOException e) {
				print("There is nothing to connect to on port " + port + ". Try another port.");
			}
		}
		
		// First: Ask for name
		clientName = readString("What is your name?");
		
		if (clientName.equals(Protocol.Client.QUIT)) {
			print("Goodbye!");
			try {
				sock.close();
			} catch (IOException e) {
				// Socket is probably already closed, do nothing..
			}
			return;
		}
		
		print("");
		
		print("-----------------------");
		print("|     o-o   o-o       |");
		print("|     o     o   o     |");
		print("|     |  -o |   |     |");
		print("|     o   | o   o     |");
		print("|      o-o   o-o      |");
		print("-----------------------");
		
		print("Welcome " + clientName + "!");
		
		try {
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		} catch (IOException e) {
			// When problem with socket, close the client
			shutDown();
		}
		
		new ServerHandler(this, in, out);
	}
	
	/**
	 * This method is used when the client must be shut down.
	 */
	public void shutDown() {
		try {
			sock.close();
			in.close();
			out.close();
		} catch (IOException e) {
			// Is already closing everything:
			// if there is a problem with closing, is is probably already closed.
		}
		
		print("");
		print("Goodbye!");
		System.exit(0);
	}
	
	/**
	 * Starts a new client.
	 */
	public static void main(String[] args) {
		(new Client()).start();
	}
}
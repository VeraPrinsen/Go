package clientController;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;

import general.Protocol;

/**
 * The ServerInputHandler handles all the input that the client gets from the server.
 * @author vera.prinsen
 *
 */
public class ServerInputHandler implements Runnable {
	
	private ServerHandler sh;
	private BufferedReader in;
	private Scanner inClient;
	private boolean isOpen;
	
	private Thread readThread;
	private String msg;
	
	public ServerInputHandler(ServerHandler sh, BufferedReader in) {
		this.sh = sh;
		this.in = in;
		inClient = new Scanner(System.in);
		isOpen = true;
	}
	
	/**
	 * This checks the input stream from the server to this particular client constantly.
	 */
	// TO DO: EXCEPTION HANDLING
	public void run() {
		String msg;
		try {
			while (isOpen && (msg = in.readLine()) != null) {
				if (readThread != null) {
					sh.print("Thread is open");
					readThread.interrupt();
				}
				sh.processServerInput(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// if in.readLine() == null (input stream has ended)
		System.out.println("Server has disconnected.");
				
		sh.client.shutDown();
	}
	
	/**
	 * Method reads a String from the input console.
	 */
	// TO DO: EXCEPTION HANDLING
	public String readString(String prompt) {
		readThread = new Thread(new Runnable() {
			public void run() {
				System.out.print(prompt + ": ");
				if (inClient.hasNext()) {
					msg = inClient.nextLine();
				}
				
			}
		});	
		
		readThread.start();
		try {
			readThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		readThread = null;
		
		if (msg.equalsIgnoreCase("exit")) {
			//client.shutDown();
			return Protocol.Client.QUIT;
		} else {
			return msg;
		}
	}
	
	/**
	 * Method reads an integer from the input console.
	 */
	public int readInt(String prompt) {
		boolean inputOK = false;
		int input = 0;

		while (!inputOK) {
			try {
				input = Integer.parseInt(readString(prompt));
				inputOK = true;
			} catch (NumberFormatException e) {
				sh.print("Input must be an integer.");
			}
		}

		return input;
	}
	
	public void shutDown() {
		isOpen = false;
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

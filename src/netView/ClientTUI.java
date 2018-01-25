package netView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import clientController.Client;
import general.Protocol;

/**
 * The ClientTUI takes care of the in- and output to and from the console.
 * 
 * @author vera.prinsen
 *
 */
public class ClientTUI implements Runnable {

	private Client client;
	private Scanner in;
	private boolean isOpen;
	private Thread readThread;
	private String msg;

	public ClientTUI(Client client) {
		this.client = client;
		in = new Scanner(System.in);
		isOpen = true;
	}

	// THIS CHECKS FOR INPUT FROM THE CLIENT ITSELF
	public void run() {
		String msg;
		try {
			while (isOpen && in.hasNext()) {
				msg = in.nextLine();
				if (msg.equalsIgnoreCase("exit")) {
					client.shutDown();
				} else {
					client.processClientInput(msg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void print(String msg) {
		System.out.println(msg);
	}

	public void shutDown() {
		isOpen = false;
		in.close();
	}

	/**
	 * Method reads a String from the input console.
	 */
	// TO DO: EXCEPTION HANDLING
	public String readString(String prompt) {
		readThread = new Thread(new Runnable() {
			public void run() {
				System.out.print(prompt + ": ");
				if (in.hasNext()) {
					msg = in.nextLine();
				}
				
			}
		});	
		
		readThread.start();
		try {
			readThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (msg.equalsIgnoreCase("exit")) {
			client.shutDown();
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
				print("Input must be an integer.");
			}
		}

		return input;
	}
}

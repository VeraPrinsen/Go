package serverclientconnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientTUI implements Runnable {

	private Client client;
	private BufferedReader in;
	private boolean isOpen;
	private Lock exitLock = new ReentrantLock();
	
	public ClientTUI(Client client) {
		this.client = client;
		in = new BufferedReader(new InputStreamReader(System.in));
		isOpen = true;
	}
	
	// THIS CHECKS FOR INPUT FROM THE CLIENT ITSELF
	public void run() {
		String msg;
		try {
			while (isOpen && (msg = in.readLine()) != null) {
				// exitLock.unlock();
				if (msg.equalsIgnoreCase("exit")) {
					client.shutDown();
				} else {
					client.processClientInput(msg);
				}
				// exitLock.lock();
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
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method reads a String from the input console.
	 */
	// TO DO: EXCEPTION HANDLING
	public String readString(String prompt) {
		// exitLock.unlock();
		String msg;
		try {
			System.out.print(prompt + ": ");
			if ((msg = in.readLine()).equalsIgnoreCase("exit")) {
				client.shutDown();
				return "";
			} else {
				return msg;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		} finally {
			// exitLock.lock();
		}
	}

	/**
	 * Method reads an integer from the input console.
	 */
	// TO DO: EXCEPTION HANDLING
	public int readInt(String prompt) {
		// exitLock.unlock();
		return Integer.parseInt(readString(prompt));
		// exitLock.lock();
	}
}

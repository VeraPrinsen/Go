package serverclientconnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientTUI {

	private Client client;
	private BufferedReader in;
	
	public ClientTUI(Client client) {
		this.client = client;
		in = new BufferedReader(new InputStreamReader(System.in));
	}
	
//	// THIS CHECKS FOR INPUT FROM THE CLIENT ITSELF
//	public void run() {
//		String msg;
//		try {
//			while (!(msg = in.readLine()).equals("exit")) {
//				client.processClientInput(msg);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	public void print(String msg) {
		System.out.println(msg);
	}
	
	/**
	 * Method reads a String from the input console.
	 */
	// TO DO: EXCEPTION HANDLING
	public String readString(String prompt) {
		try {
			System.out.print(prompt + ": ");
			return in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		
	}

	/**
	 * Method reads an integer from the input console.
	 */
	// TO DO: EXCEPTION HANDLING
	public int readInt(String prompt) {
		try {
			System.out.print(prompt + ": ");
			return Integer.parseInt(in.readLine());
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		
	}
}

package serverclientconnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;

public class ServerHandler {

	private Client client;
	private BufferedReader in;
	private BufferedWriter out;
	
	private ServerInputHandler serverInput;
	private ClientInput serverOutput;
	
	public ServerHandler(Client client, BufferedReader in, BufferedWriter out) {
		this.client = client;
		this.in = in;
		this.out = out;
		
		run();
	}
	
	// MAYBE THIS CAN BE PUT IN THE CONSTRUCTOR AND IT DOESN'T NEED TO BE A THREAD, BECAUSE OTHERWISE IT DOES NOTHING?????
	public void run() {
		serverInput = new ServerInputHandler(this, in);
		serverOutput = new ClientInput(this, out);
		
		Thread inputThread = new Thread(serverInput);
		Thread outputThread = new Thread(serverOutput);
		
		inputThread.start();
		outputThread.start();
	}
	
	// THIS IS WHAT HAPPENDS WITH THE INPUT FROM THE SERVER
	public void processServerInput(String msg) {
		System.out.println("Server input received.");
	}
	
	// THIS IS WHAT HAPPENDS TO THE INPUT FROM THE CLIENT (AND WHAT MUST BE SEND TO THE SERVER)
	public void processClientInput(String msg) throws Exception {
		if (msg.equalsIgnoreCase("exit")) {
			client.shutDown();
		} else {
			System.out.println("Client input received.");
			send(msg);
		}
	}
	
	public void send(String msg) throws Exception {
		out.write(msg + "\n");
		out.flush();
	}
}

package serverclientconnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class ClientHandler {
	
	private Server server;
	private BufferedReader in;
	private BufferedWriter out;
	
	private ClientInputHandler clientInput;
	private ServerToClient clientOutput;
	
	public ClientHandler(Server server, BufferedReader in, BufferedWriter out) {
		this.server = server;
		this.in = in;
		this.out = out;
		
		run();
	}
	
	// MAYBE THIS CAN BE PUT IN THE CONSTRUCTOR AND IT DOESN'T NEED TO BE A THREAD, BECAUSE OTHERWISE IT DOES NOTHING?????
	public void run() {
		clientInput = new ClientInputHandler(this, in);
		clientOutput = new ServerToClient(this, out);
		
		Thread inputThread = new Thread(clientInput);
		Thread outputThread = new Thread(clientOutput);
		
		inputThread.start();
		outputThread.start();
	}
	
	// THIS IS WHAT INPUT IS GOTTEN FROM THE CLIENT (MOVE, ACCEPT, REQUESTGAME etc.)
	public void processClientInput(String msg) {
		System.out.println("Client input received.");
	}
	
	// THIS IS WHAT IS SEND TO THIS SPECIFIC CLIENT
	public void send(String msg) {
		try {
			out.write("Server: " + msg);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}


}

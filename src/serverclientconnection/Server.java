package serverclientconnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	
	private ServerSocket ssock;
	private List<ClientHandler> clients;
	ServerTUI tui;
	
	public Server() {
		clients = new ArrayList<>();
		tui = new ServerTUI(this);
	}
	
	public static void main(String[] args) throws Exception {
		(new Server()).start();
	}
	
	public void start() throws Exception {	
		boolean portOK = false;
		int port;
		
		while (!portOK) {
			port = tui.getPort();
			
			System.out.println("Trying to connect a server.");
			ssock = new ServerSocket(port);
			System.out.println("Server connected.");
			portOK = true;
		}
		
		Thread tuiThread = new Thread(tui);
		tuiThread.start();
		
		while (true) {
			Socket sock = ssock.accept();
			BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			ClientHandler ch = new ClientHandler(this, in, out);
			clients.add(ch);	
		}
	}
	
	// THIS IS WHAT HAPPENDS WHEN THE SERVER ITSELF SAYS SOMETHING (LIKE EXIT)
	// for now, print it on the server console and on the console of all the clients
	public void processServerInput(String msg) {
		broadcast(msg);
	}
	
	// THIS IS TO PUT IS ONLY ON THE CONSOLE OF THE SERVER
	public void print(String msg) {
		tui.print(msg);
	}
	
	// THIS IS WHAT IS SEND TO ALL THE CLIENTS IN THE CLIENTS LIST
	public void broadcast(String msg) {
		for (ClientHandler ch : clients) {
			ch.send(msg + "\n");
		}
	}
}

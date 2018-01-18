package serverclientconnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {

	private Socket sock;
	
	public Client() {
		
	}
	
	public static void main(String[] args) throws Exception {
		(new Client()).start();
	}
	
	public void start() throws Exception {
		System.out.println("Trying to connect to the server.");
		sock = new Socket("localhost", 4567);
		System.out.println("Connected to server.");
		
		BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		
		ServerHandler sh = new ServerHandler(this, in, out);
	}
	
	public void print(String msg) {
		System.out.println(msg);
	}
	
	public void shutDown() throws Exception {
		sock.close();
	}
}
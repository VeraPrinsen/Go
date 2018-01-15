package server;

import java.io.*;
import java.net.*;

public class ClientHandler {

	private Socket clientSock;
	private ServerSocket serverSock;
	private BufferedReader in;
	private BufferedWriter out;
	
	public ClientHandler(ServerSocket ssock, Socket sock) throws IOException {
		this.clientSock = sock;
		this.serverSock = ssock;
		
		in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(clientSock.getOutputStream()));
	}
	
	// 
}

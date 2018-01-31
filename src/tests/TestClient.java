package tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import general.Extensions;
import general.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TestClient {

	private ServerSocket ssock;
	private Socket sock;
	private BufferedReader in;
	// If test was more extensive, this outputStream must also be used.
	//private BufferedWriter out;
	
	@Before
	public void setUp() throws IOException {
		ssock = new ServerSocket(6666);
		sock = ssock.accept();
		
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		//out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
	}

	@Test
	public void initTest() throws IOException {
		String input = in.readLine();
		String[] args = input.split("\\" + Protocol.General.DELIMITER1);
		
		assertEquals(args[0], Protocol.Client.NAME);
		assertEquals(args[1], "Vera");
		assertEquals(args[2], Protocol.Client.VERSION);
		assertEquals(Integer.parseInt(args[3]), Protocol.Client.VERSIONNO);
		assertEquals(args[4], Protocol.Client.EXTENSIONS);
		assertEquals(Integer.parseInt(args[5]), Extensions.chat);
		assertEquals(Integer.parseInt(args[6]), Extensions.challenge);
		assertEquals(Integer.parseInt(args[7]), Extensions.leaderboard);
		assertEquals(Integer.parseInt(args[8]), Extensions.security);
		assertEquals(Integer.parseInt(args[9]), Extensions.multiplayer);
		assertEquals(Integer.parseInt(args[10]), Extensions.simultaneous);
		assertEquals(Integer.parseInt(args[11]), Extensions.multimoves);
	}
	
	
	
}

package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import general.Extensions;
import general.Protocol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class TestServer {

	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	
	@Before
	public void setUp() throws IOException {
		sock = new Socket("localhost", 6666);
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
	}

	@Test
	public void initTest() throws IOException {
		String input = in.readLine();
		String[] args = input.split("\\" + Protocol.General.DELIMITER1);
		
		assertEquals(args[0], Protocol.Client.NAME);
		assertEquals(args[1], "ServerVera");
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

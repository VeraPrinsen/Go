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

	private Socket sock1;
	private Socket sock2;
	private BufferedReader in1;
	private BufferedWriter out1;
	private BufferedReader in2;
	private BufferedWriter out2;
	
	@Before
	public void setUp() throws IOException {
		// Make two dummy Clients
		sock1 = new Socket("localhost", 6666);
		sock2 = new Socket("localhost", 6666);
		in1 = new BufferedReader(new InputStreamReader(sock1.getInputStream()));
		out1 = new BufferedWriter(new OutputStreamWriter(sock1.getOutputStream()));
		in2 = new BufferedReader(new InputStreamReader(sock2.getInputStream()));
		out2 = new BufferedWriter(new OutputStreamWriter(sock2.getOutputStream()));
	}

	@Test
	public void testGoodInputGame() throws IOException, InterruptedException {
		String name1 = "Piet";
		String name2 = "Henk";
		
		// The first command the client should get from the server is the NAME command
		String input1 = in1.readLine();
		String[] args = input1.split("\\" + Protocol.General.DELIMITER1);
		
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
		
		// Same for the second client, not checking that, but clearing the input stream
		String input2 = in2.readLine();
		
		// Before anything can be send to the server the NAME command should be send to the server
		String message1 = Protocol.Client.NAME + Protocol.General.DELIMITER1 + name1
		+ Protocol.General.DELIMITER1 + "VERSION" + Protocol.General.DELIMITER1 + Protocol.Client.VERSIONNO
		+ Protocol.General.DELIMITER1 + Protocol.Client.EXTENSIONS + Protocol.General.DELIMITER1
		+ Extensions.chat + Protocol.General.DELIMITER1 + Extensions.challenge + Protocol.General.DELIMITER1
		+ Extensions.leaderboard + Protocol.General.DELIMITER1 + Extensions.security
		+ Protocol.General.DELIMITER1 + Extensions.multiplayer + Protocol.General.DELIMITER1
		+ Extensions.simultaneous + Protocol.General.DELIMITER1 + Extensions.multimoves;
		out1.write(message1 + Protocol.General.COMMAND_END);
		out1.flush();
		
		Thread.sleep(1000);
		
		// For both clients
		String message2 = Protocol.Client.NAME + Protocol.General.DELIMITER1 + name2
				+ Protocol.General.DELIMITER1 + "VERSION" + Protocol.General.DELIMITER1 + Protocol.Client.VERSIONNO
				+ Protocol.General.DELIMITER1 + Protocol.Client.EXTENSIONS + Protocol.General.DELIMITER1
				+ Extensions.chat + Protocol.General.DELIMITER1 + Extensions.challenge + Protocol.General.DELIMITER1
				+ Extensions.leaderboard + Protocol.General.DELIMITER1 + Extensions.security
				+ Protocol.General.DELIMITER1 + Extensions.multiplayer + Protocol.General.DELIMITER1
				+ Extensions.simultaneous + Protocol.General.DELIMITER1 + Extensions.multimoves;
		out2.write(message2 + Protocol.General.COMMAND_END);
		out2.flush();
		
		Thread.sleep(1000);
		
		// Both clients will request a (default) 2 player game.
		out1.write(Protocol.Client.REQUESTGAME + Protocol.General.DELIMITER1 + 2 + Protocol.General.COMMAND_END);
		out1.flush();
		
		Thread.sleep(1000);
				
		out2.write(Protocol.Client.REQUESTGAME + Protocol.General.DELIMITER1 + 2 + Protocol.General.COMMAND_END);
		out2.flush();
	
		// In response the first client to request this (client1) will receive a START command which will ask for the settings of the game.
		assertEquals("START$2", in1.readLine());
		
		Thread.sleep(1000);
		
		// Client1 will send their SETTINGS to the server.
		out1.write(Protocol.Client.SETTINGS + Protocol.General.DELIMITER1 + Protocol.General.BLACK + Protocol.General.DELIMITER1 + 19 + Protocol.General.COMMAND_END);
		out1.flush();
		
		Thread.sleep(1000);
		
		// And the clients expect another START command from the server
		assertEquals("START$2$" + Protocol.General.BLACK + "$19$" + name1 + "$" + name2, in1.readLine());
		
		Thread.sleep(1000);
		
		assertEquals("START$2$" + Protocol.General.WHITE + "$19$" + name1 + "$" + name2, in2.readLine());
		
		Thread.sleep(1000);
		
		// And the BLACK player can expect the TURN FIRST command
		assertEquals("TURN$" + name1 + "$FIRST$" + name1, in1.readLine());
		
		Thread.sleep(1000);
		
		// Client1 will send a move to the server
		out1.write("MOVE$3_2" + Protocol.General.COMMAND_END);
		out1.flush();
		
		Thread.sleep(1000);
		
		// Both Clients will receive a confirmation that the move is valid
		assertEquals("TURN$" + name1 + "$3_2$" + name2, in1.readLine());
				
		Thread.sleep(1000);
				
		assertEquals("TURN$" + name1 + "$3_2$" + name2, in2.readLine());
		
		Thread.sleep(1000);
		
		// Client2 will send a move to the server
		out2.write("MOVE$6_6" + Protocol.General.COMMAND_END);
		out2.flush();

		Thread.sleep(1000);

		// Both Clients will receive a confirmation that the move is valid
		assertEquals("TURN$" + name2 + "$6_6$" + name1, in1.readLine());

		Thread.sleep(1000);

		assertEquals("TURN$" + name2 + "$6_6$" + name1, in2.readLine());

		Thread.sleep(1000);
		
		// Client1 will send a move to the server
		out1.write("MOVE$8_8" + Protocol.General.COMMAND_END);
		out1.flush();

		Thread.sleep(1000);

		// Both Clients will receive a confirmation that the move is valid
		assertEquals("TURN$" + name1 + "$8_8$" + name2, in1.readLine());

		Thread.sleep(1000);

		assertEquals("TURN$" + name1 + "$8_8$" + name2, in2.readLine());

		Thread.sleep(1000);
				
		// Client2 will pass
		out2.write("MOVE$PASS" + Protocol.General.COMMAND_END);
		out2.flush();
		
		// Both Clients will receive a confirmation that the pass is valid
		assertEquals("TURN$" + name2 + "$PASS$" + name1, in1.readLine());
						
		Thread.sleep(1000);
						
		assertEquals("TURN$" + name2 + "$PASS$" + name1, in2.readLine());
		
		// Client1 will pass (which will then cause the game to be finished)
		out1.write("MOVE$PASS" + Protocol.General.COMMAND_END);
		out1.flush();

		// Both Clients will receive a confirmation that the pass is valid
		assertEquals("TURN$" + name1 + "$PASS$" + name2, in1.readLine());

		Thread.sleep(1000);

		assertEquals("TURN$" + name1 + "$PASS$" + name2, in2.readLine());

		Thread.sleep(1000);
		
		// Both Clients will receive an ENDGAME command. Piet scored 2 points, Henk scored 1 point.
		assertEquals("ENDGAME$FINISHED$" + name1 + "$2$" + name2 +"$1", in1.readLine());

		Thread.sleep(1000);

		assertEquals("ENDGAME$FINISHED$" + name1 + "$2$" + name2 +"$1", in2.readLine());
		
	}
	
	@Test
	public void testInvalidMove() throws IOException, InterruptedException {
		String name1 = "Anna";
		String name2 = "Tim";
		
		// The first command the client should get from the server is the NAME command
		String input1 = in1.readLine();
		String[] args = input1.split("\\" + Protocol.General.DELIMITER1);
		
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
		
		// Same for the second client, not checking that, but clearing the input stream
		String input2 = in2.readLine();
		
		// Before anything can be send to the server the NAME command should be send to the server
		String message1 = Protocol.Client.NAME + Protocol.General.DELIMITER1 + name1
		+ Protocol.General.DELIMITER1 + "VERSION" + Protocol.General.DELIMITER1 + Protocol.Client.VERSIONNO
		+ Protocol.General.DELIMITER1 + Protocol.Client.EXTENSIONS + Protocol.General.DELIMITER1
		+ Extensions.chat + Protocol.General.DELIMITER1 + Extensions.challenge + Protocol.General.DELIMITER1
		+ Extensions.leaderboard + Protocol.General.DELIMITER1 + Extensions.security
		+ Protocol.General.DELIMITER1 + Extensions.multiplayer + Protocol.General.DELIMITER1
		+ Extensions.simultaneous + Protocol.General.DELIMITER1 + Extensions.multimoves;
		out1.write(message1 + Protocol.General.COMMAND_END);
		out1.flush();
		
		Thread.sleep(1000);
		
		// For both clients
		String message2 = Protocol.Client.NAME + Protocol.General.DELIMITER1 + name2
				+ Protocol.General.DELIMITER1 + "VERSION" + Protocol.General.DELIMITER1 + Protocol.Client.VERSIONNO
				+ Protocol.General.DELIMITER1 + Protocol.Client.EXTENSIONS + Protocol.General.DELIMITER1
				+ Extensions.chat + Protocol.General.DELIMITER1 + Extensions.challenge + Protocol.General.DELIMITER1
				+ Extensions.leaderboard + Protocol.General.DELIMITER1 + Extensions.security
				+ Protocol.General.DELIMITER1 + Extensions.multiplayer + Protocol.General.DELIMITER1
				+ Extensions.simultaneous + Protocol.General.DELIMITER1 + Extensions.multimoves;
		out2.write(message2 + Protocol.General.COMMAND_END);
		out2.flush();
		
		Thread.sleep(1000);
		
		// Both clients will request a (default) 2 player game.
		out1.write(Protocol.Client.REQUESTGAME + Protocol.General.DELIMITER1 + 2 + Protocol.General.COMMAND_END);
		out1.flush();
		
		Thread.sleep(1000);
				
		out2.write(Protocol.Client.REQUESTGAME + Protocol.General.DELIMITER1 + 2 + Protocol.General.COMMAND_END);
		out2.flush();
	
		// In response the first client to request this (client1) will receive a START command which will ask for the settings of the game.
		assertEquals("START$2", in1.readLine());
		
		Thread.sleep(1000);
		
		// Client1 will send their SETTINGS to the server.
		out1.write(Protocol.Client.SETTINGS + Protocol.General.DELIMITER1 + Protocol.General.BLACK + Protocol.General.DELIMITER1 + 19 + Protocol.General.COMMAND_END);
		out1.flush();
		
		Thread.sleep(1000);
		
		// And the clients expect another START command from the server
		assertEquals("START$2$" + Protocol.General.BLACK + "$19$" + name1 + "$" + name2, in1.readLine());
		
		Thread.sleep(1000);
		
		assertEquals("START$2$" + Protocol.General.WHITE + "$19$" + name1 + "$" + name2, in2.readLine());
		
		Thread.sleep(1000);
		
		// And the BLACK player can expect the TURN FIRST command
		assertEquals("TURN$" + name1 + "$FIRST$" + name1, in1.readLine());
		
		Thread.sleep(1000);
		
		// Client1 will send a move to the server
		out1.write("MOVE$3_2" + Protocol.General.COMMAND_END);
		out1.flush();
		
		Thread.sleep(1000);
		
		// Both Clients will receive a confirmation that the move is valid
		assertEquals("TURN$" + name1 + "$3_2$" + name2, in1.readLine());
				
		Thread.sleep(1000);
				
		assertEquals("TURN$" + name1 + "$3_2$" + name2, in2.readLine());
		
		Thread.sleep(1000);
		
		// ====================================================================================================
		// Client2 will send a non-valid move to the server
		out2.write("MOVE$60_6" + Protocol.General.COMMAND_END);
		out2.flush();

		Thread.sleep(1000);

		// Client2 will receive an INVALIDMOVE error
		assertTrue(in2.readLine().contains("ERROR$INVALIDMOVE$"));
		// ====================================================================================================
		
		Thread.sleep(1000);
		
		// Client2 will send a move to the server
		out2.write("MOVE$6_6" + Protocol.General.COMMAND_END);
		out2.flush();

		Thread.sleep(1000);

		// Both Clients will receive a confirmation that the move is valid
		assertEquals("TURN$" + name2 + "$6_6$" + name1, in1.readLine());

		Thread.sleep(1000);

		assertEquals("TURN$" + name2 + "$6_6$" + name1, in2.readLine());

		Thread.sleep(1000);
		
		// Client1 will send a move to the server
		out1.write("MOVE$8_8" + Protocol.General.COMMAND_END);
		out1.flush();

		Thread.sleep(1000);

		// Both Clients will receive a confirmation that the move is valid
		assertEquals("TURN$" + name1 + "$8_8$" + name2, in1.readLine());

		Thread.sleep(1000);

		assertEquals("TURN$" + name1 + "$8_8$" + name2, in2.readLine());

		Thread.sleep(1000);
				
		// Client2 will pass
		out2.write("MOVE$PASS" + Protocol.General.COMMAND_END);
		out2.flush();
		
		// Both Clients will receive a confirmation that the pass is valid
		assertEquals("TURN$" + name2 + "$PASS$" + name1, in1.readLine());
						
		Thread.sleep(1000);
						
		assertEquals("TURN$" + name2 + "$PASS$" + name1, in2.readLine());
		
		// Client1 will pass (which will then cause the game to be finished)
		out1.write("MOVE$PASS" + Protocol.General.COMMAND_END);
		out1.flush();

		// Both Clients will receive a confirmation that the pass is valid
		assertEquals("TURN$" + name1 + "$PASS$" + name2, in1.readLine());

		Thread.sleep(1000);

		assertEquals("TURN$" + name1 + "$PASS$" + name2, in2.readLine());

		Thread.sleep(1000);
		
		// Both Clients will receive an ENDGAME command. Piet scored 2 points, Henk scored 1 point.
		assertEquals("ENDGAME$FINISHED$" + name1 + "$2$" + name2 + "$1", in1.readLine());

		Thread.sleep(1000);

		assertEquals("ENDGAME$FINISHED$" + name1 + "$2$" + name2 + "$1", in2.readLine());
		
	}
	
}

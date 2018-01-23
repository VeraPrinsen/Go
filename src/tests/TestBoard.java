package tests;

import boardView.*;
import model.Board;
import model.Token;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class TestBoard {

	private Board board;
	private int DIM;
	
	@Before
	public void setUp() {
		DIM = 9;
		board = new Board(DIM, false);
	}

	@Test
	public void initTest() {
		assertEquals(board.getDIM(), DIM);
		
		for (int i = 0; i < DIM; i++) {
			for (int j = 0; j < DIM; j++) {
				assertEquals(board.getField(i, j), Token.EMPTY);
			}
		}
		
		// There should only be one group, of all empty tokens
		assertTrue(board.getGroups().size() == 1);
		assertEquals(board.getGroups().get(0).getToken(), Token.EMPTY);
	}
	
	@Test
	public void testGroup() {
		board.setField(0, 0, Token.BLACK);
		board.setField(0, 1, Token.BLACK);
		board.setField(1, 1, Token.BLACK);
		board.setField(2, 1, Token.BLACK);
		board.setField(2, 2, Token.BLACK);
		board.setField(3, 1, Token.BLACK);
		board.setField(3, 2, Token.BLACK);
		
		// Test if the right fields are filled in
		assertEquals(board.getField(0, 0), Token.BLACK);
		assertEquals(board.getField(0, 1), Token.BLACK);
		assertEquals(board.getField(0, 2), Token.EMPTY);
		assertEquals(board.getField(1, 0), Token.EMPTY);
		assertEquals(board.getField(1, 1), Token.BLACK);
		assertEquals(board.getField(1, 2), Token.EMPTY);
		assertEquals(board.getField(2, 0), Token.EMPTY);
		assertEquals(board.getField(2, 1), Token.BLACK);
		assertEquals(board.getField(2, 2), Token.BLACK);
		assertEquals(board.getField(3, 0), Token.EMPTY);
		assertEquals(board.getField(3, 1), Token.BLACK);
		assertEquals(board.getField(3, 2), Token.BLACK);
		
		// There should only be two groups, one empty, one black
		assertTrue(board.getGroups().size() == 2);
		if (board.getGroups().get(0).getToken() == Token.EMPTY) {
			assertEquals(board.getGroups().get(1).getToken(), Token.BLACK);
		} else if (board.getGroups().get(0).getToken() == Token.BLACK) {
			assertEquals(board.getGroups().get(1).getToken(), Token.EMPTY);
		}
	}
	
	

}

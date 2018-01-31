package tests;

import model.Board;
import model.Token;

import org.junit.Before;
import org.junit.Test;

import boardview.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestBoard {

	private GOGUI gui;
	private Board board;
	private int dim;
	
	@Before
	public void setUp() {
		dim = 9;
		gui = new GoGUIIntegrator(false, false, dim);
	}

	@Test
	public void initTest() {
		boolean useGUI = false;
		if (useGUI) {
			board = new Board(dim, gui);
		} else {
			board = new Board(dim);
		}
		
		assertEquals(board.getDIM(), dim);
		
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				assertEquals(board.getField(i, j), Token.EMPTY);
			}
		}
		
		// There should only be one group, of all empty tokens
		assertTrue(board.getGroups().size() == 1);
		assertEquals(board.getGroups().get(0).getToken(), Token.EMPTY);
	}
	
	@Test
	public void testBoardCopy() {
		
	}
	
	@Test
	public void testSettersRandom() {
		boolean useGUI = false;
		if (useGUI) {
			board = new Board(dim, gui);
		} else {
			board = new Board(dim);
		}
		
		board.setField(0, 1, Token.BLACK);
		board.setField(1, 0, Token.BLACK);
		board.setField(1, 1, Token.BLACK);
		board.setField(2, 8, Token.WHITE);
		board.setField(7, 3, Token.EMPTY);
		board.setField(5, 5, Token.BLACK);
		board.setField(4, 6, Token.WHITE);
		board.setField(8, 1, Token.WHITE);
		board.setField(2, 5, Token.EMPTY);
		board.setField(1, 1, Token.BLACK);
		board.setField(7, 7, Token.WHITE);
		board.setField(6, 8, Token.EMPTY);
		board.setField(3, 5, Token.BLACK);
		board.setField(3, 2, Token.WHITE);
		
		if (useGUI) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		// Test if the right fields are filled in
		assertEquals(board.getField(0, 1), Token.BLACK);
		assertEquals(board.getField(1, 0), Token.BLACK);
		assertEquals(board.getField(1, 1), Token.BLACK);
		assertEquals(board.getField(2, 8), Token.WHITE);
		assertEquals(board.getField(7, 3), Token.EMPTY);
		assertEquals(board.getField(5, 5), Token.BLACK);
		assertEquals(board.getField(4, 6), Token.WHITE);
		assertEquals(board.getField(8, 1), Token.WHITE);
		assertEquals(board.getField(2, 5), Token.EMPTY);
		assertEquals(board.getField(1, 1), Token.BLACK);
		assertEquals(board.getField(7, 7), Token.WHITE);
		assertEquals(board.getField(6, 8), Token.EMPTY);
		assertEquals(board.getField(3, 5), Token.BLACK);
		assertEquals(board.getField(3, 2), Token.WHITE);
		
		// There should only be 10 groups, one empty, one black
		assertTrue(board.getGroups().size() == 10);
		
		// How to check the groups?? Because you do not know which group is which??
//		if (board.getGroups().get(0).getToken() == Token.EMPTY) {
//			assertEquals(board.getGroups().get(1).getToken(), Token.BLACK);
//		} else if (board.getGroups().get(0).getToken() == Token.BLACK) {
//			assertEquals(board.getGroups().get(1).getToken(), Token.EMPTY);
//		}
	}
	
	@Test
	public void testSettersGroup() {
		boolean useGUI = false;
		if (useGUI) {
			board = new Board(dim, gui);
		} else {
			board = new Board(dim);
		}
		
		board.setField(0, 0, Token.BLACK);
		board.setField(0, 1, Token.BLACK);
		board.setField(1, 1, Token.BLACK);
		board.setField(2, 1, Token.BLACK);
		board.setField(2, 2, Token.BLACK);
		board.setField(3, 1, Token.BLACK);
		board.setField(3, 2, Token.BLACK);
		
		if (useGUI) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
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
	
	@Test
	public void testGroupCapture() {
		boolean useGUI = false;
		if (useGUI) {
			board = new Board(dim, gui);
		} else {
			board = new Board(dim);
		}
		
		board.setField(0, 0, Token.BLACK);
		board.setField(0, 1, Token.BLACK);
		board.setField(1, 1, Token.BLACK);
		board.setField(2, 1, Token.BLACK);
		board.setField(2, 2, Token.BLACK);
		board.setField(3, 1, Token.BLACK);
		board.setField(3, 2, Token.BLACK);
		
		if (useGUI) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		board.setField(0, 0, Token.BLACK);
		board.setField(0, 1, Token.BLACK);
		board.setField(1, 1, Token.BLACK);
		board.setField(2, 1, Token.BLACK);
		board.setField(2, 2, Token.BLACK);
		board.setField(3, 1, Token.BLACK);
		board.setField(3, 2, Token.BLACK);
		
		if (useGUI) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void testCheckMove() {
		
	}
	
	@Test
	public void testBoardsEqual() {
		
	}
	
}

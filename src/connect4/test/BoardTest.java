package connect4.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import connect4.Board;
import connect4.Cell;
import connect4.Player;
import connect4.Position;
import connect4.SimpleBoard;

public class BoardTest {

	private Player player1;
	private Player player2;
	private Board board;
	
	@Before
	public void setupBoard() {
		board = new SimpleBoard();
		player1 = new Player();
		player2 = new Player();
	}
	
	@Test
	public void testEmptyBoardOnStart() {
		Cell[][] matrix = board.getMatrix();
		
		for (int i = 0; i < SimpleBoard.ROWS; i++) {
			for (int j = 0; j < SimpleBoard.COLS; j++) {
				assertNull(matrix[i][j]);
			}
		}
	}
	
	@Test
	public void testCanPlaceCoin() {
		assertTrue(board.canPlace(4));
		
		// Fill column
		for (int i = 0; i < SimpleBoard.COLS; i++) {
			board.place(player1, 4);
		}
		
		assertFalse(board.canPlace(4));
	}
	
	@Test
	public void testPlaceCoin() {
		board.place(player1, 0);
		
		// Cell in row 0, column 0 should not be null
		assertNotNull(board.getMatrix()[0][0]);
		
		Cell cell = board.getMatrix()[0][0];
		assertEquals(cell.getPlayer(), player1);
		
		assertEquals(new Position(0,0), cell.getPosition());
	}
	
	@Test
	public void testGetLastPosition() {
		board.place(player1, 4);
		board.place(player1, 4);
		
		assertEquals(new Position(1, 4), board.getLastPosition());
	}
	
	@Test
	public void testForHorizontalWin() {
		for (int i = 0; i < 4; i++) {
			board.place(player1, i);
		}
		
		Player winner = board.check(board.getLastPosition());
		
		assertNotNull(winner);
		assertEquals(winner, player1);
	}
	
	@Test
	public void testForVerticalWin() {
		for (int i = 0; i < 4; i++) {
			board.place(player2, 0);
		}
		
		Player winner = board.check(board.getLastPosition());
		
		assertNotNull(winner);
		assertEquals(winner, player2);
	}
	
	
	@Test
	public void testForDiagonalWin1() {
		/**
		 * Placing coin like below:
		 * 
		 * 				A
		 *			A 	B
		 * 		A	B	B
		 * 	A	B	B	B
		 * 
		 */
		
		// Placing B's
		board.place(player2, 1);
		board.place(player2, 2);
		board.place(player2, 2);
		board.place(player2, 3);
		board.place(player2, 3);
		board.place(player2, 3);
		
		// Placing A's
		board.place(player1, 0);
		board.place(player1, 1);
		board.place(player1, 2);
		board.place(player1, 3);
		
		Player winner = board.check(board.getLastPosition());
		assertEquals(winner, player1);
	}
	
	@Test
	public void testForDiagonalWin2() {
		/**
		 * Placing coin like below:
		 *
		 * 	A
		 * 	B	A	
		 * 	B	B	A
		 * 	B	B	B	A
		 * 
		 */
		
		// Placing B's
		board.place(player2, 0);
		board.place(player2, 0);
		board.place(player2, 0);
		board.place(player2, 1);
		board.place(player2, 1);
		board.place(player2, 2);
		
		// Placing A's
		board.place(player1, 0);
		board.place(player1, 1);
		board.place(player1, 2);
		board.place(player1, 3);
		
		Player winner = board.check(board.getLastPosition());
		assertEquals(winner, player1);
	}

}

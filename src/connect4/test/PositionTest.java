package connect4.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import connect4.Position;

/**
 * Unit test for the position class.
 * 
 * @author Daniel, Pierre, Mazen og Jacob
 */
public class PositionTest {

	@Test
	public void test() {
		Position pos = new Position(4, 7);
		
		assertEquals(4, pos.getRow());
		assertEquals(7, pos.getCol());
	}
	
	@Test
	public void testEquals() {
		assertEquals(new Position(1,1), new Position(1,1));
	}

}

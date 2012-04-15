package connect4.test;

import static org.junit.Assert.*;

import org.junit.Test;

import connect4.Player;

public class PlayerTest {

	@Test
	public void testSettersAndGetters() {
		Player player = new Player();
		
		player.setName("Joe");
		assertEquals("Joe", player.getName());
	}
	
	@Test
	public void testConstructor() {
		Player player = new Player("Jim");
		assertEquals("Jim", player.getName());
		
		player = new Player();
		assertEquals("", player.getName());
	}

}

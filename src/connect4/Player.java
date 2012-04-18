package connect4;

/**
 * Simple player class for easy sub classing.
 * A GUI interface implementation would extend this class and add
 * information for example about color or sprite.
 * 
 * @author Daniel, Pierre, Mazen og Jacob
 */
public class Player {
	
	/** The players name */
	private String name;

	/**
	 * Empty constructor
	 */
	public Player() {
		name = "";
	}
	
	/**
	 * @param n The player name
	 */
	public Player(String n) {
		name = n;
	}
	
	/**
	 * @param n The player name
	 */
	public void setName(String n) {
		name = n;
	}
	
	/** 
	 * @return The player name
	 */
	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}
}

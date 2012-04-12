package connect4;

/**
 * Simple player class for easy sub classing.
 * A GUI interface implementation would extend this class and add
 * information for example about color or sprite.
 *  
 * @author Jacob Pedersen
 *
 */
public class Player {
	
	/** The players name */
	private String name;
	
	public Player() {
		name = "";
	}
	
	public Player(String n) {
		name = n;
	}
	
	public void setName(String n) {
		name = n;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}
}

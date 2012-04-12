package connect4;

public class Player {
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

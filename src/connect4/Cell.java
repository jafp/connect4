package connect4;

public class Cell {
	private Player player;
	private Position position;
	
	public Cell(Player pl, Position po) {
		player = pl;
		position = po;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Position getPosition() {
		return position;
	}
}

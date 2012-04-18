package connect4;

/**
 * A cell in the board
 * 
 * @author Daniel, Pierre, Mazen og Jacob
 */
public class Cell {
	/** The player that placed this cell */
	private Player player;
	/** The position of this cell */
	private Position position;
	
	/**
	 * Constructor 
	 * @param pl Player
	 * @param po Position
	 */
	public Cell(Player pl, Position po) {
		player = pl;
		position = po;
	}
	
	/**
	 * @return Player
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * @return Position
	 */
	public Position getPosition() {
		return position;
	}
}

package connect4;

/**
 * Board interface.
 * Defines the operations that a board can do.
 * 
 * @author Daniel, Pierre, Mazen og Jacob
 */
public interface Board {
	/**
	 * 
	 * @return The contents of the board
	 */
	Cell[][] getMatrix();
	
	/**
	 * 
	 * @param col The column index
	 * @return True if a coin can be placed in the given column
	 */
	boolean canPlace(int col);
	
	/**
	 * @return True if the board is full, or false
	 */
	boolean isFull();
	
	/**
	 * Places a coin in the given cell
	 * 
	 * @param player The player that places the coin
	 * @param col The column index
	 * @return The position of the placed coin
	 */
	Position place(Player player, int col);
	
	/**
	 * Check if a player has won
	 * 
	 * @return The winner, or null
	 */
	Player check();
	
	/**
	 * @return The position of the latest placed coin
	 */
	Position getLastPosition();
	
}

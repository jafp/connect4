package connect4;

/**
 * A position on the board.
 * 
 * @author Daniel, Pierre, Mazen og Jacob
 */
public class Position {
	
	/** The row index */
	private int row;
	/** The column index */
	private int col;
	
	/**
	 * Constructor
	 * 
	 * @param r Row index
	 * @param c Column index
	 */
	public Position(int r, int c) {
		row = r;
		col = c;
	}
	
	/**
	 * @return Row index
	 */
	public int getRow() {
		return row;
	}
	
	/**
	 * @return Column index
	 */
	public int getCol() {
		return col;
	}

	/**
	 * @return True if the position of the other object are the same 
	 */
	@Override
	public boolean equals(Object obj) {
		return ( ((Position) obj).row == row && ((Position) obj).col == col );
	}
}

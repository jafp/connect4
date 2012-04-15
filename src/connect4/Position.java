package connect4;

public class Position {
	private int row;
	private int col;
	
	public Position(int r, int c) {
		row = r;
		col = c;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}

	@Override
	public boolean equals(Object obj) {
		return ( ((Position) obj).row == row && ((Position) obj).col == col );
	}
}

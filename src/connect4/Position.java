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
	
	public String toString() {
		return "(" + row + ", " + col + ")";
	}
}

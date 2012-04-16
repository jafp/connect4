package connect4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * My implementation of the board.
 * 
 * @author Jacob Pedersen 
 * 
 */
public class SimpleBoard implements Board {

	public static final int ROWS = 6;
	public static final int COLS = 7;
	
	private Position lastPosition;
	private final Cell[][] matrix = new Cell[ROWS][COLS];
	
	/**
	 * @return The matrix of cells
	 */
	public Cell[][] getMatrix() {
		return matrix;
	}

	/**
	 * Checks if we can place a coin in the given column.
	 * 
	 * The check is performed by looking at the topmost cell in the column.
	 * If that cell is empty (null), there is room for at least one more coin
	 * in the column.
	 * 
	 * @param col The column index
	 * @return True if we can place a coin in the given column 
	 */
	public boolean canPlace(int col) {
		return matrix[5][col] == null;
	}

	/**
	 * Places a coin from the given player in the given column.
	 * 
	 * @param player The player
	 * @param col The column
	 * @return The position of the newly placed cell, or null if it's unavailable.
	 */
	public Position place(Player player, int col) {
		if (canPlace(col)) {
			lastPosition = getFirstEmptyCell(col);
			matrix[lastPosition.getRow()][lastPosition.getCol()] = new Cell(player, lastPosition);
			return lastPosition;
		}
		return null;
	}

	/**
	 * @param pos The last placed cell's position
	 * @return Player The winner, or null if nobody has won
	 */
	public Player check(Position pos) {
		Cell cell = getCell(pos, 0, 0);
		
		if (checkHorizontal(cell) || checkVertical(cell) || checkDiagonalBottomLeftTopRight(cell) || checkDiagonalBottomRightTopLeft(cell)) {
			return cell.getPlayer();
		}
		return null;
	}
	
	/**
	 * @return The position of the last placed coin.
	 */
	public Position getLastPosition() {
		return lastPosition;
	}
	
	public Player loadFromFile(File file, Player a, Player b) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			String next = reader.readLine();
			String name = next.split(" ")[1];
			
			next = reader.readLine();
			while (next != null) {
				String parts[] = next.split(" ");
				
				Position pos = new Position(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
				Player player = parts[2].equals(a.getName()) ? a : b;
				
				Cell cell = new Cell(player, pos);
				matrix[pos.getRow()][pos.getCol()] = cell;
				
				next = reader.readLine();
			}
			
			reader.close();
			
			return name.equals(a.getName()) ? a : b;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void persistToFile(File file, Player next) {
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(file));
			
			writer.println("NEXT " + next.getName());
			
			for (int i = 0; i < ROWS; i++) {
				for (int j = 0; j < COLS; j++) {
					Cell cell = matrix[i][j];
					
					if (cell != null) {
						Position pos = cell.getPosition();
						writer.println(pos.getRow() + " " + pos.getCol() +  " " + cell.getPlayer().getName());
					}
				}
			}
			
			writer.flush();
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Iterate from bottom up in the given column, and return the 
	 * position or null.
	 * 
	 * @param col The column index
	 * @return The position of the first empty cell in the given column
	 */
	private Position getFirstEmptyCell(int col) {
		for (int i = 0; i < 7; i++) {
			if (matrix[i][col] == null) {
				return new Position(i, col);
			}
		}
		return null;
	}
	
	/**
	 * Helper method for finding cell from a position and an offset
	 * 
	 * @param pos Original position
	 * @param offsetRow Offset row
	 * @param offsetCol Offset column
	 * @return Cell at the new position
	 */
	private Cell getCell(Position pos, int offsetRow, int offsetCol) {
		return matrix[pos.getRow() + offsetRow][pos.getCol() + offsetCol];
	}
	
	/**
	 * Checks for four horizontal connected cells
	 * 
	 * @param origin The origin cell
	 * @return True if found, or false
	 */
	private boolean checkHorizontal(Cell origin) {
		int count = 1;
		Cell next = null;
		Position pos = origin.getPosition();
		
		// Count coins to the left
		if (pos.getCol() > 0) {
			// First cell to the left
			next = getCell(pos, 0, -1);
			
			// Continue left until the cell is empty or placed by another player
			while (next != null && next.getPlayer() == origin.getPlayer())
			{
				count += 1;
				next = next.getPosition().getCol() > 0 ? getCell(next.getPosition(), 0, -1) : null;
			}
		}
		
		// Count coins to the right
		if (pos.getCol() < 6) {
			next = getCell(pos, 0, 1);
			
			// Continue right until the cell is empty or placed by another player
			while (next != null && next.getPlayer() == origin.getPlayer())
			{
				count += 1;
				next = next.getPosition().getCol() < 6 ? getCell(next.getPosition(), 0, 1) : null;
			}
		}
		
		if (count == 4) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Check for four vertical connected coins
	 * 
	 * @param origin The last cell
	 * @return True if we found four connected coins
	 */
	private boolean checkVertical(Cell origin) {
		int count = 1;
		Position pos = origin.getPosition();
		
		//
		// Only check downwards
		//
		
		if (pos.getRow() > 0) {
			Cell next = getCell(pos, -1, 0);
			
			while (next != null && next.getPlayer() == origin.getPlayer()) {
				count += 1;
				next = next.getPosition().getRow() > 0 ? getCell(next.getPosition(), -1, 0) : null;
			}
		}
		
		if (count == 4) {
			return true;
		}
	
		return false;
	}
	
	/**
	 * Check for four connected coins going from bottom left to top right
	 * 
	 * @param origin The first cell
	 * @return True or false
	 */
	private boolean checkDiagonalBottomLeftTopRight(Cell origin) {
		int count = 1;
		Position pos = origin.getPosition();
		
		// Count left-down
		//
		//
		//	   /
		//	  /
		//	 <
		//
		if (pos.getCol() > 0 && pos.getRow() > 0) {
			Cell next = getCell(pos, -1, -1);
			
			while (next != null && next.getPlayer() == origin.getPlayer()) {
				count += 1;
				//
				//	"next" must be at least at x's position
				//
				//	|	|	|	|	|
				//	-----------------
				//	|	| x	|	|	|
				//	-----------------
				//	|	|	|	|	|
				//	-----------------
				//
				next = (next.getPosition().getCol() > 0 && next.getPosition().getRow() > 0) ? getCell(next.getPosition(), -1, -1) : null;
			}
		}
		
		// Count right-up
		//
		//    >
		//	 /
		//	/
		//
		if (pos.getCol() < 6 && pos.getRow() < 5) {
			Cell next = getCell(pos, 1, 1);
			
			while (next != null && next.getPlayer() == origin.getPlayer()) {
				count += 1;
				next = (next.getPosition().getCol() < 6 && next.getPosition().getRow() < 5) ? getCell(next.getPosition(), 1, 1) : null;
			}
		}
		
		if (count == 4) {
			return true;
		}
		return false;
	}
	
	/**
	 * Check for four connected coins in a diagonal going from bottom right to top left
	 * 
	 * @param origin The last cell
	 * @return True or false
	 */
	private boolean checkDiagonalBottomRightTopLeft(Cell origin) {
		int count = 1;
		Position pos = origin.getPosition();
		
		if (pos.getCol() < 6  && pos.getRow() > 0) {
			Cell next = getCell(pos, -1, 1);
			
			while (next != null && next.getPlayer() == origin.getPlayer()) {
				count += 1;
				next = (next.getPosition().getCol() < 6  && next.getPosition().getRow() > 0) ? getCell(next.getPosition(), -1, 1) : null;
			}
		}
		
		if (pos.getCol() > 0 && pos.getRow() < 5) {
			Cell next = getCell(pos, 1, -1);
			
			while (next != null && next.getPlayer() == origin.getPlayer()) {
				count += 1;
				next = (next.getPosition().getCol() > 0 && next.getPosition().getRow() < 5) ? getCell(next.getPosition(), 1, -1) : null;
			}
		}
		
		if (count == 4) {
			return true;
		}
		return false;
	}
	
}

package connect4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * The class encapsulates the persistence functionality.
 */
public class Persistence {
	
	private Board board;
	private Player player1;
	private Player player2;
	
	public Persistence(Player p1, Player p2, Board b) {
		player1 = p1;
		player2 = p2;
		board = b;
	}
	
	/**
	 * Loads board and current player from the given file.
	 * 
	 * @param file The board file
	 * @return The current player, or null if the load fails
	 */
	public Player load(String gameName) {
		try {
			File file = new File(getFileName(gameName));
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			// Skip first line (game name)
			reader.readLine();
			
			// Name of current player
			String next = reader.readLine();
			
			next = reader.readLine();
			while (next != null) {
				String parts[] = next.split(" ");
				
				Position pos = new Position(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
				Player player = player1.getName().equals(parts[2]) ? player1 : player2;
				
				Cell cell = new Cell(player, pos);
				board.getMatrix()[pos.getRow()][pos.getCol()] = cell;
				
				next = reader.readLine();
			}
			
			reader.close();
			
			return player1.getName().equals(next) ? player1 : player2;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Stores the current game state to a file.
	 * 
	 * The format of the file is like
	 * 
	 * TEST			- name of game
	 * X			- current player
	 * 0 0 X		- coin placed by X at 0,0
	 * 0 1 O		- coin placed by O as 0,1
	 * 
	 */
	public void persist(String name, Player current) {
		try {
			File file = new File(getFileName(name));
			
			// Make sure file exists
			if (!file.exists()) {
				file.createNewFile();
			}
			
			// Create writer - false means NO APPEND
			PrintWriter writer = new PrintWriter(new FileWriter(file, false));
			
			// Line 1: Name of the name
			writer.println(name);
			
			// Line 2: Name of current player
			writer.println(current.getName());
			
			// Line 3-?: Board data
			for (int i = 0; i < SimpleBoard.ROWS; i++) {
				for (int j = 0; j < SimpleBoard.COLS; j++) {
					Cell cell = board.getMatrix()[i][j];
					
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
	 * Removes the file for the game with the given name
	 * 
	 * @param gameName Name of the game
	 */
	public void remove(String gameName) {
		new File(getFileName(gameName)).delete();
	}
	
	/**
	 * @return Names of saved games
	 */
	public List<String> getSavedGames() {
		List<String> games = new ArrayList<String>();
		
		try {
			File dir = new File(".");
			String[] children = dir.list();
			
			for (String filename : children) {
				if (filename.startsWith("board.")) {
					/** 
					 * Opens a reader and reads the first line, which should be the name of the game
					 */
					BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
					games.add(reader.readLine());
					reader.close();
				}
				
			}
		}
		catch (Exception e) { }
		
		return games;
	}
	
	/**
	 * @param gameName The name of the game
	 * @return The name of the file with data about the game with the given name
	 */
	private String getFileName(String gameName) {
		return "board." + gameName.hashCode() + ".txt";
	}
}

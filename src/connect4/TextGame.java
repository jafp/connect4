package connect4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Implementation of game with text based interface
 * 
 * @author Daniel, Pierre, Mazen og Jacob
 */
public class TextGame implements Game {
	
	private Board board;
	private Player player1;
	private Player player2;
	private Player current;
	
	/** Name of the loaded game */
	private String loadedGame;

	public TextGame() {
		board = new SimpleBoard();
		player1 = new Player("X");
		player2 = new Player("O");
		loadedGame = null;
	}

	/**
	 * Play!
	 */
	@Override
	public void play() {
		Scanner input = new Scanner(System.in);
		Player winner = null;
		List<String> savedGames = getSavedGames();
		
		/**
		 * Menu
		 */
		System.out.println("Welcome to Connect4!");
		System.out.println("1) Start new game");
		if (savedGames.size() > 0) {
			System.out.println("2) Load saved game");
		}
		
		System.out.println("Enter your choice: ");
		String choice = input.nextLine();
		
		/**
		 * Handle choice
		 */
		if ("2".equalsIgnoreCase(choice.trim())) {
			
			System.out.println("Saved games:");
			
			for (int i = 0; i < savedGames.size(); i++) {
				System.out.println("  " + i + ": " + savedGames.get(i));
			}
			
			int gameIndex = -1;
			while (gameIndex == -1) {
				System.out.println("Enter number of the game you want to load: ");
				gameIndex = input.nextInt();
				
				// Read newline character
				input.nextLine();	
				
				if (gameIndex < 0 || gameIndex >= savedGames.size()) {
					gameIndex = -1;
					System.out.println("Not a valid game number!");
				}
			}
			
			String gameName = savedGames.get(gameIndex);
			loadFromFile(gameName);
		
		} 
		else {
			current = player1;
		}		
		
		/**
		 * Continue until a winner is found, or the board is full (draw)
		 */
		while (winner == null && !board.isFull()) {
			printBoard();
			
			System.out.println("Is it now " + current.getName());
			
			int colIndex = -1;
			while (colIndex == -1) {
				System.out.println("Enter column number (1-7) (SAVE or QUIT): ");
				String colString = input.nextLine();
				try {
					/**
					 * Save board and current player to file when
					 * user enter "SAVE"
					 */
					if ("SAVE".equalsIgnoreCase(colString)) {
						
						if (loadedGame != null) {
							persistToFile(loadedGame);
						} else {
							System.out.println("Enter name of the game: ");
							String name = input.nextLine();
							persistToFile(name);
						}
						
						System.out.println("Board saved, goodbye!");
						System.exit(0);
					}
					else if ("QUIT".equalsIgnoreCase(colString)) {
						System.out.println("Goodbye!");
						System.exit(0);
					}
					else {
					
						colIndex = Integer.parseInt(colString);
						colIndex = colIndex - 1;
						
						if (!board.canPlace(colIndex)) {
							colIndex = -1;
							System.out.println("You cannot place a coin there...");
						}
					}
					
				} catch (Exception e) { 
					colIndex = -1;
					System.out.println("Not a valid column, try again.");
				}
			}
			
			board.place(current, colIndex);
			winner = board.check();
			
			current = current == player1 ? player2 : player1;
		}
		
		printBoard();
		
		if (loadedGame != null) {
			removeGameFile(loadedGame);
		}
		
		if (winner == null && board.isFull()) {
			System.out.println("IT'S A DRAW!");
		}
		else {
			System.out.println("\nWINNER IS: " + winner + " !");
		}
	}
	
	/**
	 * Loads board and current player from the given file.
	 * 
	 * @param file The board file
	 */
	private void loadFromFile(String gameName) {
		try {
			File file = new File(getFileName(gameName));
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			// Skip first line (game name)
			reader.readLine();
			
			String next = reader.readLine();
			current = next.equals("X") ? player1 : player2;
			
			next = reader.readLine();
			while (next != null) {
				String parts[] = next.split(" ");
				
				Position pos = new Position(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
				Player player = parts[2].equals("X") ? player1 : player2;
				
				Cell cell = new Cell(player, pos);
				board.getMatrix()[pos.getRow()][pos.getCol()] = cell;
				
				next = reader.readLine();
			}
			
			reader.close();
			loadedGame = gameName;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return Names of saved games
	 */
	private List<String> getSavedGames() {
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
	private void persistToFile(String name) {
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
	private void removeGameFile(String gameName) {
		new File(getFileName(gameName)).delete();
	}
	
	/**
	 * @param gameName The name of the game
	 * @return The name of the file with data about the game with the given name
	 */
	public String getFileName(String gameName) {
		return "board." + gameName.hashCode() + ".txt";
	}
	
	
	/**
	 * Prints the board
	 */
	private void printBoard() {
		System.out.println("\n----------------------------");
		for (int i = 5; i >= 0; i--) {
			System.out.print("|");
			for (int j = 0; j <= 6; j++) {
				Cell cell = board.getMatrix()[i][j];
				if (cell != null) {
					System.out.print(" " + cell.getPlayer().getName() + " |");
				} else {
					System.out.print("   |");
				}
			}
			System.out.println("\n----------------------------");
		}
	}

	public static void main(String[] args) {
		Game game = new TextGame();
		game.play();
	}
}

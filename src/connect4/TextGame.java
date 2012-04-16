package connect4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Implementation of game with text based interface
 * 
 * @author Jacob Pedersen
 *
 */
public class TextGame implements Game {
	
	private Board board;
	private Player player1;
	private Player player2;
	private Player current;
	
	private File file;
	
	public TextGame() {
		board = new SimpleBoard();
		player1 = new Player("X");
		player2 = new Player("0");
		
		file = new File("board.txt");
	}

	/**
	 * Play!
	 */
	@Override
	public void play() {
		Scanner input = new Scanner(System.in);
		Player winner = null;
		
		current = player1;
		
		if (file.exists()) {
			loadFromFile();
		}
		
		while (winner == null && !board.isFull()) {
			printBoard();
			
			System.out.println("Is it now " + current.getName());
			
			int colIndex = -1;
			while (colIndex == -1) {
				System.out.println("Enter column number (1-7) or SAVE to save game: ");
				String colString = input.next();
				try {
					/**
					 * Save board and current player to file when
					 * user enter "SAVE"
					 */
					if ("SAVE".equalsIgnoreCase(colString)) {
						persistToFile();
						System.out.println("Board saved, goodbye!");
						System.exit(0);
					}
					
					colIndex = Integer.parseInt(colString);
					colIndex = colIndex - 1;
					
					if (!board.canPlace(colIndex)) {
						colIndex = -1;
						System.out.println("You cannot place a coin there...");
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
		file.delete();
		
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
	private void loadFromFile() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
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
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Stores the current game state to a file.
	 */
	private void persistToFile() {
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(file));
			
			writer.println(current.getName());
			
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

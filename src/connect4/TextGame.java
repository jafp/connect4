package connect4;

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
	private Persistence persistence;
	
	/** Name of the loaded game */
	private String loadedGame;

	public TextGame() {
		board = new SimpleBoard();
		player1 = new Player("X");
		player2 = new Player("O");
		persistence = new Persistence(player1, player2, board);
		loadedGame = null;
	}

	/**
	 * Play!
	 */
	@Override
	public void play() {
		Scanner input = new Scanner(System.in);
		Player winner = null;
		List<String> savedGames = persistence.getSavedGames();
		
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
			persistence.load(gameName);
		
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
							persistence.persist(loadedGame, current);
						} else {
							System.out.println("Enter name of the game: ");
							String name = input.nextLine();
							persistence.persist(name, current);
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
			persistence.remove(loadedGame);
		}
		
		if (winner == null && board.isFull()) {
			System.out.println("IT'S A DRAW!");
		}
		else {
			System.out.println("\nWINNER IS: " + winner + " !");
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

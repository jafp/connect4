package connect4;

import java.util.Scanner;

/**
 * Implementation of game with text based interface
 * 
 * @author Jacob Pedersen
 *
 */
public class TextGame extends Game {
	
	public TextGame() {
		board = new SimpleBoard();
		player1 = new Player("A");
		player2 = new Player("B");
	}

	/**
	 * Play!
	 */
	@Override
	public void play() {
		Scanner input = new Scanner(System.in);
		Player winner = null, current = player1;
		
		while (winner == null) {
			printBoard();
			
			System.out.println("Is it now " + current.getName());
			
			int colIndex = -1;
			
			while (colIndex == -1) {
				System.out.println("Enter column number (1-7): ");
				String colString = input.next();
				try {
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
			
			Position last = board.place(current, colIndex);
			winner = board.check(last);
			
			current = current == player1 ? player2 : player1;
		}
		
		System.out.println("\nJubiii!!! WINNER IS " + winner + " !!! ");
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

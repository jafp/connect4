package connect4;

public interface Board {
	Cell[][] getMatrix();
	boolean canPlace(int col);
	Position place(Player player, int col);
	Player check(Position lastCell);
}

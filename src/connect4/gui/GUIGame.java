package connect4.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import connect4.Cell;
import connect4.Game;
import connect4.SimpleBoard;
import connect4.Sound;

/**
 * The GUI implementation of our Connect 4 game
 * @author Andre, Mazen, Pierre & Jacob
 */
@SuppressWarnings("serial")
public class GUIGame extends JFrame implements Game {

	/** Width of the window */
	public static int CANVAS_WIDTH = 640;
	/** Height of the window */
	public static int CANVAS_HEIGHT = 630;
	/** Coin width and height */
	public static int COIN_WIDTH = 80;
	public static int COIN_HEIGHT = 80;
	
	/** Game playing state */
	public static enum State { 
		PLAYING, 
		WINNER,
		DRAW,
		RESTART
	};
	
	/** States while playing */
	public static enum PlayState {
		WAITING_FOR_PLAYER, 
		PREPARE_ANIMATION,
		ANIMATION,
	};
	
	private SimpleBoard board;
	private GUIPlayer player1;
	private GUIPlayer player2;
	private GUIPlayer winner;
	private GUIPlayer current;
	private Canvas canvas;
	private State state;
	private PlayState playState;
	private int nextCoinColumn;
	private int queueColumn;
	private double droppingCoinX, droppingCoinY, droppingCoinDeltaY, droppingCoinBottom, droppingCoinEnergyLoss;
	
	/**
	 * Constructor.
	 */
	public GUIGame() {
		
		initialize();
		
		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
		setContentPane(canvas);
		setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setTitle("Connect4 (C) 2012 André, Mazen, Pierre & Jacob");
		pack();
		setVisible(true);
		
		start();
	}
	
	/**
	 * Initialize the game. 
	 * Create the board and players, and give them colors and names.
	 */
	public void initialize() {
		board = new SimpleBoard();
		player1 = new GUIPlayer("X", Color.BLUE);
		player2 = new GUIPlayer("O", Color.GREEN);
		nextCoinColumn = 0;
		current = player1;
	}
	
	/**
	 * Start the thread that runs the game loop.
	 */
	public void start() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				loop();
			}
		});
		
		thread.start();
	}
	
	/**
	 * The main game loop.
	 */
	public void loop() {		
		while (true) {
			repaint();
			
			try {
				Thread.sleep( 10 );
			} catch (InterruptedException e) {}
		}
	}
	
	/**
	 * This is where all the magic happens.
	 */
	public void draw(Graphics2D g) {
		// Force the use of anti-aliasing and good render quality
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    
	    // Draw the background white
	    g.setColor(Color.white);
	    g.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
		
	    // The overall game state
		switch (state) {
			case PLAYING: {
		
				// The playing state.
				// This is only used to implement the animation stuff.
				switch (playState) {
					
					// While waiting for the current player to drop a coin
					case WAITING_FOR_PLAYER: {
						
						drawNextCoin(g);
						drawBoard(g);
						drawGrid(g);
						
						break;
					}
					
					// While the player has released a coin, we prepare for the animation
					case PREPARE_ANIMATION: {
						droppingCoinBottom = getBottomOfColumn(queueColumn);
						droppingCoinX = getPositionAtCanvas(SimpleBoard.ROWS, queueColumn).getX();
						droppingCoinY = 0;
						droppingCoinDeltaY = 5;
						droppingCoinEnergyLoss = 0.2;
				
						drawNextCoin(g);
						drawBoard(g);
						drawGrid(g);
						
						playState = PlayState.ANIMATION;
						break;
					}
					
					// While the coin travels down through the rows.
					case ANIMATION: {
						
						droppingCoinDeltaY += 0.1;
						droppingCoinY += droppingCoinDeltaY;
						
						if (droppingCoinY > (droppingCoinBottom - COIN_HEIGHT)) {
							droppingCoinY = droppingCoinBottom - COIN_HEIGHT;
							
							droppingCoinDeltaY *= -1;
							droppingCoinDeltaY *= droppingCoinEnergyLoss;
						}
						
						if (droppingCoinY >= (droppingCoinBottom - COIN_HEIGHT) && droppingCoinDeltaY > -0.012) {
							// The coin is now standing still
							// Place the coin on the board and return to waiting state
							
							board.place(current, queueColumn);
							winner = (GUIPlayer) board.check();
							current = current == player1 ? player2 : player1;
							
							if (winner != null) {
								state = State.WINNER;
								Sound.play("sound/winner.wav");
							} else if (board.isFull()) {
								state = State.DRAW;
								Sound.play("sound/draw.wav");
							}
							else {
								playState = PlayState.WAITING_FOR_PLAYER;
							}
						}
					
						g.setColor(current.getColor());
						g.fillRect((int) droppingCoinX, (int) droppingCoinY, COIN_WIDTH, COIN_HEIGHT);

						drawBoard(g);
						drawGrid(g);
						
						break;
					}
				}
				break;
			}
			// When a winner is found. 
			case WINNER: {
				if (winner != null) {
					g.setColor(winner.getColor());
					g.setFont(new Font("Verdana", Font.ITALIC, 30));
					g.drawString("GAMEOVER! " + ((winner == player1) ? "Blue" : "Green") + " has won!", 110, 200);
					
					g.setFont(new Font("Verdana", Font.ITALIC, 20));
					g.drawString("Click to restart game!", 210, 270);
				}
				break;
			}
			// When the game ends in a draw
			case DRAW: {
				g.setColor(Color.DARK_GRAY);
				g.setFont(new Font("Verdana", Font.ITALIC, 30));
				g.drawString("IT'S A DRAW!", 220, 200);
				break;
			}
			// When the player choose to restart the game
			case RESTART: {
				board = new SimpleBoard();
				current = player1;
				state = State.PLAYING;
				playState = PlayState.WAITING_FOR_PLAYER;
			}
		}		
		
	}
	
	/**
	 * @return The background of the grid
	 */
	private Area getGridClip() {
		Area notToDrawIn = new Area(new Rectangle2D.Double(0, 80, CANVAS_WIDTH, CANVAS_HEIGHT));
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 6; j++) {
				Rectangle2D bum = new Rectangle2D.Double(i * 90 + 10, j * 90 + 80 + 10, 80, 80); 
				notToDrawIn.subtract(new Area(bum));
			}
		}
		return notToDrawIn;
	}
	
	/**
	 * Draws the grid
	 * @param g The graphics object
	 */
	private void drawGrid(Graphics2D g) {
		g.setColor(Color.DARK_GRAY);
		g.setClip(getGridClip());
		g.fillRect(0, 80, CANVAS_WIDTH, CANVAS_HEIGHT);
	}
	
	/**
	 * Draws all coins placed on the board
	 * @param g The graphics object
	 */
	private void drawBoard(Graphics2D g) {
		for (int i = 0; i < SimpleBoard.ROWS; i++) {
			for (int j = 0; j < SimpleBoard.COLS; j++) {
				
				Cell c = board.getMatrix()[i][j];
				if (c != null) {
					GUIPlayer p = (GUIPlayer) c.getPlayer();
					Point point = getPositionAtCanvas(i, j);
					g.setColor(p.getColor());
					g.fillRect((int) point.getX(), (int) point.getY(), COIN_WIDTH, COIN_HEIGHT);
					
				}
			}
		}
	}
	
	/**
	 * Draw the "next coin" in the top of the canvas.
	 * @param g The graphics object
	 */
	private void drawNextCoin(Graphics2D g) {
		Point nextCoinPos = getPositionAtCanvas(SimpleBoard.ROWS, nextCoinColumn);
		g.setColor( board.canPlace(nextCoinColumn) ? current.getColor() : Color.red);
		g.fillRect((int) nextCoinPos.getX(), (int) nextCoinPos.getY(), COIN_WIDTH, COIN_HEIGHT);
	}
	
	/**
	 * Play! 
	 * This method implements the play method in the Game interface.
	 */
	@Override
	public void play() {
		state = State.PLAYING;
		playState = PlayState.WAITING_FOR_PLAYER;
	}
	
	/**
	 * @param col A column index
	 * @return The y-position of the bottom of the given column
	 */
	private int getBottomOfColumn(int col) {
		for (int i = 0; i < SimpleBoard.ROWS; i++) {
			if (board.getMatrix()[i][col] == null) {
				Point p = getPositionAtCanvas(i, col);
				return ((int) p.getY()) + COIN_HEIGHT;
			}
		}
		return -1;
	}
	
	/**
	 * 
	 * @param row A row index
	 * @param col A column index 
	 * @return The position (x,y) on the canvas
	 */
	private Point getPositionAtCanvas(int row, int col) {
		return new Point(col * 90 + 10, (CANVAS_HEIGHT - 90) - (row * 90));
	}
	
	/**
	 * A JPanel class that encapsulates the "canvas" we paint on.
	 * This class also serves as a listener for mouse clicks and mouse movements. 
	 */
	public class Canvas extends JPanel implements MouseMotionListener, MouseListener {
		
		public Canvas() {
			addMouseMotionListener(this);
			addMouseListener(this);
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			draw((Graphics2D) g); 
		}

		@Override
		public void mouseDragged(MouseEvent e) { }

		/**
		 * Resolves the column of the "next coin" when the
		 * mouse is moved
		 */
		@Override
		public void mouseMoved(MouseEvent e) {
			nextCoinColumn = e.getX() / 90;
		}

		/**
		 * Called when the mouse is clicked
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			// If we are playing
			if (state == State.PLAYING) {
				if (playState == PlayState.WAITING_FOR_PLAYER) {
					// If we can place, play the click sound and prepare the animation.
					if (board.canPlace(nextCoinColumn)) {
						
						// Play the click sound
						Sound.play("sound/click.wav");
						
						playState = PlayState.PREPARE_ANIMATION;
						queueColumn = nextCoinColumn;
					} else {
						// Else the column must be full, play the full sound.
						Sound.play("sound/full.wav");
					}
				}
			}
			// If a player clicked in the WINNER state, then restart the game
			else if (state == State.WINNER) {
				state = State.RESTART;
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) { }

		@Override
		public void mouseExited(MouseEvent e) { }

		@Override
		public void mousePressed(MouseEvent e) { }

		@Override
		public void mouseReleased(MouseEvent e) { }
	}
	
	public static void main(String[] args) {
		Game game = new GUIGame();
		game.play();
	}

}

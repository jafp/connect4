package connect4.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import connect4.Cell;
import connect4.Game;
import connect4.SimpleBoard;

@SuppressWarnings("serial")
public class GUIGame extends JFrame implements Game {

	public static int CANVAS_WIDHT = 560;
	public static int CANVAS_HEIGHT = 560;
	public static int COIN_WIDTH = 80;
	public static int COIN_HEIGHT = 80;
	
	public static enum State { 
		PLAYING, 
		WINNER,
		DRAW
	};
	
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
	
	public GUIGame() {
		
		initialize();
		
		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(CANVAS_WIDHT, CANVAS_HEIGHT));
		
		setContentPane(canvas);
		setSize(CANVAS_WIDHT, CANVAS_HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		pack();
		setVisible(true);
		
		start();
	}
	
	public void initialize() {
		board = new SimpleBoard();
		player1 = new GUIPlayer("X", Color.BLUE);
		player2 = new GUIPlayer("O", Color.GREEN);
		nextCoinColumn = 0;
		current = player1;
	}
	
	public void start() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				loop();
			}
		});
		
		thread.start();
	}
	
	public void loop() {		
		while (true) {

			update();
			repaint();
			
			try {
				Thread.sleep( 10 );
			} catch (InterruptedException e) {}
		}
	}
	
	public void update() {
	}
	
	public void draw(Graphics2D g) {

		switch (state) {
			case PLAYING: {
				
				switch (playState) {
					case WAITING_FOR_PLAYER: {
				
						drawNextCoin(g);
						drawBoard(g);
						
						break;
					}
					case PREPARE_ANIMATION: {
						droppingCoinBottom = getBottomOfColumn(queueColumn);
						droppingCoinX = getPositionAtCanvas(SimpleBoard.ROWS, queueColumn).getX();
						droppingCoinY = 0;
						droppingCoinDeltaY = 6;
						droppingCoinEnergyLoss = 0.2;
				
						drawNextCoin(g);
						drawBoard(g);
						
						playState = PlayState.ANIMATION;
						break;
					}
					case ANIMATION: {
						
						droppingCoinDeltaY += 0.09;
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
							} 
							else {
								playState = PlayState.WAITING_FOR_PLAYER;
							}
						}
						
						g.setColor(current.getColor());
						g.fill3DRect((int) droppingCoinX, (int) droppingCoinY, COIN_WIDTH, COIN_HEIGHT, true);
						
						drawBoard(g);
						
						break;
					}
				}
				break;
			}
			case WINNER: {
				if (winner != null) {
					g.setFont(new Font("Verdana", Font.ITALIC, 30));
					g.drawString("GAMEOVER! " + winner.getName() + " has won!", 100, 200);
				}
				break;
			}
			case DRAW: {
				g.setFont(new Font("Verdana", Font.ITALIC, 30));
				g.drawString("IT'S A DRAW!", 180, 200);
			}
		}
	}
	
	private void drawBoard(Graphics2D g) {
		for (int i = 0; i < SimpleBoard.ROWS; i++) {
			for (int j = 0; j < SimpleBoard.COLS; j++) {
				
				Cell c = board.getMatrix()[i][j];
				if (c != null) {
					GUIPlayer p = (GUIPlayer) c.getPlayer();
					Point point = getPositionAtCanvas(i, j);
					g.setColor(p.getColor());
					g.fill3DRect((int) point.getX(), (int) point.getY(), COIN_WIDTH, COIN_HEIGHT, true);
				}
			}
		}
	}
	
	private void drawNextCoin(Graphics2D g) {
		Point nextCoinPos = getPositionAtCanvas(SimpleBoard.ROWS, nextCoinColumn);
		g.setColor( board.canPlace(nextCoinColumn) ? current.getColor() : Color.red);
		g.fill3DRect((int) nextCoinPos.getX(), (int) nextCoinPos.getY(), COIN_WIDTH, COIN_HEIGHT, true);
	}
	
	@Override
	public void play() {
		state = State.PLAYING;
		playState = PlayState.WAITING_FOR_PLAYER;
	}
	
	private int getBottomOfColumn(int col) {
		for (int i = 0; i < SimpleBoard.ROWS; i++) {
			if (board.getMatrix()[i][col] == null) {
				Point p = getPositionAtCanvas(i, col);
				return ((int) p.getY()) + COIN_HEIGHT;
			}
		}
		return -1;
	}
	
	private Point getPositionAtCanvas(int row, int col) {
		return new Point(col * COIN_WIDTH, (CANVAS_HEIGHT - COIN_WIDTH) - (row * COIN_WIDTH));
	}
	
	/**
	 * The game canvas
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

		@Override
		public void mouseMoved(MouseEvent e) {
			double x = e.getX();
			if (x >= 0 && x <= 79) {
				nextCoinColumn = 0;
			} 
			else if (x >= 80 && x <= 159) {
				nextCoinColumn = 1;
			}
			else if (x >= 160 && x <= 239) {
				nextCoinColumn = 2;
			}
			else if (x >= 240 && x <= 319) {
				nextCoinColumn = 3;
			}
			else if (x >= 320 && x <= 399) {
				nextCoinColumn = 4;
			}
			else if (x >= 400 && x <= 479) {
				nextCoinColumn = 5;
			} 
			else if (x >= 480 && x <= 560) {
				nextCoinColumn = 6;
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) { 
			if (board.canPlace(nextCoinColumn)) {
				playState = PlayState.PREPARE_ANIMATION;
				queueColumn = nextCoinColumn;
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

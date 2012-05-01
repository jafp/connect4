package connect4.gui;

import java.awt.Color;

import connect4.Player;

public class GUIPlayer extends Player {
	
	private Color color;
	
	public GUIPlayer(String n, Color c) {
		super(n);
		color = c;
	}
	
	public Color getColor() {
		return color;
	}
}

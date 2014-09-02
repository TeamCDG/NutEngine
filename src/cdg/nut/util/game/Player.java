package cdg.nut.util.game;

import cdg.nut.util.gl.GLColor;

public class Player {

	private GLColor playerColor;
	private String name;
	
	public Player(GLColor col, String name)
	{
		this.playerColor = col;
		this.name = name;
	}
	
	
	public GLColor getPlayerColor() {
		return playerColor;
	}
	public void setPlayerColor(GLColor playerColor) {
		this.playerColor = playerColor;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}

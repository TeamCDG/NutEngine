package cdg.nut.util.game.fish;

import cdg.nut.util.game.Entity2D;
import cdg.nut.util.game.Player;

public class Bulding extends FishGameEntity {

	private int tileWidth;
	private int tileHeight;
	
	private int tileXMove;
	private int tileYMove;
	
	public Bulding(float x, float y, float width, float height, Player p) {
		super(x, y, width, height, p);


		this.tileWidth = Math.round(width * 10.0f);
		this.tileHeight = Math.round(height * 10.0f);
		
		this.setTileXMove(this.tileWidth/2);
		this.setTileYMove(this.tileHeight/2);
	}

	/**
	 * @return the tileWidth
	 */
	public int getTileWidth() {
		return tileWidth;
	}

	/**
	 * @return the tileHeight
	 */
	public int getTileHeight() {
		return tileHeight;
	}

	public int getTileXMove() {
		return tileXMove;
	}

	public void setTileXMove(int tileXMove) {
		this.tileXMove = tileXMove;
	}

	public int getTileYMove() {
		return tileYMove;
	}

	public void setTileYMove(int tileYMove) {
		this.tileYMove = tileYMove;
	}
	

}

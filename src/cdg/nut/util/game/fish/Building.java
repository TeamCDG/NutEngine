package cdg.nut.util.game.fish;

import cdg.nut.interfaces.IEntity;
import cdg.nut.util.game.Entity2D;
import cdg.nut.util.game.Player;

public class Building extends FishGameEntity {

	private int tileWidth;
	private int tileHeight;
	
	private int tileXMove;
	private int tileYMove;
	
	private boolean built = false;
	private int buildPoints = 0;
	private int maxBuildPoints = 100;
	
	
	public Building(float x, float y, float width, float height, int owner) {
		super(x, y, width, height, owner);


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

	/**
	 * @return the built
	 */
	public boolean isBuilt() {
		return built;
	}


	public int getTileYMove() {
		return tileYMove;
	}

	/**
	 * @return the buildPoints
	 */
	protected int getBuildPoints() {
		return buildPoints;
	}

	/**
	 * @return the maxBuildPoints
	 */
	protected int getMaxBuildPoints() {
		return maxBuildPoints;
	}

	public void setTileYMove(int tileYMove) {
		this.tileYMove = tileYMove;
	}
	
	public void onBuildHit(IEntity hitter)
	{
		if(!this.built)
			this.buildPoints++;
		if(this.buildPoints >= this.maxBuildPoints)
			this.built = true;
	}
	
	@Override 
	protected void passUniforms()
	{
		super.passUniforms();
		
		if(this.getOwner()!=null)
		{
			if(this.built)
				this.getShader().passColor("playerColor", this.getOwner().getPlayerColor());
			else
				this.getShader().passColor("playerColor", this.getColor());
		}
	}
}

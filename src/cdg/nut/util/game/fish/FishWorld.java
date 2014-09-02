package cdg.nut.util.game.fish;

import cdg.nut.util.game.Grid;
import cdg.nut.util.game.World;

public class FishWorld extends World {

	public FishWorld(int width, int height) {
		super(width, height);
		// TODO Auto-generated constructor stub
	}


	@Override
	public Grid initGrid(int width, int height)
	{
		return new FishGrid(width, height, this);
	}

}

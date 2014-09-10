package cdg.nut.util.game.fish;

import cdg.nut.util.game.Grid;
import cdg.nut.util.game.Tile;
import cdg.nut.util.game.World;
import cdg.nut.util.gl.GLTexture;

public class FishGrid extends Grid {

	public FishGrid() {}
	
	public FishGrid(int width, int height, World parent) {
		super(width, height, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Tile[][] initTiles(int width, int height)
	{
		Tile[][] grid = new Tile[height][width];
		
		for(int h = 0; h < height; h++)
		{
			for(int w = 0; w < width; w++)
			{
				grid[h][w] = new SandTile(w * 0.1f, h * -0.1f -0.1f, h * width + w +1, w, h);
				grid[h][w].setParent(this.getParent());
			}
		}
		
		return grid;
		
		
	}

}

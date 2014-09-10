package cdg.nut.util.game.fish;

import com.google.gson.JsonObject;

import cdg.nut.util.game.Tile;
import cdg.nut.util.gl.GLTexture;

public class SandTile extends Tile {

	public static GLTexture SAND = new GLTexture("res/fish/sand2.png");
	
	@Override
	public void deserialize(JsonObject json)
	{
		super.deserialize(json);
		
		this.setImage(SandTile.SAND);
		
	}
	
	public SandTile() {}
	
	public SandTile(float x, float y, int id, int w, int h)
	{
		super(x,y,id,w,h, SandTile.SAND);
	}
}

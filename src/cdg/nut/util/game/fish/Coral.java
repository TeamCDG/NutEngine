package cdg.nut.util.game.fish;

import com.google.gson.JsonObject;

import cdg.nut.util.game.Player;
import cdg.nut.util.gl.GLTexture;

public class Coral extends FishGameEntity {

	public static final GLTexture TEXTURE = new GLTexture("res/fish/coral.png", true);
	
	
	public Coral() {};
	
	@Override
	public void deserialize(JsonObject json)
	{
		super.deserialize(json);
		
		this.setPrimary(Coral.TEXTURE);
		
	}
	
	public Coral(float x, float y, int owner) {
		super(x, y, 0.1f, 0.1f, owner);
		this.setPrimary(Coral.TEXTURE);
		// TODO Auto-generated constructor stub
	}


}

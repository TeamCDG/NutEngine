package cdg.nut.util.game.fish;

import cdg.nut.util.game.Player;
import cdg.nut.util.gl.GLTexture;

public class Coral extends FishGameEntity {

	public static final GLTexture TEXTURE = new GLTexture("res/fish/coral.png", true);
	
	public Coral(float x, float y, Player p) {
		super(x, y, 0.1f, 0.1f, p);
		this.setPrimary(Coral.TEXTURE);
		// TODO Auto-generated constructor stub
	}


}

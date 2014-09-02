package cdg.nut.util.game.fish;

import org.lwjgl.opengl.GL13;

import cdg.nut.util.game.Player;
import cdg.nut.util.gl.GLTexture;

public class PeasantHut extends Bulding{

	public static GLTexture TEXTURE = new GLTexture("res/fish/peasant_hut.png");
	public static GLTexture TEXTURE_HEAD = new GLTexture("res/fish/peasant_hut_head.png", GL13.GL_TEXTURE1);
	public PeasantHut(float x, float y, Player p) {
		super(x, y, 0.3f, 0.3f, p);
		this.setPrimary(PeasantHut.TEXTURE);
		this.setLayer1(PeasantHut.TEXTURE_HEAD);
	}


}

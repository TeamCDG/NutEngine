package cdg.nut.util.game.fish;

import org.lwjgl.opengl.GL13;

import cdg.nut.interfaces.IEntity;
import cdg.nut.util.game.Player;
import cdg.nut.util.gl.GLTexture;

public class PeasantHut extends Building{

	public static GLTexture TEXTURE_FINAL = new GLTexture("res/fish/peasant_hut.png", true);
	public static GLTexture TEXTURE_STAGE0 = new GLTexture("res/fish/peasant_hut_stage0.png", true);
	public static GLTexture TEXTURE_STAGE1 = new GLTexture("res/fish/peasant_hut_stage1.png", true);
	public static GLTexture TEXTURE_HEAD = new GLTexture("res/fish/peasant_hut_head.png", GL13.GL_TEXTURE4, true);
	
	private boolean firstTick = true;
	private int stage = 0;
	
	public PeasantHut(float x, float y, int owner) {
		super(x, y, 0.3f, 0.3f, owner);
		this.setPrimary(PeasantHut.TEXTURE_FINAL);
		this.setLayer4(PeasantHut.TEXTURE_HEAD);
	}
	
	@Override
	public boolean onTick()
	{
		boolean change = super.onTick();
		
		if(this.firstTick)
		{
			this.setPrimary(TEXTURE_STAGE0);
			this.setLayer4(null);
		}
		this.firstTick = false;
		
		return change;
	}

	@Override
	public void onBuildHit(IEntity hitter)
	{
		super.onBuildHit(hitter);
		
		if((float)this.getBuildPoints() / (float)this.getMaxBuildPoints() > 0.33f && this.stage == 0)
		{
			this.setPrimary(TEXTURE_STAGE1);
			this.stage++;
		}
		else if((float)this.getBuildPoints() / (float)this.getMaxBuildPoints() > 0.66f && this.stage == 1)
		{
			this.setPrimary(TEXTURE_FINAL);
			this.stage++;
		}
		else if(this.isBuilt() && this.stage == 2)
		{
			this.setLayer4(PeasantHut.TEXTURE_HEAD);
			this.stage++;
		}
		
	}
}

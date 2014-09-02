package cdg.nut.util.game.fish;

import org.lwjgl.opengl.GL13;

import cdg.nut.logging.Logger;
import cdg.nut.util.Engine;
import cdg.nut.util.game.Player;
import cdg.nut.util.gl.GLTexture;

public class WorkerFish extends Fish{

	private int type = 0; //0: Builder; 1: Miner
	private float time = 0;
	
	private boolean swi = false;
	GLTexture item = new GLTexture("res/fish/builder.png", GL13.GL_TEXTURE1, true);
	GLTexture itemWalk = new GLTexture("res/fish/builder_walk.png", GL13.GL_TEXTURE1, true);
	
	
	public WorkerFish(float x, float y, Player p) {
		super(x, y, p);
		this.setLayer1(item);
		
		
	}

	
	@Override
	public void onTick()
	{
		super.onTick();
		
		this.time += Engine.getDelta();
		
		
		if(this.time > 250)
		{
			if(this.swi)
				this.setLayer1(item);
			else
				this.setLayer1(itemWalk);
			
			this.swi = !this.swi;
			this.time = 0;
		}
		
	}
}

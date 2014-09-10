package cdg.nut.util.game.fish;

import java.util.ArrayList;

import org.lwjgl.opengl.GL13;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cdg.nut.interfaces.IVertex;
import cdg.nut.logging.Logger;
import cdg.nut.util.Engine;
import cdg.nut.util.Vertex4;
import cdg.nut.util.enums.Commands;
import cdg.nut.util.game.PathRender;
import cdg.nut.util.game.Player;
import cdg.nut.util.gl.GLTexture;

public class WorkerFish extends Fish{

	private int fishType = 0; //0: Builder; 1: Miner
	private float time = 0;
	
	private boolean swi = false;
	private transient GLTexture item = new GLTexture("res/fish/builder.png", GL13.GL_TEXTURE1, true);
	private transient GLTexture itemWalk = new GLTexture("res/fish/builder_walk.png", GL13.GL_TEXTURE1, true);
	
	public WorkerFish() {}
	
	public WorkerFish(float x, float y, int owner) {
		super(x, y, owner);
		this.setLayer1(itemWalk);
		
		
		
	}

	@Override
	public void deserialize(JsonObject json)
	{
		super.deserialize(json);
		
		this.setLayer1(itemWalk);
		
		this.swi = json.get("swi").getAsBoolean();
		this.fishType = json.get("fishType").getAsInt();
		this.time = json.get("time").getAsFloat();
		
	}
	
	@Override
	public boolean onTick()
	{
		boolean change = super.onTick();
		
		if(this.getCom() == Commands.BUILD && this.time >= 500)
		{
			if(!((Building)this.getParent().get(getTargetId())).isBuilt())
			{
				((Building)this.getParent().get(getTargetId())).onBuildHit(this);
			}
			else
			{
				this.setCom(Commands.IDLE);
				change = true;
				this.setLayer1(itemWalk);
			}
			
			this.setLayer1(item);
			this.time = 0;
			this.swi = !this.swi;
		}
		else if(this.time >=  250 && !this.swi)
		{
			this.setLayer1(itemWalk);
			this.swi = !this.swi;
		}
		
		
		
		this.time += Engine.getDelta();
		
		return change;
		
	}
}

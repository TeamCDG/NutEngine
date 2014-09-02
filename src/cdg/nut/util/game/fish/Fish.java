package cdg.nut.util.game.fish;

import java.util.ArrayList;
import java.util.List;

import cdg.nut.interfaces.IEntity;
import cdg.nut.interfaces.IVertex;
import cdg.nut.logging.Logger;
import cdg.nut.util.Engine;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex2;
import cdg.nut.util.ai.Pathfinding;
import cdg.nut.util.enums.Commands;
import cdg.nut.util.game.Entity2D;
import cdg.nut.util.game.PathRender;
import cdg.nut.util.game.Player;
import cdg.nut.util.gl.GLTexture;

public class Fish extends FishGameEntity {

	private IVertex targetPosition;
	private Entity2D target;
	private Commands com = Commands.IDLE;
	
	private PathRender pathRender;
	
	private List<IVertex> path;
	
	private float speed = 0.0005f;
	
	static GLTexture fishBase = new GLTexture("res/fish/base.png", true);
	
	public Fish(float x, float y, Player p) {
		super(x, y, 0.1f, 0.05f, p);
		
		this.setPrimary(fishBase);
	 
	}

	@Override
	public void rightClickhappened(IEntity entity, float worldX, float worldY) {
		this.com = Commands.MOVE;
		
		long t1 = System.currentTimeMillis();
		List<IVertex> wp = Pathfinding.astar(this.getParent().getGrid(), this.getX(), this.getY(), worldX, worldY);
		Logger.info("Pathfinding took: "+((System.currentTimeMillis()-t1))+"ms");
		
		if(wp == null) return;
		
		if(this.path == null) this.path = new ArrayList<IVertex>(wp.size()+2);
		else this.path.clear();
		
		this.path.add(new Vertex2(this.getX(), this.getY()));
		for(int i = 1; i < wp.size()-1; i++)
		{
			this.path.add(new Vertex2(wp.get(i).getX()*0.1f + 0.05f, wp.get(i).getY()*-0.1f - 0.05f));
		}
		this.path.add(new Vertex2(worldX, worldY));
		
		if(pathRender == null) pathRender = new PathRender(this.path);
		else pathRender.setWayPoints(this.path);
		
		Logger.debug("rc happened");
	}
	
	@Override
	public void onTick()
	{
		switch(this.com)
		{
		case ATTACK:
			break;
		case BUILD:
			break;
		case FLEE:
			break;
		case FOLLOW:
			break;
		case GATHER:
			break;
		case IDLE:
			break;
		case MOVE:
			if(this.targetPosition == null && this.path.size() >  0)
			{
				this.targetPosition = this.path.get(0);
				this.path.remove(0);
			}
			if(this.targetPosition != null && this.targetPosition.getDistanceTo(new Vertex2(this.getX(), this.getY())) > 0.02f)
			{
				float dx = targetPosition.getX() - this.getX();
				float dy = targetPosition.getY() - this.getY();
				
				float ang = (float)(Math.atan2(dx, dy));
				
				this.moveAdd(this.speed * Engine.getDelta() * (float)Math.sin(ang), 
							 this.speed * Engine.getDelta() * (float)Math.cos(ang));
			}
			else if(this.targetPosition.getDistanceTo(new Vertex2(this.getX(), this.getY())) < 0.02f && this.path.size() >  0)
			{
				this.targetPosition = this.path.get(0);
				this.path.remove(0);
			}
			else
			{
				this.com = Commands.IDLE;
				this.move(this.targetPosition.getX(), this.targetPosition.getY());
			}
			
			break;
		case PROTECT:
			break;
		default:
			break;
		
		}
	}

	public Entity2D getTarget() {
		return target;
	}

	public void setTarget(Entity2D target) {
		this.target = target;
	}

	public IVertex getTargetPosition() {
		return targetPosition;
	}

	public void setTargetPosition(IVertex targetPosition) {
		this.targetPosition = targetPosition;
	}
	
	@Override
	public void drawChildren(boolean s)
	{
		if(!s && this.pathRender != null && this.isActive()) 
		{
			this.pathRender.setCam(this.getParent().getCamera());
			this.pathRender.draw();
		}
	}
}

package cdg.nut.util.game.fish;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cdg.nut.interfaces.IEntity;
import cdg.nut.interfaces.IVertex;
import cdg.nut.logging.Logger;
import cdg.nut.util.Engine;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex2;
import cdg.nut.util.Vertex4;
import cdg.nut.util.ai.AsyncPathfindingManager;
import cdg.nut.util.ai.Pathfinding;
import cdg.nut.util.ai.PathfindingObject;
import cdg.nut.util.enums.Commands;
import cdg.nut.util.game.Entity2D;
import cdg.nut.util.game.PathRender;
import cdg.nut.util.game.Player;
import cdg.nut.util.game.Tile;
import cdg.nut.util.gl.GLTexture;

public class Fish extends FishGameEntity {

	private IVertex targetPosition;
	private int targetId;
	private Commands com = Commands.IDLE;
	private Commands nxt = Commands.IDLE;
	private Commands wfp = Commands.IDLE;
	private transient PathRender pathRender;
	
	private int pathPoint = 0;
	private float worldX, worldY;
	
	private List<IVertex> path;
	
	private float speed = 0.0005f;
	private boolean dodge = false;
	
	static GLTexture fishBase = new GLTexture("res/fish/base.png", true);
	
	
	public Fish() {}
	
	@Override
	public void deserialize(JsonObject json)
	{
		super.deserialize(json);
		
		Gson g = new Gson();
		
		this.setPrimary(fishBase);
		this.dodge = json.get("dodge").getAsBoolean();
		this.speed = json.get("speed").getAsFloat();
		this.worldX = json.get("worldX").getAsFloat();
		this.worldY = json.get("worldY").getAsFloat();
		
		this.pathPoint = json.get("pathPoint").getAsInt();
		
		this.wfp = Commands.valueOf(json.get("wfp").getAsString());
		this.nxt = Commands.valueOf(json.get("nxt").getAsString());
		this.com = Commands.valueOf(json.get("com").getAsString());
		
		this.targetPosition = g.fromJson(json.get("targetPosition"), Vertex4.class);
		this.targetId = json.get("targetId").getAsInt();
		
		if(json.get("path") != null)
		{
			JsonArray wp = json.get("path").getAsJsonArray();
			this.path = new ArrayList<IVertex>(wp.size());
			
			for(int i = 0; i < wp.size(); i++)
			{
				this.path.add(g.fromJson(wp.get(i), Vertex4.class));
			}
			
			if(this.path.size() > 0) this.pathRender = new PathRender(this.path);
		}
		
	}
	
	public Fish(float x, float y, int owner) {
		super(x, y, 0.1f, 0.05f, owner);
		
		this.setPrimary(fishBase);
	 
	}

	@Override
	public void rightClickhappened(IEntity entity, float worldX, float worldY) {
		
		long t1 = System.currentTimeMillis();
		this.com = Commands.WAIT_FOR_PATH;
		this.wfp = Commands.MOVE;
		this.nxt = Commands.IDLE;
		
		
		this.worldX = worldX;
		this.worldY = worldY;
		
		if(FishGameUtility.isBuilding(entity))
		{
			Building b = (Building) entity;
			Tile t = this.getParent().getGrid().getTile(b.getX(), b.getY());
			Tile t2 = this.getParent().getGrid().getTile(this.getX(), this.getY());
			
			//wp 	= Pathfinding.astar(this.getParent().getGrid(), t2.getTX(), t2.getTY(), t.getTX(), t.getTY(), b.getId());
			
			AsyncPathfindingManager.add(new PathfindingObject(this.getId(), this.getParent().getGrid(), t2.getTX(), t2.getTY(), t.getTX(), t.getTY(), b.getId()));
			
			Logger.info("Building: "+t.getTX()+"/"+t.getTY());


			if(this.getOwner().equals(b.getOwner()))
			{
				if(!b.isBuilt())
				{
					this.nxt = Commands.BUILD;
					this.targetId = b.getId();
				}
			}
			else
			{
				//ally check, etc bla bla
			}
			
		}
		else
		{
			AsyncPathfindingManager.add(new PathfindingObject(this.getId(), this.getParent().getGrid(), this.getX(), this.getY(), worldX, worldY));
		}
		
		
		long t2 = System.currentTimeMillis() - t1; 
		Logger.debug("rc happened, handling took: "+t2+"ms");
	}
	
	
	private void makeDodgeWaypoints(List<IVertex> wp)
	{
		if(wp == null) return;
		
		this.dodge = false;
		
		if(this.path == null) this.path = new ArrayList<IVertex>(wp.size()+2);
		else this.path.clear();
		
		this.path.add(new Vertex2(this.getX(), this.getY()));
		for(int i = 1; i < wp.size(); i++)
		{
			this.path.add(new Vertex2(wp.get(i).getX()*0.1f + 0.05f, wp.get(i).getY()*-0.1f - 0.05f));
		}
		if(pathRender == null) pathRender = new PathRender(this.path);
		else pathRender.setWayPoints(this.path);
		this.targetPosition = null;
		this.pathPoint = 0;
		this.com = Commands.MOVE;
	}
	
	private void makeWaypoints(List<IVertex> wp){
		if(wp == null) return;
		
		if(this.path == null) this.path = new ArrayList<IVertex>(wp.size()+2);
		else this.path.clear();
		
		this.path.add(new Vertex2(this.getX(), this.getY()));
		for(int i = 1; i < wp.size()-1; i++)
		{
			this.path.add(new Vertex2(wp.get(i).getX()*0.1f + 0.05f, wp.get(i).getY()*-0.1f - 0.05f));
		}
		
		if(this.getParent().getGrid().getTile((int)wp.get(wp.size() - 1).getX(), (int)wp.get(wp.size() - 1).getY()).isOccupied() && wp.size() > 1)
		{
			this.path.remove(this.path.size() -1);
			float xoff = 0.05f;
			float yoff = -0.05f;
			
			if(wp.get(wp.size() - 1).getX() > wp.get(wp.size() - 2).getX())
				xoff = 0.09f;
			else if(wp.get(wp.size() - 1).getX() < wp.get(wp.size() - 2).getX())
				xoff = 0.01f;
			
			if(wp.get(wp.size() - 1).getY() > wp.get(wp.size() - 2).getY())
				yoff = -0.09f;
			else if(wp.get(wp.size() - 1).getY() < wp.get(wp.size() - 2).getY())
				yoff = -0.01f;
			
			this.path.add(new Vertex2(wp.get(wp.size() - 2).getX()*0.1f + xoff, wp.get(wp.size() - 2).getY()*-0.1f + yoff));
		}
		else
		{
			this.path.add(new Vertex2(this.worldX, this.worldY));
		}
		
		if(pathRender == null) pathRender = new PathRender(this.path);
		else pathRender.setWayPoints(this.path);
		this.targetPosition = null;
		this.pathPoint = 0;
	}
	
	/**
	 * @return the com
	 */
	protected Commands getCom() {
		return com;
	}

	/**
	 * @param com the com to set
	 */
	protected void setCom(Commands com) {
		this.com = com;
	}

	@Override
	public boolean onTick()
	{
		boolean change = false;
		
		Tile t = this.getParent().getGrid().getTile(this.getX(), this.getY());
		if(t.isOccupied() && this.com != Commands.MOVE)
		{
			if(this.com != Commands.WAIT_FOR_PATH)
			{
				AsyncPathfindingManager.add(new PathfindingObject(this.getId(), this.getParent().getGrid(), t.getTX(), t.getTY()));
				this.dodge = true;
				this.com = Commands.WAIT_FOR_PATH;
				this.wfp = Commands.MOVE;
				change = true;
				
			}
		}
		
		if(this.com == Commands.WAIT_FOR_PATH)
		{
			List<IVertex> wp = AsyncPathfindingManager.getPath(this.getId());
			if(wp != null)
			{
				if(this.dodge)
					this.makeDodgeWaypoints(wp);
				else
					this.makeWaypoints(wp);
				
				
				this.com = this.wfp;
				change = true;
			}
		}
		
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
			if(this.targetPosition == null && this.path != null && this.pathPoint < this.path.size())
			{
				this.targetPosition = this.path.get(this.pathPoint);
				this.pathPoint++;
				change = true;
			}
			if(this.targetPosition != null && this.targetPosition.getDistanceTo(new Vertex2(this.getX(), this.getY())) > 0.02f)
			{
				float dx = targetPosition.getX() - this.getX();
				float dy = targetPosition.getY() - this.getY();
				
				float ang = (float)(Math.atan2(dx, dy));
				
				this.moveAdd(this.speed * Engine.getDelta() * (float)Math.sin(ang), 
							 this.speed * Engine.getDelta() * (float)Math.cos(ang));
				change = true;
			}
			else if(this.targetPosition != null && this.targetPosition.getDistanceTo(new Vertex2(this.getX(), this.getY())) < 0.02f && this.path != null && this.pathPoint < this.path.size())
			{
				this.targetPosition = this.path.get(this.pathPoint);
				this.pathPoint++;
				change = true;
			}
			else if(this.targetPosition != null && this.path != null)
			{
				this.com = this.nxt;
				this.move(this.targetPosition.getX(), this.targetPosition.getY());
				change = true;
			}
			
			break;
		case PROTECT:
			break;
		default:
			break;
			
			
		
		}
		
		return change;
	}

	public int getTargetId() {
		return targetId;
	}

	public void setTarget(Entity2D target) {
		this.targetId = target.getId();
	}
	
	public void setTargetId(int target) {
		this.targetId = target;
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
			//this.pathRender.setCam(this.getParent().getCamera());
			this.pathRender.draw();
		}
	}
	

	@Override 
	protected void passUniforms()
	{
		super.passUniforms();
		
		if(this.getOwner()!=null)
			this.getShader().passColor("playerColor", this.getOwner().getPlayerColor());
	}
}

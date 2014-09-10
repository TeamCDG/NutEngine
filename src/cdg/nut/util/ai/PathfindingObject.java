package cdg.nut.util.ai;

import java.util.List;

import cdg.nut.interfaces.IFinishedListener;
import cdg.nut.interfaces.IVertex;
import cdg.nut.logging.Logger;
import cdg.nut.util.game.Grid;
import cdg.nut.util.game.Player;
import cdg.nut.util.game.Tile;
import cdg.nut.util.game.World;

public class PathfindingObject extends Thread {
	
	private Grid g;
	private World w;
	private Player p;
	private int sx, sy, ex = -1, ey = -1;
	private int bid = -1;
	private Class findType;
	
	private IFinishedListener lis;
	private int eid;
	private boolean canceled = false;
	
	public PathfindingObject(int eid, Grid g, IVertex begin, IVertex end)
	{
		Tile s = g.getTile(begin.getX(), begin.getY());
		Tile e = g.getTile(end.getX(), end.getY());
		
		this.load(eid, g, s.getTX(), s.getTY(), e.getTX(), e.getTY());		
	}
	
	public PathfindingObject(int eid, Grid g, float sx, float sy, float ex, float ey)
	{
		Tile s = g.getTile(sx, sy);
		Tile e = g.getTile(ex, ey);
		
		this.load(eid, g, s.getTX(), s.getTY(), e.getTX(), e.getTY());
	}
	
	public PathfindingObject(int eid, Grid g, Tile s, Tile e)
	{		
		this.load(eid, g, s.getTX(), s.getTY(), e.getTX(), e.getTY());
	}
	
	public PathfindingObject(int eid, Grid g, int sx, int sy, int ex, int ey)
	{
		this.eid = eid;
		this.g = g;
		this.sx = sx;
		this.sy = sy;
		this.ex = ex;
		this.ey = ey;
	}
	
	public PathfindingObject(int eid, Grid g, int sx, int sy, int ex, int ey, int bid)
	{
		this.eid = eid;
		this.g = g;
		this.sx = sx;
		this.sy = sy;
		this.ex = ex;
		this.ey = ey;
		this.bid  = bid;
	}
	
	public PathfindingObject(int eid, World w, int sx, int sy, Class findType)
	{
		this.eid = eid;
		this.w = w;
		this.sx = sx;
		this.sy = sy;
		this.findType = findType;
	}
	
	public PathfindingObject(int eid, Grid g, int sx, int sy)
	{
		this.eid = eid;
		this.g = g;
		this.sx = sx;
		this.sy = sy;
	}
	
	public PathfindingObject(int eid, World w, int sx, int sy, Player p, Class findType)
	{
		this.eid = eid;
		this.w = w;
		this.sx = sx;
		this.sy = sy;
		this.p = p;
		this.findType = findType;
	}
	
	private void load(int eid, Grid g, int sx, int sy, int ex, int ey)
	{
		this.eid = eid;
		this.g = g;
		this.sx = sx;
		this.sy = sy;
		this.ex = ex;
		this.ey = ey;
	}
	
	@Override
	public void run()
	{
		List<IVertex> p = null;
		
		long t1 = System.currentTimeMillis();
		if(ex != -1 && ey != -1)
		{
			if(this.bid != -1)
			{
				Logger.info("astar: building search, from tile("+sx+"/"+sy+") to building("+bid+") located at tile("+ex+"/"+ey+")");
				p = Pathfinding.astar(g, sx, sy, ex, ey, bid);
			}
			else
			{
				Logger.info("astar: from tile("+sx+"/"+sy+") to tile("+ex+"/"+ey+")");
				p = Pathfinding.astar(g, sx, sy, ex, ey);			
			}
		}
		else
		{
			if(this.findType == null)
			{
				if(g != null) p = Pathfinding.findNextFree(g, sx, sy);
				else p = Pathfinding.findNextFree(w.getGrid(), sx, sy);
			}
			else
			{
				//TODO: Djikstra
			}
		}
		long t2 = ((System.currentTimeMillis()-t1));
		Logger.info("Pathfinding took: "+t2+"ms");
		
		if(!this.canceled  && this.lis != null)
			lis.finished(this.eid, p);
	}

	public IFinishedListener getLis() {
		return lis;
	}

	public void setLis(IFinishedListener lis) {
		this.lis = lis;
	}

	public int getEid() {
		return eid;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

}

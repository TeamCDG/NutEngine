package cdg.nut.util.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cdg.nut.interfaces.*;
import cdg.nut.logging.Logger;
import cdg.nut.util.Vertex2;
import cdg.nut.util.game.Grid;
import cdg.nut.util.game.Tile;

public abstract class Pathfinding {

	
	public static List<IVertex> findNextFree(Grid g, int sx, int sy) // using Dijkstra
	{
		
		List<PFTile> open = new ArrayList<PFTile>(g.getTileCount());
		List<PFTile> closed = new ArrayList<PFTile>(g.getHTileCount() + g.getVTileCount());
		
		byte[][] whr = new byte[g.getVTileCount()][g.getHTileCount()]; // save in which list we find the tile... way faster than searching
		
		
		Logger.info("Dijkstra: from Tile( "+sx+" / "+sy+" ) for next free Tile");
		
		closed.add(new PFTile(0, 0, sx, sy));
		whr[sx][sy] = Pathfinding.CLOSED;
		PFTile previous = closed.get(0);
		
		boolean pFound = false;
		
		while(!pFound)
		{
			
			if(whr[previous.getX()][previous.getY()] == Pathfinding.OPEN)
				open.remove(previous);
			closed.add(previous);
			whr[previous.getX()][previous.getY()] = Pathfinding.CLOSED;
			
			if(!g.getTile(previous.getX(), previous.getY()).isOccupied())
			{
				pFound = true;
				break;
			}
			
			
			Tile[] tmp = g.getTileSurroundings(previous.getX(), previous.getY());
			
			for(int i = 0; i < tmp.length; i++)
			{
				
				
				if(whr[tmp[i].getTX()][tmp[i].getTY()] == Pathfinding.NOT_LISTED)
				//if(!inList(tmp[i].getTX(),tmp[i].getTY(), open) && !inList(tmp[i].getTX(),tmp[i].getTY(), closed))
				{
					PFTile t = new PFTile(g(tmp[i].getTX(), tmp[i].getTY(), previous), 0, tmp[i].getTX(), tmp[i].getTY(), previous);
					whr[t.getX()][t.getY()] = Pathfinding.OPEN;
					open.add(getIdx(t, open), t);
					
				}
				else if (whr[tmp[i].getTX()][tmp[i].getTY()] == Pathfinding.OPEN)
				//else if (inList(tmp[i].getTX(),tmp[i].getTY(), open))
				{
					int tIdx = getTile(tmp[i].getTX(), tmp[i].getTY(), open);
					int gOld = open.get(tIdx).getG();
					int gNew = g(open.get(tIdx).getX(), open.get(tIdx).getY(), previous);
					
					if(gOld >= gNew)
					{
						PFTile t = open.get(tIdx);
						open.remove(tIdx);
						t.setG(gNew);
						open.add(getIdx(t, open), t);
					}
				}
			}
			
			if(open.size() == 0)
				break;
			
			previous = open.get(0);
		}
		
		if(!pFound)
			return null; // no path found :(
		
		List<IVertex> points = new ArrayList<IVertex>(closed.size());
		
		boolean bts = false;
		PFTile step = closed.get(closed.size()-1);
		
		while(!bts)
		{
			Logger.debug("step: ( "+step.getX()+" / "+step.getY()+" )");
			points.add(0, new Vertex2(step.getX(), step.getY()));
			
			step = step.getPrevious();
			
			if(step.getX() == sx && step.getY() == sy)
				bts = true;
		}
		
		points.add(0, new Vertex2(step.getX(), step.getY()));
		
		return points;
	}
	
	
	

	public static List<IVertex> astar(Grid g, IVertex begin, IVertex end)
	{
		Tile s = g.getTile(begin.getX(), begin.getY());
		Tile e = g.getTile(end.getX(), end.getY());
		
		return Pathfinding.astar(g, s.getTX(), s.getTY(), e.getTX(), e.getTY());
	}
	
	public static List<IVertex> astar(Grid g, float sx, float sy, float ex, float ey)
	{
		Tile s = g.getTile(sx, sy);
		Tile e = g.getTile(ex, ey);
		
		return Pathfinding.astar(g, s.getTX(), s.getTY(), e.getTX(), e.getTY());
	}
	
	public static List<IVertex> astar(Grid g, Tile s, Tile e)
	{		
		return Pathfinding.astar(g, s.getTX(), s.getTY(), e.getTX(), e.getTY());
	}
	
	private static final byte NOT_LISTED = 0x0;
	private static final byte OPEN = 0x1;
	private static final byte CLOSED = 0x2;
	
	public static List<IVertex> astar(Grid g, int sx, int sy, int ex, int ey)
	{
		
		if(sx == ex && sy == ey)
		{
			List<IVertex> points = new ArrayList<IVertex>(1);
			points.add(new Vertex2(sx,sy));
			return points;
		}
			
		
		List<PFTile> open = new ArrayList<PFTile>(g.getTileCount());
		List<PFTile> closed = new ArrayList<PFTile>(g.getHTileCount() + g.getVTileCount());
		
		byte[][] whr = new byte[g.getVTileCount()][g.getHTileCount()]; // save in which list we find the tile... way faster than searching
		
		Logger.info("A*: from tile( "+sx+" / "+sy+" ) to tile( "+ex+" / "+ey+" )");
		
		closed.add(new PFTile(0, Pathfinding.manhatten(sx, sy, ex, ey), sx, sy));
		whr[sx][sy] = Pathfinding.CLOSED;
		PFTile previous = closed.get(0);
		
		boolean pFound = false;
		
		while(!pFound)
		{
			
			if(whr[previous.getX()][previous.getY()] == Pathfinding.OPEN)
				open.remove(previous);
			
			closed.add(previous);
			whr[previous.getX()][previous.getY()] = Pathfinding.CLOSED;
			
			
			if(previous.getX() == ex && previous.getY() == ey)
			{
				pFound = true;
				break;
			}
			
			
			Tile[] tmp = g.getTileSurroundings(previous.getX(), previous.getY());
			
			for(int i = 0; i < tmp.length; i++)
			{
				
				if((tmp[i].getTX() == ex && tmp[i].getTY() == ey && tmp[i].isOccupied()) || !tmp[i].isOccupied())
				{
					if(whr[tmp[i].getTX()][tmp[i].getTY()] == Pathfinding.NOT_LISTED)
						//if(!inList(tmp[i].getTX(),tmp[i].getTY(), open) && !inList(tmp[i].getTX(),tmp[i].getTY(), closed))
						{
							PFTile t = new PFTile(g(tmp[i].getTX(), tmp[i].getTY(), previous), manhatten(tmp[i].getTX(), tmp[i].getTY(), ex, ey), tmp[i].getTX(), tmp[i].getTY(), previous);
							whr[t.getX()][t.getY()] = Pathfinding.OPEN;
							open.add(getIdx(t, open), t);
							
						}
						else if (whr[tmp[i].getTX()][tmp[i].getTY()] == Pathfinding.OPEN)
						//else if (inList(tmp[i].getTX(),tmp[i].getTY(), open))
						{
							int tIdx = getTile(tmp[i].getTX(), tmp[i].getTY(), open);
							int gOld = open.get(tIdx).getG();
							int gNew = g(open.get(tIdx).getX(), open.get(tIdx).getY(), previous);
							
							if(gOld >= gNew)
							{
								PFTile t = open.get(tIdx);
								open.remove(tIdx);
								t.setG(gNew);
								open.add(getIdx(t, open), t);
							}
						}
				}
				
				
				//if(tmp[i].isOccupied()) continue;
				
				
				
			}
			
			if(open.size() == 0)
				break;
			
			previous = open.get(0);
		}
		
		if(!pFound)
			return null; // no path found :(
		
		List<IVertex> points = new ArrayList<IVertex>(closed.size());
		
		boolean bts = false;
		PFTile step = closed.get(closed.size()-1);
		
		while(!bts)
		{
			//Logger.debug("step: ( "+step.getX()+" / "+step.getY()+" )");
			points.add(0, new Vertex2(step.getX(), step.getY()));
			
			step = step.getPrevious();
			
			if(step.getX() == sx && step.getY() == sy)
				bts = true;
		}
		
		points.add(0, new Vertex2(step.getX(), step.getY()));
		
		return points;
	}
	
	public static List<IVertex> astar(Grid g, int sx, int sy, int ex, int ey, int bid) //building.... some hack to go, defnitely needs better
	{
		
		if(sx == ex && sy == ey)
		{
			List<IVertex> points = new ArrayList<IVertex>(1);
			points.add(new Vertex2(sx,sy));
			return points;
		}
			
		
		List<PFTile> open = new ArrayList<PFTile>(g.getTileCount());
		List<PFTile> closed = new ArrayList<PFTile>(g.getHTileCount() + g.getVTileCount());
		
		byte[][] whr = new byte[g.getVTileCount()][g.getHTileCount()]; // save in which list we find the tile... way faster than searching
		
		Logger.info("A*: from tile( "+sx+" / "+sy+" ) to tile( "+ex+" / "+ey+" )");
		
		closed.add(new PFTile(0, Pathfinding.manhatten(sx, sy, ex, ey), sx, sy));
		whr[sx][sy] = Pathfinding.CLOSED;
		PFTile previous = closed.get(0);
		
		boolean pFound = false;
		
		while(!pFound)
		{
			
			if(whr[previous.getX()][previous.getY()] == Pathfinding.OPEN)
				open.remove(previous);
			
			closed.add(previous);
			whr[previous.getX()][previous.getY()] = Pathfinding.CLOSED;
			
			
			if(g.getTile(previous.getX(), previous.getY()).getEntityId() == bid)
			{
				pFound = true;
				break;
			}
			
			
			Tile[] tmp = g.getTileSurroundings(previous.getX(), previous.getY());
			
			for(int i = 0; i < tmp.length; i++)
			{
				
				if((tmp[i].getEntityId() == bid && tmp[i].isOccupied()) || tmp[i].isOccupied() == false)
				{
					if(whr[tmp[i].getTX()][tmp[i].getTY()] == Pathfinding.NOT_LISTED)
						//if(!inList(tmp[i].getTX(),tmp[i].getTY(), open) && !inList(tmp[i].getTX(),tmp[i].getTY(), closed))
						{
							PFTile t = new PFTile(g(tmp[i].getTX(), tmp[i].getTY(), previous), manhatten(tmp[i].getTX(), tmp[i].getTY(), ex, ey), tmp[i].getTX(), tmp[i].getTY(), previous);
							whr[t.getX()][t.getY()] = Pathfinding.OPEN;
							open.add(getIdx(t, open), t);
							
						}
						else if (whr[tmp[i].getTX()][tmp[i].getTY()] == Pathfinding.OPEN)
						//else if (inList(tmp[i].getTX(),tmp[i].getTY(), open))
						{
							int tIdx = getTile(tmp[i].getTX(), tmp[i].getTY(), open);
							int gOld = open.get(tIdx).getG();
							int gNew = g(open.get(tIdx).getX(), open.get(tIdx).getY(), previous);
							
							if(gOld >= gNew)
							{
								PFTile t = open.get(tIdx);
								open.remove(tIdx);
								t.setG(gNew);
								open.add(getIdx(t, open), t);
							}
						}
				}
				
				
				//if(tmp[i].isOccupied()) continue;
				
				
				
			}
			
			if(open.size() == 0)
				break;
			
			previous = open.get(0);
		}
		
		if(!pFound)
			return null; // no path found :(
		
		List<IVertex> points = new ArrayList<IVertex>(closed.size());
		
		boolean bts = false;
		PFTile step = closed.get(closed.size()-1);
		
		while(!bts)
		{
			Logger.debug("step: ( "+step.getX()+" / "+step.getY()+" )");
			points.add(0, new Vertex2(step.getX(), step.getY()));
			
			step = step.getPrevious();
			
			if(step.getX() == sx && step.getY() == sy)
				bts = true;
		}
		
		points.add(0, new Vertex2(step.getX(), step.getY()));
		
		return points;
	}
	
	private static int getTile(int x, int y, List<PFTile> l)
	{
		for(int i = 0; i < l.size(); i++)
		{
			if(l.get(i).getX() == x && l.get(i).getY() == y)
				return i;
		}
		return -1;
	}
	
	private static int getIdx(PFTile t, List<PFTile> l)
	{
		int idx = Collections.binarySearch(l, t, new PFTileComperator());
		
		if(idx >= 0)
			return idx;
		else
			return -idx-1;
	}
	
	private static boolean inList(int x, int y, List<PFTile> list)
	{
		for(int i = 0; i < list.size(); i++)
		{
			if(list.get(i).getX() == x && list.get(i).getY() == y)
				return true; 
		}
		return false;
	}
	
	private static boolean inList(PFTile t, List<PFTile> list)
	{
		for(int i = 0; i < list.size(); i++)
		{
			if(list.get(i).getX() == t.getX() && list.get(i).getY() == t.getY())
				return true;
		}
		return false;
	}
	
	private static int f(PFTile b, int ex, int ey)
	{
		return g(b) + manhatten(b.getX(), b.getY(), ex, ey);
	}
	
	private static int g(int x, int y, PFTile b)
	{		
		int g = 0;
		
		int xdif = Math.abs(b.getX() - x);
		int ydif = Math.abs(b.getY() - y);
		
		if(xdif > 0 && ydif > 0)
		{
			g += 14;
		}
		else 
		{
			g += 10;
		}
		
		return g + g(b);
	}
	
	private static int g(PFTile b)
	{		
		int g = 0;
		
		while(b.getPrevious() != null)
		{
			int xdif = Math.abs(b.getX() - b.getPrevious().getX());
			int ydif = Math.abs(b.getY() - b.getPrevious().getY());
			
			if(xdif > 0 && ydif > 0)
			{
				g += 14;
			}
			else 
			{
				g += 10;
			}
			
			b = b.getPrevious();
		}
		
		return g;
	}
	
	private static int manhatten(int x, int y, int ex, int ey) //Manhatten...
	{
		return 10*(Math.abs(x-ex) + Math.abs(y-ey));
	}

}

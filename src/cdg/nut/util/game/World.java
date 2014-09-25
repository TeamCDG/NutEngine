package cdg.nut.util.game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import cdg.nut.interfaces.IEntity;
import cdg.nut.logging.Logger;
import cdg.nut.util.enums.Colors;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.net.Package;

public class World {

	int nextId = 1; //TODO: use global id's frame and world...
	
	
	
	private Camera camera;
	
	private int width;
	private int height;

	
	private transient int skippedTiles;
	private transient int drawnTiles;
	
	private transient int skippedEntities;
	private transient int drawnEntities;
	
	private boolean renderGridOccupiedView = false;
	private boolean renderGrid = false;
	private boolean forceSelectionRender = false;
	
	private List<IEntity> entities;
	private List<Player> player;
	
	private transient List<Integer> added = new LinkedList<Integer>();
	private transient List<Integer> removed = new LinkedList<Integer>();
	
	private Grid grid;
	private String type;
	
	
	
	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the added
	 */
	protected List<Integer> getAdded() {
		List<Integer> cloned = new LinkedList<Integer>(added);
		this.added.clear();
		return cloned;		
	}

	/**
	 * @return the removed
	 */
	protected List<Integer> getRemoved() {
		List<Integer> cloned = new LinkedList<Integer>(removed);
		this.removed.clear();
		return cloned;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	
	public World() {this.player = new ArrayList<Player>();}
	
	public void deserialize(int width, int height, Camera cam, List<Player> p, Grid g, List<IEntity> entities)
	{
		this.width = width;
		this.height = height;
		this.camera = cam;
		this.player = p;
		this.grid = g;
		this.entities = entities;
		this.type = this.getClass().getName();
	}
	
	public World(int width, int height)
	{
		this.entities = new ArrayList<IEntity>();
		this.setCamera(new Camera());
		
		this.width = width;
		this.height = height;
		
		this.grid = this.initGrid(width, height);
		this.player = new ArrayList<Player>();

		this.type = this.getClass().getName();
	}
	
	public Grid initGrid(int width, int height)
	{
		
		return new Grid(width, height, this);
	}
	
	public void onTick()
	{
		if(this.entities != null)
		{
			for(int i = 0; i < this.entities.size(); i++)
			{
				this.entities.get(i).onTick();
			}
		}
	}
	
	public void add(IEntity e)
	{
		this.addEntity(e);
	}
	
	public void addEntity(IEntity e)
	{		
		this.nextId += e.setId(this.nextId);
		e.setParent(this);
		this.entities.add(e);
		this.added.add(e.getId());
	}
	
	public IEntity get(int id)
	{
		return this.getEntity(id);
	}
	
	public IEntity getEntity(int id)
	{
		for(int i = 0; i < this.entities.size(); i++)
		{
			if(this.entities.get(i).getId() == id)
			{
				return this.entities.get(i);
			}
		}
		
		return null;
	}
	
	public void removeEntity(IEntity e)
	{
		this.entities.remove(e);
		this.removed.add(e.getId());
	}
	
	public void removeEntity(int id)
	{
		for(int i = 0; i < this.entities.size(); i++)
		{
			if(this.entities.get(i).getId() == id)
			{
				this.entities.remove(i);
				this.removed.add(i);
				break;
			}
		}
	}
	
	public List<IEntity> getEntites()
	{
		return this.entities;
	}
	

	public <T extends IEntity> List<T> getEntitesByClass(Class<T> c)
	{
		List<T> list = new ArrayList<T>(this.entities.size());
		
		for(int i = 0; i < this.entities.size(); i++)
		{
			if(this.entities.get(i).getClass() == c)
			{
				list.add(c.cast(this.entities.get(i)));
			}
		}
		
		return list;
	}

	public void setNextId(int nextId) {
		this.nextId = nextId;
		
	}
	
	public int getNextId() {
		return this.nextId;
		
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public Grid getGrid() {
		return grid;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	public int getSkippedTiles() {
		return skippedTiles;
	}

	public void setSkippedTiles(int skippedTiles) {
		this.skippedTiles = skippedTiles;
	}

	public int getDrawnTiles() {
		return drawnTiles;
	}

	public void setDrawnTiles(int drawnTiles) {
		this.drawnTiles = drawnTiles;
	}
	
	public void draw()
	{
		this.skippedTiles = 0;
		this.drawnTiles = 0;
		
		this.skippedEntities = 0;
		this.drawnEntities = 0;
		
		this.grid.draw();
		for(int i = 0; i < this.entities.size(); i++)
		{
			this.entities.get(i).draw();
		}
	}

	public boolean isRenderGridOccupiedView() {
		return renderGridOccupiedView;
	}

	public void setRenderGridOccupiedView(boolean renderGridOccupiedView) {
		this.renderGridOccupiedView = renderGridOccupiedView;
	}

	public boolean isRenderGrid() {
		return renderGrid;
	}

	public void setRenderGrid(boolean renderGrid) {
		this.renderGrid = renderGrid;
	}

	public boolean isForceSelectionRender() {
		return forceSelectionRender;
	}

	public void setForceSelectionRender(boolean forceSelectionRender) {
		this.forceSelectionRender = forceSelectionRender;
	}

	public int getDrawnEntities() {
		return drawnEntities;
	}

	public void setDrawnEntities(int drawnEntities) {
		this.drawnEntities = drawnEntities;
	}

	public int getSkippedEntities() {
		return skippedEntities;
	}

	public void setSkippedEntities(int skippedEntities) {
		this.skippedEntities = skippedEntities;
	}
	
	public void addPlayer(String name, GLColor color, boolean local)
	{
		Player p = new Player(color, name);
		p.setPlayerId(this.player.size());
		p.setLocal(local);
		this.player.add(p);
	}
	
	public Player getPlayer(int id)
	{
		return this.player.get(id);
	}
	
	public Player getLocalPlayer()
	{
		for(int i = 0; i < this.player.size(); i++)
		{
			if(this.player.get(i).isLocal())
				return this.player.get(i);
			
		}
		
		return null;
	}
	
	public static World deserialize(String path)
	{
		return World.deserialize(path, false);
	}
	
	public static World deserialize(String path, boolean gl_less)
	{
	    BufferedReader br = null;
	    try {
	    	br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(path))));
	        JsonReader r = new JsonReader(br);
	        r.setLenient(true);
	        JsonObject world = new JsonParser().parse(r).getAsJsonObject();
	      	        
	        World w = (World) Class.forName(world.get("type").getAsString()).newInstance();
	        Grid g = (Grid) Class.forName(world.get("grid").getAsJsonObject().get("type").getAsString()).newInstance();
	        g.deserialize(world.get("grid").getAsJsonObject());
	        g.setParent(w);
	        
	        Tile[][] gr = new Tile[g.getHTileCount()][g.getVTileCount()];
	        JsonArray tiles = world.get("grid").getAsJsonObject().get("grid").getAsJsonArray();
	        
	        for(int y = 0; y < g.getHTileCount(); y++)
	        {
	        	for(int x = 0; x < g.getVTileCount(); x++)
		        {
	        		gr[y][x] = (Tile) Class.forName(tiles.get(y).getAsJsonArray().get(x).getAsJsonObject().get("type").getAsString()).newInstance();
	        		gr[y][x].deserialize(tiles.get(y).getAsJsonArray().get(x).getAsJsonObject());
	        		gr[y][x].setParent(w);
		        }
	        }
	        
	        g.setGrid(gr);
	        
	       
	        
	        JsonArray ets = world.getAsJsonObject().get("entities").getAsJsonArray();
	        
	        List<IEntity> entities = new ArrayList<IEntity>(ets.size());
	        
	        for(int i = 0; i < ets.size(); i++)
	        {
	        	IEntity e = (IEntity) Class.forName(ets.get(i).getAsJsonObject().get("type").getAsString()).newInstance();	        	
	        	e.deserialize(ets.get(i).getAsJsonObject());	  
	        	e.setParent(w);
	        	entities.add(e);
	        }
	        
	
	        
	        
	        JsonArray pl = world.getAsJsonObject().get("player").getAsJsonArray();
	        
	        List<Player> player = new ArrayList<Player>(pl.size());
	        
	        for(int i = 0; i < pl.size(); i++)
	        {
	        	Player p = new Player();
	        	p.deserialize(pl.get(i).getAsJsonObject());
	        	player.add(p);
	        }
	        
	        
	        
	        Camera cam = new Camera();
	        
	        cam.deserialize(world.get("camera").getAsJsonObject());
	        
	        
	        w.deserialize(world.get("width").getAsInt(), world.get("height").getAsInt(), cam, player, g, entities);
	        
	        w.setForceSelectionRender(world.get("forceSelectionRender").getAsBoolean());
	        w.setNextId(world.get("nextId").getAsInt());
	        w.setRenderGrid(world.get("renderGrid").getAsBoolean());
	        w.setRenderGridOccupiedView(world.get("renderGridOccupiedView").getAsBoolean());
	        return w;
	        
	        
	    } catch (Exception e) {
	    	Logger.log(e);
	    }
	    
	    finally {
	        try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Logger.log(e);
			}
	    }
	    
	    return null;
	}
	
	private void setPlayer(List<Player> player) {
		this.player = player;
		
	}

	private void setEntites(List<IEntity> entities) {
		this.entities = entities;		
	}

	public String serialize()
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(this);		
	}
	
	public void serialize(String path)
	{
		this.serialize(new File(path));
	}
	
	public void serialize(File f)
	{
		
		BufferedWriter writer = null;
		try
		{
			writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new GZIPOutputStream(new FileOutputStream(f))
                        ));//new BufferedWriter(new FileWriter(f, true));
			writer.write(serialize());
			
		}
		catch(Exception e)
		{	
			Logger.log(e);			
		}
		finally
		{
			try {
				writer.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				Logger.log(e1);
			}
		}
	}

	public int getPlayerCount() {
		return this.player.size();
	}
	
	public List<Player> getAllPlayer()
	{
		return this.player;
	}

	public void onPackage(Package p) {
		// TODO Auto-generated method stub
		
	}

	public void removePlayer(int id) {
		
		for(int i = 0; i < this.player.size(); i++)
		{
			if(this.player.get(i).getPlayerId() == id)
			{
				this.player.remove(i);
				break;
			}
		}
			
		//Iterator<IEntity> i = this.entities.iterator();
		//while (i.hasNext()) {
		//   IEntity s = i.next();
		   
		   
		//  i.remove();
		//}
		
	}
}

package cdg.nut.util.game;

import java.util.ArrayList;
import java.util.List;

import cdg.nut.interfaces.IEntity;

public class World {

	int nextId = 1; //TODO: use global id's frame and world...
	
	private List<IEntity> entities;
	
	private Camera camera;
	
	private int width;
	private int height;
	
	private Grid grid;
	
	private int skippedTiles;
	private int drawnTiles;
	
	private int skippedEntities;
	private int drawnEntities;
	
	private boolean renderGridOccupiedView = false;
	private boolean renderGrid = false;
	private boolean forceSelectionRender = false;
	
	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	public World(int width, int height)
	{
		this.entities = new ArrayList<IEntity>();
		this.setCamera(new Camera());
		
		this.width = width;
		this.height = height;
		
		this.grid = this.initGrid(width, height);
		
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
}

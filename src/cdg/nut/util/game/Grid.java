package cdg.nut.util.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import cdg.nut.interfaces.IGuiObject;
import cdg.nut.interfaces.IPolygonGenerator;
import cdg.nut.logging.Logger;
import cdg.nut.util.Matrix4x4;
import cdg.nut.util.Vertex3;
import cdg.nut.util.Vertex4;
import cdg.nut.util.VertexData;
import cdg.nut.util.enums.MatrixTypes;
import cdg.nut.util.gl.GLPolygon;
import cdg.nut.util.settings.SetKeys;

public class Grid extends GLPolygon implements IPolygonGenerator {
	
	private World parent;
	
	private Tile[][] grid;
	
	private int width;
	private int height;
	
	public  Grid(int width, int height, World parent)
	{
		super();
		this.width = width;
		this.height = height;
		this.parent = parent;
		
		super.setGen(this);
		
		this.setClipping(false);
		this.setShader(Entity2D.DEFAULT_SHADER);
		
		
		
		
		this.grid = initTiles(width, height);
	}
	
	public Tile[][] initTiles(int width, int height)
	{
		Tile[][] grid = new Tile[height][width];
		
		for(int h = 0; h < this.height; h++)
		{
			for(int w = 0; w < this.width; w++)
			{
				grid[h][w] = new Tile(w * 0.1f, h * -0.1f -0.1f, h * width + w +1, w, h);
				grid[h][w].setParent(this.parent);
			}
		}
		
		return grid;
		
		
	}

	@Override
	public VertexData[] generateData(float x, float y, float w, float h) {
		// TODO Auto-generated method stub
		
		
		List<Vertex4> pOL = new ArrayList<Vertex4>();
		
		
		//r: row
		//c: column
		
		for(int r = 0; r < width + 1; r++)
		{
			pOL.add(new Vertex4(0.0f,r * -0.1f, 0.0f, 1.0f));
			pOL.add(new Vertex4((this.width) * 0.1f,r * -0.1f, 0.0f, 1.0f));
		}
		
		for(int c = 0; c < height + 1; c++)
		{
			pOL.add(new Vertex4(c * 0.1f, 0.0f, 0.0f, 1.0f));
			pOL.add(new Vertex4(c * 0.1f, (height) * -0.1f, 0.0f, 1.0f));
		}
		
		VertexData[] data = new VertexData[pOL.size()];
		
		for(int i = 0; i < pOL.size(); i++)
		{
			data[i] = new VertexData(pOL.get(i));
		}
		
		return data;
	}

	@Override
	public int[] generateIndicies() {
		
		int lc = this.width + this.height + 2;
		
		int[] lineIndicies = new int[lc*2];
		
		for(int i = 0; i < lc; i++)
		{
			lineIndicies[i * 2] = i * 2;
			lineIndicies[i * 2 + 1] = i * 2 + 1;
		}
		
		
		return lineIndicies;
	}
	
	@Override
	protected void draw(boolean selection)
	{
		
		this.getShader().bind();
		this.getShader().passVertex4("clippingArea", this.isClipping()&&this.getClippingArea()!=null?this.getClippingArea():Vertex4.ORIGIN);
		
		this.getShader().passMatrix(Matrix4x4.getIdentity(), MatrixTypes.TRANSLATION);
		this.getShader().passMatrix(Matrix4x4.getIdentity(), MatrixTypes.ROTATION);
		this.getShader().passMatrix(Matrix4x4.getIdentity(), MatrixTypes.SCALING);
		
		this.getShader().passMatrix(SetKeys.WIN_MATRIX.getValue(Matrix4x4.class), MatrixTypes.WINDOW);
		if(this.parent != null) this.getShader().passMatrix(this.parent.getCamera().getMatrix(), MatrixTypes.CAMERA);
		if(this.parent != null) this.getShader().passMatrix(this.parent.getCamera().getRotMatrix(), MatrixTypes.CAMERA_ROTATION);
		
		for(int h = 0; h < width; h++)
		{
			for(int w = 0; w < width; w++)
			{
				this.grid[h][w].draw();
			}
		}
		
		
		
		if(this.getVAO() == -1 || this.isHidden()|| (selection&&!this.isSelectable()) || this.parent == null || !this.parent.isRenderGrid())
			return;
		
		this.drawing = true;
		
		
		if(!selection)
		{
			this.bindTextures();
			if(this.getImage() != null) this.getImage().bind();
		}
		
		
		
		this.getShader().bind();
		
		GL30.glBindVertexArray(this.getVAO()); //point the pointers to the right point.
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
				
		//tell the GPU where to find information about drawing this GLObject
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.getIVBO());
		
		
		this.passUniforms(); 

		
		if(this.getColor() != null) this.getShader().passColor("color", this.getColor() );
		this.getShader().pass1i("noTexture", 1);
		//this.mainShader.pass4f("visible_Area",this.visibleArea.getX(),this.visibleArea.getY(),this.visibleArea.getZ(),this.visibleArea.getW());
		// Draw the vertices
		GL11.glDrawElements(GL11.GL_LINES, this.getICount(), GL11.GL_UNSIGNED_INT, 0); //finallay draw
				
		//reset everything (undpoint pointing pointers... ;) )
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0); 
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		
		
		if(!selection)
		{
			this.unbindTextures();
			if(this.getImage() != null) this.getImage().unbind();
		}
		
		this.getShader().unbind();
		
		this.drawChildren(selection);
		
		this.drawing = false;
		
		//Logger.debug("Line count: "+(this.width + this.height + 2));
		//Logger.debug("Line in: "+Arrays.toString(this.generateIndicies()));
	}

	public World getParent() {
		return parent;
	}

	public void setParent(World parent) {
		this.parent = parent;
	}
	
	public boolean checkId(int id)
	{
		boolean found = false;
		
		for(int h = 0; h < this.height; h++)
		{
			for(int w = 0; w < this.width; w++)
			{
				if(this.grid[h][w].getId() == id && !this.grid[h][w].isSelected())
				{
					this.grid[h][w].selected();
					found = true;
				}
				else if(this.grid[h][w].getId() != id && this.grid[h][w].isSelected())
				{
					this.grid[h][w].unselected();
				}
			}
		}
		
		return found;
	}
	
	
	
	@Override
	public void drawSelect()
	{
		this.getShader().bind();
		this.getShader().pass1i("noTexture", 1);
		this.getShader().passVertex4("clippingArea", this.isClipping()&&this.getClippingArea()!=null?this.getClippingArea():new Vertex4(0.0f,0.0f,0.0f,0.0f));
		
		this.getShader().passMatrix(Matrix4x4.getIdentity(), MatrixTypes.TRANSLATION);
		this.getShader().passMatrix(Matrix4x4.getIdentity(), MatrixTypes.ROTATION);
		this.getShader().passMatrix(Matrix4x4.getIdentity(), MatrixTypes.SCALING);
		
		this.getShader().passMatrix(SetKeys.WIN_MATRIX.getValue(Matrix4x4.class), MatrixTypes.WINDOW);
		if(this.parent != null) this.getShader().passMatrix(this.parent.getCamera().getMatrix(), MatrixTypes.CAMERA);
		if(this.parent != null) this.getShader().passMatrix(this.parent.getCamera().getRotMatrix(), MatrixTypes.CAMERA_ROTATION);
		
		for(int h = 0; h < this.height; h++)
		{
			for(int w = 0; w < this.width; w++)
			{
				this.grid[h][w].drawSelect();
			}
		}
	}
	
	public Tile getTile(int x, int y)
	{
		return this.grid[Math.abs(y)][Math.abs(x)];
	}
	
	public boolean checkTileSurroundings(int x, int y, int nx, int px, int ny, int py) 
	{
		boolean allgood = true;
		
		//Logger.debug("x: "+x+"| y: "+y+"| nx: "+nx+"| px: "+px+"| ny: "+ny+"| py: "+py);
		//Logger.debug("width: "+width+" | height: "+height);
		for(int w = x-nx; w <= x + px; w++)
		{
			for(int h = y-ny; h <= y + py; h++)
			{
				/*try
				{Logger.debug("Tile ("+w+"/"+h+"): w < 0: "+(w < 0)+" | w > = width: "+(w >= this.width)+" | h < 0: "+(h < 0)+" | h > = height: "+(h >= this.height)+" | occupied: "+getTile(w,h).isOccupied());}
						catch(Exception e){}*/
				if(w < 0 || w >= this.width || h < 0 || y >= this.height || getTile(w,h).isOccupied())
					allgood = false;
			}
		}
		
		return  allgood;
	}
	
	public Tile[] getTileSurroundings(int x, int y, int nx, int px, int ny, int py) 
	{
		List<Tile> temp = new ArrayList<Tile>((nx + px + 1) * (ny + py +1));
		
		for(int w = x-nx; w <= x + px; w++)
		{
			for(int h = y-ny; h <= y + py; h++)
			{
				if(w >= 0 && w < this.width && h >= 0 && y < this.height)
					temp.add(getTile(w, h));
			}
		}
		
		return  temp.toArray(new Tile[1]);
	}
	
	public Tile[] getTileSurroundings(int x, int y)
	{
		List<Tile> temp = new ArrayList<Tile>(8);
		
		if(x - 1 >= 0)
		{
			temp.add(this.getTile(x - 1, y));
			
			if(y - 1 >= 0)
				temp.add(this.getTile(x - 1, y - 1));
			if(y + 1 < this.width)
				temp.add(this.getTile(x - 1, y + 1));		
		}
		
		if(x + 1 < this.width)
		{
			temp.add(this.getTile(x + 1, y));
			
			if(y - 1 >= 0)
				temp.add(this.getTile(x + 1, y - 1));
			if(y + 1 < this.width)
				temp.add(this.getTile(x + 1, y + 1));		
		}
		
		if(y - 1 >= 0)
			temp.add(this.getTile(x, y - 1));
		if(y + 1 < this.width)
			temp.add(this.getTile(x, y + 1));	
		
		return temp.toArray(new Tile[1]);
	}
	
	public Tile getTile(float x, float y)
	{

		int xt = (int) Math.abs(x / 0.1f);
		int yt = (int) Math.abs(y / 0.1f);
		
		
		if (xt > width -1)
			xt = width -1;
		
		if (yt > height -1)
			yt = height -1;
		//Logger.debug("dat x: "+x+"| dat y: "+y+" ergo: ("+xt+"/"+yt+")");
		return this.grid[yt][xt];
	}
	
	public IGuiObject getTileAsIGuiObject(int x, int y)
	{
		return this.grid[y][x];
	}
	
	public Tile getTile(int id)
	{
		for(int h = 0; h < this.height; h++)
		{
			for(int w = 0; w < this.width; w++)
			{
				if(this.grid[h][w].getId() == id)
					return this.grid[h][w];
			}
		}
		
		return null;
	}
	
	public IGuiObject getTileAsIGuiObject(int id)
	{
		for(int h = 0; h < this.height; h++)
		{
			for(int w = 0; w < this.width; w++)
			{
				if(this.grid[h][w].getId() == id)
					return (IGuiObject)this.grid[h][w];
			}
		}
		
		return null;
	}

	public int getTileCount() {
		return this.width * this.height;
	}

	public int getHTileCount() {
		// TODO Auto-generated method stub
		return this.height;
	}

	public int getVTileCount() {
		// TODO Auto-generated method stub
		return this.width;
	}
}

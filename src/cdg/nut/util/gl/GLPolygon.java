package cdg.nut.util.gl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import cdg.nut.interfaces.IPolygonGenerator;
import cdg.nut.interfaces.ISelectable;
import cdg.nut.logging.Logger;
import cdg.nut.util.DefaultShader;
import cdg.nut.util.ShaderProgram;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex2;
import cdg.nut.util.Vertex4;
import cdg.nut.util.VertexData;
import cdg.nut.util.enums.Colors;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class GLPolygon implements ISelectable {
	
	private float x;
	private float y;
	
	private float width;
	private float height;
	
	private int edgeCount = -1;
	
	private boolean centerRef;
	
	private int[] indices;	
	private VertexData[] data;
	private Vertex4[] points;
	
	private float xrad;
	private float yrad;
	private int iCount;
	private int VAO = -1;
	private int VBO = -1;
	private int iVBO = -1;
	private boolean drawing = false;
	private boolean selectable = true;
	private boolean hidden;
	private ShaderProgram shader = DefaultShader.simple;;
	private int id;
	private boolean selected = false;
	private boolean autoClipping = true;
	private boolean clipping = false;
	private boolean scaleWithBoundingBox = true;
	private Vertex4 clippingArea;
	private GLColor idColor;
	private int bs = -1;
	
	private IPolygonGenerator gen;
	
	private GLTexture image;
	private GLColor color = Colors.WHITE.getGlColor();
	
	protected GLPolygon(){}
	
	protected void load(float x, float y, float width, float height, IPolygonGenerator gen)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.data = gen.generateData(x, y, width, height); 
		this.indices = gen.generateIndicies();
		this.centerRef = false;
		this.gen = gen;
		if(this.data != null) this.points = Utility.extractPoints(this.data);
		
		this.setupGL();
	}
	
	protected void load(int x, int y, int width, int height, IPolygonGenerator gen)
	{
		float[] tpos = Utility.pixelToGL(x, y);
		float[] tsize = Utility.pixelSizeToGLSize(width, height);
		this.x = tpos[0];
		this.y = tpos[1];
		this.width = tsize[0];
		this.height = tsize[1];
		this.data = gen.generateData(x, y, width, height); 
		this.indices = gen.generateIndicies();
		this.centerRef = false;
		this.gen = gen;
		if(this.data.length == 0) this.points = Utility.extractPoints(this.data);
		
		this.setupGL();
	}
	
	public GLPolygon(int x, int y, int width, int height, IPolygonGenerator gen)
	{
		float[] tpos = Utility.pixelToGL(x, y);
		float[] tsize = Utility.pixelSizeToGLSize(width, height);
		this.x = tpos[0];
		this.y = tpos[1];
		this.width = tsize[0];
		this.height = tsize[1];
		this.data = gen.generateData(x, y, width, height); 
		this.indices = gen.generateIndicies();
		this.centerRef = false;
		this.gen = gen;
		if(this.data != null) this.points = Utility.extractPoints(this.data);
		
		this.setupGL();
	}
		
	public GLPolygon(int x, int y, int width, int height)
	{
		this(x,y,width,height,0, false);
	}
	
	public GLPolygon(int x, int y, int width, int height, int id)
	{
		this(x,y,width,height,id, false);
	}
	
	public GLPolygon(int x, int y, int width, int height, int id, boolean cref)
	{
		float[] tpos = Utility.pixelToGL(x, y);
		float[] tsize = Utility.pixelSizeToGLSize(width, height);
		this.x = tpos[0];
		this.y = tpos[1];
		this.width = tsize[0];
		this.height = tsize[1];
		this.centerRef = cref;
		this.edgeCount = 4;
		this.id = id;
		this.idColor = new GLColor(id);
		
		this.createQuad();
		this.setupGL();
	}
	
	public GLPolygon(int x, int y, int width, int height, int id, boolean cref, int bs)
	{
		float[] tpos = Utility.pixelToGL(x, y);
		float[] tsize = Utility.pixelSizeToGLSize(width, height);
		this.x = tpos[0];
		this.y = tpos[1];
		this.width = tsize[0];
		this.height = tsize[1];
		this.centerRef = cref;
		this.edgeCount = 4;
		this.id = id;
		this.idColor = new GLColor(id);
		this.bs = bs;
		
		this.createBorderQuad();
		this.setupGL();
	}
	
	public GLPolygon(int x, int y, int width, int height, boolean cref, int edgeCount)
	{
		this(x,y,width,height,cref,(float)Math.min(height, width)/2.0f,(float)Math.min(height, width)/2.0f,edgeCount,0);
	}
	
	public GLPolygon(int x, int y, int width, int height, boolean cref, float radius, int edgeCount)
	{
		this(x,y,width,height,cref,radius,radius,edgeCount,0);
	}
	
	public GLPolygon(int x, int y, int width, int height, boolean cref, float radius, int edgeCount, int id)
	{
		this(x,y,width,height,cref,radius,radius,edgeCount,id);
	}
	
	public GLPolygon(int x, int y, int width, int height, boolean cref, float xrad, float yrad, int edgeCount, int id)
	{
		float[] tpos = Utility.pixelToGL(x, y);
		float[] tsize = Utility.pixelSizeToGLSize(width, height);
		this.x = tpos[0];
		this.y = tpos[1];
		this.width = tsize[0];
		this.height = tsize[1];
		this.centerRef = cref;
		this.xrad = xrad;
		this.yrad = yrad;
		this.edgeCount = edgeCount;
		this.id = id;
		this.idColor = new GLColor(id);
		
		this.generateCrefPoly();
		this.setupGL();
	}
	
	public GLPolygon(int x, int y, int width, int height, boolean cref, float xrad, float yrad, int edgeCount, int id, int bs)
	{
		float[] tpos = Utility.pixelToGL(x, y);
		float[] tsize = Utility.pixelSizeToGLSize(width, height);
		this.x = tpos[0];
		this.y = tpos[1];
		this.width = tsize[0];
		this.height = tsize[1];
		this.centerRef = cref;
		this.xrad = xrad;
		this.yrad = yrad;
		this.edgeCount = edgeCount;
		this.id = id;
		this.idColor = new GLColor(id);
		this.bs = bs;
		
		this.generateBorderCrefPoly();
		this.setupGL();
	}
	
	public GLPolygon(float x, float y, float width, float height,
			int id, boolean b, int bs) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.centerRef = b;
		this.edgeCount = 4;
		this.id = id;
		this.idColor = new GLColor(id);
		this.bs = bs;
		
		this.createBorderQuad();
		this.setupGL();
	}

	public GLPolygon(float x, float y, float width, float height) {
		this(x,y,width,height,0,false);
	}
	
	public GLPolygon(float x, float y, float width, float height, int id)
	{
		this(x,y,width,height,id, false);
	}
	
	public GLPolygon(float x, float y, float width, float height, int id, boolean cref)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.centerRef = cref;
		this.edgeCount = 4;
		this.id = id;
		this.idColor = new GLColor(id);
		
		this.createQuad();
		this.setupGL();
	}

	/**
	 * @return the bs
	 */
	public int getBordersize() {
		return bs;
	}

	/**
	 * @param bs the bs to set
	 */
	public void setBordersize(int bs) {
		this.bs = bs;
		if(this.bs != -1)
		{
			if(this.edgeCount != 4)
				this.generateBorderCrefPoly();
			else
				this.createBorderQuad();
		}
		else
		{
			if(this.edgeCount != 4)
				this.generateCrefPoly();
			else
				this.createQuad();
		}
		
		this.setupGL();
	}

	private void createQuad()
	{
		this.points = Utility.generateQuadPoints(this.x, this.y, this.width, this.height);		
		Vertex2[] st = Utility.generateSTPoints(1.0f, 0.0f, -1.0f, 1.0f);
		this.data = Utility.generateQuadData(this.points, this.idColor, st);
		this.indices = Utility.createQuadIndicesInt(1);
	}
	
	private void createBorderQuad()
	{
		float sx = Utility.pixelSizeToGLSize(bs, 0)[0];
		float sy = Utility.pixelSizeToGLSize(0, bs)[1];
		ArrayList<VertexData> res = new ArrayList<VertexData>(16);
		
		Vertex4[][] p = new Vertex4[4][4];
		
		p[0] = Utility.generateQuadPoints(x, y, width, sy);
		p[1] = Utility.generateQuadPoints(x+(width-sx), y+sy, sx, height-2*sy);
		p[2] = Utility.generateQuadPoints(x, y+(height-sy), width, sy);
		p[3] = Utility.generateQuadPoints(x, y+sy, sx, height-2*sy);
		
		this.points = new Vertex4[16];
		for(int i1 = 0; i1 < 4; i1++)
		{
			for(int i2 = 0; i2 < 4; i2++)
			{
				points[i1*4+i2] = p[i1][i2];
			}
		}
		
		this.data = new VertexData[this.points.length];
		for(int i = 0; i < this.points.length; i++)
		{
			data[i] = new VertexData(this.points[i], this.idColor);
		}
		
		this.indices = Utility.createQuadIndicesInt(4);
		
		/*
		addAll(res, Utility.generateQuadData(x, y, width, sy, c)); //north
		addAll(res, Utility.generateQuadData(x+(width-sx), y+sy, sx, height-2*sy, c)); //east
		addAll(res, Utility.generateQuadData(x, y+(height-sy), width, sy, c)); //south
		addAll(res, Utility.generateQuadData(x, y+sy, sx, height-2*sy, c)); //west*/
	}
	
	
	
	private void generateCrefPoly()
	{
		//float radius = (float)Math.min(this.getPixelHeight(), this.getFO().getPixelHeight())/2.0f;
		//float s = radius <= 10?Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class)/2:Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class);
		
		float alpha = Utility.rad(360.0f/this.edgeCount);
		Vertex2 center = new Vertex2(this.getPixelX()+this.xrad, this.getPixelY()+this.yrad);
		List<Vertex4> points = new ArrayList<Vertex4>((this.edgeCount)*2);
		float rad = alpha/2.0f;
		
		points.add(Utility.toGL(new Vertex4(center)));
		
		//radius -= 2*s;
		
		for(int i = 0; i < this.edgeCount; i++)
		{
			float px = center.getX()+(float) (Math.cos(rad) * xrad);
			float py = center.getY()+(float) (Math.sin(rad) * yrad);
			
			points.add(Utility.toGL(new Vertex4(px,py)));
			
			Logger.debug("P: "+i+" / c: "+this.edgeCount+" / deg: "+rad+" / px: "+px+" / py: "+py+" / cx: "+center.getX()+" / cy: "+center.getY()+" / xr: "+xrad+" / yr: "+yrad, "Polygon.generateCrefPoly");
			
			rad+=alpha;
		}
		
		this.data = new VertexData[points.size()];
		for(int i = 0; i < points.size(); i++)
		{
			data[i] = new VertexData(points.get(i), this.idColor);
		}
				
		this.indices = new int[this.edgeCount*3];
		for(int i = 0; i < this.edgeCount; i++)
		{
			
			if(i+1 < this.edgeCount)
			{
				this.indices[i*3+0] = i+1;
				this.indices[i*3+1] = i+2;
				this.indices[i*3+2] = 0;
			}
			else
			{
				this.indices[i*3+0] = i+1;
				this.indices[i*3+1] = 1;
				this.indices[i*3+2] = 0;
			}
		}
		
		this.points = points.toArray(new Vertex4[1]);
		
	}
	
	private void generateBorderCrefPoly()
	{
		//float radius = (float)Math.min(this.getPixelHeight(), this.getFO().getPixelHeight())/2.0f;
		//float s = radius <= 10?Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class)/2:Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class);
		
		float alpha = Utility.rad(360.0f/this.edgeCount);
		Vertex2 center = new Vertex2(this.getPixelX()+this.xrad, this.getPixelY()+this.yrad);
		List<Vertex4> points = new ArrayList<Vertex4>((this.edgeCount)*2);
		float rad = alpha/2.0f;
		
		//points.add(Utility.toGL(new Vertex4(center)));
		
		float xrad = this.xrad-this.bs;
		float yrad = this.yrad-this.bs;
		
		for(int i = 0; i < this.edgeCount; i++)
		{
			float px = center.getX()+(float) (Math.cos(rad) * this.xrad);
			float py = center.getY()+(float) (Math.sin(rad) * this.yrad);
			
			float plx = center.getX()+(float) (Math.cos(rad) * xrad);
			float ply = center.getY()+(float) (Math.sin(rad) * yrad);
			
			points.add(Utility.toGL(new Vertex4(px,py)));
			points.add(Utility.toGL(new Vertex4(plx,ply)));
			
			Logger.debug("P: "+i+" / c: "+this.edgeCount+" / deg: "+rad+" / px: "+px+" / py: "+py+" / cx: "+center.getX()+" / cy: "+center.getY()+" / xr: "+xrad+" / yr: "+yrad, "Polygon.generateCrefPoly");
			
			rad+=alpha;
		}
		
		this.data = new VertexData[points.size()];
		for(int i = 0; i < points.size(); i++)
		{
			data[i] = new VertexData(points.get(i), this.idColor);
		}
				
		this.indices = new int[this.edgeCount*6];
		for(int i = 0; i < this.edgeCount; i++)
		{
			
			if(i+1 < this.edgeCount)
			{
				this.indices[i*6+0] = i*2+0;
				this.indices[i*6+1] = i*2+1;
				this.indices[i*6+2] = i*2+2;
				this.indices[i*6+3] = i*2+2;
				this.indices[i*6+4] = i*2+3;
				this.indices[i*6+5] = i*2+1;
			}
			else
			{
				this.indices[i*6+0] = i*2+0;
				this.indices[i*6+1] = i*2+1;
				this.indices[i*6+2] = 0;
				this.indices[i*6+3] = 0;
				this.indices[i*6+4] = 1;
				this.indices[i*6+5] = i*2+1;
			}
		}
		
		this.points = points.toArray(new Vertex4[1]);
		
	}
	
	
	/**
	 * @return the x
	 */
	public float getX() {
		return x;
	}
	/**
	 * @return the y
	 */
	public float getY() {
		return y;
	}
	/**
	 * @return the width
	 */
	public float getWidth() {
		return width;
	}
	/**
	 * @return the height
	 */
	public float getHeight() {
		return height;
	}
	/**
	 * @return the edgeCount
	 */
	public int getEdgeCount() {
		return edgeCount;
	}
	/**
	 * @return the centerRef
	 */
	public boolean isCenterRef() {
		return centerRef;
	}
	/**
	 * @return the indices
	 */
	public int[] getIndices() {
		return indices;
	}
	/**
	 * @return the data
	 */
	public VertexData[] getData() {
		return data;
	}
	/**
	 * @return the points
	 */
	public Vertex4[] getPoints() {
		return points;
	}
	/**
	 * @return the xrad
	 */
	public float getXrad() {
		return xrad;
	}
	/**
	 * @return the yrad
	 */
	public float getYrad() {
		return yrad;
	}
	
	
	public final void setupGL()
	{
		if(this.data.length == 0)
			return;
		
		Logger.debug("datalength: "+data.length, "GLPolygon.setupGL");
		
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(this.data.length *
				VertexData.ELEMENT_COUNT);
		for (int i = 0; i < this.data.length; i++) {
			// Add position, color and texture floats to the buffer
			verticesBuffer.put(this.data[i].getElements());
		}
		verticesBuffer.flip();	
		
		this.iCount = this.indices.length;
		IntBuffer indicesBuffer = BufferUtils.createIntBuffer(iCount);
		indicesBuffer.put(indices);
		indicesBuffer.flip();
		
		// Create a new Vertex Array Object in memory and select it (bind)
		if(this.VAO == -1)
			VAO = GL30.glGenVertexArrays();			
		GL30.glBindVertexArray(VAO);
		
		// Create a new Vertex Buffer Object in memory and select it (bind)
		if(this.VBO == -1)
			VBO = GL15.glGenBuffers();
		
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
			
		
		// Put the position coordinates in attribute list 0
		GL20.glVertexAttribPointer(0, VertexData.POSITION_ELEMENT_COUNT, GL11.GL_FLOAT, 
				false, VertexData.STRIDE, VertexData.POSITION_BYTE_OFFSET);
		// Put the color components in attribute list 1
		GL20.glVertexAttribPointer(1, VertexData.COLOR_ELEMENT_COUNT, GL11.GL_FLOAT, 
				false, VertexData.STRIDE, VertexData.COLOR_BYTE_OFFSET);
		// Put the texture coordinates in attribute list 2
		GL20.glVertexAttribPointer(2, VertexData.COLOR_ELEMENT_COUNT, GL11.GL_FLOAT, 
				false, VertexData.STRIDE, VertexData.TEXTURE_BYTE_OFFSET);
			
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
				
		// Deselect (bind to 0) the VAO
		GL30.glBindVertexArray(0);
				
		// Create a new VBO for the indices and select it (bind) - INDICES
		if(this.iVBO == -1)
			this.iVBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.iVBO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		if(this.autoClipping) this.setupClippingArea();
	}
	
	
	private void updateId()
	{
		while(drawing) { } //don't change the VBO if we are currently drawing and wait until drawing has finished
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		
		for(int i = 0; i < points.length; i++)
		{
			//for every point that our GLObject has, pack the new id as a color in a float buffer
			FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(VertexData.COLOR_ELEMENT_COUNT);
			colorBuffer.put(Utility.idToGlColor(this.id, false));
			colorBuffer.rewind(); 
			
			//upload it to the GPU memory overriding the old value(s)
			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, i * VertexData.STRIDE + VertexData.COLOR_BYTE_OFFSET, colorBuffer);
		}
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); //unbind cause we are done
	}
	
	protected void move(float nx, float ny)
	{
		while(drawing) { } //don't change the VBO if we are currently drawing and wait until drawing has finished
		
		if(this.points != null && this.VAO != -1)
		{
		
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
			float xm = this.x - nx;
			float ym = this.y - ny;	
			
			//Logger.debug("moving: "+xm+"; "+ym+" / gl: "+this.points[0].getX()+"; "+this.points[0].getY(),"GLObject.move");
			//Logger.debug("npos: "+(this.points[0].getX()-xm)+"; "+(this.points[0].getY()-ym),"GLObject.move");
			
			for(int i = 0; i < points.length; i++)
			{
				float[] pos = new float[2];
				pos[0] = this.points[i].getX()-xm;
				pos[1] = this.points[i].getY()-ym;
				//for every point that our GLObject has, pack the new id as a color in a float buffer
				FloatBuffer posBuffer = BufferUtils.createFloatBuffer(2);
				posBuffer.put(pos);
				posBuffer.rewind(); 
				
				//upload it to the GPU memory overriding the old value(s)
				GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, i * VertexData.STRIDE + 0, posBuffer);
				this.points[i] = new Vertex4(pos[0], pos[1]);
			}
			
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); //unbind cause we are done
			
		}
		
		this.x = nx;
		this.y = ny;
		
		if(this.autoClipping) this.setupClippingArea();
	}
	
	//TODO: Javadoc
	protected void draw(boolean selection)
	{
		if(this.VAO == -1 || this.hidden|| (selection&&!this.selectable))
			return;
		
		this.drawing = true;
		
		this.shader.bind();
		
		if(!selection)
		{
			this.bindTextures();
			if(this.image != null) this.image.bind();
		}
		
		
		
		GL30.glBindVertexArray(this.VAO); //point the pointers to the right point.
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
				
		//tell the GPU where to find information about drawing this GLObject
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.iVBO);
		
		this.passUniforms(); //pass the unfiforms from the user
		this.shader.pass1i("selection", selection?1:0);
		this.shader.passVertex4("clippingArea", this.clipping&&this.clippingArea!=null?this.clippingArea:new Vertex4(0.0f,0.0f,0.0f,0.0f));
		if(this.color != null) this.getShader().passColor("color", this.color);
		//this.mainShader.pass4f("visible_Area",this.visibleArea.getX(),this.visibleArea.getY(),this.visibleArea.getZ(),this.visibleArea.getW());
		// Draw the vertices
		GL11.glDrawElements(GL11.GL_TRIANGLES, this.iCount, GL11.GL_UNSIGNED_INT, 0); //finallay draw
				
		//reset everything (undpoint pointing pointers... ;) )
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0); 
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		
		
		if(!selection)
		{
			this.unbindTextures();
			if(this.image != null) this.image.unbind();
		}
		
		this.shader.unbind();
		
		this.drawChildren(selection);
		
		this.drawing = false;
	}
	
	protected void bindTextures()
	{
		// nothing todo here, let the user decide if he wants to bind textures..
	}	
	
	protected void unbindTextures()
	{
		// nothing todo here, let the user decide if he wants to unbind textures..
	}
	
	protected void passUniforms()
	{
		// nothing todo here, let the user decide if he wants to pass uniforms..
	}
	
	protected void drawChildren(boolean selection)
	{
		// nothing todo here, let the user decide if he wants to unbind textures..
	}
	
	public int getPixelX() { return Utility.glToPixel(this.x, this.y)[0]; }

	public void setX(float x) {
		if(this.VAO != -1) this.move(x, this.y);
	}
	
	public void setX(int x) { float nx = Utility.pixelToGL(x, 0)[0]; this.move(nx, this.y);}
	
	public int getPixelY() { return Utility.glToPixel(this.x, this.y)[1]; }

	public void setY(float y) {
		//this.y = y;
		this.move(this.x, y);
	}
	
	public void setY(int y) { float ny = Utility.pixelToGL(0, y)[1]; this.move(this.x, ny);}
	
	public int getPixelWidth() {
		return Utility.glSizeToPixelSize(width, 0)[0];
	}
	
	public void setPosition(int x, int y)
	{
		
		
		
		float[] tmp = Utility.pixelToGL(x, y);
		//this.x = tmp[0];
		//this.y = tmp[1];
		
		//Logger.debug("moving to: "+x+"; "+y+" / gl: "+tmp[0]+"; "+tmp[1],"GLObject.setPosition");
		
		this.move(tmp[0], tmp[1]);
	}
	
	public void setPosition(float x, float y)
	{
		this.move(x,y);
	}

	protected void setWidthSupEvent(float width) {
		this.width = width;
		if(this.autoClipping) this.setupClippingArea();
	}
	
	public void setWidth(float width) {
		this.setDimension(width, this.height);
	}
	
	public int getPixelHeight() {
		return Utility.glSizeToPixelSize(0, height)[1];
	}
	
	protected void setHeightSupEvent(float height) {
		Logger.debug("new height: "+height,"GLPolygon.setHeightSupEvent");
		this.height = height;
		if(this.autoClipping) this.setupClippingArea();
	}
	
	public void setHeight(float height) {
		this.setDimension(this.width, height);
	}
	
	//TODO: Javadoc
	@Override
	public void draw() {
		this.draw(false);		
	}
	
	//TODO: Javadoc
	@Override
	public void drawSelect() {
		this.draw(true);		
	}

	//TODO: Javadoc
	@Override
	public int setId(int id) {
		this.id = id;	
		this.idColor = new GLColor(id);
		this.updateId();
		return 1;
	}

	//TODO: Javadoc
	@Override
	public int getId() {
		return this.id;
	}

	//TODO: Javadoc
	@Override
	public void setSelected(boolean selection) {
		this.selected = selection;		
	}

	//TODO: Javadoc
	@Override
	public boolean isSelected() {
		return this.selected;
	}
	
	@Override
	public boolean checkId(int id) {
		
		if(this.id == id)
			this.setSelected(true);
		else
			this.setSelected(false);
		
		return this.selected;
	}

	public ShaderProgram getShader() {
		return shader;
	}

	public void setShader(ShaderProgram shader) {
		this.shader = shader;
	}

	public boolean isHidden() {
		return this.hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public void hide()
	{
		this.hidden = true;
	}
	
	public void show()
	{
		this.hidden = false;
	}
	
	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}
	
	public void setDimension(int width, int height) {
		float dim[] = Utility.pixelSizeToGLSize(width, height);
		this.setDimension(dim[0], dim[1]);
	}
	
	public void setDimension(float width, float height) {
		this.height = height;
		this.width = width;
		

		if (this.gen != null)
		{
			while(drawing) { };
			if(this.scaleWithBoundingBox)
			{
				this.data = gen.generateData(x, y, width, height); 
				this.indices = gen.generateIndicies();
				if(this.data != null) this.points = Utility.extractPoints(this.data);
				this.setupGL();
			}
			
		}
		else if(this.edgeCount == 4)
		{
			while(drawing) { };
			if(this.scaleWithBoundingBox)
			{
				if(this.bs != -1)
					this.createBorderQuad();
				else
					this.createQuad();
				
				this.setupGL();
			}
			
			//this.setupGL(Utility.generateQuadData(this.points[0].getX(), this.points[0].getY(), width, height, new GLColor(this.id)), Utility.createQuadIndicesByte(4));
		}
		else
		{
			while(drawing) { };	
			if(this.scaleWithBoundingBox)
			{
				this.xrad = this.getPixelWidth()/2;
				this.yrad = this.getPixelHeight()/2;
				Logger.debug("xrad: "+xrad+" / yrad: "+yrad, "GLPolygon.setDimension");
				
				if(this.bs != -1)
					this.generateBorderCrefPoly();
				else
					this.generateCrefPoly();
				this.setupGL();
			}
		}
		
		if(this.autoClipping) this.setupClippingArea();
	}

	/**
	 * @return the gen
	 */
	public IPolygonGenerator getGen() {
		return gen;
	}

	/**
	 * @param gen the gen to set
	 */
	public void setGen(IPolygonGenerator gen) {
		this.gen = gen;
		
		this.data = gen.generateData(x, y, width, height); 
		this.indices = gen.generateIndicies();
		if(this.data != null) this.points = Utility.extractPoints(this.data);
		
		this.setupGL();
	}

	public boolean isClipping() {
		return clipping;
	}

	public void setClipping(boolean clipping) {
		this.clipping = clipping;
	}

	public boolean isAutoClipping() {
		return autoClipping;
	}

	public void setAutoClipping(boolean autoClipping) {
		this.autoClipping = autoClipping;
		if(this.autoClipping) this.setupClippingArea();
	}

	/**
	 * @param edgeCount the edgeCount to set
	 */
	public void setEdgeCount(int edgeCount) {
		this.edgeCount = edgeCount;
		this.setDimension(this.width, this.height);
	}

	public Vertex4 getClippingArea() {
		return clippingArea;
	}

	public void setClippingArea(Vertex4 clippingArea) {
		this.autoClipping = false;
		this.clippingArea = clippingArea;
		this.onClippingChange(clippingArea);
	}
	
	public void onClippingChange(Vertex4 c){}
	
	private void setupClippingArea()
	{
		this.clippingArea = new Vertex4(this.x, this.y, this.x+this.width, this.y+this.height);
		this.onClippingChange(new Vertex4(this.x, this.y, this.x+this.width, this.y+this.height));
	}

	public boolean isScaleWithBoundingBox() {
		return scaleWithBoundingBox;
	}

	public void setScaleWithBoundingBox(boolean scaleWithBoundingBox) {
		this.scaleWithBoundingBox = scaleWithBoundingBox;
	}

	public GLColor getColor() {
		return color;
	}

	public void setColor(GLColor color) {
		this.color = color;
	}

	public GLTexture getImage() {
		return image;
	}

	public void setImage(GLTexture image) {
		this.image = image;
	}

}

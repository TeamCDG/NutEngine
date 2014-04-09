package cdg.nut.util.gl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import cdg.nut.interfaces.IDrawable;
import cdg.nut.interfaces.ISelectable;
import cdg.nut.logging.Logger;
import cdg.nut.util.DefaultShader;
import cdg.nut.util.ShaderProgram;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex2;
import cdg.nut.util.Vertex4;
import cdg.nut.util.VertexData;


//TODO: implement moving, rotation and scaling
//TODO: Javadoc
public abstract class GLObject implements ISelectable {

	private int id;
	
	private float angle = 0;
	private Vertex4[] points;
	
	private float x;
	private float y;
	
	private float width;
	private float height;
	
	private boolean selected;
	private boolean selectable = true;

	private int iCount = -1;
	private int VAO = -1;
	private int VBO = -1;
	private int iVBO = -1;
	private ShaderProgram shader = DefaultShader.simple;
	private boolean intIndicies = false;
	private boolean noDraw = false;
	
	private boolean drawing = false;
	
	

	public GLObject(int width, int height)
	{
		this(0, 0.0f, 0.0f, Utility.pixelSizeToGLSize(width, height)[0], Utility.pixelSizeToGLSize(width, height)[1]);
	}
	
	public GLObject(int id, int width, int height)
	{
		this(id, 0.0f, 0.0f, Utility.pixelSizeToGLSize(width, height)[0], Utility.pixelSizeToGLSize(width, height)[1]);
	}
	
	public GLObject(int x, int y, int width, int height)
	{
		this(0, Utility.pixelToGL(x, y)[0], Utility.pixelToGL(x, y)[1], Utility.pixelSizeToGLSize(width, height)[0], Utility.pixelSizeToGLSize(width, height)[1]);
	}
	
	public GLObject(int id, int x, int y, int width, int height)
	{
		this(id, Utility.pixelToGL(x, y)[0], Utility.pixelToGL(x, y)[1], Utility.pixelSizeToGLSize(width, height)[0], Utility.pixelSizeToGLSize(width, height)[1]);
	}
	
	public GLObject(int id)
	{
		this(id, 0.0f, 0.0f, 1.0f, 1.0f);
	}
	
	public GLObject(float width, float height)
	{
		this(0, 0.0f, 0.0f, width, height);
	}
	
	public GLObject(int id, float width, float height)
	{
		this(id, 0.0f, 0.0f, width, height);
	}
	
	public GLObject(float x, float y, float width, float height)
	{
		this(0, x, y, width, height);
	}
	
	public GLObject(int id,float x, float y, float width, float height)
	{
		this.id = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.setupGL();
	}
	
	public GLObject(float width, float height, VertexData[] vertices, byte[] indices)
	{
		this.id = -1;
		this.x = vertices[0].getXYZ()[0];
		this.y = vertices[0].getXYZ()[1];
		this.width = width;
		this.height = height;
		this.points = Utility.extractPoints(vertices);
		this.setupGL(vertices, indices);
	}
	
	public GLObject(VertexData[] vertices, byte[] indices)
	{
		this.id = -1;
		this.x = vertices[0].getXYZ()[0];
		this.y = vertices[0].getXYZ()[1];
		this.points = Utility.extractPoints(vertices);
		this.setupGL(vertices, indices);
	}
	
	protected GLObject()
	{
		this.id = -1;
	}
	
	private void setupGL()
	{
		Logger.spam("A("+this.x+"/"+this.y+"); "+
					 "B("+this.x+"/"+(this.y+this.height)+"); "+
					 "C("+(this.x+this.width)+"/"+(this.y+this.height)+"); "+
					 "D("+(this.x+this.width)+"/"+this.y+"); ","GLObject.setupGL");
		
			this.points = Utility.generateQuadPoints(this.x, this.y, this.width, this.height);		
			Vertex2[] st = Utility.generateSTPoints(1.0f, 0.0f, -1.0f, 1.0f);
			VertexData[] vertices = Utility.generateQuadData(this.points, new GLColor(id), st);
			byte[] indices = Utility.createQuadIndicesByte(1);
			setupGL(vertices, indices);
			
	}
	
	protected final void setupGL(VertexData[] vertices, byte[] indices)
	{
		this.intIndicies = false;
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length *
				VertexData.ELEMENT_COUNT);
		for (int i = 0; i < vertices.length; i++) {
			// Add position, color and texture floats to the buffer
			verticesBuffer.put(vertices[i].getElements());
		}
		verticesBuffer.flip();	
		
		this.iCount = indices.length;
		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(iCount);
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
	}
	
	protected final void setupGL(VertexData[] vertices, int[] indices)
	{
		this.intIndicies = true;
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length *
				VertexData.ELEMENT_COUNT);
		for (int i = 0; i < vertices.length; i++) {
			// Add position, color and texture floats to the buffer
			verticesBuffer.put(vertices[i].getElements());
		}
		verticesBuffer.flip();	
		
		this.iCount = indices.length;
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
	
	protected void move()
	{
		while(drawing) { } //don't change the VBO if we are currently drawing and wait until drawing has finished
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		float xm = this.points[0].getX()-this.x;
		float ym = this.points[0].getY()-this.y;
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
		}
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); //unbind cause we are done
	}
	
	//TODO: Javadoc
	private void draw(boolean selection)
	{
		if(this.VAO == -1 || this.noDraw || (selection&&!this.selectable))
			return;
		
		this.drawing = true;
		
		this.shader.bind();
		
		if(!selection)
			this.bindTextures();
		
		GL30.glBindVertexArray(this.VAO); //point the pointers to the right point.
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
				
		//tell the GPU where to find information about drawing this GLObject
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.iVBO);
		
		this.passUniforms(); //pass the unfiforms from the user
		this.shader.pass1i("selection", selection?1:0);
		//this.mainShader.pass4f("visible_Area",this.visibleArea.getX(),this.visibleArea.getY(),this.visibleArea.getZ(),this.visibleArea.getW());
		// Draw the vertices
		GL11.glDrawElements(GL11.GL_TRIANGLES, this.iCount, this.intIndicies?GL11.GL_UNSIGNED_INT:GL11.GL_UNSIGNED_BYTE, 0); //finallay draw
				
		//reset everything (undpoint pointing pointers... ;) )
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0); 
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		
		
		if(!selection)
			this.unbindTextures();
		
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
	
	public float getX() { return x; }
	
	public int getPixelX() { return Utility.glToPixel(this.x, this.y)[0]; }

	public void setX(float x) {
		this.x = x;
		if(this.VAO != -1) this.move();
	}
	
	public void setX(int x) { this.x = Utility.pixelToGL(x, 0)[0]; this.move();}

	public float getY() { return y; }
	
	public int getPixelY() { return Utility.glToPixel(this.x, this.y)[1]; }

	public void setY(float y) {
		this.y = y;
		if(this.VAO != -1) this.move();
	}
	
	public void setY(int y) { this.y = Utility.pixelToGL(0, y)[1]; this.move();}

	public float getWidth() {
		return width;
	}
	
	public void setPosition(int x, int y)
	{
		float[] tmp = Utility.pixelToGL(x, y);
		this.x = tmp[0];
		this.y = tmp[1];
		if(this.VAO != -1) this.move();
	}
	
	public void setPosition(float x, float y)
	{
		this.x = x;
		this.y = y;
		if(this.VAO != -1) this.move();
	}

	protected void setWidthSupEvent(float width) {
		this.width = width;
	}
	
	public void setWidth(float width) {
		this.setDimension(width, this.height);
	}

	public float getHeight() {
		return height;
	}
	
	protected void setHeightSupEvent(float height) {
		this.height = height;
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
	public void setId(int id) {
		this.id = id;	
		this.updateId();
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

	public boolean isNoDraw() {
		return noDraw;
	}

	public void setNoDraw(boolean noDraw) {
		this.noDraw = noDraw;
	}

	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}
	
	public void setDimension(float width, float height) {
		this.height = height;
		this.width = width;
		
		if(this.points.length == 4)
		{
			while(drawing) { };
			
			this.setupGL(Utility.generateQuadData(this.points[0].getX(), this.points[0].getY(), width, height, new GLColor(this.id)), Utility.createQuadIndicesByte(4));
		}
	}
	
	

}

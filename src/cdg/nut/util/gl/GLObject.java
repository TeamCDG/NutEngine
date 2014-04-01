package cdg.nut.util.gl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import cdg.nut.interfaces.IDrawable;
import cdg.nut.interfaces.ISelectable;
import cdg.nut.util.ShaderProgram;
import cdg.nut.util.Utility;
import cdg.nut.util.VertexData;


//TODO: implement moving, rotation and scaling
//TODO: Javadoc
public abstract class GLObject implements ISelectable {

	private int id;
	
	private float x;
	private float y;
	
	private float width;
	private float height;
	
	private boolean selected;

	private int iCount;
	private int VAO;
	private int VBO;
	private int iVBO;	
	private ShaderProgram shader;
	
	

	public GLObject(int width, int height)
	{
		this(0, 0.0f, 0.0f, Utility.pixelToGL(width, height)[0], Utility.pixelToGL(width, height)[0]);
	}
	
	public GLObject(int id, int width, int height)
	{
		this(id, 0.0f, 0.0f, Utility.pixelToGL(width, height)[0], Utility.pixelToGL(width, height)[0]);
	}
	
	public GLObject(int x, int y, int width, int height)
	{
		this(0, Utility.pixelToGL(x, y)[0], Utility.pixelToGL(x, y)[1], Utility.pixelToGL(width, height)[0], Utility.pixelToGL(width, height)[0]);
	}
	
	public GLObject(int id, int x, int y, int width, int height)
	{
		this(id, Utility.pixelToGL(x, y)[0], Utility.pixelToGL(x, y)[1], Utility.pixelToGL(width, height)[0], Utility.pixelToGL(width, height)[0]);
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
		
	}
	
	private void setupGL()
	{
		VertexData[] points = new VertexData[]{
				   new VertexData(new float[]{0.0f,0.0f,0.0f,1.0f}, 
					   Utility.idToGlColor(this.id, false), new float[]{1.0f, 0.0f}),
					   
				   new VertexData(new float[]{0.0f,-this.height,0.0f,1.0f}, 
					   Utility.idToGlColor(this.id, false), new float[]{1.0f, 1.0f}),
					   
				   new VertexData(new float[]{this.width,-this.height,0.0f,1.0f}, 
					   Utility.idToGlColor(this.id, false), new float[]{0.0f, 1.0f}),
					   
				   new VertexData(new float[]{this.width,0.0f,0.0f,1.0f}, 
					   Utility.idToGlColor(this.id, false), new float[]{0.0f, 0.0f})};
			byte[] indices = {
				0, 1, 2,
				2, 3, 0
			};
			// Put each 'Vertex' in one FloatBuffer
			FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(points.length *
					VertexData.ELEMENT_COUNT);
			for (int i = 0; i < points.length; i++) {
				// Add position, color and texture floats to the buffer
				verticesBuffer.put(points[i].getElements());
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
	
	
	//TODO: Javadoc
	private void draw(boolean selection)
	{
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
		GL11.glDrawElements(GL11.GL_TRIANGLES, this.iCount, GL11.GL_UNSIGNED_BYTE, 0); //finallay draw
				
		//reset everything (undpoint pointing pointers... ;) )
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0); 
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		
		
		if(!selection)
			this.unbindTextures();
		
		this.shader.unbind();
	}
	
	public void bindTextures()
	{
		// nothing todo here, let the user decide if he wants to bind textures..
	}	
	
	public void unbindTextures()
	{
		// nothing todo here, let the user decide if he wants to unbind textures..
	}
	
	public void passUniforms()
	{
		// nothing todo here, let the user decide if he wants to pass uniforms..
	}
	
	
	
	public float getX() { return x; }
	
	public int getPixelX() { return Utility.glToPixel(this.x, this.y)[0]; }

	public void setX(float x) {
		this.x = x;
		//TODO: move
	}
	
	public void setX(int x) { this.x = Utility.pixelToGL(x, 0)[0]; }

	public float getY() { return y; }
	
	public int getPixelY() { return Utility.glToPixel(this.x, this.y)[1]; }

	public void setY(float y) {
		this.y = y;
		//TODO: move
	}
	
	public void setY(int y) { this.y = Utility.pixelToGL(0, y)[1]; }

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
		//TODO: actually change width
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
		//TODO: actually change height
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

	public ShaderProgram getShader() {
		return shader;
	}

	public void setShader(ShaderProgram shader) {
		this.shader = shader;
	}

}

package cdg.nut.util.gl;

import cdg.nut.interfaces.ISelectable;
import cdg.nut.util.ShaderProgram;
import cdg.nut.util.Vertex2;
import cdg.nut.util.Vertex3;
import cdg.nut.util.VertexData;

//TODO: finalize()
//TODO: Javadoc
public abstract class GLBase implements ISelectable{
	
	private boolean selectable = false;
	private int id;
	private int VBO;
	private int VAO;
	private int indiciesCount;
	private ShaderProgram shader;
	private boolean alignToWindow = true;
	
	public GLBase(float width, float height, ShaderProgram shader)
	{
		this(0, width, height, shader, false, true);
	}

	public GLBase(int id, float width, float height, ShaderProgram shader)
	{
		this(id, width, height, shader, false, true);
	}
	
	public GLBase(int id, float width, float height, ShaderProgram shader, boolean selectable)
	{
		this(id, width, height, shader, selectable, true);
	}
	
	public GLBase(int id, float width, float height, ShaderProgram shader, boolean selectable, boolean textured)
	{
		this(id, width, height, shader, selectable, textured, new Vertex3(0.0f, 0.0f, 0.0f));
	}
	
	public GLBase(int id, float width, float height, ShaderProgram shader, boolean selectable, boolean textured, Vertex3 center)
	{
		this(id, width, height, shader, selectable, textured, center, false);
	}
	
	public GLBase(int id, float width, float height, ShaderProgram shader, boolean selectable, boolean textured, Vertex3 center, boolean thirdDimension)
	{
		this.create(id, width, height, textured, center, thirdDimension);
		this.selectable = selectable;
		this.shader = shader;
	}

	public GLBase(Vertex3[] points, byte[] indicies, ShaderProgram shader)
	{
		this(0, points, null, indicies, shader, false);
	}
	
	public GLBase(int id, Vertex3[] points, byte[] indicies, ShaderProgram shader)
	{
		this(id, points, null, indicies, shader, false);
	}
	
	public GLBase(int id, Vertex3[] points, byte[] indicies, ShaderProgram shader, boolean selectable)
	{
		this(id, points, null, indicies, shader, selectable);
	}
	
	public GLBase(Vertex3[] points, Vertex2[] textureCoords, byte[] indicies, ShaderProgram shader)
	{
		this(0, points, textureCoords, indicies, shader, false);
	}
	
	public GLBase(int id, Vertex3[] points, Vertex2[] textureCoords, byte[] indicies, ShaderProgram shader)
	{
		this(id, points, textureCoords, indicies, shader, false);
	}
	
	public GLBase(int id, Vertex3[] points, Vertex2[] textureCoords, byte[] indicies, ShaderProgram shader, boolean selectable)
	{
		this.create(id, points, textureCoords, indicies);
		this.selectable = selectable;
		this.shader = shader;
	}
	
	public GLBase(int id, VertexData[] points, byte[] indicies, ShaderProgram shader, boolean selectable)
	{
		this.create(id, points, indicies);
		this.selectable = selectable;
		this.shader = shader;
	}
	
	
	
	
	
	private void create(int id, float width, float height, boolean textured,
			Vertex3 center, boolean thirdDimension) 
	{
		if(thirdDimension)
		{
			
		}
		else
		{
			Vertex3[] points = new Vertex3[]{
					new Vertex3(center.getX()+(0.5f*width), center.getY()+(0.5f*height), center.getZ()),
					new Vertex3(center.getX()+(0.5f*width), center.getY()-(0.5f*height), center.getZ()),
					new Vertex3(center.getX()-(0.5f*width), center.getY()-(0.5f*height), center.getZ()),
					new Vertex3(center.getX()-(0.5f*width), center.getY()+(0.5f*height), center.getZ())
			};
			
			Vertex2[] tex = new Vertex2[4];
			
			if(textured)
			{
				tex[0] = new Vertex2(0.0f, 0.0f);
				tex[1] = new Vertex2(0.0f, 1.0f);
				tex[2] = new Vertex2(1.0f, 1.0f);
				tex[3] = new Vertex2(1.0f, 0.0f);
			}
			else
			{
				tex[0] = new Vertex2(0.0f, 0.0f);
				tex[1] = new Vertex2(0.0f, 0.0f);
				tex[2] = new Vertex2(0.0f, 0.0f);
				tex[3] = new Vertex2(0.0f, 0.0f);
			}
			
			byte[] indicies = new byte[]{0,1,2,2,3,4,0};
			
			create(id, points, tex, indicies);			
		}
	}
	
	private void create(int id, Vertex3[] points, Vertex2[] textureCoords, byte[] indicies)
	{
		// TODO: create creation helper method
	}
	
	private void create(int id, VertexData[] data, byte[] indicies)
	{
		// TODO: create creation helper method
	}
	
	
	
	@Override
	public void draw() {
		// TODO let's draw
		
		
	}

	public void select() {
		
		if(!this.selectable)
			return;
		
	}


	public abstract void passShaderParams();
	
	/**
	 * @return the selectable
	 */
	public boolean isSelectable() {
		return selectable;
	}

	/**
	 * @param selectable the selectable to set
	 */
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	/**
	 * @return the vBO
	 */
	public int getVBO() {
		return VBO;
	}

	/**
	 * @param vBO the vBO to set
	 */
	public void setVBO(int vBO) {
		VBO = vBO;
	}

	/**
	 * @return the vAO
	 */
	public int getVAO() {
		return VAO;
	}

	/**
	 * @param vAO the vAO to set
	 */
	public void setVAO(int vAO) {
		VAO = vAO;
	}

	/**
	 * @return the indiciesCount
	 */
	public int getIndiciesCount() {
		return indiciesCount;
	}

	/**
	 * @param indiciesCount the indiciesCount to set
	 */
	public void setIndiciesCount(int indiciesCount) {
		this.indiciesCount = indiciesCount;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public int setId(int id) {
		this.id = id;
		return 1;
	}

	/**
	 * @return the shader
	 */
	public ShaderProgram getShader() {
		return shader;
	}

	/**
	 * @param shader the shader to set
	 */
	public void setShader(ShaderProgram shader) {
		this.shader = shader;
	}

	/**
	 * @return the alignToWindow
	 */
	public boolean isAlignToWindow() {
		return alignToWindow;
	}

	/**
	 * @param alignToWindow the alignToWindow to set
	 */
	public void setAlignToWindow(boolean alignToWindow) {
		this.alignToWindow = alignToWindow;
	}

	

}

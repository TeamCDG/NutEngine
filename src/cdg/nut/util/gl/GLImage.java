package cdg.nut.util.gl;

import cdg.nut.interfaces.IDrawable;
import cdg.nut.util.Colors;
import cdg.nut.util.Utility;
import cdg.nut.util.VertexData;

public class GLImage extends GLObject{

	private GLTexture image;
	private GLColor color = Colors.WHITE.getGlColor();
	
	//TODO: get/set image
	//TODO: get/set color
	
	//TODO: Javadoc
	public GLImage(GLTexture image, float width, float height)
	{
		this(null, image, 0.0f, 0.0f, width, height, -1);
	}
	
	//TODO: Javadoc
	public GLImage(GLColor color, float width, float height)
	{
		this(color, null, 0.0f, 0.0f, width, height, -1);
	}
	
	//TODO: Javadoc
	public GLImage(GLColor color, float x, float y, float width, float height)
	{
		this(color, null, x, y, width, height, -1);
	}
	
	//TODO: Javadoc
	public GLImage(GLTexture image, float x, float y, float width, float height)
	{
		this(null, image, x, y, width, height, -1);
	}
	//TODO: Javadoc
	public GLImage(float width, float height)
	{
		this(null, null, 0.0f, 0.0f, width, height, -1);
	}
	
	//TODO: Javadoc
	public GLImage(GLColor color, GLTexture image, float width, float height)
	{
		this(color, image, 0.0f, 0.0f, width, height, -1);
	}
	
	//TODO: Javadoc
	public GLImage(GLColor color, GLTexture image, float x, float y, float width, float height)
	{
		this(color, image, x, y, width, height, -1);
	}
	
	//TODO: Javadoc
	public GLImage(GLColor color, GLTexture image, float width, float height, int id)
	{
		this(color,  image, 0.0f, 0.0f, width, height, id);
	}
	
	public GLImage(GLColor color, GLTexture image, float x, float y, float width, float height, int id)
	{
		super(id, x, y, width, height);
		this.color = color;
		this.image = image;
	}
	
	public GLImage(GLColor color, float width, float height, VertexData[] vertices)
	{
		super(width, height, vertices, Utility.createQuadIndicesByte(vertices.length/4));
		this.color = color;
	}
	
	public GLImage(GLColor color, float width, float height, VertexData[] vertices, byte[] indices)
	{
		super(width, height, vertices, indices);
		this.color = color;
		
	}
	
	public GLImage(GLTexture image, float width, float height, VertexData[] vertices, byte[] indices)
	{
		super(width, height, vertices, indices);
		this.image = image;
	}
	
	public GLImage(GLColor color, GLTexture image, float width, float height, VertexData[] vertices, byte[] indices)
	{
		super(width, height, vertices, indices);
		this.color = color;
		this.image = image;
	}

	//TODO: Javadoc
	public GLImage(GLTexture image, int width, int height)
	{
		this(null, image, 0, 0, width, height, -1);
	}
	
	//TODO: Javadoc
	public GLImage(GLColor color, int width, int height)
	{
		this(color, null, 0, 0, width, height, -1);
	}
	
	public GLTexture getImage() {
		return image;
	}

	public void setImage(GLTexture image) {
		this.image = image;
	}

	public GLColor getColor() {
		return color;
	}

	public void setColor(GLColor color) {
		this.color = color;
	}

	//TODO: Javadoc
	public GLImage(GLColor color, int x, int y, int width, int height)
	{
		this(color, null, x, y, width, height, -1);
	}
	
	//TODO: Javadoc
	public GLImage(GLTexture image, int x, int y, int width, int height)
	{
		this(null, image, x, y, width, height, -1);
	}
	
	//TODO: Javadoc
	public GLImage(int width, int height)
	{
		this(null, null, 0, 0, width, height, -1);
	}
	
	//TODO: Javadoc
	public GLImage(GLColor color, GLTexture image, int width, int height)
	{
		this(color, image, 0, 0, width, height, -1);
	}
	
	//TODO: Javadoc
	public GLImage(GLColor color, GLTexture image, int x, int y, int width, int height)
	{
		this(color, image, x, y, width, height, -1);
	}
	
	//TODO: Javadoc
	public GLImage(GLColor color, GLTexture image, int width, int height, int id)
	{
		this(color,  image, 0, 0, width, height, id);
	}
	
	public GLImage(GLColor color, GLTexture image, int x, int y, int width, int height, int id)
	{
		super(id, x, y, width, height);
		this.color = color;
		this.image = image;
	}
	
	public GLImage(GLColor color, float width, float height,
			VertexData[] vertices, int[] indices) {
		super(width, height, vertices, indices);
		this.color = color;
	}

	@Override
	protected void passUniforms()
	{
		if(this.color != null) this.getShader().passColor("color", this.color);
	}
	
	@Override
	protected void bindTextures()
	{
		if(this.image != null) this.image.bind();
	}
	
	@Override
	protected void unbindTextures()
	{
		if(this.image != null) this.image.unbind();
	}
}

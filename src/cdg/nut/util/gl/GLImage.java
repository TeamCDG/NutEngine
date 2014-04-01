package cdg.nut.util.gl;

import cdg.nut.interfaces.IDrawable;

public class GLImage extends GLObject{

	GLTexture image;
	GLColor color;
	
	//TODO: get/set image
	//TODO: get/set color
	
	//TODO: Javadoc
	public GLImage(GLTexture image, float width, float height)
	{
		this(null, image, width, height, -1);
	}
	
	//TODO: Javadoc
	public GLImage(GLColor color, float width, float height)
	{
		this(color, null, width, height, -1);
	}
	
	//TODO: Javadoc
	public GLImage(float width, float height)
	{
		this(null, null, width, height, -1);
	}
	
	//TODO: Javadoc
	public GLImage(GLColor color, GLTexture image, float width, float height)
	{
		this(color, image, width, height, -1);
	}
	
	//TODO: Javadoc
	public GLImage(GLColor color, GLTexture image, float width, float height, int id)
	{
		super(id, width, height);
		this.color = color;
		this.image = image;
	}


}

package cdg.nut.util.gl;

import java.util.Random;

import cdg.nut.util.Utility;

public class GLColor {

	private float r;
	private float g;
	private float b;
	private float a;
	
	//TODO: Javadoc
	public GLColor(float[] color)
	{
		this(color[0], color[1], color[2], color[3]);
	}
	
	//TODO: Javadoc
	public GLColor(int id)
	{
		this(Utility.idToGlColor(id, false));
	}
	
	//TODO: Javadoc
	public GLColor(float r, float g, float b)
	{
		this(r,g,b,1.0f);
	}
	
	//TODO: Javadoc
	public GLColor(float r, float g, float b, float a)
	{
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		
	}
	
	public GLColor(int r, int g, int b, int a)
	{
		this.r = (1.0f/255.0f) * (float)r;
		this.g = (1.0f/255.0f) * (float)g;
		this.b = (1.0f/255.0f) * (float)b;
		this.a = (1.0f/255.0f) * (float)a;
		
	}
	
	//TODO: Javadoc
	public float[] toArray()
	{
		return new float[]{this.r,this.g,this.b,this.a};
	}
	
	//TODO: Javadoc
	public float getR()
	{
		return this.r;
	}
	
	//TODO: Javadoc
	public void setR(float r)
	{
		this.r = r;
	}
	
	//TODO: Javadoc
	public float getG()
	{
		return this.g;
	}
	
	//TODO: Javadoc
	public void setG(float g)
	{
		this.g = g;
	}
	
	//TODO: Javadoc
	public float getB()
	{
		return this.b;
	}
	
	//TODO: Javadoc
	public void setB(float b)
	{
		this.b = b;
	}
	
	//TODO: Javadoc
	public float getA()
	{
		return this.a;
	}
	
	public static final String TEXT_COLOR_IDENTIFIER = "\u0010";
	public static final String TEXT_COLOR_RESET = "\u0011";
	
	@Override
	public String toString() {
		return TEXT_COLOR_IDENTIFIER+((char)((int)(255.0f*this.r)))+((char)((int)(255.0f*this.g)))+((char)((int)(255.0f*this.b)))+((char)((int)(255.0f*this.a)));
	}

	//TODO: Javadoc
	public void setA(float a)
	{
		this.a = a;
	}
	
	public static GLColor random()
	{
		return random(false);
	}
	
	public static GLColor random(boolean alpha)
	{
		if(alpha)
			return new GLColor(new Random().nextFloat(),new Random().nextFloat(),new Random().nextFloat(),new Random().nextFloat());
		else
			return new GLColor(new Random().nextFloat(),new Random().nextFloat(),new Random().nextFloat());
	}

	public String toReadableString() {
		return this.r+"/"+this.g+"/"+this.b+"/"+this.a;
	}

}

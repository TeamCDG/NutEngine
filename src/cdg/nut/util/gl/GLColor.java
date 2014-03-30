package cdg.nut.util.gl;

import cdg.nut.util.Utility;

public class GLColor {

	private float r;
	private float g;
	private float b;
	private float a;
	
	
	public GLColor(float[] color)
	{
		this(color[0], color[1], color[2], color[3]);
	}
	
	public GLColor(int id)
	{
		this(Utility.idToGlColor(id, false));
	}
	
	public GLColor(float r, float g, float b)
	{
		this(r,g,b,1.0f);
	}
	
	public GLColor(float r, float g, float b, float a)
	{
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		
	}
	
	public float[] toArray()
	{
		return new float[]{this.r,this.g,this.b,this.a};
	}
	
	
	public float getR()
	{
		return this.r;
	}
	
	public void setR(float r)
	{
		this.r = r;
	}
	
	public float getG()
	{
		return this.g;
	}
	
	public void setG(float g)
	{
		this.g = g;
	}
	
	public float getB()
	{
		return this.b;
	}
	
	public void setB(float b)
	{
		this.b = b;
	}
	
	public float getA()
	{
		return this.a;
	}
	
	public void setA(float a)
	{
		this.a = a;
	}
	
	

}

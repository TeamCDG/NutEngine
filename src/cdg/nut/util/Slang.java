package cdg.nut.util;

public abstract class Slang {

	public static Vertex4 vec4(float x, float y, float z, float w)
	{
		return new Vertex4(x,y,z,w);
	}
	
	public static Vertex3 vec3(float x, float y, float z)
	{
		return new Vertex3(x,y,z);
	}
	
	public static Vertex2 vec2(float x, float y)
	{
		return new Vertex2(x,y);
	}
}

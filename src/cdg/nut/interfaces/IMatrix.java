package cdg.nut.interfaces;

import java.nio.FloatBuffer;

public interface IMatrix 
{
	public IVertex multiply(IVertex vertex);
	
	public float[] toArray();
	
	public void set(float x0, float y0, float z0, float w0,
			 float x1, float y1, float z1, float w1,
			 float x2, float y2, float z2, float w2,
			 float x3, float y3, float z3, float w3);
	
	public FloatBuffer getAsBuffer();
	
	public void finalize();
	
	public void set(String position, float value);
	
	public float get(String position);
}

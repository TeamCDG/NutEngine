package cdg.nut.interfaces;

import cdg.nut.util.Vertex2;

public interface IVertex 
{
	public float getX();
	public float getY();
	public float getZ();
	public float getW();
	public double getNorm();
	
	public float[] toArray();
	
	public boolean isGreaterCoords(IVertex v);
	public boolean isGreaterNorm(IVertex v);
	public boolean isEqual(IVertex v);
	public boolean isLessCoords(IVertex v);
	public boolean isLessNorm(IVertex v);
	public float getDistanceTo(IVertex v);
}

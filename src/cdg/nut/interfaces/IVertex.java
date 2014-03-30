package cdg.nut.interfaces;

public interface IVertex 
{
	public float getX();
	public float getY();
	public float getZ();
	public float getW();
	
	public boolean isGreater(IVertex v);
	public boolean isEqual(IVertex v);
	public boolean isLess(IVertex v);
}

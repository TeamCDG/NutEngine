package cdg.nut.util;

import cdg.nut.interfaces.IVertex;

public abstract class VertexUtility {
	
	public static boolean useRad = false;

	public static <T extends IVertex> Vertex4 substract(T v1, T v2)
	{		
		Vertex4 ret = new Vertex4(v2.getX()-v1.getX(),
								  v2.getY()-v1.getY(),
								  v2.getZ()-v1.getZ(),
								  0);		
		return ret;
	}
	
	public static <T extends IVertex> Vertex4 add(T v1, T v2)
	{		
		Vertex4 ret = new Vertex4(v2.getX()+v1.getX(),
								  v2.getY()+v1.getY(),
								  v2.getZ()+v1.getZ(),
								  0);		
		return ret;
	}
	
	public static <T extends IVertex> float dot(T v1, T v2)
	{			
		return v1.getX()*v2.getX()+v1.getY()*v2.getY()+v1.getZ()*v2.getZ();
	}
	
	public static <T extends IVertex> float angle(T v1, T v2)
	{	
		float rad = (float) Math.cosh(dot(v1,v2)/(abs(v1)*abs(v2)));
		
		if(useRad)
			return rad;
		else
			return Utility.deg(rad);
	}
	
	public static <T extends IVertex> float abs(T v1)
	{			
		return (float) Math.sqrt(v1.getX()*v1.getX()+v1.getY()*v1.getY()+v1.getZ()*v1.getZ());
	}
	
	public static <T extends IVertex> Vertex4 multiply(T v1, float value)
	{
		return new Vertex4(v1.getX()*value, v1.getY()*value,v1.getZ()*value,0);
	}
	
	public static <T extends IVertex> Vertex4 intersectionPoint(T sv1, T dv1, T sv2, T dv2)
	{
		Vertex4 sub = substract(sv1, sv2);
		
		float[][] mat = new float[][]{{dv1.getX(), dv2.getX()},
	  								  {dv1.getY(), dv2.getY()}};
		 
		
		float[] solve = Utility.linearSolve(mat, new float[]{sub.getX(), sub.getY()});
		
		Vertex4 ip = add(sv1, multiply(dv1, solve[0]));
		
		return ip;
	}
	
	
	//linearSolve(float[][] A, float[] b)
	
	/*
	public static <T extends IVertex> Vertex2 to2RAD(float len, float rad)
	{
		return new Vertex2()
	}
	
	public static <T extends IVertex> Vertex2 to2DEG(float len, float deg)
	{
		return to2RAD(len, Utility.rad(deg));
	}*/
}

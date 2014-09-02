package cdg.nut.util;

import cdg.nut.interfaces.IVertex;

public class Vertex3 implements IVertex {
	
	float x, y, z, w;
	
	public Vertex3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = 1.0f;
	}

	public Vertex3(Vertex2 vertex2) {
		this(vertex2.getX(), vertex2.getY(), 0.0f);
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getZ() {
		return this.z;
	}

	public float getW() {
		return this.w;
	}
	
	public double getNorm() {
		return Math.sqrt(x*x + y*y + z*z);
	}
	
	public Vertex2 toVertex2()
	{
		return new Vertex2(this.x,this.y);
	}

	@Override
	public boolean isGreaterCoords(IVertex v) {
		if(v.getX() < this.getX() &&
		   v.getY() < this.getY() &&
		   v.getZ() < this.getZ())
			return true;
		return false;
	}
	
	@Override
	public boolean isGreaterNorm(IVertex v) {
		return (this.getNorm() > v.getNorm());
	}

	@Override
	public boolean isEqual(IVertex v) {
		if(v.getX() == this.getX() &&
		   v.getY() == this.getY() &&
		   v.getZ() == this.getZ())
			return true;
		return false;
	}

	@Override
	public boolean isLessCoords(IVertex v) {
		if(v.getX() > this.getX() &&
		   v.getY() > this.getY() &&
		   v.getZ() > this.getZ())
			return true;
		return false;
	}
	
	@Override
	public boolean isLessNorm(IVertex v) {
		return (this.getNorm() < v.getNorm());
	}

	@Override
	public float[] toArray() {
		return new float[]{this.x,this.y,this.z,this.w};
	}
	
	@Override
	public float getDistanceTo(IVertex v) {
		float dx = this.getX() - v.getX();
		float dy = this.getY() - v.getY();
		float dz = this.getZ() - v.getZ();
		return (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
	}
	
}

package cdg.nut.util;

import cdg.nut.interfaces.*;

public class Vertex2 implements IVertex {
	
	float x, y, z, w;

	public Vertex2(float x, float y) {
		this.x = x;
		this.y = y;
		this.z = 0.0f;
		this.w = 1.0f;
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

	public Vertex2 getVertex2() {
		return new Vertex2(this.x, this.y);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Vertex2 [x=" + x + ", y=" + y + ", z=" + z + ", w=" + w + "]";
	}

	@Override
	public float[] toArray() {
		return new float[]{this.x,this.y,this.z,this.w};
	}

}

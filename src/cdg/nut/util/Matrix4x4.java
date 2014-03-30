package cdg.nut.util;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import cdg.nut.interfaces.IMatrix;
import cdg.nut.interfaces.IVertex;

public class Matrix4x4 implements IMatrix 
{

	float[] matrix;
	FloatBuffer matrixBuf = BufferUtils.createFloatBuffer(16);
	
	public Matrix4x4(float[] matrix)
	{
		this.matrix = matrix;
		this.updateMatrixBuffer();
	}
	
	public Matrix4x4(float x0, float y0, float z0, float w0,
					 float x1, float y1, float z1, float w1,
					 float x2, float y2, float z2, float w2,
					 float x3, float y3, float z3, float w3)
	{
		this(new float[]{x0,y0,z0,w0,x1,y1,z1,w1,x2,y2,z2,w2,x3,y3,z3,w3});
	}
	
	private void updateMatrixBuffer()
	{
		this.matrixBuf.clear();
		this.matrixBuf.put(this.matrix);
		this.matrixBuf.flip();	
	}
	
	@Override
	public float[] toArray()
	{
		return this.matrix;
	}
	
	@Override
	public Vertex4 multiply(IVertex vertex) {
		float nx = vertex.getX()*this.matrix[0]+vertex.getY()*this.matrix[1]+vertex.getZ()*this.matrix[2]+vertex.getW()*this.matrix[3];
		float ny = vertex.getX()*this.matrix[4]+vertex.getY()*this.matrix[5]+vertex.getZ()*this.matrix[6]+vertex.getW()*this.matrix[7];
		//System.out.println(nx+"/"+ny);
		return new Vertex4(nx, 
						   ny, 
						   vertex.getX()*this.matrix[8]+vertex.getY()*this.matrix[9]+vertex.getZ()*this.matrix[10]+vertex.getW()*this.matrix[11], 
						   vertex.getX()*this.matrix[12]+vertex.getY()*this.matrix[13]+vertex.getZ()*this.matrix[14]+vertex.getW()*this.matrix[15]);
	}

	
	public static Matrix4x4 getIdentity() {
		return new Matrix4x4(1.0f, 0.0f, 0.0f, 0.0f,
							 0.0f, 1.0f, 0.0f, 0.0f,
							 0.0f, 0.0f, 1.0f, 0.0f,
							 0.0f, 0.0f, 0.0f, 1.0f);
	}

	@Override
	public void set(float x0, float y0, float z0, float w0, float x1, float y1,
					float z1, float w1, float x2, float y2, float z2, float w2,
					float x3, float y3, float z3, float w3) {
	
		this.matrix[0] = x0;
		this.matrix[1] = y0;
		this.matrix[2] = z0;
		this.matrix[3] = w0;
		
		this.matrix[4] = x1;
		this.matrix[5] = y1;
		this.matrix[6] = z1;
		this.matrix[7] = w1;
		
		this.matrix[8] = x2;
		this.matrix[9] = y2;
		this.matrix[10] = z2;
		this.matrix[11] = w2;
		
		this.matrix[12] = x3;
		this.matrix[13] = y3;
		this.matrix[14] = z3;
		this.matrix[15] = w3;
				
		this.updateMatrixBuffer();
		
	}

	@Override
	public FloatBuffer getAsBuffer() {
		return this.matrixBuf;
	}
	
	@Override
	public void finalize() {
		this.matrixBuf.clear();
		this.matrix = null;
		this.matrixBuf = null;
	}
	
	public Matrix4x4 multiply(Matrix4x4 mat)
	{
		
		float[] ret = new float[16];
		float[] old = mat.toArray();
		
		ret[0] = old[0]*this.matrix[0] + old[1]*this.matrix[4] + old[2]*this.matrix[8] + old[3]*this.matrix[12];
		ret[1] = old[0]*this.matrix[1] + old[1]*this.matrix[5] + old[2]*this.matrix[9] + old[3]*this.matrix[13];
		ret[2] = old[0]*this.matrix[2] + old[1]*this.matrix[6] + old[2]*this.matrix[10] + old[3]*this.matrix[14];
		ret[3] = old[0]*this.matrix[3] + old[1]*this.matrix[7] + old[2]*this.matrix[11] + old[3]*this.matrix[15];
		
		ret[4] = old[4]*this.matrix[0] + old[5]*this.matrix[4] + old[6]*this.matrix[8] + old[7]*this.matrix[12];
		ret[5] = old[4]*this.matrix[1] + old[5]*this.matrix[5] + old[6]*this.matrix[9] + old[7]*this.matrix[13];
		ret[6] = old[4]*this.matrix[2] + old[5]*this.matrix[6] + old[6]*this.matrix[10] + old[7]*this.matrix[14];
		ret[7] = old[4]*this.matrix[3] + old[5]*this.matrix[7] + old[6]*this.matrix[11] + old[7]*this.matrix[15];
		
		ret[8] = old[8]*this.matrix[0] + old[9]*this.matrix[4] + old[10]*this.matrix[8] + old[11]*this.matrix[12];
		ret[9] = old[8]*this.matrix[1] + old[9]*this.matrix[5] + old[10]*this.matrix[9] + old[11]*this.matrix[13];
		ret[10] = old[8]*this.matrix[2] + old[9]*this.matrix[6] + old[10]*this.matrix[10] + old[11]*this.matrix[14];
		ret[11] = old[8]*this.matrix[3] + old[9]*this.matrix[7] + old[10]*this.matrix[11] + old[11]*this.matrix[15];
		
		ret[12] = old[12]*this.matrix[0] + old[13]*this.matrix[4] + old[14]*this.matrix[8] + old[15]*this.matrix[12];
		ret[13] = old[12]*this.matrix[1] + old[13]*this.matrix[5] + old[14]*this.matrix[9] + old[15]*this.matrix[13];
		ret[14] = old[12]*this.matrix[2] + old[13]*this.matrix[6] + old[14]*this.matrix[10] + old[15]*this.matrix[14];
		ret[15] = old[12]*this.matrix[3] + old[13]*this.matrix[7] + old[14]*this.matrix[11] + old[15]*this.matrix[15];
		
		return new Matrix4x4(ret);
	}
	
	public void multiplyThis(Matrix4x4 mat)
	{
		
		float[] ret = new float[16];
		float[] old = mat.toArray();
		
		ret[0] = old[0]*this.matrix[0] + old[1]*this.matrix[4] + old[2]*this.matrix[8] + old[3]*this.matrix[12];
		ret[1] = old[0]*this.matrix[1] + old[1]*this.matrix[5] + old[2]*this.matrix[9] + old[3]*this.matrix[13];
		ret[2] = old[0]*this.matrix[2] + old[1]*this.matrix[6] + old[2]*this.matrix[10] + old[3]*this.matrix[14];
		ret[3] = old[0]*this.matrix[3] + old[1]*this.matrix[7] + old[2]*this.matrix[11] + old[3]*this.matrix[15];
		
		ret[4] = old[4]*this.matrix[0] + old[5]*this.matrix[4] + old[6]*this.matrix[8] + old[7]*this.matrix[12];
		ret[5] = old[4]*this.matrix[1] + old[5]*this.matrix[5] + old[6]*this.matrix[9] + old[7]*this.matrix[13];
		ret[6] = old[4]*this.matrix[2] + old[5]*this.matrix[6] + old[6]*this.matrix[10] + old[7]*this.matrix[14];
		ret[7] = old[4]*this.matrix[3] + old[5]*this.matrix[7] + old[6]*this.matrix[11] + old[7]*this.matrix[15];
		
		ret[8] = old[8]*this.matrix[0] + old[9]*this.matrix[4] + old[10]*this.matrix[8] + old[11]*this.matrix[12];
		ret[9] = old[8]*this.matrix[1] + old[9]*this.matrix[5] + old[10]*this.matrix[9] + old[11]*this.matrix[13];
		ret[10] = old[8]*this.matrix[2] + old[9]*this.matrix[6] + old[10]*this.matrix[10] + old[11]*this.matrix[14];
		ret[11] = old[8]*this.matrix[3] + old[9]*this.matrix[7] + old[10]*this.matrix[11] + old[11]*this.matrix[15];
		
		ret[12] = old[12]*this.matrix[0] + old[13]*this.matrix[4] + old[14]*this.matrix[8] + old[15]*this.matrix[12];
		ret[13] = old[12]*this.matrix[1] + old[13]*this.matrix[5] + old[14]*this.matrix[9] + old[15]*this.matrix[13];
		ret[14] = old[12]*this.matrix[2] + old[13]*this.matrix[6] + old[14]*this.matrix[10] + old[15]*this.matrix[14];
		ret[15] = old[12]*this.matrix[3] + old[13]*this.matrix[7] + old[14]*this.matrix[11] + old[15]*this.matrix[15];
		
		this.set( ret[0],  ret[1],  ret[2], ret[3],
				  ret[4],  ret[5],  ret[6], ret[7],
				  ret[8],  ret[9], ret[10], ret[11],
				 ret[12], ret[13], ret[14], ret[15]);
	}

	

}

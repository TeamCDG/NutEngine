package cdg.nut.util.game;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL32;

import com.google.gson.JsonObject;

import cdg.nut.logging.Logger;
import cdg.nut.util.Matrix4x4;
import cdg.nut.util.ShaderProgram;
import cdg.nut.util.Utility;

public class Camera {

	private float xmove = 0.0f;
	private float ymove = 0.0f;
	private float rotation = 0.0f;
	private float scale = 1.0f;
	
	private transient Matrix4x4 matrix = Matrix4x4.getIdentity();
	private transient Matrix4x4 rotMatrix = Matrix4x4.getIdentity();

	private transient int cameraMatricesUBO;
	
	
	public void deserialize(JsonObject json)
	{
		
		this.setXmove(json.get("xmove").getAsFloat());
		this.setYmove(json.get("ymove").getAsFloat());
		this.setRotation(json.get("rotation").getAsFloat());
		this.setScale(json.get("scale").getAsFloat());
		
	}
	
	public Camera()
	{
		this.cameraMatricesUBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, this.cameraMatricesUBO);
		FloatBuffer fb = BufferUtils.createFloatBuffer(32);
		fb.put(this.matrix.toArray());
		fb.put(this.rotMatrix.toArray());
		fb.rewind();
		GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, fb, GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);
		GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, ShaderProgram.CAM_BINING_POINT, this.cameraMatricesUBO);
	}
	
	private void uploadMatrix()
	{
		GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, this.cameraMatricesUBO);
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		fb.put(this.matrix.toArray());
		fb.rewind();
		GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, 0, fb);
		GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);
	}
	
	private void uploadRotMatrix()
	{
		GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, this.cameraMatricesUBO);
		FloatBuffer fb = BufferUtils.createFloatBuffer(32);
		fb.put(this.matrix.toArray());
		fb.put(this.rotMatrix.toArray());
		fb.rewind();
		GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, fb, GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, 0);
	}
	
	/**
	 * @return the xmove
	 */
	public float getXmove() {
		return xmove;
	}

	/**
	 * @param xmove the xmove to set
	 */
	public void setXmove(float xmove) {
		this.xmove = Math.round(xmove * 100.0f) / 100.0f;

		this.matrix.set(this.scale, 0.0f, 0.0f, 0.0f, 
			 	0.0f, this.scale, 0.0f, 0.0f, 
			 	0.0f, 0.0f, this.scale, 0.0f, 
			 	this.xmove, this.ymove, 0.0f, 1.0f);
		
		this.uploadMatrix();
		
		
	}

	/**
	 * @return the ymove
	 */
	public float getYmove() {
		return ymove;
	}

	/**
	 * @param ymove the ymove to set
	 */
	public void setYmove(float ymove) {
		this.ymove = Math.round(ymove * 100.0f) / 100.0f;

		this.matrix.set(this.scale, 0.0f, 0.0f, 0.0f, 
			 	0.0f, this.scale, 0.0f, 0.0f, 
			 	0.0f, 0.0f, this.scale, 0.0f, 
			 	this.xmove, this.ymove, 0.0f, 1.0f);
		
		this.uploadMatrix();
	}

	/**
	 * @return the rotation
	 */
	public float getRotation() {
		return rotation;
	}

	/**
	 * @param rotation the rotation to set
	 */
	public void setRotation(float rotation) {
		this.rotation = (Math.round(rotation * 100.0f) / 100.0f) % 360.0f;

		this.rotMatrix.set((float)Math.cos(Utility.rad(this.rotation)), (float)Math.sin(Utility.rad(this.rotation)), 0.0f, 0.0f, 
			 	-(float)Math.sin(Utility.rad(this.rotation)), (float)Math.cos(Utility.rad(this.rotation)), 0.0f, 0.0f, 
			 	0.0f, 0.0f, 1.0f, 0.0f, 
			 	0.0f, 0.0f, 0.0f, 1.0f);
		
		this.uploadRotMatrix();
	}

	/**
	 * @return the scale
	 */
	public float getScale() {
		return scale;
	}

	/**
	 * @param scale the scale to set
	 */
	public void setScale(float scale) {
		
		if(scale > 0.1f)
			this.scale = Math.round(scale * 100.0f) / 100.0f;
		
		this.matrix.set(this.scale, 0.0f, 0.0f, 0.0f, 
			 	0.0f, this.scale, 0.0f, 0.0f, 
			 	0.0f, 0.0f, this.scale, 0.0f, 
			 	this.xmove, this.ymove, 0.0f, 1.0f);
		
		this.uploadMatrix();
	}

	/**
	 * @return the matrix
	 */
	public Matrix4x4 getMatrix() {
		return matrix;
	}

	/**
	 * @param matrix the matrix to set
	 */
	public void setMatrix(Matrix4x4 matrix) {
		this.matrix = matrix;
	}
	
	/**
	 * @return the rotMatrix
	 */
	public Matrix4x4 getRotMatrix() {
		return rotMatrix;
	}

	/**
	 * @param rotMatrix the rotMatrix to set
	 */
	public void setRotMatrix(Matrix4x4 rotMatrix) {
		this.rotMatrix = rotMatrix;
	}

	@Override
	public String toString()
	{
		return "Camera: xmove("+this.xmove+"); ymove("+this.ymove+");\n"+
	           "       rotation("+this.rotation+");\n"+
			   "       scale("+this.scale+")";
	}
	
}

package cdg.nut.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import cdg.nut.interfaces.IMatrix;

public final class ShaderProgram {
	
	private int shaderProgrammId;
	private int vertexShaderId;
	private int fragmentShaderId;
	
	private int scalingMatrixLocation;
	private int translationMatrixLocation;
	private int rotationMatrixLocation;
	private int windowMatrixLocation;
	private int camMatrixLocation;
	
	public ShaderProgram(String vertexShaderPath, String fragmentShaderPath) 
	{
		// Load the vertex shader
		this.vertexShaderId = ShaderProgram.loadShader(vertexShaderPath, GL20.GL_VERTEX_SHADER);
		// Load the fragment shader
		this.fragmentShaderId = ShaderProgram.loadShader(fragmentShaderPath, GL20.GL_FRAGMENT_SHADER);
				
		// Create a new shader program that links both shaders
		this.shaderProgrammId = GL20.glCreateProgram();
		
		GL20.glAttachShader(this.shaderProgrammId, this.vertexShaderId);
		GL20.glAttachShader(this.shaderProgrammId, this.fragmentShaderId);
		
		
		// Position information will be attribute 0
		GL20.glBindAttribLocation(this.shaderProgrammId, 0, "in_Position");
		// Color information will be attribute 1
		GL20.glBindAttribLocation(this.shaderProgrammId, 1, "in_Color");
		// Textute information will be attribute 2
		GL20.glBindAttribLocation(this.shaderProgrammId, 2, "in_TextureCoord");		
				
		
		GL20.glLinkProgram(this.shaderProgrammId);
		GL20.glValidateProgram(this.shaderProgrammId);
		
		this.scalingMatrixLocation = GL20.glGetUniformLocation(this.shaderProgrammId, "scaling_Matrix");
		this.translationMatrixLocation = GL20.glGetUniformLocation(this.shaderProgrammId, "translation_Matrix");
		this.rotationMatrixLocation = GL20.glGetUniformLocation(this.shaderProgrammId, "rotation_Matrix");
		this.windowMatrixLocation = GL20.glGetUniformLocation(this.shaderProgrammId, "window_Matrix");
		this.camMatrixLocation = GL20.glGetUniformLocation(this.shaderProgrammId, "cam_Matrix");
	}
	
	@SuppressWarnings("deprecation")
	/**
	 * @param filename location of the shader source file
	 * @param type type of the shader: either GL_VERTEX_SHADER or GL_FRAGMENT_SHADER
	 */
	public static int loadShader(String filename, int type) 
	{
		String[] split = filename.split("/");
		System.out.println("loading "+split[split.length-1]);
		StringBuilder shaderSource = new StringBuilder();
		int shaderID = 0;
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Could not read file.");
			e.printStackTrace();
			System.exit(-1);
		}
		
		shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		
		if (GL20.glGetShader(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.err.println("Could not compile shader.");
			System.exit(-1);
		}
		
		return shaderID;
	}
	
	/**
	 * Passes a standard matrix object to the shader
	 * @param mat the matrix to pass
	 * @param type the type of the matrix (standard matrices only)
	 */
	public void passMatrix(IMatrix mat, MatrixTypes type)
	{
		
		if(type == MatrixTypes.SCALING)
			GL20.glUniformMatrix4(this.scalingMatrixLocation, false, mat.getAsBuffer());
		else if(type == MatrixTypes.TRANSLATION)
			GL20.glUniformMatrix4(this.translationMatrixLocation, false, mat.getAsBuffer());
		else if(type == MatrixTypes.ROTATION)
			GL20.glUniformMatrix4(this.rotationMatrixLocation, false, mat.getAsBuffer());
		else if(type == MatrixTypes.WINDOW)
			GL20.glUniformMatrix4(this.windowMatrixLocation, false, mat.getAsBuffer());
		else if(type == MatrixTypes.CAMERA)
			GL20.glUniformMatrix4(this.camMatrixLocation, false, mat.getAsBuffer());
			
	}
	
	/**
	 * Passes a matrix object to the shader
	 * @param mat the matrix to pass
	 * @param type name of the matrix location
	 */
	public void passMatrix(IMatrix mat, String locationName)
	{
		GL20.glUniformMatrix4(GL20.glGetUniformLocation(this.shaderProgrammId, locationName), false, mat.getAsBuffer());
	}
	
	public void pass1i(String locationName, int value)
	{
		GL20.glUniform1i(GL20.glGetUniformLocation(this.shaderProgrammId, locationName), value);
	}
	
	public void pass2i(String locationName, int value1, int value2)
	{
		GL20.glUniform2i(GL20.glGetUniformLocation(this.shaderProgrammId, locationName), value1, value2);
	}
	
	public void pass3i(String locationName, int value1, int value2, int value3)
	{
		GL20.glUniform3i(GL20.glGetUniformLocation(this.shaderProgrammId, locationName), value1, value2, value3);
	}
	
	public void pass4i(String locationName, int value1, int value2, int value3, int value4)
	{
		GL20.glUniform4i(GL20.glGetUniformLocation(this.shaderProgrammId, locationName), value1, value2, value3, value4);
	}
	
	public void pass1f(String locationName, float value)
	{
		GL20.glUniform1f(GL20.glGetUniformLocation(this.shaderProgrammId, locationName), value);
	}
	
	public void passVertex2(String locationName, Vertex2 vertex)
	{
		this.pass2f(locationName, vertex.x, vertex.y);
	}
	
	public void pass2f(String locationName, float value1, float value2)
	{
		GL20.glUniform2f(GL20.glGetUniformLocation(this.shaderProgrammId, locationName), value1, value2);
	}
	
	public void pass3f(String locationName, float value1, float value2, float value3)
	{
		GL20.glUniform3f(GL20.glGetUniformLocation(this.shaderProgrammId, locationName), value1, value2, value3);
	}
	
	public void passVertex4(String locationName, Vertex4 vertex)
	{
		this.pass4f(locationName, vertex.x, vertex.y, vertex.z, vertex.w);
	}
	
	public void pass4f(String locationName, float value1, float value2, float value3, float value4)
	{
		GL20.glUniform4f(GL20.glGetUniformLocation(this.shaderProgrammId, locationName), value1, value2, value3, value4);
	}
	
	public void bind()
	{
		GL20.glUseProgram(this.shaderProgrammId);
	}
	
	public void unbind()
	{
		GL20.glUseProgram(0);
	}
	
	/**
	 * Finalizes a ShaderProgram object and deletes all used resources
	 */
	public void finalize()
	{
		this.unbind();
		
		GL20.glDetachShader(this.shaderProgrammId, this.vertexShaderId);
		GL20.glDetachShader(this.shaderProgrammId, this.fragmentShaderId);
		
		GL20.glDeleteShader(this.vertexShaderId);
		GL20.glDeleteShader(this.fragmentShaderId);
		GL20.glDeleteProgram(this.shaderProgrammId);	
	}

	/**
	 * @return the shaderProgrammId
	 */
	public int getShaderProgrammId() {
		return shaderProgrammId;
	}

	public void pass4f(String string, float[] color) {
		pass4f(string, color[0], color[1], color[2], color[3]);
		
	}

}

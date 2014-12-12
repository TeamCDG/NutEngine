package cdg.nut.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL31;
import org.lwjgl.util.glu.GLU;

import cdg.nut.interfaces.IMatrix;
import cdg.nut.logging.Logger;
import cdg.nut.util.enums.MatrixTypes;
import cdg.nut.util.gl.GLColor;


//TODO: Javadoc
public final class ShaderProgram {
	
	private transient int shaderProgrammId;
	private transient int vertexShaderId;
	private transient int fragmentShaderId;
	
	private transient int scalingMatrixLocation;
	private transient int translationMatrixLocation;
	private transient int rotationMatrixLocation;
	private transient int windowMatrixLocation;
	
	private transient HashMap<String, Integer> locations = new HashMap<String, Integer>();
	//private int camMatrixLocation;
	//private int camRotMatrixLocation;
	
	private transient int camUniformBlockIndex;
	
	public ShaderProgram(File vertexShader, File fragmentShader) 
	{
		// Load the vertex shader
		this.vertexShaderId = this.loadShader(vertexShader, GL20.GL_VERTEX_SHADER);
		// Load the fragment shader
		this.fragmentShaderId = this.loadShader(fragmentShader, GL20.GL_FRAGMENT_SHADER);
				
		this.setup();
	}
	
	public ShaderProgram(String vertexShaderPath, String fragmentShaderPath) 
	{
		// Load the vertex shader
		this.vertexShaderId = this.loadShader(new File(vertexShaderPath), GL20.GL_VERTEX_SHADER);
		// Load the fragment shader
		this.fragmentShaderId = this.loadShader(new File(fragmentShaderPath), GL20.GL_FRAGMENT_SHADER);
				
		this.setup();
	}
	
	public ShaderProgram(String vertexShader, String fragmentShader, String name) 
	{
		// Load the vertex shader
		this.vertexShaderId = this.loadShader(vertexShader, name, GL20.GL_VERTEX_SHADER);
		// Load the fragment shader
		this.fragmentShaderId = this.loadShader(fragmentShader, name, GL20.GL_FRAGMENT_SHADER);
				
		this.setup();
	}
	
	public static final int CAM_BINING_POINT = 0;
	
	private void setup()
	{
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
		//this.camMatrixLocation = GL20.glGetUniformLocation(this.shaderProgrammId, "cam_Matrix");
		//this.camRotMatrixLocation = GL20.glGetUniformLocation(this.shaderProgrammId, "camRot_Matrix");
		
		this.camUniformBlockIndex = GL31.glGetUniformBlockIndex(this.shaderProgrammId, "CameraMatrices");
		
		if(this.camUniformBlockIndex != -1)
		{
			GL31.glUniformBlockBinding(this.shaderProgrammId, this.camUniformBlockIndex, ShaderProgram.CAM_BINING_POINT);
		}
	}
	
	private static String read(File file)
	{
		StringBuilder shaderSource = new StringBuilder();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			Logger.log(e, "ShaderProgram.read", "Unable to read shader file ("+file.getAbsolutePath()+")");
			Logger.createCrashDump("Unable to read shader file ("+file.getAbsolutePath()+")", "ShaderProgram.read", e);
			System.exit(-1);
		}
		return shaderSource.toString();
	}
	
	private static int compile(String src, String name, int type)
	{
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, src);
		GL20.glCompileShader(shaderID);
		
		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			Logger.error("Unable to compile shader: "+GL20.glGetShaderInfoLog(shaderID, GL20.glGetShaderi(shaderID, GL20.GL_INFO_LOG_LENGTH))+" ("+name+")", "ShaderProgram.compile");
			Logger.createCrashDump("Unable to compile shader: "+GL20.glGetShaderInfoLog(shaderID, GL20.glGetShaderi(shaderID, GL20.GL_INFO_LOG_LENGTH))+" ("+name+")", "ShaderProgram.compile", null, true, src);
			System.exit(-1);
		}
		
		return shaderID;
	}
	
	private String vshaderSrc;
	private String fshaderSrc;
	
	/**
	 * @param file location of the shader source file
	 * @param type type of the shader: either GL_VERTEX_SHADER or GL_FRAGMENT_SHADER
	 */
	private int loadShader(File file, int type) 
	{
		Logger.info("loading shader: "+file.getName());
		
		String src = read(file);
		if(type == GL20.GL_VERTEX_SHADER)
		{
			this.vshaderSrc = src;
			
		}
		else
		{
			this.fshaderSrc = src;
		}
		
		return compile(src, file.getAbsolutePath(), type);	
	}
	
	/**
	 * @param src  source code of the shader
	 * @param name name of the shader
	 * @param type type of the shader: either GL_VERTEX_SHADER or GL_FRAGMENT_SHADER
	 */
	private int loadShader(String src, String name, int type) 
	{
		Logger.info("loading shader: "+name+(type==GL20.GL_VERTEX_SHADER?".vert":".frag"));
		
		if(type == GL20.GL_VERTEX_SHADER)
			this.vshaderSrc = src;
		else
			this.fshaderSrc = src;
		
		return compile(src, name+(type==GL20.GL_VERTEX_SHADER?".vert":".frag"), type);		
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
		//else if(type == MatrixTypes.CAMERA)
		//	GL20.glUniformMatrix4(this.camMatrixLocation, false, mat.getAsBuffer());
		//else if(type == MatrixTypes.CAMERA_ROTATION)
		//	GL20.glUniformMatrix4(this.camRotMatrixLocation, false, mat.getAsBuffer());
			
	}
	
	/**
	 * Passes a matrix object to the shader
	 * @param mat the matrix to pass
	 * @param type name of the matrix location
	 */
	public void passMatrix(IMatrix mat, String locationName)
	{
		if(!this.locations.containsKey(locationName));
			this.locations.put(locationName, GL20.glGetUniformLocation(this.shaderProgrammId, locationName));
		GL20.glUniformMatrix4(this.locations.get(locationName), false, mat.getAsBuffer());
	}
	
	public void pass1i(String locationName, int value)
	{
		if(!this.locations.containsKey(locationName));
			this.locations.put(locationName, GL20.glGetUniformLocation(this.shaderProgrammId, locationName));
		GL20.glUniform1i(this.locations.get(locationName), value);
	}
	
	public void pass2i(String locationName, int value1, int value2)
	{
		if(!this.locations.containsKey(locationName));
			this.locations.put(locationName, GL20.glGetUniformLocation(this.shaderProgrammId, locationName));
		GL20.glUniform2i(this.locations.get(locationName), value1, value2);
	}
	
	public void pass3i(String locationName, int value1, int value2, int value3)
	{
		if(!this.locations.containsKey(locationName));
			this.locations.put(locationName, GL20.glGetUniformLocation(this.shaderProgrammId, locationName));
		GL20.glUniform3i(this.locations.get(locationName), value1, value2, value3);
	}
	
	public void pass4i(String locationName, int value1, int value2, int value3, int value4)
	{
		if(!this.locations.containsKey(locationName));
			this.locations.put(locationName, GL20.glGetUniformLocation(this.shaderProgrammId, locationName));
		GL20.glUniform4i(this.locations.get(locationName), value1, value2, value3, value4);
	}
	
	public void pass1f(String locationName, float value)
	{
		if(!this.locations.containsKey(locationName));
			this.locations.put(locationName, GL20.glGetUniformLocation(this.shaderProgrammId, locationName));
		GL20.glUniform1f(this.locations.get(locationName), value);
	}
	
	public void passVertex2(String locationName, Vertex2 vertex)
	{
		this.pass2f(locationName, vertex.x, vertex.y);
	}
	
	public void pass2f(String locationName, float value1, float value2)
	{
		if(!this.locations.containsKey(locationName));
			this.locations.put(locationName, GL20.glGetUniformLocation(this.shaderProgrammId, locationName));
		GL20.glUniform2f(this.locations.get(locationName), value1, value2);
	}
	
	public void pass3f(String locationName, float value1, float value2, float value3)
	{
		if(!this.locations.containsKey(locationName));
			this.locations.put(locationName, GL20.glGetUniformLocation(this.shaderProgrammId, locationName));
		GL20.glUniform3f(this.locations.get(locationName), value1, value2, value3);
	}
	
	public void passVertex4(String locationName, Vertex4 vertex)
	{
		this.pass4f(locationName, vertex.x, vertex.y, vertex.z, vertex.w);
	}
	
	public void pass4f(String locationName, float value1, float value2, float value3, float value4)
	{
		if(!this.locations.containsKey(locationName));
			this.locations.put(locationName, GL20.glGetUniformLocation(this.shaderProgrammId, locationName));
			
//		for(String s: this.locations.keySet())
//		{
//			Logger.debug("Unfirorm location of '"+s+"': "+this.locations.get(s)+" ("+this.locations.size()+")");
//			
//		}
			
		GL20.glUniform4f(this.locations.get(locationName), value1, value2, value3, value4);
	}
	
	public void passColor(String locationName, GLColor color)
	{
		this.pass4f(locationName, color.toArray());
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

	public int getCamUniformBlockIndex() {
		return camUniformBlockIndex;
	}

}

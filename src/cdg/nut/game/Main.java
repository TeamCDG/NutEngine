package cdg.nut.game;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.glu.GLU;

import cdg.nut.gui.Border;
import cdg.nut.gui.components.ColorBox;
import cdg.nut.gui.components.ImageBox;
import cdg.nut.logging.LogLevel;
import cdg.nut.logging.Logger;
import cdg.nut.util.Colors;
import cdg.nut.util.DefaultShader;
import cdg.nut.util.ShaderProgram;
import cdg.nut.util.VertexData;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.settings.CfgReader;
import cdg.nut.util.settings.Cmd;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new Main();

	}
	
	public Main()
	{
		//Cmd.exec("win_resolution 800 800");
		CfgReader.read("settings.cfg");
		
		Logger.log("gui_cmp_border_color: "+Settings.get(SetKeys.GUI_CMP_BORDER_COLOR, GLColor.class).toString());
		GL11.glClearColor(0.0f,0.0f,0.0f,1.0f);
		
		Logger.setOutputLevel(LogLevel.SPAM);
		
		
		int c=  20;
		int s = 20;
		ArrayList<ColorBox> boxes = new ArrayList<ColorBox>(c);
		int y = 0;
		int x = 0;
		
		for(int i = 0; i< c; i++)
		{
			
			if(x > 9*128.0f)
			{
				x=0;
				y+=s;
			}
			boxes.add(new ColorBox(x, y, s, s, GLColor.random()));
			
			x+=s;
		}
		
		/*
		for(int i = 0; i< c; i++)
		{
			boxes.add(new ColorBox(s, s, s, s, GLColor.random()));
			//if(new Random().nextInt(100) == 42) boxes.get(i).setPosition(20,20);
			//if(new Random().nextInt(100) == 43) boxes.get(i).setPosition(0,20);
			//if(new Random().nextInt(100) == 44) boxes.get(i).setPosition(20,0);
			
		}*/
		//b.setShader(DefaultShader.simple);
		while (!Display.isCloseRequested()) {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glEnable(GL11.GL_BLEND);
			
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			for(int i = 0; i < boxes.size(); i++)
			{
				if(new Random().nextInt(100) == 420) boxes.get(i).setPosition(new Random().nextInt(Settings.get(SetKeys.WIN_WIDTH, Integer.class)-40)+20, new Random().nextInt(Settings.get(SetKeys.WIN_HEIGHT, Integer.class)-40)+20);
				if(new Random().nextInt(100) == 42) boxes.get(i).setColor(GLColor.random());
				boxes.get(i).draw();
			}
			
			
			Display.update();
			Display.sync(60);
		}
	}

	private int backgroundVAO;
	private int indicesCount;
	private int backgroundIndiciesVBO;
	private int backgroundVBO;
	
private void setupQuad() {
		
		VertexData[] points = new VertexData[]{new VertexData(new float[]{-1.0f,1.0f,0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{1.0f,0.0f}),
				   new VertexData(new float[]{-1.0f,-1.0f,0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{1.0f,1.0f}),
				   new VertexData(new float[]{1.0f,-1.0f,0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{0.0f,1.0f}),
				   new VertexData(new float[]{1.0f,1.0f,0.0f,1.0f}, new float[]{1.0f,1.0f,1.0f,1.0f}, new float[]{0.0f,0.0f})};
		// Put each 'Vertex' in one FloatBuffer
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(points.length *
				VertexData.ELEMENT_COUNT);
		for (int i = 0; i < points.length; i++) {
			// Add position, color and texture floats to the buffer
			verticesBuffer.put(points[i].getElements());
		}
		verticesBuffer.flip();	
		// OpenGL expects to draw vertices in counter clockwise order by default
		byte[] indices = {
				0, 1, 2,
				0, 3, 2
		};
		indicesCount = indices.length;
		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
		indicesBuffer.put(indices);
		indicesBuffer.flip();
		
		// Create a new Vertex Array Object in memory and select it (bind)
		backgroundVAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(backgroundVAO);
		
		// Create a new Vertex Buffer Object in memory and select it (bind)
		backgroundVBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, backgroundVBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
		
		// Put the position coordinates in attribute list 0
		// Put the position coordinates in attribute list 0
					GL20.glVertexAttribPointer(0, VertexData.POSITION_ELEMENT_COUNT, GL11.GL_FLOAT, 
							false, VertexData.STRIDE, VertexData.POSITION_BYTE_OFFSET);
					// Put the color components in attribute list 1
					GL20.glVertexAttribPointer(1, VertexData.COLOR_ELEMENT_COUNT, GL11.GL_FLOAT, 
							false, VertexData.STRIDE, VertexData.COLOR_BYTE_OFFSET);
					// Put the texture coordinates in attribute list 2
					GL20.glVertexAttribPointer(2, VertexData.COLOR_ELEMENT_COUNT, GL11.GL_FLOAT, 
							false, VertexData.STRIDE, VertexData.TEXTURE_BYTE_OFFSET);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		// Deselect (bind to 0) the VAO
		GL30.glBindVertexArray(0);
		
		// Create a new VBO for the indices and select it (bind) - INDICES
		backgroundIndiciesVBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, backgroundIndiciesVBO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		this.exitOnGLError("setupQuad");
	}

private void exitOnGLError(String errorMessage) {
	int errorValue = GL11.glGetError();
	
	if (errorValue != GL11.GL_NO_ERROR) {
		System.err.println("ERROR - " + errorMessage + ": " + GLU.gluErrorString(errorValue));
		
		if (Display.isCreated()) Display.destroy();
		System.exit(-1);
	}
	
	errorValue = 0;
}

}

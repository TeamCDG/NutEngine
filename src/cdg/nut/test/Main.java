package cdg.nut.test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.colorchooser.ColorSelectionModel;

import org.fusesource.jansi.Ansi.Color;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.glu.GLU;

import cdg.nut.gui.Border;
import cdg.nut.gui.FontObject;
import cdg.nut.gui.Frame;
import cdg.nut.gui.components.Button;
import cdg.nut.gui.components.ColorBox;
import cdg.nut.gui.components.ImageBox;
import cdg.nut.logging.LogLevel;
import cdg.nut.logging.Logger;
import cdg.nut.logging.ConsoleColor;
import cdg.nut.util.BitmapFont;
import cdg.nut.util.Colors;
import cdg.nut.util.DefaultShader;
import cdg.nut.util.ShaderProgram;
import cdg.nut.util.Utility;
import cdg.nut.util.VertexData;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.settings.CfgReader;
import cdg.nut.util.settings.Cmd;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		try
		{
			new Main();
		}
		catch(LWJGLException e)
		{
			Logger.log(e);
			Logger.createCrashDump(null, "Main.main", e, true);
			System.exit(-1);
		}
		catch(Exception e)
		{
			Logger.log(e);
		}

	}
	
	public Main() throws Exception
	{
		//Settings.flashDefaults();
		Settings.addFont(new BitmapFont("res/font/consolas.txt"));
		CfgReader.read("settings.cfg");

		GL11.glClearColor(0.0f,0.0f,0.0f,1.0f);
		Logger.setOutputLevel(LogLevel.SPAM);
		Button b = new Button(30, 300, "#YOLO");
		b.setAutosizeWithText(true);
		b.setText("ABCDEFGHIJKLMNOPQRSTUVWXYZÜÖÄabcdefghijklmnopqrstuvwxyzüöä,.-;:_#+*'!\"§$%&/()=?<>|\\1234567890");
		b.setFontSize(18);
		
		int c=  0;
		int s = 40;
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
		
		Frame tf = new TestFrame();
		
		Logger.debug("gui_cmp_font_size: "+Settings.get(SetKeys.GUI_CMP_FONT_SIZE, Float.class));
		Logger.debug(""+GLColor.random());
		Button f = new Button(30, 450, Colors.BLUE+"G"+Colors.RED+"o"+Colors.YELLOW+"o"+Colors.BLUE+"g"+Colors.DARKGREEN+"l"+Colors.RED+"e");
		//FontObject f = new FontObject(0.0f, 0.0f, Colors.BLUE+"G"+Colors.RED+"o"+Colors.YELLOW+"o"+Colors.BLUE+"g"+Colors.DARKGREEN+"l"+Colors.RED+"e");
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
			SetKeys.R_CLEAR_BOTH.execute(null);	
			GL11.glEnable(GL11.GL_BLEND);
			
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			//for(int i = 0; i < boxes.size(); i++)
			//{
			//	if(new Random().nextInt(100) == 420) boxes.get(i).setPosition(new Random().nextInt(SetKeys.WIN_WIDTH.getValue(Integer.class)-40)+20, new Random().nextInt(SetKeys.WIN_HEIGHT.getValue(Integer.class)-40)+20);
			//	if(new Random().nextInt(100) == 420) boxes.get(i).setColor(GLColor.random());
			//	boxes.get(i).draw();
			//}
			//if(new Random().nextInt(100) == 420) b.setText(Utility.randomString(100));
			tf.draw();
			//b.draw();
			//f.draw();
			
			
			
			Display.update();
			Display.sync(60);
		}
	}



}

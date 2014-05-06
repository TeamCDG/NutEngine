package cdg.nut.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL13;

import cdg.nut.logging.ConsoleColor;
import cdg.nut.logging.Logger;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLTexture;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

//TODO: Javadoc
public class BitmapFont 
{
	private ArrayList<FontChar> font;
	private GLTexture fontTex;
	private String fontName;
	private String fontPath;
	private float staticHeight;
	private float fontSpace = 0.005f;
	
	public static final BitmapFont EMPTY = new BitmapFont();
	
	private BitmapFont() { } 
	
	public BitmapFont(String fontInfoFilePath) throws IOException
	{
		File f = new File(fontInfoFilePath);
		Reader r = new java.io.InputStreamReader(new FileInputStream(f), "UTF8");
		this.font = new ArrayList<FontChar>(144);
		BufferedReader reader = new BufferedReader(r);
		String line = reader.readLine();
		this.fontName = line.split(";")[0];
		this.staticHeight = Float.valueOf(line.split(";")[4]);
		while((line = reader.readLine()) != null)
		{
			font.add(new FontChar(line));
		}
		reader.close();
		
		this.fontPath = f.getPath().replace(f.getName(),"")+"/"+this.fontName+".png";
		
		if(Display.isCreated())
			this.fontTex = new GLTexture(f.getPath().replace(f.getName(),"")+"/"+this.fontName+".png", GL13.GL_TEXTURE0, true);
		
		this.font.add(new FontChar(" ", 0, 0, this.getChar("#").getWidth(), 0));
	}
	
	public BitmapFont(String fontInfoFilePath, String imagePath) throws IOException
	{
		
		File f = new File(fontInfoFilePath);
		Reader r = new java.io.InputStreamReader(new FileInputStream(f), "UTF8");
		this.font = new ArrayList<FontChar>(144);
		BufferedReader reader = new BufferedReader(r);
		String line = reader.readLine();
		this.fontName = line.split(";")[0];
		this.staticHeight = Float.valueOf(line.split(";")[4]);
		while((line = reader.readLine()) != null)
		{
			font.add(new FontChar(line));
		}
		reader.close();
		
		this.fontPath = imagePath;
		
		if(Display.isCreated())
			this.fontTex = new GLTexture(imagePath, GL13.GL_TEXTURE0, true);
		
		this.font.add(new FontChar(" ", 0, 0, this.getChar("#").getWidth(), 0));
		
		
	}
	
	public FontChar getChar(String c)
	{
		for(int i = 0; i < font.size(); i++)
		{
			if(font.get(i).getC().equals(c))
			{
				return font.get(i);
			}
		}
		return new FontChar(c, 0, 0, 0, 0);		
	}
	
	public void printDebug()
	{
		for(int i = 0; i < 12; i++)
		{
			for(int x = 0; x < 12; x++)
			{
				if(i*12+x < this.font.size())
					System.out.print(this.font.get(i*12+x).getC()+" ");
			}
			System.out.println();
		}
	}
	
	
	public float getX(String c)
	{
		return getChar(c).getX();
	}
	
	public float getY(String c)
	{
		return getChar(c).getY();
	}
	
	public float getWidth(String c)
	{
		return getChar(c).getWidth()+fontSpace;
	}
	
	public float getHeight(String c)
	{
		return getChar(c).getHeight();
	}

	/**
	 * @return the fontName
	 */
	public String getFontName() {
		return fontName;
	}

	/**
	 * @return the staticHeight
	 */
	public float getStaticHeight() {
		return staticHeight;
	}

		

	public GLTexture getFontTex() {
		if(this.fontTex == null)
			this.fontTex = new GLTexture(this.fontPath, GL13.GL_TEXTURE0, true);
		
		return fontTex;
	}
	
	public float[] measureDimensions(String text, float fontSize)
	{
		return measureDimensions(text, fontSize, (char)0);
	}
	
	public float[] measureDimensions(String text, float fontSize, char passwordChar)
	{
		float xoff = 0.0f;
		float yoff = 0.0f;		
		float xoffmax = 0.0f;
		float scalingConst = fontSize / this.getHeight("A");
		
		for(int i = 0; i < text.length(); i++)
		{
			String c = text.substring(i, i+1);
			
			if(c.equals("\u001B")) //color codes...
			{
				String aco = "";
				try
				{
					aco = text.substring(i, i+5);
					
				} catch(Exception e) {}
				
				if(aco.endsWith("m"))
				{
					i+=4;
				}
				else
				{
					i+=3;
					
				}
				
			}
			else if(c.equals(GLColor.TEXT_COLOR_IDENTIFIER)) // gl color
			{
				String aco = "";
				try
				{
					aco = text.substring(i, i+5);
					i+=4;
				} catch(Exception e) {}
			}
			else if(c.equals(GLColor.TEXT_COLOR_RESET)) // back to default
			{
			}
			else if(c.equals("\n")) // new line
			{
				xoff = 0.0f;
				yoff += fontSize;
			}
			else // we actually have something that looks like text
			{
				if(passwordChar != (char)0)
					c = ""+passwordChar;
				
				float w = (scalingConst)*this.getWidth(c)*(1/Settings.get(SetKeys.WIN_ASPECT_RATIO, Float.class))*-1.0f;
				
				xoff += w;
				
				
				
				if(xoff > xoffmax )
					xoffmax = xoff;
			}
			
		}
	
		Logger.debug("Dimensions of '"+text+"': "+xoffmax+"/"+(yoff+fontSize));
		return new float[]{xoffmax, yoff+fontSize};
	}
	
	@Override
	public String toString() {
		return "BitmapFont [fontName=" + fontName + ", fontPath=" + fontPath
				+ "]";
	}

	public int[] measureDimensionsAsInt(String text, float fontSize)
	{
		return measureDimensionsAsInt(text, fontSize, (char)0);
	}
	
	public int[] measureDimensionsAsInt(String text, float fontSize, char passwordChar)
	{
		float[] f = measureDimensions(text, fontSize, passwordChar);
		return Utility.glSizeToPixelSize(f[0], f[1]);
	}
	
	public int[] getCursorPixelPos(String text, float fontSize, int cursorPos)
	{
		return getCursorPixelPos(text, fontSize, (char)0, cursorPos);
	}
	
	public int[] getCursorPixelPos(String text, float fontSize, char passwordChar, int cursorPos)
	{
		float[] f = getCursorPos(text, fontSize, passwordChar, cursorPos);
		return Utility.glSizeToPixelSize(f[0], f[1]);
	}
	
	public float[] getCursorPos(String text, float fontSize, int cursorPos)
	{
		return getCursorPos(text, fontSize, (char)0, cursorPos);
	}
	
	public float[] getCursorPos(String text, float fontSize, char passwordChar, int cursorPos)
	{
		float xoff = 0.0f;
		float yoff = 0.0f;		
		float scalingConst = fontSize / this.getHeight("A");
		
		for(int i = 0; i < cursorPos; i++)
		{
			String c = text.substring(i, i+1);
			
			if(c.equals("\u001B")) //color codes...
			{
				String aco = "";
				try
				{
					aco = text.substring(i, i+5);
					
				} catch(Exception e) {}
				
				if(aco.endsWith("m"))
				{
					i+=4;
				}
				else
				{
					i+=3;
					
				}
				
			}
			else if(c.equals(GLColor.TEXT_COLOR_IDENTIFIER)) // gl color
			{
				try
				{
					i+=4;
				} catch(Exception e) {}
			}
			else if(c.equals(GLColor.TEXT_COLOR_RESET)) // back to default
			{
			}
			else if(c.equals("\n")) // new line
			{
				xoff = 0.0f;
				yoff += fontSize;
			}
			else // we actually have something that looks like text
			{
				if(passwordChar != (char)0)
					c = ""+passwordChar;
				
				float w = (scalingConst)*this.getWidth(c)*(1/Settings.get(SetKeys.WIN_ASPECT_RATIO, Float.class))*-1.0f;
				
				xoff += w;
				
				
			}
			
		}
	
		return new float[]{xoff, yoff};
	}

	public int getIndexByPosition(int x, int y, String text, float fontSize) {
		return getIndexByPosition(x,y,text,fontSize,(char)0);
	}

	public int getIndexByPosition(int x, int y, String text, float fontSize, char passwordChar) {
		float[] pos = Utility.pixelSizeToGLSize(-x, -y);
		return getIndexByPosition(pos[0], pos[1], text, fontSize, passwordChar);
	}
	
	public int getIndexByPosition(float x, float y, String text, float fontSize) {
		return getIndexByPosition(x, y, text, fontSize, (char)0);
	}
	
	public int getIndexByPosition(float x, float y, String text, float fontSize,char passwordChar) {
		float xoff = 0.0f;
		float yoff = 0.0f;		
		x = Math.abs(x);
		y = Math.abs(y);
		float scalingConst = fontSize / this.getHeight("A");
		
		int index = text.length();
		
		for(int i = 0; i < text.length(); i++)
		{
			String c = text.substring(i, i+1);
			
			if(c.equals("\u001B")) //color codes...
			{
				String aco = "";
				try
				{
					aco = text.substring(i, i+5);
					
				} catch(Exception e) {}
				
				if(aco.endsWith("m"))
				{
					i+=4;
				}
				else
				{
					i+=3;
					
				}
				
			}
			else if(c.equals(GLColor.TEXT_COLOR_IDENTIFIER)) // gl color
			{
				try
				{
					i+=4;
				} catch(Exception e) {}
			}
			else if(c.equals(GLColor.TEXT_COLOR_RESET)) // back to default
			{
			}
			else if(c.equals("\n")) // new line
			{
				xoff = 0.0f;
				yoff -= fontSize;
				
				if(yoff > y)
				{
					index = i;
					break;
				}
			}
			else // we actually have something that looks like text
			{
				if(passwordChar != (char)0)
					c = ""+passwordChar;
				
				float w = (scalingConst)*this.getWidth(c)*(1/Settings.get(SetKeys.WIN_ASPECT_RATIO, Float.class))*-1.0f;
				
				xoff += w;
				
				Logger.debug("xoff: "+xoff+" / yoff: "+(yoff-fontSize)+" / x: "+x+" / y:"+y,"BitmapFont.getIndexByPosition");
				
				if(xoff > x && yoff-fontSize > y)
				{
					index = i;
					break;
				}
				
			}
			
		}
			
		return index;
	}

}

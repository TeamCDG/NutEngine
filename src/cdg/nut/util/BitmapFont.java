package cdg.nut.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL13;

import cdg.nut.util.gl.GLTexture;

//TODO: Javadoc
public class BitmapFont 
{
	private ArrayList<FontChar> font;
	private GLTexture fontTex;
	private String fontName;
	private String fontPath;
	private float staticHeight;
	private float fontSpace = 0.005f;
	
	private float height = 0.1f;
	
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
		
		this.fontPath = f.getPath().replace(f.getName(),"")+"\\"+this.fontName+".png";
		
		if(Display.isCreated())
			this.fontTex = new GLTexture(f.getPath().replace(f.getName(),"")+"\\"+this.fontName+".png", GL13.GL_TEXTURE0, true);
		
		this.font.add(new FontChar(" ", 0, 0, this.getChar("A").getWidth(), 0));
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

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}
	
	public void setHeight(int height) {
		this.height = Utility.pixelSizeToGLSize(0, height)[1];
	}

	public GLTexture getFontTex() {
		if(this.fontTex == null)
			this.fontTex = new GLTexture(this.fontPath, GL13.GL_TEXTURE0, true);
		
		return fontTex;
	}


}

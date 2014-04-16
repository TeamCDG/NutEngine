package cdg.nut.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.lwjgl.util.WaveData;

import cdg.nut.logging.Logger;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public abstract class Utility 
{
	
	public static HashMap<String, String> loadInfoTxt(String filename)
	{
		HashMap<String, String> values = new HashMap<String, String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e1) {
			Logger.log(e1, "Utility.loadInfoTxt", "Error creating FileReader for \""+filename);
		}
	    try {
	        String line = br.readLine();

	        while (line != null) {
	        	String[] sp = line.split(":");
	        	values.put(sp[0], sp[1]);
	            line = br.readLine();
	        }
	        
	    } catch (IOException e) {
	    	Logger.log(e, "Utility.loadInfoTxt", "Error reading file for \""+filename);
		} finally {
	        try {
				br.close();
			} catch (IOException e) {
				Logger.log(e, "Utility.loadInfoTxt", "Error closing FileReader for \""+filename);
			}
	    }
		return values;
	}
	
	public static final float GL_COLOR_PER_BIT = 1.0f/255.0f;
	
	public static float[] idToGlColor(long l, boolean useAlpha)
	{
		int[] val = new int[4];

		val[0] = (int) (l >> 24 & 0xFF);
		val[1] = (int) (l >> 16 & 0xFF);
		val[2] = (int) (l >> 8 & 0xFF);
		val[3] = (int) (l >> 0 & 0xFF);
		
		
		
		float[] col;
		if(useAlpha)
			col = new float[] { (float)val[3]*GL_COLOR_PER_BIT,
								(float)val[2]*GL_COLOR_PER_BIT,
								(float)val[1]*GL_COLOR_PER_BIT,
								(float)val[0]*GL_COLOR_PER_BIT};
		else
			col = new float[] { (float)val[3]*GL_COLOR_PER_BIT,
								(float)val[2]*GL_COLOR_PER_BIT,
								(float)val[1]*GL_COLOR_PER_BIT,
								1.0f};
		
		//Logger.spam("color ("+l+") : "+Arrays.toString(col)+" "+Arrays.toString(val));
		
		return col;
	}
	
	public static int glColorToId(int[] color, boolean useAlpha)
	{
		if(useAlpha)
			return color[0]*1+color[1]*256+color[2]*65536+color[3]*16777216;
		else
			return color[0]*1+color[1]*256+color[2]*65536;
		
	}
	
	public static int glColorToId(byte[] color, boolean useAlpha)
	{
		if(useAlpha)
		{
			byte[] fin = new byte[]{color[0], color[1], color[2], color[3]};
			
			return   fin[0] & 0xFF |
		            (fin[1] & 0xFF) << 8 |
		            (fin[2] & 0xFF) << 16 |
		            (fin[3] & 0xFF) << 24;
		}
		else
		{
			byte[] fin = new byte[]{color[0], color[1], color[2]};
			
			return   fin[0] & 0xFF |
		            (fin[1] & 0xFF) << 8 |
		            (fin[2] & 0xFF) << 16|
		            (0 & 0xFF) << 24;
		}
		
	}
	

	
	public static void printFloatArray(float[] f)
	{
		for(int i = 0; i < f.length; i++)
		{
			System.out.print(f[i]+"; ");
		}
		System.out.print("\n");
		System.out.flush();
	}
	
	public static float degToRad(float deg)
	{
		return deg*((float)Math.PI/180.0f);
	}
	
	public static Vertex2 mouseTo2DGL(int x, int y, int screenWidth, int screenHeight)
	{
		float cX = 2.0f/screenWidth;
		float cY = 2.0f/screenHeight;
		
		float glX = x * cX - 1.0f;
		float glY = y * cY -1.0f;
				
		return new Vertex2(glX, glY);
	}
	
	
	

	public static ArrayList<Integer> filterIds(ByteBuffer pixel) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		//byte[] px = pixel.array();
		for(int i = 0; i < pixel.limit(); i+=4)
		{
			int pId = Utility.glColorToId(new byte[]{pixel.get(i), pixel.get(i+1), pixel.get(i+2), pixel.get(i+3)}, false);
			if(pId != 0)
			{
				ids.add(pId);
			}
		}
		
		//if(ids.size() == 0)
		//{
		//	return null;
		//}
		
		return ids;
	}
	
	public static Cursor loadCursor(String path)
	{
		Image c=Toolkit.getDefaultToolkit().getImage(path).getScaledInstance(32,32, Image.SCALE_SMOOTH);
	    BufferedImage biCursor=new BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB);
	    while(!biCursor.createGraphics().drawImage(c,0,31,31,0,0,0,31,31,null))
	      try
	      {
	        Thread.sleep(5);
	      }
	      catch(InterruptedException e)
	      {
	      }
	    /*
	    int[] data=biCursor.getRaster().getPixels(0,0,32,32,(int[])null);
	    
	    IntBuffer ib=BufferUtils.createIntBuffer(32*32);
	    for(int i=0;i<data.length;i+=4)
	      ib.put(data[i] | data[i+1]<<8 | data[i+2]<<16 | data[i+3]<<24);
	    ib.flip();
	    */
	    
	    IntBuffer ib=BufferUtils.createIntBuffer(32*32);
	    for(int x = 0; x < biCursor.getWidth(); x++)
	    {
	    	for(int y = 0; y < biCursor.getHeight(); y++)
	    	{
	    		ib.put(biCursor.getRGB(x, y));
	    	}
	    }
	    ib.flip();
	    
	    try {
			return new Cursor(32, 32, 0, 0, 1, ib, null);
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return null;
	}
	
	public static boolean between(float x, float v1, float v2)
	{
		return x >= Math.min(v1, v2) && x <= Math.max(v1, v2);
	}
	
	public static boolean lineLineIntersect2D(Vertex2 p1, Vertex2 p2, Vertex2 p3, Vertex2 p4)
	{
		float xDif1 = p1.getX() - p2.getX();
		float yDif1 = p1.getY() - p2.getY();
		float xDif2 = p3.getX() - p4.getX();
		float yDif2 = p3.getY() - p4.getY();
		
		if(xDif1 == 0 && xDif2 == 0)
			return p1.getX() == p3.getX();
		else if(xDif1 == 0 && xDif2 != 0)
		{
			float m2 = yDif2 / xDif2;
			float n2 = p3.getY() - (m2 * p3.getX());
			float ty = m2 * p1.getX() + n2;
			return Utility.between(ty, p1.getY(), p2.getY()) && 
				   Utility.between(p1.getX(), p3.getX(), p4.getX());
		}
		else if(xDif2 == 0 && xDif1 != 0)
		{
			float m1 = yDif1 / xDif1;
			float n1 = p1.getY() - (m1 * p1.getX());
			float ty = m1 * p3.getX() + n1;
			return Utility.between(ty, p3.getY(), p4.getY()) && 
				   Utility.between(p3.getX(), p1.getX(), p2.getX());
		}
		else
		{
			float m1 = yDif1 / xDif1;
			float m2 = yDif2 / xDif2;
			float n1 = p1.getY() - (m1 * p1.getX());
			float n2 = p3.getY() - (m2 * p3.getX());
			
			if(m1 == m2)
				return n1 == n2;
			
			float xCol = (n1-n2)/(m2-m1);
			//float yCol = m1 * xCol + n1;
			
			return Utility.between(xCol, p1.getX(), p2.getX()) && 
				   Utility.between(xCol, p3.getX(), p4.getX());
		}
	}
	
	public static boolean lineCircleIntersect2D(Vertex2 p1, Vertex2 p2, Vertex2 m, float r)
	{
		Vertex2 p1s = new Vertex2(p1.getX() - m.getX(), p1.getY()-m.getY());
		Vertex2 p2s = new Vertex2(p2.getX() - m.getX(), p2.getY()-m.getY());
		
		float dx = p2s.getX() - p1s.getX();
		float dy = p2s.getY() - p1s.getY();
		float dr = (float) Math.sqrt(Math.pow(dx,2)+ Math.pow(dy, 2));
		float D = (p1s.getX() * p2s.getY()) - (p2s.getX() * p1s.getY());
		
		return ((Math.pow(r, 2) * Math.pow(dr, 2))-Math.pow(D, 2)) >= 0;
	}
	
	public static boolean circleCircleIntersect2D(Vertex2 m1, float r1, Vertex2 m2, float r2)
	{
		float dx = m2.getX() - m1.getX();
		float dy = m2.getY() - m1.getY();
		
		return Math.sqrt(Math.pow(dx,2)+ Math.pow(dy, 2)) <= (r1 + r2);
	}
	
	/**
	 * 1) Identify the error code.
	 * 2) Return the error as a string.
	 */
	public static String getALErrorString(int err) {
		  switch (err) {
		    case AL10.AL_NO_ERROR:
		      return "AL_NO_ERROR";
		    case AL10.AL_INVALID_NAME:
		      return "AL_INVALID_NAME";
		    case AL10.AL_INVALID_ENUM:
		      return "AL_INVALID_ENUM";
		    case AL10.AL_INVALID_VALUE:
		      return "AL_INVALID_VALUE";
		    case AL10.AL_INVALID_OPERATION:
		      return "AL_INVALID_OPERATION";
		    case AL10.AL_OUT_OF_MEMORY:
		      return "AL_OUT_OF_MEMORY";
		    default:
		      return "unkwon error";
		  }
	}
	
	/**
	 * 1) Identify the error code.
	 * 2) Return the error as a string.
	 */  
	public static String getALCErrorString(int err) {
		  switch (err) {
		    case ALC10.ALC_NO_ERROR:
		      return "AL_NO_ERROR";
		    case ALC10.ALC_INVALID_DEVICE:
		      return "ALC_INVALID_DEVICE";
		    case ALC10.ALC_INVALID_CONTEXT:
		      return "ALC_INVALID_CONTEXT";
		    case ALC10.ALC_INVALID_ENUM:
		      return "ALC_INVALID_ENUM";
		    case ALC10.ALC_INVALID_VALUE:
		      return "ALC_INVALID_VALUE";
		    case ALC10.ALC_OUT_OF_MEMORY:
		      return "ALC_OUT_OF_MEMORY";
		    default:
		      return "unkwon error";
		  }
	}
	
	public static int loadWav(String filename )
	{
		int buf = -1;
		int src = -1;
		try
		{
			buf = AL10.alGenBuffers();
		}
		catch(UnsatisfiedLinkError e)
		{
			Logger.info("Seems no AL sound device has been created... creating now", "Utility.loadWav");
			
			try {
				AL.create();
			} catch (LWJGLException e1) {
				
				Logger.log(e1, "Utility.loadWav", "Unable to create AL sound device");
				return -1;
			}
			buf = AL10.alGenBuffers();
		}
		
		if (AL10.alGetError() != AL10.AL_NO_ERROR)
		{
			Logger.error(Utility.getALErrorString(AL10.alGetError()), "Utility.loadWav");
			return -1;
		}
		
		WaveData waveFile = WaveData.create(filename);
		AL10.alBufferData(buf, waveFile.format, waveFile.data, waveFile.samplerate);
		waveFile.dispose();
		
		src = AL10.alGenSources();
		
		if (AL10.alGetError() != AL10.AL_NO_ERROR)
		{
			Logger.error(Utility.getALErrorString(AL10.alGetError()), "Utility.loadWav");
			return -1;
		}
		
		
		AL10.alSourcei(src, AL10.AL_BUFFER, buf);
		AL10.alSourcef(src, AL10.AL_PITCH, 1.0f);
		AL10.alSourcef(src, AL10.AL_GAIN, 1.0f);
		
		FloatBuffer sourcePos = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
		FloatBuffer sourceVel = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
		
		AL10.alSource(src, AL10.AL_POSITION, sourcePos);
		AL10.alSource(src, AL10.AL_VELOCITY, sourceVel);
		return src;
		
	}

	public static int[] glToPixel(float x, float y) {
		int px = Math.round(((float)(x+1.0f)*((float)Settings.get(SetKeys.WIN_WIDTH, Integer.class)/2.0f)));
		int py = Math.round(((float)((y*-1.0f)+1.0f)*((float)Settings.get(SetKeys.WIN_HEIGHT, Integer.class)/2.0f)))*1;
		return new int[]{px,py};
	}
	
	public static int[] glSizeToPixelSize(float x, float y) {
		int px = (int)(((float)x*((float)Settings.get(SetKeys.WIN_WIDTH, Integer.class)/2.0f)));
		int py = (int)(((float)y*((float)Settings.get(SetKeys.WIN_HEIGHT, Integer.class)/2.0f)))*-1;
		return new int[]{px,py};
	}

	public static float[] pixelToGL(int x, int y) {
		float glx = (((float)x*(2.0f/(float)Settings.get(SetKeys.WIN_WIDTH, Integer.class)))-1.0f);
		float gly = (((float)y*(2.0f/(float)Settings.get(SetKeys.WIN_HEIGHT, Integer.class)))-1.0f)*-1.0f;
		
		//Logger.spam("Pixel("+x+"/"+y+") = GL("+glx+"/"+gly+")");
		
		return new float[]{glx, gly};
	}
	
	public static float[] pixelSizeToGLSize(int x, int y) {
		float glx = ((float)x*(2.0f/(float)Settings.get(SetKeys.WIN_WIDTH, Integer.class)));//*(1.0f/Settings.get(SetKeys.WIN_ASPECT_RATIO, Float.class));
		float gly = ((float)y*(2.0f/(float)Settings.get(SetKeys.WIN_HEIGHT, Integer.class)))*-1.0f;
		
		//Logger.spam("Pixel-->("+x+"/"+y+") = GL-->("+glx+"/"+gly+")");
		
		return new float[]{glx, gly};
	}

	public static String unescape(String esc) {
		int i = esc.indexOf("\\");
        while(i != -1 && i < esc.length())
        {
            if(i == 0)
                esc = esc.substring(i, esc.length());
            else
                esc = esc.substring(0, i) + esc.substring(i+1, esc.length()); 
                
            i = esc.indexOf("\\", i+1);
        }        
        return esc;
	}
	
	public static byte[] createQuadIndicesByte(int quadCount)
    {
		byte[] in = new byte[quadCount * 6];
        for(int i = 0; i < quadCount; i++)
        {
            in[i*6] = (byte) (i*4);
            in[i*6+1] = (byte) ((i*4)+1);
            in[i*6+2] = (byte) ((i*4)+2);
            in[i*6+3] = (byte) ((i*4)+2);
            in[i*6+4] = (byte) ((i*4)+3);
            in[i*6+5] = (byte) (i*4);
        }
        
        for(int y = 0; y < in.length; y++)
        {
           	//Logger.spam("Indices for "+quadCount+" quads: "+in[y]+", ");
        }
        
        
        return in;
    }
	
	public static int[] createQuadIndicesInt(int quadCount)
    {
		int[] in = new int[quadCount * 6];
        for(int i = 0; i < quadCount; i++)
        {
            in[i*6] = (i*4);
            in[i*6+1] = ((i*4)+1);
            in[i*6+2] = ((i*4)+2);
            in[i*6+3] = ((i*4)+2);
            in[i*6+4] = ((i*4)+3);
            in[i*6+5] = (i*4);
        }
        
        for(int y = 0; y < in.length; y++)
        {
        }
        
        
        return in;
        
    }
	
	
	
	public static VertexData[] generateQuadData(float x, float y, float width, float height, GLColor idColor)
	{
		Vertex4[] qp = generateQuadPoints(x, y, width, height);
		return generateQuadData(qp, idColor, generateSTPoints(0, 0, 1.0f, 1.0f));
	}
	
	public static VertexData[] generateQuadData(Vertex4[] qp, GLColor idColor)
	{
		return generateQuadData(qp, idColor, generateSTPoints(0, 0, 1.0f, 1.0f));
	}
	
	public static VertexData[] generateQuadData(Vertex4[] qp, GLColor idColor, Vertex2[] st)
	{
		return new VertexData[]{new VertexData(qp[0], idColor, st[0]),
								new VertexData(qp[1], idColor, st[1]),
								new VertexData(qp[2], idColor, st[2]),
								new VertexData(qp[3], idColor, st[3])};
	}
	
	public static Vertex2[] generateSTPointsFlipped(float x, float y, float width, float height)
	{
		//return new Vertex2[]{new Vertex2(x,y),  new Vertex2(x,y+height), new Vertex2(x+width,y+height), new Vertex2(x+width,y)};
		return new Vertex2[]{new Vertex2(x,y),  new Vertex2(x,y+height), new Vertex2(x+width,y+height), new Vertex2(x+width,y)};
		
		
	}
	
	public static Vertex2[] generateSTPoints(float x, float y, float width, float height)
	{
		return new Vertex2[]{new Vertex2(x,y),  new Vertex2(x,y+height), new Vertex2(x+width,y+height), new Vertex2(x+width,y)};
		//return new Vertex2[]{new Vertex2(x+width,y), new Vertex2(x,y), new Vertex2(x,y+height), new Vertex2(x+width,y+height)};
		
	}

	
	public static Vertex4[] generateQuadPoints(float x, float y, float width, float height)
	{
		return new Vertex4[]{new Vertex4(x,y,0.0f,1.0f),  new Vertex4(x,y+height,0.0f,1.0f), new Vertex4(x+width,y+height,0.0f,1.0f), new Vertex4(x+width,y,0.0f,1.0f)};
		
	}

	public static Vertex4[] extractPoints(VertexData[] vertices) {
		Vertex4[] points = new Vertex4[vertices.length];
		for(int i = 0; i < vertices.length; i++)
		{
			points[i] = new Vertex4(vertices[i].getXYZW());
		}
		return points;
	}
	
	public static String randomString()
	{
		return randomString(5, "ABCDEFGHIJKLMNOPQRSTUVWXYZÜÖÄabcdefghijklmnopqrstuvwxyzüöä,.-;:_#+*'!\"§$%&/()=?<\\1234567890");
	}
	
	public static String randomString(int length)
	{
		return randomString(length, "ABCDEFGHIJKLMNOPQRSTUVWXYZÜÖÄabcdefghijklmnopqrstuvwxyzüöä,.-;:_#+*'!\"§$%&/()=?<>|\\1234567890");
	}
	
	public static String randomString(int length, String chars)
	{
		StringBuilder b = new StringBuilder();
		
		for(int i = 0; i < length; i++)
		{
			int r = new Random().nextInt(chars.length());
			b.append(chars.substring(r,r+1));
		}
		
		return b.toString();
	}

	public static String getClipboard() {
		 Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		 Transferable t = c.getContents( null );

	     if ( t.isDataFlavorSupported(DataFlavor.stringFlavor) )
	     {
	    	 
	            try {
					return (String)t.getTransferData( DataFlavor.stringFlavor );
				} catch (UnsupportedFlavorException | IOException e) {
					Logger.log(e, "Utility.getClipboard");
				}
	     }
		return "";
	}
	
	public static void setClipboard(String text)
	{
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection data = new StringSelection(text);
		c.setContents(data, data);
	}
}

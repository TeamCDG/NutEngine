package cdg.nut.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.lwjgl.util.WaveData;

import cdg.nut.logging.Logger;

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
	
	public static final float GL_COLOR_PER_BIT = 0.00390625f;
	
	public static float[] idToGlColor(long l, boolean useAlpha)
	{
		byte[] val = new byte[4];

		val[0] = (byte) (l >> 24);
		val[1] = (byte) (l >> 16);
		val[2] = (byte) (l >> 8);
		val[3] = (byte) (l >> 0);
		
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
		
		return col;
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
		// TODO Auto-generated method stub
		return null;
	}

	public static float[] pixelToGL(int x, int i) {
		// TODO Auto-generated method stub
		return null;
	}
}

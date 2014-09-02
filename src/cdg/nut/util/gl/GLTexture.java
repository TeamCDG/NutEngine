package cdg.nut.util.gl;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import cdg.nut.interfaces.IntIntChangedListener;
import cdg.nut.util.Globals;
import cdg.nut.util.ImageUtils;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

//TODO: Javadoc
public class GLTexture implements IntIntChangedListener
{
	
	public static final int FILTER_NONSMOOTH_MIN = 0;
	public static final int FILTER_NONSMOOTH_MAG = 1;
	public static final int FILTER_SMOOTH_MIN = 2;
	public static final int FILTER_SMOOTH_MAG = 3;
	public static final int RELOAD = 4;
	
	private int textureId;
	private int textureUnit;
	private String filename;
	private BufferedImage image;
	private boolean smooth;
	
	public GLTexture(String filename, int textureUnit)
	{
		this(filename, textureUnit, false);
	}
	
	public GLTexture(String filename, int textureUnit, boolean smooth)
	{
		this.textureId = loadPNGTexture(filename, textureUnit, smooth);
		this.textureUnit = textureUnit;
		this.filename = filename;
		this.smooth = smooth;
		Globals.addTextureChangeListener(this);
	}
	
	public static int loadPNGTexture(String filename, int textureUnit, boolean smooth) 
	{
		if(!Display.isCreated())
			return -1;
		int texId = GL11.glGenTextures();
		loadF(filename, textureUnit, smooth, texId);
		return texId;
	}
	
	private static void loadF(String filename, int textureUnit, boolean smooth, int texId)
	{
		ByteBuffer buf = null;
		int tWidth = 0;
		int tHeight = 0;
		
		try {
			// Open the PNG file as an InputStream
			InputStream in = new FileInputStream(filename);
			// Link the PNG decoder to this stream
			PNGDecoder decoder = new PNGDecoder(in);
			
			// Get the width and height of the texture
			tWidth = decoder.getWidth();
			tHeight = decoder.getHeight();
			
			
			// Decode the PNG file in a ByteBuffer
			buf = ByteBuffer.allocateDirect(
					4 * decoder.getWidth() * decoder.getHeight());
			decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
			buf.flip();
			
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Create a new texture object in memory and bind it
		
		GL13.glActiveTexture(textureUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
		
		// All RGB bytes are aligned to each other and each component is 1 byte
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		
		// Upload the texture data and generate mip maps (for scaling)
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, tWidth, tHeight, 0, 
				GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		
		// Setup the ST coordinate system
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		
		// Setup what to do when the texture has to be scaled
		if(smooth)
		{
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, 
					Globals.getTextureFilerSmoothMag());
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, 
					Globals.getTextureFilerSmoothMin());
		}
		else
		{
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, 
					Globals.getTextureFilerNonSmoothMag());
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, 
					Globals.getTextureFilerNonSmoothMin());
		}
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public GLTexture(BufferedImage img, int textureUnit)
	{
		this(img, textureUnit, false);
	}
	
	public GLTexture(BufferedImage img, int textureUnit, boolean smooth)
	{
		this.textureId = loadBufferdImage(img, textureUnit, smooth);
		this.textureUnit = textureUnit;
		this.image = img;
		this.smooth = smooth;
		Globals.addTextureChangeListener(this);
	}
	
	public GLTexture(String path) {
		this(path, GL13.GL_TEXTURE0);
	}

	public GLTexture(String path, boolean b) {
		this(path, GL13.GL_TEXTURE0, b);
	}

	public static int loadBufferdImage(BufferedImage img, int textureUnit, boolean smooth)
	{
		int texId = GL11.glGenTextures();
		loadB(img, textureUnit, smooth, texId);
		return texId;
	}
	
	private static void loadB(BufferedImage img, int textureUnit, boolean smooth, int texId)
	{
		ByteBuffer buf = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 4); //4 for RGBA, 3 for RGB
	    
	    for(int y = 0; y < img.getHeight(); y++) {
	    	for(int x = 0; x < img.getWidth(); x++) {
	    		
	    		int[] argb = ImageUtils.getARGB(img.getRGB(x, y));
	            buf.put((byte) argb[1]);    // Red component
	            buf.put((byte) argb[2]);    // Green component
	            buf.put((byte) argb[3]);	// Blue component
	            buf.put((byte) argb[0]);    // Alpha component. Only for RGBA
	        }
	    }

	    buf.flip();
		
		// Create a new texture object in memory and bind it
		
		GL13.glActiveTexture(textureUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
		
		// All RGB bytes are aligned to each other and each component is 1 byte
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		
		// Upload the texture data and generate mip maps (for scaling)
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, img.getWidth(), img.getHeight(), 0, 
				GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		
		// Setup the ST coordinate system
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		
		// Setup what to do when the texture has to be scaled
		if(smooth)
		{
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, 
					Globals.getTextureFilerSmoothMag());
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, 
					Globals.getTextureFilerSmoothMin());
		}
		else
		{
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, 
					Globals.getTextureFilerNonSmoothMag());
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, 
					Globals.getTextureFilerNonSmoothMin());
		}
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public int getTextureId()
	{
		return this.textureId;
	}
	
	public int getTextureUnit()
	{
		return this.textureUnit;
	}
	
	public void reload()
	{
		if(this.filename == null)
			reloadBI();
		else
			reloadF();
	}
	
	private void reloadBI()
	{
		GLTexture.loadB(this.image, this.textureUnit, this.smooth, this.textureId);
	}
	
	private void reloadF()
	{
		GLTexture.loadF(this.filename, this.textureUnit, this.smooth, this.textureId);
	}
	
	public void bind()
	{
		GL13.glActiveTexture(this.textureUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureId);
	}
	
	public void unbind()
	{
		GL13.glActiveTexture(this.textureUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public void finalize()
	{
		if(this.image != null)
		{
			this.image.flush();
			this.image = null;
		}
		this.filename = null;
		
		GL11.glDeleteTextures(this.textureId);
	}

	@Override
	public void onChange(int identifier, int value) {
		if(identifier == GLTexture.FILTER_SMOOTH_MIN && this.smooth)
		{
			this.bind();
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, 
					Globals.getTextureFilerSmoothMin());
			this.unbind();
		}
		else if(identifier == GLTexture.FILTER_SMOOTH_MAG && this.smooth)
		{
			this.bind();
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, 
					Globals.getTextureFilerSmoothMag());
			this.unbind();
		}
		else if(identifier == GLTexture.FILTER_NONSMOOTH_MIN && !this.smooth)
		{
			this.bind();
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, 
					Globals.getTextureFilerNonSmoothMin());
			this.unbind();
		}
		else if(identifier == GLTexture.FILTER_NONSMOOTH_MAG && !this.smooth)
		{
			this.bind();
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, 
					Globals.getTextureFilerNonSmoothMag());
			this.unbind();
		}
		else if(identifier == GLTexture.RELOAD)
		{
			reload();
		}
		
	}
}

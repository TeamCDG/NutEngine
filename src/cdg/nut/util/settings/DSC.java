package cdg.nut.util.settings;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;


public abstract class DSC {
	public <T> void valueChanged(SettingsKeys key,T value)
	{
		String k = key.name().toLowerCase();
		if(k.equals("window_width"))
		{
			setWindowResolution((Integer) value, Settings.get(SettingsKeys.WIN_HEIGHT, int.class));
		}
		else if(k.equals("window_height"))
		{
			setWindowResolution(Settings.get(SettingsKeys.WIN_WIDTH, int.class), (Integer) value);
		}
	}
	
	private static void setWindowResolution(int width, int height){
		Settings.set(SettingsKeys.WIN_ASPECT_RATIO, (float) width/ (float) height);
		/*Globals.windowMatrix.set(1/Globals.aspectRatio, 0.0f, 0.0f, 0.0f,
												  0.0f, 1.0f, 0.0f, 0.0f,
												  0.0f, 0.0f, 1.0f, 0.0f,
												  0.0f, 0.0f, 0.0f, 1.0f);*/
		
		//for(IWindowChangeListener lis : Globals.windowChangeListener)
		//{
		//	lis.onWindowResolutionChange(Globals.windowWidth, Globals.windowHeight);
		//}
		
		try {
			DisplayMode[] modes = Display.getAvailableDisplayModes();
			
			DisplayMode finalMode = new DisplayMode(width, height);
			
			for (int i=0;i<modes.length;i++) {
			    DisplayMode current = modes[i];
			    if(current.getWidth() == width
			       && current.getHeight() == height
			       && current.getBitsPerPixel() == 32
			       && current.getFrequency() == 60)
			    finalMode = current;
			}
			
			Display.setDisplayMode(finalMode);
			
			if(!Display.isCreated())
			{
				PixelFormat pixelFormat = new PixelFormat();
				ContextAttribs contextAtrributes = new ContextAttribs(3, 2)
					.withForwardCompatible(true)
					.withProfileCore(true);
				Display.create(pixelFormat, contextAtrributes);
			}
			
			GL11.glViewport(0, 0, width,height);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		Display.update();
	}
}

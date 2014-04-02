package cdg.nut.util.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import cdg.nut.interfaces.ISettingsListener;
import cdg.nut.util.Globals;

//TODO: Javadoc
public class Settings {
	private static HashMap<SettingsKeys, Object> settings = new HashMap<SettingsKeys, Object>();
	private static List<ISettingsListener> listener = new ArrayList<ISettingsListener>();
	
	public static void set(SettingsKeys key, Object value)
	{
		if(key.getAccess() == SettingsAccess.PUBLIC)
		{
			settings.put(key, value);
		}
		
		if(key == SettingsKeys.WIN_WIDTH)
		{
			setWindowResolution((Integer) value, Settings.get(SettingsKeys.WIN_HEIGHT, int.class));
		}
		else if(key == SettingsKeys.WIN_HEIGHT)
		{
			setWindowResolution(Settings.get(SettingsKeys.WIN_WIDTH, int.class), (Integer) value);
		}
		else
		{
			for(int i = 0; i < listener.size(); i++)
			{
				listener.get(i).valueChanged(key, key.getCls().cast(value));
			}
		}
	}
	
	public static void set(String key, Object value)
	{
		Settings.set(SettingsKeys.valueOf(key.toUpperCase()), value);		
	}
	
	public static Object get(String key)
	{
		return Settings.get(SettingsKeys.valueOf(key.toUpperCase()));
	}
	
	public static <T> T get(String key, Class<T> c)
	{
		return Settings.get(SettingsKeys.valueOf(key.toUpperCase()), c);
	}
	
	
	public static Object get(SettingsKeys key)
	{
		
		return settings.get(key);
	}
	
	public static <T> T get(SettingsKeys key, Class<T> c)
	{
		return c.cast(settings.get(key));
	}

	public static void removeListener(ISettingsListener lis) {
		listener.remove(lis);
	}

	public static void addListener(ISettingsListener lis) {
		Settings.listener.add(lis);
	}
	
	public static void setWindowResolution(int width, int height){
		settings.put(SettingsKeys.WIN_WIDTH, width);
		settings.put(SettingsKeys.WIN_HEIGHT, height);
		settings.put(SettingsKeys.WIN_ASPECT_RATIO, (float) width/ (float) height);
		Settings.set(SettingsKeys.WIN_RESOLUTION_CHANGED, null);
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
			//TODO: use logger
		}
		Display.update();
	}
}

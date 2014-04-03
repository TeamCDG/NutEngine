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
	private static HashMap<SetKeys, Object> settings = new HashMap<SetKeys, Object>();
	private static List<ISettingsListener> listener = new ArrayList<ISettingsListener>();
	
	@SuppressWarnings("unchecked")
	public static void set(SetKeys key, Object value)
	{
		if(key.getType() == SettingsType.SETTING)
		{
			settings.put(key, value);
		}
		else if(key.getType() == SettingsType.COMMAND_AND_SETTING)
		{
			key.execute((List<String>) value);
		}
	}
	
	public static void set(String key, Object value)
	{
		Settings.set(SetKeys.valueOf(key.toUpperCase()), value);		
	}
	
	public static Object get(String key)
	{
		return Settings.get(SetKeys.valueOf(key.toUpperCase()));
	}
	
	public static <T> T get(String key, Class<T> c)
	{
		return Settings.get(SetKeys.valueOf(key.toUpperCase()), c);
	}
	
	
	public static Object get(SetKeys key)
	{
		
		return settings.get(key);
	}
	
	public static <T> T get(SetKeys key, Class<T> c)
	{
		return c.cast(settings.get(key));
	}

	public static void removeListener(ISettingsListener lis) {
		listener.remove(lis);
	}

	public static void addListener(ISettingsListener lis) {
		Settings.listener.add(lis);
	}
	
	private static void fire(SetKeys event)
	{
		fire(event, null);
	}
	
	private static void fire(SetKeys event, Object value)
	{
		for(int i = 0; i < listener.size(); i++)
		{
			listener.get(i).valueChanged(event, event.getCls().cast(value));
		}
	}
	
	public static void setWindowResolution(int width, int height){
		settings.put(SetKeys.WIN_WIDTH, width);
		settings.put(SetKeys.WIN_HEIGHT, height);
		settings.put(SetKeys.WIN_ASPECT_RATIO, (float) width/ (float) height);
		
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
		fire(SetKeys.WIN_RESOLUTION_CHANGED);
	}
}

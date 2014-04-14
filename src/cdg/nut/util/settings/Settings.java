package cdg.nut.util.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import cdg.nut.interfaces.ISettingsListener;
import cdg.nut.logging.Logger;
import cdg.nut.util.BitmapFont;
import cdg.nut.util.Globals;

//TODO: Javadoc
public class Settings {
	//private static HashMap<SetKeys, Object> settings = new HashMap<SetKeys, Object>();
	private static HashMap<String, BitmapFont> availableFonts = new HashMap<String, BitmapFont>();
	private static List<ISettingsListener> listener = new ArrayList<ISettingsListener>();
	
	@SuppressWarnings("unchecked")
	public static void set(SetKeys key, Object value)
	{
		Logger.debug("Setting '"+key.name().toLowerCase()+"' to '"+value+"'");
		if(key.getType() == SettingsType.SETTING)
		{
			key.setValue(value);
			fire(key);
		}
		else if(key.getType() == SettingsType.COMMAND_AND_SETTING)
		{
			if(value instanceof List<?>)
				key.execute((List<String>) value);
			else if(value != null)
			{
				ArrayList<String> l = new ArrayList<String>();
				l.add(value.toString());
				key.execute(l);
			}
			
			fire(key);
		}
	}
	
	public static void setSuppress(SetKeys key, Object value)
	{
		key.setValue(value);
		//settings.put(key, value);
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
		return key.getValue();
		//return settings.get(key);
	}
	
	public static <T> T get(SetKeys key, Class<T> c)
	{
		//return c.cast(settings.get(key));
		return key.getValue(c);
	}

	public static void removeListener(ISettingsListener lis) {
		listener.remove(lis);
	}

	public static void addListener(ISettingsListener lis) {
		Settings.listener.add(lis);
	}
	
	private static void fire(SetKeys event)
	{
		for(int i = 0; i < listener.size(); i++)
		{
			listener.get(i).valueChanged(event);
		}
	}
	
	public static void setWindowResolution(int width, int height){
		if(Settings.get(SetKeys.WIN_WIDTH, Integer.class) != null && Settings.get(SetKeys.WIN_WIDTH, Integer.class) == width && Settings.get(SetKeys.WIN_HEIGHT, Integer.class) != null &&Settings.get(SetKeys.WIN_HEIGHT, Integer.class) == height)
			return;
		
		if(width != 0)
			//settings.put(SetKeys.WIN_WIDTH, width);
			SetKeys.WIN_WIDTH.setValue(width);
		
		if(height != 0)
			SetKeys.WIN_HEIGHT.setValue(height);
		
		if(height == 0 || width == 0)
			return;
		
		SetKeys.WIN_ASPECT_RATIO.setValue((float) width/ (float) height);
		
			/*Globals.windowMatrix.set(1/Globals.aspectRatio, 0.0f, 0.0f, 0.0f,
												  0.0f, 1.0f, 0.0f, 0.0f,
													  0.0f, 0.0f, 1.0f, 0.0f,
													  0.0f, 0.0f, 0.0f, 1.0f);*/
			
			//for(IWindowChangeListener lis : Globals.windowChangeListener)
			//{
			//	lis.onWindowResolutionChange(Globals.windowWidth, Globals.windowHeight);
			//}
		DisplayMode finalMode = new DisplayMode(width, height);
		try {
			DisplayMode[] modes = Display.getAvailableDisplayModes();
			
			
			
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
			Logger.createCrashDump("Unable to create/update display", "Settings.setWindowResolution", e, true, "Resolution: "+width+"x"+height+"\nFullscreen capable: "+finalMode.isFullscreenCapable());
			System.exit(-1);
		}
		Display.update();
		fire(SetKeys.WIN_RESOLUTION_CHANGED);
	}

	public static void setFullscreen(Boolean value) {
		SetKeys.WIN_FULLSCREEN.setValue(value);
		try {
			Display.setFullscreen(value);
			Display.update();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void addFont(BitmapFont f)
	{
		if(SetKeys.GUI_CMP_FONT.getValue(BitmapFont.class) == BitmapFont.EMPTY)
			SetKeys.GUI_CMP_FONT.setValue(f);
		Settings.availableFonts.put(f.getFontName().toLowerCase(), f);
	}
	
	public static void removeFont(BitmapFont f)
	{
		Settings.availableFonts.remove(f.getFontName().toLowerCase());
	}
	
	public static BitmapFont getFont(String name)
	{
		BitmapFont b = Settings.availableFonts.get(name.toLowerCase());
		if(b == null)
			return BitmapFont.EMPTY;
		else
			return b;
	}
	
	public static void flashDefaults()
	{
		for(SetKeys key : SetKeys.values())
		{
			if(key.getType() == SettingsType.SETTING || key.getType() == SettingsType.COMMAND_AND_SETTING)
			{
				Settings.set(key, key.getDefaultValue());
			}
		}
	}
}

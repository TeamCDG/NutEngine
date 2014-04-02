package cdg.nut.util.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cdg.nut.interfaces.ISettingsListener;

//TODO: Javadoc
public class Settings<T> {
	private static HashMap<String, Object> settings = new HashMap<String, Object>();
	private static List<ISettingsListener> listener = new ArrayList<ISettingsListener>();
	
	public static void set(SettingsKeys key, Object value)
	{
		Settings.set(key.name().toLowerCase(), value);
	}
	
	public static void set(String key, Object value)
	{
		settings.put(key, value);
		for(int i = 0; i < listener.size(); i++)
		{
			listener.get(i).valueChanged(SettingsKeys.valueOf(key.toUpperCase()), value);
		}
	}
	
	public static Object get(String key)
	{
		return Settings.get(key);
	}
	
	public static <T> T get(String key, Class<T> c)
	{
		return c.cast(Settings.get(key));
	}
	
	public static Object get(SettingsKeys key)
	{
		return Settings.get(key.name().toLowerCase());
	}
	
	public static <T> T get(SettingsKeys key, Class<T> c)
	{
		return get(key.name().toLowerCase(), c);
	}

	public static void removeListener(ISettingsListener lis) {
		listener.remove(lis);
	}

	public static void addListener(ISettingsListener lis) {
		Settings.listener.add(lis);
	}
}

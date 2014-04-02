package cdg.nut.util.settings;

import cdg.nut.util.BitmapFont;
import cdg.nut.util.gl.GLColor;

public enum SettingsKeys {
	
	//---- REGION WINDOW -----
	WIN_WIDTH("<int>", "width of the window in pixels", int.class, SettingsAccess.PUBLIC),
	WIN_HEIGHT("<int>", "height of the window in pixels", int.class, SettingsAccess.PUBLIC),
	WIN_FULLSCREEN("<boolean>", "true if window is fullscreen", boolean.class, SettingsAccess.PUBLIC),
	WIN_ASPECT_RATIO("<float>", "aspect ration of window", float.class, SettingsAccess.READONLY),
	WIN_RESOLUTION_CHANGED("", "event identifier", Object.class, SettingsAccess.PRIVATE),
	
	//---- REGION COMPONENTS -----
	CMP_BACKGROUND_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]", "default background color of components", GLColor.class, SettingsAccess.PUBLIC),
	CMP_BACKGROUND_H_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]", "default highlight background color of components", GLColor.class, SettingsAccess.PUBLIC),
	CMP_FRAME_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]", "default frame color of components", GLColor.class, SettingsAccess.PUBLIC),
	CMP_FRAME_H_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]", "default highlight frame color of components", GLColor.class, SettingsAccess.PUBLIC),
	CMP_FONT("<string>", "default font of components", BitmapFont.class, SettingsAccess.PUBLIC),
	CMP_FONT_SIZE("<int>/<float>", "default font of components", int.class, SettingsAccess.PUBLIC),
	CMP_FONT_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]", "default text color of components", GLColor.class, SettingsAccess.PUBLIC),
	CMP_FONT_H_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]", "default highlight text color of components", GLColor.class, SettingsAccess.PUBLIC),
	;
	
	private String parameterList;
	private String doc;
	private Class<?> cls;
	private SettingsAccess acc;
	
	SettingsKeys(String parameterList, String doc, Class<?> cls, SettingsAccess acc)
	{
		this.parameterList = parameterList;
		this.doc = doc;
		this.cls = cls;
		this.acc = acc;
	}

	public String getParameterList() {
		return parameterList;
	}

	public String getDoc() {
		return doc;
	}

	public Class<?> getCls() {
		return cls;
	}

	public SettingsAccess getAccess() {
		return acc;
	}


}

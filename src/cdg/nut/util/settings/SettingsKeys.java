package cdg.nut.util.settings;

import cdg.nut.util.BitmapFont;
import cdg.nut.util.gl.GLColor;

public enum SettingsKeys {
	
	//---- REGION WINDOW -----
	WIN_WIDTH("<int>", "width of the window in pixels", int.class),
	WIN_HEIGHT("<int>", "height of the window in pixels", int.class),
	WIN_FULLSCREEN("<boolean>", "true if window is fullscreen", boolean.class),
	
	//---- REGION COMPONENTS -----
	CMP_BACKGROUND_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]", "default background color of components", GLColor.class),
	CMP_BACKGROUND_H_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]", "default highlight background color of components", GLColor.class),
	CMP_FRAME_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]", "default frame color of components", GLColor.class),
	CMP_FRAME_H_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]", "default highlight frame color of components", GLColor.class),
	CMP_FONT("<string>", "default font of components", BitmapFont.class),
	CMP_FONT_SIZE("<int>/<float>", "default font of components", int.class),
	CMP_FONT_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]", "default text color of components", GLColor.class),
	CMP_FONT_H_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]", "default highlight text color of components", GLColor.class),
	;
	
	private String parameterList;
	private String doc;
	private Class<?> cls;
	
	SettingsKeys(String parameterList, String doc, Class<?> cls)
	{
		this.parameterList = parameterList;
		this.doc = doc;
		this.cls = cls;
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

}

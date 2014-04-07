package cdg.nut.util.settings;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;

import cdg.nut.interfaces.ICommandExecuter;
import cdg.nut.logging.Logger;
import cdg.nut.util.BitmapFont;
import cdg.nut.util.gl.GLColor;

public enum SetKeys {
	
	//---- REGION WINDOW -----
	WIN_WIDTH("<int>", "width of the window in pixels", 1280, new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			int x = Integer.parseInt(parameter.get(0));
			int y = Settings.get(SetKeys.WIN_HEIGHT) == null?0:Settings.get(SetKeys.WIN_HEIGHT, Integer.class);
			Settings.setWindowResolution(x, y);
		}}),
	WIN_HEIGHT("<int>", "height of the window in pixels", 720, new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			int y = Integer.parseInt(parameter.get(0));
			int x = Settings.get(SetKeys.WIN_WIDTH) == null?0:Settings.get(SetKeys.WIN_WIDTH, Integer.class);
			Settings.setWindowResolution(x, y);
		}}),
	WIN_FULLSCREEN("<boolean>", "true if window is fullscreen", false, new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			Settings.setFullscreen(Boolean.valueOf(parameter.get(0)));
		}}),
	WIN_ASPECT_RATIO("<float>", "aspect ration of window", 0.0f),
	WIN_RESOLUTION_CHANGED("the resolution of the window has changed"),
	WIN_RESOLUTION("<int> <int>", "set the window resolution", new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			Settings.setWindowResolution(Integer.parseInt(parameter.get(0)), Integer.parseInt(parameter.get(1)));
		}}),
	
	//----- REGION GUI ------
	GUI_CMP_BACKGROUND_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]/<string>", "default background color of components", new GLColor(0.3f, 0.3f, 0.3f, 0.8f)),
	GUI_CMP_BACKGROUND_H_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]/<string>", "default highlight background color of components", new GLColor(0.5f, 0.5f, 0.5f, 0.8f)),
	GUI_CMP_BORDER_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]/<string>", "default border color of components", new GLColor(1.0f, 1.0f, 1.0f, 1.0f)),
	GUI_CMP_BORDER_H_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]/<string>", "default highlight border color of components", new GLColor(1.0f, 1.0f, 1.0f, 1.0f)),
	GUI_CMP_BORDER_SIZE("<int>", "default size of borders", 2),
	GUI_CMP_FONT("<string>", "default font of components", Settings.getFont("consolas")),
	GUI_CMP_FONT_SIZE("<int>/<float>", "default font of components", 18),
	GUI_CMP_FONT_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]/<string>", "default text color of components", new GLColor(1.0f, 1.0f, 1.0f, 1.0f)),
	GUI_CMP_FONT_H_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]/<string>", "default highlight text color of components", new GLColor(1.0f, 1.0f, 1.0f, 1.0f)),
	
	//--- REGION RENDERER ---
	R_VSYNC("<boolean>", "true for vsync", true, new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			boolean value = parameter.get(0).toLowerCase().equals("true") || parameter.get(0).toLowerCase().equals("1");
			Display.setVSyncEnabled(value);
			Settings.setSuppress(SetKeys.R_VSYNC, value);
	}}),
	R_MAX_FPS("<int>", "maximal fps, -1 for infinite", 60)
	;
	
	private String parameterList;
	private String doc;
	private Class<?> cls;
	private SettingsType acc;
	private ArrayList<ICommandExecuter> ces;
	private Object defaultValue;
	
	SetKeys(String parameterList, String doc, Object defaultValue)
	{
		this.parameterList = parameterList;
		this.doc = doc;
		this.cls = defaultValue.getClass();
		this.defaultValue = defaultValue;
		this.acc = SettingsType.SETTING;
	}
	
	SetKeys(String doc, Object defaultValue)
	{
		this.doc = doc;
		this.cls = defaultValue.getClass();
		this.defaultValue = defaultValue;
		this.acc = SettingsType.READONLY;
	}
	
	SetKeys(String doc)
	{
		this.doc = doc;
		this.acc = SettingsType.EVENT;
	}
	
	SetKeys(String parameterList, String doc, ICommandExecuter dflt)
	{
		this.parameterList = parameterList;
		this.doc = doc;
		this.acc = SettingsType.COMMAND;
		this.ces = new ArrayList<ICommandExecuter>();
		this.ces.add(dflt);
	}
	
	SetKeys(String parameterList, String doc, Object defaultValue, ICommandExecuter dflt)
	{


		this.parameterList = parameterList;
		this.doc = doc;
		this.cls = defaultValue.getClass();
		this.defaultValue = defaultValue;
		this.acc = SettingsType.COMMAND_AND_SETTING;
		this.ces = new ArrayList<ICommandExecuter>();
		this.ces.add(dflt);
	}
	
	public void execute(List<String> parameter)
	{
		if(this.ces != null)
		{
			for(int i = 0; i < this.ces.size(); i++)
			{
				this.ces.get(i).exec(parameter);
			}
		}
	}
	
	public void addCommandExecuter(ICommandExecuter e)
	{
		this.ces.add(e);
	}
	
	public void removeCommandExecuter(ICommandExecuter e)
	{
		this.ces.remove(e);
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

	public SettingsType getType() {
		return acc;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}



}

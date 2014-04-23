package cdg.nut.util.settings;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import cdg.nut.interfaces.ICommandExecuter;
import cdg.nut.logging.Logger;
import cdg.nut.test.Main;
import cdg.nut.util.BitmapFont;
import cdg.nut.util.Utility;
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
	GUI_CMP_BACKGROUND_A_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]/<string>", "default active background color of components", new GLColor(0.3f, 0.3f, 0.3f, 0.8f)),
	GUI_CMP_BORDER_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]/<string>", "default border color of components", new GLColor(1.0f, 1.0f, 1.0f, 1.0f)),
	GUI_CMP_BORDER_H_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]/<string>", "default highlight border color of components", new GLColor(1.0f, 1.0f, 1.0f, 1.0f)),
	GUI_CMP_BORDER_A_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]/<string>", "default active border color of components", new GLColor(1.0f, 0.0f, 0.0f, 1.0f)),
	GUI_CMP_BORDER_SIZE("<int>", "default size of borders", 4),
	GUI_CMP_SCROLLBAR_SIZE("<int>", "default size of scrollbars", 10),
	GUI_CMP_SCROLLBAR_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]/<string>", "default border color of components", new GLColor(0.75f, 0.75f, 0.75f, 1.0f)),
	GUI_CMP_FONT("<string>", "default font of components", BitmapFont.EMPTY),
	GUI_CMP_FONT_PADDING("<int>", "default font padding of components", 8),
	GUI_CMP_SCROLL_XFALLBACK("<boolean>", "true to scroll xscrollbar with mousewheel if there is no yscrollbar", true),
	GUI_CMP_SCROLL_MWHEELFACTOR("<float>", "used to control the amout of pixel per scroll", 0.1f),
	GUI_MAX_SELECT_SKIP("<int>", "max frames without selection", 2),
	GUI_CMP_FONT_SIZE("<int>/<float>", "default font of components", Utility.pixelSizeToGLSize(0,18)[1], new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			try {
				if(Cmd.isInteger(parameter.get(0)))
				{
					SetKeys.GUI_CMP_FONT_SIZE.setValue(Utility.pixelSizeToGLSize(0,Integer.parseInt(parameter.get(0)))[1]);
				}
				else
				{
					SetKeys.GUI_CMP_FONT_SIZE.setValue(Float.parseFloat(parameter.get(0)));
				}
			} catch (Exception e) {
				Logger.error("Unable to change setting (gui_cmp_font_size): illegel parameter 0 ("+parameter.get(0)+")", "CommandExecuter.exec");
				return;
			}
			
		}}),
		
	GUI_TOOLTIP_FONT_SIZE("<int>/<float>", "default font size of tooltips", Utility.pixelSizeToGLSize(0,18)[1], new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			try {
				if(Cmd.isInteger(parameter.get(0)))
				{
					SetKeys.GUI_TOOLTIP_FONT_SIZE.setValue(Utility.pixelSizeToGLSize(0,Integer.parseInt(parameter.get(0)))[1]);
				}
				else
				{
					SetKeys.GUI_TOOLTIP_FONT_SIZE.setValue(Float.parseFloat(parameter.get(0)));
				}
			} catch (Exception e) {
				Logger.error("Unable to change setting (gui_tooltip_font_size): illegel parameter 0 ("+parameter.get(0)+")", "CommandExecuter.exec");
				return;
			}
				
		}}),
	GUI_TOOLTIP_BACKGROUND_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]/<string>", "default background color of components", new GLColor(0.2f, 0.2f, 0.2f, 1.0f)),
	GUI_TOOLTIP_FONT_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]/<string>", "default background color of components", new GLColor(1.0f, 1.0f, 1.0f, 1.0f)),
	GUI_CMP_FONT_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]/<string>", "default text color of components", new GLColor(1.0f, 1.0f, 1.0f, 1.0f)),
	GUI_CMP_FONT_H_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]/<string>", "default highlight text color of components", new GLColor(1.0f, 1.0f, 1.0f, 1.0f)),
	GUI_CMP_FONT_A_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]/<string>", "default active text color of components", new GLColor(1.0f, 1.0f, 1.0f, 1.0f)),
	
	//--- REGION RENDERER ---
	R_VSYNC("<boolean>", "true for vsync", true, new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			boolean value = parameter.get(0).toLowerCase().equals("true") || parameter.get(0).toLowerCase().equals("1");
			Display.setVSyncEnabled(value);
			Settings.setSuppress(SetKeys.R_VSYNC, value);
	}}),
	R_MAX_FPS("<int>", "maximal fps, -1 for infinite", 60),
	R_CLEAR_COLOR("-", "clear color buffer",new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	}}),
	R_CLEAR_DEPTH("-", "clear depth buffer",new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
	}}),
	R_CLEAR_BOTH("-", "clear color and depth buffer",new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}}),
	
	//--- REGION COMMANDS ---
	EXIT("", "exit", new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			Main.closeRequested = true;
	}}),
	
	QUIT("", "exit", new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			Main.closeRequested = true;
	}}),
	;
	
	private String parameterList;
	private String doc;
	private Class<?> cls;
	private SettingsType acc;
	private ArrayList<ICommandExecuter> ces;
	private Object defaultValue;
	private Object value;
	
	SetKeys(String parameterList, String doc, Object defaultValue)
	{
		this.parameterList = parameterList;
		this.doc = doc;
		this.cls = defaultValue.getClass();
		this.defaultValue = defaultValue;
		this.acc = SettingsType.SETTING;
		this.value = defaultValue;
		System.out.println("Value "+value);
	}
	
	SetKeys(String doc, Object defaultValue)
	{
		this.doc = doc;
		this.cls = defaultValue.getClass();
		this.defaultValue = defaultValue;
		this.acc = SettingsType.READONLY;
		this.value = defaultValue;
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
		this.value = defaultValue;
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

	public Object getValue() {
		return value;
	}
	
	public <T> T getValue(Class<T> cls) {
		return cls.cast(value);
	}

	public void setValue(Object value) {
		this.value = value;
	}

}

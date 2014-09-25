package cdg.nut.util.settings;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import cdg.nut.interfaces.ICommandExecuter;
import cdg.nut.logging.Logger;
import cdg.nut.test.Main;
import cdg.nut.util.BitmapFont;
import cdg.nut.util.Engine;
import cdg.nut.util.Matrix4x4;
import cdg.nut.util.Utility;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.net.ServerInfo;
import cdg.nut.util.net.Server;

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
	WIN_TITLE("<string>", "set window title", "Engine Update #7", new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			Display.setTitle(parameter.get(0));
		}}),
	WIN_ASPECT_RATIO("<float>", "aspect ration of window", 0.0f),
	WIN_RESOLUTION_CHANGED("the resolution of the window has changed"),
	WIN_RESOLUTION("<int> <int>", "set the window resolution", new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			Settings.setWindowResolution(Integer.parseInt(parameter.get(0)), Integer.parseInt(parameter.get(1)));
		}}),
	WIN_MATRIX("<matrix>", "window matrix", Matrix4x4.getIdentity()),
	
	//----- REGION GUI ------
	GUI_CMP_BACKGROUND_COLOR("<color>", "default background color of components", new GLColor(0.3f, 0.3f, 0.3f, 0.8f)),
	GUI_CMP_BACKGROUND_H_COLOR("<color>", "default highlight background color of components", new GLColor(0.5f, 0.5f, 0.5f, 0.8f)),
	GUI_CMP_BACKGROUND_A_COLOR("<color>", "default active background color of components", new GLColor(0.3f, 0.3f, 0.3f, 0.8f)),
	GUI_CMP_BACKGROUND_D_COLOR("<color>", "default background color of disabled components", new GLColor(0.3f, 0.3f, 0.3f, 0.8f)),
	GUI_CMP_BORDER_COLOR("<color>", "default border color of components", new GLColor(1.0f, 1.0f, 1.0f, 1.0f)),
	GUI_CMP_BORDER_H_COLOR("<color>", "default highlight border color of components", new GLColor(1.0f, 1.0f, 1.0f, 1.0f)),
	GUI_CMP_BORDER_A_COLOR("<color>", "default active border color of components", new GLColor(1.0f, 0.0f, 0.0f, 1.0f)),
	GUI_CMP_BORDER_D_COLOR("<color>", "default border color of disabled components", new GLColor(0.65f, 0.65f, 0.65f, 1.0f)),
	GUI_CMP_BORDER_SIZE("<int>", "default size of borders", 4),
	GUI_CMP_SCROLLBAR_SIZE("<int>", "default size of scrollbars", 10),
	GUI_TRACKBAR_SIZE("<int>", "default size of scrollbars", 10),
	GUI_CMP_SCROLLBAR_COLOR("<color>", "default border color of components", new GLColor(0.75f, 0.75f, 0.75f, 1.0f)),
	GUI_TRACKBAR_TRACK_COLOR("<color>", "default border color of components", new GLColor(0.75f, 0.75f, 0.75f, 1.0f)),
	GUI_TRACKBAR_TRACKLINE_COLOR("<color>", "default border color of components", new GLColor(0.0f, 0.8f, 0.0f, 1.0f)),
	GUI_TRACKBAR_COLOR("<color>", "default border color of components", new GLColor(1.0f, 1.0f, 1.0f, 1.0f)),
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
	GUI_TOOLTIP_BACKGROUND_COLOR("<color>", "default background color of components", new GLColor(0.2f, 0.2f, 0.2f, 1.0f)),
	GUI_PROGRESSBAR_COLOR("<color>", "default color of progress bars", new GLColor(0.0f, 0.8f, 0.0f, 1.0f)),
	GUI_CHECKBOX_CHECK_COLOR("<color>", "default color of check box check", new GLColor(0.0f, 0.8f, 0.0f, 1.0f)),
	GUI_RADIOBUTTON_CHECK_COLOR("<color>", "default color of radiobutton check", new GLColor(0.0f, 0.8f, 0.0f, 1.0f)),
	GUI_RADIOBUTTON_CORNER_COUNT("<int>", "corner count of radiobutton check", 8),
	GUI_TOOLTIP_FONT_COLOR("<color>", "default background color of components", new GLColor(1.0f, 1.0f, 1.0f, 1.0f)),
	GUI_CMP_FONT_COLOR("<color>", "default text color of components", new GLColor(1.0f, 1.0f, 1.0f, 1.0f)),
	GUI_CMP_FONT_H_COLOR("<color>", "default highlight text color of components", new GLColor(1.0f, 1.0f, 1.0f, 1.0f)),
	GUI_CMP_FONT_A_COLOR("<color>", "default active text color of components", new GLColor(1.0f, 1.0f, 1.0f, 1.0f)),
	GUI_CMP_FONT_D_COLOR("<color>", "default text color of disabled components", new GLColor(0.65f, 0.65f, 0.65f, 1.0f)),
	
	//--- REGION AI ---
	AI_PF_USE_RANDOM("<boolean>", "true if ai should use random elements in pathfinding", true),
	
	//--- REGION SERVER ---
	SV_PORT("<int>", "portnumber", 1337),
	SV_AUTOSAVE_INTERVAL("<int>", "autosave interval in minutes", 15),
	SV_TICKRATE("<int>", "ticks per second", 60),
	SV_PACKAGE_COMPRESSION_SIZE("<int>", "min data size for compression", 256),
	SV_MAX_PLAYER("<int>", "max player count", 4),
	SV_NAME("<string>", "server name", "a NutEngine game server"),
	SV_MOTD("<string>", "message of the day", "Welcome to the League of Nuts..."),
	
	
	//--- REGION CLIENT ---
	CL_TILE_OCCUPIED_COLOR("<color>", "default color of occupied tiles", new GLColor(1.0f, 0.0f, 0.0f, 1.0f)),
	CL_TILE_NORMAL_COLOR("<color>", "default color of tiles", new GLColor(1.0f, 1.0f, 1.0f, 1.0f)),
	CL_TILE_FREE_COLOR("<color>", "default color of free tiles", new GLColor(0.0f, 1.0f, 0.0f, 1.0f)),
	CL_TILE_SELECTED_COLOR("<color>", "default color of selected tiles", new GLColor(0.0f, 0.0f, 1.0f, 1.0f)),
	
	//--- REGION DEBUG ---
	DB_LOGGER("<boolean>", "true if logger should be enabled", true, new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			
				if(parameter.get(0).equals("true") || parameter.get(0).equals("1"))
				{
					SetKeys.DB_LOGGER.setValue(true);
					Logger.enable();
				}
				else if(parameter.get(0).equals("false") || parameter.get(0).equals("0"))
				{
					SetKeys.DB_LOGGER.setValue(true);
					Logger.enable();
				}
				else
				{
					Logger.error("Unable to change setting (db_logger): illegel parameter 0 ("+parameter.get(0)+")", "CommandExecuter.exec");
					return;
				}}}),
				
	DB_ENABLE_LOGGER("", "enable logger", new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			Logger.enable();
		}}),
		
	DB_DISABLE_LOGGER("", "disable logger", new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
				Logger.enable();
		}}),
			
		
	
	//--- REGION RENDERER ---
	R_DRAW("<boolean>", "true for vsync", true),
	
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
	R_ANTIALIAS("<int>", "antialias level", 0),
	
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
	
	SERV_INIT("", "[DEBUG] inits a server", new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			if(Engine.getServer() != null)
				Engine.getServer().shutdown();
			Engine.setServer(new Server());
			Engine.getServer().init();
	}}),
	
	SERVERINFO("<string>", "retrieve info from server", new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			String ip = parameter.get(0).split(":")[0];
			int port = parameter.get(0).split(":").length>1?Integer.parseInt(parameter.get(0).split(":")[1]):1337;
			
			ServerInfo info = ServerInfo.fromServer(ip, port);
			Logger.info(info.getPing()+"ms ---> "+ info.getName() +" ("+info.getPlayerCount()+"/"+info.getMaxPlayerCount()+"): \n"+info.getMotd());
			
	}}),
	
	CONSOLE_SHOW("", "shows console", new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			Engine.console.show();
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

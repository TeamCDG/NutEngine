package cdg.nut.util.settings;

import java.util.ArrayList;
import java.util.List;

import cdg.nut.interfaces.ICommandExecuter;
import cdg.nut.util.BitmapFont;
import cdg.nut.util.gl.GLColor;

public enum SetKeys {
	
	//---- REGION WINDOW -----
	WIN_WIDTH("<int>", "width of the window in pixels", int.class, new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			Settings.setWindowResolution(Integer.parseInt(parameter.get(0)), Settings.get(SetKeys.WIN_HEIGHT, int.class));
		}}),
	WIN_HEIGHT("<int>", "height of the window in pixels", int.class, new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			Settings.setWindowResolution(Settings.get(SetKeys.WIN_WIDTH, int.class), Integer.parseInt(parameter.get(0)));
		}}),
	WIN_FULLSCREEN("<boolean>", "true if window is fullscreen", boolean.class),
	WIN_ASPECT_RATIO("<float>", "aspect ration of window", float.class),
	WIN_RESOLUTION_CHANGED("the resolution of the window has changed"),
	WIN_RESOLUTION("<int> <int>", "set the window resolution", new ICommandExecuter(){
		@Override
		public void exec(List<String> parameter) {
			Settings.setWindowResolution(Integer.parseInt(parameter.get(0)), Integer.parseInt(parameter.get(1)));
		}}),
	
	//----- REGION GUI ------
	GUI_CMP_BACKGROUND_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]/<string>", "default background color of components", GLColor.class),
	GUI_CMP_BACKGROUND_H_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]/<string>", "default highlight background color of components", GLColor.class),
	GUI_CMP_FRAME_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]/<string>", "default frame color of components", GLColor.class),
	GUI_CMP_FRAME_H_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]/<string>", "default highlight frame color of components", GLColor.class),
	GUI_CMP_FONT("<string>", "default font of components", BitmapFont.class),
	GUI_CMP_FONT_SIZE("<int>/<float>", "default font of components", int.class),
	GUI_CMP_FONT_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]/<string>", "default text color of components", GLColor.class),
	GUI_CMP_FONT_H_COLOR("<int> <int> <int> [int]/<float> <float> <float> [float]/<string>", "default highlight text color of components", GLColor.class),
	;
	
	private String parameterList;
	private String doc;
	private Class<?> cls;
	private SettingsType acc;
	private ArrayList<ICommandExecuter> ces;
	
	SetKeys(String parameterList, String doc, Class<?> cls)
	{
		this.parameterList = parameterList;
		this.doc = doc;
		this.cls = cls;
		this.acc = SettingsType.SETTING;
	}
	
	SetKeys(String doc, Class<?> cls)
	{
		this.doc = doc;
		this.cls = cls;
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
	
	SetKeys(String parameterList, String doc, Class<?> cls, ICommandExecuter dflt)
	{
		this.parameterList = parameterList;
		this.doc = doc;
		this.cls = cls;
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


}

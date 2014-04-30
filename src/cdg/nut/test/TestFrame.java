package cdg.nut.test;

import java.util.LinkedList;
import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import cdg.nut.gui.Frame;
import cdg.nut.gui.ToolTip;
import cdg.nut.gui.components.Button;
import cdg.nut.gui.components.CheckBox;
import cdg.nut.gui.components.ColorBox;
import cdg.nut.gui.components.ImageBox;
import cdg.nut.gui.components.Label;
import cdg.nut.gui.components.ProgressBar;
import cdg.nut.gui.components.TextBox;
import cdg.nut.interfaces.ICheckedChangedListener;
import cdg.nut.interfaces.IClickListener;
import cdg.nut.interfaces.ICommandListener;
import cdg.nut.interfaces.IToolTipGenerator;
import cdg.nut.logging.Logger;
import cdg.nut.util.Colors;
import cdg.nut.util.MouseButtons;
import cdg.nut.util.Utility;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLTexture;
import cdg.nut.util.settings.Cmd;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;
import cdg.nut.util.settings.SettingsType;

public class TestFrame extends Frame {
	Button test;
	Button ctest;
	Label l;
	Label md;
	ColorBox cbox;
	ImageBox ibox;
	TextBox txt;
	TextBox pw;
	TextBox com;
	ProgressBar b;
	CheckBox cb;
	
	int bg = 0;
	
	public TestFrame()
	{
		super(new GLTexture("bg0.png",GL13.GL_TEXTURE0));
		this.test = new Button(420, 262, Colors.BLUE+"G"+Colors.RED+"o"+Colors.YELLOW+"o"+Colors.BLUE+"g"+Colors.DARKGREEN+"l"+Colors.RED+"e");
		this.test.setBackgroundHighlightColor(Colors.VIOLET.getGlColor());
		this.test.setBorderColor(Colors.LIGHTSKYBLUE.getGlColor());
		this.test.setTooltip("nonsens button");
		this.test.addClickListener(new IClickListener(){

			@Override
			public void onClick(int x, int y, MouseButtons button, int grabx, int graby) {
				l.setText(GLColor.random()+Utility.randomString());
				ctest.setPosition(new Random().nextInt(100), new Random().nextInt(100));
			}});
		this.addComponent(test);
		this.addComponent(new Button(820, 262, Colors.BLUE+"G"+Colors.RED+" "+Colors.YELLOW+"o"+Colors.BLUE+"g"+Colors.DARKGREEN+"l"+Colors.RED+"e"));
		this.addComponent(new Button(20, 162, Colors.BLUE+"G"+Colors.RED+"o"+Colors.YELLOW+"o"+Colors.BLUE+"g"+Colors.DARKGREEN+"l"+Colors.RED+"e"));
		
		this.ctest = new Button(420, 462, 100, 100, "change background");
		this.ctest.setPosition(10, 10);
		this.ctest.setTooltip("it's kind of magic... well black sorcery");
		this.ctest.addClickListener(new IClickListener(){

			@Override
			public void onClick(int x, int y, MouseButtons button, int grabx, int graby) {
				setRandomBg();
				
			}});
		this.ctest.setBorderHighlightColor(Colors.VIOLET.getGlColor());
		this.addComponent(ctest);
		
		this.l = new Label(150, 10, GLColor.random()+Utility.randomString());
		this.addComponent(l);
		
		this.md = new Label(340, 140, Mouse.getX()+"/"+Mouse.getY());
		this.md.setFontSize(14);
		this.addComponent(md);
		
		this.cbox = new ColorBox(10, 550, 50, 50, GLColor.random());
		this.addComponent(cbox);
		
		this.ibox = new ImageBox(820, 400, 200, 200, new GLTexture("grid0.png", GL13.GL_TEXTURE0));
		this.ibox.setDragable(true);
		this.ibox.setTooltip("ImageBox, dragable!");
		this.addComponent(ibox);
		
		this.txt = new TextBox(100, 300, test.getPixelWidth(), 2*test.getPixelHeight(), "");
		this.txt.setTooltip("yet another TextBox");
		this.addComponent(txt);
		
		this.pw = new TextBox(100+test.getPixelWidth()+50, 400, test.getPixelWidth(), test.getPixelHeight(), "");
		this.pw.setPasswordMode(true);
		this.pw.setTooltip("password goes here");
		this.addComponent(pw);
		
		this.com = new TextBox(420, 10, 700, 60, "");
		this.com.setFontSize(30);
		this.com.setCommandMode(true);
		this.com.setTooltip("CommandBox");
		this.com.addCommandListener(new ICommandListener(){

			@Override
			public void exec(String command) {
				Cmd.exec(command);
				
			}});
		this.com.settTGen(new IToolTipGenerator(){

			@Override
			public String[] generateToolTip(String text) {
				LinkedList<String> ret = new LinkedList<String>();
				for(SetKeys s: SetKeys.values())
				{
					if(s.name().toLowerCase().startsWith(text.toLowerCase().split(" ")[0]) && (s.getType() == SettingsType.COMMAND || s.getType() == SettingsType.COMMAND_AND_SETTING|| s.getType() == SettingsType.SETTING))
					{
						if(s.getParameterList() != null)
							ret.add(s.name().toLowerCase()+" "+
									(s.getParameterList().length()>30?s.getParameterList().substring(0,30)+"...":s.getParameterList())+
									(s.getDefaultValue()!=null?(" Default value: "+(s.getCls()!=GLColor.class?s.getDefaultValue().toString():((GLColor)s.getDefaultValue()).toReadableString())):""));
						//Logger.debug(s.name().toLowerCase()+" "+s.getParameterList()+" Default value: "+(s.getCls()!=GLColor.class?s.getDefaultValue().toString():((GLColor)s.getDefaultValue()).toReadableString()));
					}
					
					
				}
				
				return ret.toArray(new String[1]);
			}});
		this.addComponent(com);
		
		this.b = new ProgressBar(420,10+this.com.getPixelHeight()+10,700,50);
		this.b.setValue(57.378926f);
		this.b.setTooltip("basically the ProgressBar shows progress");
		this.add(b);
		
		this.cb = new CheckBox(420,b.getPixelY()+60,"disable CommandBox");
		this.cb.setTooltip("dat hook = op");
		this.cb.addCheckChangedListener(new ICheckedChangedListener(){

			@Override
			public void onCheckedChange(boolean value) {
				com.setEnabled(!value);
				
			}});
		this.cb.setFontSize(20);
		this.add(cb);
		
	}
	
	public void setRandomBg()
	{
		bg++;
		bg = bg%4;
		this.setBackground("bg"+bg+".png");
	}
	
	@Override
	public void draw()
	{
		super.draw();
		//this.md.setText(Mouse.getX()+"/"+(SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY()));
	}
}

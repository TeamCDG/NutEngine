package cdg.nut.gui;

import java.util.LinkedList;

import cdg.nut.gui.components.ColorBox;
import cdg.nut.gui.components.InnerWindow;
import cdg.nut.gui.components.RadioButton;
import cdg.nut.gui.components.TextBox;
import cdg.nut.interfaces.ICommandListener;
import cdg.nut.interfaces.IParent;
import cdg.nut.interfaces.IToolTipGenerator;
import cdg.nut.logging.Logger;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.settings.Cmd;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.SettingsType;

public class Console extends InnerWindow {
	
	private TextBox com;
	private ColorBox cb;
	private TextBox con;

	@Override
	public int setId(int id)
	{
		super.setId(id);
		return 10;
	}
	
	public Console()
	{
		super(0, 0, 200, 200, "Console");
		this.setFontSize(20);

		
		this.com = new TextBox(0, 0, "");
		this.com.setFontSize(16);
		this.com.setText("lawl");
		this.com.setText("");
		this.com.setWidth(this.getPixelWidth()-2*this.getBorderSize());
		this.com.setAutosizeWithText(false);
		this.com.setHasBorder(false);
		this.com.setCommandMode(true);
		this.com.setTooltip("CommandBox");
		this.com.setPosition(this.getPixelX(), this.getPixelY()+this.getPixelHeight()-2*this.getBorderSize()-this.com.getPixelHeight()-this.getHeadHeight());
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
		
		this.cb = new ColorBox(0, 0, this.getPixelWidth()-2*this.getBorderSize(), this.getBorderSize(), this.getBorderColor());
		this.cb.setScrollable(false);
		this.cb.setSelectable(false);
		this.cb.setPosition(this.getPixelX(), this.com.getPixelY()-this.getBorderSize()-this.getHeadHeight());
		this.add(cb);
		
		this.con = new TextBox(0, 0, this.getPixelWidth()-2*this.getBorderSize(), this.cb.getPixelY()-this.getHeadHeight()-this.getBorderSize(), "");
		this.con.setFontSize(13);
		this.con.setEditable(false);
		this.con.setHasBorder(false);
		this.con.setBackgroundHighlightColor(this.con.getBackgroundColor());
		this.add(con);
		
		//this.hide();
	}
	
	@Override
	public void setDimension(float w, float h)
	{
		super.setDimension(w, h);
		this.com.setWidth(this.getPixelWidth()-2*this.getBorderSize());
		this.com.setPosition(this.getPixelX()+this.getBorderSize(), this.getPixelY()+this.getPixelHeight()-this.getBorderSize()-this.com.getPixelHeight());
		
		this.cb.setWidth(this.getPixelWidth()-2*this.getBorderSize());
		this.cb.setPosition(this.getPixelX()+this.getBorderSize(), this.com.getPixelY()-this.getBorderSize());
		
		this.con.setWidth(this.getPixelWidth()-2*this.getBorderSize());
		this.con.setHeight(this.getPixelHeight()-this.com.getPixelHeight()-this.getHeadHeight()-3*this.getBorderSize());
	}
	
	@Override
	public void setParent(IParent p)
	{
		if(this.getParent() != null)
			this.getParent().remove(this);
		
		super.setParent(p);
	}
	
	public void println(String text)
	{
		boolean b = Logger.isDisabled();
		if(!b) Logger.disable();
		this.con.setText(this.con.getColortext()+text+"\n");
		if(!b) Logger.enable();
		
		this.con.getYsb().setScrollValue(this.con.getYsb().getMaxValue());
	}
}

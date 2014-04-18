package cdg.nut.test;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import cdg.nut.gui.Frame;
import cdg.nut.gui.components.Button;
import cdg.nut.gui.components.ColorBox;
import cdg.nut.gui.components.ImageBox;
import cdg.nut.gui.components.Label;
import cdg.nut.gui.components.TextBox;
import cdg.nut.interfaces.IClickListener;
import cdg.nut.interfaces.ICommandListener;
import cdg.nut.util.Colors;
import cdg.nut.util.MouseButtons;
import cdg.nut.util.Utility;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLTexture;
import cdg.nut.util.settings.Cmd;
import cdg.nut.util.settings.SetKeys;

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
	
	public TestFrame()
	{
		super(new GLTexture("bg.png",GL13.GL_TEXTURE0));
		this.test = new Button(420, 262, Colors.BLUE+"G"+Colors.RED+"o"+Colors.YELLOW+"o"+Colors.BLUE+"g"+Colors.DARKGREEN+"l"+Colors.RED+"e");
		this.test.setBackgroundHighlightColor(Colors.VIOLET.getGlColor());
		this.test.setBorderColor(Colors.LIGHTSKYBLUE.getGlColor());
		this.test.addClickListener(new IClickListener(){

			@Override
			public void onClick(int x, int y, MouseButtons button, int grabx, int graby) {
				l.setText(GLColor.random()+Utility.randomString());
							
			}});
		this.addComponent(test);
		this.addComponent(new Button(820, 262, Colors.BLUE+"G"+Colors.RED+" "+Colors.YELLOW+"o"+Colors.BLUE+"g"+Colors.DARKGREEN+"l"+Colors.RED+"e"));
		this.addComponent(new Button(20, 162, Colors.BLUE+"G"+Colors.RED+"o"+Colors.YELLOW+"o"+Colors.BLUE+"g"+Colors.DARKGREEN+"l"+Colors.RED+"e"));
		
		this.ctest = new Button(420, 462, 100, 100, "penispenispenis");
		this.ctest.setPosition(10, 10);
		this.ctest.setBorderHighlightColor(Colors.VIOLET.getGlColor());
		this.addComponent(ctest);
		
		this.l = new Label(150, 10, GLColor.random()+Utility.randomString());
		this.addComponent(l);
		
		this.md = new Label(340, 140, Mouse.getX()+"/"+Mouse.getY());
		this.md.setFontSize(14);
		this.addComponent(md);
		
		this.cbox = new ColorBox(10, 550, 50, 50, GLColor.random());
		this.addComponent(cbox);
		
		this.ibox = new ImageBox(420, 10, 200, 200, new GLTexture("grid0.png", GL13.GL_TEXTURE0));
		this.ibox.setDragable(true);
		this.addComponent(ibox);
		
		this.txt = new TextBox(100, 300, test.getPixelWidth(), 2*test.getPixelHeight(), "");
		this.addComponent(txt);
		
		this.pw = new TextBox(100+test.getPixelWidth()+50, 400, test.getPixelWidth(), test.getPixelHeight(), "");
		this.pw.setPasswordMode(true);
		this.addComponent(pw);
		
		this.com = new TextBox(100, 570, 700, 60, "");
		this.com.setFontSize(30);
		this.com.setCommandMode(true);
		this.com.addCommandListener(new ICommandListener(){

			@Override
			public void exec(String command) {
				Cmd.exec(command);
				
			}});
		this.addComponent(com);
	}
	
	@Override
	public void draw()
	{
		super.draw();
		//this.md.setText(Mouse.getX()+"/"+(SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY()));
	}
}

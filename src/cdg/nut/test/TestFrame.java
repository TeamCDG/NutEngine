package cdg.nut.test;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import cdg.nut.gui.Frame;
import cdg.nut.gui.components.Button;
import cdg.nut.gui.components.ColorBox;
import cdg.nut.gui.components.ImageBox;
import cdg.nut.gui.components.Label;
import cdg.nut.interfaces.IClickListener;
import cdg.nut.util.Colors;
import cdg.nut.util.MouseButtons;
import cdg.nut.util.Utility;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLTexture;

public class TestFrame extends Frame {
	Button test;
	Button ctest;
	Label l;
	ColorBox cbox;
	ImageBox ibox;
	
	public TestFrame()
	{
		super(new GLTexture("bg.png",GL13.GL_TEXTURE0));
		this.test = new Button(420, 262, Colors.BLUE+"G"+Colors.RED+"o"+Colors.YELLOW+"o"+Colors.BLUE+"g"+Colors.DARKGREEN+"l"+Colors.RED+"e");
		this.test.setBackgroundHighlightColor(Colors.VIOLET.getGlColor());
		this.test.setBorderColor(Colors.LIGHTSKYBLUE.getGlColor());
		this.test.addClickListener(new IClickListener(){

			@Override
			public void onClick(int x, int y, MouseButtons button) {
				l.setText(GLColor.random()+Utility.randomString());
							
			}});
		this.addComponent(test);
		this.addComponent(new Button(820, 262, Colors.BLUE+"G"+Colors.RED+"o"+Colors.YELLOW+"o"+Colors.BLUE+"g"+Colors.DARKGREEN+"l"+Colors.RED+"e"));
		this.addComponent(new Button(20, 262, Colors.BLUE+"G"+Colors.RED+"o"+Colors.YELLOW+"o"+Colors.BLUE+"g"+Colors.DARKGREEN+"l"+Colors.RED+"e"));
		
		this.ctest = new Button(420, 462, 100, 100, "penispenispenis");
		this.ctest.setPosition(10, 10);
		this.ctest.setBorderHighlightColor(Colors.VIOLET.getGlColor());
		this.addComponent(ctest);
		
		this.l = new Label(400, 10, GLColor.random()+Utility.randomString());
		this.addComponent(l);
		
		this.cbox = new ColorBox(10, 550, 50, 50, GLColor.random());
		this.addComponent(cbox);
		
		this.ibox = new ImageBox(700, 10, 200, 200, new GLTexture("grid0.png", GL13.GL_TEXTURE0));
		this.ibox.setDragable(true);
		this.addComponent(ibox);
	}
}

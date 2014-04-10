package cdg.nut.test;

import org.lwjgl.opengl.GL13;

import cdg.nut.gui.Frame;
import cdg.nut.gui.components.Button;
import cdg.nut.util.Colors;
import cdg.nut.util.gl.GLTexture;

public class TestFrame extends Frame {
	Button test;
	Button ctest;
	
	public TestFrame()
	{
		super(new GLTexture("bg.png",GL13.GL_TEXTURE0));
		this.test = new Button(420, 262, Colors.BLUE+"G"+Colors.RED+"o"+Colors.YELLOW+"o"+Colors.BLUE+"g"+Colors.DARKGREEN+"l"+Colors.RED+"e");
		this.addComponent(test);
		this.addComponent(new Button(820, 262, Colors.BLUE+"G"+Colors.RED+"o"+Colors.YELLOW+"o"+Colors.BLUE+"g"+Colors.DARKGREEN+"l"+Colors.RED+"e"));
		this.addComponent(new Button(20, 262, Colors.BLUE+"G"+Colors.RED+"o"+Colors.YELLOW+"o"+Colors.BLUE+"g"+Colors.DARKGREEN+"l"+Colors.RED+"e"));
		
		this.ctest = new Button(420, 462, 100, 100, "penispenispenis");
		this.addComponent(ctest);
	}
}

package cdg.nut.gui;

import cdg.nut.interfaces.IDrawable;
import cdg.nut.util.Utility;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLImage;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class Border implements IDrawable {

	private GLImage northBorder;
	private GLImage eastBorder;
	private GLImage southBorder;
	private GLImage westBorder;
	
	public Border(float x, float y, float width, float height)
	{
		float sx = Utility.pixelSizeToGLSize(Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class),0)[0];
		float sy = Utility.pixelSizeToGLSize(0,Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class))[1];
		this.northBorder = new GLImage(Settings.get(SetKeys.GUI_CMP_BORDER_COLOR, GLColor.class), x, y, width, sy);
		this.eastBorder = new GLImage(Settings.get(SetKeys.GUI_CMP_BORDER_COLOR, GLColor.class), x+(width-sx), y, sx, height);
		this.southBorder = new GLImage(Settings.get(SetKeys.GUI_CMP_BORDER_COLOR, GLColor.class), x, y-(height-sy), width, sy);
		this.westBorder = new GLImage(Settings.get(SetKeys.GUI_CMP_BORDER_COLOR, GLColor.class), x, y, sx, height);
	}
	
	public Border(int x, int y, int width, int height)
	{
		this.northBorder = new GLImage(Settings.get(SetKeys.GUI_CMP_BORDER_COLOR, GLColor.class), x, y, width, Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class));
		this.eastBorder = new GLImage(Settings.get(SetKeys.GUI_CMP_BORDER_COLOR, GLColor.class), x+(width-Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class)), y, Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class), height);
		this.southBorder = new GLImage(Settings.get(SetKeys.GUI_CMP_BORDER_COLOR, GLColor.class), x, y-(height-Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class)), width, Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class));
		this.westBorder = new GLImage(Settings.get(SetKeys.GUI_CMP_BORDER_COLOR, GLColor.class), x, y, Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class), height);
	}
	
	@Override
	public void draw() {
		northBorder.draw();
		eastBorder.draw();
		southBorder.draw();
		westBorder.draw();		
	}
	
	public void setPosition(int x, int y)
	{
		this.northBorder.setPosition(x,y);
		this.eastBorder.setPosition(x,y);
		this.southBorder.setPosition(x,y);
		this.westBorder.setPosition(x,y);
	}
}

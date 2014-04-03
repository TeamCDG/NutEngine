package cdg.nut.gui;

import cdg.nut.interfaces.ISelectable;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLImage;
import cdg.nut.util.gl.GLObject;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

//TODO: this class needs some love
public abstract class Component extends GLImage {
	
	private Border border;
	
	
	public Component(float x, float y, float width, float height)
	{
		super(Settings.get(SetKeys.GUI_CMP_BACKGROUND_COLOR, GLColor.class), x, y, width, height);		
		this.border = new Border(x, y, width, height);
	}
	
	public Component(int x, int y, int width, int height)
	{
		super(Settings.get(SetKeys.GUI_CMP_BACKGROUND_COLOR, GLColor.class), x, y, width, height);
		this.border = new Border(x, y, width, height);
	}
	
	@Override
	protected void move()
	{
		super.move();
		this.border.setPosition(this.getPixelX(), this.getPixelY());
	}
	
	@Override
	protected void drawChildren()
	{
		super.drawChildren();
		this.border.draw();
	}
}

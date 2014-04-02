package cdg.nut.gui;

import cdg.nut.interfaces.ISelectable;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLImage;
import cdg.nut.util.gl.GLObject;
import cdg.nut.util.settings.Settings;

//TODO: this class needs some love
public abstract class Component extends GLImage {
	
	public Component(float x, float y, float width, float height)
	{
		super(Settings.get("component_background_color", GLColor.class), x, y, width, height);
		
		
	}
	
	public Component(int x, int y, int width, int height)
	{
		super(new GLColor(1.0f, 1.0f, 1.0f, 1.0f), x, y, width, height);
	}
}

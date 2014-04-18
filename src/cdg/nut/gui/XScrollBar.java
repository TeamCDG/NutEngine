package cdg.nut.gui;

import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLImage;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class XScrollBar extends GLImage{
	
	public XScrollBar(int x, int y, int width) {
		super(Settings.get(SetKeys.GUI_CMP_SCROLLBAR_COLOR, GLColor.class), x, y, width, Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class));
		// TODO Auto-generated constructor stub
	}

	private GLImage scroll;
	
	private int scrollValue = 0;
	private int maxValue = 0;

	public int getScrollOfAbs(int x)
	{
		return this.getScrollOf(x-this.getPixelX());
	}
	
	public int getScrollOf(int x)
	{
		return -1;
	}
	
	public int getScrollValue() {
		return scrollValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
		
		int sw = Math.max(Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class), Math.round((float)this.getPixelWidth() / this.maxValue)-1);
		if(this.scroll == null)
			this.scroll = new GLImage(GLColor.random(), this.getPixelX(), this.getPixelY(), sw, Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class));
		else
		{
			this.scroll.setPosition(getPixelX(), this.getPixelY());
			this.scroll.setDimension(sw, Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class));
		}
	}
	
	@Override
	protected void drawChildren(boolean selection)
	{
		if(this.scroll != null) this.scroll.draw();
	}
}

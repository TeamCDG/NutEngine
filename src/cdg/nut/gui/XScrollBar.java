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
		int sw = (int) (this.maxValue / this.getPixelWidth());
	}
}

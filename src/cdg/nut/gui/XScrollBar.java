package cdg.nut.gui;

import java.util.ArrayList;
import java.util.List;

import cdg.nut.interfaces.IClickListener;
import cdg.nut.interfaces.IScrollListener;
import cdg.nut.logging.Logger;
import cdg.nut.util.Colors;
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
	private List<IScrollListener> scrollListener = new ArrayList<IScrollListener>();

	public int getScrollOfAbs(int x)
	{
		return this.getScrollOf(x-this.getPixelX());
	}
	
	public int getScrollOf(int x)
	{
		return ((this.getPixelWidth()-this.scroll.getPixelWidth())/this.maxValue)*x;
	}
	
	private int getScrollPixel(int value)
	{
		int sz = Math.round((((float)this.getPixelWidth()-(float)this.scroll.getPixelWidth())/(float)this.maxValue)*(float)value);
		Logger.debug("ScrollPosition: "+sz,"XScrollBar.getScrollPixel");
		return sz;
		
		//return Math.round(((float)this.maxValue/(float)(this.getPixelWidth()-(float)this.scroll.getPixelWidth()))*(float)value);
	}
	
	public int getScrollValue() {
		return scrollValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void removeScrollListener(IScrollListener scrollListener) {
		this.scrollListener.remove(scrollListener);
	}

	public void addScrollListener(IScrollListener scrollListener) {
		this.scrollListener.add(scrollListener);
	}

	
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
		
		int sw = Math.max(Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class), this.getPixelWidth() - this.maxValue);
		if(this.scroll == null)
			this.scroll = new GLImage(Colors.GRAY50.getGlColor(), this.getPixelX(), this.getPixelY(), sw, Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class));
		else
		{
			//this.scroll.setPosition(getPixelX(), this.getPixelY());
			this.scroll.setDimension(sw, Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class));
		}
	}
	
	@Override
	protected void drawChildren(boolean selection)
	{
		if(this.scroll != null) this.scroll.draw();
	}

	public void setScrollValue(int value) {
		this.scrollValue = value;
		if(this.scroll != null) this.scroll.setX(this.getPixelX()+this.getScrollPixel(value));
		
		for(int i = 0; i < this.scrollListener.size(); i++)
		{
			this.scrollListener.get(i).onScroll(this.scrollValue, true);
		}
	}
	
	
	
	
}

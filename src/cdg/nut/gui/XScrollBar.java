package cdg.nut.gui;

import java.util.ArrayList;
import java.util.List;

import cdg.nut.interfaces.IClickListener;
import cdg.nut.interfaces.IScrollListener;
import cdg.nut.logging.Logger;
import cdg.nut.util.Utility;
import cdg.nut.util.enums.Colors;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLPolygon;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class XScrollBar extends GLPolygon {
	
	public XScrollBar(int x, int y, int width) {
		super(x, y, width, Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class));
		this.setColor(Settings.get(SetKeys.GUI_CMP_SCROLLBAR_COLOR, GLColor.class));
		this.width = width;
	}

	private boolean doublescroll = false;
	private int width;
	
	private GLPolygon scroll;
	
	private int scrollValue = 0;
	private int maxValue = 0;
	private List<IScrollListener> scrollListener = new ArrayList<IScrollListener>();

	public int getScrollOfAbs(int x)
	{
		return this.getScrollOf(x-this.getPixelX());
	}
	
	public int getScrollOf(int x)
	{
		return Math.max(0, Math.min(this.maxValue,Math.round((float)this.maxValue/((float)(this.getPixelWidth()-this.scroll.getPixelWidth()))*(float)x)));
	}
	
	private int getScrollPixel(int value)
	{
		if(this.scroll != null)
		{
			int sz = Math.round((((float)this.getPixelWidth()-(float)this.scroll.getPixelWidth())/(float)this.maxValue)*(float)value);
			Logger.debug("ScrollPosition: "+sz,"XScrollBar.getScrollPixel");
			return Math.max(0,Math.min(sz, this.getPixelWidth()-this.scroll.getPixelWidth()));
		}
		return 0;
		
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
		{
			this.scroll = new GLPolygon(this.getPixelX(), this.getPixelY(), sw, Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class));
			this.scroll.setColor(Colors.GRAY50.getGlColor());
		}
		else
		{
			//this.scroll.setPosition(getPixelX(), this.getPixelY());
			this.scroll.setDimension(sw, Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class));
		}
	}
	
	@Override
	protected void drawChildren(boolean selection)
	{
		if(this.scroll != null){ if(this.scroll.getPixelY()!= this.getPixelY()) this.scroll.setPosition(getPixelX()+this.getScrollPixel(this.scrollValue), this.getPixelY()); this.scroll.draw();}
	}

	public void setScrollValue(int value) {
		this.scrollValue = Math.max(0, Math.min(this.maxValue,value));
		
		Logger.debug("new value: "+scrollValue+" / is max value: "+(this.maxValue==this.scrollValue),"XScrollBar.setScrollValue");
		
		if(this.scroll != null) this.scroll.setPosition(this.getPixelX()+this.getScrollPixel(value), this.getPixelY());
		
		for(int i = 0; i < this.scrollListener.size(); i++)
		{
			this.scrollListener.get(i).onScroll(this.scrollValue, true);
		}
	}
	
	public boolean isScrollDings(int x, int y)
	{

		//Logger.debug("x: "+x+" / y: "+y+" / tx: "+this.scroll.getPixelX()+" / ty: "+this.scroll.getPixelY()+" / txw: "+(this.scroll.getPixelX()+this.scroll.getPixelWidth())+" / tyw: "+(this.scroll.getPixelY()+this.scroll.getPixelHeight())
		//		+" / xbt: "+Utility.between(x, this.scroll.getPixelX(), this.scroll.getPixelX()+this.scroll.getPixelWidth())+" / ybt: "+Utility.between(y, this.scroll.getPixelY(), this.scroll.getPixelY()+this.scroll.getPixelHeight()),"XScrollBar.isScrollDings");
		
		//return !(x >= this.scroll.getPixelX() || x <= this.scroll.getPixelX()+this.scroll.getPixelWidth() &&
		//		y <= this.scroll.getPixelY() || y <= this.scroll.getPixelY()+this.scroll.getPixelHeight());
		if(this.scroll == null)
			return false; 
		
		return (Utility.between(x, this.scroll.getPixelX(), this.scroll.getPixelX()+this.scroll.getPixelWidth()) && Utility.between(y, this.scroll.getPixelY(), this.scroll.getPixelY()+this.scroll.getPixelHeight()));
	}
	
	public boolean isScrollBar(int x, int y)
	{
		//Logger.debug("x: "+x+" / y: "+y+" / tx: "+this.getPixelX()+" / ty: "+this.getPixelY()+" / txw: "+(this.getPixelX()+this.getPixelWidth())+" / tyw: "+(this.getPixelY()+this.getPixelHeight())
		//		+" / xbt: "+Utility.between(x, this.getPixelX(), this.getPixelX()+this.getPixelWidth())+" / ybt: "+Utility.between(y, this.getPixelY(), this.getPixelY()+this.getPixelHeight()),"XScrollBar.isScrollBar");
		
		return (Utility.between(x, this.getPixelX(), this.getPixelX()+this.getPixelWidth()) && Utility.between(y, this.getPixelY(), this.getPixelY()+this.getPixelHeight()));
	}

	public void setDoublescroll(boolean doublescroll) {
		
		if(doublescroll != this.doublescroll)
		{
			if(doublescroll)
			{
				this.setDimension(this.width-Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class), Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class));
			}
			else
			{
				this.setDimension(this.width, Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class));
			}
			this.setMaxValue(this.maxValue);
			this.setScrollValue(this.scrollValue);
		}
		this.doublescroll = doublescroll;
		
	}
	
	@Override
	public void setDimension(float w, float h)
	{
		float nw = w;
		
		if(this.doublescroll)
		{
			nw -= Utility.pixelSizeToGLSize(Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class), 0)[0];
		}
		
		super.setDimension(nw, h);
				
		this.setScrollValue(this.scrollValue);
	}
	
	
	@Override
	public void move(float x, float y)
	{
		super.move(x, y);
		this.setScrollValue(this.getScrollValue());
	
	}
}

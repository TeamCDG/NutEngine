package cdg.nut.gui;

import java.util.ArrayList;
import java.util.List;

import cdg.nut.interfaces.IScrollListener;
import cdg.nut.logging.Logger;
import cdg.nut.util.Colors;
import cdg.nut.util.Utility;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLImage;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class YScrollBar extends GLImage{
	
	
	public YScrollBar(int x, int y, int height) {
		super(Settings.get(SetKeys.GUI_CMP_SCROLLBAR_COLOR, GLColor.class), x, y, Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class), height);
		// TODO Auto-generated constructor stub
	}

	private GLImage scroll;
	
	private int scrollValue = 0;
	private int maxValue = 0;
	private List<IScrollListener> scrollListener = new ArrayList<IScrollListener>();

	public int getScrollOfAbs(int y)
	{
		return this.getScrollOf(y-this.getPixelY());
	}
	
	public int getScrollOf(int y)
	{
		return Math.max(0, Math.min(this.maxValue,Math.round((float)this.maxValue/((float)(this.getPixelHeight()-this.scroll.getPixelHeight()))*(float)y)));
	}
	
	private int getScrollPixel(int value)
	{
		if(this.scroll != null)
		{
			int sz = Math.round((((float)this.getPixelHeight()-(float)this.scroll.getPixelHeight())/(float)this.maxValue)*(float)value);
			Logger.debug("ScrollPosition: "+sz,"YScrollBar.getScrollPixel");
			return sz;
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
		
		int sh = Math.max(Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class), this.getPixelHeight() - this.maxValue);
		if(this.scroll == null)
			this.scroll = new GLImage(Colors.GRAY50.getGlColor(), this.getPixelX(), this.getPixelY(), Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class), sh);
		else
		{
			//this.scroll.setPosition(getPixelX(), this.getPixelY());
			this.scroll.setDimension(Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class), sh);
		}
	}
	
	@Override
	protected void drawChildren(boolean selection)
	{
		if(this.scroll != null){ if(this.scroll.getPixelX()!= this.getPixelX()) this.scroll.setPosition(getPixelX(), this.getPixelY()+this.getScrollPixel(this.scrollValue)); this.scroll.draw();}
	}

	public void setScrollValue(int value) {
		this.scrollValue = Math.max(0, Math.min(this.maxValue,value));
		
		Logger.debug("new value: "+scrollValue+" / is max value: "+(this.maxValue==this.scrollValue),"YScrollBar.setScrollValue");
		
		if(this.scroll != null) this.scroll.setPosition(this.getPixelX(), this.getPixelY()+this.getScrollPixel(value));
		
		for(int i = 0; i < this.scrollListener.size(); i++)
		{
			this.scrollListener.get(i).onScroll(this.scrollValue, false);
		}
	}
	
	public boolean isScrollDings(int x, int y)
	{

		//Logger.debug("x: "+x+" / y: "+y+" / tx: "+this.scroll.getPixelX()+" / ty: "+this.scroll.getPixelY()+" / txw: "+(this.scroll.getPixelX()+this.scroll.getPixelWidth())+" / tyw: "+(this.scroll.getPixelY()+this.scroll.getPixelHeight())
		//		+" / xbt: "+Utility.between(x, this.scroll.getPixelX(), this.scroll.getPixelX()+this.scroll.getPixelWidth())+" / ybt: "+Utility.between(y, this.scroll.getPixelY(), this.scroll.getPixelY()+this.scroll.getPixelHeight()),"YScrollBar.isScrollDings");
		
		//return !(x >= this.scroll.getPixelX() || x <= this.scroll.getPixelX()+this.scroll.getPixelWidth() &&
		//		y <= this.scroll.getPixelY() || y <= this.scroll.getPixelY()+this.scroll.getPixelHeight());
		return (Utility.between(x, this.scroll.getPixelX(), this.scroll.getPixelX()+this.scroll.getPixelWidth()) && Utility.between(y, this.scroll.getPixelY(), this.scroll.getPixelY()+this.scroll.getPixelHeight()));
	}
	
	public boolean isScrollBar(int x, int y)
	{
		//Logger.debug("x: "+x+" / y: "+y+" / tx: "+this.getPixelX()+" / ty: "+this.getPixelY()+" / txw: "+(this.getPixelX()+this.getPixelWidth())+" / tyw: "+(this.getPixelY()+this.getPixelHeight())
		//		+" / xbt: "+Utility.between(x, this.getPixelX(), this.getPixelX()+this.getPixelWidth())+" / ybt: "+Utility.between(y, this.getPixelY(), this.getPixelY()+this.getPixelHeight()),"YScrollBar.isScrollBar");
		
		return (Utility.between(x, this.getPixelX(), this.getPixelX()+this.getPixelWidth()) && Utility.between(y, this.getPixelY(), this.getPixelY()+this.getPixelHeight()));
	}
}

package cdg.nut.gui.components;

import java.util.ArrayList;
import java.util.List;

import cdg.nut.gui.Component;
import cdg.nut.util.Utility;
import cdg.nut.util.enums.Colors;
import cdg.nut.util.enums.MouseButtons;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLImage;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;
import cdg.nut.interfaces.IFloatListener;

public class TrackBar extends Component{

	private GLImage track;
	private GLImage trackline;
	private GLImage bar;
	
	private float value = 0;
	private float minValue = 0;
	private float maxValue = 100;
	
	private List<IFloatListener> listener;
	
	public int getTrackPixel(float value)
	{
		int sz = Math.round((((float)this.getPixelWidth()-(float)this.track.getPixelWidth())/(float)this.maxValue)*(float)value);
		return Math.max(0,Math.min(sz, this.getPixelWidth()-this.track.getPixelWidth()));
	}
	
	public TrackBar(int x, int y, int width, int height) {
		super(x, y, width, height);
		
		
		
		this.setup();
		
		
		//this.setValue(0);
	}
	
	/**
	 * @return the value
	 */
	public float getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(float value) {
		this.value = Math.max(this.minValue, Math.min(this.maxValue,value));
		this.track.setPosition(this.getPixelX()+this.getTrackPixel(this.value), this.getPixelY());
		this.trackline.setDimension(this.getTrackPixel(this.value), this.bar.getPixelHeight());
		for(int i = 0; i < this.listener.size(); i++)
		{
			this.listener.get(i).onChange(this.value);
		}
	}

	/**
	 * @return the minValue
	 */
	public float getMinValue() {
		return minValue;
	}

	/**
	 * @param minValue the minValue to set
	 */
	public void setMinValue(float minValue) {
		this.minValue = minValue;
		this.setValue(this.value);
	}

	public TrackBar(float x, float y, float width, float height) {
		super(x, y, width, height);

		this.setup();
	}
	
	private void setup()
	{
		int sz = Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class);
		int bs = Settings.get(SetKeys.GUI_TRACKBAR_SIZE, Integer.class);
			
		this.track = new GLImage(Settings.get(SetKeys.GUI_TRACKBAR_TRACK_COLOR, GLColor.class), this.getPixelX(), this.getPixelY(), bs, this.getPixelHeight());
		this.trackline = new GLImage(Settings.get(SetKeys.GUI_TRACKBAR_TRACKLINE_COLOR, GLColor.class), this.getPixelX(), this.getPixelY()+((this.getPixelHeight()-bs)/2), 0,0);
		this.bar = new GLImage(Settings.get(SetKeys.GUI_TRACKBAR_COLOR, GLColor.class), this.getPixelX(), this.getPixelY()+((this.getPixelHeight()-bs)/2), this.getPixelWidth(), bs);
		
		this.listener = new ArrayList<IFloatListener>();
		
		this.setHasBackground(false);
		this.setHasBorder(false);
		this.setScrollable(false);
		this.setTextSelectable(true);
	}
	
	private boolean trackGrabbed = false;
	
	@Override
	protected void onClick(int x, int y, MouseButtons button, boolean grabbed, int grabx, int graby){
		if(this.isTrack(x, y) && grabbed)
		{
			this.trackGrabbed = true;
			this.setValue(this.getValueOfAbs(x));
		}
		else if(this.trackGrabbed && grabbed )
		{
			this.setValue(this.getValueOfAbs(x));
		}
		else if(this.isBar(x, y))
		{
			this.setValue(this.getValueOfAbs(x));
		}
		else
		{
			this.trackGrabbed = false;
		}
	}
	
	private float getValueOfAbs(int val) {
		// TODO Auto-generated method stub
		return this.getValueOf(val-this.getPixelX());
	}
	
	private float getValueOf(int val)
	{
		return Math.max(this.minValue, Math.min(this.maxValue,(float)this.maxValue/((float)(this.getPixelWidth()-this.track.getPixelWidth()))*(float)val));
	}

	@Override
	protected void drawChildren(boolean selection)
	{
		if(!selection)
		{
			
			this.bar.draw();
			this.trackline.draw();
			this.track.draw();
		}
	}
	
	public float getMaxValue()
	{
		return this.maxValue;
	}
	
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
		
		
		//int sw = Math.round(Math.max(Settings.get(SetKeys.GUI_TRACKBAR_SIZE, Integer.class), this.getPixelWidth() - this.maxValue));
		//this.track.setDimension(sw, Settings.get(SetKeys.GUI_TRACKBAR_SIZE, Integer.class));
		
		this.setValue(this.value);
	}
	
	public boolean isTrack(int x, int y)
	{
		return (Utility.between(x, this.track.getPixelX(), this.track.getPixelX()+this.track.getPixelWidth()) && Utility.between(y, this.track.getPixelY(), this.track.getPixelY()+this.track.getPixelHeight()));
	}
	
	public boolean isBar(int x, int y)
	{
		return (Utility.between(x, this.getPixelX(), this.getPixelX()+this.getPixelWidth()) && Utility.between(y, this.getPixelY(), this.getPixelY()+this.getPixelHeight()));
	}
	
	@Override
	protected void move(float x, float y)
	{
		
	}
	
	public void addValueChangeListener(IFloatListener lis)
	{
		this.listener.add(lis);
	}
	
	public void removeValueChangeListener(IFloatListener lis)
	{
		this.listener.remove(lis);
	}
	
}

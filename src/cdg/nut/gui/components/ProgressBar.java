package cdg.nut.gui.components;

import cdg.nut.gui.Component;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLImage;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class ProgressBar extends Component{

	public ProgressBar(int x, int y, int width, int height) {
		super(x, y, width, height, "elfiehfs");
		
		int sz = Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class);
		this.progress = new GLImage(Settings.get(SetKeys.GUI_PROGRESSBAR_COLOR, GLColor.class), this.getPixelX()+sz, this.getPixelY()+sz, 0,0);
		
		this.setScrollable(false);
		this.setCenterText(true);
		this.setFontSize(this.getPixelHeight()-2*(Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class)+Settings.get(SetKeys.GUI_CMP_FONT_PADDING, Integer.class)));
		
		this.setValue(0);
	}
	
	public ProgressBar(float x, float y, float width, float height) {
		super(x, y, width, height, "wdzuagfua");

		int sz = Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class);
		this.progress = new GLImage(Settings.get(SetKeys.GUI_PROGRESSBAR_COLOR, GLColor.class), this.getPixelX()+sz, this.getPixelY()+sz, 0,0);
		
		this.setScrollable(false);
		this.setCenterText(true);
		this.setFontSize(this.getPixelHeight()-2*(Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class)+Settings.get(SetKeys.GUI_CMP_FONT_PADDING, Integer.class)));
		
		this.setValue(0);
	}
	
	@Override
	public void setSelected(boolean value)
	{
		
	}
	
	private boolean horizontal = true;
	private boolean percentage = true;
	
	private GLImage progress;
	
	private float minValue = 0;
	private float maxValue = 100;
	private float value = 0;
	
	private int decPlaces = 2;
	
	public boolean isHorizontal() {
		return horizontal;
	}
	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
	}
	public boolean isPercentage() {
		return percentage;
	}
	public void setPercentage(boolean percentage) {
		this.percentage = percentage;
	}
	public float getMinValue() {
		return minValue;
	}
	public void setMinValue(float minValue) {
		this.minValue = minValue;
	}
	public float getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = Math.min(this.maxValue, Math.max(this.minValue, (float)Math.round(value*Math.pow(10, this.decPlaces))/(float)Math.pow(10, this.decPlaces)));
		
		int sz = Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class);
		
		if(this.horizontal)
			this.progress.setDimension(Math.round((float)(this.getPixelWidth()-2*sz)/this.maxValue*this.value), this.getPixelHeight()-2*sz);
		else
			this.progress.setDimension(this.getPixelWidth()-2*sz, Math.round((float)(this.getPixelHeight()-2*sz)/this.maxValue*this.value));
		
		if(this.percentage)
			this.setText((100.0f/this.maxValue*this.value)+"%");
		else
			this.setText(this.value+" / "+this.getMaxValue());
	}

	public int getDecPlaces() {
		return decPlaces;
	}

	public void setDecPlaces(int decPlaces) {
		this.decPlaces = decPlaces;
	}
	
	@Override
	protected void drawChildren(boolean selection)
	{
		
		
		if(!selection) this.progress.draw();
		
		super.drawChildren(selection);
		
	}
	
}

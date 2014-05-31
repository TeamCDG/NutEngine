package cdg.nut.gui.components;

import static cdg.nut.util.VertexUtility.angle;
import static cdg.nut.util.VertexUtility.intersectionPoint;
import static cdg.nut.util.VertexUtility.substract;

import java.util.ArrayList;
import java.util.List;

import cdg.nut.gui.Component;
import cdg.nut.interfaces.ICheckedChangedListener;
import cdg.nut.logging.Logger;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex2;
import cdg.nut.util.Vertex4;
import cdg.nut.util.VertexData;
import cdg.nut.util.enums.MouseButtons;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLPolygon;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class RadioButton extends Component{

	private GLPolygon check;
	private GLPolygon circle;
	private GLPolygon circleBorder;
	
	private boolean first = true;
	
	private int cornerCount = Settings.get(SetKeys.GUI_RADIOBUTTON_CORNER_COUNT, Integer.class);
	
	private List<ICheckedChangedListener> checkChangedListener = new ArrayList<ICheckedChangedListener>();
	
	private boolean checked = false;
	
	public RadioButton(int x, int y, int width, int height, String text) {
		super(x, y, width, height, text);
		this.setup();
	}
	
	public RadioButton(float x, float y, float width, float height, String text) {
		super(x, y, width, height, text);
		this.setup();
	}
	
	public RadioButton(int x, int y, String text) {
		super(x, y, text);
		this.setup();
	}
	
	public RadioButton(float x, float y, String text) {
		super(x, y, text);
		this.setup();
	}
	
	@Override
	public void setDimension(float width, float height) {
		super.setDimension(width, height);
		
		this.setupChilds();
	}
	
	private void setupChilds() {
		float radius = (float)Math.min(this.getPixelHeight(), this.getFO().getPixelHeight())/2.0f;
		float s = radius <= 14?Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class)/2:Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class);
		this.circle = new GLPolygon(this.getPixelX(), this.getPixelY(), (int)radius*2, (int)radius*2, false, radius, this.cornerCount);
		this.circle.setColor(Settings.get(SetKeys.GUI_CMP_BACKGROUND_COLOR, GLColor.class));
		
		this.circleBorder = new GLPolygon(this.getPixelX(), this.getPixelY(), (int)radius*2, (int)radius*2, false, radius, radius, this.cornerCount, 0, (int)s);
		this.circleBorder.setColor(this.getBorderColor());
		
		this.check= new GLPolygon(this.getPixelX()+(int)(2*s), this.getPixelY()+(int)(2*s), (int)radius*2, (int)radius*2, false, radius-2*s, this.cornerCount);
		this.check.setColor(Settings.get(SetKeys.GUI_RADIOBUTTON_CHECK_COLOR, GLColor.class));
		
		this.setAdditionalPadding(Math.round(radius*2)+Settings.get(SetKeys.GUI_CMP_FONT_PADDING, Integer.class), 0);
		
	}

	@Override
	public void setSelected(boolean b)
	{
		super.setSelected(b);
		
		//Logger.debug("value: "+b,"CheckBox.setSelected");
		
		if(b)
		{
			this.circleBorder.setColor(this.getBorderHighlightColor());
			this.circle.setColor(this.getBackgroundHighlightColor());
		}
		else
		{
			this.circleBorder.setColor(this.getBorderColor());
			this.circle.setColor(this.getBackgroundColor());
		}
	}
	
	@Override
	public void onClick(int x, int y, MouseButtons button, boolean grabbed, int grabx, int graby) {
		
		List<RadioButton> l = this.getParent().getComponents(RadioButton.class);
		for(int i = 0; i < l.size(); i++)
		{
			if(l.get(i) != this)
				l.get(i).setChecked(false);
		}
		
		this.setChecked(!this.checked);
	}
	
	@Override
	protected void drawChildren(boolean selection)
	{

		super.drawChildren(selection);
		
		if(!selection || first) this.circle.draw();
		if(!selection || first) this.circleBorder.draw();
		if(!selection && checked) this.check.draw();
		
		first = false;
	}
	
	private void setup()
	{
		this.setScrollable(false);
		this.setHasBackground(false);
		this.setHasBorder(false);
		
		this.setupChilds();
		/*
		this.generateBackground();
		this.generateCircle();
		this.generateCheck();
		*/
	}
	
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		
		this.checked = checked;
		
		
		for(int i = 0; i < this.checkChangedListener.size(); i++)
		{
			this.checkChangedListener.get(0).onCheckedChange(this.checked); 
		}
	}

	

	public void addCheckChangedListener(ICheckedChangedListener checkChangedListener) {
		this.checkChangedListener.add(checkChangedListener);
	}

	public void removeCheckChangedListener(ICheckedChangedListener checkChangedListener) {
		this.checkChangedListener.remove(checkChangedListener);
	}

	public int getCornerCount() {
		return cornerCount;
	}

	public void setCornerCount(int cornerCount) {
		this.cornerCount = cornerCount;
				
		this.circle.setEdgeCount(cornerCount);
		this.circleBorder.setEdgeCount(cornerCount);
		this.check.setEdgeCount(cornerCount);
	}
	
	@Override
	protected void move(float x, float y)
	{
		super.move(x,y);
		
		this.setupChilds();
	}

	
}

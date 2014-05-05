package cdg.nut.gui.components;

import static cdg.nut.util.VertexUtility.angle;
import static cdg.nut.util.VertexUtility.intersectionPoint;
import static cdg.nut.util.VertexUtility.substract;

import java.util.ArrayList;
import java.util.List;

import cdg.nut.gui.Border;
import cdg.nut.gui.Component;
import cdg.nut.interfaces.ICheckedChangedListener;
import cdg.nut.logging.Logger;
import cdg.nut.util.MouseButtons;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex2;
import cdg.nut.util.Vertex4;
import cdg.nut.util.VertexData;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLImage;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class RadioButton extends Component{

	private GLImage check;
	private GLImage circle;
	private GLImage circleBorder;
	
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
		
		this.generateBackground();
		this.generateCircle();
		this.generateCheck();
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
		
		if(!selection) this.circle.draw();
		if(!selection) this.circleBorder.draw();
		if(!selection && checked) this.check.draw();
	}
	
	private void setup()
	{
		this.setScrollable(false);
		this.setHasBackground(false);
		this.setHasBorder(false);
		
		
		this.generateBackground();
		this.generateCircle();
		this.generateCheck();
	}
	
	private void generateBackground()
	{			
		int cc = this.cornerCount;		
		float radius = (float)Math.min(this.getPixelHeight(), this.getFO().getPixelHeight())/2.0f;
		float alpha = Utility.rad(360.0f/cc);
		Vertex2 center = new Vertex2(this.getPixelX()+radius, this.getPixelY()+radius);
		List<Vertex4> points = new ArrayList<Vertex4>((cc)*2);
		float rad = alpha/2.0f;
		
		points.add(Utility.toGL(new Vertex4(center)));
		
		for(int i = 0; i < cc; i++)
		{
			float px = center.getX()+(float) (Math.cos(rad) * radius);
			float py = center.getY()+(float) (Math.sin(rad) * radius);
			
			points.add(Utility.toGL(new Vertex4(px,py)));
			
			Logger.debug("P: "+i+" / c: "+cc+" / deg: "+rad+" / px: "+px+" / py: "+py+" / cx: "+center.getX()+" / cy: "+center.getY()+" / r: "+radius, "Radiobutton.generateBackground");
			
			rad+=alpha;
		}
		
		VertexData[] data = new VertexData[points.size()];
		for(int i = 0; i < points.size(); i++)
		{
			data[i] = new VertexData(points.get(i));
		}
				
		int[] ind = new int[cc*3];
		for(int i = 0; i < cc; i++)
		{
			if(i+1 < cc)
			{
				ind[i*3+0] = i+1;
				ind[i*3+1] = i+2;
				ind[i*3+2] = 0;
			}
			else
			{
				ind[i*3+0] = i+1;
				ind[i*3+1] = 1;
				ind[i*3+2] = 0;
			}
		}
		
		this.circle = new GLImage(Settings.get(SetKeys.GUI_CMP_BACKGROUND_COLOR, GLColor.class), radius*2, radius*2, data, ind);
		
		this.setAdditionalPadding(Math.round(radius*2)+Settings.get(SetKeys.GUI_CMP_FONT_PADDING, Integer.class), 0);
	}
	
	private void generateCircle()
	{
		int cc = this.cornerCount;
		
		float radius = (float)Math.min(this.getPixelHeight(), this.getFO().getPixelHeight())/2.0f;
		float s = radius <= 10?Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class)/2:Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class);
		float alpha = Utility.rad(360.0f/cc);
		Vertex2 center = new Vertex2(this.getPixelX()+radius, this.getPixelY()+radius);
		List<Vertex4> points = new ArrayList<Vertex4>((cc)*2);
		float rad = alpha/2.0f;
		
		for(int i = 0; i < cc; i++)
		{
			float px = center.getX()+(float) (Math.cos(rad) * radius);
			float py = center.getY()+(float) (Math.sin(rad) * radius);
			
			float plx = center.getX()+(float) (Math.cos(rad) * (radius-s));
			float ply = center.getY()+(float) (Math.sin(rad) * (radius-s));
			
			points.add(Utility.toGL(new Vertex4(px,py)));
			points.add(Utility.toGL(new Vertex4(plx,ply)));
			
			Logger.debug("P: "+i+" / c: "+cc+" / deg: "+rad+" / px: "+px+" / py: "+py+" / plx: "+plx+" / ply: "+ply+" / cx: "+center.getX()+" / cy: "+center.getY()+" / r: "+radius, "RadioButton.generateCircle");
			
			rad+=alpha;
		}
		
		VertexData[] data = new VertexData[points.size()];
		for(int i = 0; i < points.size(); i++)
		{
			data[i] = new VertexData(points.get(i));
		}
				
		int[] ind = new int[cc*6];
		for(int i = 0; i < cc; i++)
		{
			
			if(i+1 < cc)
			{
				ind[i*6+0] = i*2+0;
				ind[i*6+1] = i*2+1;
				ind[i*6+2] = i*2+2;
				ind[i*6+3] = i*2+2;
				ind[i*6+4] = i*2+3;
				ind[i*6+5] = i*2+1;
			}
			else
			{
				ind[i*6+0] = i*2+0;
				ind[i*6+1] = i*2+1;
				ind[i*6+2] = 0;
				ind[i*6+3] = 0;
				ind[i*6+4] = 1;
				ind[i*6+5] = i*2+1;
			}
		}
		
		this.circleBorder = new GLImage(Settings.get(SetKeys.GUI_CMP_BORDER_COLOR, GLColor.class), this.circle.getHeight(), this.circle.getWidth(), data, ind);
	}
	
	private void generateCheck() 
	{
		int cc = this.cornerCount;		

		float radius = (float)Math.min(this.getPixelHeight(), this.getFO().getPixelHeight())/2.0f;
		float s = radius <= 10?Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class)/2:Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class);
		
		float alpha = Utility.rad(360.0f/cc);
		Vertex2 center = new Vertex2(this.getPixelX()+radius, this.getPixelY()+radius);
		List<Vertex4> points = new ArrayList<Vertex4>((cc)*2);
		float rad = alpha/2.0f;
		
		points.add(Utility.toGL(new Vertex4(center)));
		
		radius -= 2*s;
		
		for(int i = 0; i < cc; i++)
		{
			float px = center.getX()+(float) (Math.cos(rad) * radius);
			float py = center.getY()+(float) (Math.sin(rad) * radius);
			
			points.add(Utility.toGL(new Vertex4(px,py)));
			
			Logger.debug("P: "+i+" / c: "+cc+" / deg: "+rad+" / px: "+px+" / py: "+py+" / cx: "+center.getX()+" / cy: "+center.getY()+" / r: "+radius, "Radiobutton.generateBackground");
			
			rad+=alpha;
		}
		
		VertexData[] data = new VertexData[points.size()];
		for(int i = 0; i < points.size(); i++)
		{
			data[i] = new VertexData(points.get(i));
		}
				
		int[] ind = new int[cc*3];
		for(int i = 0; i < cc; i++)
		{
			
			if(i+1 < cc)
			{
				ind[i*3+0] = i+1;
				ind[i*3+1] = i+2;
				ind[i*3+2] = 0;
			}
			else
			{
				ind[i*3+0] = i+1;
				ind[i*3+1] = 1;
				ind[i*3+2] = 0;
			}
		}
		
		this.check = new GLImage(Settings.get(SetKeys.GUI_RADIOBUTTON_CHECK_COLOR, GLColor.class), this.circle.getHeight(), this.circle.getWidth(), data, ind);
		
	}
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		
		if(checked && !this.checked) this.generateCheck();
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
		
		this.generateBackground();
		this.generateCircle();
		this.generateCheck();
	}
	
	@Override
	protected void move()
	{
		super.move();
		
		this.generateBackground();
		this.generateCircle();
		this.generateCheck();
	}

	
}

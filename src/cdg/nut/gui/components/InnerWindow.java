package cdg.nut.gui.components;

import cdg.nut.gui.Component;
import cdg.nut.logging.Logger;
import cdg.nut.util.DefaultShader;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex4;
import cdg.nut.util.enums.Colors;
import cdg.nut.util.gl.GLPolygon;
import cdg.nut.util.gl.GLTexture;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class InnerWindow extends Component {

	Panel contentPane;
	private int headHeight = 20;
	private boolean sizegrab;
	private boolean rgrab;
	private boolean bgrab;
	private boolean lgrab;
	private boolean blgrab;
	private boolean brgrab;
	
	public InnerWindow(int x, int y, int width, int height, String text) {
		super(x, y, width, height, text);
		setup();
		
	}
	
	
	protected void setup()
	{
		this.contentPane = new Panel(this.getPixelX()+this.getBorderSize(), this.getPixelY()+this.headHeight+this.getBorderSize(), this.getPixelWidth()-2*+this.getBorderSize(), this.getPixelHeight()-2*this.getBorderSize()-this.headHeight);
		this.contentPane.setHasBorder(false);
		this.contentPane.setHasBackground(true);
		this.setHasBorder(true);
		this.setHasBackground(true);
		this.setSelectable(true);
		this.setDragable(true);
		
	}
	
	@Override
	public void dragged(int mx, int my)
	{
		int x = this.getParent().getMouseGrabSX();
		int y = this.getParent().getMouseGrabSY();
		
		this.sizegrab = true;
		
		//Logger.debug("x: "+x+" / y: "+y+" / px: "+this.getPixelX()+" / py: "+this.getPixelY()+" / pw: "+this.getPixelWidth()+" / ph: "+this.getPixelHeight()+" / pwx: "+(this.getPixelX()+this.getPixelWidth())+" / phx: "+(this.getPixelY()+this.getPixelHeight())+" / bs: "+this.getBorderSize());
		//Logger.debug("i1: "+Utility.between(x, this.getPixelX(), this.getPixelX()+this.getBorderSize())+" / i2: "+Utility.between(x, this.getPixelX()+this.getWidth()-2*this.getBorderSize(), this.getPixelX()+this.getWidth()));
		//Logger.debug("x: "+x+" / v1: "+(this.getPixelX()+this.getWidth()-2*this.getBorderSize())+" / v2: "+(this.getPixelX()+this.getWidth()));
		
		if(Utility.between(x, this.getPixelX(), this.getPixelX()+this.getBorderSize()) && Utility.between(y, this.getPixelY(), this.getPixelY()+this.getPixelHeight()))
		{
			
		}
		else if((this.rgrab || Utility.between(x, this.getPixelX()+this.getPixelWidth(), this.getPixelX()+this.getPixelWidth()-this.getBorderSize())) && Utility.between(y, this.getPixelY(), this.getPixelY()+this.getPixelHeight()))
		{
			//Logger.debug("Section 2");
			this.rgrab = true;
			this.setDimension(this.getPixelWidth()-mx, this.getPixelHeight());
			this.getBorder().setDimension(this.getPixelWidth(), this.getPixelHeight());
			//this.setPosition(this.getPixelX()-mx, this.getPixelY());
		}
		else
		{
			super.dragged(mx, my);
		}
	}

	@Override
	protected void setTextClipping(boolean b)
	{
		this.getFO().setClippingArea(new Vertex4(this.getX(), this.getY(), this.getX()+this.getWidth(), this.getY()+this.getHeight()));

		this.setAddPadY(-5);
	}
	
	@Override
	public void drawChildren(boolean selection)
	{
		super.drawChildren(selection);
		
		if(this.sizegrab)
		{
			this.sizegrab = this.getParent().isMouseGrabbed();
			
			if(this.rgrab)
				this.rgrab = this.sizegrab;
			
			if(this.lgrab)
				this.lgrab = this.sizegrab;
			
			if(this.bgrab)
				this.bgrab = this.sizegrab;
			
			if(this.brgrab)
				this.brgrab = this.sizegrab;
			
			if(this.blgrab)
				this.blgrab = this.sizegrab;
		}
		
		if(selection)
			this.contentPane.drawSelect();
		else
			this.contentPane.draw();
	}
	
	@Override
	protected void move(float x, float y)
	{
		super.move(x, y);
		
		Logger.debug("moving panel to new position...");
		
		this.contentPane.setPosition(this.getPixelX()+this.getBorderSize(), this.getPixelY()+this.headHeight+this.getBorderSize());
	}
	
}

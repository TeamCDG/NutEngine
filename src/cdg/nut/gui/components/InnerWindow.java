package cdg.nut.gui.components;

import java.util.ArrayList;

import cdg.nut.gui.Component;
import cdg.nut.interfaces.IPolygonGenerator;
import cdg.nut.logging.Logger;
import cdg.nut.util.DefaultShader;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex4;
import cdg.nut.util.VertexData;
import cdg.nut.util.enums.Colors;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLPolygon;
import cdg.nut.util.gl.GLTexture;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class InnerWindow extends Component implements IPolygonGenerator{

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
		this.headHeight = this.getFO().getPixelHeight();
		
		this.contentPane = new Panel(this.getPixelX()+this.getBorderSize(), this.getPixelY()+this.headHeight+this.getBorderSize(), this.getPixelWidth()-2*+this.getBorderSize(), this.getPixelHeight()-2*this.getBorderSize()-this.headHeight);
		this.contentPane.setHasBorder(false);
		this.contentPane.setHasBackground(true);
		this.getBorder().setId(this.getId());
		this.getBorder().setGen(new IPolygonGenerator(){

			@Override
			public VertexData[] generateData(float x, float y, float width,
					float height) {
				float sx = Utility.pixelSizeToGLSize(getBorderSize(), 0)[0];
				float sy = Utility.pixelSizeToGLSize(0, getBorderSize())[1];
				
				float[] miPos = Utility.pixelToGL(getPixelX()+getBorderSize(), getPixelY()+headHeight);
				float[] miSize = Utility.pixelSizeToGLSize(getPixelWidth()-2*getBorderSize(), getBorderSize());
				
				ArrayList<VertexData> res = new ArrayList<VertexData>(20);
				
				Vertex4[][] p = new Vertex4[5][4];
				
				p[0] = Utility.generateQuadPoints(x, y, width, sy);
				p[1] = Utility.generateQuadPoints(x+(width-sx), y+sy, sx, height-2*sy);
				p[2] = Utility.generateQuadPoints(x, y+(height-sy), width, sy);
				p[3] = Utility.generateQuadPoints(x, y+sy, sx, height-2*sy);
				p[4] = Utility.generateQuadPoints(miPos[0], miPos[1], miSize[0], miSize[1]);
				
				Vertex4[] points = new Vertex4[20];
				for(int i1 = 0; i1 < 5; i1++)
				{
					for(int i2 = 0; i2 < 4; i2++)
					{
						points[i1*4+i2] = p[i1][i2];
					}
				}
				
				VertexData[] data = new VertexData[points.length];
				for(int i = 0; i < points.length; i++)
				{
					data[i] = new VertexData(points[i], new GLColor(getId()));
				}
				
				return data;
			}

			@Override
			public int[] generateIndicies() {
				return Utility.createQuadIndicesInt(5);
			}});
		this.setHasBorder(true);
		this.setHasBackground(true);
		this.setSelectable(true);
		this.setDragable(true);
		this.setGen(this);
		this.setAdditionalPadding(0, -5);
	}
	
	@Override
	public int setId(int id)
	{
		super.setId(id);
		this.getBorder().setId(this.getId());
		return 1;
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
			
			if(this.getPixelWidth()-mx > this.getBorderSize()*2)
			{
				this.rgrab = true;
				this.setDimension(this.getPixelWidth()-mx, this.getPixelHeight());
				this.getBorder().setDimension(this.getPixelWidth(), this.getPixelHeight());
			}
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
			this.getBorder().drawSelect();
		
		if(selection)
			this.contentPane.drawSelect();
		else
			this.contentPane.draw();
	}
	
	@Override
	public void setDimension(float w, float h)
	{
		super.setDimension(w, h);
		this.contentPane.setDimension(this.getPixelWidth()-2*+this.getBorderSize(), this.getPixelHeight()-2*this.getBorderSize()-this.headHeight);
	}
	
	@Override
	public void setFontSize(int f)
	{
		super.setFontSize(f);
		this.onFS();
	}
	
	@Override
	public void setFontSize(float f)
	{
		super.setFontSize(f);
		this.onFS();
		
	}
	
	private void onFS()
	{
		this.headHeight = this.getFO().getPixelHeight()-this.getAddPadY()+5;
		this.regen();
		this.getBorder().regen();
		this.contentPane.setPosition(this.getPixelX()+this.getBorderSize(), this.getPixelY()+this.headHeight+this.getBorderSize());
		this.contentPane.setDimension(this.getPixelWidth()-2*+this.getBorderSize(), this.getPixelHeight()-2*this.getBorderSize()-this.headHeight);
	}
	
	@Override
	protected void move(float x, float y)
	{
		super.move(x, y);
		
		Logger.debug("moving panel to new position...");
		Logger.debug("hh: "+this.headHeight+" / fh: "+this.getFO().getPixelHeight());
		this.contentPane.setPosition(this.getPixelX()+this.getBorderSize(), this.getPixelY()+this.headHeight+this.getBorderSize());
	}


	@Override
	public VertexData[] generateData(float x, float y, float width, float height) {
		float hh = Utility.pixelSizeToGLSize(0,  this.headHeight)[1];
		return Utility.generateQuadData(this.getX(), this.getY(), this.getWidth(), hh, new GLColor(this.getId()));
	}


	@Override
	public int[] generateIndicies() {
		return Utility.createQuadIndicesInt(1);
	}
	
}

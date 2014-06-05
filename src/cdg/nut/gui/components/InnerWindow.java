package cdg.nut.gui.components;

import java.util.ArrayList;
import java.util.List;

import cdg.nut.gui.Component;
import cdg.nut.interfaces.IParent;
import cdg.nut.interfaces.IPolygonGenerator;
import cdg.nut.logging.Logger;
import cdg.nut.util.DefaultShader;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex2;
import cdg.nut.util.Vertex4;
import cdg.nut.util.VertexData;
import cdg.nut.util.enums.Colors;
import cdg.nut.util.enums.MouseButtons;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLPolygon;
import cdg.nut.util.gl.GLTexture;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class InnerWindow extends Component implements IPolygonGenerator{

	Panel contentPane;
	GLPolygon cross;
	GLPolygon crossBorder;
	private int headHeight = 20;
	private boolean sizegrab;
	private boolean rgrab;
	private boolean bgrab;
	private boolean lgrab;
	private boolean blgrab;
	private boolean brgrab;
	private boolean movegrab;
	
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
		
		this.cross = new GLPolygon(this.getPixelX()+this.getPixelWidth()-headHeight, this.getPixelY()+this.getBorderSize(), 
									this.headHeight-this.getBorderSize(), this.headHeight-this.getBorderSize(), 
									new IPolygonGenerator(){

										@Override
										public VertexData[] generateData(
												float x, float y, float width,
												float height) {
											float bs = getBorderSize();
											float pl = (float) (bs * Math.sin(Utility.rad(45)));
											
											int lx = getPixelX()+getPixelWidth()-headHeight;
											int ly = getPixelY()+getBorderSize();
											
											int rx = lx + headHeight-getBorderSize();
											int ry = ly + headHeight-getBorderSize();
											
											Vertex4[] p = new Vertex4[8];
											p[0] = Utility.toGL(new Vertex4(lx+pl, ly));
											p[1] = Utility.toGL(new Vertex4(lx, ly+pl));
											p[2] = Utility.toGL(new Vertex4(rx-pl, ry));
											p[3] = Utility.toGL(new Vertex4(rx, ry-pl));
											
											p[4] = Utility.toGL(new Vertex4(rx-pl, ly));
											p[5] = Utility.toGL(new Vertex4(rx, ly+pl));
											p[6] = Utility.toGL(new Vertex4(lx+pl, ry));
											p[7] = Utility.toGL(new Vertex4(lx, ry-pl));
											
											VertexData[] data = new VertexData[p.length];
											for(int i = 0; i < p.length; i++)
											{
												data[i] = new VertexData(p[i], new GLColor(0));
											}
											
											return data;
										}

										@Override
										public int[] generateIndicies() {
											return  Utility.createQuadIndicesInt(2);
										}});
		this.cross.setColor(Colors.RED.getGlColor());
		this.crossBorder = new GLPolygon(this.getPixelX()+this.getPixelWidth()-this.getBorderSize()-headHeight, this.getPixelY(), 
				this.headHeight+this.getBorderSize(), this.headHeight+this.getBorderSize(),0, false, this.getBorderSize());
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
		
		if(!this.movegrab)
			this.sizegrab = true;
		
		//Logger.debug("x: "+x+" / y: "+y+" / px: "+this.getPixelX()+" / py: "+this.getPixelY()+" / pw: "+this.getPixelWidth()+" / ph: "+this.getPixelHeight()+" / pwx: "+(this.getPixelX()+this.getPixelWidth())+" / phx: "+(this.getPixelY()+this.getPixelHeight())+" / bs: "+this.getBorderSize());
		//Logger.debug("i1: "+Utility.between(x, this.getPixelX(), this.getPixelX()+this.getBorderSize())+" / i2: "+Utility.between(x, this.getPixelX()+this.getWidth()-2*this.getBorderSize(), this.getPixelX()+this.getWidth()));
		//Logger.debug("x: "+x+" / v1: "+(this.getPixelX()+this.getWidth()-2*this.getBorderSize())+" / v2: "+(this.getPixelX()+this.getWidth()));
		if(sizegrab && brgrab || (Utility.between(x,  this.getPixelX()+this.getPixelWidth(), this.getPixelX()+this.getPixelWidth()-this.getBorderSize()) 
				      && Utility.between(y, this.getPixelY()+this.getPixelHeight()-this.getBorderSize(), this.getPixelY()+this.getPixelHeight())))
		{
			if(this.getPixelWidth()-mx > this.getBorderSize()*2 && this.getPixelHeight() -my > this.headHeight + 2 * this.getBorderSize())
			{
				this.brgrab = true;
				this.setDimension(this.getPixelWidth()-mx, this.getPixelHeight()-my);
				this.getBorder().setDimension(this.getPixelWidth(), this.getPixelHeight());
			}
		}
		else if(sizegrab && blgrab || (Utility.between(x,  this.getPixelX(), this.getPixelX()-this.getBorderSize()) 
				      && Utility.between(y, this.getPixelY()+this.getPixelHeight()-this.getBorderSize(), this.getPixelY()+this.getPixelHeight())))
		{
			if(this.getPixelWidth()+mx > this.getBorderSize()*2 && this.getPixelHeight() -my > this.headHeight + 2 * this.getBorderSize())
			{
				this.blgrab = true;

				this.setPosition(this.getPixelX()-mx, this.getPixelY());
				this.setDimension(this.getPixelWidth()+mx, this.getPixelHeight()-my);
				this.getBorder().setDimension(this.getPixelWidth(), this.getPixelHeight()-my);
			}
		}
		else if(sizegrab && lgrab || (Utility.between(x, this.getPixelX(), this.getPixelX()+this.getBorderSize()) && Utility.between(y, this.getPixelY(), this.getPixelY()+this.getPixelHeight())))
		{
			if(this.getPixelWidth()+mx > this.getBorderSize()*2)
			{
				this.lgrab = true;
				this.setPosition(this.getPixelX()-mx, this.getPixelY());
				this.setDimension(this.getPixelWidth()+mx, this.getPixelHeight());
				this.getBorder().setDimension(this.getPixelWidth(), this.getPixelHeight());
			}
			//this.setPosition(this.getPixelX()-mx, this.getPixelY());
		}
		else if(sizegrab && this.rgrab || (Utility.between(x, this.getPixelX()+this.getPixelWidth(), this.getPixelX()+this.getPixelWidth()-this.getBorderSize()) 
											&& Utility.between(y, this.getPixelY()+this.headHeight, this.getPixelY()+this.getPixelHeight())))
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
		else if(sizegrab && this.bgrab || (Utility.between(x, this.getPixelX()+this.getBorderSize(), this.getPixelX()+this.getPixelWidth()-this.getBorderSize()) 
							   && Utility.between(y, this.getPixelY()+this.getPixelHeight()-this.getBorderSize(), this.getPixelY()+this.getPixelHeight())))
		{
			//Logger.debug("Section 2");
			
			if(this.getPixelHeight() -my > this.headHeight + 2 * this.getBorderSize())
			{
				this.bgrab = true;
				this.setDimension(this.getPixelWidth(), this.getPixelHeight()-my);
				this.getBorder().setDimension(this.getPixelWidth(), this.getPixelHeight());
			}
			//this.setPosition(this.getPixelX()-mx, this.getPixelY());
		}
		else
		{
			super.dragged(mx, my);
			this.sizegrab = false;
			this.movegrab = true;
		}
	}

	
	@Override
	public boolean checkId(int id)
	{
		boolean r = this.contentPane.checkId(id);	
		
		if(super.checkId(id) &&!r)
		{
			r = true;
		}
		
		return r;
	}
	
	@Override
	protected void onClick(int x, int y, MouseButtons button, boolean grabbed, int grabx, int graby)
	{
		if(this.isX(x, y) && button == MouseButtons.LEFT)
			this.xClick();
		
		super.onClick(x, y, button, grabbed, grabx, graby);
	}
	
	protected void xClick() {
		this.hide();		
	}


	@Override
	protected void setTextClipping(boolean b)
	{
		this.getFO().setClippingArea(new Vertex4(this.getX(), this.getY(), this.getX()+this.getWidth(), this.getY()+this.getHeight()));

		this.setAddPadY(-5);
	}
	
	private boolean isX(int x, int y)
	{
		return Utility.between(x, this.crossBorder.getPixelX(), this.crossBorder.getPixelX()+this.crossBorder.getPixelWidth()) &&
				Utility.between(y, this.crossBorder.getPixelY(), this.crossBorder.getPixelY()+this.crossBorder.getPixelHeight());
	}
	
	@Override
	public void drawChildren(boolean selection)
	{
		super.drawChildren(selection);
		
		if(this.getParent() != null && isX(this.getParent().getMouseX(), this.getParent().getMouseY()))
			this.crossBorder.setColor(Colors.RED.getGlColor());
		else
			this.crossBorder.setColor(Colors.WHITE.getGlColor());
		
		if(this.sizegrab)
			this.sizegrab = this.getParent().isMouseGrabbed();
		
		if(this.movegrab)
			this.movegrab = this.getParent().isMouseGrabbed();
		
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
		
		if(selection)
			this.getBorder().drawSelect();
		
		if(selection)
			this.contentPane.drawSelect();
		else
			this.contentPane.draw();
		
		if(!selection)
			this.cross.draw();
		
		if(!selection)
			this.crossBorder.draw();
	}
	
	@Override
	public void setParent(IParent p)
	{
		super.setParent(p);
		this.contentPane.setParent(p);
	}
	
	@Override
	public void setDimension(float w, float h)
	{
		super.setDimension(w, h);
		this.contentPane.setDimension(this.getPixelWidth()-2*+this.getBorderSize(), this.getPixelHeight()-2*this.getBorderSize()-this.headHeight);
		//this.crossBorder.setDimension(this.headHeight+this.getBorderSize(), this.headHeight+this.getBorderSize());
		
		this.cross.setPosition(this.getPixelX()+this.getPixelWidth()-headHeight, this.getPixelY()+this.getBorderSize());
		this.crossBorder.setPosition(this.getPixelX()+this.getPixelWidth()-this.getBorderSize()-headHeight, this.getPixelY());
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
		this.cross.setPosition(this.getPixelX()+this.getPixelWidth()-headHeight, this.getPixelY()+this.getBorderSize());
		this.cross.setDimension(this.headHeight-this.getBorderSize(), this.headHeight-this.getBorderSize());
		this.crossBorder.setPosition(this.getPixelX()+this.getPixelWidth()-this.getBorderSize()-headHeight, this.getPixelY());
		this.crossBorder.setDimension(this.headHeight+this.getBorderSize(), this.headHeight+this.getBorderSize());
	}
	
	@Override
	protected void move(float x, float y)
	{
		super.move(x, y);
		
		Logger.debug("moving panel to new position...");
		Logger.debug("hh: "+this.headHeight+" / fh: "+this.getFO().getPixelHeight());
		this.contentPane.setPosition(this.getPixelX()+this.getBorderSize(), this.getPixelY()+this.headHeight+this.getBorderSize());
		this.cross.setPosition(this.getPixelX()+this.getPixelWidth()-headHeight, this.getPixelY()+this.getBorderSize());
		this.crossBorder.setPosition(this.getPixelX()+this.getPixelWidth()-this.getBorderSize()-headHeight, this.getPixelY());
	}


	/**
	 * @return the headHeight
	 */
	public int getHeadHeight() {
		return headHeight;
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
	
	@Override
	public Component getById(int id) {
		if(this.getId() == id)
			return this;
		else if(this.contentPane.get(id) != null)
			return this.contentPane.get(id);
		else
			return null;
	}	
	
	public Component get(int id) {
		return this.contentPane.get(id);
	}

	public Component getComponent(int id) {
		return this.contentPane.get(id);
	}
	
	public List<Component> getAll() {
		return this.contentPane.getAllComponents();
	}

	public List<Component> getAllComponents() {
		return this.contentPane.getAllComponents();
	}

	public <T extends Component> List<T> getByClass(Class<T> c) {
		return this.contentPane.getComponents(c);
	}

	public <T extends Component> List<T> getComponentsByClass(Class<T> c) {
		return this.contentPane.getComponents(c);
	}

	public <T extends Component> List<T> getComponents(Class<T> c) {
		return this.contentPane.getComponents(c);
	}
		
	public void add(Component c)
	{
		this.contentPane.addComponent(c);
	}
	
	public void addComponent(Component c)
	{
		this.contentPane.add(c);
	}

	
	public void removeComponent(Component c)
	{
		this.contentPane.remove(c);
	}
	

	public void removeComponent(int id)
	{
		this.contentPane.remove(id);
	}
	
}

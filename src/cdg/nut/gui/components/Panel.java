package cdg.nut.gui.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.input.Mouse;

import cdg.nut.gui.Component;
import cdg.nut.gui.Container;
import cdg.nut.gui.ToolTip;
import cdg.nut.interfaces.IDimensionChangedListener;
import cdg.nut.interfaces.IGuiObject;
import cdg.nut.interfaces.IMovedListener;
import cdg.nut.interfaces.IParent;
import cdg.nut.logging.Logger;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex4;
import cdg.nut.util.settings.SetKeys;

public class Panel extends Component implements IParent, IDimensionChangedListener, IMovedListener {

	private Container con = new Container();
	private int bufferId = 0;
	
//	private List<Integer[]> positions = new ArrayList<Integer[]>();
	
	private int childX;
	private int childY;
	
	private boolean autosizeWithContentX = false;
	private boolean autosizeWithContentY = false;
	private boolean scrolling;
	
	public Panel(float x, float y, float width, float height) {
		super(x, y, width, height);

		this.childX = this.getTextX();
		this.childY = this.getTextY();
		this.setup();
	}
	
	public Panel(int x, int y, int width, int height) {
		super(x, y, width, height);

		this.childX = this.getTextX();
		this.childY = this.getTextY();
		this.setup();
	}
	
	protected Panel(float x, float y, float width, float height, String text) {
		super(x, y, width, height, text);

		this.childX = this.getTextX();
		this.childY = this.getTextY();
		this.setup();
	}
	
	protected Panel(int x, int y, int width, int height, String text) {
		super(x, y, width, height, text);

		this.childX = this.getTextX();
		this.childY = this.getTextY();
		this.setup();
	}
	
	protected Panel(float x, float y, String text) {
		super(x, y, text);

		this.childX = this.getTextX();
		this.childY = this.getTextY();
		this.setup();
	}
	
	protected Panel(int x, int y, String text) {
		super(x, y, text);

		this.childX = this.getTextX();
		this.childY = this.getTextY();
		this.setup();
	}
	
	protected void setup()
	{
		//this.setSelectable(false);
		this.setHasBackground(false);
		this.setHasBorder(false);
		this.setTextSelectable(true);
		this.setScrollable(false);
		
		this.setBackgroundHighlightColor(this.getBackgroundColor());
	}
	
	@Override
	public void setSelected(boolean b){
		if(this.isSelectable()) super.setSelected(b);
	}
	
//	private Integer[] getPosById(int id)
//	{
//		for(int i = 0; i < this.positions.size(); i++)
//		{
//			if(this.positions.get(i)[0] == id)
//				return this.positions.get(i);
//		}
//		return new Integer[]{0,0,0};
//	}
	
	@Override
	protected void move(float x, float y)
	{
		int oldx = this.getPixelX();
		int oldy = this.getPixelY();
		
		float[] bs = Utility.pixelSizeToGLSize(this.getBorderSize(), this.getBorderSize());
		
		super.move(x, y);
		
		List<Component> cl = this.con.getComponents();
		for(int i = 0; i < this.con.getComponentCount(); i++)
		{
			Component c = cl.get(i);
			//Integer[] posdata = getPosById(c.getId());
			c.setPosition(c.getPixelX()-oldx+this.getPixelX(), c.getPixelY()-oldy+this.getPixelY());
			//c.setClippingArea(new Vertex4(this.getX()+bs[0], this.getY()+bs[1], this.getX()+this.getWidth()-bs[0], this.getY()+this.getHeight()-bs[1]));
			//c.setClippingArea(new Vertex4(this.getX(), this.getY(), this.getX()+this.getWidth(), this.getY()+this.getHeight()));
			
			if(this.isAutoClipping())
				c.setClippingArea(new Vertex4(this.getX(), this.getY(), this.getX()+this.getWidth()-Utility.pixelSizeToGLSize(this.getBorderSize(),0)[0], this.getY()+this.getHeight()));
			else
			{
				Vertex4 ca = this.getClippingArea();
				Vertex4 cca = this.getContentClippingArea();
				
				Vertex4 fin = new Vertex4(0,0,0,0);
				
				fin.setX(Math.max(ca.getX(), cca.getX()));
				fin.setY(Math.min(ca.getY(), cca.getY()));
				fin.setZ(Math.min(ca.getZ(), cca.getZ()));
				fin.setW(Math.max(ca.getW(), cca.getW()));
				
				c.setClippingArea(fin);
			}
			
			Logger.debug("moving panel and component...");
		}
	}
	
	@Override
	public boolean checkId(int id)
	{
		boolean r = false;
		
		List<Component> cl = this.con.getComponents();
		for(int i = 0; i < this.con.getComponentCount(); i++)
		{
			Component c = cl.get(i);
			
			r = c.checkId(id);
		}
		
		
		
		if(super.checkId(id) &&!r)
		{
			r = true;
		}
		
		return r;
	}
	
	@Override
	protected void drawChildren(boolean selection)
	{		
		List<Component> cl = this.con.getComponents();
		for(int i = 0; i < this.con.getComponentCount(); i++)
		{
			Component c = cl.get(i);

			if(selection)
			{
				c.drawSelect();
			}
			else
			{
				c.draw();
			}
		}
		
		super.drawChildren(selection);
	}

	@Override
	public void remove(Component c) {
		this.con.remove(c);
	}

	@Override
	public void remove(int id) {
		this.con.remove(id);		
	}

	@Override
	public void addToNextId(int i) {
		if(this.getParent() != null)
			this.getParent().addToNextId(i);
		this.bufferId += i;
	}

	@Override
	public int getNextId() {
		if(this.getParent() != null)
			return this.getParent().getNextId();
		else
			return this.bufferId;
	}

	@Override
	public Component get(int id) {
		return this.con.get(id);
	}

	@Override
	public Component getComponent(int id) {
		return this.con.get(id);
	}

	@Override
	public List<Component> getAll() {
		return this.con.getComponents();
	}

	@Override
	public List<Component> getAllComponents() {
		return this.con.getComponents();
	}

	@Override
	public <T extends Component> List<T> getByClass(Class<T> c) {
		return this.con.getComponents(c);
	}

	@Override
	public <T extends Component> List<T> getComponentsByClass(Class<T> c) {
		return this.con.getComponents(c);
	}

	@Override
	public <T extends Component> List<T> getComponents(Class<T> c) {
		return this.con.getComponents(c);
	}
	
	@Override
	public void add(Component c)
	{
		this.addComponent(c);
	}
	
	@Override
	public void addComponent(Component c)
	{
		this.addToNextId(c.setId(this.getNextId()));
		c.setParent(this);
//		this.positions.add(new Integer[]{c.getId(), c.getPixelX(), c.getPixelY()});
		c.setParentXDif(c.getPixelX()+(this.hasBorder()?this.getBorderSize():0));
		c.setParentYDif(c.getPixelY()+(this.hasBorder()?this.getBorderSize():0));
		c.setPosition(this.getPixelX()+(this.hasBorder()?this.getBorderSize():0)+c.getPixelX(), this.getPixelY()+(this.hasBorder()?this.getBorderSize():0)+c.getPixelY());
		c.setClipping(true);
		c.setAutoClipping(false);
		c.addMovedListener(this);
	
		if(this.isAutoClipping())
			c.setClippingArea(this.getContentClippingArea());
		else
		{
			Vertex4 ca = this.getClippingArea();
			Vertex4 cca = this.getContentClippingArea();
			
			Vertex4 fin = new Vertex4(0,0,0,0);
			
			fin.setX(Math.max(ca.getX(), cca.getX()));
			fin.setY(Math.min(ca.getY(), cca.getY()));
			fin.setZ(Math.min(ca.getZ(), cca.getZ()));
			fin.setW(Math.max(ca.getW(), cca.getW()));
			
			c.setClippingArea(fin);
		}
		
		c.addDimensionChangedListener(this);
		this.con.add(c);
		if(this.autosizeWithContentX || this.autosizeWithContentY) this.autosize();
		else if(this.isScrollable()) this.setScroll();
		
		
	}

	@Override
	public void removeComponent(Component c)
	{
		this.con.remove(c);
		if(this.autosizeWithContentX || this.autosizeWithContentY) this.autosize();
		else if(this.isScrollable()) this.setScroll();
	}
	
	@Override
	public void removeComponent(int id)
	{
		this.con.remove(id);
		if(this.autosizeWithContentX || this.autosizeWithContentY) this.autosize();
		else if(this.isScrollable()) this.setScroll();
	}
	
	@Override
	public ToolTip getActiveToolTip() {
		if(this.getParent() != null)
			return this.getParent().getActiveToolTip();
		else
			return null;
	}
	
	@Override
	public void setActiveToolTip(ToolTip activeToolTip) {
		if(this.getParent() != null)
			this.getParent().setActiveToolTip(activeToolTip);
	}
	
	@Override
	public void setParent(IParent p)
	{
		if(this.getParent() == null)
		{
			List<Component> cl = this.con.getComponents();
			for(int i = 0; i < this.con.getComponentCount(); i++)
			{
				cl.get(i).setId(cl.get(i).getId()+p.getNextId());
			}
			
			p.addToNextId(this.bufferId);
		}
		else
		{
			List<Component> cl = this.con.getComponents();
			for(int i = 0; i < this.con.getComponentCount(); i++)
			{
				cl.get(i).setId(i+1+p.getNextId());
			}
			
			p.addToNextId(this.bufferId);
		}
		
		super.setParent(p);
	}

	@Override
	public Component getById(int id) {
		if(this.getId() == id)
			return this;
		else if(this.con.get(id) != null)
			return this.con.get(id);
		else
			return null;
	}

	public int getChildX() {
		return childX;
	}

	public void setChildX(int childX) {
		this.childX = childX;
	}

	public int getChildY() {
		return childY;
	}

	public void setChildY(int childY) {
		this.childY = childY;
	}
	
	@Override
	public int getMouseGrabSX() {
		return this.getParent().getMouseGrabSX();
	}

	@Override
	public int getMouseGrabSY() {
		return this.getParent().getMouseGrabSY();
	}

	@Override
	public boolean isMouseGrabbed() {
		return this.getParent().isMouseGrabbed();
	}
	
	@Override
	public int getMouseX() {
		return this.getParent().getMouseX();
	}

	@Override
	public int getMouseY() {
		return this.getParent().getMouseY();
	}

	@Override
	public void setDimension(float w, float h)
	{
		super.setDimension(w, h);
		for(Component c: this.con.getComponents())
		{
			if(this.isAutoClipping())
				c.setClippingArea(this.getContentClippingArea());
			else
			{
				Vertex4 ca = this.getClippingArea();
				Vertex4 cca = this.getContentClippingArea();
				
				Vertex4 fin = new Vertex4(0,0,0,0);
				
				fin.setX(Math.max(ca.getX(), cca.getX()));
				fin.setY(Math.min(ca.getY(), cca.getY()));
				fin.setZ(Math.min(ca.getZ(), cca.getZ()));
				fin.setW(Math.max(ca.getW(), cca.getW()));
				
				c.setClippingArea(fin);
				
				
			}
		}
	}

	public Collection<? extends IGuiObject> getSelPos() {
		List<IGuiObject> e = this.con.getComponentsAG();
		List<IGuiObject> pssbl = new ArrayList<IGuiObject>(10);
		
		for(int i = 0; i < e.size(); i++)
		{
					
			if(Utility.between(Mouse.getX(), ((IGuiObject)e.get(i)).getPixelX(), ((IGuiObject)e.get(i)).getPixelX()+((IGuiObject)e.get(i)).getPixelWidth()) &&
					Utility.between(SetKeys.WIN_HEIGHT.getValue(Integer.class) - Mouse.getY(), ((IGuiObject)e.get(i)).getPixelY(), ((IGuiObject)e.get(i)).getPixelY()+((IGuiObject)e.get(i)).getPixelHeight()))
			{
				if(Panel.class.isAssignableFrom(e.get(i).getClass()))
					pssbl.addAll(((Panel)e.get(i)).getSelPos());
				else if(InnerWindow.class.isAssignableFrom(e.get(i).getClass()))
					pssbl.addAll(((InnerWindow)e.get(i)).getSelPos());
				else
					pssbl.add(e.get(i));
			}
			else
			{
				e.get(i).unselected();
			}
		}
		
		if(Utility.between(Mouse.getX(), this.getPixelX(), this.getPixelX()+this.getPixelWidth()) &&
				Utility.between(SetKeys.WIN_HEIGHT.getValue(Integer.class) - Mouse.getY(), this.getPixelY(), this.getPixelY()+this.getPixelHeight()))
		{
			pssbl.add(this);
		}
		
		
		return pssbl;
	}
	
	@Override
	public void onScroll(int sv, boolean horizontal)
	{
		
		this.scrolling = true;
		
		if(!this.getXScroll() && !this.getYScroll())
			return;
		
		for(Component c: this.con.getComponents())
		{
			if(horizontal)
			{
				c.setX(this.getPixelX()+c.getParentXDif()-sv);
			}
			else
			{
				c.setY(this.getPixelY()+c.getParentYDif()-sv);
			}
		}
		
		this.scrolling = false;
	}

	public List<IGuiObject> getComponentsAG() {
		return this.con.getComponentsAG();
	}
	
	@Override
	protected void setScroll()
	{		
		if(!this.isScrollable())
			return;
		
		int mx = 0;
		int my = 0;
		
		for(Component c: this.con.getComponents())
		{
			if(c.getPixelX()+c.getPixelWidth() > mx)
				mx = c.getPixelX()+c.getPixelWidth();
			
			if(c.getPixelY()+c.getPixelHeight() > my)
				my = c.getPixelY()+c.getPixelHeight();
		}
		
		
		int border = 1*(this.hasBorder()?this.getBorderSize():0);
		int xsb = this.getXScroll()?this.getXsb().getPixelHeight():0;
		int ysb = this.getYScroll()?this.getYsb().getPixelWidth():0;
		
		Logger.debug("mx: "+mx+" / xpw-b-x: "+(this.getPixelX()+this.getPixelWidth()-border-ysb)+" / dif: "+(mx-(this.getPixelX()+this.getPixelWidth()-border-ysb)));
		Logger.debug("my: "+my+" / ypw-b-y: "+(this.getPixelY()+this.getPixelHeight()-border-xsb)+" / dif: "+(my-(this.getPixelY()+this.getPixelHeight()-border-xsb)));
		
		if(mx > this.getPixelX()+this.getPixelWidth()-border-ysb)
		{
			if(this.getXsb() != null) 
			{
				this.getXsb().setMaxValue(mx-(this.getPixelX()+this.getPixelWidth()-border-ysb));
				this.setXScroll(true);
			}				
		}
		else
		{
			this.setXScroll(false);
			if(this.getXsb() != null) { this.getXsb().setScrollValue(0); this.getXsb().setMaxValue(0);}
		}
		
		if(my > this.getPixelY()+this.getPixelHeight()-border-xsb)
		{
			if(this.getYsb() != null) 
			{
				this.getYsb().setMaxValue(my-(this.getPixelY()+this.getPixelHeight()-border-xsb));
				this.setYScroll(true);
			}				
		}
		else
		{
			this.setYScroll(false);
			if(this.getYsb() != null) { this.getYsb().setScrollValue(0); this.getYsb().setMaxValue(0);}
		}
		
		Logger.debug("xscroll: "+this.getXScroll()+" / yscroll: "+this.getYScroll());
		
		if(this.getXsb() != null) this.getXsb().setDoublescroll(this.getXScroll() && this.getYScroll());
		if(this.getYsb() != null) this.getYsb().setDoublescroll(this.getXScroll() && this.getYScroll());
		
		for(Component c: this.con.getComponents())
		{
			if(this.isAutoClipping())
				c.setClippingArea(this.getContentClippingArea());
			else
				c.setClippingArea(this.getClippingArea());
		}
	}

	@Override
	public void dimensionChanged(int id, int width, int height) {
		
		if(this.autosizeWithContentX || this.autosizeWithContentY) this.autosize();
		else if(this.isScrollable()) this.setScroll();
			
	}
	
	private void autosize()
	{
		
		if(!this.autosizeWithContentX && !this.autosizeWithContentY)
			return;
		
		int mx = 0;
		int my = 0;
		
		for(Component c: this.con.getComponents())
		{
			if(c.getPixelX()+c.getPixelWidth() > mx)
				mx = c.getPixelX()+c.getPixelWidth();
			
			if(c.getPixelY()+c.getPixelHeight() > my)
				my = c.getPixelY()+c.getPixelHeight();
		}
		
		if(this.autosizeWithContentX && !this.autosizeWithContentY) this.setDimension(mx, this.getPixelHeight());
		else if(!this.autosizeWithContentX && this.autosizeWithContentY) this.setDimension(this.getPixelWidth(), my);
		else if(this.autosizeWithContentX && this.autosizeWithContentY) this.setDimension(mx, my);
		
		Logger.debug("X: "+this.autosizeWithContentX+"("+mx+") / Y: "+this.autosizeWithContentY+"("+my+")");
	}

	public boolean isAutosizeWithContentX() {
		return autosizeWithContentX;
	}

	public void setAutosizeWithContentX(boolean autosizeWithContentX) {
		this.autosizeWithContentX = autosizeWithContentX;
		
		if(this.autosizeWithContentX) this.autosize();
	}
	
	public boolean isAutosizeWithContentY() {
		return autosizeWithContentY;
	}

	public void setAutosizeWithContentY(boolean autosizeWithContentY) {
		this.autosizeWithContentY = autosizeWithContentY;
		
		if(this.autosizeWithContentY) this.autosize();
	}
	
	@Override
	public void setAutoClipping(boolean b)
	{
		super.setAutoClipping(b);
		
		for(Component c: this.con.getComponents())
		{
			if(this.isAutoClipping())
				c.setClippingArea(this.getContentClippingArea());
			else
				c.setClippingArea(this.getClippingArea());
		}
		
		
	}

	@Override
	public void moved(int id, int x, int y) {
		
		if(!this.scrolling)
		{
			this.get(id).setParentXDif(this.get(id).getPixelX()-this.getTextX());
		}
	}
	
	


}

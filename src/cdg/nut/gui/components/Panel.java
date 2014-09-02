package cdg.nut.gui.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.input.Mouse;

import cdg.nut.gui.Component;
import cdg.nut.gui.Container;
import cdg.nut.gui.ToolTip;
import cdg.nut.interfaces.IGuiObject;
import cdg.nut.interfaces.IParent;
import cdg.nut.logging.Logger;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex4;
import cdg.nut.util.settings.SetKeys;

public class Panel extends Component implements IParent {

	private Container con = new Container();
	private int bufferId = 0;
	
	private List<Integer[]> positions = new ArrayList<Integer[]>();
	
	private int childX;
	private int childY;
	
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
	}
	
	@Override
	public void setSelected(boolean b){}
	
	private Integer[] getPosById(int id)
	{
		for(int i = 0; i < this.positions.size(); i++)
		{
			if(this.positions.get(i)[0] == id)
				return this.positions.get(i);
		}
		return new Integer[]{0,0,0};
	}
	
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
			c.setClippingArea(new Vertex4(this.getX(), this.getY(), this.getX()+this.getWidth(), this.getY()+this.getHeight()));
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
		super.drawChildren(selection);
		
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
		this.positions.add(new Integer[]{c.getId(), c.getPixelX(), c.getPixelY()});
		c.setPosition(this.getPixelX()+c.getPixelX(), this.getPixelY()+c.getPixelY());
		c.setClipping(true);
		c.setAutoClipping(false);
		c.setClippingArea(new Vertex4(this.getX(), this.getY(), this.getX()+this.getWidth(), this.getY()+this.getHeight()));
		this.con.add(c);
	}

	@Override
	public void removeComponent(Component c)
	{
		this.con.remove(c);
	}
	
	@Override
	public void removeComponent(int id)
	{
		this.con.remove(id);
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
		// TODO Auto-generated method stub
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
		List<Component> cl = this.con.getComponents();
		for(int i = 0; i < this.con.getComponentCount(); i++)
		{
			Component c = cl.get(i);
			c.setClippingArea(new Vertex4(this.getX(), this.getY(), this.getX()+this.getWidth(), this.getY()+this.getHeight()));
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

	public List<IGuiObject> getComponentsAG() {
		return this.con.getComponentsAG();
	}
}

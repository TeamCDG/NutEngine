package cdg.nut.gui;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cdg.nut.gui.components.InnerWindow;
import cdg.nut.gui.components.Panel;
import cdg.nut.interfaces.IEntity;
import cdg.nut.interfaces.IGuiObject;
import cdg.nut.interfaces.IParent;
import cdg.nut.logging.Logger;
import cdg.nut.util.Engine;
import cdg.nut.util.Utility;
import cdg.nut.util.enums.MouseButtons;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLPolygon;
import cdg.nut.util.gl.GLTexture;
import cdg.nut.util.settings.Cmd;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public abstract class Frame implements IParent {
	protected String title;
	protected Container con;
	protected GLPolygon background;
	protected ToolTip activeToolTip;
	protected boolean manualToolTipHide;
	
	protected boolean forceSelect = false;

	protected int frNoMove = 0;
	protected int oldMouseX;
	protected int oldMouseY;
	protected int mouseGrabX;
	protected int mouseGrabY;
	protected int mouseGrabSX;
	protected int mouseGrabSY;
	protected boolean grabStart = true;
	
	@Override
	public int getMouseGrabSX() {
		return mouseGrabSX;
	}

	@Override
	public int getMouseGrabSY() {
		return mouseGrabSY;
	}

	protected int currentCursor = 0;
	protected Cursor activeCursor;
	protected Cursor normalCursor;
	protected boolean mouseLeftPressed;
	protected boolean mouseGrabbed;
	protected boolean deltaMouseGrabbed;
	protected boolean mouseRightPressed;

	protected IGuiObject active;

	protected int nextId = 1;
	protected int lastId;

	protected int maxSelectSkip = Settings.get(SetKeys.GUI_MAX_SELECT_SKIP, Integer.class);
	protected int selectSkip = Settings.get(SetKeys.GUI_MAX_SELECT_SKIP, Integer.class);
	protected int grabId;
	protected boolean grabable;;

	public Frame()
	{
		this.con = new Container();
	}

	public Frame(GLTexture background)
	{
		this.background = new GLPolygon(-1.0f, 1.0f, 2.0f, -2.0f);
		this.background.setImage(background);
		this.background.setColor(new GLColor(1.0f, 1.0f, 1.0f, 1.0f));
		this.con = new Container();
	}

	public void draw() //Never ever ask me to explain this method. it works, that's all, hua?
	{
		if(this.con.getComponents(Console.class).size() == 0)
			this.add(Engine.console);
		
		//Logger.debug("console id: "+Engine.console.getId());
		
		this.selectSkip++;
		Mouse.poll();
		this.deltaMouseGrabbed = this.mouseGrabbed;

		if(this.oldMouseX == Mouse.getX() && this.oldMouseY == (SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY()))
			this.frNoMove++;
		else
			this.frNoMove = 0;
		
		if(this.frNoMove >= Settings.get(SetKeys.R_MAX_FPS, Integer.class)/2 && this.lastId != 0 && this.con.get(this.lastId) != null) 
		{
			this.con.get(this.lastId).showToolTip(Mouse.getX(), (SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY()));
		}
		else if(this.lastId != 0 && this.con.get(this.lastId) != null)
		{
			this.con.get(this.lastId).hideToolTip();
		}
		else if(this.lastId == 0 && this.active == null)
		{
			this.activeToolTip = null;
		}
		
		if (((this.oldMouseX != Mouse.getX() || this.oldMouseY != SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY()) &&
			this.selectSkip > this.maxSelectSkip && Mouse.isInsideWindow())
			|| this.forceSelect) {
			
			this.selectSkip = 0;	
			this.forceSelect = false;
			this.select();
			SetKeys.R_CLEAR_BOTH.execute(null);
		}
		
		int mdwheel = Mouse.getDWheel();
		if(mdwheel != 0) Logger.debug("mdwheel: "+mdwheel,"Frame.draw");
		if(this.lastId != 0 && this.con.get(this.lastId) != null && this.con.get(this.lastId).isScrollable() && mdwheel != 0) this.con.get(this.lastId).mwheel(mdwheel);
		
		if (Mouse.isButtonDown(MouseButtons.LEFT.getKey())) {
			
			if(! this.mouseGrabbed)
				this.grabId = this.lastId;
			
			Logger.debug("grabid: "+this.grabId+" / mouseGrabbed: "+this.mouseGrabbed,"Frame.draw");
			
			this.mouseGrabX = this.oldMouseX-Mouse.getX();
			this.mouseGrabY = this.oldMouseY-(SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY());
			if(this.grabable && this.grabId!=0)
			{
				this.mouseGrabbed = this.mouseLeftPressed && this.con.get(this.grabId) !=null &&(this.con.get(this.grabId).isDragable() || this.con.get(this.grabId).isScrollable() || (this.con.get(this.grabId).isTextSelectable()));
				
				if(this.mouseGrabbed && !this.grabStart)
				{
					this.mouseGrabSX = Mouse.getX();
					this.mouseGrabSY = (SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY());
				}
				this.grabStart = this.mouseGrabbed;
			}
			else
				this.grabable = false;
			
			
			//TODO: Cursor class, with easy bind()
			if(this.currentCursor == 0 && this.activeCursor != null) { try {
				Mouse.setNativeCursor(this.activeCursor);
				this.currentCursor = 1; 
			} catch (LWJGLException e) {
			}}			
			
			this.mouseLeftPressed = true;
			
			//if(this.grabId != 0) Logger.debug("scrollable: "+this.con.get(this.grabId).isScrollable()+" / mgrabbed: "+this.mouseGrabbed,"Frame.draw");
			
			if(this.grabId != 0 && this.mouseGrabbed && this.con.get(this.grabId).isDragable()) this.con.get(this.grabId).dragged(this.mouseGrabX, this.mouseGrabY);
			if(this.grabId != 0 && this.mouseGrabbed && (this.con.get(this.grabId).isTextSelectable() || this.con.get(this.grabId).isScrollable())) this.con.get(this.grabId).clicked(Mouse.getX(), (SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY()), MouseButtons.LEFT, this.mouseGrabbed && (Math.abs(this.mouseGrabSX - Mouse.getX()) >= 5 || Math.abs(this.mouseGrabSY - (SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY())) >= 5), this.deltaMouseGrabbed, this.mouseGrabSX, this.mouseGrabSY);
			
			
		} else if (!Mouse.isButtonDown(MouseButtons.LEFT.getKey())) {
			
			if(this.mouseLeftPressed)
			{
				
				
				if (this.active != null && this.active.getId() != lastId) {
					this.active.setActive(false);
				}
				
				Component c = this.con.get(this.lastId);

				//Logger.info("now active: "+this.lastId+" / actual id: "+c.getId());
				
				if (lastId != 0 && c != null) {
					c.clicked(Mouse.getX(), (SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY()), MouseButtons.LEFT, this.mouseGrabbed && (Math.abs(this.mouseGrabSX - Mouse.getX()) >= 5 || Math.abs(this.mouseGrabSY - (SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY())) >= 5), this.deltaMouseGrabbed, this.mouseGrabSX, this.mouseGrabSY);
					//c.clicked(Mouse.getX(), (SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY()), MouseButtons.LEFT, this.mouseGrabbed);
					c.setActive(true);
				}
				
				this.active = c;
			}
			
			this.grabable = true;
			this.mouseLeftPressed = false;
			this.mouseGrabbed = false;
			this.grabStart = false;
			
			if(this.currentCursor == 1 && this.normalCursor != null) { try {
				Mouse.setNativeCursor(this.normalCursor);
				this.currentCursor = 0; 
			} catch (LWJGLException e) {
			} Logger.debug("changing cursor to active");}
		}

		if (Mouse.isButtonDown(MouseButtons.RIGHT.getKey())) {
			this.mouseRightPressed = true;
		} else if (!Mouse.isButtonDown(MouseButtons.RIGHT.getKey())) {
			this.mouseRightPressed = false;
		}

		Keyboard.enableRepeatEvents(true);
		Keyboard.poll();

		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState() && this.active!=null) {
				this.active.key(Keyboard.getEventKey(), Keyboard.getEventCharacter());
			}
		}

		if (
			(
				this.oldMouseX != Mouse.getX() ||
				this.oldMouseY != (SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY())
				
			) &&
			!this.deltaMouseGrabbed
		) {
			this.oldMouseX = Mouse.getX();
			this.oldMouseY = (SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY());
			this.mouseGrabX = Mouse.getX();
			this.mouseGrabY = (SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY());
		} else if (this.deltaMouseGrabbed) {
			this.oldMouseX = Mouse.getX();
			this.oldMouseY = (SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY());
		}

		if (this.background != null) this.background.draw();

		this.con.drawComponents();
		
		if(this.activeToolTip != null)
			this.activeToolTip.draw();
	}

	protected int select()
	{
		this.con.drawComponentSelection();

		ByteBuffer pixel = ByteBuffer.allocateDirect(16);
		GL11.glReadPixels(Mouse.getX(), Mouse.getY(), 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixel);

		int[] vs = new int[] {
			(int) (pixel.get(0) & 0xFF),
			(int) (pixel.get(1) & 0xFF),
			(int) (pixel.get(2) & 0xFF),
			(int) (pixel.get(3) & 0xFF)
		};

		int gotId = Utility.glColorToId(vs, false);



		if(!this.mouseGrabbed)
		{
			for (int i = 0; i < this.con.getComponentCount(); i++) {
				this.con.getComponents().get(i).checkId(gotId);
			}
		}

		this.lastId = gotId;
		return gotId;
	}
	
	protected int getSelectionId()
	{

		ByteBuffer pixel = ByteBuffer.allocateDirect(16);
		GL11.glReadPixels(Mouse.getX(), Mouse.getY(), 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixel);

		int[] vs = new int[] {
			(int) (pixel.get(0) & 0xFF),
			(int) (pixel.get(1) & 0xFF),
			(int) (pixel.get(2) & 0xFF),
			(int) (pixel.get(3) & 0xFF)
		};

		return Utility.glColorToId(vs, false);
	}

	@Override
	public void add(Component c)
	{
		this.addComponent(c);
	}
	
	@Override
	public void addComponent(Component c)
	{
		this.nextId += c.setId(nextId);
		
		c.setParent(this);

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

	public void setTitle()
	{
		// TODO Auto-generated method stub
	}

	public String getTitle()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void reset()
	{
		// TODO Auto-generated method stub
	}

	public Cursor getNormalCursor() {
		return normalCursor;
	}

	public void setNormalCursor(Cursor normalCursor) {
		this.normalCursor = normalCursor;
	}

	public Cursor getActiveCursor() {
		return activeCursor;
	}

	public void setActiveCursor(Cursor activeCursor) {
		this.activeCursor = activeCursor;
	}

	@Override
	public ToolTip getActiveToolTip() {
		return activeToolTip;
	}
	
	@Override
	public void setActiveToolTip(ToolTip activeToolTip) {
		this.activeToolTip = activeToolTip;
	}

	public boolean isManualToolTipHide() {
		return manualToolTipHide;
	}

	public void setManualToolTipHide(boolean manualToolTipHide) {
		this.manualToolTipHide = manualToolTipHide;
	}
	
	public void setBackground(String path)
	{		
		if(this.background == null)
		{
			this.background = new GLPolygon(-1.0f, 1.0f, 2.0f, -2.0f);
			this.background.setColor(new GLColor(1.0f, 1.0f, 1.0f, 1.0f));
		}
		this.background.setImage(new GLTexture(path));
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
		this.nextId += i;
	}

	@Override
	public int getNextId() {
		return this.nextId;
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
	public boolean isMouseGrabbed() {
		// TODO Auto-generated method stub
		return this.mouseGrabbed;
	}
	
	@Override
	public int getMouseX() {
		return Mouse.getX();
	}

	@Override
	public int getMouseY() {
		return SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY();
	}

	public List<? extends IGuiObject> getSelPos() {


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
		return pssbl;
	}
}

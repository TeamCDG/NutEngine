package cdg.nut.util.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cdg.nut.gui.Component;
import cdg.nut.gui.Console;
import cdg.nut.gui.Frame;
import cdg.nut.gui.components.Button;
import cdg.nut.gui.components.Label;
import cdg.nut.interfaces.IClickListener;
import cdg.nut.interfaces.IEntity;
import cdg.nut.interfaces.IGuiObject;
import cdg.nut.logging.Logger;
import cdg.nut.util.Engine;
import cdg.nut.util.Utility;
import cdg.nut.util.enums.Colors;
import cdg.nut.util.enums.MouseButtons;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class GameFrame extends Frame {

	private World world;
	
	float dd = Engine.getDelta();
	
	private Label camDebug;
	private Label deltaDebug;
	private Label tileCullingDebug;
	private Label mouseTileDebug;
	private Label fps;
	
	private Button ddReset;
	
	private IGuiObject clickedOne;
	private List<IEntity> selectedEntities = new LinkedList<IEntity>();
	
	private int tx;
	private int ty;
	
	public GameFrame()
	{
		
		
		
		super();
		
		GLColor debugBG = new GLColor(0.5f, 0.5f, 0.5f, 0.75f);
		
		int wrldWidth = 20;
		int wrldHeight = 20;
		
		this.nextId = 1 + (wrldWidth * wrldHeight);
		
		
		this.world = this.initWorld(wrldWidth, wrldHeight);
		this.camDebug = new Label(0, 0, this.world.getCamera().toString());
		this.camDebug.setFontSize(16);
		this.camDebug.setScrollable(false);
		this.camDebug.setBackgroundColor(debugBG);
		this.camDebug.setHasBackground(true);
		this.camDebug.setBorderPaddingDisabled(true);
		this.add(this.camDebug);
		
		this.deltaDebug = new Label(0, this.camDebug.getPixelHeight(), "Delta peak: "+Engine.getDelta());
		this.deltaDebug.setFontSize(16);
		this.deltaDebug.setScrollable(false);
		this.deltaDebug.setBackgroundColor(debugBG);
		this.deltaDebug.setHasBackground(true);
		this.deltaDebug.setBorderPaddingDisabled(true);
		this.add(this.deltaDebug);
		
		this.ddReset = new Button(0, this.deltaDebug.getPixelY() + this.deltaDebug.getPixelHeight(), "dpeak reset");
		this.ddReset.setFontSize(16);
		this.ddReset.addClickListener(new IClickListener(){

			@Override
			public void onClick(int x, int y, MouseButtons button, int grabx, int graby) {
				//GameFrame g = new TestGameFrame();
				dd = 0;
				//Main.activeFrame = g;
				
			}});
		this.add(this.ddReset);
		
		this.tileCullingDebug = new Label(0, this.ddReset.getPixelY() + this.ddReset.getPixelHeight(), "Tiles --> drawn: "+this.world.getDrawnTiles()+" / skipped: "+this.world.getSkippedTiles());
		this.tileCullingDebug.setFontSize(16);
		this.tileCullingDebug.setScrollable(false);
		this.tileCullingDebug.setBackgroundColor(debugBG);
		this.tileCullingDebug.setHasBackground(true);
		this.tileCullingDebug.setBorderPaddingDisabled(true);
		this.add(this.tileCullingDebug);
		
		this.mouseTileDebug = new Label(0, this.tileCullingDebug.getPixelY() + this.tileCullingDebug.getPixelHeight(), "yoooooooooooooooooooooooooooooolooooooooooooooooooooooooooooooooooo");
		this.mouseTileDebug.setFontSize(16);
		this.mouseTileDebug.setScrollable(false);
		this.mouseTileDebug.setBackgroundColor(debugBG);
		this.mouseTileDebug.setHasBackground(true);
		this.mouseTileDebug.setBorderPaddingDisabled(true);
		this.add(this.mouseTileDebug);
		
		this.fps = new Label(0, this.mouseTileDebug.getPixelY() + this.mouseTileDebug.getPixelHeight(), "FPS: "+Engine.getFPS());
		this.fps.setFontSize(16);
		this.fps.setScrollable(false);
		this.fps.setBackgroundColor(debugBG);
		this.fps.setHasBackground(true);
		this.fps.setBorderPaddingDisabled(true);
		this.add(this.fps);
		
		this.world.addPlayer("feget", GLColor.random(), true);
	}
	
	/**
	 * @return the tx
	 */
	public int getTx() {
		return tx;
	}

	/**
	 * @return the ty
	 */
	public int getTy() {
		return ty;
	}

	public World initWorld(int width, int height)
	{
		return new World(width, height);
	}
	
	/**
	 * @return the world
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * @param world the world to set
	 */
	public void setWorld(World world) {
		this.world = world;
	}

	@Override
	public void draw()
	{
		if(this.con.getComponents(Console.class).size() == 0)
		{
			 this.add(Engine.console);
			 this.world.setNextId(this.nextId +1);
			 this.nextId++;
		}
		
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
		
		this.mouseTileDebug();
		
		if (((this.oldMouseX != Mouse.getX() || this.oldMouseY != SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY()) &&
			this.selectSkip > this.maxSelectSkip && Mouse.isInsideWindow())
			|| this.forceSelect) {
			
			this.selectSkip = 0;	
			this.forceSelect = false;
			this.lastId = this.select();
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
				
				
				this.mouseLeftClicked();
				
				
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
		
		if(!Mouse.isButtonDown(MouseButtons.RIGHT.getKey()) && this.mouseRightPressed) //ergo click
		{
			float[] rpos = Utility.pixelToGL(Mouse.getX(), SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY());
			float wx =  rpos[0] * SetKeys.WIN_ASPECT_RATIO.getValue(Float.class) - this.world.getCamera().getXmove();
			float wy = rpos[1] - this.world.getCamera().getYmove();
			for(int i = 0; i < this.selectedEntities.size(); i++)
			{
				IEntity e = this.selectedEntities.get(i);
				if(e != null) e.rightClickhappened(this.world.get(this.lastId), wx / this.world.getCamera().getScale(), wy / this.world.getCamera().getScale());
				else this.selectedEntities.remove(e);
			}
		}

		if (Mouse.isButtonDown(MouseButtons.RIGHT.getKey())) {
			this.mouseRightPressed = true;
		} else if (!Mouse.isButtonDown(MouseButtons.RIGHT.getKey())) {
			this.mouseRightPressed = false;
		}

		this.keyDown();

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

		
		if(this.world != null) this.world.draw();
		
		this.tileCullingDebug.setText("Tiles --> drawn: "+this.world.getDrawnTiles()+" / skipped: "+this.world.getSkippedTiles());
		this.fps.setText("FPS: "+Engine.getFPS());
		
		this.con.drawComponents();
		
		if(this.activeToolTip != null)
			this.activeToolTip.draw();
		
		long t1 = System.currentTimeMillis();
		this.world.onTick(); //TODO: hell out of here...
		
		long t2 = System.currentTimeMillis() - t1;

	}
	 
	
	protected void mouseLeftClicked()
	{
		if (this.clickedOne != null) {
			
			this.clickedOne.setActive(false);
		}
		
		
		this.selectedEntities.clear();
		
		Component c = this.con.get(this.lastId);
		IEntity t = this.world.get(this.lastId);

		IGuiObject go = null;
		
		if(c == null)
			go = (IGuiObject)t;
		else
			go = (IGuiObject)c;
		//Logger.info("now active: "+this.lastId+" / actual id: "+c.getId());
		
		if (lastId != 0 && go != null) {
			go.clicked(Mouse.getX(), (SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY()), MouseButtons.LEFT, this.mouseGrabbed && (Math.abs(this.mouseGrabSX - Mouse.getX()) >= 5 || Math.abs(this.mouseGrabSY - (SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY())) >= 5), this.deltaMouseGrabbed, this.mouseGrabSX, this.mouseGrabSY);
			//c.clicked(Mouse.getX(), (SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY()), MouseButtons.LEFT, this.mouseGrabbed);
			go.setActive(true);
			
			this.clickedOne = go;
		}
		
		if(t != null);
			this.selectedEntities.add(t);
		
		this.active = go;
	}
	
	//This method handles all KeyInput...
	private void keyDown()
	{
		this.camChange = false;
		
		Keyboard.enableRepeatEvents(true);
		Keyboard.poll();


		while (Keyboard.next()) {
			
			if (Keyboard.getEventKeyState()) {
				
				
				//Logger.debug("pressed key code: "+Keyboard.getEventKey());
				
				boolean keyAction = false;
				
				if(this.clickedOne != null)
				{
					keyAction = this.clickedOne.key(Keyboard.getEventKey(), Keyboard.getEventCharacter());
				}
				
				if(!keyAction)
				{
					this.handleKey(Keyboard.getEventKey());
				}
				
			}
		}
		
		
		if(Engine.getDelta() > dd)
		{
			this.deltaDebug.setText("Delta peak: "+ Engine.getDelta());
			dd = Engine.getDelta();
		}
		
		if(this.camChange)
		{
			
			this.camDebug.setText(this.world.getCamera().toString());
			this.forceSelect = true;
		}
	}
	
	
	private boolean camChange = false;
	protected void handleKey(int key)
	{
		
		if(key == Keyboard.KEY_RIGHT)
		{
			this.world.getCamera().setXmove(this.world.getCamera().getXmove() - 0.1f);
			this.camChange = true;
		}
		else if(key == Keyboard.KEY_LEFT)
		{
			this.world.getCamera().setXmove(this.world.getCamera().getXmove() + 0.1f);
			this.camChange = true;
		}
		else if(key == Keyboard.KEY_UP)
		{
			this.world.getCamera().setYmove(this.world.getCamera().getYmove() - 0.1f);
			this.camChange = true;
		}
		else if(key == Keyboard.KEY_DOWN)
		{
			this.world.getCamera().setYmove(this.world.getCamera().getYmove() + 0.1f);
			this.camChange = true;
		}
		else if(key == Keyboard.KEY_ADD)
		{
			this.world.getCamera().setScale(this.world.getCamera().getScale() + 0.1f);
			this.camChange = true;
		}
		else if(key == Keyboard.KEY_SUBTRACT)
		{
			this.world.getCamera().setScale(this.world.getCamera().getScale() - 0.1f);
			this.camChange = true;
		}
		else if(key == Keyboard.KEY_Q)
		{
			this.world.getCamera().setRotation(this.world.getCamera().getRotation() + 3.0f);
			this.camChange = true;
		}
		else if(key == Keyboard.KEY_E)
		{
			this.world.getCamera().setRotation(this.world.getCamera().getRotation() - 3.0f);
			this.camChange = true;
		}
		else if(key == Keyboard.KEY_O)
		{
			this.world.setRenderGridOccupiedView(!this.world.isRenderGridOccupiedView());
		}
		else if(key == Keyboard.KEY_G)
		{
			this.world.setRenderGrid(!this.world.isRenderGrid());
		}
		else if(key == Keyboard.KEY_S)
		{
			this.world.setForceSelectionRender(!this.world.isForceSelectionRender());
		}
		else if(key == 43 && Engine.console.isHidden()) //43 is the '^' key
		{
			Engine.console.show();
		}
		
	}
	
	@Override
	protected int select()
	{
		
		List<IGuiObject> pssbl = new ArrayList<IGuiObject>(10);
		if(this.world != null)
		{
			for(int x = 0; x < this.world.getWidth(); x++)
			{
				for(int y = 0; y < this.world.getHeight(); y++)
				{
					Tile t = this.world.getGrid().getTile(x, y);
					float tx = (this.world.getCamera().getXmove() + t.getX() * this.world.getCamera().getScale()) * (1.0f/SetKeys.WIN_ASPECT_RATIO.getValue(Float.class));
					float ty = (this.world.getCamera().getYmove() + t.getY() * this.world.getCamera().getScale()) ;
					
					
					float s = 0.1f * this.world.getCamera().getScale();
					
					int[] ps = Utility.glSizeToPixelSize(s * (1.0f/SetKeys.WIN_ASPECT_RATIO.getValue(Float.class)), s);
					int[] ppos = Utility.glToPixel(tx, ty);
					
					//Logger.debug("MPicking #"+t.getId()+": tx: "+tx+" / ty: "+ty+" / s: "+s+" / ps: {"+ps[0]+","+ps[1]+"} / ppos: {"+ppos[0]+","+ppos[1]+"} / Mouse: {"+Mouse.getX()+","+(SetKeys.WIN_HEIGHT.getValue(Integer.class) - Mouse.getY())+"} ");
					
					if(Utility.between(Mouse.getX(), ppos[0], ppos[0]+ps[0]) &&
						Utility.between(SetKeys.WIN_HEIGHT.getValue(Integer.class) - Mouse.getY(), ppos[1], ppos[1]+ps[1]))
					{
						pssbl.add(t);
					}
					else if(this.world.getCamera().getRotation() != 0.0f){
						pssbl.add(this.world.getGrid().getTile(x, y));
					}
					else
					{
						this.world.getGrid().getTile(x, y).unselected();
					}
				}
			}
			
			//this.world.getGrid().drawSelect();
			
			List<IEntity> e = this.world.getEntites();
			for(int i = 0; i < e.size(); i++)
			{
				//((IGuiObject)e.get(i)).drawSelection();
				
				float tx = (this.world.getCamera().getXmove() + ((IGuiObject)e.get(i)).getX() * this.world.getCamera().getScale()) * (1.0f/SetKeys.WIN_ASPECT_RATIO.getValue(Float.class));
				float ty = (this.world.getCamera().getYmove() + ((IGuiObject)e.get(i)).getY() * this.world.getCamera().getScale());
				
				
				int[] ps = Utility.glSizeToPixelSize(((IGuiObject)e.get(i)).getWidth() * this.world.getCamera().getScale(), ((IGuiObject)e.get(i)).getHeight() * this.world.getCamera().getScale());
				int[] ppos = Utility.glToPixel(tx, ty);
				
				//Logger.debug("MPicking #"+e.get(i).getId()+": tx: "+tx+" / ty: "+ty+" / s: "+s+" / ps: {"+ps[0]+","+ps[1]+"} / ppos: {"+ppos[0]+","+ppos[1]+"} / Mouse: {"+Mouse.getX()+","+(SetKeys.WIN_HEIGHT.getValue(Integer.class) - Mouse.getY())+"} ");
				
				if(Utility.between(Mouse.getX(), ppos[0] - (0.5f * ps[0]), ppos[0] + (0.5f * ps[0])) &&
						Utility.between(SetKeys.WIN_HEIGHT.getValue(Integer.class) - Mouse.getY(), ppos[1] - (0.5f * ps[1]), ppos[1] + (0.5f * ps[1
						                                                                                                                          ])))
				{
					pssbl.add(e.get(i));
				}
				else if(this.world.getCamera().getRotation() != 0.0f){
					pssbl.add(e.get(i));
				}
				else
				{
					e.get(i).unselected();
				}
			}
		}
		
		//int pss = pssbl.size();
		
		pssbl.addAll(super.getSelPos());
		
		Logger.debug("pssbl size: "+pssbl.size());
		
		int id = 0;
		if(pssbl.size() > 1)
		{
			for(int i = 0; i < pssbl.size(); i++)
			{
				pssbl.get(i).drawSelection();
			}
			
			id = super.select();
			
			
		}
		else if(pssbl.size() == 1)
		{
			
			id = pssbl.get(0).getId();
			
		}
		
		//Logger.debug("pssbl: "+pss+" / "+pssbl.size()+" #"+id);
		
		if(!this.mouseGrabbed)
		{
			for (int i = 0; i < pssbl.size(); i++) {
				if(pssbl.get(i).checkId(id))
				{
					this.active = pssbl.get(i);
				}
			}
		}

		this.lastId = id;
		
		/*
		if(this.world != null)
		{
			List<IEntity> e = this.world.getEntites();
			for(int i = 0; i < e.size(); i++)
			{
				IGuiObject go = (IGuiObject)e.get(i);
					
				if(go.isSelected() && go.getId() != id)
				{
					go.unselected();
				}
				else if(!go.isSelected() && go.getId() == id)
				{
					go.selected();
				}
			}
			
			if(this.world.getGrid().checkId(id))
				this.active = this.world.getGrid().getTileAsIGuiObject(id);
		}
		*/

		return id;
	}
	
	private void mouseTileDebug()
	{
		float[] rpos = Utility.pixelToGL(Mouse.getX(), SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY());
		float wx =  rpos[0] * SetKeys.WIN_ASPECT_RATIO.getValue(Float.class) - this.world.getCamera().getXmove();
		float wy = rpos[1] - this.world.getCamera().getYmove();
		
		wx = wx / this.world.getCamera().getScale();
		wy = wy / this.world.getCamera().getScale();
		
		
		if(this.getWorld() != null)
		{
			Tile t = this.getWorld().getGrid().getTile(wx, wy);
			
			this.tx = t.getTX();
			this.ty = t.getTY();
			
			this.mouseTileDebug.setText("Mouse ( "+Mouse.getX()+" / "+(SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY())+" ) Tile ( "+t.getTX()+" / "+t.getTY()+" )");
		}
	}
	
	public void addEntity(IEntity e)
	{
		this.world.add(e);
		this.nextId = this.world.getNextId();
	}
	
	@Override
	public void addComponent(Component c)
	{
		
		super.addComponent(c);
		
		if(this.world != null)
		{
			this.world.setNextId(this.nextId);
		}
	}

	public Player getLocalPlayer() {
		return this.world.getLocalPlayer();
	}

}

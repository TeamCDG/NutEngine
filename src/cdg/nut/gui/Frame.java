package cdg.nut.gui;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cdg.nut.logging.Logger;
import cdg.nut.util.MouseButtons;
import cdg.nut.util.Utility;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLImage;
import cdg.nut.util.gl.GLTexture;
import cdg.nut.util.settings.Cmd;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public abstract class Frame {
	private String title;
	private Container con;
	private GLImage background;

	private int oldMouseX;
	private int oldMouseY;
	private int mouseGrabX;
	private int mouseGrabY;
	
	private int currentCursor = 0;
	private Cursor activeCursor;
	private Cursor normalCursor;
	private boolean mouseLeftPressed;
	private boolean mouseGrabbed;
	private boolean deltaMouseGrabbed;
	private boolean mouseRightPressed;

	private Component active;

	private int nextId = 1;
	private int lastId;

	private int maxSelectSkip = Settings.get(SetKeys.GUI_MAX_SELECT_SKIP, Integer.class);
	private int selectSkip = Settings.get(SetKeys.GUI_MAX_SELECT_SKIP, Integer.class);;

	public Frame()
	{
		this.con = new Container();
	}

	public Frame(GLTexture background)
	{
		this.background = new GLImage(background, -1.0f, 1.0f, 2.0f, -2.0f);
		this.background.setColor(new GLColor(1.0f, 1.0f, 1.0f, 1.0f));
		this.con = new Container();
	}

	public void draw()
	{
		this.selectSkip++;
		Mouse.poll();
		this.deltaMouseGrabbed = this.mouseGrabbed;

		if (
			(
				this.oldMouseX != Mouse.getX() ||
				this.oldMouseY != Mouse.getY()
			) &&
			(
				this.selectSkip > this.maxSelectSkip
			) &&
			Mouse.isInsideWindow()
		) {
			this.selectSkip = 0;

			//GL11.glEnable(GL11.GL_BLEND);
			this.select();
			SetKeys.R_CLEAR_BOTH.execute(null);
		}

		if (Mouse.isButtonDown(MouseButtons.LEFT)) {
			//System.out.println("did="+deltaId+" | lid="+lastId);
			
			if(this.currentCursor == 0 && this.activeCursor != null) { try {
				Mouse.setNativeCursor(this.activeCursor);
				this.currentCursor = 1; 
			} catch (LWJGLException e) {
			} Logger.debug("changing cursor to normal");}
			
			if (this.active != null && this.active.getId() != lastId) {
				this.active.setActive(false);
			}

			Component c = this.con.get(this.lastId);

			if (lastId != 0 && c != null) {
				c.clicked(Mouse.getX(), Mouse.getY(), MouseButtons.LEFT, this.mouseLeftPressed);
			}

			this.active = c;
			this.mouseLeftPressed = true;
		} else if (!Mouse.isButtonDown(MouseButtons.LEFT)) {
			this.mouseLeftPressed = false;
			this.mouseGrabbed = false;
			
			if(this.currentCursor == 1 && this.normalCursor != null) { try {
				Mouse.setNativeCursor(this.normalCursor);
				this.currentCursor = 0; 
			} catch (LWJGLException e) {
			} Logger.debug("changing cursor to active");}
		}

		if (Mouse.isButtonDown(MouseButtons.RIGHT)) {
			this.mouseRightPressed = true;
		} else if (!Mouse.isButtonDown(MouseButtons.RIGHT)) {
			this.mouseRightPressed = false;
		}

		Keyboard.enableRepeatEvents(true);
		Keyboard.poll();

		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState() && this.active!=null) {
				this.active.keyDown(Keyboard.getEventKey(), Keyboard.getEventCharacter());
			}
		}

		if (
			(
				this.oldMouseX != Mouse.getX() ||
				this.oldMouseY != Mouse.getY()
			) &&
			!this.deltaMouseGrabbed
		) {
			this.oldMouseX = Mouse.getX();
			this.oldMouseY = Mouse.getY();
			this.mouseGrabX = Mouse.getX();
			this.mouseGrabY = Mouse.getY();
			this.mouseGrabbed = this.mouseLeftPressed;
		} else if (this.deltaMouseGrabbed) {
			this.oldMouseX = Mouse.getX();
			this.oldMouseY = Mouse.getY();
			this.mouseGrabbed = this.mouseLeftPressed;
		}

		if (this.background != null) this.background.draw();

		this.con.drawComponents();
	}

	private void select()
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

		if (gotId == lastId) {
			return; //my work here is done
		}

		for (int i = 0; i < this.con.getComponentCount(); i++) {
			this.con.getComponents().get(i).checkId(gotId);
		}

		this.lastId = gotId;
	}

	public void addComponent(Component c)
	{
		c.setId(nextId);
		nextId++;

		this.con.add(c);
	}

	public void removeComponent(Component c)
	{
		this.con.remove(c);
	}

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
}

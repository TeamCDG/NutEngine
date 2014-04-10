package cdg.nut.gui;

import cdg.nut.interfaces.ISelectable;
import cdg.nut.logging.Logger;
import cdg.nut.util.BitmapFont;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex4;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLImage;
import cdg.nut.util.gl.GLObject;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

//TODO: this class needs some love
public abstract class Component extends GLImage {

	private Border border;
	private FontObject text;
	private float[] padding;

	public boolean hasBackground = true;
	private boolean autosizeWithText;

	@SuppressWarnings("unused")
	private boolean ownerFontSize = false;

	public Component(float x, float y, float width, float height)
	{
		super(Settings.get(SetKeys.GUI_CMP_BACKGROUND_COLOR, GLColor.class), x, y, width, height);
		this.border = new Border(x, y, width, height);
		this.autosizeWithText = false;
		
		this.setupPadding();
	}

	public Component(int x, int y, int width, int height)
	{
		super(Settings.get(SetKeys.GUI_CMP_BACKGROUND_COLOR, GLColor.class), x, y, width, height);
		this.border = new Border(x, y, width, height);
		this.autosizeWithText = false;
		
		this.setupPadding();
	}
	
	public Component(float x, float y, float width, float height, String text)
	{
		this(x, y, width, height);
		this.text = new FontObject(x+padding[0], y+padding[1], text);		
		this.setTextClipping();
	}

	public Component(int x, int y, int width, int height, String text)
	{
		this(x, y, width, height);
		int[] pad = Utility.glSizeToPixelSize(padding[0], padding[1]);
		this.text = new FontObject(x+pad[0], y+pad[1], text);		
		this.setTextClipping();
	}

	private Component(float x, float y, float[] dim, float[] add)
	{
		super(Settings.get(SetKeys.GUI_CMP_BACKGROUND_COLOR, GLColor.class),
				x, y,
				dim[0] + 2 * add[0],
				dim[1] + 2 * add[1]);
		this.border = new Border(x, y, dim[0] + 2 * add[0], dim[1] + 2 * add[1]);
		this.autosizeWithText = true;
	}

	private Component(int x, int y, int[] dim, int[] add)
	{
		super(Settings.get(SetKeys.GUI_CMP_BACKGROUND_COLOR, GLColor.class),
				x, y,
				dim[0] + 2 * add[0],
				dim[1] + 2 * add[1]);
		this.border = new Border(x, y, dim[0] + 2 * add[0], dim[1] + 2 * add[1]);
		this.autosizeWithText = true;
	}

	public Component(float x, float y, String text)
	{
		this(
			x, y,
			SetKeys.GUI_CMP_FONT.getValue(BitmapFont.class).measureDimensions(
				text, SetKeys.GUI_CMP_FONT_SIZE.getValue(Float.class)),
			Utility.pixelSizeToGLSize(
				Settings.get(SetKeys.GUI_CMP_FONT_PADDING, Integer.class) +
				Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class),
				Settings.get(SetKeys.GUI_CMP_FONT_PADDING, Integer.class) +
				Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class)
			)
		);

		this.setupPadding();
		
		this.text = new FontObject(x+this.padding[0], y+this.padding[1], text);
	}

	public Component(int x, int y, String text)
	{
		this(x, y,
			SetKeys.GUI_CMP_FONT.getValue(BitmapFont.class).measureDimensionsAsInt(
				text,
				SetKeys.GUI_CMP_FONT_SIZE.getValue(Float.class)
			),
			new int[]{
				Settings.get(SetKeys.GUI_CMP_FONT_PADDING, Integer.class) +
				Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class),
				Settings.get(SetKeys.GUI_CMP_FONT_PADDING, Integer.class) +
				Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class)
			}
		);

		this.setupPadding();
		
		int[] pad = Utility.glSizeToPixelSize(this.padding[0], this.padding[1]);
		
		this.text = new FontObject(x+pad[0], y+pad[1], text);
	}

	private void setupPadding()
	{
		int pabs = Settings.get(SetKeys.GUI_CMP_FONT_PADDING, Integer.class) +
				   Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class);

		this.padding = Utility.pixelSizeToGLSize(pabs, pabs);
	}
	
	@Override
	protected void move()
	{
		super.move();
		//this.border.setPosition(this.getPixelX(), this.getPixelY());
	}

	@Override
	protected void drawChildren(boolean selection)
	{
		super.drawChildren(selection);

		if (!selection)						 this.border.draw();
		if (this.text != null && !selection) this.text.draw();
	}

	public boolean isAutosizeWithText() {
		return autosizeWithText;
	}

	public void setAutosizeWithText(boolean autosizeWithText) {
		this.autosizeWithText = autosizeWithText;

		Logger.debug(
			"Dimensions: " +
			(
				this.text.getWidth() +
				2 *
				this.padding[0]
			) +
			"/" +
			(
				this.text.getHeight() +
				2 *
				this.padding[1]
			)
		);
		this.autosize();
		//if((this.text.getWidth()+2*this.padding[0] > this.getWidth() || this.text.getHeight()+2*this.padding[1] > this.getHeight()) && autosizeWithText)
		//	this.setDimensions(this.text.getWidth()+2*this.padding[0]>this.getWidth()?this.text.getWidth()+2*this.padding[0]:this.getWidth(), this.text.getHeight()+2*this.padding[1]>this.getHeight()?this.text.getHeight()+2*this.padding[1]:this.getHeight());
	}

	public void setActive(boolean b) {
		// TODO Auto-generated method stub

	}

	public String getText()
	{
		return this.text.getText();
	}

	public void setText(String text)
	{
		this.text.setText(text);
		this.autosize();
		Logger.debug(
			"Dimensions: " +
			(
				this.text.getWidth() +
				2 *
				this.padding[0]
			) +
			"/" +
			(
			this.text.getHeight() +
			2 *
			this.padding[1]
			)
		);
	}

	private void autosize()
	{
		if (this.autosizeWithText) {
			this.setDimension(this.text.getWidth()+2*this.padding[0], this.text.getHeight()+2*this.padding[1]);
			this.border.setDimension(this.text.getWidth()+2*this.padding[0], this.text.getHeight()+2*this.padding[1]);
		}
	}

	public float getFontSize()
	{
		return this.text.getFontSize();
	}

	public void setFontSize(float fontSize)
	{
		this.text.setFontSize(fontSize);
		this.ownerFontSize = true;
		this.autosize();
	}

	public void setFontSize(int fontSize)
	{
		this.text.setFontSize(fontSize);
		this.ownerFontSize = true;
		this.autosize();
	}

	public void resetFontSize()
	{
		this.text.setFontSize(Settings.get(SetKeys.GUI_CMP_FONT_SIZE, Float.class));
		this.ownerFontSize = false;
		this.autosize();
	}

	public void keyDown(int eventKey, char eventCharacter) {
		// TODO Auto-generated method stub

	}

	public void clicked(int x, int y, int left, boolean mouseLeftPressed) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSelected(boolean value)
	{
		super.setSelected(value);

		//TODO: owner override color
		if (value == true) {
			this.setColor(Settings.get(SetKeys.GUI_CMP_BACKGROUND_H_COLOR, GLColor.class));
			this.border.setColor(Settings.get(SetKeys.GUI_CMP_BORDER_H_COLOR, GLColor.class));
		} else {
			this.setColor(Settings.get(SetKeys.GUI_CMP_BACKGROUND_COLOR, GLColor.class));
			this.border.setColor(Settings.get(SetKeys.GUI_CMP_BORDER_COLOR, GLColor.class));
		}
	}

	@Override
	public boolean checkId(int id)
	{
		if (this.getId() == id) {
			this.setSelected(true);
			return true;
		} else {
			this.setSelected(false);
			return false;
		}
	}
	
	@Override
	public void setDimension(float width, float height)
	{
		super.setDimension(width, height);
		this.setTextClipping();
	}
	
	private void setTextClipping()
	{
		if(this.text != null)
		{
			Vertex4 ca = new Vertex4(this.getX()+this.padding[0],
					 this.getY()+this.padding[1],
					 (this.getX()+this.getWidth())-this.padding[0],
					 (this.getY()+this.getHeight())-this.padding[1]);
			this.text.setClippingArea(ca);
			
			Logger.debug("text clipping area: "+ca.toString(), "Component.setTextClipping");
		}
	}
}

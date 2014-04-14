package cdg.nut.gui;

import java.util.ArrayList;
import java.util.List;

import cdg.nut.interfaces.ISelectable;
import cdg.nut.logging.Logger;
import cdg.nut.util.BitmapFont;
import cdg.nut.util.Colors;
import cdg.nut.util.MouseButtons;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex4;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLImage;
import cdg.nut.util.gl.GLObject;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;
import cdg.nut.interfaces.ISettingsListener;
import cdg.nut.interfaces.IClickListener;

//TODO: this class needs some love
public abstract class Component extends GLImage implements ISettingsListener {

	private Border border;
	private boolean customFont, customFontSize, customFontC, customFontHC, customFontAC = false;
	private FontObject text;
	private float[] padding;

	private boolean hasBackground = true;
	private boolean autosizeWithText;
	
	private boolean customBgC, customBgHC, customBgAC = false;
	private GLColor backgroundColor;
	private GLColor backgroundHighlightColor;
	private GLColor backgroundActiveColor;
	
	private boolean customBorC, customBorHC, customBorAC = false;
	private GLColor borderColor;
	private GLColor borderHighlightColor;
	private GLColor borderActiveColor;
	
	private boolean dragable;
	
	private List<IClickListener> clickListener = new ArrayList<IClickListener>();
	
	
	@SuppressWarnings("unused")
	private boolean hasBorder = true;
	private GLColor fontActiveColor;
	private GLColor fontColor;
	private GLColor fontHighlightColor;
	private boolean active;

	public Component(float x, float y, float width, float height)
	{
		super(Settings.get(SetKeys.GUI_CMP_BACKGROUND_COLOR, GLColor.class), x, y, width, height);
		this.border = new Border(x, y, width, height);
		this.autosizeWithText = false;
		
		this.setupPadding();
		
		this.restoreDefaults();
	}

	public Component(int x, int y, int width, int height)
	{
		super(Settings.get(SetKeys.GUI_CMP_BACKGROUND_COLOR, GLColor.class), x, y, width, height);
		this.border = new Border(x, y, width, height);
		this.autosizeWithText = false;
		
		this.setupPadding();
		
		this.restoreDefaults();
	}
	
	public Component(float x, float y, float width, float height, String text)
	{
		this(x, y, width, height);
		this.text = new FontObject(x+padding[0], y+padding[1], text);		
		this.setTextClipping();
		
		this.restoreDefaults();
	}

	public Component(int x, int y, int width, int height, String text)
	{
		this(x, y, width, height);
		int[] pad = Utility.glSizeToPixelSize(padding[0], padding[1]);
		this.text = new FontObject(x+pad[0], y+pad[1], text);		
		this.setTextClipping();
		
		this.restoreDefaults();
	}

	private Component(float x, float y, float[] dim, float[] add)
	{
		super(Settings.get(SetKeys.GUI_CMP_BACKGROUND_COLOR, GLColor.class),
				x, y,
				dim[0] + 2 * add[0],
				dim[1] + 2 * add[1]);
		this.border = new Border(x, y, dim[0] + 2 * add[0], dim[1] + 2 * add[1]);
		this.autosizeWithText = true;
		
		this.restoreDefaults();
	}

	private Component(int x, int y, int[] dim, int[] add)
	{
		super(Settings.get(SetKeys.GUI_CMP_BACKGROUND_COLOR, GLColor.class),
				x, y,
				dim[0] + 2 * add[0],
				dim[1] + 2 * add[1]);
		this.border = new Border(x, y, dim[0] + 2 * add[0], dim[1] + 2 * add[1]);
		this.autosizeWithText = true;
		
		this.restoreDefaults();
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
		
		this.restoreDefaults();
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
		
		this.restoreDefaults();
	}
	
	private void setColor()
	{
		if(this.active)
		{
			this.setColor(this.backgroundActiveColor);
			if(this.border != null) this.border.setColor(this.borderActiveColor);
			if(this.text != null)   this.text.setColor(this.fontActiveColor);
		}
		else if(this.isSelected())
		{
			this.setColor(this.backgroundHighlightColor);
			if(this.border != null) this.border.setColor(this.borderHighlightColor);
			if(this.text != null)   this.text.setColor(this.fontHighlightColor);
		}
		else
		{
			this.setColor(this.backgroundColor);
			if(this.border != null) this.border.setColor(this.borderColor);
			if(this.text != null)   this.text.setColor(this.fontColor);
		}
	}
	
	public void restoreDefaults()
	{
		this.customBgAC = false; this.backgroundActiveColor = Settings.get(SetKeys.GUI_CMP_BACKGROUND_A_COLOR, GLColor.class);
		this.customBgC = false; this.backgroundColor = Settings.get(SetKeys.GUI_CMP_BACKGROUND_COLOR, GLColor.class);
		this.customBgHC = false; this.backgroundHighlightColor = Settings.get(SetKeys.GUI_CMP_BACKGROUND_H_COLOR, GLColor.class);
		
		this.customBorAC = false; this.borderActiveColor = Settings.get(SetKeys.GUI_CMP_BORDER_A_COLOR, GLColor.class);
		this.customBorC = false; this.borderColor = Settings.get(SetKeys.GUI_CMP_BORDER_COLOR, GLColor.class);
		this.customBorHC = false; this.borderHighlightColor = Settings.get(SetKeys.GUI_CMP_BORDER_H_COLOR, GLColor.class);
				
		this.customFontAC = false; this.fontActiveColor = Settings.get(SetKeys.GUI_CMP_FONT_A_COLOR, GLColor.class);
		this.customFontC = false; this.fontColor = Settings.get(SetKeys.GUI_CMP_FONT_COLOR, GLColor.class);
		this.customFontHC = false; this.fontHighlightColor = Settings.get(SetKeys.GUI_CMP_FONT_H_COLOR, GLColor.class);		
		
		this.setColor();
	}
	
	public void valueChanged(SetKeys key) {
		switch(key)
		{
		case GUI_CMP_BACKGROUND_A_COLOR:
			if(!this.customBgAC) this.backgroundActiveColor =  key.getValue(GLColor.class);
			break;
		case GUI_CMP_BACKGROUND_COLOR:
			if(!this.customBgC) this.backgroundColor =  key.getValue(GLColor.class);
			break;
		case GUI_CMP_BACKGROUND_H_COLOR:
			if(!this.customBgHC) this.backgroundHighlightColor =  key.getValue(GLColor.class);
			break;
		case GUI_CMP_BORDER_A_COLOR:
			if(!this.customBorAC) this.borderActiveColor =  key.getValue(GLColor.class);
			break;
		case GUI_CMP_BORDER_COLOR:
			if(!this.customBorC) this.borderColor =  key.getValue(GLColor.class);
			break;
		case GUI_CMP_BORDER_H_COLOR:
			if(!this.customBorHC) this.borderHighlightColor =  key.getValue(GLColor.class);
			break;
		case GUI_CMP_BORDER_SIZE:
			this.setupPadding();
			break;
		case GUI_CMP_FONT:
			break;
		case GUI_CMP_FONT_A_COLOR:
			if(!this.customFontAC) this.fontActiveColor =  key.getValue(GLColor.class);
			break;
		case GUI_CMP_FONT_COLOR:
			if(!this.customFontC) this.fontColor =  key.getValue(GLColor.class);
			break;
		case GUI_CMP_FONT_H_COLOR:
			if(!this.customFontHC) this.fontHighlightColor =  key.getValue(GLColor.class);
			break;
		case GUI_CMP_FONT_PADDING:
			this.setupPadding();
			break;
		case GUI_CMP_FONT_SIZE:
			if(!this.customFontSize) this.text.setFontSize(key.getValue(Float.class));
			break;
		case GUI_MAX_SELECT_SKIP:
			break;
		case R_CLEAR_BOTH:
			break;
		case R_CLEAR_COLOR:
			break;
		case R_CLEAR_DEPTH:
			break;
		case R_MAX_FPS:
			break;
		case R_VSYNC:
			break;
		case WIN_ASPECT_RATIO:
			break;
		case WIN_FULLSCREEN:
			break;
		case WIN_HEIGHT:
			break;
		case WIN_RESOLUTION:
			break;
		case WIN_RESOLUTION_CHANGED:
			break;
		case WIN_WIDTH:
			break;
		default:
			break;
		
		}
	}

	private void setupPadding()
	{
		int pabs = Settings.get(SetKeys.GUI_CMP_FONT_PADDING, Integer.class) +
				   Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class);

		this.padding = Utility.pixelSizeToGLSize(pabs, pabs);
		
		if(this.text != null) this.text.setPosition(this.getPixelX()+pabs, this.getPixelY()+pabs);
	}
	
	@Override
	protected void move()
	{
		super.move();
		
		//Logger.debug("Pixel coordinates: "+this.getPixelX()+" ("+this.getX()+") / "+this.getPixelY()+" ("+this.getY()+")", "Component.move");
		this.border.setPosition(this.getPixelX(), this.getPixelY());
		if(this.text != null) this.text.setPosition(this.getX()+this.padding[0], this.getY()+this.padding[1]);
		
		this.setTextClipping();
	}

	@Override
	protected void drawChildren(boolean selection)
	{
		super.drawChildren(selection);

		if (!selection && this.hasBorder)	 this.border.draw();
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
		this.active = b;
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
		this.customFontSize = true;
		this.autosize();
	}

	public void setFontSize(int fontSize)
	{
		this.text.setFontSize(fontSize);
		this.customFontSize = true;
		this.autosize();
	}

	public void resetFontSize()
	{
		this.text.setFontSize(Settings.get(SetKeys.GUI_CMP_FONT_SIZE, Float.class));
		this.customFontSize = false;
		this.autosize();
	}

	public void keyDown(int eventKey, char eventCharacter) {
		// TODO Auto-generated method stub

	}

	public void clicked(int x, int y, MouseButtons button, boolean mouseLeftPressed) {
		for(int i = 0; i < this.clickListener.size(); i++)
		{
			this.clickListener.get(i).onClick(x, y, button);
		}
	}

	@Override
	public void setSelected(boolean value)
	{
		super.setSelected(value);

		//TODO: custom override color
		if (value == true) {
			if(this.hasBackground) this.setColor(this.backgroundHighlightColor);
			this.border.setColor(this.borderHighlightColor);
		} else {
			if(this.hasBackground) this.setColor(this.backgroundColor);
			this.border.setColor(this.borderColor);
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

	public boolean hasBackground() {
		return hasBackground;
	}

	public void setHasBackground(boolean hasBackground) {
		if(hasBackground && !this.hasBackground)
			this.setColor(this.backgroundColor);
		else if(!hasBackground && this.hasBackground)
			this.setColor(Colors.TRANSPARENT.getGlColor());
		this.hasBackground = hasBackground;
		
	}


	/**
	 * @return the backgroundColor
	 */
	public GLColor getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @param backgroundColor the backgroundColor to set
	 */
	public void setBackgroundColor(GLColor backgroundColor) {
		this.backgroundColor = backgroundColor;
		if(!this.active && !this.isSelected()&& this.hasBackground)
			this.setColor(backgroundColor);
	}
	
	public void resetBackgroundColor() {
		this.setBackgroundColor(Settings.get(SetKeys.GUI_CMP_BACKGROUND_COLOR, GLColor.class));
		this.customBgC = false;
	}

	/**
	 * @return the backgroundHighlightColor
	 */
	public GLColor getBackgroundHighlightColor() {
		return backgroundHighlightColor;
	}

	/**
	 * @param backgroundHighlightColor the backgroundHighlightColor to set
	 */
	public void setBackgroundHighlightColor(GLColor backgroundHighlightColor) {
		this.backgroundHighlightColor = backgroundHighlightColor;
		if(!this.active && this.isSelected() && this.hasBackground)
			this.setColor(backgroundHighlightColor);
	}
	
	public void resetBackgroundHighlightColor() {
		this.setBackgroundHighlightColor(Settings.get(SetKeys.GUI_CMP_BACKGROUND_H_COLOR, GLColor.class));
		this.customBgHC = false;
	}

	/**
	 * @return the backgroundActiveColor
	 */
	public GLColor getBackgroundActiveColor() {
		return backgroundActiveColor;
	}

	/**
	 * @param backgroundActiveColor the backgroundActiveColor to set
	 */
	public void setBackgroundActiveColor(GLColor backgroundActiveColor) {
		this.backgroundActiveColor = backgroundActiveColor;
		if(this.active && this.hasBackground)
			this.setColor(backgroundActiveColor);
	}
	
	public void resetBackgroundActiveColor() {
		this.setBackgroundActiveColor(Settings.get(SetKeys.GUI_CMP_BACKGROUND_A_COLOR, GLColor.class));
		this.customBgAC = false;
	}

	/**
	 * @return the borderColor
	 */
	public GLColor getBorderColor() {
		return borderColor;
	}

	/**
	 * @param borderColor the borderColor to set
	 */
	public void setBorderColor(GLColor borderColor) {
		this.borderColor = borderColor;
		if(!this.active && !this.isSelected())
			this.border.setColor(borderColor);
	}
	
	public void resetBorderColor() {
		this.setBorderColor(Settings.get(SetKeys.GUI_CMP_BORDER_COLOR, GLColor.class));
		this.customBorC = false;
	}

	/**
	 * @return the borderHighlightColor
	 */
	public GLColor getBorderHighlightColor() {
		return borderHighlightColor;
	}

	/**
	 * @param borderHighlightColor the borderHighlightColor to set
	 */
	public void setBorderHighlightColor(GLColor borderHighlightColor) {
		this.borderHighlightColor = borderHighlightColor;
		if(!this.active && this.isSelected())
			this.border.setColor(borderHighlightColor);
	}
	
	public void resetBorderHighlightColor() {
		this.setBorderHighlightColor(Settings.get(SetKeys.GUI_CMP_BORDER_H_COLOR, GLColor.class));
		this.customBorHC = false;
	}

	/**
	 * @return the borderActiveColor
	 */
	public GLColor getBorderActiveColor() {
		return borderActiveColor;
	}

	/**
	 * @param borderActiveColor the borderActiveColor to set
	 */
	public void setBorderActiveColor(GLColor borderActiveColor) {
		this.borderActiveColor = borderActiveColor;
		if(this.active)
			this.border.setColor(borderActiveColor);
	}
	
	public void resetBorderActiveColor() {
		this.setBorderActiveColor(Settings.get(SetKeys.GUI_CMP_BORDER_A_COLOR, GLColor.class));
		this.customBorAC = false;
	}

	/**
	 * @return the textColor
	 */
	public GLColor getFontColor() {
		return fontColor;
	}

	/**
	 * @param textColor the textColor to set
	 */
	public void setFontColor(GLColor fontColor) {
		this.fontColor = fontColor;
		if(!this.active && !this.isSelected() &&this.text != null)
			this.text.setColor(fontColor);
	}
	
	public void resetFontColor() {
		this.setFontColor(Settings.get(SetKeys.GUI_CMP_FONT_COLOR, GLColor.class));
		this.customFontC = false;
	}

	/**
	 * @return the textHighlightColor
	 */
	public GLColor getFontHighlightColor() {
		return fontHighlightColor;
	}

	/**
	 * @param textHighlightColor the textHighlightColor to set
	 */
	public void setFontHighlightColor(GLColor fontHighlightColor) {
		this.fontHighlightColor = fontHighlightColor;
		this.customFontHC = true;
		if(!this.active && this.isSelected() && this.text != null)
			this.text.setColor(fontHighlightColor);
	}
	
	public void resetFontHighlightColor() {
		this.setFontHighlightColor(Settings.get(SetKeys.GUI_CMP_FONT_H_COLOR, GLColor.class));
		this.customFontHC = false;
	}

	/**
	 * @return the textActiveColor
	 */
	public GLColor getFontActiveColor() {
		return fontActiveColor;
	}

	/**
	 * @param textActiveColor the textActiveColor to set
	 */
	public void setFontActiveColor(GLColor fontActiveColor) {
		this.fontActiveColor = fontActiveColor;
		this.customFontAC = true;
		if(this.active && this.text != null)
			this.text.setColor(fontActiveColor);
	}
	
	public void resetFontActiveColor() {
		this.setFontActiveColor(Settings.get(SetKeys.GUI_CMP_FONT_A_COLOR, GLColor.class));
		this.customFontAC = false;
	}

	public boolean isDragable() {
		return dragable;
	}

	public void setDragable(boolean dragable) {
		this.dragable = dragable;
	}

	/**
	 * @return the hasBorder
	 */
	public boolean hasBorder() {
		return hasBorder;
	}

	/**
	 * @param hasBorder the hasBorder to set
	 */
	public void setHasBorder(boolean hasBorder) {
		this.hasBorder = hasBorder;
	}

	public void removeClickListener(IClickListener clickListener) {
		this.clickListener.remove(clickListener);
	}

	public void addClickListener(IClickListener clickListener) {
		this.clickListener.add(clickListener);
	}
}

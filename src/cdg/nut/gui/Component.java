package cdg.nut.gui;

import java.util.ArrayList;
import java.util.List;

import cdg.nut.interfaces.ISelectable;
import cdg.nut.logging.Logger;
import cdg.nut.util.BitmapFont;
import cdg.nut.util.Colors;
import cdg.nut.util.DefaultShader;
import cdg.nut.util.MouseButtons;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex4;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLImage;
import cdg.nut.util.gl.GLObject;
import cdg.nut.util.gl.GLTexture;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;
import cdg.nut.interfaces.IKeyboardListener;
import cdg.nut.interfaces.IScrollListener;
import cdg.nut.interfaces.ISettingsListener;
import cdg.nut.interfaces.IClickListener;

//TODO: this class needs some love
public abstract class Component extends GLImage implements ISettingsListener, IScrollListener {

	private Frame parent;
	
	private Border border;
	private boolean customFont, customFontSize, customFontC, customFontHC, customFontAC, customFontDC = false;
	private FontObject text;
	private GLImage dsBEx;
	private GLImage icon;
	private ToolTip tooltip;
	private float[] padding;

	private boolean hasBackground = true;
	private boolean autosizeWithText;
	private boolean enabled = true;
	
	private boolean customBgC, customBgHC, customBgAC, customBgDC = false;
	private GLColor backgroundColor;
	private GLColor backgroundHighlightColor;
	private GLColor backgroundActiveColor;
	private GLColor backgroundDisabledColor;
	
	private boolean customBorC, customBorHC, customBorAC, customBorDC = false;
	private GLColor borderColor;
	private GLColor borderHighlightColor;
	private GLColor borderActiveColor;
	private GLColor borderDisabledColor;
	
	private boolean dragable, textSelectable = false;
	private boolean activeable = false;
	private boolean centerText = false;
	
	private boolean xscroll, yscroll = false;
	private XScrollBar xsb;
	private YScrollBar ysb;
	
	private List<IClickListener> clickListener = new ArrayList<IClickListener>();
	private List<IKeyboardListener> keyListener = new ArrayList<IKeyboardListener>();
	
	@SuppressWarnings("unused")
	private boolean hasBorder = true;
	private GLColor fontActiveColor;
	private GLColor fontColor;
	private GLColor fontHighlightColor;
	private GLColor fontDisabledColor;
	private boolean active;

	public Component(float x, float y, float width, float height)
	{
		super(Settings.get(SetKeys.GUI_CMP_BACKGROUND_COLOR, GLColor.class), x, y, width, height);
		this.border = new Border(x, y, width, height);
		this.autosizeWithText = false;
		
		this.init();
		
	}

	public Component(int x, int y, int width, int height)
	{
		super(Settings.get(SetKeys.GUI_CMP_BACKGROUND_COLOR, GLColor.class), x, y, width, height);
		this.border = new Border(x, y, width, height);
		this.autosizeWithText = false;
		
		this.init();
	}
	
	public Component(float x, float y, float width, float height, String text)
	{
		this(x, y, width, height);
		
		
		//this.setText(text);
		this.text = new FontObject(this.getTextX(), this.getTextY(), text);

		this.setTextClipping();
	}

	public Component(int x, int y, int width, int height, String text)
	{
		this(x, y, width, height);
		
		
		//this.setText(text);
		this.text = new FontObject(this.getTextX(), this.getTextY(), text);

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
		
		this.init();
	}

	private Component(int x, int y, int[] dim, int[] add)
	{
		super(Settings.get(SetKeys.GUI_CMP_BACKGROUND_COLOR, GLColor.class),
				x, y,
				dim[0] + 2 * add[0],
				dim[1] + 2 * add[1]);
		this.border = new Border(x, y, dim[0] + 2 * add[0], dim[1] + 2 * add[1]);
		this.autosizeWithText = true;
		
		this.init();
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
				
		//this.setText(text);
		this.text = new FontObject(this.getTextX(), this.getTextY(), text);
		
		this.setTextClipping();
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
		
		//this.setText(text);
		this.text = new FontObject(this.getTextX(), this.getTextY(), text);

		this.setTextClipping();
		
	}
	
	private void init()
	{
		
		
		this.setupPadding();
		this.restoreDefaults();
		Settings.addListener(this);
		
		int sbs = Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class);
		int bs = Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class);
		
		this.xsb = new XScrollBar(this.getPixelX()+bs, 
				this.getPixelY()+this.getPixelHeight()-bs-sbs, 
				this.getPixelWidth() - 2*bs);
		this.xsb.addScrollListener(this);
		
		this.ysb = new YScrollBar(this.getPixelX()+this.getPixelWidth()-bs-sbs, 
				this.getPixelY()+bs, 
				this.getPixelHeight() - 2*bs);
		this.ysb.addScrollListener(this);
		
		this.dsBEx = new GLImage(this.borderColor,
								 this.getPixelX()+this.getPixelWidth()-bs-sbs,
								 this.getPixelY()+this.getPixelHeight()-bs-sbs,
								 sbs,
								 sbs);
		
		this.tooltip = new ToolTip("");
		
		this.text = new FontObject(this.getTextX(), this.getTextY(), "");
		
	}
	
	private void setColor()
	{
		if(!this.enabled)
		{
			
			Logger.debug("border: "+this.borderDisabledColor.toReadableString(),"Component.setColor()");
			Logger.debug("background: "+this.backgroundDisabledColor.toReadableString(),"Component.setColor()");
			this.setColor(this.backgroundDisabledColor);
			if(this.border != null) this.border.setColor(this.borderDisabledColor);
			if(this.dsBEx != null)  this.dsBEx.setColor(this.borderDisabledColor);
			if(this.text != null)   this.text.setColor(this.fontDisabledColor);
		}
		else if(this.active)
		{
			this.setColor(this.backgroundActiveColor);
			if(this.border != null) this.border.setColor(this.borderActiveColor);
			if(this.dsBEx != null)  this.dsBEx.setColor(this.borderActiveColor);
			if(this.text != null)   this.text.setColor(this.fontActiveColor);
		}
		else if(this.isSelected())
		{
			this.setColor(this.backgroundHighlightColor);
			if(this.border != null) this.border.setColor(this.borderHighlightColor);
			if(this.dsBEx != null)  this.dsBEx.setColor(this.borderHighlightColor);
			if(this.text != null)   this.text.setColor(this.fontHighlightColor);
		}
		else
		{
			this.setColor(this.backgroundColor);
			if(this.border != null) this.border.setColor(this.borderColor);
			if(this.dsBEx != null)  this.dsBEx.setColor(this.borderColor);
			if(this.text != null)   this.text.setColor(this.fontColor);
		}
	}
	
	public void restoreDefaults()
	{
		this.resetBackgroundActiveColor();
		this.resetBackgroundColor();
		this.resetBackgroundDisabledColor();
		this.resetBackgroundHighlightColor();
		
		this.resetBorderActiveColor();
		this.resetBorderColor();
		this.resetBorderDisabledColor();
		this.resetBorderHighlightColor();
		
		this.resetFontActiveColor();
		this.resetFontColor();
		this.resetFontDisabledColor();
		this.resetFontHighlightColor();
		
		this.setColor();
	}
	
	public void valueChanged(SetKeys key) {
		
		Logger.debug("recieved change key: "+key.name().toLowerCase(), "Component.valueChanged");
		switch(key)
		{
		case GUI_CMP_BACKGROUND_A_COLOR:
			if(!this.customBgAC) this.resetBackgroundActiveColor();
			break;
		case GUI_CMP_BACKGROUND_COLOR:
			if(!this.customBgC) this.resetBackgroundColor();
			break;
		case GUI_CMP_BACKGROUND_H_COLOR:
			if(!this.customBgHC) this.resetBackgroundHighlightColor();
			break;
		case GUI_CMP_BORDER_A_COLOR:
			if(!this.customBorAC) this.resetBorderActiveColor();
			break;
		case GUI_CMP_BORDER_COLOR:
			if(!this.customBorC) this.resetBorderColor();
			break;
		case GUI_CMP_BORDER_H_COLOR:
			if(!this.customBorHC) this.resetBorderHighlightColor();
			break;
		case GUI_CMP_BORDER_SIZE:
			this.setupPadding();
			break;
		case GUI_CMP_FONT:
			break;
		case GUI_CMP_FONT_A_COLOR:
			if(!this.customFontAC) this.resetFontActiveColor(); 
			break; 
		case GUI_CMP_FONT_COLOR:
			if(!this.customFontC) this.resetFontColor();
			break;
		case GUI_CMP_FONT_H_COLOR:
			if(!this.customFontHC) this.resetFontHighlightColor();
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
		if(this.xsb != null) this.xsb.setPosition(this.getPixelX()+Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class), 
				this.getPixelY()+this.getPixelHeight()-Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class)-Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class));
		
		if(this.ysb != null) this.ysb.setPosition(this.getPixelX()+this.getPixelWidth()-Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class)-Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class), 
				this.getPixelY()+Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class));
		
		if(this.text != null) this.text.setPosition(this.getX()+this.padding[0], this.getY()+this.padding[1]);
		
		if(this.dsBEx != null) this.dsBEx.setPosition(this.getPixelX()+this.getPixelWidth()-Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class)-Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class), 
													  this.getPixelY()+this.getPixelHeight()-Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class)-Settings.get(SetKeys.GUI_CMP_SCROLLBAR_SIZE, Integer.class));
		
		this.setTextClipping();
		
		if(this.icon != null)
		{
			GLTexture tex = this.icon.getImage();
			this.icon = null;
			this.setIcon(tex);
		}
	}

	@Override
	protected void drawChildren(boolean selection)
	{
		super.drawChildren(selection);

		if(this.tooltipShown && this.parent.getActiveToolTip() != this.tooltip)
			this.tooltipShown = false;
		
		if (!selection && this.hasBorder)	 this.border.draw();
		if (this.text != null && !selection) this.text.draw();
		if (this.xsb != null && !selection && this.xscroll) this.xsb.draw();
		if (this.ysb != null && !selection && this.yscroll) this.ysb.draw();
		if (this.yscroll && this.xscroll && this.dsBEx != null && this.scrollable)  this.dsBEx.draw();
		if(this.icon != null) this.icon.draw();
		//if(this.tooltip != null && this.tooltipShown) this.tooltip.draw();
	}

	public boolean isAutosizeWithText() {
		return autosizeWithText;
	}

	public void setAutosizeWithText(boolean autosizeWithText) {
		this.autosizeWithText = autosizeWithText;
		this.autosize();
		//if((this.text.getWidth()+2*this.padding[0] > this.getWidth() || this.text.getHeight()+2*this.padding[1] > this.getHeight()) && autosizeWithText)
		//	this.setDimensions(this.text.getWidth()+2*this.padding[0]>this.getWidth()?this.text.getWidth()+2*this.padding[0]:this.getWidth(), this.text.getHeight()+2*this.padding[1]>this.getHeight()?this.text.getHeight()+2*this.padding[1]:this.getHeight());
	}

	protected void setActive(boolean b) {
		
		Logger.debug("active: "+b+"/ activable: "+this.activeable, "Component.setActive");
		
		if(this.activeable && this.enabled)
		{
			this.active = b;
			this.setColor();
		}
	}

	public String getText()
	{
		return this.text.getText();
	}

	public void setText(String text)
	{
		this.text.setText(text);
		
		if(this.centerText)
			this.text.setPosition(this.getTextX(), this.getTextY());
		
		this.setScroll();
		
		//if(this.xsb != null) this.xsb.setScrollValue(this.xsb.getMaxValue());
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
		if(this.icon != null)
		{
			GLTexture tex = this.icon.getImage();
			this.icon = null;
			this.setIcon(tex);
		}
		
		if (this.autosizeWithText) {
			this.xscroll = false;
			this.yscroll = false;
			this.setDimension(this.text.getPixelWidth()+this.getTextX()+this.getTextPad()-this.getPixelX(), this.text.getPixelHeight()+this.getTextY()+this.getTextPad()-this.getPixelY());
			Logger.debug("fh: "+text.getPixelHeight(),"Component.autosize");
			this.border.setDimension(this.text.getPixelWidth()+this.getTextX()+this.getTextPad()-this.getPixelX(), this.text.getPixelHeight()+this.getTextY()+this.getTextPad()-this.getPixelY());
		}
		
		
	}

	public int getTextPad() {
		
		if(!this.hasBorder && !this.hasBackground)
			return 0;
		else
			return Settings.get(SetKeys.GUI_CMP_FONT_PADDING, Integer.class) +
				   Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class);
	}

	public float getFontSize()
	{
		return this.text.getFontSize();
	}
	
	public int getFontPixelSize()
	{
		return Utility.glSizeToPixelSize(0, this.text.getFontSize())[1];
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
Logger.debug("size: "+fontSize+" / fh: "+this.text.getPixelHeight()+" / th: "+this.getPixelHeight(),"Component.setFontSize");
	}

	public void resetFontSize()
	{
		this.text.setFontSize(Settings.get(SetKeys.GUI_CMP_FONT_SIZE, Float.class));
		this.customFontSize = false;
		this.autosize();
	}

	protected void key(int eventKey, char eventCharacter) {
		
		if(!this.enabled)
			return;
		
		for(int i = 0; i < this.keyListener.size(); i ++)
		{
			this.keyListener.get(i).keyDown(eventKey, eventCharacter);
		}

	}

	
	private boolean xscrollGrabbed, yscrollGrabbed = false;
	private boolean tooltipShown;
	private boolean manualTThide;

	private boolean manualTTshow;

	private boolean scrollable = true;

	private int[] addPad;

	private int addPadX;

	private int addPadY;
	protected final void clicked(int x, int y, MouseButtons button, boolean grabbed, boolean deltaDown, int grabx, int graby) {
		
		Logger.debug("xscrollGrabbed: "+this.xscrollGrabbed+" / yscrollGrabbed: "+this.yscrollGrabbed+" / deltadown: "+deltaDown,"Component.clicked");
		
		if(!this.enabled)
			return;
		
		if(this.textSelectable)
			this.onClick(x, y, button, grabbed, grabx, graby);
		
		if(this.xsb != null && this.xscroll && this.xsb.isScrollDings(x, y) && grabbed && !this.yscrollGrabbed)
		{
			this.xscrollGrabbed = true;
			this.xsb.setScrollValue(this.xsb.getScrollOfAbs(x));
		}
		else if(this.xscrollGrabbed && grabbed && this.xsb != null && !this.yscrollGrabbed)
		{
			this.xsb.setScrollValue(this.xsb.getScrollOfAbs(x));
		}
		else if(this.xsb != null && this.xscroll && this.xsb.isScrollBar(x, y) && !this.yscrollGrabbed)
		{
			this.xsb.setScrollValue(this.xsb.getScrollOfAbs(x));
		}
		else if(this.ysb != null && this.yscroll && this.ysb.isScrollDings(x, y) && grabbed && !this.xscrollGrabbed)
		{
			this.yscrollGrabbed = true;
			this.ysb.setScrollValue(this.ysb.getScrollOfAbs(y));
		}
		else if(this.yscrollGrabbed && grabbed && this.ysb != null && !this.xscrollGrabbed)
		{
			this.ysb.setScrollValue(this.ysb.getScrollOfAbs(y));
		}
		else if(this.ysb != null && this.yscroll && this.ysb.isScrollBar(x, y) && !this.xscrollGrabbed)
		{
			this.ysb.setScrollValue(this.ysb.getScrollOfAbs(y));
		}
		else
		{
			this.xscrollGrabbed = false;
			this.yscrollGrabbed = false;
			this.onClick(x, y, button, grabbed, grabx, graby);
			
			if(!deltaDown)
			{
				for(int i = 0; i < this.clickListener.size(); i++)
				{
					this.clickListener.get(i).onClick(x, y, button, grabx, graby);
				}
			}
		}
		
		
	}
	
	protected void onClick(int x, int y, MouseButtons button, boolean grabbed, int grabx, int graby){}
	

	@Override
	public void setSelected(boolean value)
	{
		super.setSelected(value && this.enabled);

		//TODO: custom override color
		if (value == true && !this.active && this.enabled) {
			if(this.hasBackground) this.setColor(this.backgroundHighlightColor);
			this.border.setColor(this.borderHighlightColor);
			if(this.dsBEx != null)  this.dsBEx.setColor(borderHighlightColor);
		} else if(!this.active && this.enabled) {
			if(this.hasBackground) this.setColor(this.backgroundColor);
			this.border.setColor(this.borderColor);
			if(this.dsBEx != null)  this.dsBEx.setColor(borderColor);
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
	
	private void setScroll()
	{
		
		
		if(!this.scrollable)
			return;
		
		this.setTextClipping(true);
		
		
		int pl = Utility.glToPixel(this.text.getClippingArea().getX(), 0)[0];
		int pr = Utility.glToPixel(this.text.getClippingArea().getZ(), 0)[0];
		int pt = Utility.glToPixel(0, this.text.getClippingArea().getY())[1];
		int pb = Utility.glToPixel(0, this.text.getClippingArea().getW())[1];
		int tw = pr-pl;
		int th = pb-pt;
		
		if(this.text.getPixelWidth() > tw)
		{
			if(this.xsb != null) 
			{
				this.xsb.setMaxValue(this.text.getPixelWidth()-tw);
				this.xscroll = true;
			}				
		}
		else
		{
			this.xscroll = false;
			if(this.xsb != null) { this.xsb.setScrollValue(0); this.xsb.setMaxValue(0);}
		}
		
		if(this.text.getPixelHeight() > th)
		{
			if(this.ysb != null) 
			{
				this.ysb.setMaxValue(this.text.getPixelHeight()-th);
				this.yscroll = true;
			}				
		}
		else
		{
			this.yscroll = false;
			if(this.ysb != null) { this.ysb.setScrollValue(0); this.ysb.setMaxValue(0);}
		}
		
		Logger.debug("xscroll: "+this.xscroll+" / yscroll: "+this.yscroll,"Component.setScroll");
		
		if(this.xsb != null) this.xsb.setDoublescroll(this.xscroll && this.yscroll);
		if(this.ysb != null) this.ysb.setDoublescroll(this.xscroll && this.yscroll);
		
		this.setTextClipping(true);
	}
	
	
	private void setTextClipping()
	{
		this.setTextClipping(false);
	}
	
	private void setTextClipping(boolean calledByScroll)
	{
		if(this.text != null)
		{
			
			float[] pad = Utility.pixelSizeToGLSize(this.getTextPad(), this.getTextPad());
			
			Vertex4 ca = new Vertex4(this.getX()+pad[0],
					 this.getY()+pad[1],
					 (this.getX()+this.getWidth())-pad[0]-(yscroll?this.ysb.getWidth():0),
					 (this.getY()+this.getHeight())-pad[1]-(xscroll?this.xsb.getHeight():0));
			this.text.setClippingArea(ca);
			
			if(!calledByScroll) this.setScroll();
			
			
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
		
		
		if(!this.hasBackground && !this.hasBorder)
		{
			this.text.setPosition(this.getTextX(), this.getTextY());
			this.setTextClipping();
		}
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
		this.customBgC = true;
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
		this.customBgHC = true;
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
		this.customBgAC = true;
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
		this.customBorC = true;
		if(!this.active && !this.isSelected())
		{
			this.border.setColor(borderColor);
			if(this.dsBEx != null)  this.dsBEx.setColor(borderColor);
		}
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
		this.customBorHC = true;
		if(!this.active && this.isSelected())
		{
			this.border.setColor(borderHighlightColor);
			if(this.dsBEx != null)  this.dsBEx.setColor(borderHighlightColor);
		}
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
		this.customBorAC = true;
		if(this.active)
		{
			this.border.setColor(borderActiveColor);
			if(this.dsBEx != null)  this.dsBEx.setColor(borderActiveColor);
		}
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
		this.customFontC = true;
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
		
		if(!this.hasBackground && !this.hasBorder)
		{
			this.text.setPosition(this.getTextX(), this.getTextY());
			this.setTextClipping();
		}
	}

	public void removeKeyListener(IKeyboardListener keyListener) {
		this.keyListener.remove(keyListener);
	}

	public void addKeyListener(IKeyboardListener keyListener) {
		this.keyListener.add(keyListener);
	}
	
	public void removeClickListener(IClickListener clickListener) {
		this.clickListener.remove(clickListener);
	}

	public void addClickListener(IClickListener clickListener) {
		this.clickListener.add(clickListener);
	}

	protected void dragged(int mouseGrabX, int mouseGrabY) {
		if(this.dragable) this.setPosition(this.getPixelX()-mouseGrabX, this.getPixelY()-mouseGrabY);
	}

	public boolean isActiveable() {
		return activeable;
	}

	public void setActiveable(boolean activeable) {
		this.activeable = activeable;
		if(!activeable)
			this.setActive(false);
	}
	
	/**
	 * @return the passwordMode
	 */
	public boolean isPasswordMode() {
		return this.text.isPasswordMode();
	}

	/**
	 * @param passwordMode the passwordMode to set
	 */
	public void setPasswordMode(boolean passwordMode) {
		this.text.setPasswordMode(passwordMode);
	}
	
	public char getPasswordChar() {
		return this.text.getPasswordChar();
	}

	public void setPasswordChar(char passwordChar) {
		this.text.setPasswordChar(passwordChar);
	}
	
	public int getTextX()
	{
		int pabs = this.getTextPad()+(this.icon!=null?this.icon.getPixelWidth()+Settings.get(SetKeys.GUI_CMP_FONT_PADDING, Integer.class):0);
		
		if(this.centerText)
		{
			int xoff = (this.getPixelWidth()-2*pabs-this.text.getPixelWidth()-this.addPadX)/2;
			return this.getPixelX()+Math.max(pabs, xoff);
		}
		else
		{
			return this.getPixelX() + pabs + this.addPadX;
		}
	}
	
	public int getTextY()
	{
		int pabs = this.getTextPad();
		
		if(this.centerText)
		{
			int yoff = (this.getPixelHeight()-2*pabs-this.text.getPixelHeight()-this.addPadY)/2;
			return this.getPixelY()+Math.max(pabs, yoff);
		}
		else
		{
			return this.getPixelY() + pabs + this.addPadY;
		}
	}
	
	/**
	 * @return the addPadX
	 */
	public int getAddPadX() {
		return addPadX;
	}

	/**
	 * @param addPadX the addPadX to set
	 */
	public void setAddPadX(int addPadX) {
		this.addPadX = addPadX;
		this.text.setPosition(this.getTextX(), this.getTextY());
	
	}

	/**
	 * @return the addPadY
	 */
	public int getAddPadY() {
		return addPadY;
	}

	/**
	 * @param addPadY the addPadY to set
	 */
	public void setAddPadY(int addPadY) {
		this.addPadY = addPadY;
		this.text.setPosition(this.getTextX(), this.getTextY());
	
	}

	protected int[] getCursorPos(int index)
	{
		return this.text.getCursorPos(index);
	}
	
	protected int getIndexByPosition(int x, int y)
	{
		Logger.debug("x: "+x+" / y: "+y+" / textX: "+this.getTextX()+" / textY: "+this.getTextY(),"Component.getIndexByPosition");
		return this.text.getIndexByPosition(this.getTextX()-x,this.getTextY()-y);
	}
	
	protected boolean isActive()
	{
		return this.active;
	}
	
	public boolean isScrollable()
	{
		return this.xscroll || this.yscroll;
	}

	public boolean isTextSelectable() {
		return textSelectable;
	}

	public void setTextSelectable(boolean textSelectable) {
		this.textSelectable = textSelectable;
	}
	
	public void setTextSelection(int start, int end)
	{
		this.text.setSelected(start,end);
	}
	
	public String getSelectedText()
	{
		return this.text.getSelectedText();
	}
	
	@Override
	public void onScroll(int sv, boolean horizontal)
	{
		if(horizontal && this.xscroll)
		{
			Logger.debug("scrollvalue: "+sv, "Component.onScroll");
			if(this.text != null) this.text.setX(this.getTextX()-sv);
		}
		else if(!this.xscroll && horizontal)
		{
			if(this.text != null && this.text.getPixelX() != this.getTextX()) this.text.setX(this.getTextX());
		}
		else if(!horizontal && this.yscroll)
		{
			Logger.debug("scrollvalue: "+sv, "Component.onScroll");
			if(this.text != null) this.text.setY(this.getTextY()-sv);
		}
		else if(!this.yscroll && !horizontal)
		{
			if(this.text != null && this.text.getPixelY() != this.getTextY()) this.text.setY(this.getTextY());
		}
	}
	
	protected XScrollBar getXsb()
	{
		return this.xsb;
	}
	
	protected YScrollBar getYsb()
	{
		return this.ysb;
	}
	
	protected FontObject getFO()
	{
		return this.text;
	}
	
	
	protected int getXPadding()
	{
		return Utility.glSizeToPixelSize(padding[0], 0)[0];
	}
	
	protected int getYPadding()
	{
		return Utility.glSizeToPixelSize(0, padding[1])[1];
	}
	
	public void mwheel(int value)
	{
		if(this.yscroll)
			this.ysb.setScrollValue(this.ysb.getScrollValue()-Math.max(Math.round((float)value*Settings.get(SetKeys.GUI_CMP_SCROLL_MWHEELFACTOR, Float.class)),1));
		else if(this.xscroll && Settings.get(SetKeys.GUI_CMP_SCROLL_XFALLBACK, Boolean.class))
			this.xsb.setScrollValue(this.xsb.getScrollValue()-Math.max(Math.round((float)value*Settings.get(SetKeys.GUI_CMP_SCROLL_MWHEELFACTOR, Float.class)),1));
	}

	public ToolTip getTooltip() {
		return tooltip;
	}

	public void setTooltip(String text) {
		this.tooltip.setText(text);
	}

	public void showToolTip(int x, int y) {
		
		if(this.tooltip != null && this.tooltip.getContent().length != 0 && !this.tooltipShown && !this.manualTTshow)
		{
			this.tooltip.setPosition(x,y);
			this.tooltipShown = true;
			this.getParent().setActiveToolTip(this.tooltip);
		}
	}
	
	public void manualShowToolTip(int x, int y) {
		
		if(this.tooltip != null && this.tooltip.getContent().length != 0 && !this.tooltipShown)
		{
			this.tooltip.setPosition(x,y);
			this.tooltipShown = true;
			this.getParent().setActiveToolTip(this.tooltip);
		}
	}
	
	public void hideToolTip()
	{
		if(!this.manualTThide)
		{
			this.getParent().setActiveToolTip(null);
			this.tooltipShown = false;
		}
	}
	
	public boolean isTooltipShown()
	{
		return this.tooltipShown;
	}
	
	public void manualHideToolTip()
	{
		this.getParent().setActiveToolTip(null);
		this.tooltipShown = false;
	}
	
	public void setManualTThide(boolean val)
	{
		this.manualTThide = val;
	}
	
	public void setManualTTshow(boolean val)
	{
		this.manualTTshow = val;
	}

	public Frame getParent() {
		return parent;
	}

	public void setParent(Frame parent) {
		this.parent = parent;
	}
	
	public void setScrollable(boolean value)
	{
		
		if(this.scrollable && !value)
		{
			this.yscroll = false;
			this.xscroll = false;
		}
		else if(!this.scrollable && value)
		{
			this.setScroll();
		}
		
		this.scrollable = value;
		
		
	}

	public boolean isCenterText() {
		return centerText;
	}

	public void setCenterText(boolean centerText) {
		this.centerText = centerText;
	}
	
	public void setAdditionalPadding(int addPadX, int addPadY)
	{
		this.addPadX = addPadX;
		this.addPadY = addPadY;
		this.text.setPosition(this.getTextX(), this.getTextY());
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		
		this.setColor();
	}

	public GLColor getBackgroundDisabledColor() {
		return backgroundDisabledColor;
	}

	public void setBackgroundDisabledColor(GLColor backgroundDisabledColor) {
		this.backgroundDisabledColor = backgroundDisabledColor;
		this.customBgDC = true;
		
		if(!this.enabled)
			this.setColor(backgroundDisabledColor);
	}
	
	public void resetBackgroundDisabledColor() {
		this.setBackgroundDisabledColor(Settings.get(SetKeys.GUI_CMP_BACKGROUND_D_COLOR, GLColor.class));
		this.customBgDC = false;
	}

	public GLColor getBorderDisabledColor() {
		return borderDisabledColor;
	}

	public void setBorderDisabledColor(GLColor borderDisabledColor) {
		this.borderDisabledColor = borderDisabledColor;
		this.customBorDC = true;
		
		if(!this.enabled)
			this.border.setColor(borderDisabledColor);
	}
	
	public void resetBorderDisabledColor() {
		this.setBorderDisabledColor(Settings.get(SetKeys.GUI_CMP_BORDER_D_COLOR, GLColor.class));
		
		this.customBorDC = false;
	}

	public GLColor getFontDisabledColor() {
		return fontDisabledColor;
	}

	public void setFontDisabledColor(GLColor fontDisabledColor) {
		this.fontDisabledColor = fontDisabledColor;
		this.customFontDC = true;
		
		if(!this.enabled)
			this.text.setColor(fontDisabledColor);
	}
	
	public void resetFontDisabledColor() {
		this.setFontDisabledColor(Settings.get(SetKeys.GUI_CMP_FONT_D_COLOR, GLColor.class));
		this.customFontDC = false;
	}

	public GLImage getIcon() {
		return icon;
	}
	
	private void createIcon(GLTexture tex)
	{
		int fs = Utility.glSizeToPixelSize(0,  this.text.getFontSize())[1];
		//Logger.spam("G E N E R A T I N G -------------------------------------------------------------------------------------------------------");
		this.icon = new GLImage(tex, this.getTextX(), this.getTextY(), fs, fs);
		
		this.text.setPosition(this.getTextX(), this.getTextY());
		this.icon.setSelectable(false);
		this.icon.setColor(Colors.WHITE.getGlColor());
		this.icon.setShader(DefaultShader.image);
	}
	
	public void setIcon(String icon) {
		if(this.icon != null)
			this.icon.setImage(new GLTexture(icon));
		else
			createIcon(new GLTexture(icon));
	}
	
	public void setIcon(GLTexture icon) {
		if(this.icon != null)
			this.icon.setImage(icon);
		else
			createIcon(icon);
	}

	public void setIcon(GLImage icon) {
		this.icon = icon;
		
		this.text.setPosition(this.getTextX(), this.getTextY());
	}
}

package cdg.nut.util.gl;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import cdg.nut.interfaces.IPolygonGenerator;
import cdg.nut.logging.ConsoleColor;
import cdg.nut.logging.Logger;
import cdg.nut.util.BitmapFont;
import cdg.nut.util.DefaultShader;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex2;
import cdg.nut.util.Vertex4;
import cdg.nut.util.VertexData;
import cdg.nut.util.enums.Colors;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class GLFont extends GLPolygon implements IPolygonGenerator{

	private BitmapFont font = Settings.get(SetKeys.GUI_CMP_FONT, BitmapFont.class);
	private GLColor color = Settings.get(SetKeys.GUI_CMP_FONT_COLOR, GLColor.class);
	private String colorText = "";
	private String actualText = "";
	private float fontSize;
	private float scalingConst;
	
	private GLPolygon selectionArea;
	
	private char passwordChar = '*';
	private boolean passwordMode = false;

	private int selectionStart = 0;
	private int selectionEnd = 0;
	private int aqc;
	
	private float alphaTestValue = 0.25f;
	private boolean alphaTest = true;
	private boolean autoATV = true;
	
	private boolean initialised = false;
	
	/**
	 * @return the passwordMode
	 */
	public boolean isPasswordMode() {
		return passwordMode;
	}

	/**
	 * @param passwordMode the passwordMode to set
	 */
	public void setPasswordMode(boolean passwordMode) {
		this.passwordMode = passwordMode;
		this.setDimension(this.getWidth(), this.getHeight());
	}

	public BitmapFont getFont()
	{
		return font;
	}

	public void setFont(BitmapFont font)
	{
		this.font = font;
	}

	public GLColor getColor()
	{
		return color;
	}

	public void setColor(GLColor color)
	{
		this.color = color;
		this.setDimension(this.getWidth(), this.getHeight());
	}

	public String getText()
	{
		return this.actualText;
	}

	public void setText(String text)
	{
		if(text == null)
		{
			this.setHidden(true);
			return;
		}
		else
		{
			if(!this.colorText.equals(text))
			{
				this.setHidden(false);
				this.colorText = text;
				this.setDimension(this.getWidth(), this.getHeight());
			}
		}
		
		
	}

	public GLFont(float x, float y, String text)
	{
		super();
		this.colorText = text;
		this.setFontSize(Settings.get(SetKeys.GUI_CMP_FONT_SIZE, Float.class));
		this.load(x, y, 0, 0, this);
		this.setup();
	}

	public GLFont(int x, int y, String text)
	{
		super();
		this.colorText = text;
		this.setFontSize(Settings.get(SetKeys.GUI_CMP_FONT_SIZE, Float.class));
		this.load(x, y, 0, 0, this);
		this.setup();
	}

	private void setup()
	{
		this.setShader(DefaultShader.text);
		this.selectionArea = new GLPolygon(0, 0, 0, 0, new IPolygonGenerator(){
			int aqc = 0;
			
			@Override
			public VertexData[] generateData(float x, float y, float width,
					float height) {
				float xoff = getX();
				float yoff = getY();
				List<VertexData> ret = new LinkedList<VertexData>();
				scalingConst = fontSize / font.getHeight("A");
				aqc = 0;
				
				for(int i = 0; i < selectionEnd; i++)
				{
					if(i+1 > actualText.length())
						break;
					String c = actualText.substring(i,i+1);
					
					if(passwordMode) c = ""+passwordChar;
					
					if(c.equals("\n"))
					{
						xoff = getX();
						yoff += fontSize;
					}
					else
					{
						float w =
							(
								scalingConst
							) *
							font.getWidth(c) *
							(
								1 /
								Settings.get(SetKeys.WIN_ASPECT_RATIO, Float.class)
							) *
							-1.0f;
						
						Vertex4[] qp = Utility.generateQuadPoints(xoff, yoff, w, fontSize);
						Vertex2[] st = Utility.generateSTPoints(
							font.getX(c),
							font.getY(c),
							font.getWidth(c),
							font.getHeight(c)
						);
						/*
						Utility.generateSTPointsFlipped(
							this.font.getX(c),
							this.font.getY(c),
							this.font.getWidth(c),
							this.font.getHeight(c)
						);
						*/
						VertexData[] data = Utility.generateQuadData(qp, Colors.NAVY.getGlColor(), st);
						
						if(i >= selectionStart)
						{
							for (int d = 0; d < data.length; d++) {
								ret.add(data[d]);
							}
				
							aqc++;
						}
						xoff += w;
					}
				}
				

				if (ret.size() >= 0) {
					//HACK for half cutted letter (dunno how to fix)
					Vertex4[] qp = Utility.generateQuadPoints(0, 0, 0, 0);
					Vertex2[] st = Utility.generateSTPoints(0, 0, 0, 0);
					VertexData[] data = Utility.generateQuadData(qp, Colors.NAVY.getGlColor(), st);

					for (int d = 0; d < data.length; d++) {
						ret.add(data[d]);
					}
					//END HACK

					
				}
				return ret.size() > 0?ret.toArray(new VertexData[1]):new VertexData[]{};
			}

			@Override
			public int[] generateIndicies() {
				return Utility.createQuadIndicesInt(this.aqc);
			}});
		this.setClipping(true);
		this.selectionArea.setClipping(true); //TODO: use text clipping
		this.selectionArea.setAutoClipping(false);
		this.setSelectable(false);
		this.selectionArea.setColor(Colors.NAVY.getGlColor());
		this.setAutoClipping(true);
		
		this.initialised = true;
	}

	public float getFontSize()
	{
		return this.fontSize;
	}

	public void setFontSize(float size)
	{
		this.fontSize = size;
		int fpxs = Utility.glSizeToPixelSize(0, size)[1];
		Logger.debug("fpxs: "+fpxs);
		if(this.autoATV)
		{
			this.alphaTestValue = 1.0f/20.0f * fpxs * 0.25f;
			if(fpxs > 30)
				this.alphaTest = false;
		}
		
		//this.setDimension(this.getWidth(), this.getHeight());
		if(this.initialised ) this.regen();
	}

	public void setFontSize(int size)
	{
		//Logger.debug("setting fs to: "+size);
		this.setFontSize(Utility.pixelSizeToGLSize(0, size)[1]);
		//Logger.debug(this.actualText+" --------------------->: "+this.getPixelHeight()+" / "+this.getFontSize());
	}

	@Override
	protected void bindTextures()
	{
		this.font.getFontTex().bind();
		//GL11.glShadeModel(GL11.GL_FLAT);
	}

	@Override
	protected void unbindTextures()
	{
		this.font.getFontTex().unbind();
		//GL11.glShadeModel(GL11.GL_SMOOTH);
	}
	
	@Override 
	protected void draw(boolean selection)
	{
		if(this.selectionArea != null){ this.selectionArea.draw();}
		super.draw(selection);
		//Logger.debug("drawing: "+this.actualText+" / clippingarea: "+this.getClippingArea().toString());
	}

	@Override
	protected void passUniforms()
	{
		this.getShader().pass1f("alphaTestValue", this.alphaTestValue);
		this.getShader().pass1i("alphaTest", this.alphaTest ? 1 : 0);
	}

	public char getPasswordChar() {
		return passwordChar;
	}

	public void setPasswordChar(char passwordChar) {
		this.passwordChar = passwordChar;
		//this.setDimension(this.getWidth(), this.getHeight());
		this.regen();
	}

	public int[] getCursorPos(int index) {
		index = Math.min(this.actualText.length(), index);
		int[] dim = this.passwordMode? this.font.getCursorPixelPos(this.colorText.substring(0, index), this.fontSize, this.passwordChar, index) : this.font.getCursorPixelPos(this.colorText.substring(0, index), this.fontSize, index);
		return dim;//new int[]{dim[0]+this.getPixelX(), dim[1]+this.getPixelY()};
	}

	public int getIndexByPosition(int x, int y) {
		return this.passwordMode?this.font.getIndexByPosition(x, y, this.colorText, this.fontSize, this.passwordChar):this.font.getIndexByPosition(x, y, this.colorText, this.fontSize);
	}

	public void setSelected(int start, int end) {
		this.selectionStart = Math.min(start,end);
		this.selectionEnd = Math.max(start,end);
		
		this.selectionArea.setDimension(0, 0);
	}
	public String getSelectedText()
	{
		return this.actualText.substring(this.selectionStart, this.selectionEnd);
	}
	
	@Override
	protected void move(float x, float y)
	{
		super.move(x, y);
		this.selectionArea.move(this.getX(), this.getY());
		//Logger.debug("################################################################### px: "+this.getPixelX()+" / py: "+this.getPixelY());
	}

	@Override
	public VertexData[] generateData(float x, float y, float width, float height) {
		if(this.colorText == null)
		{
			this.setHidden(true);
		}
		
		List<VertexData> ret = new LinkedList<VertexData>();
		String finText = this.colorText;
		this.scalingConst = this.fontSize / this.font.getHeight("A");

		if (this.colorText != "") {
			int aqc = 0;

			float xoff = this.getX();
			float yoff = this.getY();
			
			//Logger.debug("xoff: "+xoff+" / yoff: "+yoff,"GLFont.setupFontGL");

			GLColor currentColor = this.color;

			float trueoff = 0.0f;
			float xoffmax = 0.0f;

			for (int i = 0; i < this.colorText.length(); i++) {
				String c = colorText.substring(i, i+1);

				if (c.equals("\u001B")) { //color codes...
					String aco = "";

					try {
						aco = colorText.substring(i, i+5);
					} catch(Exception e) {}

					if (aco.endsWith("m")) {
						for (ConsoleColor cc: ConsoleColor.values()) {
							if (cc.getAnsiColor().equals(aco)) {
								currentColor = cc.getColor();
								i+=4;
								finText = finText.replaceFirst(Pattern.quote(aco), "");
								break;
							}
						}
					} else {
						finText = finText.replaceFirst(Pattern.quote(ConsoleColor.RESET.getAnsiColor().toString()), "");
						currentColor = this.color;
						i+=3;
					}
				} else if (c.equals(GLColor.TEXT_COLOR_IDENTIFIER)) { // gl color
					String aco = "";

					try {
						aco = colorText.substring(i, i+5);
						currentColor = new GLColor(
							(int)aco.charAt(1),
							(int)aco.charAt(2),
							(int)aco.charAt(3),
							(int)aco.charAt(4)
						);
						i+=4;
						finText = finText.replaceFirst(aco, "");
					} catch(Exception e) {}
				} else if (c.equals(GLColor.TEXT_COLOR_RESET)) { // back to default
					currentColor = this.color;
					finText = finText.replaceFirst(GLColor.TEXT_COLOR_RESET, "");
				} else if (c.equals("\n")) { // new line
					xoff = this.getX();
					yoff += this.fontSize;
					trueoff = 0.0f;
				} else { // we actually have something that looks like text
					
					if(this.passwordMode) c = ""+this.passwordChar;
					float w = this.scalingConst * this.font.getWidth(c) * (1.0f / Settings.get(SetKeys.WIN_ASPECT_RATIO, Float.class)) * -1.0f;
					
					Vertex4[] qp = Utility.generateQuadPoints(xoff, yoff, w, this.fontSize);
					Vertex2[] st = Utility.generateSTPoints(
						this.font.getX(c),
						this.font.getY(c),
						this.font.getWidth(c),
						this.font.getHeight(c)
					);
					
					VertexData[] data = Utility.generateQuadData(qp, currentColor, st);

					for (int d = 0; d < data.length; d++) {
						ret.add(data[d]);
					}
					
					//Logger.debug("aqc: "+aqc+" / w: "+w+" / sc: "+this.scalingConst+" / fcw: "+this.font.getWidth(c),"GLPolygon.setHeightSupEvent");

					aqc++;
					xoff += w;
					trueoff += w;

					if (trueoff > xoffmax) {
						xoffmax = trueoff;
					}
				}
			}

			if (ret.size() >= 0) {
				//HACK for half cutted letter (dunno how to fix)
				Vertex4[] qp = Utility.generateQuadPoints(0, 0, 0, 0);
				Vertex2[] st = Utility.generateSTPoints(0, 0, 0, 0);
				VertexData[] data = Utility.generateQuadData(qp, currentColor, st);

				for (int d = 0; d < data.length; d++) {
					ret.add(data[d]);
				}
				//END HACK

				this.setHidden(false);
				//this.setupGL(ret.toArray(new VertexData[1]), Utility.createQuadIndicesInt(aqc));
				//Logger.spam(
				//		(
				//			"ydim: " +
				//			(yoff + this.fontSize - this.getY()) +
				//			"/ xdim: " +
				//			xoffmax
				//		),
				//		"GLFont.setupFontGL"
				//	);
				this.setHeightSupEvent(yoff + this.fontSize - this.getY());
				this.setWidthSupEvent(xoffmax);
				this.aqc = aqc;
				//Logger.spam(
				//	(
				//		"Text dimension: " +
				//		this.getWidth() +
				//		"/" +
				//		this.getHeight()
				//	),
				//	"GLFont.setupFontGL"
				//);
			}
		} else {
			this.setHidden(true);
		}

		this.actualText = finText;
		
		return ret.size() > 0?ret.toArray(new VertexData[1]):new VertexData[]{};
	}

	@Override
	public int[] generateIndicies() {
		return Utility.createQuadIndicesInt(this.aqc);
	}
	
	public String getColortext()
	{
		return this.colorText;
	}
	
	@Override 
	public void onClippingChange(Vertex4 c)
	{
		if(this.selectionArea != null) this.selectionArea.setClippingArea(c);
	}

	public float getAlphaTestValue() {
		return alphaTestValue;
	}

	public void setAlphaTestValue(float alphaTestValue) {
		this.alphaTestValue = alphaTestValue;
	}

	public boolean isAutoATV() {
		return autoATV;
	}

	public void setAutoATV(boolean autoATV) {
		this.autoATV = autoATV;
	}

	public boolean isAlphaTest() {
		return alphaTest;
	}

	public void setAlphaTest(boolean alphaTest) {
		this.alphaTest = alphaTest;
	}
}

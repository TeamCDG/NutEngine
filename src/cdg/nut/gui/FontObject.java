package cdg.nut.gui;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cdg.nut.util.Colors;
import cdg.nut.util.DefaultShader;
import cdg.nut.util.Vertex2;
import cdg.nut.util.Vertex4;
import cdg.nut.util.VertexData;
import cdg.nut.util.gl.GLObject;
import cdg.nut.util.gl.GLImage;
import cdg.nut.util.gl.GLColor;
import cdg.nut.logging.ConsoleColor;
import cdg.nut.logging.Logger;
import cdg.nut.util.BitmapFont;
import cdg.nut.util.settings.Settings;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.Utility;

public class FontObject extends GLObject {

	private BitmapFont font = Settings.get(SetKeys.GUI_CMP_FONT, BitmapFont.class);
	private GLColor color = Settings.get(SetKeys.GUI_CMP_FONT_COLOR, GLColor.class);
	private String colorText;
	private String actualText;
	private float fontSize;
	private float scalingConst;
	
	private GLImage selectionArea;
	
	private char passwordChar = '*';
	private boolean passwordMode = false;

	private int selectionStart = 0;
	private int selectionEnd = 0;
	
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
		this.setupFontGL();
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
		this.setupFontGL();
	}

	public String getText()
	{
		return this.actualText;
	}

	public void setText(String text)
	{
		this.colorText = text;
		this.setupFontGL();
	}

	public FontObject(float x, float y, String text)
	{
		super();
		this.setPosition(x, y);
		this.fontSize = Settings.get(SetKeys.GUI_CMP_FONT_SIZE, Float.class);
		this.setText(text);
		this.setShader(DefaultShader.text);
		this.selectionArea = new GLImage(Colors.NAVY.getGlColor(), 0, 0, 0, 0);
		this.setClipping(true);
		this.selectionArea.setClipping(true);
		//this.setSelectable(false);
	}

	public FontObject(int x, int y, String text)
	{
		super();
		this.setPosition(x, y);
		this.fontSize = Settings.get(SetKeys.GUI_CMP_FONT_SIZE, Float.class);
		this.setText(text);
		this.setShader(DefaultShader.text);
		this.selectionArea = new GLImage(Colors.NAVY.getGlColor(), 0, 0, 0, 0);
		this.setClipping(true);
		this.selectionArea.setClipping(true);
		//this.setSelectable(false);
	}

	private void setupFontGL()
	{
		List<VertexData> ret = new LinkedList<VertexData>();
		String finText = this.colorText;
		this.scalingConst = this.fontSize / this.font.getHeight("A");

		if (this.colorText != "") {
			int aqc = 0;

			float xoff = this.getX();
			float yoff = this.getY();

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
								finText = finText.replaceFirst(aco, "");
								break;
							}
						}
					} else {
						finText = finText.replaceFirst(ConsoleColor.RESET.getAnsiColor(), "");
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
					float w =
						(
							this.scalingConst
						) *
						this.font.getWidth(c) *
						(
							1 /
							Settings.get(SetKeys.WIN_ASPECT_RATIO, Float.class)
						) *
						-1.0f;
					
					Vertex4[] qp = Utility.generateQuadPoints(xoff, yoff, w, this.fontSize);
					Vertex2[] st = Utility.generateSTPoints(
						this.font.getX(c),
						this.font.getY(c),
						this.font.getWidth(c),
						this.font.getHeight(c)
					);
					/*
					Utility.generateSTPointsFlipped(
						this.font.getX(c),
						this.font.getY(c),
						this.font.getWidth(c),
						this.font.getHeight(c)
					);
					*/
					VertexData[] data = Utility.generateQuadData(qp, currentColor, st);

					for (int d = 0; d < data.length; d++) {
						ret.add(data[d]);
					}

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

				this.setNoDraw(false);
				this.setupGL(ret.toArray(new VertexData[1]), Utility.createQuadIndicesInt(aqc));
				this.setHeightSupEvent(yoff + this.fontSize - this.getY());
				this.setWidthSupEvent(xoffmax);

				Logger.spam(
					(
						"Text dimension: " +
						this.getWidth() +
						"/" +
						this.getHeight()
					),
					"FontObject.setupFontGL"
				);
			}
		} else {
			this.setNoDraw(true);
		}

		this.actualText = finText;
		//return ret.toArray(new VertexData[1]);
	}

	public float getFontSize()
	{
		return this.fontSize;
	}

	public void setFontSize(float size)
	{
		this.fontSize = size;
		this.setupFontGL();
	}

	public void setFontSize(int size)
	{
		this.setFontSize(Utility.pixelSizeToGLSize(0, size)[1]);
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
		if(this.selectionArea != null) this.selectionArea.draw();		
		super.draw(selection);
	}

	@Override
	protected void passUniforms()
	{

	}

	public char getPasswordChar() {
		return passwordChar;
	}

	public void setPasswordChar(char passwordChar) {
		this.passwordChar = passwordChar;
		this.setupFontGL();
	}

	public int[] getCursorPos(int index) {
		int[] dim = this.passwordMode? this.font.getCursorPixelPos(this.colorText.substring(0, index), this.fontSize, this.passwordChar, index) : this.font.getCursorPixelPos(this.colorText.substring(0, index), this.fontSize, index);
		return dim;
	}

	public int getIndexByPosition(int x, int y) {
		return this.passwordMode?this.font.getIndexByPosition(x, y, this.colorText, this.fontSize, this.passwordChar):this.font.getIndexByPosition(x, y, this.colorText, this.fontSize);
	}

	public void setSelected(int start, int end) {
		this.selectionStart = Math.min(start,end);
		this.selectionEnd = Math.max(start,end);
		
		this.setupSelectionGL();
	}
	
	private void setupSelectionGL() {
		
		float xoff = this.getX();
		float yoff = this.getY();
		List<VertexData> ret = new LinkedList<VertexData>();
		this.scalingConst = this.fontSize / this.font.getHeight("A");
		int aqc = 0;
		
		for(int i = 0; i < this.selectionEnd; i++)
		{
			
			String c = this.actualText.substring(i,i+1);
			
			if(this.passwordMode) c = ""+this.passwordChar;
			
			if(c.equals("\n"))
			{
				xoff = this.getX();
				yoff += this.fontSize;
			}
			else
			{
				float w =
					(
						this.scalingConst
					) *
					this.font.getWidth(c) *
					(
						1 /
						Settings.get(SetKeys.WIN_ASPECT_RATIO, Float.class)
					) *
					-1.0f;
				
				Vertex4[] qp = Utility.generateQuadPoints(xoff, yoff, w, this.fontSize);
				Vertex2[] st = Utility.generateSTPoints(
					this.font.getX(c),
					this.font.getY(c),
					this.font.getWidth(c),
					this.font.getHeight(c)
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
				
				if(i >= this.selectionStart)
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

			this.selectionArea.setupGL(ret.toArray(new VertexData[1]), Utility.createQuadIndicesInt(aqc));
			this.selectionArea.setClippingArea(this.getClippingArea());
			
			
			Logger.spam(
				(
					"Text dimension: " +
					this.getWidth() +
					"/" +
					this.getHeight()
				),
				"FontObject.setupFontGL"
			);
		}
	}

	public String getSelectedText()
	{
		return this.actualText.substring(this.selectionStart, this.selectionEnd);
	}
}

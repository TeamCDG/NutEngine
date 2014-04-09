package cdg.nut.gui;

import java.util.LinkedList;
import java.util.List;

import cdg.nut.util.DefaultShader;
import cdg.nut.util.Vertex2;
import cdg.nut.util.Vertex4;
import cdg.nut.util.VertexData;
import cdg.nut.util.gl.GLObject;
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
		//this.setSelectable(false);
	}

	public FontObject(int x, int y, String text)
	{
		super();
		this.setPosition(x, y);
		this.fontSize = Settings.get(SetKeys.GUI_CMP_FONT_SIZE, Float.class);
		this.setText(text);
		this.setShader(DefaultShader.text);
		//this.setSelectable(false);
	}

	private void setupFontGL()
	{
		List<VertexData> ret = new LinkedList<VertexData>();
		String finText = this.colorText;

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
					float w =
						(
							this.fontSize / this.
							font.getHeight(c)
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
	}

	@Override
	protected void unbindTextures()
	{
		this.font.getFontTex().unbind();
	}

	@Override
	protected void passUniforms()
	{

	}
}

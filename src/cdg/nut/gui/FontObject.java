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
	private String text;

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
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
		this.setupFontGL();
	}

	public FontObject(float x, float y, String text)
	{
		super();
		this.setPosition(x, y);
		this.font.setHeight(Settings.get(SetKeys.GUI_CMP_FONT_SIZE, Integer.class));
		this.setText(text);
		this.setShader(DefaultShader.text);
		//this.setSelectable(false);
	}

	private void setupFontGL()
	{
		List<VertexData> ret = new LinkedList<VertexData>();

		if (this.text != "") {
			int aqc = 0;
			float xoff = this.getX();
			float yoff = this.getY();
			GLColor currentColor = this.color;
			float xoffmax = 0.0f;

			for (int i = 0; i < this.text.length(); i++) {
				String c = text.substring(i, i+1);

				if (c.equals("\u001B")) { //color codes...
					String aco = "";

					try {
						aco = text.substring(i, i+5);
					} catch (Exception e) {}

					if (aco.endsWith("m")) {

						for (ConsoleColor cc: ConsoleColor.values()) {
							if (cc.getAnsiColor().equals(aco)) {
								currentColor = cc.getColor();
								i+=4;
								break;
							}
						}

					} else {
						currentColor = this.color;
						i+=3;
					}

				} else if (c.equals("\n")) { // new line
					xoff = this.getX();
					yoff += this.font.getHeight();

				} else { // we actually have something that looks like text
					float w =
						(
						 this.font.getHeight() /
						 this.font.getHeight(c)
						) *
						this.font.getWidth(c) *
						(
						 1 /
						 Settings.get(SetKeys.WIN_ASPECT_RATIO, Float.class)
						) *
						-1.0f;
					Vertex4[] qp = Utility.generateQuadPoints(xoff, yoff, w, this.font.getHeight());
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

					aqc++;
					xoff += w;

					if (xoff > xoffmax) {
						xoffmax = xoff;
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
				this.setHeightSupEvent(yoff+this.font.getHeight());
				this.setWidthSupEvent(xoffmax);
			}

		} else {
			this.setNoDraw(true);
		}

		//return ret.toArray(new VertexData[1]);
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
}

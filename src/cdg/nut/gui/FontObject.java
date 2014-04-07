package cdg.nut.gui;

import java.util.LinkedList;
import java.util.List;

import cdg.nut.util.VertexData;
import cdg.nut.util.gl.GLObject;
import cdg.nut.util.BitmapFont;
import cdg.nut.util.settings.Settings;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.Utility;

public class FontObject extends GLObject{

	private BitmapFont f = Settings.get(SetKeys.GUI_CMP_FONT, BitmapFont.class);
	private int aqc;
	private String text;
	
	public FontObject(float x, float y, String text) {
		super();
		this.setPosition(x, y);
		this.text = text;
		this.setupGL(makeFontGL(), Utility.createQuadIndicesInt(aqc));
	}
	
	private VertexData[] makeFontGL()
	{
		List<VertexData> ret = new LinkedList<VertexData>();
		if(this.text != "")
		{
			
		}
		else
		{
			
		}
		return ret.toArray(new VertexData[1]);
	}
}

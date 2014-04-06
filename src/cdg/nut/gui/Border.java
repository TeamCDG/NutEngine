package cdg.nut.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cdg.nut.interfaces.IDrawable;
import cdg.nut.util.Utility;
import cdg.nut.util.VertexData;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLImage;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class Border extends GLImage {

	public Border(float x, float y, float width, float height)
	{
		super(Settings.get(SetKeys.GUI_CMP_BORDER_COLOR, GLColor.class), width, height, makeData(x,y,width,height));
		//super(GLColor.random(), width, height,  Utility.generateQuadData(x, y, width, height, GLColor.random()));
		//super(GLColor.random(), x, y ,width, height);
	}
	
	public Border(int x, int y, int width, int height)
	{
		this(Utility.pixelToGL(x, y)[0], Utility.pixelToGL(x, y)[1], Utility.pixelSizeToGLSize(width, height)[0], Utility.pixelSizeToGLSize(width, height)[1]);
	}
	
	private static VertexData[] makeData(float x, float y, float width, float height)
	{
		float sx = Utility.pixelSizeToGLSize(Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class),0)[0];
		float sy = Utility.pixelSizeToGLSize(0,Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class))[1];
		ArrayList<VertexData> res = new ArrayList<VertexData>(16);
		
		GLColor c = new GLColor(0,0,0,0);
		
		addAll(res, Utility.generateQuadData(x, y, width, sy, c)); //north
		addAll(res, Utility.generateQuadData(x+(width-sx), y, sx, height, c)); //east
		addAll(res, Utility.generateQuadData(x, y+(height-sy), width, sy, c)); //south
		addAll(res, Utility.generateQuadData(x, y, sx, height, c)); //west
		
		
		return res.toArray(new VertexData[1]);
	}
	
	private static List<VertexData> addAll(List<VertexData> l, VertexData[] a)
	{
		for(int i = 0; i < a.length; i++)
		{
			l.add(a[i]);
		}
		return l;
	}

}

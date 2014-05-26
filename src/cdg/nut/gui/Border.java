package cdg.nut.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cdg.nut.interfaces.IDrawable;
import cdg.nut.logging.Logger;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex2;
import cdg.nut.util.Vertex4;
import cdg.nut.util.VertexData;
import cdg.nut.util.enums.Colors;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLImage;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class Border extends GLImage {

	private int borderSize = -1;
	private boolean polygonal = false;
	private int pointCount = 4;
	
	public Border(float x, float y, float width, float height)
	{
		super(Settings.get(SetKeys.GUI_CMP_BORDER_COLOR, GLColor.class),
				width, height,
				makeData(x,y,width,height, Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class)));
		this.setSelectable(false);
	}
	
	public Border(float x, float y, float width, float height, int bs)
	{
		super(Settings.get(SetKeys.GUI_CMP_BORDER_COLOR, GLColor.class),
				width, height,
				makeData(x,y,width,height, bs));
		this.setSelectable(false);
		this.borderSize = bs;
	}
	
	/*
	public Border(float x, float y, float width, float height, int pointCount)
	{
		super(Settings.get(SetKeys.GUI_CMP_BORDER_COLOR, GLColor.class),
				width, height,
				makeDataPolygon(x,y,width,height, pointCount), createBorderIndices(pointCount));
		this.pointCount = pointCount;
		this.polygonal = true;
		this.setSelectable(false);
	}
	
	public Border(int x, int y, int width, int height, int pointCount)
	{
		
		super(Settings.get(SetKeys.GUI_CMP_BORDER_COLOR, GLColor.class),
				width, height,
				makeDataPolygon(x,y,width,height, pointCount), new byte[]{0,1,2});//createBorderIndices(pointCount));
		
		super(Settings.get(SetKeys.GUI_CMP_BORDER_COLOR, GLColor.class), 222222220, 22222220);
		
		this.setupGL(makeDataPolygon(x,y,width,height, pointCount), createBorderIndices(pointCount));
		
		this.setClipping(false);
		
		this(Utility.pixelToGL(x, y)[0],
				Utility.pixelToGL(x, y)[1],
				Utility.pixelSizeToGLSize(width, height)[0],
				Utility.pixelSizeToGLSize(width, height)[1], pointCount);
	}*/

	private static int[] createBorderIndices(int pointCount) {
		int[] in = new int[pointCount*6];
		for(int i = 0; i < 2; i++)
		{
			in[i+0] = i*6 + 0;
			in[i+1] = i*6 + 1;
			in[i+2] = i*6 + 2;
			in[i+3] = i*6 + 2;
			in[i+4] = i*6 + 3;
			in[i+5] = i*6 + 0;
		}
		return in;
	}

	public Border(int x, int y, int width, int height)
	{
		this(Utility.pixelToGL(x, y)[0],
			Utility.pixelToGL(x, y)[1],
			Utility.pixelSizeToGLSize(width, height)[0],
			Utility.pixelSizeToGLSize(width, height)[1]);
		this.setSelectable(false);
	}

	private static VertexData[] makeDataPolygon(float x, float y, float width, float height, int pointCount)
	{
		float radius = Math.min(width, height)/2.0f;
		float s = 4;//customBorderSize == -1?Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class):customBorderSize;
		float alpha = 360.0f/pointCount;
		Vertex2 center = new Vertex2(x+radius, y+radius);
		List<Vertex4> points = new ArrayList<Vertex4>((pointCount+1)*2);
		float deg = alpha/2.0f;
		
		for(int i = 0; i < 2/*pointCount+1*/; i++)
		{
			float px = center.getX()+(float) (Math.cos(Utility.rad(deg)) * radius);
			float py = center.getY()+(float) (Math.sin(Utility.rad(deg)) * radius);
			
			float plx = center.getX()+(float) (Math.cos(Utility.rad(deg)) * (radius-s));
			float ply = center.getY()+(float) (Math.sin(Utility.rad(deg)) * (radius-s));
			
			points.add(Utility.toGL(new Vertex4(px,py)));
			points.add(Utility.toGL(new Vertex4(plx,ply)));
			
			Logger.debug("P: "+i+" / c: "+pointCount+" / deg: "+deg+" / px: "+px+" / py: "+py+" / plx: "+plx+" / ply: "+ply+" / cx: "+center.getX()+" / cy: "+center.getY()+" / r: "+radius, "Border.makedataPolygon");
			
			deg+=alpha;
		}
		
		VertexData[] data = new VertexData[points.size()];
		for(int i = 0; i < points.size(); i++)
		{
			data[i] = new VertexData(points.get(i));
		}
		
		return data;
	}
	
	private static VertexData[] makeData(float x, float y, float width, float height, int bs)
	{
		float sx = Utility.pixelSizeToGLSize(bs, 0)[0];
		float sy = Utility.pixelSizeToGLSize(0, bs)[1];
		ArrayList<VertexData> res = new ArrayList<VertexData>(16);
		GLColor c = new GLColor(0,0,0,0);

		addAll(res, Utility.generateQuadData(x, y, width, sy, c)); //north
		addAll(res, Utility.generateQuadData(x+(width-sx), y+sy, sx, height-2*sy, c)); //east
		addAll(res, Utility.generateQuadData(x, y+(height-sy), width, sy, c)); //south
		addAll(res, Utility.generateQuadData(x, y+sy, sx, height-2*sy, c)); //west

		return res.toArray(new VertexData[1]);
	}

	@Override
	public void setDimension(float width, float height)
	{
		super.setDimension(width, height);
		super.setupGL(makeData(this.getX(),this.getY(),width,height, this.borderSize),
			Utility.createQuadIndicesByte(4));
	}

	private static List<VertexData> addAll(List<VertexData> l, VertexData[] a)
	{
		for (int i = 0; i < a.length; i++) {
			l.add(a[i]);
		}

		return l;
	}

	public void setBorderSize(int i) {
		this.borderSize = i;
		this.setupGL(makeData(this.getX(), this.getY(), this.getWidth(), this.getHeight(),  this.borderSize), Utility.createQuadIndicesByte(4));
		
	}
}

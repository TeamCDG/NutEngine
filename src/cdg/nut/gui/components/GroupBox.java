package cdg.nut.gui.components;

import java.util.ArrayList;
import java.util.List;

import cdg.nut.interfaces.IPolygonGenerator;
import cdg.nut.util.Utility;
import cdg.nut.util.VertexData;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLFont;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class GroupBox extends Panel implements IPolygonGenerator{

	private GLFont title;
	
	public GroupBox(float x, float y, float width, float height, String text) {
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
		//this.text = text;
		this.setup();
	}
	
	public GroupBox(int x, int y, int width, int height, String text) {
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
		//this.text = text;
		this.setup();
	}
	
	public GroupBox(float x, float y, float width, float height) {
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
		this.setup();
	}
	
	public GroupBox(int x, int y, int width, int height) {
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
		this.setup();
	}
	
	@Override
	protected void drawChildren(boolean selection)
	{
		super.drawChildren(selection);
		
		this.title.draw();
		//this.border.draw();
	}
	
	@Override
	protected void setup()
	{
		this.setHasBackground(false);
		
		float tx = Utility.pixelToGL(this.getPixelX()+12,0)[0];
		
		
		
		this.title = new GLFont(tx, this.getY(),"GroupBox"); 
		this.title.setFontSize(20); //TODO: SETTING
		
		//this.generateBorder();
		
		this.getBorder().setGen(new IPolygonGenerator(){

			@Override
			public VertexData[] generateData(float x, float y, float width,
					float height) {
				float tx = Utility.pixelToGL(getPixelX()+12,0)[0];
				float px = Utility.pixelSizeToGLSize(4,0)[0];
				
				float sx = Utility.pixelSizeToGLSize(getBorderSize(),0)[0];
				float sy = Utility.pixelSizeToGLSize(0,getBorderSize())[1];
				
				ArrayList<VertexData> res = new ArrayList<VertexData>(20);
				GLColor c = new GLColor(0,0,0,0);
				
				addAll(res, Utility.generateQuadData(getX(), getY()+(title.getHeight()/2), (tx-getX())-px, sy, c)); //north pleft
				addAll(res, Utility.generateQuadData(tx+title.getWidth(), getY()+(title.getHeight()/2), getWidth()-title.getWidth()-(tx-getX()), sy, c)); //north pright
				addAll(res, Utility.generateQuadData(getX()+(getWidth()-sx), getY()+(title.getHeight()/2)+sy, sx, getHeight()-2*sy-(title.getHeight()/2), c)); //east
				addAll(res, Utility.generateQuadData(getX(), getY()+(getHeight()-sy), getWidth(), sy, c)); //south
				addAll(res, Utility.generateQuadData(getX(), getY()+(title.getHeight()/2)+sy, sx, getHeight()-2*sy-(title.getHeight()/2), c)); //west
				
				return res.toArray(new VertexData[1]);
			}

			@Override
			public int[] generateIndicies() {
				return Utility.createQuadIndicesInt(5);
			}});

		//return res.toArray(new VertexData[1]);
	}
	
	private static List<VertexData> addAll(List<VertexData> l, VertexData[] a)
	{
		for (int i = 0; i < a.length; i++) {
			l.add(a[i]);
		}

		return l;
	}

	@Override
	public VertexData[] generateData(float x, float y, float width, float height) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] generateIndicies() {
		// TODO Auto-generated method stub
		return null;
	}

}

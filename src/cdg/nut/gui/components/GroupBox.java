package cdg.nut.gui.components;

import java.util.ArrayList;
import java.util.List;

import cdg.nut.gui.FontObject;
import cdg.nut.util.Utility;
import cdg.nut.util.VertexData;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLImage;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class GroupBox extends Panel {

	private GLImage border;
	private FontObject title;
	private String text = "";
	
	public GroupBox(float x, float y, float width, float height, String text) {
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
		this.text = text;
		this.setup();
	}
	
	public GroupBox(int x, int y, int width, int height, String text) {
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
		this.text = text;
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
		this.border.draw();
	}
	
	@Override
	protected void setup()
	{
		
		this.setHasBackground(false);
		this.setHasBorder(false);
		
		float tx = Utility.pixelToGL(this.getPixelX()+12,0)[0];
		
		
		
		this.title = new FontObject(tx, this.getY(),"PEEEENIS"); 
		this.title.setFontSize(20); //TODO: SETTING
		
		this.generateBorder();

		//return res.toArray(new VertexData[1]);
	}
	
	private void generateBorder()
	{
		float tx = Utility.pixelToGL(this.getPixelX()+12,0)[0];
		float px = Utility.pixelSizeToGLSize(4,0)[0];
		
		float sx = Utility.pixelSizeToGLSize(this.getBorderSize(),0)[0];
		float sy = Utility.pixelSizeToGLSize(0,this.getBorderSize())[1];
		
		ArrayList<VertexData> res = new ArrayList<VertexData>(20);
		GLColor c = new GLColor(0,0,0,0);
		
		addAll(res, Utility.generateQuadData(this.getX(), this.getY()+(this.title.getHeight()/2), (tx-this.getX())-px, sy, c)); //north pleft
		addAll(res, Utility.generateQuadData(tx+this.title.getWidth(), this.getY()+(this.title.getHeight()/2), this.getWidth()-this.title.getWidth()-(tx-this.getX()), sy, c)); //north pright
		addAll(res, Utility.generateQuadData(this.getX()+(this.getWidth()-sx), this.getY()+(this.title.getHeight()/2)+sy, sx, this.getHeight()-2*sy-(this.title.getHeight()/2), c)); //east
		addAll(res, Utility.generateQuadData(this.getX(), this.getY()+(this.getHeight()-sy), this.getWidth(), sy, c)); //south
		addAll(res, Utility.generateQuadData(this.getX(), this.getY()+(this.title.getHeight()/2)+sy, sx, this.getHeight()-2*sy-(this.title.getHeight()/2), c)); //west

		this.border = new GLImage(GLColor.random(), this.getWidth(), this.getHeight(), res.toArray(new VertexData[1]), Utility.createQuadIndicesByte(5));
		
	}
	
	private static List<VertexData> addAll(List<VertexData> l, VertexData[] a)
	{
		for (int i = 0; i < a.length; i++) {
			l.add(a[i]);
		}

		return l;
	}

}

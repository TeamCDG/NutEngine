package cdg.nut.gui.components;

import java.util.ArrayList;
import java.util.List;

import cdg.nut.gui.Component;
import cdg.nut.interfaces.ICheckedChangedListener;
import cdg.nut.interfaces.IPolygonGenerator;
import cdg.nut.logging.Logger;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex2;
import cdg.nut.util.Vertex4;
import cdg.nut.util.VertexData;
import static cdg.nut.util.VertexUtility.*;
import cdg.nut.util.enums.MouseButtons;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLPolygon;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class CheckBox extends Component implements IPolygonGenerator{

	GLPolygon check;
	GLPolygon box;
	GLPolygon boxBorder;
	
	private List<ICheckedChangedListener> checkChangedListener = new ArrayList<ICheckedChangedListener>();
	
	private boolean checked = false;
	
	public CheckBox(int x, int y, int width, int height, String text) {
		super(x, y, width, height, text);
		this.setup();
	}
	
	public CheckBox(float x, float y, float width, float height, String text) {
		super(x, y, width, height, text);
		this.setup();
	}
	
	public CheckBox(int x, int y, String text) {
		super(x, y, text);
		this.setup();
	}
	
	public CheckBox(float x, float y, String text) {
		super(x, y, text);
		this.setup();
	}
	
	@Override
	public void setDimension(float width, float height) {
		super.setDimension(width, height);
		
		this.generateBox();
	}
	
	@Override
	public void setSelected(boolean b)
	{
		super.setSelected(b);
		
		//Logger.debug("value: "+b,"CheckBox.setSelected");
		
		if(b)
		{
			this.boxBorder.setColor(this.getBorderHighlightColor());
			this.box.setColor(this.getBackgroundHighlightColor());
		}
		else
		{
			this.boxBorder.setColor(this.getBorderColor());
			this.box.setColor(this.getBackgroundColor());
		}
	}
	
	@Override
	public void onClick(int x, int y, MouseButtons button, boolean grabbed, int grabx, int graby) {
		this.setChecked(!this.checked);
	}
	
	@Override
	protected void drawChildren(boolean selection)
	{

		super.drawChildren(selection);
		
		if(!selection) this.box.draw();
		if(!selection) this.boxBorder.draw();
		if(!selection && checked) this.check.draw();
	}
	
	private void setup()
	{
		this.setScrollable(false);
		this.setHasBackground(false);
		this.setHasBorder(false);
		
		
		this.generateBox();
	}
	
	private void generateBox()
	{
		int s = Math.min(this.getPixelHeight(), this.getFO().getPixelHeight());
				
		this.box = new GLPolygon(this.getPixelX(), this.getTextY(), s, s);
		this.box.setColor(Settings.get(SetKeys.GUI_CMP_BACKGROUND_COLOR, GLColor.class));
		this.check = new GLPolygon(this.getPixelX(), this.getTextY(), s, s, this);
		this.check.setColor(Settings.get(SetKeys.GUI_CHECKBOX_CHECK_COLOR, GLColor.class));
		this.boxBorder = new GLPolygon(this.getPixelX(), this.getTextY(), s, s,0,false, box.getPixelWidth()<=20?Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class)/2:Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class));
		
		
		this.setAdditionalPadding(this.box.getPixelWidth()+Settings.get(SetKeys.GUI_CMP_FONT_PADDING, Integer.class), 0);
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
		
		for(int i = 0; i < this.checkChangedListener.size(); i++)
		{
			this.checkChangedListener.get(0).onCheckedChange(this.checked); 
		}
	}

	public void addCheckChangedListener(ICheckedChangedListener checkChangedListener) {
		this.checkChangedListener.add(checkChangedListener);
	}

	public void removeCheckChangedListener(ICheckedChangedListener checkChangedListener) {
		this.checkChangedListener.remove(checkChangedListener);
	}

	@Override
	public VertexData[] generateData(float x, float y, float width, float height) {
		int checkSize = Math.round((1.0f/8.0f)*(float)this.box.getPixelWidth());
		int padding = this.box.getPixelWidth()<= 20?Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class)/2:Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class);
		if(padding != Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class)) this.boxBorder.setBordersize(padding);
		
		Vertex2 p_bot_mid = new Vertex2(this.box.getPixelX()+(this.box.getPixelWidth()/2), this.box.getPixelY()+this.box.getPixelHeight()-padding);
		Vertex2 p_left_mid = new Vertex2(this.box.getPixelX()+padding, this.box.getPixelY()+(this.box.getPixelHeight()/2));
		Vertex2 p_top_right = new Vertex2(this.box.getPixelX()+this.box.getPixelWidth()-padding, this.box.getPixelY()+checkSize+(checkSize/2));
		
		float alpha = angle(substract(p_bot_mid, p_left_mid), substract(p_bot_mid, p_top_right));
		float beta = 180.0f-alpha;
		
		float gamma = 254+angle(substract(p_bot_mid, p_top_right), new Vertex2(1.0f,0.0f));
		float delta = angle(substract(p_bot_mid, p_left_mid), new Vertex2(1.0f,0.0f))-16;
		
		Vertex2 p2 = new Vertex2(p_top_right.getX()-checkSize*(float)Math.cos(Utility.rad(gamma)), p_top_right.getY()+checkSize*(float)Math.sin(Utility.rad(gamma)));
		Vertex2 p1 = new Vertex2(p_left_mid.getX()+checkSize*(float)Math.cos(Utility.rad(delta)), p_left_mid.getY()-checkSize*(float)Math.sin(Utility.rad(delta)));
		
		Vertex2 px = intersectionPoint(p1, substract(p_bot_mid, p_left_mid), p2, substract(p_bot_mid, p_top_right)).toVertex2();
		Vertex2 p_fin = intersectionPoint(p1, substract(p_bot_mid, p_left_mid), p_bot_mid, substract(p_bot_mid, p_top_right)).toVertex2();
		
		Logger.debug("alpha: "+alpha+" / beta: "+beta+" / gamma: "+gamma,"CheckBox.generateCheck");
		Logger.debug("p_top_right: "+p_top_right+" / p2: "+p2,"CheckBox.generateCheck");
		Logger.debug("p_left_mid: "+p_left_mid+" / p1: "+p1,"CheckBox.generateCheck");
		Logger.debug("p_bot_mid: "+p_bot_mid+" / px: "+px+" / p_fin: "+p_fin,"CheckBox.generateCheck");
		
		Vertex4[] qpBig = new Vertex4[]{new Vertex4(Utility.toGL(px)), new Vertex4(Utility.toGL(p2)), new Vertex4(Utility.toGL(p_top_right)), new Vertex4(Utility.toGL(p_fin))};
		VertexData[] q1 = Utility.generateQuadData(qpBig, new GLColor(0.0f, 0.0f, 0.0f, 0.0f));
		
		Vertex4[] qpSmall = new Vertex4[]{new Vertex4(Utility.toGL(p_left_mid)), new Vertex4(Utility.toGL(p1)), new Vertex4(Utility.toGL(p_fin)), new Vertex4(Utility.toGL(p_bot_mid))};
		VertexData[] q2 = Utility.generateQuadData(qpSmall, new GLColor(0.0f, 0.0f, 0.0f, 0.0f));
		
		VertexData[] fin = new VertexData[]{q1[0],q1[1],q1[2],q1[3],
											q2[0],q2[1],q2[2],q2[3]};
		
		return fin;
	}

	@Override
	public int[] generateIndicies() {
		return Utility.createQuadIndicesInt(2);
	}


}

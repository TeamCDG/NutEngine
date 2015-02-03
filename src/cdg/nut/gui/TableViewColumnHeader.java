package cdg.nut.gui;

import cdg.nut.gui.components.Label;
import cdg.nut.interfaces.IParent;
import cdg.nut.logging.Logger;
import cdg.nut.util.Colors;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex4;
import cdg.nut.util.enums.MouseButtons;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLPolygon;

public class TableViewColumnHeader extends Label {

	private GLPolygon seperator;
	
	public TableViewColumnHeader(int x, int y, int width, String text) {
		super(x, y, text);
		if(width != -1) this.setWidth(width);
		this.init();
	}
	
	public TableViewColumnHeader(float x, float y, float width, String text) {
		super(x, y, text);
		if(width != -1) this.setWidth(width);
		this.init();
	}
	
	private void init()
	{
		this.setSelectable(true);
		this.setDragable(true);
		this.setBackgroundColor(GLColor.random());
		this.setHasBackground(true);
		
		this.seperator = new GLPolygon(this.getPixelX()+this.getPixelWidth(), this.getPixelY(), 2, this.getPixelHeight());
		this.seperator.setColor(Colors.WHITE.getGlColor());
		
		this.seperator.setClipping(true);
		this.seperator.setAutoClipping(false);
		this.seperator.setShader(this.getShader());

	}
	
	@Override
	protected void onClick(int x, int y, MouseButtons button, boolean grabbed, int grabx, int graby){
		
	}
	
	@Override
	public void setClippingArea(Vertex4 ca)
	{
		super.setClippingArea(ca);
		if(this.seperator != null) this.seperator.setClippingArea(ca);	
	}
	
	@Override
	public void setDimension(float w, float h)
	{
		super.setDimension(w,h);
		
		if(this.seperator != null) this.seperator.setHeight(this.getPixelHeight());
		if(this.seperator != null) this.seperator.setPosition(this.getPixelX()+this.getPixelWidth(), this.getPixelY());
		
//		this.setupClippingArea();
	}
	
	@Override
	public int setId(int id)
	{
		this.seperator.setId(id);
		return super.setId(id);
	}
	
	@Override
	public void move(float x, float y)
	{
		super.move(x,y);
		if(this.seperator != null) this.seperator.setPosition(this.getPixelX()+this.getPixelWidth(), this.getPixelY());
//		this.setupClippingArea();
		Logger.debug(this.getId()+" ----> moved to x: "+this.getPixelX());
		Logger.printStackTrace();
	}
	
	
	
	
	private boolean dragging = false;
	
	@Override
	public void dragged(int mouseGrabX, int mouseGrabY) {
		
		Logger.enable();
		
		if(mouseGrabX != 0)
		{
			if(this.getPixelWidth()-mouseGrabX > 5)
				this.setWidth(this.getPixelWidth()-mouseGrabX);				
			
			this.seperator.setX(this.getPixelX()+this.getPixelWidth());
			this.dragging = true;
		}
		
	}
	
	
	
	@Override
	public void unselected()
	{
		super.unselected();
		this.dragging = false;
	}
	
	@Override
	public void drawChildren(boolean selection)
	{
		
		if(selection)
			this.seperator.drawSelect();
		else
			this.seperator.draw();
		
		super.drawChildren(selection);
		
		
	}
	
	@Override
	public void setParent(IParent p)
	{
		super.setParent(p);
		
	
	}
}

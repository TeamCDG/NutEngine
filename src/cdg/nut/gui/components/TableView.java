package cdg.nut.gui.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.input.Mouse;

import cdg.nut.gui.Component;
import cdg.nut.gui.TableViewColumnHeader;
import cdg.nut.gui.TableViewItem;
import cdg.nut.interfaces.IDimensionChangedListener;
import cdg.nut.interfaces.IGuiObject;
import cdg.nut.interfaces.IParent;
import cdg.nut.interfaces.IScrollChangedListener;
import cdg.nut.interfaces.IScrollListener;
import cdg.nut.logging.Logger;
import cdg.nut.util.Utility;
import cdg.nut.util.enums.Colors;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLPolygon;
import cdg.nut.util.gl.GLTexture;
import cdg.nut.util.settings.SetKeys;

public class TableView extends Component implements IDimensionChangedListener, IScrollChangedListener{

	private Panel contentPane;	
	private Panel headPanel;
	private List<TableViewColumnHeader> columns;
	private GLPolygon headerBorder;
	private List<TableViewItem> items;
	private List<Panel> columnPanels;
	
	
	public TableView(int x, int y, int width, int height) {
		super(x, y, width, height);
		this.init();
		
	}
	
	public TableView(float x, float y, float width, float height) {
		super(x, y, width, height);
		this.init();
	}
	
	private void init()
	{
		this.setHasBorder(true);
		this.setBorderSize(2);
		
		this.headPanel = new Panel(this.getPixelX()+this.getBorderSize(), this.getPixelY()+this.getBorderSize(), this.getPixelWidth()-2*this.getBorderSize(), this.getPixelWidth()-2*+this.getBorderSize());

		this.headPanel.setAutoClipping(true);
		this.headPanel.setSelectable(true);
		this.headPanel.setHasBackground(true);
		this.headPanel.setDragable(false);
		this.headPanel.setManualScrollX(true);
		
		
		
		
		this.contentPane = new Panel(this.getPixelX()+this.getBorderSize(), this.getPixelY()+this.getBorderSize(), this.getPixelWidth()-2*this.getBorderSize(), this.getPixelWidth()-2*+this.getBorderSize());
		this.contentPane.setScrollable(true);
		
		this.columns = new ArrayList<TableViewColumnHeader>();
		this.items = new ArrayList<TableViewItem>();
		this.columnPanels = new ArrayList<Panel>();
		
//		this.contentPane.setHasBorder(true);
//		this.contentPane.setBorderSize(2);
		this.contentPane.setAutoClipping(true);
		this.contentPane.setSelectable(true);
		this.contentPane.setHasBackground(true);
		this.contentPane.setBackgroundColor(GLColor.random());
		this.contentPane.addScrollChangedListener(this);
		this.contentPane.setDragable(false);
//		this.contentPane.getXsb().addScrollListener(this.headPanel);
		

		
		this.setSelectable(true);
		this.setHasBackground(false);
		//this.setHasBorder(false);
		
		this.headerBorder = new GLPolygon(this.getPixelX()+this.getBorderSize(), this.getPixelY()+20, this.getPixelWidth()-(2*this.getBorderSize()), 2);
		
		this.setBorderSize(2);
		//this.setAddPadX(-2);
		
	}
	
	private int getNewColumnPosX() {
		int xoff = 0;
		
		for(int i = 0; i < this.columns.size(); i++)
		{
			xoff += this.columns.get(i).getPixelWidth() + 2;
		}
		
		return xoff;
	}
	
	public void addColumn(String head)
	{
		TableViewColumnHeader h = new TableViewColumnHeader(this.getNewColumnPosX(), 0, -1, head);
		h.setFontSize(18);
		h.addDimensionChangedListener(this);
		columns.add(h);
		this.headPanel.add(h);
		
		if(this.headerBorder.getPixelY() != this.columns.get(0).getPixelY()+this.columns.get(0).getPixelHeight())
		{
			this.headerBorder.setY(h.getPixelY()+h.getPixelHeight());
			this.headPanel.setHeight(h.getPixelHeight());
			this.contentPane.setY(this.headerBorder.getPixelY()+this.headerBorder.getPixelHeight());
			this.contentPane.setHeight(this.getPixelHeight() - (this.contentPane.getPixelY() -  this.getPixelY()) -  this.getBorderSize());
		}
		
		
		Panel cp = new Panel(h.getParentXDif(), 0, h.getPixelWidth(), 0);
		cp.setHasBackground(true);
		cp.setBackgroundColor(GLColor.random());
		this.contentPane.add(cp);
		
		cp.setPosition(h.getPixelX(), cp.getPixelY());
		cp.setAutosizeWithContentY(true);
		
		this.columnPanels.add(cp);
		
		
		
	}
	
	public void addColumn(String head, int width)
	{
		//columns.add(new TableViewColumnHeader(this.getTextX(), this.getTextY(), width, head));
	}
	
	public void removeColumn(String head)
	{
		
	}
	
	public void removeColumn(int idx)
	{
		
	}
	
	
	@Override
	public int setId(int id)
	{
		this.contentPane.setId(id);
		return super.setId(id);
	}
	
	@Override
	public void setDimension(float w, float h)
	{
		super.setDimension(w, h);
		this.contentPane.setDimension(w, h);
	}
	
	@Override
	public boolean checkId(int id)
	{
		boolean r = this.contentPane.checkId(id);	
		
		boolean h =  this.headPanel.checkId(id);		
		
//		Logger.debug("checked: r: "+r+" / h: "+h);
		
		return super.checkId(id) || r || h;
	}
	
	@Override
	public void setParent(IParent p)
	{
		super.setParent(p);
		this.contentPane.setParent(p);
		this.headPanel.setParent(p);
	}
	
	@Override
	public void drawChildren(boolean selection)
	{
		super.drawChildren(selection);
		
		if(selection)
			this.getBorder().drawSelect();
		
		if(selection)
		{
			this.contentPane.drawSelect();
			this.headPanel.drawSelect();
		}
		else
		{
			this.contentPane.draw();
			this.headPanel.draw();
		}
		
		if(this.headerBorder!= null && !selection) this.headerBorder.draw();
	}
	
	@Override
	public Component getById(int id) {
		if(this.getId() == id)
			return this.contentPane;
		else if(this.contentPane.get(id) != null)
			return this.contentPane.get(id);
		else if(this.headPanel.get(id) != null)
			return this.headPanel.get(id);
		else
			return null;
	}	
	
	public Collection<? extends IGuiObject> getSelPos() {
		List<IGuiObject> e = this.contentPane.getComponentsAG();
		List<IGuiObject> h = this.contentPane.getComponentsAG();
		List<IGuiObject> pssbl = new ArrayList<IGuiObject>(10);
		
		for(int i = 0; i < e.size(); i++)
		{
					
			if(Utility.between(Mouse.getX(), ((IGuiObject)e.get(i)).getPixelX(), ((IGuiObject)e.get(i)).getPixelX()+((IGuiObject)e.get(i)).getPixelWidth()) &&
					Utility.between(SetKeys.WIN_HEIGHT.getValue(Integer.class) - Mouse.getY(), ((IGuiObject)e.get(i)).getPixelY(), ((IGuiObject)e.get(i)).getPixelY()+((IGuiObject)e.get(i)).getPixelHeight()))
			{
				if(Panel.class.isAssignableFrom(e.get(i).getClass()))
					pssbl.addAll(((Panel)e.get(i)).getSelPos());
				else if(InnerWindow.class.isAssignableFrom(e.get(i).getClass()))
					pssbl.addAll(((InnerWindow)e.get(i)).getSelPos());
				else
					pssbl.add(e.get(i));
			}
			else
			{
				e.get(i).unselected();
			}
		}
		
		for(int i = 0; i < h.size(); i++)
		{
					
			if(Utility.between(Mouse.getX(), ((IGuiObject)h.get(i)).getPixelX(), ((IGuiObject)h.get(i)).getPixelX()+((IGuiObject)h.get(i)).getPixelWidth()) &&
					Utility.between(SetKeys.WIN_HEIGHT.getValue(Integer.class) - Mouse.getY(), ((IGuiObject)h.get(i)).getPixelY(), ((IGuiObject)h.get(i)).getPixelY()+((IGuiObject)h.get(i)).getPixelHeight()))
			{
				if(Panel.class.isAssignableFrom(h.get(i).getClass()))
					pssbl.addAll(((Panel)h.get(i)).getSelPos());
				else if(InnerWindow.class.isAssignableFrom(h.get(i).getClass()))
					pssbl.addAll(((InnerWindow)h.get(i)).getSelPos());
				else
					pssbl.add(h.get(i));
			}
			else
			{
				h.get(i).unselected();
			}
		}
		
		if(Utility.between(Mouse.getX(), this.getPixelX(), this.getPixelX()+this.getPixelWidth()) &&
				Utility.between(SetKeys.WIN_HEIGHT.getValue(Integer.class) - Mouse.getY(), this.getPixelY(), this.getPixelY()+this.getPixelHeight()))
		{
			pssbl.add(this);
		}
		
		return pssbl;
	}

	boolean resizing = false;
	
	@Override
	public void dimensionChanged(int id, int width, int height) {

		
		this.resizing = true;
		int xoff = this.getPixelX()+this.getBorderSize();
		
		for(int i = 0; i < this.columns.size(); i++)
		{
			Logger.debug("Loop start: "+i);
			if(this.columnPanels.get(i).getPixelWidth() != this.columns.get(i).getPixelWidth())
			{
				Logger.debug("width different: "+i);
				this.columnPanels.get(i).setWidth(this.columns.get(i).getPixelWidth());
				Logger.debug("new width: "+this.columnPanels.get(i).getPixelWidth());
			}
			
			if(i>0)
			{
				Logger.debug("set panel x: "+i);
				this.columnPanels.get(i).setX(this.columnPanels.get(i-1).getPixelX()+this.columnPanels.get(i-1).getPixelWidth()+2);
				Logger.debug("set header x: "+i);
				this.columns.get(i).setX(columnPanels.get(i).getPixelX());
			}
			
			//this.columnPanels.get(i).setX(xoff);
//			this.columns.get(i).setX(this.columnPanels.get(i).getPixelX());
			
//			if(this.columns.get(i).getPixelX() != xoff)
//			{
//				this.columns.get(i).setX(xoff);
//				
//			}
			
			
			Logger.debug(i+"("+this.columns.get(i).getId()+"): xoff:"+xoff+" / header x: "+this.columns.get(i).getPixelX()+" / header width: "+this.columns.get(i).getPixelWidth()+" / panel x: "+this.columnPanels.get(i).getPixelX()+" / panel width: "+this.columnPanels.get(i).getPixelWidth());
			xoff += this.columns.get(i).getPixelWidth() + 2;
			
			
			
		}
		
		this.resizing = false;
		/*
		if(this.columns.size() > 0)
		{
			if(this.headerBorder.getPixelY() != this.columns.get(0).getPixelY()+this.columns.get(0).getPixelHeight())
				this.headerBorder.setY(this.columns.get(0).getPixelY()+this.columns.get(0).getPixelHeight());
		}
		*/
	}

	private int getYPos(int idx)
	{
		int yoff = 0;
		if(this.columns.size()>0) yoff += this.columns.get(0).getPixelY()+this.columns.get(0).getPixelHeight()+this.getBorderSize();
		
		for(int i = 0; i < idx; i++)
		{
			yoff += this.items.get(0).get(0).getPixelHeight();
		}
		
		return yoff;
	}
	
	public void addItem(TableViewItem item) {
		
		int ypos = this.getYPos(this.items.size());		
		
		this.items.add(item);
		
		for(int i = 0; i < this.columnPanels.size() && i < item.getColumnCount(); i++)
		{
			item.get(i).setFontSize(16);	
//			if(i == 0) item.get(i).setX(0); else item.get(i).setX(2);
			item.get(i).setX(2);
			this.columnPanels.get(i).add(item.get(i));
			item.get(i).setY(ypos);
		}
		
		
	}

	@Override
	public void scrollChanged(int id, boolean xscroll, boolean yscroll) {
		if(this.contentPane.getXsb() != null && !this.resizing) this.headPanel.onScroll(this.contentPane.getXsb().getScrollValue(), true);
//		Logger.debug("YOLO");
	}
	

}

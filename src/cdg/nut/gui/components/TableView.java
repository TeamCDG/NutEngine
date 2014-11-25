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
		this.contentPane = new Panel(this.getPixelX(), this.getPixelY(), this.getPixelWidth(), this.getPixelWidth());
		this.contentPane.setScrollable(true);
		
		this.columns = new ArrayList<TableViewColumnHeader>();
		this.items = new ArrayList<TableViewItem>();
		this.columnPanels = new ArrayList<Panel>();
		
		this.contentPane.setHasBorder(true);
		this.contentPane.setBorderSize(2);
		this.contentPane.setAutoClipping(true);
		this.contentPane.setSelectable(true);
		this.contentPane.setHasBackground(true);
		this.contentPane.addScrollChangedListener(this);
		this.contentPane.setDragable(false);
		
		this.setSelectable(true);
		this.setHasBackground(false);
		//this.setHasBorder(false);
		
		this.headerBorder = new GLPolygon(this.getPixelX()+this.contentPane.getBorderSize(), this.getPixelY()+20, this.getPixelWidth()-(2*this.contentPane.getBorderSize()), 2);
		
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
		this.contentPane.add(h);
		
		Panel cp = new Panel(h.getParentXDif(), h.getPixelHeight(), h.getPixelWidth()+3, 0);
		cp.setHasBackground(true);
		cp.setBackgroundColor(GLColor.random());
		this.contentPane.add(cp);
		
		cp.setPosition(h.getPixelX(), cp.getPixelY());
		cp.setAutosizeWithContentY(true);
		
		this.columnPanels.add(cp);
		
		if(this.headerBorder.getPixelY() != this.columns.get(0).getPixelY()+this.columns.get(0).getPixelHeight())
			this.headerBorder.setY(this.columns.get(0).getPixelY()+this.columns.get(0).getPixelHeight());
		
	}
	
	public void addColumn(String head, int width)
	{
		columns.add(new TableViewColumnHeader(this.getTextX(), this.getTextY(), width, head));
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
		
		if(super.checkId(id) &&!r)
		{
			r = true;
		}
		
		return r;
	}
	
	@Override
	public void setParent(IParent p)
	{
		super.setParent(p);
		this.contentPane.setParent(p);
	}
	
	@Override
	public void drawChildren(boolean selection)
	{
		super.drawChildren(selection);
		
		if(selection)
			this.getBorder().drawSelect();
		
		if(selection)
			this.contentPane.drawSelect();
		else
			this.contentPane.draw();
		
		if(this.headerBorder!= null && !selection) this.headerBorder.draw();
	}
	
	@Override
	public Component getById(int id) {
		if(this.getId() == id)
			return this.contentPane;
		else if(this.contentPane.get(id) != null)
			return this.contentPane.get(id);
		else
			return null;
	}	
	
	public Collection<? extends IGuiObject> getSelPos() {
		List<IGuiObject> e = this.contentPane.getComponentsAG();
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
		
		if(Utility.between(Mouse.getX(), this.getPixelX(), this.getPixelX()+this.getPixelWidth()) &&
				Utility.between(SetKeys.WIN_HEIGHT.getValue(Integer.class) - Mouse.getY(), this.getPixelY(), this.getPixelY()+this.getPixelHeight()))
		{
			pssbl.add(this);
		}
		
		return pssbl;
	}

	@Override
	public void dimensionChanged(int id, int width, int height) {

		

		int xoff = this.getPixelX()+this.contentPane.getBorderSize();
		
		for(int i = 0; i < this.columns.size(); i++)
		{
			if(this.columnPanels.get(i).getPixelWidth() != this.columns.get(i).getPixelWidth())
			{
				this.columnPanels.get(i).setWidth(this.columns.get(i).getPixelWidth());
				Logger.debug("new width: "+this.columnPanels.get(i).getPixelWidth());
			}
			
			if(this.columns.get(i).getPixelX() != xoff)
			{
				this.columns.get(i).setX(xoff);
				this.columnPanels.get(i).setX(xoff);
			}
			
			xoff += this.columns.get(i).getPixelWidth() + 2;
		}
		
		if(this.columns.size() > 0)
		{
			if(this.headerBorder.getPixelY() != this.columns.get(0).getPixelY()+this.columns.get(0).getPixelHeight())
				this.headerBorder.setY(this.columns.get(0).getPixelY()+this.columns.get(0).getPixelHeight());
		}
		
	}

	private int getYPos(int idx)
	{
		int yoff = 0;
		if(this.columns.size()>0) yoff += this.columns.get(0).getPixelY()+this.columns.get(0).getPixelHeight()+this.contentPane.getBorderSize();
		
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
			item.get(i).setX(2);
			this.columnPanels.get(i).add(item.get(i));
			item.get(i).setY(ypos);
		}
	}

	@Override
	public void scrollChanged(int id, boolean xscroll, boolean yscroll) {
		if(yscroll)
			this.headerBorder.setWidth(this.getPixelWidth()-2*this.contentPane.getBorderSize()-this.contentPane.getYsb().getPixelWidth());
		else
			this.headerBorder.setWidth(this.getPixelWidth()-2*this.contentPane.getBorderSize());
	}
	

}

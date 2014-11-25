package cdg.nut.gui;

import java.util.ArrayList;
import java.util.List;

import cdg.nut.gui.components.*;
import cdg.nut.util.gl.GLColor;

public class TableViewItem {

	
	private List<Label> subItems = new ArrayList<Label>();
	
	public TableViewItem(String subItem)
	{
		this.subItems.add(new Label(0,0,subItem));
	}
	
	public TableViewItem(String[] subItem)
	{
		for(int i = 0; i < subItem.length; i++)
		{
			this.subItems.add(new Label(0,0,subItem[i]));
		}
	}
	
	public void setHasBackground(boolean b)
	{
		for(int i = 0; i < this.subItems.size(); i++)
		{
			this.subItems.get(i).setHasBackground(b);
		}
	}
	
	public void setBackgroundColor(GLColor c)
	{
		for(int i = 0; i < this.subItems.size(); i++)
		{
			this.subItems.get(i).setBackgroundColor(c);
		}
	}
	
	public void setBackgroundActiveColor(GLColor c)
	{
		for(int i = 0; i < this.subItems.size(); i++)
		{
			this.subItems.get(i).setBackgroundActiveColor(c);
		}
	}
	
	public void setBackgroundHighlightColor(GLColor c)
	{
		for(int i = 0; i < this.subItems.size(); i++)
		{
			this.subItems.get(i).setBackgroundHighlightColor(c);
		}
	}
	
	public void draw(boolean selection)
	{
		for(int i = 0; i < this.subItems.size(); i++)
		{
			this.subItems.get(i).draw();
		}
	}
	
	public Label get(int column)
	{
		if(column < this.subItems.size())
		{
			return this.subItems.get(column);
		}
		
		return null;
	}
	
	public String getString(int column)
	{
		if(column < this.subItems.size())
		{
			return this.subItems.get(column).getText();
		}
		
		return null;
	}
	
	public String getColorString(int column)
	{
		if(column < this.subItems.size())
		{
			return this.subItems.get(column).getColortext();
		}
		
		return null;
	}

	public int getColumnCount() {
		return this.subItems.size();
	}

	
}

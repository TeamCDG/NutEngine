package cdg.nut.util.game.fish.frames;

import java.util.ArrayList;
import java.util.List;

import cdg.nut.gui.Frame;
import cdg.nut.gui.TableViewItem;
import cdg.nut.gui.components.Button;
import cdg.nut.gui.components.Label;
import cdg.nut.gui.components.TableView;
import cdg.nut.gui.components.TextBox;
import cdg.nut.interfaces.IClickListener;
import cdg.nut.logging.Logger;
import cdg.nut.test.Main;
import cdg.nut.util.Utility;
import cdg.nut.util.enums.MouseButtons;
import cdg.nut.util.gl.*;

public class ServerBrowser extends Frame {

	private Button addColumn;
	private TableView serverList;
	private Button addItem;
	private Button test;
	
	private List<GLPolygon> ftest = new ArrayList<GLPolygon>();
	
	public ServerBrowser()
	{
//		Logger.debug("#########################################################");
		this.addColumn = new Button(5, 5, 150, 34, "add column");
		this.addColumn.setAutosizeWithText(true);	
		this.addColumn.setFontSize(20);
		this.addColumn.regen();
		this.addColumn.setBorderSize(2);
		this.addColumn.addClickListener(new IClickListener(){

			@Override
			public void onClick(int x, int y, MouseButtons button, int grabx,
					int graby) {
				serverList.addColumn(Utility.randomString(10));
				Logger.debug("#########################################################");
			}});
		this.add(this.addColumn);
		
		this.addItem = new Button(155, 5, 150, 34, "add item");
		this.addItem.setAutosizeWithText(true);	
		this.addItem.setFontSize(20);
		this.addItem.regen();
		this.addItem.setBorderSize(2);
		this.addItem.addClickListener(new IClickListener(){

			@Override
			public void onClick(int x, int y, MouseButtons button, int grabx,
					int graby) {
				serverList.addItem(new TableViewItem(new String[]{Utility.randomString(10),Utility.randomString(10),Utility.randomString(10),Utility.randomString(10)}));
			}});
		this.add(this.addItem);
//		Logger.debug("#########################################################");
		
		this.test = new  Button(300, 5, 200, 34, "");
		this.test.setFontSize(16);
		this.test.addClickListener(new IClickListener(){

			@Override
			public void onClick(int x, int y, MouseButtons button, int grabx,
					int graby) {
				for(int i = 0; i < 10000; i++)
				{
					ftest.add(new GLPolygon(20, 20, 100, 100, false, 7));
//					ftest.add(new GLFont(20, 20, Utility.randomString(10)));
				}
				Logger.debug("ftest size: "+ftest.size());
			}});
		this.addComponent(this.test);
		
		this.serverList = new TableView(5, 70, 500, 500);
		this.add(this.serverList);
	}
	
	@Override 
	public void draw()
	{
		super.draw();
		for(int i = 0; i < ftest.size(); i++)
		{
			ftest.get(i).draw();
		}
	}
	
}

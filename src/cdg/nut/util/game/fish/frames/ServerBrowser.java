package cdg.nut.util.game.fish.frames;

import cdg.nut.gui.Frame;
import cdg.nut.gui.TableViewItem;
import cdg.nut.gui.components.Button;
import cdg.nut.gui.components.TableView;
import cdg.nut.gui.components.TextBox;
import cdg.nut.interfaces.IClickListener;
import cdg.nut.logging.Logger;
import cdg.nut.test.Main;
import cdg.nut.util.Utility;
import cdg.nut.util.enums.MouseButtons;

public class ServerBrowser extends Frame {

	private Button addColumn;
	private TableView serverList;
	private Button addItem;
	private TextBox testBox;
	
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
		
		this.testBox = new  TextBox(300, 5, 200, 34, "");
		this.testBox.setFontSize(16);
		this.addComponent(this.testBox);
		
		this.serverList = new TableView(5, 70, 500, 500);
		this.add(this.serverList);
	}
	
}

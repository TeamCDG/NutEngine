package cdg.nut.gui.components;

import cdg.nut.gui.Component;

public class Label extends Component{

	public Label(int x, int y, String text)
	{
		super(x, y, text);
		this.setHasBackground(false);
		this.setHasBorder(false);
		this.setSelectable(false);
		this.setAutoClipping(true);
	}

	public Label(int x, int y, int width, int height, String text) {
		super(x,y,width,height,text);
		this.setHasBackground(false);
		this.setHasBorder(false);
		this.setSelectable(false);
	}
	
	public Label(float x, float y, String text)
	{
		super(x, y, text);
		this.setHasBackground(false);
		this.setHasBorder(false);
		this.setSelectable(false);
		this.setAutoClipping(true);
	}

	public Label(float x, float y, float width, float height, String text) {
		super(x,y,width,height,text);
		this.setHasBackground(false);
		this.setHasBorder(false);
		this.setSelectable(false);
	}
}

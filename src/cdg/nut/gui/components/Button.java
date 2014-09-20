package cdg.nut.gui.components;

import cdg.nut.gui.Component;
import cdg.nut.util.gl.GLColor;

public class Button extends Component {

	public Button(int x, int y, String text)
	{
		super(x, y, text);
		this.init();
	}

	public Button(int x, int y, int width, int height, String text) {
		super(x,y,width,height,text);
		this.init();
	}
	
	public Button(float x, float y, String text)
	{
		super(x, y, text);
		this.init();
	}

	public Button(float x, float y, float width, float height, String text) {
		super(x,y,width,height,text);
		this.init();
	}
	
	private void init()
	{
		this.setScrollable(false);
		this.setCenterText(true);
	}
}

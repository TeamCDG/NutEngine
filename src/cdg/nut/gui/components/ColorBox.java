package cdg.nut.gui.components;

import cdg.nut.gui.Component;
import cdg.nut.util.Colors;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLImage;
import cdg.nut.util.gl.GLTexture;

public class ColorBox extends Component{
	public ColorBox(float x, float y, float width, float height, GLColor color) {
		super(x, y, width, height);
		this.setColor(color);
	}
	
	public ColorBox(int x, int y, int width, int height, GLColor color) {
		super(x, y, width, height);
		this.setColor(color);
	}
}

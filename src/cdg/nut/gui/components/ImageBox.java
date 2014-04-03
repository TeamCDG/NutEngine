package cdg.nut.gui.components;

import cdg.nut.gui.Component;
import cdg.nut.util.Colors;
import cdg.nut.util.gl.GLImage;
import cdg.nut.util.gl.GLTexture;

public class ImageBox extends Component {

	private GLImage image;
	
	public ImageBox(float x, float y, float width, float height, GLTexture image) {
		super(x, y, width, height);
		this.image = new GLImage(Colors.AQUA.getGlColor(), x, y, width, height);
	}
	
	@Override
	protected void drawChildren()
	{
		if(this.image.getShader() == null)
			this.image.setShader(this.getShader());
		//image.draw();
	}

}

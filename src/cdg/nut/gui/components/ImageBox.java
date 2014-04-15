package cdg.nut.gui.components;

import cdg.nut.gui.Component;
import cdg.nut.util.Colors;
import cdg.nut.util.Utility;
import cdg.nut.util.gl.GLImage;
import cdg.nut.util.gl.GLTexture;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class ImageBox extends Component {

	private GLImage image;

	public ImageBox(float x, float y, float width, float height, GLTexture image)
	{
		super(x, y, width, height);
		int bs = Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class);
		float[] off = Utility.pixelSizeToGLSize(bs, bs);
		this.image = new GLImage(image, x+off[0], y+off[1], width-(2*off[0]), height-(2*off[1]));
		this.image.setColor(Colors.WHITE.getGlColor());
	}
	
	public ImageBox(int x, int y, int width, int height, GLTexture image)
	{
		super(x, y, width, height);
		int bs = Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class);
		this.image = new GLImage(image, x+bs, y+bs, width-(2*bs), height-(2*bs));
		this.image.setColor(Colors.WHITE.getGlColor());
	}
	
	@Override
	protected void move()
	{
		super.move();
		int bs = Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class);
		this.image.setPosition(this.getPixelX()+bs, this.getPixelY()+bs);
	}

	@Override
	protected void drawChildren(boolean selection)
	{
		super.drawChildren(selection);
		
		if (this.image.getShader() == null && !selection) {
			this.image.setShader(this.getShader());
		}
		
		if(!selection) image.draw();
	}
}

package cdg.nut.test;

import cdg.nut.util.game.Entity2D;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLTexture;

public class Tety extends Entity2D {

	public Tety(float x, float y, float width, float height) {
		super(x, y, width, height);
		this.setPrimary(new GLTexture("plantagon.png"));
	}
	
	@Override
	public void onTick()
	{
		
	}

	
	@Override
	public void selected() {
		super.selected();
		this.setColor(new GLColor(1.0f, 0.0f, 0.0f, 1.0f));
	}
	
	@Override
	public void unselected() {
		super.unselected();
		this.setColor(new GLColor(1.0f, 1.0f, 1.0f, 1.0f));
	}
}

package cdg.nut.util.gl;

import cdg.nut.interfaces.IDrawable;
import cdg.nut.interfaces.ISelectable;

public class GLObject implements ISelectable {

	private int id;
	private boolean selected;	
	
	public GLObject()
	{
		
	}
	
	
	
	//TODO: Javadoc
	private void draw(boolean selection)
	{
		//TODO: Let's draw
	}
	
	//TODO: Javadoc
	@Override
	public void draw() {
		this.draw(false);		
	}
	
	//TODO: Javadoc
	@Override
	public void drawSelect() {
		this.draw(true);		
	}

	//TODO: Javadoc
	@Override
	public void setId(int id) {
		this.id = id;		
	}

	//TODO: Javadoc
	@Override
	public int getId() {
		return this.id;
	}

	//TODO: Javadoc
	@Override
	public void setSelected(boolean selection) {
		this.selected = selection;		
	}

	//TODO: Javadoc
	@Override
	public boolean isSelected() {
		return this.selected;
	}

}

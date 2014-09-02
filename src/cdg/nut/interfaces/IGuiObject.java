package cdg.nut.interfaces;

import cdg.nut.util.enums.MouseButtons;

public interface IGuiObject {

	public void drawSelection();

	public boolean isSelected();

	public void unselected();

	public void selected();

	public void clicked(int x, int i, MouseButtons left, boolean b,
			boolean deltaMouseGrabbed, int mouseGrabSX, int mouseGrabSY);

	public void setActive(boolean b);

	public boolean key(int eventKey, char eventCharacter);

	public int getId();

	public float getX();

	public float getY();

	public int getPixelX();

	public int getPixelWidth();

	public int getPixelHeight();

	public int getPixelY();

	public boolean checkId(int id);

	public float getWidth();

	public float getHeight();
	
}

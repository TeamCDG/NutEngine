package cdg.nut.util.enums;

public enum MouseButtons {
	
	LEFT(0), RIGHT(1), MIDDLE(2);
	
	private int key;
	
	MouseButtons(int key)
	{
		this.key = key;
	}
	
	public int getKey()
	{
		return this.key;
	}
}

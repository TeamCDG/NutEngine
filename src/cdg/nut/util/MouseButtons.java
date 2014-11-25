package cdg.nut.util;

public enum MouseButtons {
	
	LEFT((byte) 0), RIGHT((byte) 1), MIDDLE((byte) 2);
	
	private byte key;
	
	MouseButtons(byte key)
	{
		this.key = key;
	}
	
	public byte getKey()
	{
		return this.key;
	}
}

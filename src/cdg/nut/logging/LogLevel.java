package cdg.nut.logging;

/** 
 * Enumeration of different Logging/Debug Levels.
 */
public enum LogLevel {
	DEBUG(3, "DEBUG"), INFO(2, "INFO"), ERROR(1, "ERROR"), NONE(0, "NUTS_ARE_AMAZING");
	
	private int level;
	private String str;
	
	LogLevel(int lvl, String str)
	{
		this.level = lvl;
		this.str = str;
	}
	
	/** Gets the level.&nbsp;The higher, the more will be printed*/
	int getLevel()
	{
		return this.level;
	}
	
	
	/** Gets a String representation of the level */
	@Override 
	public String toString()
	{
		return this.str;
	}
	
	/** Returns true if current level is greater than given level */
	public boolean isGreater(LogLevel lvl)
	{
		if(this.level > lvl.getLevel())
			return true;
		else
			return false;
	}
}

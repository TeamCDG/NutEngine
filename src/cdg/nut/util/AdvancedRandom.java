package cdg.nut.util;

import java.util.Random;

//TODO: Javadoc
public class AdvancedRandom extends Random 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AdvancedRandom()
	{
		super();
	}
	
	public AdvancedRandom(int seed)
	{
		super(seed);
	}
	
	public int nextInt(int min, int max)
	{
		return this.nextInt(max-min)+min;
	}
	
}

package cdg.nut.util.game.fish;

import cdg.nut.interfaces.IEntity;
import cdg.nut.util.game.Entity2D;

public abstract class FishGameUtility {

	public static boolean isBuilding(IEntity e)
	{
		return (e instanceof Building);
	}
}

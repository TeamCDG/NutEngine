package cdg.nut.interfaces;

import cdg.nut.util.enums.MouseButtons;
import cdg.nut.util.game.World;

public interface IEntity extends IGuiObject{
	public void onTick();

	public int getId();
	
	public int setId(int nextId);
	
	public void draw();
	
	public World getParent();

	public void setParent(World parent);

	public void rightClickhappened(IEntity entity, float worldX, float worldY);
}

package cdg.nut.interfaces;

import com.google.gson.JsonObject;

import cdg.nut.util.enums.MouseButtons;
import cdg.nut.util.game.World;
import cdg.nut.util.net.UpdatePackage;

public interface IEntity extends IGuiObject{
	public boolean onTick();

	public int getId();
	
	public int setId(int nextId);
	
	public void draw();
	
	public World getParent();

	public void setParent(World parent);

	public void rightClickhappened(IEntity entity, float worldX, float worldY);	

	public void deserialize(JsonObject json);

	public UpdatePackage getUpdate();
	
	public void packageRecieved(cdg.nut.util.net.Package p);
}

package cdg.nut.util.game.fish;

import com.google.gson.JsonObject;

import cdg.nut.util.enums.Colors;
import cdg.nut.util.game.Entity2D;
import cdg.nut.util.game.Player;

public class FishGameEntity extends Entity2D{

	private int owner;
	private int hp = 1;
	
	@Override
	public void deserialize(JsonObject json)
	{
		super.deserialize(json);
		
		this.owner = json.get("owner").getAsInt();
		this.hp = json.get("hp").getAsInt();
	}
	
	public FishGameEntity() {}
	
	public FishGameEntity(float x, float y, float width, float height, int owner) {
		super(x, y, width, height);
		this.owner = owner;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void setActive(boolean b)
	{
		super.setActive(b);
		if(b)
			this.setColor(this.getOwner().getPlayerColor());
		else
			this.setColor(Colors.WHITE.getGlColor());
	}

	

	/**
	 * @return the owner
	 */
	public Player getOwner() {
		return this.getParent().getPlayer(this.owner);
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(Player owner) {
		this.owner = owner.getPlayerId();
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}
}

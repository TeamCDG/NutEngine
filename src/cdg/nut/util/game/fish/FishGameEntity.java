package cdg.nut.util.game.fish;

import cdg.nut.util.enums.Colors;
import cdg.nut.util.game.Entity2D;
import cdg.nut.util.game.Player;

public class FishGameEntity extends Entity2D{

	Player owner;
	
	public FishGameEntity(float x, float y, float width, float height, Player p) {
		super(x, y, width, height);
		this.owner = p;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void setActive(boolean b)
	{
		super.setActive(b);
		if(b)
			this.setColor(this.owner.getPlayerColor());
		else
			this.setColor(Colors.WHITE.getGlColor());
	}

}

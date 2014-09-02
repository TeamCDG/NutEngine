package cdg.nut.test;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import cdg.nut.gui.components.Button;
import cdg.nut.interfaces.IClickListener;
import cdg.nut.util.Utility;
import cdg.nut.util.enums.Colors;
import cdg.nut.util.enums.MouseButtons;
import cdg.nut.util.game.GameFrame;
import cdg.nut.util.game.Player;
import cdg.nut.util.game.Tile;
import cdg.nut.util.game.World;
import cdg.nut.util.game.fish.Bulding;
import cdg.nut.util.game.fish.Coral;
import cdg.nut.util.game.fish.FishWorld;
import cdg.nut.util.game.fish.PeasantHut;
import cdg.nut.util.game.fish.WorkerFish;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLPolygon;

public class TestGameFrame extends GameFrame {

	private Button gf;	
	private Tety test;
	private Bulding building;
	private boolean build;
	private boolean buildable = false;
	// LINE TEST: private LinePoly lp;

	public TestGameFrame()
	{
		super();
		//this.setBackground("asparagus.png");
		
		this.gf = new Button(420, 462, "Asparagus <3");
		this.gf.setFontSize(16);
		this.gf.setPosition(460, 10);
		this.gf.setTooltip("~ Bang Bang Gamerang ~");
		this.gf.addClickListener(new IClickListener(){

			@Override
			public void onClick(int x, int y, MouseButtons button, int grabx, int graby) {
				//GameFrame g = new TestGameFrame();
				setBackground("asparagus.png");
				//Main.activeFrame = g;
				
			}});
		this.gf.setBorderHighlightColor(Colors.CORAL.getGlColor());
		this.addComponent(gf);
		
		test = new Tety(0.0f, 0.0f, 0.2f, 0.2f);
		test.setPosition(0.2f, -0.2f);
		this.addEntity(test);
		
		for(int i = 0; i < 10; i++) this.addEntity(new WorkerFish(new java.util.Random().nextFloat(), new java.util.Random().nextFloat()-0.5f, this.getLocalPlayer()));
		
		
		for(int h = 0; h < this.getWorld().getGrid().getHTileCount(); h++)
		{
			for(int w = 0; w < this.getWorld().getGrid().getVTileCount(); w++)
			{
				if(new java.util.Random().nextInt(7) == 5)
				{
					Coral c = new Coral(w * 0.1f + ((new java.util.Random().nextFloat()*2.0f-1.0f) * 0.02f) + 0.05f, -h * 0.1f + ((new java.util.Random().nextFloat()*2.0f-1.0f) * 0.02f) - 0.05f, new Player(Colors.TRANSPARENT.getGlColor(), "WORLD"));
					this.getWorld().getGrid().getTile(w,h).setOccupied(true);
					this.addEntity(c);
					this.getWorld().getGrid().getTile(w,h).setEntityId(c.getId());
				}
				
			}
		}
	}
	
	@Override
	protected void handleKey(int key)
	{
		if(key == Keyboard.KEY_B)
		{
			float[] mc = Utility.getMouseGL();
			this.building = new PeasantHut(mc[0], mc[1], this.getLocalPlayer());		
			this.building.setColor(new GLColor(1.0f, 0.0f, 0.0f, 0.5f));
			this.build = true;
		}
		else
		{
			super.handleKey(key);
		}
	}
	
	@Override
	public World initWorld(int width, int height)
	{
		return new FishWorld(width, height);
	}
	
	
	@Override
	public void mouseLeftClicked()
	{
		if(this.build)
		{
			if(this.buildable)
			{
				this.building.setColor(Colors.WHITE.getGlColor());
				this.addEntity(this.building);
				
				Tile[] tmp = this.getWorld().getGrid().getTileSurroundings(this.getTx(), this.getTy(), 0, this.building.getTileWidth()-1, this.building.getTileHeight()-1, 0);
				
				for(int i = 0; i < tmp.length; i++)
				{
					tmp[i].setOccupied(true);
					tmp[i].setEntityId(this.building.getId());
				}
				
				this.build = false;
				this.buildable = false;
				this.building = null;
			}
		}
		else
		{
			super.mouseLeftClicked();
		}
	}
	
	@Override
	public void draw()
	{
		try
		{
			Tile[] tmp = this.getWorld().getGrid().getTileSurroundings(this.getTx(), this.getTy(), 0, this.building.getTileWidth()-1, this.building.getTileHeight()-1, 0);
			
			for(int i = 0; i < tmp.length; i++)
			{
				tmp[i].selected();
			}
		}
		catch(Exception e){}
		
		super.draw();
		//
		
		this.test.setRotation(this.test.getRotation() + 6.0f);
		
		
		
		if(this.building != null)
		{
			float[] mc = Utility.getMouseGL();
			
			Tile t = this.getWorld().getGrid().getTile(this.getTx(), this.getTy());
			
			if(this.getWorld().getGrid().checkTileSurroundings(this.getTx(), this.getTy(), 0, this.building.getTileWidth()-1, this.building.getTileHeight()-1, 0))
			{
				this.building.setColor(new GLColor(0.0f, 1.0f, 0.0f, 0.5f));
				this.buildable = true;
				
			}
			else
			{
				this.building.setColor(new GLColor(1.0f, 0.0f, 0.0f, 0.5f));
				this.buildable = false;
			}
			
			
			
			//this.building.setPosition(t.getX()+(building.getTileWidth()-building.getTileXMove())*0.05f+0.05f, t.getY()+(building.getTileHeight()-building.getTileYMove())*0.05f+0.05f);
			this.building.setPosition(t.getX()+building.getTileXMove()*0.1f+0.05f, t.getY()+building.getTileYMove()*0.1f+0.05f);
			
			this.building.draw();
			
		}
		
		
		//this.md.setText(Mouse.getX()+"/"+(SetKeys.WIN_HEIGHT.getValue(Integer.class)-Mouse.getY()));
	}
}

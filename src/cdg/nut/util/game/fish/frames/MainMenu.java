package cdg.nut.util.game.fish.frames;

import java.io.IOException;

import cdg.nut.gui.Frame;
import cdg.nut.gui.components.Button;
import cdg.nut.interfaces.IClickListener;
import cdg.nut.test.Main;
import cdg.nut.util.BitmapFont;
import cdg.nut.util.Engine;
import cdg.nut.util.enums.MouseButtons;
import cdg.nut.util.settings.SetKeys;

public class MainMenu extends Frame{
	
	private Button singleplayer;
	private Button multiplayer;
	private Button loadGame;
	private Button options;
	private Button exit;
	
	public MainMenu()
	{
		int w = 300;
		BitmapFont daedra = null;
		
		try {
			daedra = new BitmapFont("res/font/daedra.txt", "res/font/daedra.png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.singleplayer = new Button(20, 200, w, 60, "Singleplayer");
		this.singleplayer.setFontSize(40);
		this.singleplayer.getFO().setFont(daedra);
		this.singleplayer.setBorderSize(2);
		this.add(this.singleplayer);
		
		this.multiplayer = new Button(20, this.singleplayer.getPixelY() + this.singleplayer.getPixelHeight() + 20, w, 60, "Multiplayer");
		this.multiplayer.setFontSize(40);
		this.multiplayer.getFO().setFont(daedra);
		this.multiplayer.setBorderSize(2);
		this.multiplayer.addClickListener(new IClickListener(){

			@Override
			public void onClick(int x, int y, MouseButtons button, int grabx,
					int graby) {
				Main.activeFrame = new ServerBrowser();
				
			}});
		this.add(this.multiplayer);
		
		this.loadGame = new Button(20, this.multiplayer.getPixelY() + this.multiplayer.getPixelHeight() + 20, w, 60, "Load Game");
		this.loadGame.setFontSize(40);
		this.loadGame.getFO().setFont(daedra);
		this.loadGame.setBorderSize(2);
		this.add(this.loadGame);
		
		this.options = new Button(20, this.loadGame.getPixelY() + this.loadGame.getPixelHeight() + 20, w, 60, "Options");
		this.options.setFontSize(40);
		this.options.getFO().setFont(daedra);
		this.options.setBorderSize(2);
		this.add(this.options);
		
		this.exit = new Button(20, this.options.getPixelY() + this.options.getPixelHeight() + 20, w, 60, "Exit");
		this.exit.setFontSize(40);
		this.exit.getFO().setFont(daedra);
		this.exit.setBorderSize(2);
		this.add(this.exit);
		
		
		int hw = SetKeys.WIN_WIDTH.getValue(Integer.class)/2;
		
		Engine.console.setPosition(hw, 20);
		Engine.console.setDimension(hw-20, SetKeys.WIN_HEIGHT.getValue(Integer.class) -40);
		SetKeys.CONSOLE_SHOW.execute(null);
	}

}

package cdg.nut.gui.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import cdg.nut.gui.Component;
import cdg.nut.interfaces.ICommandListener;
import cdg.nut.interfaces.IKeyboardListener;
import cdg.nut.logging.Logger;
import cdg.nut.util.MouseButtons;
import cdg.nut.util.gl.GLImage;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class TextBox extends Component implements IKeyboardListener{
	
	private GLImage cursor;
	private int cursorPos = 0; //TODO: implement Cursor...
	
	private int dc = Settings.get(SetKeys.R_MAX_FPS, Integer.class);
	
	private boolean commandMode = false;
	private boolean readonly = false;
	List<ICommandListener> clis = new ArrayList<ICommandListener>();
	
	public TextBox(int x, int y, String text)
	{
		super(x, y, text);
		this.setActiveable(true);
		this.addKeyListener(this);
		this.cursor = new GLImage(this.getFontColor(), this.getTextX(), this.getTextY(), 4, this.getFontPixelSize());
	}


	public TextBox(int x, int y, int width, int height, String text) {
		super(x,y,width,height,text);
		this.setActiveable(true);
		this.addKeyListener(this);
		this.cursor = new GLImage(this.getFontColor(), this.getTextX(), this.getTextY(), 4, this.getFontPixelSize());
	}
	
	public TextBox(float x, float y, String text)
	{
		super(x, y, text);
		this.setActiveable(true);
		this.addKeyListener(this);
		this.cursor = new GLImage(this.getFontColor(), this.getTextX(), this.getTextY(), 4, this.getFontPixelSize());
	}

	public TextBox(float x, float y, float width, float height, String text) {
		super(x,y,width,height,text);
		this.setActiveable(true);
		this.addKeyListener(this);
		this.cursor = new GLImage(this.getFontColor(), this.getTextX(), this.getTextY(), 4, this.getFontPixelSize());
	}
	

	
	@Override
	public void setFontSize(float f)
	{
		super.setFontSize(f);
		
		this.cursor = new GLImage(this.getFontColor(), this.getTextX(), this.getTextY(), 4, this.getFontPixelSize());
	}
	
	@Override
	public void setFontSize(int f)
	{
		super.setFontSize(f);
		
		this.cursor = new GLImage(this.getFontColor(), this.getTextX(), this.getTextY(), 4, this.getFontPixelSize());
	}
	
	/**
	 * @return the commandMode
	 */
	public boolean isCommandMode() {
		return commandMode;
	}

	/**
	 * @param commandMode the commandMode to set
	 */
	public void setCommandMode(boolean commandMode) {
		this.commandMode = commandMode;
	}
	
	@Override
	protected void drawChildren(boolean selection)
	{
		super.drawChildren(selection);
		
		if(!selection && this.isActive() && dc > 0 && this.cursorInside())
			this.cursor.draw();
		
		dc-= Settings.get(SetKeys.R_MAX_FPS, Integer.class)/60 * 2;
		if(dc < -Settings.get(SetKeys.R_MAX_FPS, Integer.class))
			dc = Settings.get(SetKeys.R_MAX_FPS, Integer.class);
	}
	
	private boolean cursorInside()
	{
		return this.cursor.getPixelX() >= this.getPixelX() && this.cursor.getPixelX() <= this.getPixelX()+this.getPixelWidth() 
				&& this.cursor.getPixelY()+this.cursor.getPixelHeight() >= this.getPixelY() && 
				   this.cursor.getPixelY()+this.cursor.getPixelHeight() <= this.getPixelY()+this.getPixelHeight();
	}
	
	private void setCursorPos()
	{
		int [] tmp = this.getCursorPos(this.cursorPos);
		this.cursor.setPosition(this.getTextX()+tmp[0], this.getTextY()+tmp[1]);
	}
	
	private int getUpPos(String text, int cpos)
	{
		String[] lines = text.split("\n");
        int line = 0;
        int tmp = cpos;
        while(tmp > 0)
        {
            tmp -= lines[line].length() + 1;
            line++;
        }
        
        line--;
        
        
        int all = 0;
        for(int i = 0; i < line; i++)
        {
            all += lines[i].length()+1;
        }
        
        int dif = cpos - all;
        
        if(line == -1)
        	return cpos;
        
        if(dif == lines[line].length()+1)
            dif = 0;
        else if(dif >= lines[line-1].length()+1)
            dif = lines[line-1].length();
        
        int t = 0;
        for(int i = 0; i < line-1; i++)
        {
            t += lines[i].length()+1;
        }
        return t+dif;
	}
	
	private int getDownPos(String text, int cpos)
	{
		String[] lines = text.split("\n");
        int line = 0;
        int tmp = cpos;
        while(tmp > 0)
        {
            tmp -= lines[line].length() + 1;
            line++;
        }
        
        line--;
        
        
        int all = 0;
        for(int i = 0; i < line; i++)
        {
            all += lines[i].length()+1;
        }
        
        int dif = cpos - all;
        
        if(line == -1)
        	line++;
        
        if(line == lines.length-1)
        	return cpos;
        
        
        if(dif == lines[line].length()+1)
        {
            dif = 0;
            line++;
        }
        else if(dif >= lines[line+1].length()+1)
        {
            dif = lines[line+1].length();
        }
        
        int t = 0;
        for(int i = 0; i < line+1; i++)
        {
            t += lines[i].length()+1;
        }
        return t+dif;
	}
	
	@Override
	public void keyDown(int eventKey, char eventCharacter)
	{
		if(this.readonly)
			return;
		
		
		if(!Character.isISOControl(eventCharacter) && eventKey != Keyboard.KEY_SPACE)
		{
			String text = this.getText();
			this.setText(text.substring(0,this.cursorPos)+eventCharacter+text.substring(this.cursorPos));
			this.cursorPos++;
			this.setCursorPos();
		}
		else
		{
			if(eventKey == Keyboard.KEY_SPACE)
			{
				String text = this.getText();
				this.setText(text.substring(0,this.cursorPos)+" "+text.substring(this.cursorPos));
				this.cursorPos++;
				this.setCursorPos();
			}
			else if(eventKey == Keyboard.KEY_RETURN)
			{
				if(!this.commandMode)
				{
					String text = this.getText();
					this.setText(text.substring(0,this.cursorPos)+"\n"+text.substring(this.cursorPos));
					this.cursorPos++;
					this.setCursorPos();
				}
				else
				{
					for(int i = 0; i < clis.size(); i++)
					{
						clis.get(i).exec(this.getText());
					}
					
					this.setText("");
					this.cursorPos = 0;
					this.setCursorPos();
				}
			}
			else if(eventKey == Keyboard.KEY_BACK)
			{
				if(this.getText().length() >= 1 && this.cursorPos > 0)
				{
					String text = this.getText();
					this.setText(text.substring(0,this.cursorPos-1)+text.substring(this.cursorPos));
					this.cursorPos--;
					this.setCursorPos();
				}
			}
			else if(eventKey == Keyboard.KEY_DELETE)
			{
				if(this.getText().length() >= 1 && this.cursorPos != this.getText().length())
				{
					String text = this.getText();
					this.setText(text.substring(0,this.cursorPos)+text.substring(this.cursorPos+1));
				}
			}
			else if(eventKey == Keyboard.KEY_LEFT && this.cursorPos > 0)
			{
				this.cursorPos--;
				this.setCursorPos();
			}
			else if(eventKey == Keyboard.KEY_RIGHT && this.cursorPos < this.getText().length())
			{
				this.cursorPos++;
				this.setCursorPos();
			}
			else if(eventKey == Keyboard.KEY_UP)
			{
				String text = this.getText();
				this.cursorPos = this.getUpPos(text, this.cursorPos);					
				this.setCursorPos();
			}
			else if(eventKey == Keyboard.KEY_DOWN)
			{
				String text = this.getText();
				this.cursorPos = this.getDownPos(text, this.cursorPos);	
				this.setCursorPos();
			}
		}
	}
	
	@Override
	protected void clicked(int x, int y, MouseButtons button, boolean mouseLeftPressed)
	{
		super.clicked(x, y, button, mouseLeftPressed);
		
		if(button == MouseButtons.LEFT)
		{
			int index = this.getIndexByPosition(x, y);
			Logger.debug("clicked ("+x+"/"+y+") got index: "+index,"TextBox.clicked");
			if(index != -1)
			{
				this.cursorPos = index;
				this.setCursorPos();
			}
		}
	}
	
	public void addCommandListener(ICommandListener lis)
	{
		this.clis.add(lis);
	}
	
	public void removeCommandListener(ICommandListener lis)
	{
		this.clis.remove(lis);
	}


	public boolean isReadonly() {
		return readonly;
	}


	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}
}

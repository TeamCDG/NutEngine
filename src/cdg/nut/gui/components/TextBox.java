package cdg.nut.gui.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import cdg.nut.gui.Component;
import cdg.nut.interfaces.ICommandListener;
import cdg.nut.interfaces.IKeyboardListener;
import cdg.nut.interfaces.IToolTipGenerator;
import cdg.nut.logging.Logger;
import cdg.nut.util.Utility;
import cdg.nut.util.enums.MouseButtons;
import cdg.nut.util.gl.GLImage;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class TextBox extends Component implements IKeyboardListener{
	
	private GLImage cursor;
	private int cursorPos = 0; 
	private int dcursorPos = 0;
	
	private String oToolTipText;
	
	private int dc = Settings.get(SetKeys.R_MAX_FPS, Integer.class);
	
	private boolean commandMode = false;
	private boolean readonly = false;
	
	private boolean selection = false;
	private int selectionStart = 0;
	
	private IToolTipGenerator tTGen;
	
	List<ICommandListener> clis = new ArrayList<ICommandListener>();
	
	public TextBox(int x, int y, String text)
	{
		super(x, y, text);
		this.setActiveable(true);
		this.setTextSelectable(true);
		this.addKeyListener(this);
		this.cursor = new GLImage(this.getFontColor(), this.getTextX(), this.getTextY(), 4, this.getFontPixelSize());
	}


	public TextBox(int x, int y, int width, int height, String text) {
		super(x,y,width,height,text);
		this.setActiveable(true);
		this.setTextSelectable(true);
		this.addKeyListener(this);
		this.cursor = new GLImage(this.getFontColor(), this.getTextX(), this.getTextY(), 4, this.getFontPixelSize());
	}
	
	public TextBox(float x, float y, String text)
	{
		super(x, y, text);
		this.setActiveable(true);
		this.setTextSelectable(true);
		this.addKeyListener(this);
		this.cursor = new GLImage(this.getFontColor(), this.getTextX(), this.getTextY(), 4, this.getFontPixelSize());
	}

	public TextBox(float x, float y, float width, float height, String text) {
		super(x,y,width,height,text);
		this.setActiveable(true);
		this.setTextSelectable(true);
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
		
		/*
		if(commandMode)
		{
			this.setManualTThide(true);
			this.setManualTTshow(true);
		}
		else
		{
			this.setManualTThide(false);
			this.setManualTTshow(false);
		}*/
	}
	
	@Override
	protected void setActive(boolean b)
	{
		super.setActive(b);
		
		if(b)
		{
			this.oToolTipText = this.getTooltip().getText(0);
			this.setManualTTshow(true);
			this.setManualTThide(true);
			this.manualHideToolTip();
		}
		else
		{
			this.getTooltip().setText(this.oToolTipText);
			this.setManualTTshow(false);
			this.setManualTThide(false);
		}
			
		if(!b && this.selection)
		{
			this.selection = false;
			this.setTextSelection(0, 0);
		}
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
		int[] tmp = this.getCursorPos(this.cursorPos);
		int[] dtmp = this.getCursorPos(this.dcursorPos);
		
		int tw = this.getPixelWidth()-(2*this.getXPadding())-4; 
		int th = this.getPixelHeight()-(2*this.getYPadding())-4; 
				
		Logger.debug("tw: "+tw+" / textX: "+this.getTextX()+" / sv: "+this.getXsb().getScrollValue()+" / curx: "+tmp[0],"TextBox.setCursorPos");
		/*
		if(tmp[0] - this.getXsb().getScrollValue() +this.getTextX() < this.getTextX() || tmp[0] - this.getXsb().getScrollValue() + this.getTextX()> this.getTextX()+tw)
		{
			int nsv = this.getXsb().getScrollValue()+(tmp[0]-dtmp[0]);
			
			if(nsv < 0)
				nsv = (tmp[0]+(3*this.cursor.getPixelWidth()))-tw;
			
			this.getXsb().setScrollValue(nsv);
			Logger.debug("nsv: "+nsv+" / curxw: "+(tmp[0]+this.cursor.getPixelWidth()),"TextBox.setCursorPos");
		}
		
		if(tmp[0] == 0)
		{
			this.getXsb().setScrollValue(0);
		}
		else if(tmp[0] == this.getFO().getPixelWidth())
		{
			this.getXsb().setScrollValue(this.getXsb().getMaxValue());
		}
		*/
		if(tmp[0] == 0)
		{
			this.getXsb().setScrollValue(0);
		}
		else if(tmp[0] == this.getFO().getPixelWidth())
		{
			this.getXsb().setScrollValue(this.getXsb().getMaxValue());
		}
		else if(!Utility.between((tmp[0]+this.cursor.getPixelWidth())-this.getXsb().getScrollValue(),0,tw))
		{			
			if(this.dcursorPos - this.cursorPos == 1 && tmp[1] - dtmp[1] == 0)
				this.getXsb().setScrollValue(this.getXsb().getScrollValue()+(tmp[0]-dtmp[0]));
			else
				this.getXsb().setScrollValue((tmp[0]+this.cursor.getPixelWidth())-tw);
			
		}
		
		if(tmp[1] == 0)
		{
			this.getYsb().setScrollValue(0);
		}
		else if(this.getText().indexOf("\n", this.cursorPos) == -1)
		{
			this.getYsb().setScrollValue(this.getYsb().getMaxValue());
		}
		else if(!Utility.between((tmp[1]+this.cursor.getPixelHeight())-this.getYsb().getScrollValue(),0,th) || !Utility.between((tmp[1])-this.getYsb().getScrollValue(),0,th) )
		{			
			if(this.dcursorPos - this.cursorPos == 1 && tmp[0] - dtmp[0] == 0)
				this.getYsb().setScrollValue(this.getYsb().getScrollValue()+(tmp[1]-dtmp[1]));
			else 
				this.getYsb().setScrollValue((tmp[1]+((this.cursorPos > this.dcursorPos?1:2)*this.cursor.getPixelHeight()))-th);
			
		}
		
		
		
		this.cursor.setPosition(this.getTextX()+tmp[0]-this.getXsb().getScrollValue(), this.getTextY()+tmp[1]-this.getYsb().getScrollValue());
		
		
		
		this.dcursorPos = this.cursorPos;
		
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
        if(cpos != 0 && cpos < text.length() && text.substring(cpos-1, cpos).equals("\n"))
        	line++;
        
        
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
        else if(line > 0 && dif >= lines[line-1].length()+1)
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
	public void setText(String text)
	{
		if(!this.readonly)
			super.setText(text);
	}
	
	@Override
	public void keyDown(int eventKey, char eventCharacter)
	{
		Logger.debug("event key: "+eventKey+" / key name: "+Keyboard.getKeyName(eventKey), "TextBox.keyDown");
		
		if(eventKey == Keyboard.KEY_V && (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)))
		{
			if(!this.selection)
			{
				String text = this.getText();
				this.setText(text.substring(0,this.cursorPos)+Utility.getClipboard()+text.substring(this.cursorPos));
				this.cursorPos += Utility.getClipboard().length();
				this.setCursorPos();
			}
			else
			{
				String text = this.getText();
				this.setText(text.substring(0,Math.min(this.cursorPos, this.selectionStart))+Utility.getClipboard()+text.substring(Math.max(this.cursorPos, this.selectionStart)));
				this.selection = false;
				this.setTextSelection(0, 0);
				this.cursorPos = Math.min(this.cursorPos, this.selectionStart)+Utility.getClipboard().length();
				this.setCursorPos();
			}
		}
		else if(eventKey == Keyboard.KEY_C && (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)))
		{
			if(!this.isPasswordMode())
				Utility.setClipboard(this.getSelectedText());
		}
		else if(eventKey == Keyboard.KEY_X && (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)))
		{
			if(!this.isPasswordMode() && this.selection)
			{
				Utility.setClipboard(this.getSelectedText());
				String text = this.getText();
				this.setText(text.substring(0,Math.min(this.cursorPos, this.selectionStart))+text.substring(Math.max(this.cursorPos, this.selectionStart)));
				this.selection = false;
				this.setTextSelection(0, 0);
				this.cursorPos = Math.min(this.cursorPos, this.selectionStart);
				this.setCursorPos();
			}
		}
		else if(eventKey == Keyboard.KEY_A && (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)))
		{
			this.selection = true;
			this.setTextSelection(0, this.getText().length());
			//TODO: Fix to not moving cursor
			this.cursorPos = this.getText().length();
			this.selectionStart = 0;
			this.setCursorPos();
		}
		else if(!Character.isISOControl(eventCharacter) && eventKey != Keyboard.KEY_SPACE)
		{
			if(!this.selection)
			{
				String text = this.getText();
				this.setText(text.substring(0,this.cursorPos)+eventCharacter+text.substring(this.cursorPos));
				this.cursorPos++;
				this.setCursorPos();
			}
			else
			{
				String text = this.getText();
				this.setText(text.substring(0,Math.min(this.cursorPos, this.selectionStart))+eventCharacter+text.substring(Math.max(this.cursorPos, this.selectionStart)));
				this.selection = false;
				this.setTextSelection(0, 0);
				this.cursorPos = Math.min(this.cursorPos, this.selectionStart)+1;
				this.setCursorPos();
			}
		}
		else
		{
			if(eventKey == Keyboard.KEY_SPACE)
			{
				if(!this.selection)
				{
					String text = this.getText();
					this.setText(text.substring(0,this.cursorPos)+" "+text.substring(this.cursorPos));
					this.cursorPos++;
					this.setCursorPos();
				}
				else
				{
					String text = this.getText();
					this.setText(text.substring(0,Math.min(this.cursorPos, this.selectionStart))+" "+text.substring(Math.max(this.cursorPos, this.selectionStart)));
					this.selection = false;
					this.setTextSelection(0, 0);
					this.cursorPos = Math.min(this.cursorPos, this.selectionStart)+1;
					this.setCursorPos();
				}
			}
			else if(eventKey == Keyboard.KEY_RETURN)
			{
				if(!this.commandMode)
				{
					if(!this.selection)
					{
						String text = this.getText();
						this.setText(text.substring(0,this.cursorPos)+"\n"+text.substring(this.cursorPos));
						this.cursorPos++;
						this.setCursorPos();
					}
					else
					{
						String text = this.getText();
						this.setText(text.substring(0,Math.min(this.cursorPos, this.selectionStart))+"\n"+text.substring(Math.max(this.cursorPos, this.selectionStart)));
						this.selection = false;
						this.setTextSelection(0, 0);
						this.cursorPos = Math.min(this.cursorPos, this.selectionStart)+1;
						this.setCursorPos();
					}
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
				if(this.getText().length() >= 1 && this.cursorPos > 0 && !this.selection)
				{
					String text = this.getText();
					this.setText(text.substring(0,this.cursorPos-1)+text.substring(this.cursorPos));
					this.cursorPos--;
					this.setCursorPos();
				}
				else if(this.selection)
				{
					String text = this.getText();
					this.setText(text.substring(0,Math.min(this.cursorPos, this.selectionStart))+text.substring(Math.max(this.cursorPos, this.selectionStart)));
					this.selection = false;
					this.setTextSelection(0, 0);
					this.cursorPos = Math.min(this.cursorPos, this.selectionStart);
					this.setCursorPos();
				}
			}
			else if(eventKey == Keyboard.KEY_DELETE)
			{
				if(this.getText().length() >= 1 && this.cursorPos != this.getText().length() && !this.selection)
				{
					String text = this.getText();
					this.setText(text.substring(0,this.cursorPos)+text.substring(this.cursorPos+1));
					this.setCursorPos();
				}
				else if(this.selection)
				{
					String text = this.getText();
					this.setTextSelection(0, 0);
					this.setText(text.substring(0,Math.min(this.cursorPos, this.selectionStart))+text.substring(Math.max(this.cursorPos, this.selectionStart)));
					this.selection = false;
					this.cursorPos = Math.min(this.cursorPos, this.selectionStart);
					this.setCursorPos();
				}
			}
			else if(eventKey == Keyboard.KEY_LEFT && this.cursorPos > 0 && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			{
				this.cursorPos--;
				this.setCursorPos();
				if(this.selection)
				{
					this.selection = false;
					this.setTextSelection(0, 0);
				}
				
			}
			else if(eventKey == Keyboard.KEY_LEFT && this.cursorPos > 0 && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			{
				if(!this.selection)
				{
					this.selection = true;
					this.selectionStart = this.cursorPos;
				}
				this.cursorPos--;
				this.setCursorPos();
				this.setTextSelection(this.selectionStart, this.cursorPos);
			}
			else if(eventKey == Keyboard.KEY_RIGHT && this.cursorPos < this.getText().length() && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			{
				this.cursorPos++;
				this.setCursorPos();
				if(this.selection)
				{
					this.selection = false;
					this.setTextSelection(0, 0);
				}
			}
			else if(eventKey == Keyboard.KEY_RIGHT && this.cursorPos < this.getText().length() && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			{
				if(!this.selection)
				{
					this.selection = true;
					this.selectionStart = this.cursorPos;
				}
				this.cursorPos++;
				this.setCursorPos();
				this.setTextSelection(this.selectionStart, this.cursorPos);
			}
			else if(eventKey == Keyboard.KEY_UP && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			{
				String text = this.getText();
				this.cursorPos = this.getUpPos(text, this.cursorPos);					
				this.setCursorPos();
				if(this.selection)
				{
					this.selection = false;
					this.setTextSelection(0, 0);
				}
			}
			else if(eventKey == Keyboard.KEY_UP && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			{
				String text = this.getText();
				int npos = this.getUpPos(text, this.cursorPos);	
				if(!this.selection && npos != this.cursorPos)
				{
					this.selection = true;
					this.selectionStart = this.cursorPos;
				}
				
				this.cursorPos = npos;			
				this.setCursorPos();
				this.setTextSelection(this.selectionStart, this.cursorPos);
			}
			else if(eventKey == Keyboard.KEY_DOWN && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			{
				String text = this.getText();
				this.cursorPos = this.getDownPos(text, this.cursorPos);	
				this.setCursorPos();
				if(this.selection)
				{
					this.selection = false;
					this.setTextSelection(0, 0);
				}
			}
			else if(eventKey == Keyboard.KEY_DOWN && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			{
				String text = this.getText();
				int npos = this.getDownPos(text, this.cursorPos);	
				if(!this.selection && npos != this.cursorPos)
				{
					this.selection = true;
					this.selectionStart = this.cursorPos;
				}
				
				this.cursorPos = npos;			
				this.setCursorPos();
				this.setTextSelection(this.selectionStart, this.cursorPos);
			}
			else if(eventKey == Keyboard.KEY_END && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			{
				String text = this.getText();
				this.cursorPos = text.length();	
				this.setCursorPos();
				if(this.selection)
				{
					this.selection = false;
					this.setTextSelection(0, 0);
				}
			}
			else if(eventKey == Keyboard.KEY_END && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			{
				String text = this.getText();
				int npos = text.length();	
				if(!this.selection && npos != this.cursorPos)
				{
					this.selection = true;
					this.selectionStart = this.cursorPos;
				}
				
				this.cursorPos = npos;			
				this.setCursorPos();
				this.setTextSelection(this.selectionStart, this.cursorPos);
			}
			else if(eventKey == Keyboard.KEY_HOME && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			{
				this.cursorPos = 0;	
				this.setCursorPos();
				if(this.selection)
				{
					this.selection = false;
					this.setTextSelection(0, 0);
				}
			}
			else if(eventKey == Keyboard.KEY_HOME && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			{
				String text = this.getText();
				int npos = 0;	
				if(!this.selection && npos != this.cursorPos)
				{
					this.selection = true;
					this.selectionStart = this.cursorPos;
				}
				
				this.cursorPos = npos;			
				this.setCursorPos();
				this.setTextSelection(this.selectionStart, this.cursorPos);
			}
			else if(eventKey == Keyboard.KEY_TAB && this.isTooltipShown() && this.commandMode)
			{
				if(!this.getTooltip().getText(0).split(" ")[0].equals(""))
				{
					this.setText(this.getTooltip().getText(0).split(" ")[0]);
					this.cursorPos = this.getText().length();
					this.setCursorPos();
				}
			}
		}
		
		if(!this.getText().equals("") && this.tTGen != null)
		{
			String[] nc = this.tTGen.generateToolTip(this.getText());
			
			if(nc != null && nc.length != 0)
			{
				this.getTooltip().setContent(nc);
				this.manualShowToolTip(this.getPixelX(), this.getPixelY()+this.getPixelHeight());
			}
			else
			{
				this.manualHideToolTip();
			}
		}
		else
		{
			this.manualHideToolTip();
		}
	}
	
	@Override
	protected void onClick(int x, int y, MouseButtons button, boolean grabbed, int grabx, int graby)
	{
		
		Logger.debug("clicked ("+x+"/"+y+") grabbed: "+grabbed+" / lshift: "+Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)+" / selection: "+this.selection,"TextBox.clicked");
		if(button == MouseButtons.LEFT && !grabbed && !(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)))
		{
			int index = this.getIndexByPosition(x+this.getXsb().getScrollValue(), y+this.getYsb().getScrollValue());
			
			if(index != -1)
			{
				this.cursorPos = index;
				this.setCursorPos();
			}
			
			if(this.selection)
			{
				this.selection = false;
				this.setTextSelection(0,0);
			}
		}
		else if(button == MouseButtons.LEFT && grabbed)
		{
			int index = this.getIndexByPosition(x+this.getXsb().getScrollValue(), y+this.getYsb().getScrollValue());		
			if(!this.selection)
			{
				int sindex = this.getIndexByPosition(grabx+this.getXsb().getScrollValue(), graby+this.getYsb().getScrollValue());
				this.selection = true;
				this.selectionStart = sindex;
			}
			this.setTextSelection(this.selectionStart, this.cursorPos);
			this.cursorPos = index;
			this.setCursorPos();
		}
		else if(button == MouseButtons.LEFT && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && this.selection)
		{
			int index = this.getIndexByPosition(x+this.getXsb().getScrollValue(), y+this.getYsb().getScrollValue());
			this.cursorPos = index;
			this.setCursorPos();
			this.setTextSelection(this.selectionStart, this.cursorPos);
		}
		else if(button == MouseButtons.LEFT && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
		{
			int index = this.getIndexByPosition(x+this.getXsb().getScrollValue(), y+this.getYsb().getScrollValue());			
			int sindex = this.cursorPos;
			this.cursorPos = index;
			this.setCursorPos();
			this.selection = true;
			this.selectionStart = sindex;
			this.setTextSelection(this.selectionStart, this.cursorPos);
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
	
	@Override
	public void onScroll(int value, boolean horizontal)
	{
		super.onScroll(value, horizontal);
		if(this.getXsb() != null && this.cursor != null) 
		{
		
			int[] tmp = this.getCursorPos(this.cursorPos);
			this.cursor.setPosition(tmp[0]-this.getXsb().getScrollValue(), tmp[1]-this.getYsb().getScrollValue());
		}
	}


	public IToolTipGenerator gettTGen() {
		return tTGen;
	}


	public void settTGen(IToolTipGenerator tTGen) {
		this.tTGen = tTGen;
	}
}

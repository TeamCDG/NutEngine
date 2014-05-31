package cdg.nut.gui;

import java.util.ArrayList;
import java.util.List;

import cdg.nut.logging.Logger;
import cdg.nut.util.BitmapFont;
import cdg.nut.util.Utility;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLFont;
import cdg.nut.util.gl.GLPolygon;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.util.settings.Settings;

public class ToolTip extends GLPolygon {

	private List<GLFont> content;
	
	public ToolTip(String text)
	{
		this(0, 0,
				SetKeys.GUI_CMP_FONT.getValue(BitmapFont.class).measureDimensionsAsInt(
					text,
					SetKeys.GUI_CMP_FONT_SIZE.getValue(Float.class)
				),
				new int[]{
					Settings.get(SetKeys.GUI_CMP_FONT_PADDING, Integer.class) +
					Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class),
					Settings.get(SetKeys.GUI_CMP_FONT_PADDING, Integer.class) +
					Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class)
				}
			);
		
		this.content = new ArrayList<GLFont>();
		if(!text.equals("")) this.content.add(new GLFont(0, 0, text));
	}
	
	public ToolTip(String[] content)
	{
		this(0, 0,
				SetKeys.GUI_CMP_FONT.getValue(BitmapFont.class).measureDimensionsAsInt(
					content[0],
					SetKeys.GUI_CMP_FONT_SIZE.getValue(Float.class)
				),
				new int[]{
					Settings.get(SetKeys.GUI_CMP_FONT_PADDING, Integer.class) +
					Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class),
					Settings.get(SetKeys.GUI_CMP_FONT_PADDING, Integer.class) +
					Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class)
				}
			);
		
		this.content = new ArrayList<GLFont>();
		this.setContent(content);
		
	}
	
	private ToolTip(int x, int y, int[] dim, int[] add)
	{		
		 super(x,y,dim[0] + 2 * add[0], dim[1] + 2 * add[1],0,false);
		 this.setColor(Settings.get(SetKeys.GUI_TOOLTIP_BACKGROUND_COLOR, GLColor.class));
	}
	
	
	public String[] getContent()
	{
		String[] r = new String[this.content.size()];
		
		for(int i = 0; i < this.content.size(); i++)
		{
			r[i] = this.content.get(i).getText();
		}
		
		return r;
	}
	
	public void setContent(String[] con)
	{
		
		this.content.clear();
		
		int pad = Settings.get(SetKeys.GUI_CMP_FONT_PADDING, Integer.class) + Settings.get(SetKeys.GUI_CMP_BORDER_SIZE, Integer.class);
		int yoff = pad;
		int maxx = 0;
		int maxh = Settings.get(SetKeys.WIN_HEIGHT, Integer.class) - this.getPixelY();
		
		for(int i = 0; i < con.length; i++)
		{
			
			if(con[i] == null || con[i].equals(""))
				continue;
			
			GLFont fo = new GLFont(this.getPixelX()+pad, this.getPixelY()+yoff, con[i]);
			fo.setFontSize(Settings.get(SetKeys.GUI_TOOLTIP_FONT_SIZE, Float.class));
			
			
			yoff += fo.getPixelHeight();
			
			
			if(yoff+fo.getPixelHeight()+pad < maxh || con.length == 1)
			{
				this.content.add(fo);
				if(fo.getPixelWidth()+2*pad > maxx)
					maxx = fo.getPixelWidth()+2*pad;
			}
			else 
				this.content.add(new GLFont(this.getPixelX()+pad, this.getPixelY()+yoff-fo.getPixelHeight(), "..."));
		}
		
		this.setDimension(maxx, yoff+pad);
	}
	
	public String getText(int id)
	{
		try
		{
			return this.content.get(id).getText();
		}
		catch(Exception e)
		{
			Logger.log(e, "ToolTip.getText");
			return null;
		}
	}
	
	public void setText(String text)
	{
		this.setContent(new String[]{text});
	}
	
	int br = 0;
	@Override
	protected void move(float x, float y)
	{
		int tmp[] = Utility.glToPixel(x, y);
		for(int i = 0; i < this.content.size(); i++)
		{			
			int xoff = this.content.get(i).getPixelX()-this.getPixelX();
			int yoff = this.content.get(i).getPixelY()-this.getPixelY();
			this.content.get(i).setPosition(tmp[0]+xoff,  tmp[1]+yoff);
			Logger.debug("movedx: "+(tmp[0]+xoff)+" / movedy: "+(tmp[1]+yoff)+" / px: "+this.getPixelX()+" / py: "+this.getPixelY(),"ToolTip.move");
		}
		
		super.move(x, y);
		
		Logger.debug("nx: "+this.getPixelX()+" / ny: "+this.getPixelY());
		
		//super.setupGL(Utility.generateQuadData(this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.getColor()), Utility.createQuadIndicesByte(4));
		
		//this.setContent(this.getContent());
	}
	
	@Override
	protected void drawChildren(boolean selection)
	{
		super.drawChildren(selection);
		if(!selection)
		{
			for(int i = 0; i < this.content.size(); i++)
			{
				this.content.get(i).draw();
			}
		}
	}
	
}

package cdg.nut.util.game;

import com.google.gson.JsonObject;

import cdg.nut.interfaces.IGuiObject;
import cdg.nut.util.Matrix4x4;
import cdg.nut.util.Utility;
import cdg.nut.util.Vertex4;
import cdg.nut.util.enums.Colors;
import cdg.nut.util.enums.MatrixTypes;
import cdg.nut.util.enums.MouseButtons;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLPolygon;
import cdg.nut.util.gl.GLTexture;
import cdg.nut.util.settings.SetKeys;

public class Tile extends GLPolygon implements IGuiObject{

	private boolean occupied;
	private int entityId;
	private transient World parent;
	
	private transient GLColor occupiedColor =  SetKeys.CL_TILE_OCCUPIED_COLOR.getValue(GLColor.class);
	private transient GLColor freeColor =  SetKeys.CL_TILE_FREE_COLOR.getValue(GLColor.class);
	private transient GLColor normalColor =  SetKeys.CL_TILE_NORMAL_COLOR.getValue(GLColor.class);
	private transient GLColor selectedColor =  SetKeys.CL_TILE_SELECTED_COLOR.getValue(GLColor.class);
	
	private transient boolean visible = true;
	private transient float oldCamX = 1337.0f;
	private transient float oldCamY = 1337.0f;
	private transient float oldCamScale = 1337.0f;
	private transient float oldCamRotation = 1337.0f;
	
	private int tx;
	private int ty;	
	
	@Override
	public void deserialize(JsonObject json)
	{
		super.deserialize(json);
		
		this.occupied = json.get("occupied").getAsBoolean();
		this.entityId = json.get("entityId").getAsInt();
		this.tx = json.get("tx").getAsInt();
		this.ty = json.get("ty").getAsInt();
		
		this.setShader(Entity2D.DEFAULT_SHADER);
	}
	
	public Tile() {}
	
	public Tile(float x, float y, int id, int w, int h)
	{
		this(x,y,id,w,h, null);
	}
	
	protected Tile(float x, float y, int id, int w, int h, GLTexture tex)
	{
		super(x, y, 0.1f, 0.1f);	
		super.setId(id);
		this.setColor(this.normalColor);
		this.setShader(Entity2D.DEFAULT_SHADER);
		
		this.setImage(tex);
		
		tx = w;
		ty = h;
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {		
		if(occupied)
			this.setColor(this.occupiedColor);
		else
			this.setColor(this.freeColor);
		
		
		this.occupied = occupied;
	}

	public int getEntityId() {
		return entityId;
	}

	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}
	
	@Override
	public void passUniforms()
	{
		//this.getShader().pass1i("noTexture", 1);
		//this.getShader().passVertex4("clippingArea", this.isClipping()&&this.getClippingArea()!=null?this.getClippingArea():new Vertex4(0.0f,0.0f,0.0f,0.0f));
		

		
		//this.getShader().passMatrix(SetKeys.WIN_MATRIX.getValue(Matrix4x4.class), MatrixTypes.WINDOW);
		//if(this.parent != null) this.getShader().passMatrix(this.parent.getCamera().getMatrix(), MatrixTypes.CAMERA);
		//if(this.parent != null) this.getShader().passMatrix(this.parent.getCamera().getRotMatrix(), MatrixTypes.CAMERA_ROTATION);
	}
	
	@Override
	public void selected()
	{
		super.setSelected(true);
		this.setColor(this.selectedColor);
	}
	
	@Override
	public void unselected()
	{
		super.setSelected(false);
		this.setOccupied(this.occupied);
	}

	@Override
	public void drawSelection() {
		
		this.getShader().bind();
		//this.getShader().pass1i("noTexture", 1);
		
		this.getShader().passVertex4("clippingArea", this.isClipping()&&this.getClippingArea()!=null?this.getClippingArea():Vertex4.ORIGIN);
		
		this.getShader().passMatrix(Matrix4x4.IDENTITY, MatrixTypes.TRANSLATION);
		this.getShader().passMatrix(Matrix4x4.IDENTITY, MatrixTypes.ROTATION);
		this.getShader().passMatrix(Matrix4x4.IDENTITY, MatrixTypes.SCALING);
		
		this.getShader().passMatrix(SetKeys.WIN_MATRIX.getValue(Matrix4x4.class), MatrixTypes.WINDOW);
		this.draw(true);
		
	}
	
	@Override
	public void draw(boolean selection)
	{
		if(this.parent == null)
		{
			super.draw(selection); 
			return;
		}
		
		if(this.oldCamX == this.parent.getCamera().getXmove() && 
		   this.oldCamY == this.parent.getCamera().getYmove() && 
		   this.oldCamScale == this.parent.getCamera().getScale() && 
		   this.oldCamRotation == this.parent.getCamera().getRotation())
		{
			if(this.visible)
			{
				
				
				if(this.parent.isRenderGridOccupiedView() && !this.isSelected())
					this.setOccupied(this.occupied);
				else if ((this.parent.isRenderGridOccupiedView() || this.parent.isForceSelectionRender()) && this.isSelected())
					this.setColor(this.selectedColor);
				else
					this.setColor(this.normalColor);
				
				super.draw(selection);
				this.parent.setDrawnTiles(this.parent.getDrawnTiles() + 1);
			}
			else
			{
				this.parent.setSkippedTiles(this.parent.getSkippedTiles() + 1);
			}
		}
		else
		{
		
			float tx = (this.parent.getCamera().getXmove() + this.getX() * this.parent.getCamera().getScale()) * (1.0f/SetKeys.WIN_ASPECT_RATIO.getValue(Float.class));
			float ty = (this.parent.getCamera().getYmove() + this.getY() * this.parent.getCamera().getScale()) ;
				
			float s = 0.1f * this.parent.getCamera().getScale();
			float sx = s * (SetKeys.WIN_ASPECT_RATIO.getValue(Float.class));
			
			if((Utility.between((tx + 0.5f) * sx, -1.1f, 1.1f ) && 
					   Utility.between((ty + 0.5f) * s, -1.1f, 1.1f)) ||
					   (Utility.between((tx - 0.5f) * sx, -1.1f, 1.1f) && 
					   Utility.between((ty - 0.5f) * s, -1.1f, 1.1f)) || 
					   (Utility.between((tx + 0.5f) * sx, -1.1f, 1.1f) && 
					   Utility.between((ty - 0.5f) * s, -1.1f, 1.1f)) ||
					   (Utility.between((tx - 0.5f) * sx, -1.1f, 1.1f) && 
					   Utility.between((ty + 0.5f) * s, -1.1f, 1.1f)))
			{
				
				
				if(this.parent.isRenderGridOccupiedView() && !this.isSelected())
					this.setOccupied(this.occupied);
				else if ((this.parent.isRenderGridOccupiedView() || this.parent.isForceSelectionRender()) && this.isSelected())
					this.setColor(this.selectedColor);
				else
					this.setColor(this.normalColor);
				
				
				super.draw(selection);
				this.parent.setDrawnTiles(this.parent.getDrawnTiles() + 1);
				this.visible = true;
			}
			else
			{
				this.parent.setSkippedTiles(this.parent.getSkippedTiles() + 1);
				this.visible = false;
			}
			
			this.oldCamX = this.parent.getCamera().getXmove();
			this.oldCamY = this.parent.getCamera().getYmove();
			this.oldCamScale = this.parent.getCamera().getScale();
			this.oldCamRotation = this.parent.getCamera().getRotation();
		}
	}

	@Override
	public void clicked(int x, int i, MouseButtons left, boolean b,
			boolean deltaMouseGrabbed, int mouseGrabSX, int mouseGrabSY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setActive(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean key(int eventKey, char eventCharacter) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setParent(World parent) {
		this.parent = parent;
		
	}
	
	public int getTX()
	{
		return this.tx;
		//return (int) Math.abs(this.getX() / 0.1f);
	}
	
	public int getTY()
	{
		return this.ty;
		//return (int) Math.abs(this.getY() / 0.1f) -1;
	}

	public GLColor getOccupiedColor() {
		return occupiedColor;
	}

	public void setOccupiedColor(GLColor occupiedColor) {
		this.occupiedColor = occupiedColor;
	}

	public GLColor getFreeColor() {
		return freeColor;
	}

	public void setFreeColor(GLColor freeColor) {
		this.freeColor = freeColor;
	}

	public GLColor getNormalColor() {
		return normalColor;
	}

	public void setNormalColor(GLColor normalColor) {
		this.normalColor = normalColor;
	}

	public GLColor getSelectedColor() {
		return selectedColor;
	}

	public void setSelectedColor(GLColor selectedColor) {
		this.selectedColor = selectedColor;
	}
}

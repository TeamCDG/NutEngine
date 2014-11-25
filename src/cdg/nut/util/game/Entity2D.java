package cdg.nut.util.game;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.google.gson.JsonObject;

import cdg.nut.interfaces.IEntity;
import cdg.nut.interfaces.IGuiObject;
import cdg.nut.logging.Logger;
import cdg.nut.util.Matrix4x4;
import cdg.nut.util.ShaderProgram;
import cdg.nut.util.Utility;
import cdg.nut.util.enums.MatrixTypes;
import cdg.nut.util.enums.MouseButtons;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.gl.GLPolygon;
import cdg.nut.util.gl.GLTexture;
import cdg.nut.util.net.NetUpdates;
import cdg.nut.util.net.NetUtils;
import cdg.nut.util.net.Package;
import cdg.nut.util.net.UpdatePackage;
import cdg.nut.util.settings.SetKeys;

public abstract class Entity2D extends GLPolygon implements IEntity{
	
	private transient GLTexture primary;
	
	private transient UpdatePackage update;
	
	private transient GLTexture layer1;
	private transient GLTexture layer2;
	private transient GLTexture layer3;
	private transient GLTexture layer4;
	private transient GLTexture layer5;
	private transient GLTexture layer6;
	
	private transient World parent;
	
	private float rotation;
	
	private transient Matrix4x4 translationMat = Matrix4x4.getIdentity();
	private transient Matrix4x4 rotationMat = Matrix4x4.getIdentity();;
	private transient Matrix4x4 scalingMat = Matrix4x4.getIdentity();;
	
	public static ShaderProgram DEFAULT_SHADER = new ShaderProgram("entity2d.vert", "entity2d.frag");
	
	private transient boolean active = false;
	private transient boolean visible = true;
	private transient float oldCamX;
	private transient float oldCamY;
	private transient float oldCamScale;
	private transient float oldCamRotation;
	
	public Entity2D() {}
	
	public Entity2D(float x, float y, float width, float height, boolean noGL)
	{
		super(0.0f, 0.0f, width, height, true, noGL);
		this.move(x, y);
		//this.setColor(new GLColor(1.0f, 1.0f, 1.0f, 1.0f));
	}
	
	public Entity2D(float x, float y, float width, float height)
	{
		
		super(0.0f, 0.0f, width, height, true);
		this.move(x, y);
		this.setShader(Entity2D.DEFAULT_SHADER);
		//this.setColor(new GLColor(1.0f, 1.0f, 1.0f, 1.0f));
	}
	
	@Override
	public int setId(int id)
	{
		this.update = new UpdatePackage(id);
		return super.setId(id);
	}
	
	@Override
	public void move(float x, float y)
	{
		
		this.translationMat.set(1.0f, 0.0f, 0.0f, 0.0f,
				 0.0f, 1.0f, 0.0f, 0.0f,
				 0.0f, 0.0f, 1.0f, 0.0f,
				 x, y, 0.0f, 1.0f);
		
		if(this.update != null)
			this.update.addData(NetUpdates.POSITION_UPDATE, new float[]{x,y});
		
		this.x(x);
		this.y(y);
	}

	/**
	 * @return the update
	 */
	public UpdatePackage getUpdate() {
		return update;
	}

	public void moveAdd(float x, float y)
	{
		this.move(this.getX()+x, this.getY()+y);
	}
	
	@Override
	public void setPosition(float x, float y)
	{
		this.move(x, y);
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
				super.draw(selection);
				this.parent.setDrawnEntities(this.parent.getDrawnEntities() + 1);
			}
			else
			{
				this.parent.setSkippedEntities(this.parent.getSkippedEntities() + 1);
			}
		}
		else
		{
		
			float tx = (this.parent.getCamera().getXmove() + this.getX() * this.parent.getCamera().getScale()) * (1.0f/SetKeys.WIN_ASPECT_RATIO.getValue(Float.class));
			float ty = (this.parent.getCamera().getYmove() + this.getY() * this.parent.getCamera().getScale()) ;
				
			float s = 0.1f * this.parent.getCamera().getScale();
			float sx = s * (SetKeys.WIN_ASPECT_RATIO.getValue(Float.class));
			
			if((Utility.between(tx + 0.5f * sx, -1.1f, 1.1f ) && 
					   Utility.between(ty + 0.5f * s, -1.1f, 1.1f)) ||
					   (Utility.between(tx - 0.5f * sx, -1.1f, 1.1f) && 
					   Utility.between(ty - 0.5f * s, -1.1f, 1.1f)) || 
					   (Utility.between(tx + 0.5f * sx, -1.1f, 1.1f) && 
					   Utility.between(ty - 0.5f * s, -1.1f, 1.1f)) ||
					   (Utility.between(tx - 0.5f * sx, -1.1f, 1.1f) && 
					   Utility.between(ty + 0.5f * s, -1.1f, 1.1f)))
			{
				super.draw(selection);
				this.parent.setDrawnEntities(this.parent.getDrawnEntities() + 1);
				this.visible = true;
			}
			else
			{
				this.parent.setSkippedEntities(this.parent.getSkippedEntities() + 1);
				this.visible = false;
			}
			
			this.oldCamX = this.parent.getCamera().getXmove();
			this.oldCamY = this.parent.getCamera().getYmove();
			this.oldCamScale = this.parent.getCamera().getScale();
			this.oldCamRotation = this.parent.getCamera().getRotation();
		}
	}
	
	@Override
	public void bindTextures()
	{
		if(this.primary != null) this.primary.bind();
		if(this.layer1 != null) this.layer1.bind();
		if(this.layer2 != null) this.layer2.bind();
		if(this.layer3 != null) this.layer3.bind();
		if(this.layer4 != null) this.layer4.bind();
		if(this.layer5 != null) this.layer5.bind();
		if(this.layer6 != null) this.layer6.bind();
	}
	
	@Override
	public void unbindTextures()
	{
		if(this.primary != null) this.primary.unbind();
		if(this.layer1 != null) this.layer1.unbind();
		if(this.layer2 != null) this.layer2.unbind();
		if(this.layer3 != null) this.layer3.unbind();
		if(this.layer4 != null) this.layer4.unbind();
		if(this.layer5 != null) this.layer5.unbind();
		if(this.layer6 != null) this.layer6.unbind();
	}
	
	@Override
	protected void passUniforms()
	{
		if(this.getShader() != null)
		{
			this.getShader().pass1i("noTexture", 0);
			
			this.getShader().passMatrix(this.translationMat, MatrixTypes.TRANSLATION);
			this.getShader().passMatrix(this.rotationMat, MatrixTypes.ROTATION);
			this.getShader().passMatrix(this.scalingMat, MatrixTypes.SCALING);
			this.getShader().passMatrix(SetKeys.WIN_MATRIX.getValue(Matrix4x4.class), MatrixTypes.WINDOW);
			if(this.parent != null) this.getShader().passMatrix(this.parent.getCamera().getMatrix(), MatrixTypes.CAMERA);
			if(this.parent != null) this.getShader().passMatrix(this.parent.getCamera().getRotMatrix(), MatrixTypes.CAMERA_ROTATION);
			//Logger.spam("passed");
		}
		
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation % 360.0f;
		
		if(this.rotation < 0)
			this.rotation = 360.0f + this.rotation;
		
		this.rotationMat.set((float) Math.cos(Utility.rad(this.rotation)), (float) Math.sin(Utility.rad(this.rotation))*-1.0f, 0.0f, 0.0f,
											(float) Math.sin(Utility.rad(this.rotation)), 		 (float) Math.cos(Utility.rad(this.rotation)), 0.0f, 0.0f,
																						 0.0f, 													  0.0f, 1.0f, 0.0f,
																						 0.0f, 													  0.0f, 0.0f, 1.0f);
		
		if(this.update != null)
			this.update.addData(NetUpdates.ROTATION_UPDATE, this.rotation);
	}
	
	@Override
	public void deserialize(JsonObject json)
	{
		float x = json.get("x").getAsFloat();
		float y = json.get("y").getAsFloat();
		json.addProperty("x", 0.0f);
		json.addProperty("y", 0.0f);
		
		super.deserialize(json);
		
		
		this.move(x, y);
		
		float rot = json.get("rotation").getAsFloat();		
		this.setRotation(rot);
		this.setShader(Entity2D.DEFAULT_SHADER);		
	}
	
	public boolean onTick()
	{
		return false;
	}

	@Override
	public void drawSelection() {
		this.draw(true);
	}

	@Override
	public void unselected() {
		this.setSelected(false);
	}

	@Override
	public void selected() {
		this.setSelected(true);
		
	}

	@Override
	public void clicked(int x, int i, MouseButtons left, boolean b,
			boolean deltaMouseGrabbed, int mouseGrabSX, int mouseGrabSY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setActive(boolean b) {
		this.active = b;
	}
	
	
	public boolean isActive() {
		return this.active;
	}

	@Override
	public boolean key(int eventKey, char eventCharacter) {
		return false;
	}

	public World getParent() {
		return parent;
	}

	public void setParent(World parent) {
		this.parent = parent;
	}
	
	/**
	 * @return the primary
	 */
	public GLTexture getPrimary() {
		return primary;
	}

	/**
	 * @param primary the primary to set
	 */
	public void setPrimary(GLTexture primary) {
		this.primary = primary;
	}

	/**
	 * @return the layer1
	 */
	public GLTexture getLayer1() {
		return layer1;
	}

	/**
	 * @param layer1 the layer1 to set
	 */
	public void setLayer1(GLTexture layer1) {
		this.layer1 = layer1;
	}

	/**
	 * @return the layer2
	 */
	public GLTexture getLayer2() {
		return layer2;
	}

	/**
	 * @param layer2 the layer2 to set
	 */
	public void setLayer2(GLTexture layer2) {
		this.layer2 = layer2;
	}

	/**
	 * @return the layer3
	 */
	public GLTexture getLayer3() {
		return layer3;
	}

	/**
	 * @param layer3 the layer3 to set
	 */
	public void setLayer3(GLTexture layer3) {
		this.layer3 = layer3;
	}

	/**
	 * @return the layer4
	 */
	public GLTexture getLayer4() {
		return layer4;
	}

	/**
	 * @param layer4 the layer4 to set
	 */
	public void setLayer4(GLTexture layer4) {
		this.layer4 = layer4;
	}

	/**
	 * @return the layer5
	 */
	public GLTexture getLayer5() {
		return layer5;
	}

	/**
	 * @param layer5 the layer5 to set
	 */
	public void setLayer5(GLTexture layer5) {
		this.layer5 = layer5;
	}

	/**
	 * @return the layer6
	 */
	public GLTexture getLayer6() {
		return layer6;
	}

	/**
	 * @param layer6 the layer6 to set
	 */
	public void setLayer6(GLTexture layer6) {
		this.layer6 = layer6;
	}

	@Override
	public void rightClickhappened(IEntity entity, float worldX, float worldY) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void packageRecieved(cdg.nut.util.net.Package p)
	{
		switch(p.getUsage())
		{
			case NetUpdates.POSITION_UPDATE:
				this.posUpdate(p);
				break;
			case NetUpdates.ROTATION_UPDATE:
				this.rotUpdate(p);
				break;
			case NetUpdates.MOUSECLICK:
				this.mouseDown(p);
				break;
		}
	}
	
	private void mouseDown(Package p) {
		//0: byte mouse button
		//1-4: int enttity id hit
		//5-9: float wx
		//10-13: float wy
		
		byte[] data = p.getData();
		byte mbutton = data[0];
		int eid = NetUtils.toInt(NetUtils.subArray(1, 4, data));
		float wx = NetUtils.toFloat(NetUtils.subArray(5, 4, data));
		float wy = NetUtils.toFloat(NetUtils.subArray(5, 4, data));
		
		IEntity e = null;
		if(this.parent != null)
			e = this.parent.get(eid);
		
		if(mbutton == MouseButtons.RIGHT.getKey())
			this.rightClickhappened(e, wx, wy);
	}

	private void posUpdate(cdg.nut.util.net.Package p)
	{
		byte[] data = p.getData();
		float x = NetUtils.toFloat(NetUtils.subArray(0, 4, data));
		float y = NetUtils.toFloat(NetUtils.subArray(4, 4, data));
		
		this.setPosition(x, y);
	}
	
	private void rotUpdate(cdg.nut.util.net.Package p)
	{
		byte[] data = p.getData();
		float r = NetUtils.toFloat(NetUtils.subArray(0, 4, data));
		
		this.setRotation(r);
	}


}

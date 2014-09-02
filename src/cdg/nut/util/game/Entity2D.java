package cdg.nut.util.game;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

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
import cdg.nut.util.settings.SetKeys;

public class Entity2D extends GLPolygon implements IEntity{
	
	private GLTexture primary;
	

	private GLTexture layer1;
	private GLTexture layer2;
	private GLTexture layer3;
	private GLTexture layer4;
	private GLTexture layer5;
	private GLTexture layer6;
	
	private World parent;
	
	private float rotation;
	
	private Matrix4x4 translationMat = Matrix4x4.getIdentity();
	private Matrix4x4 rotationMat = Matrix4x4.getIdentity();;
	private Matrix4x4 scalingMat = Matrix4x4.getIdentity();;
	
	public static ShaderProgram DEFAULT_SHADER = new ShaderProgram("entity2d.vert", "entity2d.frag");
	
	private boolean active = false;
	private boolean visible = true;
	private float oldCamX;
	private float oldCamY;
	private float oldCamScale;
	private float oldCamRotation;
	
	public Entity2D(float x, float y, float width, float height)
	{
		super(0.0f, 0.0f, width, height, true);
		this.move(x, y);
		this.setShader(Entity2D.DEFAULT_SHADER);
		//this.setColor(new GLColor(1.0f, 1.0f, 1.0f, 1.0f));
	}
	
	@Override
	public void move(float x, float y)
	{
		
		this.translationMat.set(1.0f, 0.0f, 0.0f, 0.0f,
				 0.0f, 1.0f, 0.0f, 0.0f,
				 0.0f, 0.0f, 1.0f, 0.0f,
				 x, y, 0.0f, 1.0f);
		
		this.x(x);
		this.y(y);
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
	public void passUniforms()
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
	}
	
	public void onTick()
	{
		
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
}

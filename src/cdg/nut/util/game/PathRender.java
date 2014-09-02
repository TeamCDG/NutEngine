package cdg.nut.util.game;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import cdg.nut.util.Matrix4x4;
import cdg.nut.util.Vertex4;
import cdg.nut.util.VertexData;
import cdg.nut.util.enums.MatrixTypes;
import cdg.nut.util.gl.GLPolygon;
import cdg.nut.util.settings.SetKeys;
import cdg.nut.interfaces.IPolygonGenerator;
import cdg.nut.interfaces.IVertex;

public class PathRender extends GLPolygon implements IPolygonGenerator {

	private List<IVertex> wayPoints;
	private Camera cam;
	
	
	public List<IVertex> getWayPoints() {
		return wayPoints;
	}

	public void setWayPoints(List<IVertex> wayPoints) {
		this.wayPoints = wayPoints;
		if(this.wayPoints != null) this.regen(); // cause bitches love rain
	}
	
	public PathRender(List<IVertex> wayPoints)
	{
		this.wayPoints = wayPoints;
		this.setGen(this);
		this.setClipping(false);
		this.setShader(Entity2D.DEFAULT_SHADER);
	}
	
	@Override
	public VertexData[] generateData(float x, float y, float w, float h) {
				
		if(this.wayPoints == null) return null;
		
		VertexData[] data = new VertexData[this.wayPoints.size()];
		
		for(int i = 0; i < data.length; i++)
		{
			data[i] = new VertexData(this.wayPoints.get(i).getX(), this.wayPoints.get(i).getY(), 0.0f, 1.0f);
		}
		
		return data;
	}

	@Override
	public int[] generateIndicies() {
				
		if(this.wayPoints == null) return null;
		
		int[] lineIndicies = new int[this.wayPoints.size()];
		
		for(int i = 0; i < this.wayPoints.size(); i++)
		{
			lineIndicies[i] = i;
		}
				
		return lineIndicies;
	}
	
	@Override
	protected void draw(boolean selection)
	{
		
		this.getShader().bind();
		this.getShader().pass1i("noTexture", 1);
		this.getShader().passVertex4("clippingArea", this.isClipping()&&this.getClippingArea()!=null?this.getClippingArea():new Vertex4(0.0f,0.0f,0.0f,0.0f));
		
		this.getShader().passMatrix(Matrix4x4.getIdentity(), MatrixTypes.TRANSLATION);
		this.getShader().passMatrix(Matrix4x4.getIdentity(), MatrixTypes.ROTATION);
		this.getShader().passMatrix(Matrix4x4.getIdentity(), MatrixTypes.SCALING);
		
		this.getShader().passMatrix(SetKeys.WIN_MATRIX.getValue(Matrix4x4.class), MatrixTypes.WINDOW);
		if(this.cam != null) this.getShader().passMatrix(this.cam.getMatrix(), MatrixTypes.CAMERA);
		if(this.cam != null) this.getShader().passMatrix(this.cam.getRotMatrix(), MatrixTypes.CAMERA_ROTATION);
		
		
		if(this.getVAO() == -1 || this.isHidden()|| (selection&&!this.isSelectable()))
			return;
		
		this.drawing = true;
		
		
		if(!selection)
		{
			this.bindTextures();
			if(this.getImage() != null) this.getImage().bind();
		}
		
		
		
		this.getShader().bind();
		
		GL30.glBindVertexArray(this.getVAO()); //point the pointers to the right point.
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
				
		//tell the GPU where to find information about drawing this GLObject
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.getIVBO());
		
		
		this.passUniforms(); 

		
		if(this.getColor() != null) this.getShader().passColor("color", this.getColor());
		//this.mainShader.pass4f("visible_Area",this.visibleArea.getX(),this.visibleArea.getY(),this.visibleArea.getZ(),this.visibleArea.getW());
		// Draw the vertices
		GL11.glDrawElements(GL11.GL_LINE_STRIP, this.getICount(), GL11.GL_UNSIGNED_INT, 0); //finallay draw
		
		
		GL11.glPointSize(5f);
		GL11.glDrawElements(GL11.GL_POINTS, this.getICount(), GL11.GL_UNSIGNED_INT, 0); //finallay draw
				
		//reset everything (undpoint pointing pointers... ;) )
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0); 
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		
		
		if(!selection)
		{
			this.unbindTextures();
			if(this.getImage() != null) this.getImage().unbind();
		}
		
		this.getShader().unbind();
		
		this.drawChildren(selection);
		
		this.drawing = false;
		
		//Logger.debug("Line count: "+(this.width + this.height + 2));
		//Logger.debug("Line in: "+Arrays.toString(this.generateIndicies()));
	}

	public Camera getCam() {
		return cam;
	}

	public void setCam(Camera cam) {
		this.cam = cam;
	}

}

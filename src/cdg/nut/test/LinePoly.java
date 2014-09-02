package cdg.nut.test;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import cdg.nut.interfaces.IPolygonGenerator;
import cdg.nut.util.Vertex4;
import cdg.nut.util.VertexData;
import cdg.nut.util.gl.GLPolygon;

public class LinePoly extends GLPolygon implements IPolygonGenerator  {
	
	public LinePoly()
	{
		this.setGen(this);
	}
	
	@Override
	public VertexData[] generateData(float x, float y, float w, float h) {
		
		VertexData[] data = new VertexData[]{new VertexData(new Vertex4(0.0f, 0.0f, 0.0f, 1.0f)), new VertexData(new Vertex4(1.0f, 0.0f, 0.0f, 1.0f))};		
		return data;
	}

	@Override
	public int[] generateIndicies() {

		return new int[]{0,1};
	}
	
	@Override
	protected void draw(boolean selection)
	{
		if(this.getVAO() == -1 || this.isHidden()|| (selection&&!this.isSelectable()))
			return;
		
		this.drawing = true;
		
		this.getShader().bind();
		
		if(!selection)
		{
			this.bindTextures();
			if(this.getImage() != null) this.getImage().bind();
		}
		
		
		
		GL30.glBindVertexArray(this.getVAO()); //point the pointers to the right point.
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
				
		//tell the GPU where to find information about drawing this GLObject
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.getIVBO());
		
		this.passUniforms(); //pass the unfiforms from the user
		this.getShader().pass1i("selection", selection?1:0);
		this.getShader().passVertex4("clippingArea", this.isClipping()&&this.getClippingArea()!=null?this.getClippingArea():new Vertex4(0.0f,0.0f,0.0f,0.0f));
		if(this.getColor() != null) this.getShader().passColor("color", this.getColor());
		//this.mainShader.pass4f("visible_Area",this.visibleArea.getX(),this.visibleArea.getY(),this.visibleArea.getZ(),this.visibleArea.getW());
		// Draw the vertices
		GL11.glDrawElements(GL11.GL_LINES, this.getICount(), GL11.GL_UNSIGNED_INT, 0); //finallay draw
				
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
	}
}

package cdg.nut.interfaces;

import cdg.nut.util.VertexData;

public interface IPolygonGenerator {
	public VertexData[] generateData(float x, float y, float width, float height);
	
	public int[] generateIndicies();
}

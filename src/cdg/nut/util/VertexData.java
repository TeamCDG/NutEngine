package cdg.nut.util;

public class VertexData {
	
	// Vertex data
	private float[] xyzw = new float[] {0f, 0f, 0f, 1f}; // Position & Normal
	private float[] rgba = new float[] {1f, 1f, 1f, 1f}; // Color & Alpha
	private float[] st = new float[] {0f, 0f}; // Texture Coordinate
	
	// The amount of bytes an element has
	public static final int ELEMENT_BYTES = 4;
	
	// Elements per parameter
	public static final int POSITION_ELEMENT_COUNT = 4;
	public static final int COLOR_ELEMENT_COUNT = 4;
	public static final int TEXTURE_ELEMENT_COUNT = 2;
	
	// Bytes per parameter
	public static final int POSITION_BYTES_COUNT = POSITION_ELEMENT_COUNT * ELEMENT_BYTES;
	public static final int COLOR_BYTE_COUNT = COLOR_ELEMENT_COUNT * ELEMENT_BYTES;
	public static final int TEXTURE_BYTE_COUNT = TEXTURE_ELEMENT_COUNT* ELEMENT_BYTES;
	
	// Byte offsets per parameter
	public static final int POSITION_BYTE_OFFSET = 0;
	public static final int COLOR_BYTE_OFFSET = POSITION_BYTE_OFFSET + POSITION_BYTES_COUNT;
	public static final int TEXTURE_BYTE_OFFSET = COLOR_BYTE_OFFSET + COLOR_BYTE_COUNT;
	
	// The amount of elements that a vertex has
	public static final int ELEMENT_COUNT = POSITION_ELEMENT_COUNT + 
			COLOR_ELEMENT_COUNT + TEXTURE_ELEMENT_COUNT;	
	
	// The size of a vertex in bytes
	public static final int STRIDE = POSITION_BYTES_COUNT + COLOR_BYTE_COUNT + TEXTURE_BYTE_COUNT;
	
	public VertexData(float[] xyzw, float[] rgba, float[] st)
	{
		this.xyzw = xyzw;
		this.rgba = rgba;
		this.st = st;
	}
	
	public VertexData(float x, float y, float z, float w, float r, float g, float b, float a, float s, float t)
	{
		this.xyzw = new float[]{x,y,z,w};
		this.rgba = new float[]{r,g,b,a};
		this.st = new float[]{s,t};
	}
	
	public VertexData(float x, float y, float z, float r, float g, float b, float a, float s, float t)
	{
		this.xyzw = new float[]{x,y,z,1.0f};
		this.rgba = new float[]{r,g,b,a};
		this.st = new float[]{s,t};
	}
	
	public VertexData(float x, float y, float z, float r, float g, float b, float s, float t)
	{
		this.xyzw = new float[]{x,y,z,1.0f};
		this.rgba = new float[]{r,g,b,1.0f};
		this.st = new float[]{s,t};
	}
	
	public VertexData(float[] xyzw, float[] rgba)
	{
		this.xyzw = xyzw;
		this.rgba = rgba;
	}
	
	public VertexData(float x, float y, float z, float w, float r, float g, float b)
	{
		this.xyzw = new float[]{x,y,z,w};
		this.rgba = new float[]{r,g,b,1.0f};
	}
	
	public VertexData(float x, float y, float z, float r, float g, float b)
	{
		this.xyzw = new float[]{x,y,z,1.0f};
		this.rgba = new float[]{r,g,b,1.0f};
	}
	
	public VertexData(float x, float y, float z, float w)
	{
		this.xyzw = new float[]{x,y,z,w};
	}
	
	public VertexData(float x, float y, float z)
	{
		this.xyzw = new float[]{x,y,z,1.0f};
	}
	
	public VertexData(float[] xyzw)
	{
		this.xyzw = xyzw;
	}
	
	public VertexData() {
		// TODO Auto-generated constructor stub
	}

	// Sets XYZ
	public void setXYZ(float x, float y, float z) {
		this.setXYZW(x, y, z, 1f);
	}
	
	// Sets RGB
	public void setRGB(float r, float g, float b) {
		this.setRGBA(r, g, b, 1f);
	}
	
	// Sets ST
	public void setST(float s, float t) {
		this.st = new float[] {s, t};
	}
	
	// Sets XYZW
	public void setXYZW(float x, float y, float z, float w) {
		this.xyzw = new float[] {x, y, z, w};
	}
	
	// Sets RGBA
	public void setRGBA(float r, float g, float b, float a) {
		this.rgba = new float[] {r, g, b, 1f};
	}
	
	// Returns float array of wohle elements	
	public float[] getElements() {
		float[] out = new float[VertexData.ELEMENT_COUNT];
		int i = 0;
		
		// XYZW elements
		out[i++] = this.xyzw[0];
		out[i++] = this.xyzw[1];
		out[i++] = this.xyzw[2];
		out[i++] = this.xyzw[3];
		
		// RGBA elements
		out[i++] = this.rgba[0];
		out[i++] = this.rgba[1];
		out[i++] = this.rgba[2];
		out[i++] = this.rgba[3];
		
		// ST elements
		out[i++] = this.st[0];
		out[i++] = this.st[1];
		
		return out;
	}
	
	// Returns XYZW
	public float[] getXYZW() {
		return new float[] {this.xyzw[0], this.xyzw[1], this.xyzw[2], this.xyzw[3]};
	}
	
	// Returns XYZ
	public float[] getXYZ() {
		return new float[] {this.xyzw[0], this.xyzw[1], this.xyzw[2]};
	}
	
	// Returns RGBA
	public float[] getRGBA() {
		return new float[] {this.rgba[0], this.rgba[1], this.rgba[2], this.rgba[3]};
	}
	
	// Returns RGB
	public float[] getRGB() {
		return new float[] {this.rgba[0], this.rgba[1], this.rgba[2]};
	}
	
	// Returns ST
	public float[] getST() {
		return new float[] {this.st[0], this.st[1]};
	}
}

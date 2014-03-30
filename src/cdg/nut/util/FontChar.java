package cdg.nut.util;

public class FontChar 
{
	private String c;
	private float x;
	private float y;
	private float width;
	private float height;
	
	public FontChar(String c, float x, float y, float width, float height)
	{
		this.setC(c);
		this.setX(x);
		this.setY(y);
		this.setWidth(width);
		this.setHeight(height);
	}
	
	public FontChar(String configLine)
	{
		String[] split = configLine.split(" ");
		String[] values = split[1].split(";");
		this.setC(""+split[0].substring(0, 1));
		this.setX(Float.valueOf(values[0]));
		this.setY(Float.valueOf(values[1]));
		this.setWidth(Float.valueOf(values[2]));
		this.setHeight(Float.valueOf(values[3]));
	}

	/**
	 * @return the c
	 */
	public String getC() {
		return c;
	}

	/**
	 * @param c the c to set
	 */
	public void setC(String c) {
		this.c = c;
	}

	/**
	 * @return the x
	 */
	public float getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public float getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * @return the width
	 */
	public float getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(float width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public float getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(float height) {
		this.height = height;
	}
}

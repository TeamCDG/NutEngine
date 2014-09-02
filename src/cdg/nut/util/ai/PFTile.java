package cdg.nut.util.ai;

public class PFTile {

	private int g;
	private int h;
	
	private int x;
	private int y;
	private PFTile previous;
	
	public PFTile(int g, int h, int x, int y)
	{
		this.g = g;
		this.h = h;
		this.x = x;
		this.y = y;
	}
	
	public PFTile(int g, int h, int x, int y, PFTile previous)
	{
		this.g = g;
		this.h = h;
		this.x = x;
		this.y = y;
		this.previous = previous;
	}
	
	public int getG() {
		return g;
	}
	public void setG(int g) {
		this.g = g;
	}
	public int getH() {
		return h;
	}
	public void setH(int h) {
		this.h = h;
	}
	
	public int getF() {
		return this.g + this.h;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}

	public PFTile getPrevious() {
		return previous;
	}

	public void setPrevious(PFTile previous) {
		this.previous = previous;
	}
	
}

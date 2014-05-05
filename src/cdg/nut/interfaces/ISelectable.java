package cdg.nut.interfaces;

public interface ISelectable extends IDrawable {

	public void drawSelect();
	
	public int setId(int id);
	
	public int getId();
	
	public void setSelected(boolean selection);
	
	public boolean isSelected();
	
	public boolean checkId(int id);
	
}

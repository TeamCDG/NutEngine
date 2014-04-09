package cdg.nut.gui;

import java.util.ArrayList;

//TODO: Javadoc
public class Container {

	private ArrayList<Component> components;

	public Container()
	{
		this.components = new ArrayList<Component>();
	}

	public void drawComponents()
	{
		for (int i = 0; i < this.components.size(); i++) {
			this.components.get(i).draw();
		}
	}

	public void drawComponentSelection()
	{
		for (int i = 0; i < this.components.size(); i++) {
			this.components.get(i).drawSelect();
		}
	}

	public void add(Component c)
	{
		this.components.add(c);
	}

	public int getComponentCount()
	{
		return this.components.size();
	}

	public void remove(int id)
	{
		for (int i = 0; i < this.components.size(); i++) {
			if (this.components.get(i).getId() == id) {
				this.components.remove(this.components.get(i));
				break;
			}
		}
	}

	public void remove(Component c)
	{
		this.components.remove(c);
	}

	public ArrayList<Component> getComponents()
	{
		return this.components;
	}

	public Component get(int id)
	{
		for (int i = 0; i < this.components.size(); i++) {
			if (this.components.get(i).getId() == id) {
				return this.components.get(i);
			}
		}

		return null;
	}
}

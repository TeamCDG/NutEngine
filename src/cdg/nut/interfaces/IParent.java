package cdg.nut.interfaces;

import java.util.List;

import cdg.nut.gui.Component;
import cdg.nut.gui.ToolTip;

public interface IParent {
	
	public void add(Component c);
	
	public void addComponent(Component c);

	public void remove(Component c);

	public void remove(int id);	
	
	public void removeComponent(Component c);

	public void removeComponent(int id);
	
	public void addToNextId(int i);
	
	public int getNextId();
	
	public Component get(int id);
	
	public Component getComponent(int id);
	
	public List<Component> getAll();
	
	public List<Component> getAllComponents();
	
	public <T extends Component> List<T> getByClass(Class<T> c);
	
	public <T extends Component> List<T> getComponentsByClass(Class<T> c);
	
	public <T extends Component> List<T> getComponents(Class<T> c);
	
	public ToolTip getActiveToolTip();
	
	public void setActiveToolTip(ToolTip t);
	
	public int getMouseGrabSX();
	
	public int getMouseGrabSY();
	
	public boolean isMouseGrabbed();

	public int getMouseY();

	public int getMouseX();

}

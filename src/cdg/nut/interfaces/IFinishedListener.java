package cdg.nut.interfaces;

import java.util.List;

public interface IFinishedListener {
	
	public void finished(int id, List<IVertex> path);
	
}

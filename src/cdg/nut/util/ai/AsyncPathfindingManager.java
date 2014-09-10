package cdg.nut.util.ai;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import cdg.nut.interfaces.IFinishedListener;
import cdg.nut.interfaces.IVertex;

public class AsyncPathfindingManager{

	private static HashMap<Integer, List<IVertex>> paths = new HashMap<Integer, List<IVertex>>();
	private static List<PathfindingObject> asthreads = new LinkedList<PathfindingObject>();
	private static ReentrantLock lock = new ReentrantLock();
	private static ReentrantLock lock2 = new ReentrantLock();
	
	public static void add(PathfindingObject obj)
	{
	
		while(lock.isLocked()) {};
		
		lock.lock();
		
		for(int i = 0; i < asthreads.size(); i++)
		{
			if(asthreads.get(i).getEid() == obj.getEid())
			{
				asthreads.get(i).setCanceled(true);
				asthreads.remove(i);
				break;
			}
		}
		
		asthreads.add(obj);
		
		lock.unlock();
		
		obj.setLis(new IFinishedListener(){

			@Override
			public void finished(int id, List<IVertex> path) {
				AsyncPathfindingManager.finished(id, path);
			}});
		obj.start();
		
		
	}
	
	public static List<IVertex> getPath(int id)
	{
		while(lock2.isLocked()) {};
		lock2.lock();
		if(paths.containsKey(id))
		{
			List<IVertex> path = paths.get(id);
			paths.remove(id);
			lock2.unlock();
			return path;
		}
		else
		{
			lock2.unlock();
			return null;
		}
	}

	public static void finished(int id, List<IVertex> path) {
		while(lock2.isLocked()) {};
		lock2.lock();
		paths.put(id, path);
		lock2.unlock();
		
	}
}

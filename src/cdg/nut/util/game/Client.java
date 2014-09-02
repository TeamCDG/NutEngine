package cdg.nut.util.game;

public class Client {

	private static boolean localHorst = false;
	
	private static World world;


	public static boolean isLocalHorst() {
		return Client.localHorst;
	}


	public static void setLocalHorst(boolean localHorst) {
		Client.localHorst = localHorst;
	}

	public static World getWorld() {
		if(!Client.localHorst)
			return Client.world;
		else
			return Server.getWorld();
	}

	public static void setWorld(World world) {
		Client.world = world;
	}
	
}

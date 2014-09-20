package cdg.nut.util.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import cdg.nut.logging.Logger;
import cdg.nut.util.game.World;

public class Client {

	private static boolean localHorst = false;
	private static Socket client;
	private static World world;
	private static boolean closeRequested = false;
	private static Thread listener;
	private static DataOutputStream out;
	private static DataInputStream in;

	public static boolean connect(String ip, int port)
	{
		try
		{
			Client.client = new Socket(ip, port);
		}
		catch(IOException e)
		{
			Logger.log(e, "", "Unable to connect to: "+ip);
		}
		return false;
	}
	
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

	public static boolean isCloseRequested() {
		return closeRequested;
	}

	public static void setCloseRequested(boolean closeRequested) {
		Client.closeRequested = closeRequested;
	}
	
}

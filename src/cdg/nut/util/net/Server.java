package cdg.nut.util.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

import cdg.nut.interfaces.IEntity;
import cdg.nut.logging.Logger;
import cdg.nut.util.game.World;

public class Server {

	public static boolean isLocal = false;
	
	private static World world;
	private static Thread tick;
	private static Thread serverThread;
	private static ServerSocket server;
	private static int maxPlayers;
	private static String name;
	private static int port = 1337;
	private static int playerCount;
	private static boolean closeRequested = false;

	
	public static void init()
	{
		Server.serverThread = new Thread(new Runnable(){

			@Override
			public void run() {
				Server.server();
				
			}});
		Server.serverThread.start();
	}
	
	private static void server()
	{
		try {
			Server.server = new ServerSocket(Server.port);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(!Server.closeRequested && Server.server != null)
		{
			try {
				Server.server.setSoTimeout(1000);
				Socket so = Server.server.accept();
			} 
			catch (SocketTimeoutException e) {
			} catch (IOException e) {
				Logger.log(e);
			}
			
		}
	}
	
	/**
	 * @return the closeRequested
	 */
	protected static boolean isCloseRequested() {
		return closeRequested;
	}

	/**
	 * @param closeRequested the closeRequested to set
	 */
	public static void setCloseRequested(boolean closeRequested) {
		Server.closeRequested = closeRequested;
	}

	public static World setWorld() {
		return world;
	}
	
	public static World getWorld() {
		return world;
	}
	
	public static void onTick()
	{
		while(!Server.closeRequested)
		{
			if(Server.world != null)
			{
				List<IEntity> e = Server.world.getEntites();
				
				for(int i = 0; i < e.size(); i++)
				{
					if(e.get(i).onTick())
					{
						//TODO update :)
					}
				}
			}
		}
	}
}

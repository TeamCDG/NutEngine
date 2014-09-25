package cdg.nut.util.net;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import cdg.nut.logging.Logger;
import cdg.nut.test.Main;
import cdg.nut.util.Engine;

public class ServerInfo {
	private String name;
	private String motd;
	private int playerCount;
	private int maxPlayerCount;
	private int ping;
	
	
	public static ServerInfo fromServer(String ip, int port)
	{
		Socket so = new Socket();
		try
		{
			so.connect(new InetSocketAddress(ip, port));
			
			Logger.debug("connected to server...");
			
			so.getOutputStream().write(new Package(0, NetUpdates.SERVER_INFO, Package.UNCOMPRESSED, new byte[]{}).toData());
			so.getOutputStream().flush();
			
			Logger.debug("request sent...");
			
			DataInputStream in = new DataInputStream(so.getInputStream());
			
			byte[] idBuf = new byte[4];
			byte[] lenBuf = new byte[4];
			byte usage;
			byte compressed;
			byte[] dataBuf = new byte[]{};
				
			for(int i = 0; i < 4; i++)
			{
				while(in.available() == 0) { Thread.sleep(1); } // byte, bit, alles muss mit.....
				idBuf[i] = in.readByte();
			}
			
			Logger.debug("got id");
			
			for(int i = 0; i < 4; i++)
			{
				while(in.available() == 0) { Thread.sleep(1); } // byte, bit, alles muss mit.....
				lenBuf[i] = in.readByte();
			}
				
			
			Logger.debug("got len");
			
			while(in.available() == 0) { Thread.sleep(1); } // byte, bit, alles muss mit.....
			usage = in.readByte();
			while(in.available() == 0) { Thread.sleep(1); } // byte, bit, alles muss mit.....
			compressed = in.readByte();
			
			Logger.debug("usage: "+Integer.toHexString(new Integer(usage)));
			Logger.debug("compressed: "+Integer.toHexString(new Integer(compressed)));
			
			int bytesLeft = NetUtils.toInt(lenBuf) - 2;
			
			Logger.debug("bytes left: "+ bytesLeft);
			
			if(bytesLeft != 0)
			{
				dataBuf = new byte[bytesLeft];
				
				for(int i = 0; i < bytesLeft; i++)
				{
					Logger.debug(i+"/"+bytesLeft);
					while(in.available() == 0) { Thread.sleep(1); } // byte, bit, alles muss mit.....
					dataBuf[i] = in.readByte();
				}
			}
			
			long pingBegin = System.currentTimeMillis();
			so.getOutputStream().write(new Package(0, NetUpdates.PING, Package.UNCOMPRESSED, new byte[]{}).toData());
			so.getOutputStream().flush();
			
			int timeout = 0; //give him 1sec to pong back...				
			while(timeout < 1000)
			{
				if(in.available() > 0) 
					break;
				
				Thread.sleep(1); 
				timeout++;
			}
			
			
			int ping = (int) (System.currentTimeMillis() - pingBegin);
			
			Logger.debug("ping: "+ping+"ms");
			
			//2x int -> max player
			//2x int -> current player
			//1x int 1x string -> server name
			//1x int 1x string -> server motd
			
			Logger.debug(NetUtils.byteArrayDebug(dataBuf));
			
			int mpc = NetUtils.toInt(NetUtils.subArray(6, 4, dataBuf));
			int cpc = NetUtils.toInt(NetUtils.subArray(17, 4, dataBuf));
			int namelen = NetUtils.toInt(NetUtils.subArray(22, 4, dataBuf));
			String name = NetUtils.toString(NetUtils.subArray(28, namelen-2, dataBuf));
			
			Logger.debug(namelen+" / "+name);
			
			int motdlen = NetUtils.toInt(NetUtils.subArray(22+4+namelen+1, 4, dataBuf));
			String motd = NetUtils.toString(NetUtils.subArray(22+4+namelen+1+6, motdlen-2, dataBuf));
			
			Logger.debug(motdlen+" / "+motd);
			
			
			so.close();
			
			return new ServerInfo(name, motd, cpc, mpc, ping);
		}		
		catch(IOException e)
		{
			Logger.log(e);
			Engine.setCloseRequested(true);

			Main.closeRequested =true;
		} 
		catch (InterruptedException e) {
			Logger.log(e);
		}
		
		return null;
	}
	
	
	public ServerInfo(String name, String motd, int playerCount,
			int maxPlayerCount, int ping) {
		super();
		this.name = name;
		this.motd = motd;
		this.playerCount = playerCount;
		this.maxPlayerCount = maxPlayerCount;
		this.ping = ping;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the motd
	 */
	public String getMotd() {
		return motd;
	}

	/**
	 * @return the playerCount
	 */
	public int getPlayerCount() {
		return playerCount;
	}

	/**
	 * @return the maxPlayerCount
	 */
	public int getMaxPlayerCount() {
		return maxPlayerCount;
	}

	/**
	 * @return the ping
	 */
	public int getPing() {
		return ping;
	}
	
	
}

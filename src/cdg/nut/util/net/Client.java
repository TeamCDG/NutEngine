package cdg.nut.util.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import cdg.nut.logging.Logger;
import cdg.nut.util.Engine;
import cdg.nut.util.game.World;

public class Client {

	private boolean localHorst = false;
	private Socket client;
	private World world;
	private boolean closeRequested = false;
	private Thread listener;
	private DataOutputStream out;
	private DataInputStream in;
	private Thread worldUpdater;
	
	public boolean connect(String ip, int port)
	{
		try
		{
			this.client = new Socket();
			this.client.connect(new InetSocketAddress(ip, port));
		}
		catch(IOException e)
		{
			Logger.log(e, "", "Unable to connect to: "+ip);
		}
		return false;
	}
	
	public void worldUpdater()
	{
		try
		{
			int bytesLeft = 0;
			
			byte[] idBuf = new byte[4];
			byte[] lenBuf = new byte[4];
			byte usage;
			byte compressed;
			byte[] dataBuf = new byte[]{};
			
			while(!Engine.closeRequested())
			{
				dataBuf = new byte[]{};
				
				for(int i = 0; i < 4; i++)
				{
					while(this.in.available() == 0) { Thread.sleep(1); } // byte, bit, alles muss mit.....
					idBuf[i] = this.in.readByte();
				}
				
				for(int i = 0; i < 4; i++)
				{
					while(this.in.available() == 0) { Thread.sleep(1); } // byte, bit, alles muss mit.....
					lenBuf[i] = this.in.readByte();
				}
				
				
				while(this.in.available() == 0) { Thread.sleep(1); } // byte, bit, alles muss mit.....
				usage = this.in.readByte();
				while(this.in.available() == 0) { Thread.sleep(1); } // byte, bit, alles muss mit.....
				compressed = this.in.readByte();
				
				
				bytesLeft = NetUtils.toInt(lenBuf) - 2;
				
				if(bytesLeft != 0)
				{
					dataBuf = new byte[bytesLeft];
					
					for(int i = 0; i < bytesLeft; i++)
					{
						while(this.in.available() == 0) { Thread.sleep(1); } // byte, bit, alles muss mit.....
						dataBuf[i] = this.in.readByte();
					}
				}
				
				Package pack = new Package(NetUtils.toInt(idBuf), usage, compressed, dataBuf);
				
				this.packageRecieved(pack);
				
				
			}
		}
		catch(Exception e)
		{
			Logger.log(e);
		}
	}
	
	protected void packageRecieved(Package p) throws IOException
	{
		switch(p.getUsage())
		{
			case NetUpdates.PING:
				this.out.write(new Package(0, NetUpdates.PONG, Package.UNCOMPRESSED, new byte[]{}).toData());
				this.out.flush();
				break;
			case NetUpdates.CHAT_MSG:
				break;						
			case NetUpdates.WORLD_SYNC:
				break;
			default:
				this.world.onPackage(p);
				break;
		}
	}
	
	public boolean isLocalHorst() {
		return this.localHorst;
	}


	public void setLocalHorst(boolean localHorst) {
		this.localHorst = localHorst;
	}

	public World getWorld() {
		return this.world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public boolean isCloseRequested() {
		return closeRequested;
	}

	public void setCloseRequested(boolean closeRequested) {
		this.closeRequested = closeRequested;
	}
	
}

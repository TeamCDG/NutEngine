package cdg.nut.util.net;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

import cdg.nut.logging.Logger;
import cdg.nut.util.Engine;
import cdg.nut.util.game.Player;
import cdg.nut.util.game.World;

public class Client {

	private boolean localHorst = false;
	private Socket client;
	private World world;
	private boolean closeRequested = false;
	private Thread listener;
	private DataOutputStream out;
	private DataInputStream in;
	private Player localPlayer;
	
	public Client(Player localPlayer)
	{
		this.localPlayer = localPlayer;
	}
	
	public boolean closeRequested()
	{
		return Engine.closeRequested() || this.closeRequested;
	}
	
	public boolean connect(String ip, int port)
	{
		try
		{
			this.client = new Socket();
			this.client.connect(new InetSocketAddress(ip, port));
			
			//0-3: id, should be int 0
			//4-7: length
			//8: usage, should be 0x07
			//9: compression flag
			//10-13: player color r
			//14-17: player color g
			//18-21: player color b
			//22-25: player color a
			//26-?: player name
			
			this.out = new DataOutputStream(this.client.getOutputStream());
			this.in = new DataInputStream(this.client.getInputStream());
			
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			b.write(NetUtils.toByteArray(this.localPlayer.getPlayerColor().toArray()));
			b.write(NetUtils.toByteArray(this.localPlayer.getName()));
			
			
			Package p = new Package(0, NetUpdates.PLAYER_CONNECTED, Package.UNCOMPRESSED, b.toByteArray());
			this.out.write(p.toData());
			this.out.flush();
			
			this.listener = new Thread(new Runnable(){

				@Override
				public void run() {
					listener();
					
				}});
			this.listener.start();
			
			return true;			
		}
		catch(IOException e)
		{
			Logger.log(e, "", "Unable to connect to: "+ip);
		}
		return false;
	}
	
	public void listener()
	{
		try
		{
			int bytesLeft = 0;
			
			byte[] idBuf = new byte[4];
			byte[] lenBuf = new byte[4];
			byte usage;
			byte compressed;
			byte[] dataBuf = new byte[]{};
			
			while(!this.closeRequested())
			{
				dataBuf = new byte[]{};
				
				for(int i = 0; i < 4; i++)
				{
					while(this.in.available() == 0) { Thread.sleep(1); if(this.closeRequested()) throw new CloseRequestedException("close requested");; } // bytes, byte, bit, alles muss mit.....
					idBuf[i] = this.in.readByte();
				}
				
				for(int i = 0; i < 4; i++)
				{
					while(this.in.available() == 0) { Thread.sleep(1); if(this.closeRequested()) throw new CloseRequestedException("close requested"); } // bytes, byte, bit, alles muss mit.....
					lenBuf[i] = this.in.readByte();
				}
				
				
				while(this.in.available() == 0) { Thread.sleep(1); if(this.closeRequested()) throw new CloseRequestedException("close requested"); } // bytes, byte, bit, alles muss mit.....
				usage = this.in.readByte();
				while(this.in.available() == 0) { Thread.sleep(1); if(this.closeRequested()) throw new CloseRequestedException("close requested"); } // bytes, byte, bit, alles muss mit.....
				compressed = this.in.readByte();
				
				
				bytesLeft = NetUtils.toInt(lenBuf) - 2;
				
				if(bytesLeft != 0)
				{
					dataBuf = new byte[bytesLeft];
					
					for(int i = 0; i < bytesLeft; i++)
					{
						while(this.in.available() == 0) { Thread.sleep(1); if(this.closeRequested()) throw new CloseRequestedException("close requested"); } // bytes, byte, bit, alles muss mit.....
						dataBuf[i] = this.in.readByte();
					}
				}
				
				if(this.closeRequested()) break;
				
				
				Package pack = new Package(NetUtils.toInt(idBuf), usage, compressed, dataBuf);
				
				this.packageRecieved(pack);
				
				
			}
		}
		catch(CloseRequestedException e)
		{
			Logger.info(e.getMessage());
		}
		catch(Exception e)
		{
			Logger.log(e);
		}
		
	}
	
	public void send(Package p)
	{
		
		try {
			this.out.write(p.toData());
			this.out.flush();
		} catch (IOException e) {
			Logger.log(e);
		}
	}
	
	protected void packageRecieved(Package p) throws IOException
	{
		Logger.debug("Usage: "+p.getUsage());
		switch(p.getUsage())
		{
			case NetUpdates.PING:
				this.out.write(new Package(0, NetUpdates.PONG, Package.UNCOMPRESSED, new byte[]{}).toData());
				this.out.flush();
				Logger.info("ping");
				break;
			case NetUpdates.CHAT_MSG:
				break;						
			case NetUpdates.WORLD_SYNC:
				break;
			default:
				if(this.world != null) this.world.onPackage(p);
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

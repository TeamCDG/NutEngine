package cdg.nut.util.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cdg.nut.interfaces.IEntity;
import cdg.nut.logging.Logger;
import cdg.nut.util.Engine;
import cdg.nut.util.game.Player;
import cdg.nut.util.game.World;
import cdg.nut.util.gl.GLColor;
import cdg.nut.util.settings.SetKeys;

public class Server {

	public boolean isLocal = false;
	
	private World world;
	private Thread tick;
	private Thread serverThread;
	private ServerSocket server;
	private int port = 1337;
	private boolean closeRequested = false;
	private List<NetPlayer> player = new ArrayList<NetPlayer>();
	private Gson gson = new Gson();
	private JsonParser parser = new JsonParser();

	
	public void init()
	{
		this.serverThread = new Thread(new Runnable(){

			@Override
			public void run() {
				server();
				
			}});
		this.serverThread.start();
	}
	
	public void startTick()
	{
		this.tick = new Thread(new Runnable(){

			@Override
			public void run() {
				tick();
				
			}});
		this.tick.start();
	}
	
	private void tick()
	{
		while(!this.closeRequested && !Engine.closeRequested() && this.world != null)
		{
			
			
			long t1 = System.currentTimeMillis();
			
			this.onTick();
			
			List<IEntity> e = this.world.getEntites();
			
			for(int i = 0; i < e.size(); i++)
			{
				if(e.get(i).onTick())
				{
					this.broadcast(e.get(i).getUpdate().popPackage());
				}
			}
			
			long twt = SetKeys.SV_TICKRATE.getValue(Integer.class) - (System.currentTimeMillis() - t1);
			
			if(twt > 0)
				try {
					Thread.sleep(twt);
				} catch (InterruptedException ex) {
					Logger.log(ex);
				}
			
			Engine.setDelta(System.currentTimeMillis() - t1);
		}
	}
	
	private void server()
	{
		try {
			this.server = new ServerSocket(this.port);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(!this.closeRequested && !Engine.closeRequested() && this.server != null)
		{
			try {
				this.server.setSoTimeout(1000);
				Socket so = this.server.accept();
				NetPlayer cl = new NetPlayer(0, so);				
				this.connect(cl);
			} 
			catch (SocketTimeoutException e) {
			} catch (IOException e) {
				Logger.log(e);
			}
			
		}
	}
	
	protected void connect(final NetPlayer cl)
	{
		player.add(cl);	
		
		cl.setListenerThread(new Thread(new Runnable(){

			@Override
			public void run() {
				client(cl);
				
			}}));
		
		cl.getListenerThread().start();
	}
	
	protected void disconnect(NetPlayer cl) {
		
		try {
			cl.getClient().close();
		} catch (IOException e) {
			Logger.log(e);
		}		
		cl.getListenerThread().interrupt();
		player.remove(cl);
		
		if(cl.isPlayer())
		{
			this.world.removePlayer(cl.getId());
			this.broadcast(new Package(cl.getId(), NetUpdates.PLAYER_DISCONNECTED, Package.UNCOMPRESSED, new byte[]{}));
		}
		
	}

	private void client(NetPlayer cl)
	{
		try
		{
			while(cl.getIn().available() < 8) { Thread.sleep(1); } //Wait till we have shit to read
			
			byte[] data = new byte[cl.getIn().available()];
			cl.getIn().read(data);
			
			
			//--------> catch server info check/ping here...
			
			if(data[8] == NetUpdates.SERVER_INFO)
			{
				//someone wants to grab server info... something we can serve him :)
				
				UpdatePackage u = new UpdatePackage(0);
				u.addData(NetUpdates.SERVER_INFO, SetKeys.SV_MAX_PLAYER.getValue(Integer.class));
				u.addData(NetUpdates.SERVER_INFO, this.world != null?this.world.getAllPlayer().size():0);
				u.addData(NetUpdates.SERVER_INFO, SetKeys.SV_NAME.getValue(String.class));
				u.addData(NetUpdates.SERVER_INFO, SetKeys.SV_MOTD.getValue(String.class));
				
				Logger.debug("info package len: "+u.size());
				
				cl.getOut().write(u.popPackage().toData());
				cl.getOut().flush();
				
				int timeout = 1000; //give him 1sec to ping us...				
				while(timeout > 0)
				{
					if(cl.getIn().available() > 0) 
						break;
					
					Thread.sleep(1); 
					timeout--;
				}
				
				if(timeout > 0) //idc what he sent, just pong it...
				{
					cl.getOut().write(new Package(0, NetUpdates.PONG, Package.UNCOMPRESSED, new byte[]{}).toData());
					cl.getOut().flush();
				}
				
				disconnect(cl);
			}
			else
			{				
				// it's a player who wants to play ...
				//0-3: id, should be int 0
				//4-7: length
				//8: usage, should be 0x07
				//9: compression flag
				//10-13: player color r
				//14-17: player color g
				//18-21: player color b
				//22-25: player color a
				//26-?: player name
				
				GLColor col = new GLColor(NetUtils.toFloat(NetUtils.subArray(10, 4, data)),
										  NetUtils.toFloat(NetUtils.subArray(14, 4, data)),
										  NetUtils.toFloat(NetUtils.subArray(18, 4, data)),
										  NetUtils.toFloat(NetUtils.subArray(22, 4, data)));
				
				ByteArrayOutputStream out = new ByteArrayOutputStream();
		        GZIPOutputStream gzip = new GZIPOutputStream(out);
		        gzip.write(NetUtils.toByteArray(this.world.serialize()));
		        gzip.close();
				
		        Package p = new Package(0, NetUpdates.WORLD_SYNC, Package.COMPRESSED, out.toByteArray());
		        cl.getOut().write(p.toData());     
		        cl.getOut().flush(); //send world
		        
				this.world.addPlayer(NetUtils.toString(NetUtils.subArray(26, -1, data)), col, false);
				
				
				byte[] id = NetUtils.toByteArray(this.world.getPlayerCount() - 1);
				
				for(int i = 0; i < id.length; i++)
				{
					data[i] = id[i];
				}
				
				this.broadcast(data);	//broadcast player.		
				cl.setIsPlayer(true);
				cl.setId(this.world.getPlayerCount() - 1);
				
				//boolean wfptc = false; //wait for package to complete
				int bytesLeft = 0;
				
				byte[] idBuf = new byte[4];
				byte[] lenBuf = new byte[4];
				byte usage;
				byte compressed;
				byte[] dataBuf = new byte[]{};
				
				while(!this.closeRequested && !Engine.closeRequested())
				{
					dataBuf = new byte[]{};
					
					for(int i = 0; i < 4; i++)
					{
						while(cl.getIn().available() == 0) { Thread.sleep(1); } // byte, bit, alles muss mit.....
						idBuf[i] = cl.getIn().readByte();
					}
					
					for(int i = 0; i < 4; i++)
					{
						while(cl.getIn().available() == 0) { Thread.sleep(1); } // byte, bit, alles muss mit.....
						lenBuf[i] = cl.getIn().readByte();
					}
					
					
					while(cl.getIn().available() == 0) { Thread.sleep(1); } // byte, bit, alles muss mit.....
					usage = cl.getIn().readByte();
					while(cl.getIn().available() == 0) { Thread.sleep(1); } // byte, bit, alles muss mit.....
					compressed = cl.getIn().readByte();
					
					
					bytesLeft = NetUtils.toInt(lenBuf) - 2;
					
					if(bytesLeft != 0)
					{
						dataBuf = new byte[bytesLeft];
						
						for(int i = 0; i < bytesLeft; i++)
						{
							while(cl.getIn().available() == 0) { Thread.sleep(1); } // byte, bit, alles muss mit.....
							dataBuf[i] = cl.getIn().readByte();
						}
					}
					
					Package pack = new Package(NetUtils.toInt(idBuf), usage, compressed, dataBuf);
					
					
					this.packageRecieved(cl, pack);
					
				}
			}
		}
		catch(IOException | InterruptedException e)
		{
			Logger.log(e);
			disconnect(cl);
		}
	}
	
	public void packageRecieved(NetPlayer cl, Package p) throws IOException
	{
		switch(p.getUsage())
		{
			case NetUpdates.PING:
				cl.getOut().write(new Package(0, NetUpdates.PONG, Package.UNCOMPRESSED, new byte[]{}).toData());
				cl.getOut().flush();
				break;
			case NetUpdates.CHAT_MSG:
				this.chatmessage(p);
				break;
			case NetUpdates.PLAYER_DISCONNECTED:
				this.disconnect(cl);
				break;
			default:
				this.world.onPackage(p);
				break;
		}
	}
	
	private void chatmessage(Package p) {
		// TODO log dat, or sth like dat
		
		this.broadcast(p);
	}

	public void broadcast(int exclude, Package p)
	{
		if(p.getLength() > SetKeys.SV_PACKAGE_COMPRESSION_SIZE.getValue(Integer.class))
			this.broadcast(exclude, p.toCompressedData());
		else
			this.broadcast(exclude, p.toData());
	}
	
	public void broadcast(int exclude, byte[] data)
	{
		for(NetPlayer cl: this.player)
		{
			try
			{
				if(cl.getId() != exclude && cl.isPlayer())
				{
					cl.getOut().write(data);
					cl.getOut().flush();
				}
			}
			catch(IOException e)
			{
				Logger.log(e);
				this.disconnect(cl);
			}
		}
	}
	
	
	public void broadcast(Package p)
	{
		if(p.getLength() > SetKeys.SV_PACKAGE_COMPRESSION_SIZE.getValue(Integer.class))
			this.broadcast(-1, p.toCompressedData());
		else
			this.broadcast(-1, p.toData());
	}
	
	
	public void broadcast(byte[] data)
	{
		this.broadcast(-1, data);
	}
	
	/**
	 * @return the closeRequested
	 */
	protected boolean isCloseRequested() {
		return closeRequested;
	}

	/**
	 * @param closeRequested the closeRequested to set
	 */
	public void setCloseRequested(boolean closeRequested) {
		this.closeRequested = closeRequested;
	}

	public World setWorld() {
		return world;
	}
	
	public World getWorld() {
		return world;
	}
	
	protected void onTick()
	{
	}

	public void shutdown() {
		this.broadcast(new Package(0, NetUpdates.SHUTDOWN, Package.UNCOMPRESSED, new byte[]{}));
		this.closeRequested = true;
		
		for(NetPlayer cl: this.player)
		{
			try {
				cl.getClient().close();
			} catch (IOException e) {
				Logger.log(e);
			}
		}
		
		try {
			this.server.close();
		} catch (IOException e) {
			Logger.log(e);
		}
	}
}

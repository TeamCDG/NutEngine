package cdg.nut.util.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import cdg.nut.interfaces.IServerFoundListener;
import cdg.nut.logging.Logger;
import cdg.nut.util.Engine;
import cdg.nut.util.settings.SetKeys;

public class UDPListener {

	private int port;
	private List<IServerFoundListener> listener;
	private boolean closeRequested = false;
	private Thread listenerThread;
	private boolean iterating;
	
	/**
	 * @return the port
	 */
	protected int getPort() {
		return port;
	}

	/**
	 * @return the listenerThread
	 */
	protected Thread getListenerThread() {
		return listenerThread;
	}

	public UDPListener()
	{
		this.port = SetKeys.SV_BROADCAST_PORT.getValue(Integer.class);
		this.listener = new ArrayList<IServerFoundListener>();
		this.listenerThread = new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					listen();
				} catch (SocketException e) {
					Logger.log(e);
				}
				
			}});
		this.listenerThread.start();
		
	}
	
	public UDPListener(int port)
	{
		this.port = port;
		this.listener = new ArrayList<IServerFoundListener>();
		this.listenerThread = new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					listen();
				} catch (SocketException e) {
					Logger.log(e);
				}
				
			}});
		this.listenerThread.start();
	}
	
	public void addListener(IServerFoundListener lis)
	{
		if(this.listener != null)
		{
			while(this.iterating){};
			this.listener.add(lis);
		}
	}
	
	public void removeListener(IServerFoundListener lis)
	{
		if(this.listener != null)
		{
			while(this.iterating){};
			this.listener.remove(lis);
		}
	}
	
	public void close()
	{
		this.closeRequested = true;
	}
	
	public boolean closeRequested()
	{
		return this.closeRequested || Engine.closeRequested();
	}
	
	private void listen() throws SocketException
	{
		DatagramSocket socket = new DatagramSocket( this.port );
		socket.setSoTimeout(1000);
		while(!this.closeRequested())
		{
			Logger.debug("listen");
			try
			{
				DatagramPacket packet = new DatagramPacket( new byte[4], 4);
			    socket.receive( packet );
			    
			    InetAddress address = packet.getAddress();
			    int         port    = NetUtils.toInt(packet.getData());
			    
			    this.iterating = true;
			    
			    for(int i = 0; i < this.listener.size(); i++)
			    {
			    	this.listener.get(i).serverFound(address.toString(), port);
			    }
			    
			    this.iterating = false;
			}
			catch(IOException e)
			{
			}
		}
		
		socket.close();
	}
}

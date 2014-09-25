package cdg.nut.util.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import cdg.nut.logging.Logger;

public class NetPlayer {

	/**
	 * @return the out
	 */
	protected OutputStream getOut() {
		return out;
	}


	/**
	 * @return the in
	 */
	protected DataInputStream getIn() {
		return in;
	}


	private int playerId;
	private Socket client;
	
	private Thread listenerThread;
	
	private OutputStream out;
	private DataInputStream in;
	private boolean isPlayer;
	
	
	public NetPlayer(int id, Socket cl)
	{
		this.playerId = id;
		this.client = cl;
		
		try {
			this.out = cl.getOutputStream();
			this.in = new DataInputStream(cl.getInputStream());
		} catch (IOException e) {
			Logger.log(e);
		}
	}

	public Thread getListenerThread() {
		return listenerThread;
	}


	public void setListenerThread(Thread listenerThread) {
		this.listenerThread = listenerThread;
	}


	public int getId() {
		return this.playerId;
	}


	public boolean isPlayer()
	{
		return this.isPlayer;
	}
	
	public void setIsPlayer(boolean b) {
		this.isPlayer = b;
	}


	public Socket getClient() {
		return client;
	}


	public void setId(int i) {
		this.playerId = i;
	}
}

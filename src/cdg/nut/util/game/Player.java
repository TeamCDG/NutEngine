package cdg.nut.util.game;

import java.net.Socket;

import com.google.gson.JsonObject;

import cdg.nut.util.gl.GLColor;

public class Player {

	private GLColor playerColor;
	private String name;
	private boolean isLocal;
	private int playerId;
	private int ping;
	
	public Player() {}
	
	public void deserialize(JsonObject json)
	{
		this.name = json.get("name").getAsString();
		this.isLocal = json.get("isLocal").getAsBoolean();
		
		if(json.has("playerId"))
			this.playerId = json.get("playerId").getAsInt();
		
		JsonObject col = json.get("playerColor").getAsJsonObject();
		this.playerColor = new GLColor(col.get("r").getAsFloat(), col.get("g").getAsFloat(),col.get("b").getAsFloat(),col.get("a").getAsFloat());
	}
	
	public Player(GLColor col, String name)
	{
		this.playerColor = col;
		this.name = name;
	}
	
	
	public GLColor getPlayerColor() {
		return playerColor;
	}
	public void setPlayerColor(GLColor playerColor) {
		this.playerColor = playerColor;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


	public boolean isLocal() {
		return isLocal;
	}


	public void setLocal(boolean isLocal) {
		this.isLocal = isLocal;
	}


	public int getPlayerId() {
		return playerId;
	}


	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getPing() {
		return ping;
	}

	public void setPing(int ping) {
		this.ping = ping;
	}

}

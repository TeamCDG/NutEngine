package cdg.nut.util.net;

public abstract class NetUpdates {

	public static final byte SEPERATOR = 0x00;
	public static final byte INNER_SEPERATOR = 0x01;
	public static final byte POSITION_UPDATE = 0x02;
	public static final byte ROTATION_UPDATE = 0x03;
	public static final byte ENTITY_REMOVED = 0x04;
	public static final byte TILE_OCC_UPDATE = 0x05;
	public static final byte ENTITY_ADDED = 0x06;
	public static final byte PLAYER_CONNECTED = 0x07;
	public static final byte PLAYER_DISCONNECTED = 0x08;
	public static final byte PLAYER_PING_UPDATE = 0x09;
	public static final byte PING = 0x0A;
	public static final byte PONG = 0x0B;
	public static final byte CHAT_MSG = 0x0C;
	public static final byte WORLD_SYNC = 0x0D;
	public static final byte SERVER_INFO = 0x0E;
	public static final byte MULTI_PACKAGE = 0x0F;
	public static final byte SHUTDOWN = 0x10;
	
}

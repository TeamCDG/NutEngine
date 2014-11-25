package cdg.nut.util.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import cdg.nut.logging.Logger;

public class Package {

	public static final byte COMPRESSED = 0x00;
	public static final byte UNCOMPRESSED = 0x01;
	
	private int id;
	private int length;
	private int uncompressedLength;
	/**
	 * @return the uncompressedLength
	 */
	protected int getUncompressedLength() {
		return uncompressedLength;
	}

	private byte usage;
	private byte compressed;
	private byte[] data;
	
	public Package(int id, int length, byte usage, byte compressed, byte[] data)
	{
		this.id = id;
		this.length = length;
		this.usage = usage;
		this.data = data;
		
		this.compressed = compressed;
	}
	
	public Package(int id, byte usage, byte compressed, byte[] data)
	{
		this.id = id;
		this.length = data.length + 2;
		this.usage = usage;
		this.data = data;
		
		this.compressed = compressed;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	
	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}
	/**
	 * @return the usage
	 */
	public byte getUsage() {
		return usage;
	}

	/**
	 * @return the data
	 */
	public byte[] getData() {
		if(this.compressed == Package.UNCOMPRESSED)
			return data;
		else
		{
			ByteArrayInputStream out = new ByteArrayInputStream(data);
	        GZIPInputStream gzip = null;
	        try {
	        	gzip = new GZIPInputStream(out);
	        	byte[] ret = new byte[gzip.available()];
				gzip.read(ret);
				gzip.close();

	        	this.uncompressedLength = ret.length;
				return ret;
				
			} catch (IOException e) {
				Logger.log(e);
			}
		}
		return null;
	}

	public Package(byte[] pack)
	{
		this.id = NetUtils.toInt(NetUtils.subArray(0, 4, pack));
		this.length = NetUtils.toInt(NetUtils.subArray(4, 4, pack));
		this.usage = pack[8];
		this.compressed = pack[9];
		this.data = NetUtils.subArray(10, -1, pack);
	}
	
	public byte[] toData()
	{
		byte[] fin = new byte[4+4+1+1+data.length];
		fin = NetUtils.overwriteAt(0, fin, NetUtils.toByteArray(this.id));
		fin = NetUtils.overwriteAt(4, fin, NetUtils.toByteArray(this.data.length+2));
		fin[8] = this.usage;
		fin[9] = this.compressed;
		fin = NetUtils.overwriteAt(10, fin, this.data);
		
		return fin;
	}
	
	public byte[] toCompressedData()
	{
		byte[] fin = null;
		
		if(this.compressed == Package.COMPRESSED)	
		{
			fin = toData();
		}
		else
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
	        GZIPOutputStream gzip = null;
	        try {
	        	gzip = new GZIPOutputStream(out);
				gzip.write(this.data);
				gzip.close();
				
			} catch (IOException e) {
				Logger.log(e);
			}
	        
			byte[] c = out.toByteArray();
			
			fin = new byte[4+4+1+1+c.length];
			fin = NetUtils.overwriteAt(0, fin, NetUtils.toByteArray(this.id));
			fin = NetUtils.overwriteAt(4, fin, NetUtils.toByteArray(c.length+2));
			fin[8] = this.usage;
			fin[9] = Package.COMPRESSED;
			
			Logger.debug("len c: "+c.length+" / fin len: "+fin.length);
			
			fin = NetUtils.overwriteAt(10, fin, c);		
			
			
			this.length = c.length+2;
			this.compressed = Package.COMPRESSED;
		}
		
		return fin;
	}
	
	public static List<Package> multiPackageDownbreak(Package multi)
	{
		List<Package> pack = new ArrayList<Package>();
		
		int id = multi.getId();
		int nxtidx = 0;
		
		byte[] tmpdata = multi.getData();
		
		while(nxtidx < tmpdata.length)
		{
			int len = NetUtils.toInt(NetUtils.subArray(nxtidx, 4, tmpdata));
			byte usage = tmpdata[nxtidx+4];
			byte[] subdata = NetUtils.subArray(nxtidx+6, len-2, tmpdata);
			
			pack.add(new Package(id, usage, Package.UNCOMPRESSED, subdata));
			
			nxtidx += len+4;
		}
		
		return pack;
	}
}

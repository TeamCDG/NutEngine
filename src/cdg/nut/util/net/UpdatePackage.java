package cdg.nut.util.net;

import java.util.ArrayList;
import java.util.List;

public class UpdatePackage {

	private int id;
	private List<Byte> data = new ArrayList<Byte>(4);
	private int dataCount = 0;
	
	public UpdatePackage(int id)
	{
		this.id  = id;
		this.add(NetUtils.toByteArray(this.id));
	}
	
	public void addData(byte usage, String data)
	{
		this.addData(usage, NetUtils.toByteArray(data));
	}
	
	public void addData(byte usage, int data)
	{
		this.addData(usage, NetUtils.toByteArray(data));
	}
	
	public void addData(byte usage, int[] data)
	{
		this.addData(usage, NetUtils.toByteArray(data));
	}
	
	public void addData(byte usage, float data)
	{
		this.addData(usage, NetUtils.toByteArray(data));
	}
	
	public void addData(byte usage, float[] data)
	{
		this.addData(usage, NetUtils.toByteArray(data));
	}
	
	public void addData(byte usage, long data)
	{
		this.addData(usage, NetUtils.toByteArray(data));
	}
	
	public void addData(byte usage, long[] data)
	{
		this.addData(usage, NetUtils.toByteArray(data));
	}
	
	public void addData(byte usage, short data)
	{
		this.addData(usage, NetUtils.toByteArray(data));
	}
	
	public void addData(byte usage, short[] data)
	{
		this.addData(usage, NetUtils.toByteArray(data));
	}
	
	public void addData(byte usage, double data)
	{
		this.addData(usage, NetUtils.toByteArray(data));
	}
	
	public void addData(byte usage, double[] data)
	{
		this.addData(usage, NetUtils.toByteArray(data));
	}
	
	public void addData(byte usage, char data)
	{
		this.addData(usage, NetUtils.toByteArray(data));
	}
	
	public void addData(byte usage, char[] data)
	{
		this.addData(usage, NetUtils.toByteArray(data));
	}
	
	public void addData(byte usage, byte[] data)
	{
		if(this.data.size() > 4)
			this.data.add(NetUpdates.INNER_SEPERATOR);
		
		this.add(NetUtils.toByteArray(data.length +2));
		this.data.add(usage);
		this.data.add(Package.UNCOMPRESSED);
		this.add(data);
		
		this.dataCount++;
	}
	
	private void add(byte[] data)
	{
		for(int i = 0; i < data.length; i++)
		{
			this.data.add(data[i]);
		}
	}
	
	private void add(int pos, byte[] data)
	{
		for(int i = 0; i < data.length; i++)
		{
			this.data.add(pos+i, data[i]);
		}
	}
	
	public int size()
	{
		return this.data.size();
	}
	
	public void clear()
	{
		this.data.clear();
		this.dataCount = 0;
		this.add(NetUtils.toByteArray(this.id));
	}
	
	public Package popPackage()
	{
		List<Byte> tmp = this.popDataList();
		
		byte[] data = new byte[tmp.size()];
		
		for(int i = 0; i < tmp.size(); i++)
		{
			data[i] = tmp.get(i);
		}
		
		return new Package(data);
	}
	
	public List<Byte> popDataList()
	{
		
		if(this.dataCount > 1)
		{
			this.add(4,NetUtils.toByteArray(this.data.size()));
			this.data.add(8, NetUpdates.MULTI_PACKAGE);
			this.data.add(9, Package.UNCOMPRESSED);
		}
		
		List<Byte> tmp = new ArrayList<Byte>(data.size());
		
		for(int i = 0; i < this.data.size(); i++)
		{
			tmp.add(new Byte(this.data.get(i)));
		}		
		
		this.clear();
		
		return tmp;
	}
	
	public byte[] popData()
	{
		byte[] data = new byte[this.data.size()];
		
		for(int i = 0; i < this.data.size(); i++)
		{
			data[i] = this.data.get(i);
		}
		
		this.clear();
		
		return data;
	}
}

package cdg.nut.util.net;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public abstract class NetUtils {

	private static Charset charset = Charset.forName("UTF-8");
	private static ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
	
	public static byte[] toByteArray(int i)
	{
		return ByteBuffer.allocate(4).putInt(i).array();
	}
	
	public static byte[] toByteArray(int[] i)
	{
		ByteBuffer b = ByteBuffer.allocate(4*i.length);
		for(int e = 0; e < i.length; e++)
		{
			b.putInt(i[e]);
		}
		
		return b.array();
	}
	
	public static byte[] toByteArray(long l)
	{
		return ByteBuffer.allocate(8).putLong(l).array();
	}
	
	public static byte[] toByteArray(long[] l)
	{
		ByteBuffer b = ByteBuffer.allocate(8*l.length);
		for(int i = 0; i < l.length; i++)
		{
			b.putLong(l[i]);
		}
		
		return b.array();
	}
	
	public static byte[] toByteArray(short s)
	{
		return ByteBuffer.allocate(2).putShort(s).array();
	}
	
	public static byte[] toByteArray(short[] s)
	{
		ByteBuffer b = ByteBuffer.allocate(2*s.length);
		for(int i = 0; i < s.length; i++)
		{
			b.putShort(s[i]);
		}
		
		return b.array();
	}
	
	public static byte[] toByteArray(double d)
	{
		return ByteBuffer.allocate(8).putDouble(d).array();
	}
	
	public static byte[] toByteArray(double[] d)
	{
		ByteBuffer b = ByteBuffer.allocate(8*d.length);
		for(int i = 0; i < d.length; i++)
		{
			b.putDouble(d[i]);
		}
		
		return b.array();
	}
	
	public static byte[] toByteArray(float f)
	{
		return ByteBuffer.allocate(4).putFloat(f).array();
	}
	
	public static byte[] toByteArray(float[] f)
	{
		ByteBuffer b = ByteBuffer.allocate(4*f.length);
		for(int i = 0; i < f.length; i++)
		{
			b.putFloat(f[i]);
		}
		
		return b.array();
	}
	
	public static byte[] toByteArray(char c)
	{
		return ByteBuffer.allocate(2).putChar(c).array();
	}
	
	public static byte[] toByteArray(char[] c)
	{
		ByteBuffer b = ByteBuffer.allocate(2*c.length);
		for(int i = 0; i < c.length; i++)
		{
			b.putChar(c[i]);
		}
		
		return b.array();
	}
	
	public static byte[] toByteArray(String str)
	{
		return str.getBytes(NetUtils.charset);
	}
	
	public static int toInt(byte[] arr)
	{
		return ByteBuffer.wrap(arr).order(NetUtils.byteOrder).getInt();
	}
	
	public static float toFloat(byte[] arr)
	{
		return ByteBuffer.wrap(arr).order(NetUtils.byteOrder).getFloat();
	}
	
	public static short toShort(byte[] arr)
	{
		return ByteBuffer.wrap(arr).order(NetUtils.byteOrder).getShort();
	}
	
	public static long toLong(byte[] arr)
	{
		return ByteBuffer.wrap(arr).order(NetUtils.byteOrder).getLong();
	}
	
	public static double toDouble(byte[] arr)
	{
		return ByteBuffer.wrap(arr).order(NetUtils.byteOrder).getDouble();
	}
	
	public static char toChar(byte[] arr)
	{
		return ByteBuffer.wrap(arr).order(NetUtils.byteOrder).getChar();
	}
	
	public static String toString(byte[] arr)
	{
		if(arr == null || arr.length == 0 )
			return null;
		try {
			return new String(arr, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public static Charset getCharset() {
		return charset;
	}

	public static void setCharset(Charset charset) {
		NetUtils.charset = charset;
	}

	public static ByteOrder getByteOrder() {
		return byteOrder;
	}

	public static void setByteOrder(ByteOrder byteOrder) {
		NetUtils.byteOrder = byteOrder;
	}
	
	
	public static byte[] subArray(int start, int length, byte[] data)
	{
		if(data.length < start+length)
			return null;
		
		byte[] fin= null;
		if(length == -1)
		{
			fin = new byte[data.length-start];
			for(int i = start; i < data.length; i++)
			{
				fin[i-start] = data[i];
			}
		}
		else
		{
			fin = new byte[length];
			for(int i = start; i < start+length; i++)
			{
				fin[i-start] = data[i];
			}
		}
		
		return fin;
	}
		
	
	public static byte[] overwriteAt(int pos, byte[] arr, byte[] data)
	{
		for(int i = 0; i < data.length; i++)
		{
			arr[pos+i] = data[i];
		}
		
		return arr;
	}
	
	public static String byteArrayDebug(byte[] arr)
	{
		StringBuilder sb = new StringBuilder();
	    for (byte b : arr) {
	        sb.append(String.format("%02X ", b));
	    }
	    
	    return sb.toString();
	}
}

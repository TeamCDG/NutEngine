package cdg.nut.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

import com.jcraft.oggdecoder.OggData;
import com.jcraft.oggdecoder.OggDecoder;

import cdg.nut.interfaces.IVolumeChangedListener;
import cdg.nut.logging.Logger;

public class Sound implements IVolumeChangedListener {
	
	//TODO: Javadoc
	//TODO: finalize()
	
	private int src = -1;
	private int buf = -1;
	//private ISoundFinishListener lis;
	private boolean playing = false;
	private boolean loop = false;
	private float baseVol = 1.0f;
	private int type;
	
	public Sound(String filename, int type)
	{
		this(filename, type, false, 1.0f, 1.0f);
	}
	
	public Sound(String filename, int type, boolean loop)
	{
		this(filename, type, loop, 1.0f, 1.0f);
	}
	
	public Sound(String filename, int type, boolean loop, float volume)
	{
		this(filename, type, loop, volume, 1.0f);
	}
	
	public Sound(String filename, int type, boolean loop, float volume, float pitch)
	{
		String[] split = filename.split("/");
		System.out.println("loading "+split[split.length-1]);
		
		try
		{
			AL10.alGetError();
		}
		catch(UnsatisfiedLinkError e)
		{
			Logger.info("Seems no AL sound device has been created... creating now", "Utility.loadWav");
			
			try {
				AL.create();
			} catch (LWJGLException e1) {
				
				Logger.log(e1, "Utility.loadWav", "Unable to create AL sound device");
				return;
			}
		}
		
		this.buf = AL10.alGenBuffers();
		
		if (AL10.alGetError() != AL10.AL_NO_ERROR)
		{
			Logger.error(Utility.getALErrorString(AL10.alGetError()), "Sound.Sound");
		}
		
		
		if(filename.endsWith(".wav"))
		{
			try {
				
				WaveData waveFile = WaveData.create(new BufferedInputStream(new FileInputStream(filename)));
				System.out.println(waveFile==null);
				AL10.alBufferData(this.buf, waveFile.format, waveFile.data, waveFile.samplerate);
				waveFile.dispose();
				
			} catch (FileNotFoundException e) {
				Logger.log(e, "Sound.Sound");
				return;
			}
		}
		else if(filename.endsWith(".ogg") || filename.endsWith(".vorbis"))
		{
			try {
				OggDecoder oggDecoder = new OggDecoder();
				OggData oggData = oggDecoder.getData(new BufferedInputStream(new FileInputStream(filename)));
				AL10.alBufferData(this.buf, oggData.channels>1 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16, oggData.data, oggData.rate);
			} catch (FileNotFoundException e) {
				Logger.log(e, "Sound.Sound");
				return;
			} catch (IOException e) {
				Logger.log(e, "Sound.Sound");
				return;
			}
		}
				
		this.src = AL10.alGenSources();
		
		if (AL10.alGetError() != AL10.AL_NO_ERROR)
		{
			Logger.error(Utility.getALErrorString(AL10.alGetError()), "Utility.loadWav");
			return;
		}
		
		
		AL10.alSourcei(this.src, AL10.AL_BUFFER, this.buf);
		AL10.alSourcef(this.src, AL10.AL_PITCH, pitch);
		AL10.alSourcef(this.src, AL10.AL_GAIN, volume);
		
		
		FloatBuffer sourcePos = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
		FloatBuffer sourceVel = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
		
		AL10.alSource(this.src, AL10.AL_POSITION, sourcePos);
		AL10.alSource(this.src, AL10.AL_VELOCITY, sourceVel);
		
		this.type = type;
		
		this.setLoop(loop);
		this.setPitch(pitch);
		this.setVolume(volume);
		
		Globals.addVolumeChangedListener(this);
	}
	
	public void setLoop(boolean loop)
	{
		AL10.alSourcei(this.src, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
		this.loop = loop;
	}
	
	public void play()
	{
		if(!playing)
			AL10.alSourcePlay(this.src);
		this.playing = this.loop;
	}
	
	public void stop()
	{
		AL10.alSourceStop(this.src);
		this.playing = false;
	}
	
	public void pause()
	{
		AL10.alSourcePause(this.src);
		this.playing = false;
	}
	
	public void setVolume(float volume)
	{
		this.baseVol = volume;
		if(this.type == 0)
			AL10.alSourcef(this.src, AL10.AL_GAIN, volume*((float)Globals.getSoundVolume()/100.0f));
		else
			AL10.alSourcef(this.src, AL10.AL_GAIN, volume*((float)Globals.getMusicVolume()/100.0f));
	}
	
	public void setPitch(float pitch)
	{
		AL10.alSourcef(this.src, AL10.AL_PITCH, pitch);
	}
	
	public void finalize()
	{
		AL10.alDeleteSources(this.src);
		AL10.alDeleteBuffers(this.buf);
	}
	
	public int getState()
	{
		return AL10.alGetSourcei(this.src, AL10.AL_SOURCE_STATE);
	}

	@Override
	public void volumeChanged(int newVol) {
		this.setVolume(this.baseVol);
		
	}
}

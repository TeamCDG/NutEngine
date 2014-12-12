package cdg.nut.util;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.Sys;

import cdg.nut.gui.Console;
import cdg.nut.gui.components.InnerWindow;
import cdg.nut.logging.Logger;
import cdg.nut.util.net.Client;
import cdg.nut.util.net.Server;
import cdg.nut.util.net.UDPListener;

public abstract class Engine {

		public static final Console console = new Console();
		
		private static float delta = 0;
		private static float calculatedFPS = 0;
		private static int FPS = 0;
		private static boolean closeRequested = false;
		
		private static Server server;
		private static Client client;
		private static UDPListener udpListener;

		private static boolean showDebugOverlay = true;
		
		private static List<Long> pre_times = new ArrayList<Long>(1024);
		private static List<Long> uniform_times = new ArrayList<Long>(1024);
		private static List<Long> draw_times = new ArrayList<Long>(1024);
		private static List<Long> cleanup_times = new ArrayList<Long>(1024);
		
		public static void addPreTime(Long l)
		{
			Engine.pre_times.add(l);
		}
		
		public static void addUniformTime(Long l)
		{
			Engine.uniform_times.add(l);
		}
		
		public static void addDrawTime(Long l)
		{
			Engine.draw_times.add(l);
		}
		
		public static void addCleanupTime(Long l)
		{
			Engine.cleanup_times.add(l);
		}
		
		public static void timeDebug()
		{
			float pre = 0;			
			for(int i = 0; i < Engine.pre_times.size(); i++)
			{
				pre += Engine.pre_times.get(i);
			}
			pre = pre / Engine.pre_times.size();
			
			float uniform = 0;			
			for(int i = 0; i < Engine.uniform_times.size(); i++)
			{
				uniform += Engine.uniform_times.get(i);
			}
			uniform = uniform / Engine.uniform_times.size();
			
			float draw = 0;			
			for(int i = 0; i < Engine.draw_times.size(); i++)
			{
				draw += Engine.draw_times.get(i);
			}
			draw = draw / Engine.draw_times.size();
			
			float cleanup = 0;			
			for(int i = 0; i < Engine.cleanup_times.size(); i++)
			{
				cleanup += Engine.cleanup_times.get(i);
			}
			cleanup = cleanup / Engine.cleanup_times.size();
			
			Logger.debug("Time Statistics --> pre: "+pre+" / uniform: "+uniform+" / draw: "+draw+" / cleanup: "+cleanup);
			
			Engine.pre_times.clear();
			Engine.uniform_times.clear();
			Engine.draw_times.clear();
			Engine.cleanup_times.clear();
			
			
		}
		
		
		public static float getDelta() {
			return delta;
		}

		public static void setDelta(double d) {
			Engine.delta = (float) d;
			Engine.calculatedFPS = 1000.0f/Engine.delta;
		}
		
		public static void setDelta(float d) {
			Engine.delta = d;
		}
		
		public static long getTime()
		{
			return (Sys.getTime() * 1000) / Sys.getTimerResolution();
		}

		public static float getCalculatedFPS() {
			return calculatedFPS;
		}

		public static void setCalculatedFPS(float calculatedFPS) {
			Engine.calculatedFPS = calculatedFPS;
		}

		public static int getFPS() {
			return FPS;
		}

		public static void setFPS(int fPS) {
			FPS = fPS;
		}

		public static boolean isCloseRequested() {
			return closeRequested;
		}
		
		public static boolean closeRequested() {
			return closeRequested;
		}

		public static void setCloseRequested(boolean closeRequested) {
			Engine.closeRequested = closeRequested;
		}

		public static Client getClient() {
			return client;
		}

		public static void setClient(Client client) {
			if(Engine.client != null)
				Engine.client.setCloseRequested(true);
			Engine.client = client;
		}

		public static Server getServer() {
			return server;
		}

		public static void setServer(Server server) {
			if(Engine.server != null)
				Engine.server.shutdown();
			Engine.server = server;
		}

		public static UDPListener getUdpListener() {
			return udpListener;
		}

		public static void setUdpListener(UDPListener udpListener) {
			if(Engine.udpListener != null)
				Engine.udpListener.close();
			Engine.udpListener = udpListener;
		}

		public static void setShowDebugOverlay(boolean b) {
			 Engine.showDebugOverlay = b;
		}
		
		public static boolean showDebugOverlay() {
			// TODO Auto-generated method stub
			return Engine.showDebugOverlay;
		}
		
}

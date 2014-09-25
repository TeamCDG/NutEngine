package cdg.nut.util;

import org.lwjgl.Sys;

import cdg.nut.gui.Console;
import cdg.nut.gui.components.InnerWindow;
import cdg.nut.util.net.Client;
import cdg.nut.util.net.Server;

public abstract class Engine {

		public static final Console console = new Console();
		
		private static float delta = 0;
		private static float calculatedFPS = 0;
		private static int FPS = 0;
		private static boolean closeRequested = false;
		
		private static Server server;
		private static Client client;
		
		
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
			Engine.client = client;
		}

		public static Server getServer() {
			return server;
		}

		public static void setServer(Server server) {
			Engine.server = server;
		}
		
}

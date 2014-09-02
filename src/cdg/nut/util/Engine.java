package cdg.nut.util;

import org.lwjgl.Sys;

import cdg.nut.gui.Console;
import cdg.nut.gui.components.InnerWindow;

public abstract class Engine {

		public static final Console console = new Console();
		
		private static float delta = 0;

		public static float getDelta() {
			return delta;
		}

		public static void setDelta(double d) {
			Engine.delta = (float) d;
		}
		
		public static void setDelta(float d) {
			Engine.delta = d;
		}
		
		public static long getTime()
		{
			return (Sys.getTime() * 1000) / Sys.getTimerResolution();
		}
		
}

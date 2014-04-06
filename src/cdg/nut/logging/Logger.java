package cdg.nut.logging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.lwjgl.opengl.GL11;

public abstract class Logger {

        private static LogLevel logfileLevel = LogLevel.ERROR;
        private static LogLevel outputLevel = LogLevel.DEBUG;
        private static DateFormat crashDumpDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        private static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        private static String logfileLocation = "logfile.log";

        private static ConsoleColor debugColor = ConsoleColor.BLUE;
        private static ConsoleColor infoColor = ConsoleColor.WHITE;
        private static ConsoleColor errorColor = ConsoleColor.RED;

        private static PrintWriter logfile = null;
        
        public static LogLevel getLogfileLevel() {
			return logfileLevel;
		}


		public static void setLogfileLevel(LogLevel logfileLevel) {
			Logger.logfileLevel = logfileLevel;
		}


		public static LogLevel getOutputLevel() {
			return outputLevel;
		}


		public static void setOutputLevel(LogLevel outputLevel) {
			Logger.outputLevel = outputLevel;
		}


		public static DateFormat getCrashDumpDateFormat() {
			return crashDumpDateFormat;
		}


		public static void setCrashDumpDateFormat(DateFormat crashDumpDateFormat) {
			Logger.crashDumpDateFormat = crashDumpDateFormat;
		}


		public static DateFormat getDateFormat() {
			return dateFormat;
		}


		public static void setDateFormat(DateFormat dateFormat) {
			Logger.dateFormat = dateFormat;
		}


		public static String getLogfileLocation() {
			return logfileLocation;
		}

		/**
         * Sets the location at which the logfile will be stored. 
         *
         * @param logfileLocation Location of logfile. passing 'null' will close the stream.
         **/
		public static void setLogfileLocation(String logfileLocation) {
			if(logfileLocation == null && Logger.logfile != null)
				 Logger.logfile.close();
			else
			{
				 try {
					Logger.logfile = new PrintWriter(new BufferedWriter(new FileWriter(Logger.logfileLocation, true)));
				 } catch (IOException e) { }
			}
			
			Logger.logfileLocation = logfileLocation;
		}
		
		/**
         * ErrorColor is normally blue.
         *
         * @return DEBUG-Messages color as {@link ConsoleColor}
         **/
		public static ConsoleColor getDebugColor() {
			return debugColor;
		}

		/**
         * Sets the color in which DEBUG-Messages should be printed. <br {>You have 8 Colors to choose from {@link ConsoleColor}
         *
         * @param debugColor as {@link ConsoleColor}
         **/
		public static void setDebugColor(ConsoleColor debugColor) {
			Logger.debugColor = debugColor;
		}

		/**
         * ErrorColor is normally white.
         *
         * @return INFO-Messages color as {@link ConsoleColor}
         **/
		public static ConsoleColor getInfoColor() {
			return infoColor;
		}

		/**
         * Sets the color in which INFO-Messages should be printed. <br />You have 8 Colors to choose from {@link ConsoleColor}
         *
         * @param infoColor as {@link ConsoleColor}
         **/
		public static void setInfoColor(ConsoleColor infoColor) {
			Logger.infoColor = infoColor;
		}


		/**
         * ErrorColor is normally red.
         *
         * @return ERROR-Messages color as {@link ConsoleColor}
         **/
        public static ConsoleColor getErrorColor() {
                return errorColor;
        }


        /**
         * Sets the color in which ERROR-Messages should be printed. <br />You have 8 Colors to choose from {@link ConsoleColor}
         *
         * @param errorColor as {@link ConsoleColor}
         **/
        public static void setErrorColor(ConsoleColor errorColor) {
                Logger.errorColor = errorColor;
        }


        /**
         * Logs text with default level of {@link LogLevel#SPAM SPAM}
         *
         * @param text Message to log
         **/
        public static void spam(String text)
        {
                log(text, LogLevel.SPAM, null, false);
        }


        /**
         * Logs text with default level of {@link LogLevel#SPAM SPAM}
         *
         * @param text	       Message to log
         * @param suppressTime true if time should not be printed
         **/
        public static void spam(String text, boolean suppressTime)
        {
                log(text, LogLevel.SPAM, null, suppressTime);
        }


        /**
         * Logs text with default level of {@link LogLevel#SPAM SPAM}
         *
         * @param text	   Message to log
         * @param location Location of message (classname.methodname)
         **/
        public static void spam(String text, String location)
        {
                log(text, LogLevel.SPAM, location, false);
        }


        /**
         * Logs text with default level of {@link LogLevel#DEBUG DEBUG}
         *
         * @param text Message to log
         **/
        public static void debug(String text)
        {
                log(text, LogLevel.DEBUG, null, false);
        }


        /**
         * Logs text with default level of {@link LogLevel#DEBUG DEBUG}
         *
         * @param text	       Message to log
         * @param suppressTime true if time should not be printed
         **/
        public static void debug(String text, boolean suppressTime)
        {
                log(text, LogLevel.DEBUG, null, suppressTime);
        }


        /**
         * Logs text with default level of {@link LogLevel#DEBUG DEBUG}
         *
         * @param text 	   Message to log
         * @param location Location of message (classname.methodname)
         **/
        public static void debug(String text, String location)
        {
                log(text, LogLevel.DEBUG, location, false);
        }


        /**
         * Logs text with default level of {@link LogLevel#INFO INFO}
         *
         * @param text				Message to log
         **/
        public static void info(String text)
        {
                log(text, LogLevel.INFO, null, false);
        }


        /**
         * Logs text with default level of {@link LogLevel#INFO INFO}
         *
         * @param text         Message to log
         * @param suppressTime true if time should not be printed
         **/
        public static void info(String text, boolean suppressTime)
        {
                log(text, LogLevel.INFO, null, suppressTime);
        }


        /**
         * Logs text with default level of {@link LogLevel#INFO INFO}
         *
         * @param text     Message to log
         * @param location Location of message (classname.methodname)
         **/
        public static void info(String text, String location)
        {
                log(text, LogLevel.INFO, location, false);
        }


        /**
         * Logs text with default level of {@link LogLevel#ERROR ERROR}
         *
         * @param text Message to log
         **/
        public static void error(String text)
        {
                log(text, LogLevel.ERROR, null, false);
        }


        /**
         * Logs text with default level of {@link LogLevel#ERROR ERROR}
         *
         * @param text         Message to log
         * @param suppressTime true if time should not be printed
         **/
        public static void error(String text, boolean suppressTime)
        {
                log(text, LogLevel.ERROR, null, suppressTime);
        }


        /**
         * Logs text with default level of {@link LogLevel#ERROR ERROR}
         *
         * @param text     Message to log
         * @param location Location of message (classname.methodname)
         **/
        public static void error(String text, String location)
        {
                log(text, LogLevel.ERROR, location, false);
        }


        /**
         * Logs text.<br />
         *
         * Output to console and in logfile depending on the level
         * and the level settings.<br />
         * Defaults lvl to INFO
         *
         * @param text Message to log
         **/
        public static void log(String text)
        {
                log(text, LogLevel.INFO, null, false);
        }


        /**
         * Logs text.<br />
         * Output to console and in logfile depending on the level
         * and the level settings.
         *
         * @param text Message to log
         * @param lvl  Level of message
         **/
        public static void log(String text, LogLevel lvl)
        {
                log(text, lvl, null, false);
        }


        /**
         * Logs text.<br />
         * Output to console and in logfile depending on the level
         * and the level settings.
         *
         * @param text         Message to log
         * @param lvl          Level of message
         * @param suppressTime true if time should not be printed
         **/
        public static void log(String text, LogLevel lvl, boolean suppressTime)
        {
                log(text, lvl, null, suppressTime);
        }


        /**
         * Logs text.<br />
         * Output to console and in logfile depending on the level
         * and the level settings.
         *
         * @param text     Message to log
         * @param lvl      Level of message
         * @param location Location of message (classname.methodname)
         **/
        public static void log(String text, LogLevel lvl, String location)
        {
                log(text, lvl, location, false);
        }

        /**
         * Logs text.<br />
         * Output to console and in logfile depending on the level
         * and the level settings.
         *
         * @param text         Message to log
         * @param lvl          Level of message
         * @param location     Location of message (classname.methodname)
         * @param suppressTime true if time should not be printed
         **/
        public static void log(String text, LogLevel lvl, String location, boolean suppressTime)
        {
                if(Logger.logfile == null)
                {
                        try {
                                Logger.logfile = new PrintWriter(new BufferedWriter(new FileWriter(Logger.logfileLocation, true)));
                        } catch (IOException e) { }
                }
                else if(!lvl.isGreater(Logger.logfileLevel))
                {
                        Logger.logfile.println("["+lvl.toString()+(location == null?"":" ("+location+")")+" "+(suppressTime?"":Logger.dateFormat.format(new Date()))+"] "+text);
                }

                if(!lvl.isGreater(Logger.outputLevel))
                {
                        String color = "";

                        switch(lvl)
                        {
                                case ERROR:
                                        color = Logger.errorColor.getAnsiColor();
                                        break;
                                case DEBUG:
                                        color = Logger.debugColor.getAnsiColor();
                                        break;
                                case INFO:
                                        color = Logger.infoColor.getAnsiColor();
                                        break;
                                default:
                                        color = "";
                                        break;

                        }

                        System.out.println(color+"["+lvl.toString()+(location == null?"":" ("+location+")")+" "+(suppressTime?"":Logger.dateFormat.format(new Date()))+"] "+text+ConsoleColor.RESET.getAnsiColor());
                }
        }


        /**
         * Logs an exception.
         *
         * @param e Exception that was thrown
         **/
        public static void log(Exception e)
        {
                log(e,null,null,false);
        }


        /**
         * Logs an exception.
         *
         * @param e	             Exception that was thrown
         * @param location           Location where exception was thrown (classname.methodname)
         * @param suppressStacktrace If 'true' no stacktrace will be printed
         **/
        public static void log(Exception e, String location)
        {
                log(e,location,null,false);
        }

        /**
         * Logs an exception.
         *
         * @param e             Exception that was thrown
         * @param location      Location where exception was thrown (classname.methodname)
         * @param customMessage	Custom message
         **/
        public static void log(Exception e, String location, String customMessage)
        {
                log(e,location,customMessage,false);
        }

        /**
         * Logs an exception.
         *
         * @param e                  Exception that was thrown
         * @param location           Location where exception was thrown (classname.methodname)
         * @param suppressStacktrace If 'true' no stacktrace will pe printed
         **/
        public static void log(Exception e, String location, boolean suppressStacktrace)
        {
                log(e,location,null,suppressStacktrace);
        }

        /**
         * Logs an exception
         *
         * @param e                  Exception that was thrown
         * @param location           Location where exception was thrown (classname.methodname)
         * @param customMessage      Custom message
         * @param suppressStacktrace If 'true' no stacktrace will pe printed
         **/
        public static void log(Exception e, String location, String customMessage, boolean suppressStacktrace)
        {
                if(!LogLevel.ERROR.isGreater(Logger.logfileLevel))
                {
                        Logger.logfile.println("[ERROR "+(location == null?"":"("+location+") ")+Logger.dateFormat.format(new Date())+"] "+e.getClass().getName()+": "+(customMessage == null?customMessage:e.getMessage()));
                        if(!suppressStacktrace) e.printStackTrace(Logger.logfile);
                }

                if(!LogLevel.ERROR.isGreater(Logger.outputLevel))
                {
                        String color = Logger.errorColor.getAnsiColor();
                        System.out.println(color+"[ERROR "+(location == null?"":"("+location+") ")+Logger.dateFormat.format(new Date())+"] "+e.getClass().getName()+": "+(customMessage == null?customMessage:e.getMessage()));
                        if(!suppressStacktrace) e.printStackTrace(System.out);
                        System.out.print(ConsoleColor.RESET.getAnsiColor());
                        System.out.flush();
                }
        }

        /**
         * Creates a crashdump file.
         *
         * @param message  Custom message
         * @param location Location of exception (classname.methodname)
         * @param e        Exception that caused the crash
         **/
        public static void createCrashDump(String message, String location, Exception e)
        {
                createCrashDump(message, location, e, false, "");
        }

        /**
         * Creates a crashdump file.
         *
         * @param message  Custom message
         * @param location Location of exception (classname.methodname)
         * @param e        Exception that caused the crash
         * @param glinfo   True if crashdump should contain info about gl version, renderer etc.
         **/
        public static void createCrashDump(String message, String location, Exception e, boolean glinfo)
        {
                createCrashDump(message, location, e, glinfo, "");
        }

        /**
         * Creates a crashdump file.
         *
         * @param message  Custom message
         * @param location Location of exception (classname.methodname)
         * @param e        Exception that caused the crash
         * @param glinfo   True if crashdump should contain info about gl version, renderer etc.
         * @param appendix Custom message that will be appended on the bottom
         **/
        public static void createCrashDump(String message, String location, Exception e, boolean glinfo, String appendix)
        {
                try {
                        PrintWriter writer = new PrintWriter("crashdump_"+crashDumpDateFormat.format(new Date())+".txt", "UTF-8");
                        writer.println((e!=null?e.getMessage()+" in ":"")+location+": "+message); //first of all, tell us what it is all about
                        writer.println("OS: "+System.getProperty("os.name")+" ("+System.getProperty("os.arch")+") "+System.getProperty("os.version")); //give some os information
                        writer.println("Java: "+System.getProperty("java.vendor")+" "+System.getProperty("java.version")); //give some java version

                        if(glinfo) { //gl info is always nice
                                writer.println("GLVendor: "+GL11.glGetString(GL11.GL_VENDOR));
                                writer.println("Renderer: "+GL11.glGetString(GL11.GL_RENDERER));
                                writer.println("Version: "+GL11.glGetString(GL11.GL_VERSION));
                        }


                        if(e != null)
                        {
                                writer.println("-----------------------------------------------------");
                                writer.println("Stacktrace:");
                                writer.println(e.getMessage()+" in "+location);
                                e.printStackTrace(writer); //cook with stacktrace
                                /*
                                   for(StackTraceElement el : e.getStackTrace())
                                   {
                                   writer.println("     in "+el.getClassName()+"."+el.getMethodName()+(el.isNativeMethod()?" (native method)":":"+el.getLineNumber()));
                                   }*/
                        }

                        writer.println("-----------------------------------------------------");
                        writer.println(appendix);

                        writer.flush();
                        writer.close(); //finally close

                } catch (Exception ex) {
                        // Gnarf... Error in writing crashdump.. get the hell out of here
                        System.exit(-1);
                }
        }
}

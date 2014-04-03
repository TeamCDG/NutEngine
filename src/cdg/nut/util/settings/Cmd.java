package cdg.nut.util.settings;

import java.util.LinkedList;
import java.util.List;

import cdg.nut.logging.Logger;
import cdg.nut.util.BitmapFont;
import cdg.nut.util.Colors;
import cdg.nut.util.Utility;
import cdg.nut.util.gl.GLColor;

//TODO: Javadoc
public abstract class Cmd {
	
	public static void exec(String command)
	{		
		if(command.startsWith("#"))  //woops we have a comment here
		{
			return;
		}
		
		
		command = command.trim();	        	
    	String key = command.split(" ")[0];
    	List<String> parameter = new LinkedList<String>();
    	command = command.replace(key, "").trim();
    	
    	while(command.length() != 0)
    	{
    		String param = "";
    		if(command.startsWith("\""))
    		{
    			int secInd = 0;
    			int c = 0;
    			
    			do
                {
                    c = 0;
                    secInd = command.indexOf('"', secInd+1);
                    while(secInd-(c+1)>= 0 && command.charAt(secInd-(c+1))=='\\')
                    {
                        c++;
                    }
                    
                } while(c%2 != 0);
    			
    			param = command.substring(command.indexOf('"'), secInd+1);
    		}
			else if(command.startsWith("#")) //woops we have a comment here
			{
				break;
			}
    		else
    		{
    			param = command.split(" ")[0];
    		}
    		
    		command = command.replace(param, "").trim();
    		parameter.add(Utility.unescape(param.startsWith("\"") && param.endsWith("\"")?param.substring(1, param.length()-1):param));
    		
    	}
    	
    	Cmd.exec(key, parameter);
	}
	
	public static void exec(String key, List<String> parameter)
	{
		Class<?> c = SettingsKeys.valueOf(key.toUpperCase()).getCls();
		
		if(c != null) //we have to change settings now :)
		{
			Object val = null;
			
			if(parameter.isEmpty()) //cannot work..
			{
				Logger.error("Unable to change settings: no parameters given", "CommandExecuter.exec");
				return;
			}
			
			if(c == GLColor.class)
			{
				if(parameter.size() == 3)
				{
					if(isInteger(parameter.get(0)))
					{
						int[] values = new int[3];
						for(int i = 0; i < 3; i++)
						{
							try {
								values[i] = Integer.parseInt(parameter.get(i));		
							} catch(NumberFormatException e) { 
								Logger.error("Unable to change setting ("+key+"): illegel parameter "+i+" ("+parameter.get(i)+")", "CommandExecuter.exec");
								return;
							}
						}
						val = new GLColor(values[0], values[1], values[2]);
					}
					else
					{
						float[] values = new float[3];
						for(int i = 0; i < 3; i++)
						{
							try {
								values[i] = Float.parseFloat(parameter.get(i));		
							} catch(NumberFormatException e) { 
								Logger.error("Unable to change setting ("+key+")s: illegel parameter "+i+" ("+parameter.get(i)+")", "CommandExecuter.exec");
								return;
							}
						}
						val = new GLColor(values[0], values[1], values[2]);
					}
				}
				else if(parameter.size() == 4)
				{
					if(isInteger(parameter.get(0)))
					{
						int[] values = new int[4];
						for(int i = 0; i < 4; i++)
						{
							try {
								values[i] = Integer.parseInt(parameter.get(i));		
							} catch(NumberFormatException e) { 
								Logger.error("Unable to change setting ("+key+"): illegel parameter "+i+" ("+parameter.get(i)+")", "CommandExecuter.exec");
								return;
							}
						}
						val = new GLColor(values[0], values[1], values[2], values[3]);
					}
					else
					{
						float[] values = new float[4];
						for(int i = 0; i < 3; i++)
						{
							try {
								values[i] = Float.parseFloat(parameter.get(i));		
							} catch(NumberFormatException e) { 
								Logger.error("Unable to change setting ("+key+"): illegel parameter "+i+" ("+parameter.get(i)+")", "CommandExecuter.exec");
								return;
							}
						}
						val = new GLColor(values[0], values[1], values[2], values[3]);
					}
				}
				else
				{
					val = Colors.valueOf(parameter.get(0).toUpperCase()).getGlColor();
					if(val == null)
					{
						Logger.error("Unable to change setting ("+key+"): illegel parameter "+0+" ("+parameter.get(0)+")", "CommandExecuter.exec");
						return;
					}
				}
			}
			else if(c == BitmapFont.class)
			{
				//TODO: check for fonts
			}
			else if(c == int.class)
			{
				try {
					val = (int) Integer.parseInt(parameter.get(0));
				} catch (Exception e) {
					Logger.error("Unable to change setting ("+key+"): illegel parameter 0 ("+parameter.get(0)+")", "CommandExecuter.exec");
					return;
				}
			}
			else if(c == float.class)
			{
				try {
					val = (float) Float.parseFloat(parameter.get(0));
				} catch (Exception e) {
					Logger.error("Unable to change setting ("+key+"): illegel parameter 0 ("+parameter.get(0)+")", "CommandExecuter.exec");
					return;
				}
			}
			else if(c == boolean.class)
			{
				try {
					val = (boolean) Boolean.parseBoolean(parameter.get(0));
				} catch (Exception e) {
					Logger.error("Unable to change setting ("+key+"): illegel parameter 0 ("+parameter.get(0)+")", "CommandExecuter.exec");
					return;
				}
			}
			else if(c == String.class)
			{
				val = parameter.get(0);
			}
			
			Settings.set(key, val);
		}
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
	
	public static boolean isFloat(String s) {
	    try { 
	        Float.parseFloat(s);
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
	
}

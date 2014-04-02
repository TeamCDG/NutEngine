package cdg.nut.util.settings;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import cdg.nut.logging.Logger;

//TODO: Javadoc
public abstract class CfgReader {
	
	public static void read(String file)
	{
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			Logger.log(e, "CfgReader.read", "Error creating FileReader for \""+file+"\"");
		}
	    try {
	        String line = br.readLine();

	        while (line != null) {
	        	
	        	line = line.trim();	        	
	        	String key = line.split(" ")[0];
	        	List<String> parameter = new LinkedList<String>();
	        	line = line.replace(key, "").trim();
	        	
	        	while(line.length() != 0)
	        	{
	        		String param = "";
	        		if(line.startsWith("\""))
	        		{
	        			int secInd = line.indexOf('"', line.indexOf('"')+1)+1;
	        			
	        			while(secInd == line.indexOf("\\\"")+2)
	        			{
	        				secInd = line.indexOf('"', secInd+2)+1;
	        			}
	        			
	        			param = line.substring(line.indexOf('"'), secInd);
	        		}
	        		else
	        		{
	        			param = line.split(" ")[0];
	        		}
	        		
	        		line = line.replace(param, "").trim();
	        		parameter.add(param.startsWith("\"") && param.endsWith("\"")?param.substring(1, param.length()-1):param);
	        		
	        	}
	        	
	        	Cmd.exec(key, parameter);
	            line = br.readLine();	            
	        }
	        
	    } catch (IOException e) {
	    	Logger.log(e, "CfgReader.read", "Error reading file for \""+file+"\"");
		} finally {
	        try {
				br.close();
			} catch (IOException e) {
				Logger.log(e, "CfgReader.read", "Error closing FileReader for \""+file+"\"");
			}
	    }
	}
	
	
}

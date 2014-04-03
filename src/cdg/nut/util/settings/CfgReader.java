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
	        	
	        	Cmd.exec(line);
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

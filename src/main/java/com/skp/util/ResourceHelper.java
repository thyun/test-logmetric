package com.skp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceHelper {
	private static final Logger logger = LoggerFactory.getLogger(ResourceHelper.class);

	public static String getResourceString(String relativePath) {
		InputStream is = getResourceInputStream(relativePath);
		return readInputStream(is);
	}
	
    public static InputStream getResourceInputStream(String relativePath) {                                  
        String workDir = System.getProperty("user.dir");                                 
        String path = workDir + "/" + relativePath;                                              
        InputStream inputStream = null;
        
        try {                                                                            
            inputStream = new FileInputStream(new File(path));                           
//          logger.debug("getConfigInputStream config from file: " + path);          
            return inputStream;                                                          
        } catch (FileNotFoundException e) {                                              
//          logger.debug("getConfigInputStream config from file: " + path);          
        }                                                                                
                                                                                         
        inputStream = ResourceHelper.class.getClassLoader().getResourceAsStream(relativePath);       
//        	logger.debug("getConfigInputStream config from resource: " + file);          
        return inputStream;                                                              
    }            
    
    public static String readInputStream(InputStream is) {                
        StringBuilder textBuilder = new StringBuilder();                   
        try {                                                              
            Reader reader = new BufferedReader(new InputStreamReader(is)); 
            int c = 0;                                                     
            while ((c = reader.read()) != -1) {                            
                textBuilder.append((char) c);                              
            }                                                              
        } catch (IOException e) {                                          
        	logger.error(e.toString());                                          
        }                                                                  
        return textBuilder.toString();                                     
    }
    
    // line-by-line callback
    public static void processResource(String relativePath, LineReadCallback callback) {   
    	InputStream is = getResourceInputStream(relativePath);
        try {                                                              
            BufferedReader reader = new BufferedReader(new InputStreamReader(is)); 
            String line;    
            while ((line = reader.readLine()) != null) {
                callback.processLine(line);
            }                                                              
        } catch (IOException e) {                                          
        	logger.error(e.toString());                                          
        }
    }
	
    public interface LineReadCallback {
    	void processLine(String line);
    }

	public static List<String> getResourceLineList(String relativePath) {
    	InputStream is = getResourceInputStream(relativePath);
    	ArrayList<String> lineList = new ArrayList<>();
    	
    	try {                                                              
            BufferedReader reader = new BufferedReader(new InputStreamReader(is)); 
            String line;    
            while ((line = reader.readLine()) != null) {
                lineList.add(line);
            }                                                              
        } catch (IOException e) {                                          
            logger.error(e.toString());                                           
        }
    	return lineList;
	}
}

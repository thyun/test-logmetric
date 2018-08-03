package com.skp.testutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class ResourceHelper {

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
//            logHandler.debug("getConfigInputStream config from file: " + path);          
            return inputStream;                                                          
        } catch (FileNotFoundException e) {                                              
//          logHandler.debug("getConfigInputStream config from file: " + path);          
        }                                                                                
                                                                                         
        inputStream = ResourceHelper.class.getClassLoader().getResourceAsStream(relativePath);       
//        logHandler.debug("getConfigInputStream config from resource: " + file);          
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
            e.printStackTrace();                                           
        }                                                                  
        return textBuilder.toString();                                     
    }
    
    // line-by-line callback
    public static void processResource(String relativePath, LineReadCallback callback) {   
    	InputStream is = getResourceInputStream(relativePath);
//        StringBuilder textBuilder = new StringBuilder();                   
        try {                                                              
            BufferedReader reader = new BufferedReader(new InputStreamReader(is)); 
            String line;    
            while ((line = reader.readLine()) != null) {
//                textBuilder.append(line);
                callback.processLine(line);
            }                                                              
        } catch (IOException e) {                                          
            e.printStackTrace();                                           
        }
//        return textBuilder.toString();                                     
    }
	
    public interface LineReadCallback {
    	void processLine(String line);
    }
}

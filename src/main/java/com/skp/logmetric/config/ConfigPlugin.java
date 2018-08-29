package com.skp.logmetric.config;

import org.json.JSONObject;

public interface ConfigPlugin {

	public void init(JSONObject j); 		
	
	public void prepare(); 
	

}

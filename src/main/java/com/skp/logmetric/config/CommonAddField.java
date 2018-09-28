package com.skp.logmetric.config;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import lombok.Getter;

@Getter
public class CommonAddField {
	public static String NAME = "add_field"; 
	String field;
	String value;
	
	public CommonAddField(String field, String value) {
		super();
		this.field = field;
		this.value = value;
	}

	public static List<CommonAddField> jsonarray2List(JSONArray jsonArray) {
		ArrayList<CommonAddField> r = new ArrayList<>();
		if (jsonArray == null)
			return r;
		for (int i=0; i<jsonArray.length(); i++) {
			JSONObject jo = (JSONObject) jsonArray.get(i);
			
			r.add(new CommonAddField(jo.getString("field"), jo.getString("value")));
		}
		return r;
	}
	
}

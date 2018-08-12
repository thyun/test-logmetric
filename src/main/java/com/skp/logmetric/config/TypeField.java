package com.skp.logmetric.config;

import java.util.HashMap;

import lombok.Data;

/*
 * TypeField expression
 * ex) %{WORD:client} %{WORD}
 */
@Data
public class TypeField {
	// REGEX
	// (\\S+?) - Match type (? means non-greedy)
	// ?: - Do not remember
	// (\\S+?) - Match field
	// ? - Match 0 or 1
	public static String MATCH_REGEX = "%\\{(\\S+?)(?::(\\S+?))?\\}";
	public static String KEY_WORD = "WORD";
	public static String KEY_DATE = "DATE";
	public static String KEY_LONG = "LONG";
	public static String KEY_DOUBLE = "DOUBLE";
	public static String KEY_DATA = "DATA";
	public static HashMap<String, String> TYPE_REGEX = new HashMap<String, String>() {
		{ 
			put("WORD", "(\\S+)");
			put("DATE", "(.+?)");
			put("LONG", "(\\d+)");
			put("DOUBLE", "([\\d\\.]+)");
			put("DATA", "(.*?)");
		}
	};

	int pos;
	String type;
	String field;
	
	public TypeField(int pos, String type, String field) {
		this.pos = pos;
		this.type = type;
		this.field = field;
	}

	public static TypeField create(int pos, String type, String field) {
		return new TypeField(pos, type, field);
	}
	
	public static String getTypeRegex(String type) {
		return TYPE_REGEX.get(type);
	}
}

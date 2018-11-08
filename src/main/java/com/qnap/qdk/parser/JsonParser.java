package com.qnap.qdk.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Json parser
 */
public class JsonParser {

	JSONObject jsonobj = null;
	JSONArray jsonArray = null;
	String jsonString = "";
	
	public JsonParser(String jsonString) {
		this.jsonString = jsonString;
		try {
			jsonobj = new JSONObject(jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public JsonParser(String jsonString, String jsonArrayTagName) {
		this.jsonString = jsonString;
		try {
			jsonobj = new JSONObject(jsonString);
			if (jsonString.contains(jsonArrayTagName)) {
				jsonArray = jsonobj.getJSONArray(jsonArrayTagName);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public final String getTagValue(String tagName) {
		try {
			if (jsonobj.has(tagName)) {
				return jsonobj.getString(tagName);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public final JSONArray getJsonArray(String tagName) {
		try {
			if (jsonString.contains(tagName)) {
				jsonArray = jsonobj.getJSONArray(tagName);
				return jsonArray;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public final String getTagValueByIndex(String tagName, int index) {
		if (jsonArray != null) {
			try {
				JSONObject obj = jsonArray.getJSONObject(index);
				if (obj.has(tagName)) {
					return obj.getString(tagName);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} 
		return "";
	}
	
	public final int getJsonArrayLength(String tagName) {
		try {
			if (jsonString.contains(tagName)) {
				if (jsonArray == null) {
					jsonArray = jsonobj.getJSONArray(tagName);
				} 
				return jsonArray.length();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}
}

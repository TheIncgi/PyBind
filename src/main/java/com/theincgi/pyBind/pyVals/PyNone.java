package com.theincgi.pyBind.pyVals;

import org.json.JSONObject;

public class PyNone extends PyVal {
	public static final String TYPENAME = "NoneType";
	public PyNone() {
		super();
	}
	
	@Override
	public String getType() {
		return TYPENAME;
	}
	
	@Override
	public boolean isNone() {
		return true;
	}
	
	@Override
	public String toStr() {
		return "None";
	}
	
	@Override
	public boolean toBool() {
		return false;
	}
	
	@Override
	public JSONObject asJsonValue() {
		JSONObject obj = new JSONObject();
		obj.put("type", TYPENAME);
		return obj;
	}
	
}

package com.theincgi.pyBind.pyVals;

import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.theincgi.pyBind.NotImplementedException;
import com.theincgi.pyBind.PyBindException;


/**
 * Represents a generator in python
 * */
public class PyGen extends PyVal {
	public static final String TYPENAME = "generator";
	private long ref;
	
	public PyGen(long ref) {
		this.ref = ref;
	}
	
	@Override
	public PyVal call(JSONArray args, JSONObject kwargs) {
		throw new PyBindException("Attempt to call "+getType() + "(use next())");
	}
	
	@Override
	public PyVal invoke(JSONArray args, JSONObject kwargs) {
		throw new PyBindException("Attempt to call "+getType() + "(use next())");
	}
	
	@Override
	public String getType() {
		return TYPENAME;
	}
	
	@Override
	public String toStr() {
		return "<generator>";
	}
	
	@Override
	public boolean isPyGen() {
		return true;
	}
	
	@Override
	public PyVal next() {
		throw new NotImplementedException();
	}
	
	@Override
	public PyGen checkGen() {
		return this;
	}
	
	
	@Override
	public JSONObject asJsonValue() {
		JSONObject obj = new JSONObject();
		obj.put("type", TYPENAME);
		obj.put("ref", ref);
		return obj;
	}
	
}

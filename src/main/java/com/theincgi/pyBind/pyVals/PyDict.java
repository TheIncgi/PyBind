package com.theincgi.pyBind.pyVals;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

public class PyDict extends PyVal{
	public static final String TYPENAME = "dict";
	
	Map<PyVal, PyVal> map;
	
	public PyDict( boolean isOrdered ) {
		map = isOrdered ? new LinkedHashMap<>() : new HashMap<>();
	}
	
	public boolean isOrdered() {
		return map instanceof LinkedHashMap;
	}
	
	@Override
	public String getType() {
		return TYPENAME;
	}
	
	@Override
	public boolean isDict() {
		return true;
	}
	
	@Override
	public PyDict checkDict() {
		return this;
	}
	
	@Override
	public PyVal index(int a) {
		// TODO Auto-generated method stub
		return super.index(a);
	}
	
	@Override
	public PyVal index(PyRef r) {
		// TODO Auto-generated method stub
		return super.index(r);
	}
	
	@Override
	public PyVal index(String str) {
		// TODO Auto-generated method stub
		return super.index(str);
	}
	
	@Override
	public JSONObject asJsonValue() {
		JSONObject obj = new JSONObject();
		obj.put("type", TYPENAME);
		JSONObject val = new JSONObject();
		obj.put("val", val);
		
		for(var e : map.entrySet()) {
			if(e.getKey().isStr())
				val.put(e.getKey().toStr(), e.getValue().asJsonValue());
			else
				val.put(e.getKey().asJsonValue().toString(), e.getValue().asJsonValue());
		}
		return obj;
	}
	
}

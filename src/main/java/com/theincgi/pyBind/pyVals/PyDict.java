package com.theincgi.pyBind.pyVals;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

public class PyDict extends PyVal{
	public static final String TYPENAME = "dict";
	
	Map<PyVal, PyVal> map;
	
	public PyDict( boolean isOrdered ) {
		map = isOrdered ? new LinkedHashMap<>() : new HashMap<>();
	}
	
	public PyDict(Map<?, ?> m) {
		this( m instanceof LinkedHashMap );
		for(Entry<?, ?> e : m.entrySet()) {
			map.put( PyVal.toPyVal(e.getKey()) , PyVal.toPyVal(e.getValue()) );
		}
	}

	public PyDict(JSONObject jsonObject, boolean ordered) {
		this(ordered);
		
		JSONArray keys = jsonObject.getJSONArray("keys");
		JSONArray vals = jsonObject.getJSONArray("vals");
		if(keys.length() != vals.length())
			throw new IllegalArgumentException("num keys doesn't match num values");
		
		for(int i = 0; i<keys.length(); i++)
			map.put( PyVal.fromJson(keys.getJSONObject(i)) , PyVal.fromJson(vals.getJSONObject(i)) );
		
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
		return map.get(PyInt.valueOf(a));
	}
	
	@Override
	public PyVal index(int a, PyVal def) {
		return map.getOrDefault(PyInt.valueOf(a), def);
	}
	
	@Override
	public PyVal index(PyVal v) {
		if(v.isRef())
			return map.get( v.checkPyRef().get() );
		return map.get( v );
	}
	
	@Override
	public PyVal index(PyVal v, PyVal def) {
		if(v.isRef())
			return map.getOrDefault( v.checkPyRef().get(), def );
		return map.getOrDefault( v, def );
	}
	
	//TODO hash & equals for all py type
	@Override
	public PyVal index(String str) {
		return map.get( new PyStr(str) );
	}
	
	@Override
	public JSONObject asJsonValue() {
		JSONObject obj = new JSONObject();
		obj.put("type", TYPENAME);
		JSONObject val = new JSONObject();
		JSONArray keys = new JSONArray();
		JSONArray vals = new JSONArray();
		obj.put("val", val);
		val.put("keys", keys);
		val.put("vals", vals);
		
		
		for(var e : map.entrySet()) {
			if( (!e.getKey().isRef()) && e.getKey().isStr() )
				keys.put( e.getKey().toStr() );
			else
				keys.put( e.getKey().asJsonValue() );
			vals.put( e.getValue().asJsonValue() );
		}
		return obj;
	}
	
}

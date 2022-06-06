package com.theincgi.pyBind.pyVals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringJoiner;

import org.json.JSONArray;
import org.json.JSONObject;

import com.theincgi.pyBind.PyBindException;
import com.theincgi.pyBind.PyException;

public class PyList extends PyVal {
	public static final String TYPENAME = "list";
	private ArrayList<PyVal> list = new ArrayList<>();
	
	public PyList() {
	}
	
	public PyList(PyVal...values) {
		Collections.addAll(list, values);
	}
	
	public PyList(JSONArray jsonArray) {
		for(int i = 0; i < jsonArray.length(); i++)
			list.add( PyVal.fromJson(jsonArray.getJSONObject(i)));
	}

	public PyList(List<?> l) {
		var it = l.iterator();
		if(it.hasNext()) for(Object obj = it.next(); obj!=null; obj=it.hasNext() ? it.next() : null)
			list.add( PyVal.toPyVal(obj) );
	}

	@Override
	public String getType() {
		return TYPENAME;
	}
	
	@Override
	public String toStr() {
		StringJoiner out = new StringJoiner(",","[","]");
		for(PyVal v : list)
			out.add(v.toStr());
		return out.toString();
	}                                                                                                                                                                                                                       
	
	@Override
	public PyTuple tupleVal() {
		return new PyTuple( list.toArray(new PyVal[list.size()]) );
	}
	
	@Override
	public boolean isList() {
		return true;
	}
	
	@Override
	public PyList checkList() {
		return this;
	}
	
	@Override
	public PyList toList() {
		return this;
	}
	
	@Override
	public boolean isIndexable() {
		return true;
	}
	
	@Override
	public boolean toBool() {
		return list.size() > 0;
	}
	
	@Override
	public int len() {
		return list.size();
	}
	
	@Override
	public PyVal index(int a) {
		return list.get(a);
	}
	
	@Override
	public PyVal index(Integer a, Integer b) {
		PyList out = new PyList();
		for(int i=a; i<b; i++)
			out.list.add(list.get(i));
		return out;
	}
	
	@Override
	public PyVal index(PyVal v) {
		return index(v.checkInt().intVal());
	}
	
	@Override
	public PyVal index(int a, PyVal def) {
		if( a < 0 || len() <= a ) return def;
		return index(a);
	}
	
	@Override
	public PyVal index(PyVal v, PyVal def) {
		return index( v.checkInt().toInt(), def );
	}
//	@Override
//	public PyVal index(PyRef r) {
//		if(r.isInt()) {
//			return index(r.toInt());
//		}
//		throw new PyException("Expected reference to int type, got "+r.getType());
//	}
	
	@Override
	public JSONObject asJsonValue() {
		JSONObject obj = new JSONObject();
		obj.put("type", TYPENAME);
		JSONArray jArr = new JSONArray();
		for(PyVal v : list)
			jArr.put( v.asJsonValue() );
		obj.put("val", jArr);
		return obj;
	}
}

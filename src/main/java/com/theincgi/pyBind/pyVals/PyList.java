package com.theincgi.pyBind.pyVals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringJoiner;

import org.json.JSONArray;
import org.json.JSONObject;

import com.theincgi.pyBind.PyBindException;

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
		if(it.hasNext()) for(Object obj = it.next(); it.hasNext(); obj=it.next())
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

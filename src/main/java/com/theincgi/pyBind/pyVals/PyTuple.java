package com.theincgi.pyBind.pyVals;

import java.util.StringJoiner;

import org.json.JSONArray;
import org.json.JSONObject;

import com.theincgi.pyBind.PyBindException;

public class PyTuple extends PyVal {
	public static final String TYPENAME = "tuple";
	private final PyVal[] values;
	public PyTuple(PyVal... vals) {
		values = vals;
	}
	
	@Override
	public String getType() {
		return TYPENAME;
	}
	
	@Override
	public String toStr() {
		StringJoiner out = new StringJoiner(",","(",")");
		for(PyVal v : values)
			out.add(v.toStr());
		return out.toString();
	}
	
	@Override
	public boolean isTuple() {
		return true;
	}
	
	@Override
	public PyTuple checkTuple() {
		return this;
	}
	
	@Override
	public PyTuple tupleVal() {
		return this;
	}
	
	@Override
	public boolean isIndexable() {
		return true;
	}
	
	@Override
	public boolean toBool() {
		return values.length > 0;
	}
	
	@Override
	public int len() {
		return values.length;
	}

	@Override
	public JSONObject asJsonValue() {
		JSONObject obj = new JSONObject();
		JSONArray values = new JSONArray();
		for( PyVal v : this.values )
			values.put(v.asJsonValue());
		obj.put("type", TYPENAME);
		obj.put("val", values);
		return obj;
	}
}

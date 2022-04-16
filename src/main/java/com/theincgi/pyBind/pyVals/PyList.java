package com.theincgi.pyBind.pyVals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.StringJoiner;

import com.theincgi.pyBind.PyBindException;

public class PyList extends PyVal {
	private ArrayList<PyVal> list = new ArrayList<>();
	
	public PyList() {
	}
	
	public PyList(PyVal...values) {
		Collections.addAll(list, values);
	}
	
	@Override
	public String getType() {
		return "list";
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
}

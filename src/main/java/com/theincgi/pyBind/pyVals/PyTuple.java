package com.theincgi.pyBind.pyVals;

import java.util.StringJoiner;

import com.theincgi.pyBind.PyBindException;

public class PyTuple extends PyVal {
	private final PyVal[] values;
	public PyTuple(PyVal... vals) {
		values = vals;
	}
	
	@Override
	public String getType() {
		return "tuple";
	}
	
	@Override
	public String toStr() {
		StringJoiner out = new StringJoiner(",","(",")");
		for(PyVal v : values)
			out.add(v.toStr());
		return out.toString();
	}
}

package com.theincgi.pyBind.pyVals;

import com.theincgi.pyBind.PyBindException;

public class PyNone extends PyVal {

	public PyNone() {
		super();
	}
	
	@Override
	public String getType() {
		return "NoneType";
	}
	
	@Override
	public boolean isNone() {
		return true;
	}
	
	@Override
	public String toStr() {
		return "None";
	}
}

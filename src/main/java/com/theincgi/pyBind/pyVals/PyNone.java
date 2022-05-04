package com.theincgi.pyBind.pyVals;

import com.theincgi.pyBind.PyBindException;

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
	
}

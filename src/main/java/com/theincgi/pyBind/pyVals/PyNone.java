package com.theincgi.pyBind.pyVals;

import com.theincgi.pyBind.PyBindException;

public class PyNone extends PyVal {

	public PyNone() {
		super();
	}
	
	@Override
	public PyVal call(Object... values)  {
		throw new PyBindException("Attempt to call "+getType());
	}
	
	@Override
	public PyVal invoke(Object... values) {
		throw new PyBindException("Attempt to invoke "+getType());
	}
	
	@Override
	public String getType() {
		return "NoneType";
	}
	
	@Override
	public boolean isNone() {
		return true;
	}
	
}

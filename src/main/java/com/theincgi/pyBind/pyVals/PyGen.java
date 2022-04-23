package com.theincgi.pyBind.pyVals;

import java.util.LinkedHashMap;

import com.theincgi.pyBind.NotImplementedException;
import com.theincgi.pyBind.PyBindException;


/**
 * Represents a generator in python
 * */
public class PyGen extends PyVal {
	private String uuid;
	public PyGen(String refUUID) {
		this.uuid = refUUID;
	}
	
	@Override
	public PyVal call(Object... values)  {
		throw new PyBindException("Attempt to call "+getType() + "(use next())");
	}
	
	@Override
	public PyVal invoke(Object... values) {
		throw new PyBindException("Attempt to call "+getType() + "(use next())");
	}
	
	@Override
	public String getType() {
		return "generator";
	}
	
	@Override
	public String toStr() {
		return "<generator>";
	}
	
	@Override
	public boolean isPyGen() {
		return true;
	}
	
	@Override
	public PyVal next() {
		throw new NotImplementedException();
	}
	
}

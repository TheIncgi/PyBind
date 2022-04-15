package com.theincgi.pyBind.pyVals;

public class PyBool extends PyVal {
	private final boolean value;
	public static PyBool TRUE = new PyBool(true), FALSE = new PyBool(false);
	
	public PyBool(boolean value) {
		this.value = value;
	}
	
	@Override
	public PyBool checkBool() {
		return this;
	}
	
	@Override
	public boolean isBool() {
		return true;
	}
	
	@Override
	public boolean toBool() {
		return value;
	}
	
	@Override
	public String toJString() {
		return Boolean.toString(value);
	}
	
}

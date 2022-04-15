package com.theincgi.pyBind.pyVals;

public class PyFloat extends PyVal {
	private final double value;
	
	public PyFloat(double d) {
		value = d;
	}
	
	@Override
	public PyFloat checkDouble() {
		return this;
	}
	
	@Override
	public boolean isDouble() {
		return true;
	}
	
	@Override
	public String toJString() {
		return Double.toString(value);
	}
	
	@Override
	public PyInt checkInt() {
		return PyInt.valueOf((int) value);
	}
}

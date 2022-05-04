package com.theincgi.pyBind.pyVals;

import org.json.JSONObject;

public class PyBool extends PyFloat {
	public static final String TYPENAME = "bool";
	public static PyBool TRUE = new PyBool(true), FALSE = new PyBool(false);
	
	private PyBool(boolean value) {
		super(value ? 1.0 : 0.0);
	}

	@Override
	public String getType() {
		return TYPENAME;
	}
	
	@Override
	public int toInt() {
		return (int) value;
	}
	
	@Override
	public int toInt(int defValue) {
		return (int) value;
	}
	
	@Override
	public PyInt intVal() {
		return PyInt.valueOf( (int) value );
	}
	
	@Override
	public PyInt intVal(int defValue) {
		return intVal();
	}
	
	@Override
	public String toStr() {
		return toBool() ? "True" : "False";
	}
	
	@Override
	public double toDouble() {
		return value;
	}
	
	@Override
	public double toDouble(double defValue) {
		return value;
	}
	
	@Override
	public boolean isBool() {
		return true;
	}
	
	@Override
	public PyBool checkBool() {
		return this;
	}
	
	@Override
	public Object asJsonValue() {
		JSONObject obj = new JSONObject();
		obj.put("type", getType());
		obj.put("val", toBool());
		return obj;
	}
}

package com.theincgi.pyBind.pyVals;

import org.json.JSONObject;

import com.theincgi.pyBind.PyBindException;

public class PyFloat extends PyVal {
	public static final String TYPENAME = "float";
	protected final double value;
	
	public PyFloat(double d) {
		value = d;
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
		return PyInt.valueOf((int)value);
	}
	
	@Override
	public PyInt intVal(int defValue) {
		return intVal();
	}
	
	@Override
	public String toStr() {
		return Double.toString(value);
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
	public boolean isFloat() {
		return true;
	}
	
	@Override
	public PyFloat checkDouble() {
		return this;
	}
	
	@Override
	public PyFloat floatVal() {
		return this;
	}
	
	@Override
	public PyFloat floatVal(double defValue) {
		return this;
	}
	
	@Override
	public boolean toBool() {
		return value != 0;
	}
	
	@Override
	public JSONObject asJsonValue() {
		JSONObject obj = new JSONObject();
		obj.put("type", TYPENAME);
		obj.put("val", value);
		return obj;
	}
}

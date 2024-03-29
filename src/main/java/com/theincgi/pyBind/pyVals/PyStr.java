package com.theincgi.pyBind.pyVals;

import java.util.LinkedHashMap;

import org.json.JSONObject;

import com.theincgi.pyBind.PyBindException;

public class PyStr extends PyVal { //TODO hash & equals
	public static final String TYPENAME = "str";
	private final String value;
	public PyStr(String str) {
		value = str;
	}
	
	@Override
	public String getType() {
		return TYPENAME;
	}

	@Override
	public int toInt() {
		return Integer.parseInt(value);
	}
	
	@Override
	public int toInt(int defValue) {
		return toInt();
	}
	
	@Override
	public PyInt intVal() {
		return PyInt.valueOf( Integer.parseInt(value) );
	}
	
	@Override
	public PyInt intVal(int defValue) {
		try {
			return intVal();
		} catch (NumberFormatException e) {
			return PyInt.valueOf(defValue);
		}
	}
	@Override
	public String toStr() {
		return value;
	}
	
	@Override
	public boolean isStr() {
		return true;
	}
	
	@Override
	public PyStr checkPyStr() {
		return this;
	}
	
	@Override
	public PyStr strVal() {
		return this;
	}
	
	@Override
	public double toDouble() {
		return Double.parseDouble(value);
	}
	
	@Override
	public double toDouble(double defValue) {
		return Double.parseDouble(value);
	}
	
	@Override
	public PyFloat floatVal() {
		return new PyFloat(toDouble());
	}
	
	@Override
	public PyFloat floatVal(double defValue) {
		return new PyFloat(toDouble(defValue));
	}
	
	@Override
	public boolean toBool() {
		return value.length() > 0;
	}
	
	@Override
	public int len() {
		return value.length();
	}
	
	@Override
	public JSONObject asJsonValue() {
		JSONObject obj = new JSONObject();
		obj.put("type", TYPENAME);
		obj.put("val", value);
		return obj;
	}
}

package com.theincgi.pyBind.pyVals;

import java.util.LinkedHashMap;

import com.theincgi.pyBind.Common;
import com.theincgi.pyBind.PyBindException;
import com.theincgi.pyBind.PyTypeMismatchException;

public class PyBool extends PyVal {
	private final boolean value;
	public static PyBool TRUE = new PyBool(true), FALSE = new PyBool(false);
	
	private PyBool(boolean value) {
		this.value = value;
	}

	@Override
	public String getType() {
		return "bool";
	}
	
	@Override
	public int toInt() {
		return value ? 1 : 0;
	}
	
	@Override
	public int toInt(int defValue) {
		return value ? 1 : 0;
	}
	
	@Override
	public PyInt intVal() {
		return PyInt.valueOf( value ? 1 : 0 );
	}
	
	@Override
	public PyInt intVal(int defValue) {
		return intVal();
	}
	
	@Override
	public String toStr() {
		return value ? "True" : "False";
	}
	
	@Override
	public double toDouble() {
		return value ? 1 : 0;
	}
	
	@Override
	public double toDouble(double defValue) {
		return value ? 1 : 0;
	}
	
	@Override
	public boolean toBool() {
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
}

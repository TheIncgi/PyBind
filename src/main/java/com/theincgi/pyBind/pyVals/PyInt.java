package com.theincgi.pyBind.pyVals;

import java.util.Objects;
import java.util.WeakHashMap;

import com.theincgi.pyBind.PyBindException;

public class PyInt extends PyVal {
	private final int value;
	
	private static WeakHashMap<Integer, PyInt> ints = new WeakHashMap<>();
	
	private PyInt(int i) {
		value = i;
	}
	
	public static synchronized PyInt valueOf( int i ) {
		return ints.computeIfAbsent((Integer)i, k->{
			return new PyInt(k);
		});
	}
	
	@Override
	public String getType() {
		return "int";
	}
	
	@Override
	public int toInt() {
		return value;
	}
	
	@Override
	public int toInt(int defValue) {
		return value;
	}
	
	@Override
	public boolean isInt() {
		return true;
	}
	
	@Override
	public PyInt checkInt() {
		return this;
	}
	
	@Override
	public PyInt intVal() {
		return this;
	}
	
	@Override
	public PyInt intVal(int defValue) {
		return this;
	}
	
	@Override
	public String toStr() {
		return Integer.toString(value);
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
	public PyFloat checkDouble() {
		return new PyFloat(value);
	}
	
	@Override
	public PyFloat floatVal() {
		return new PyFloat(value);
	}
	
	@Override
	public PyFloat floatVal(double defValue) {
		return floatVal();
	}
	
}

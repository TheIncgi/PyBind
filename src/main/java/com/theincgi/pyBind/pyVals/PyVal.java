package com.theincgi.pyBind.pyVals;

import static com.theincgi.pyBind.Common.expected;

import java.util.LinkedHashMap;

import com.theincgi.pyBind.Common;
import com.theincgi.pyBind.PyTypeMismatchException;

public abstract class PyVal {
	private String typeName;
	
	public PyVal() {
	}
	
	public PyVal call(Object... values) {
		throw new RuntimeException("Not Implemented");
	}
	
	public PyVal invoke(Object... values) { //call but only take ref to result
		throw new RuntimeException("Not Implemented");
	}
	
	/**
	 * shortcut of call
	 */
	public PyVal c(Object...values) {
		return call(values);
	}
	/**
	 * shortcut of invoke
	 */
	public PyVal i(Object...values) {
		return invoke(values);
	}
	
	
	public String getType() {
		return typeName;
	}
	
	public boolean isRef() {
		return false;
	}
	
	public int toInt() {
		throw new PyTypeMismatchException();
	}
	public int toInt(int def) {
		return def;
	}
	public PyInt toPyInt() { //or cast?
		throw new PyTypeMismatchException(expected("int", getType()));
	}
	public boolean isInt() {
		return false;
	}
	
	@Override
	public String toString() {
		return "None";
	}
	
	public double toDouble() {
		throw new PyTypeMismatchException();
	}
	
	public boolean toBool() {
		throw new PyTypeMismatchException();
	}
	
	public LinkedHashMap<PyVal, PyVal> toMap() {
		throw new PyTypeMismatchException();
	}
	
	
}

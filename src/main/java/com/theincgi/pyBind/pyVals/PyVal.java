package com.theincgi.pyBind.pyVals;

import static com.theincgi.pyBind.Common.expected;

import java.util.LinkedHashMap;

import com.theincgi.pyBind.Common;
import com.theincgi.pyBind.PyBindException;
import com.theincgi.pyBind.PyNone;
import com.theincgi.pyBind.PyTypeMismatchException;

public abstract class PyVal {
	private String typeName;
	
	public static final PyVal NONE = new PyNone();
	
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
	public final PyVal c(Object...values) {
		return call(values);
	}
	/**
	 * shortcut of invoke
	 */
	public final PyVal i(Object...values) {
		return invoke(values);
	}
	
	
	public String getType() {
		return typeName;
	}
	
	public boolean isRef() {
		return false;
	}
	
	
	public boolean isFunc() {
		return false;
	}
	public PyFunc checkFunction() {
		throw new PyTypeMismatchException(expected("int", getType()));
	}
	
	//int
	public int toInt() {
		throw new PyTypeMismatchException();
	}
	public boolean isInt() {
		return false;
	}
	public PyInt checkInt() {
		throw new PyTypeMismatchException(expected("int", getType()));
	}
	
	
	
	//string
	public String toJString() {
		return "None";
	}
	@Override
	public String toString() {
		return "class <%s>%S %s".formatted(getType(), isRef()?" [REF]":"[COPY]", toJString());
	}
	public boolean isString() {
		return false;
	}
	
	
	//float
	public double toDouble() {
		throw new PyTypeMismatchException();
	}
	public boolean isDouble() {
		return false;
	}
	public PyFloat checkDouble() {
		throw new PyTypeMismatchException(expected("float", getType()));
	}
	
	
	
	
	
	//boolean
	public boolean toBool() {
		throw new PyTypeMismatchException();
	}
	public boolean isBool() {
		return false;
	}
	
	public PyBool checkBool() {
		throw new PyTypeMismatchException(expected("bool", getType()));
	}
	
	public boolean isNum() {
		return isInt() || isDouble();
	}
	
	
	public int len() {
		throw new PyBindException("Attempt to get the length of type '"+getType()+"'");
	}
	
	public PyVal attrib(String name) {
		throw new PyBindException("Attempt to get an attribute ( ."+name+" ) from type '"+getType()+"'");
	}
	public PyVal index(int a) {
		throw new PyBindException("Attempt to index ( ["+a+"] ) type '"+getType()+"'");
	}
	public PyVal index(int a, int b) {
		throw new PyBindException("Attempt to index ( ["+a+","+b+"] ) type '"+getType()+"'");
	}
	
	public LinkedHashMap<PyVal, PyVal> toMap() {
		throw new PyTypeMismatchException();
	}
	
	
	public static PyVal toPyVal( int i ) {
		return PyInt.valueOf(i);
	}
	public static PyVal toPyVal( double d ) {
		return new PyFloat(d);
	}
	public static PyVal toPyVal( boolean b ) {
		return PyBool.valueOf(b);
	}
	public static PyVal toPyVal( Object obj ) {
		if( obj instanceof String s ) {
			
		}
	}

	
	
	
}

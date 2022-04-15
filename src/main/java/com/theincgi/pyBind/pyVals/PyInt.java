package com.theincgi.pyBind.pyVals;

import java.util.Objects;
import java.util.WeakHashMap;

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
	public boolean isInt() {
		return true;
	}
	
	@Override
	public int toInt() {
		return value;
	}
	
	@Override
	public String getType() {
		return "int";
	}
	
	@Override
	public String toJString() {
		return value+"";
	}
	
	@Override
	public PyFloat checkDouble() {
		return new PyFloat(value);
	}
	
	@Override
	public boolean isDouble() {
		return true;
	}
	@Override
	public PyInt checkInt() {
		return this;
	}
	
	@Override
	public double toDouble() {
		return value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PyInt other = (PyInt) obj;
		return value == other.value;
	}
	
	
}

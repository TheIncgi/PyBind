package com.theincgi.pyBind.pyVals;

import java.util.LinkedHashMap;

import com.theincgi.pyBind.PyBindException;

public class PyRef extends PyVal {
	
	private final String uuid;
	private PyVal evaluated;
	
	public PyRef(String uuid) {
		this.uuid = uuid;
	}
	
	protected void checkRef() {
		if(evaluated==null)
			throw new PyBindUnevaluatedRef("Python variable reference doesn't have a stored value. Call `eval()` or `set` first.");
	}
	
	public PyRef eval() {
		//TODO eval
		return this;
	}
	public PyRef set(PyVal val) {
		evaluated = val;
		//TODO update remote var
		return this;
	}
	
	@Override
	public boolean isRef() {
		return true;
	}
	
	
	/////////////////////
	//  proxy methods  //
	/////////////////////
	
	@Override
	public PyVal call(Object... values) {
		checkRef();
		return evaluated.call(values);
	}

	@Override
	public PyVal invoke(Object... values) {
		checkRef();
		return evaluated.invoke(values);
	}

	@Override
	public String getType() {
		checkRef();
		return evaluated.getType();
	}


	@Override
	public int toInt() {
		checkRef();
		return evaluated.toInt();
	}

	@Override
	public boolean isInt() {
		checkRef();
		return evaluated.isInt();
	}

	@Override
	public String toJString() {
		checkRef();
		return evaluated.toJString();
	}

	@Override
	public String toString() {
		checkRef();
		return evaluated.toString();
	}

	@Override
	public boolean isString() {
		checkRef();
		return evaluated.isString();
	}

	@Override
	public double toDouble() {
		checkRef();
		return evaluated.toDouble();
	}

	@Override
	public boolean isDouble() {
		checkRef();
		return evaluated.isDouble();
	}

	@Override
	public boolean isNum() {
		checkRef();
		return evaluated.isDouble();
	}

	@Override
	public boolean toBool() {
		checkRef();
		return evaluated.toBool();
	}

	@Override
	public boolean isBool() {
		checkRef();
		return evaluated.isBool();
	}

	@Override
	public int len() {
		checkRef();
		return evaluated.len();
	}

	@Override
	public PyVal attrib(String name) {
		checkRef();
		return evaluated.attrib(name);
	}

	@Override
	public PyVal index(int a) {
		checkRef();
		return evaluated.index(a);
	}

	@Override
	public PyVal index(int a, int b) {
		checkRef();
		return evaluated.index(a, b);
	}

	@Override
	public LinkedHashMap<PyVal, PyVal> toMap() {
		checkRef();
		return evaluated.toMap();
	}
	
	public static class PyBindUnevaluatedRef extends PyBindException {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3859425468519809344L;

		public PyBindUnevaluatedRef() {
			super();
		}

		public PyBindUnevaluatedRef(String message, Throwable cause, boolean enableSuppression,
				boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}

		public PyBindUnevaluatedRef(String message, Throwable cause) {
			super(message, cause);
		}

		public PyBindUnevaluatedRef(String message) {
			super(message);
		}

		public PyBindUnevaluatedRef(Throwable cause) {
			super(cause);
		}
		
	}
	
}

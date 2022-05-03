package com.theincgi.pyBind.pyVals;

import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

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
	public PyVal call(JSONArray args, JSONObject kwargs) {
		checkRef();
		return evaluated.call(args,kwargs);
	}

	@Override
	public PyVal invoke(JSONArray args, JSONObject kwargs) {
		checkRef();
		return evaluated.invoke(args, kwargs);
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
	public String toStr() {
		checkRef();
		return evaluated.toStr();
	}

	@Override
	public String toString() {
		checkRef();
		return evaluated.toString();
	}

	@Override
	public boolean isStr() {
		checkRef();
		return evaluated.isStr();
	}

	@Override
	public double toDouble() {
		checkRef();
		return evaluated.toDouble();
	}
	
	
	@Override
	public boolean isFloat() {
		checkRef();
		return evaluated.isFloat();
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
	public PyVal index(Integer a, Integer b) {
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

	@Override
	public JSONObject asJsonValue() {
		checkRef();
		return evaluated.asJsonValue();
	}
	
}

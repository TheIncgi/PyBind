package com.theincgi.pyBind.pyVals;

import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.theincgi.pyBind.PyBind;
import com.theincgi.pyBind.PyBindException;

public class PyRef extends PyVal {
	
	private final long ref;
//	private PyVal evaluated;
	
	public PyRef(long ref) {
		if(ref < 0)
			throw new PyBindException("Invalid reference ("+ref+")");
		this.ref = ref;
	}
	
	public PyVal get() {
		return PyBind.getSocketHandler().get( ref );
	}
	public void set(PyVal val) {
		
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
		return evaluated.call(args,kwargs);
	}

	@Override
	public PyVal invoke(JSONArray args, JSONObject kwargs) {
		return evaluated.invoke(args, kwargs);
	}

	@Override
	public String getType() {
		return PyBind.getSocketHandler().refType( ref );
	}

	@Override
	public PyBool checkBool() {
		PyVal v = get();
		if(v.isRef()) super.checkBool();
		return v.checkBool();
	}
	
	@Override
	public PyFloat checkDouble() {
		PyVal v = get();
		if(v.isRef()) return super.checkDouble();
		return v.checkDouble();
	}
	
	@Override
	public PyFunc checkFunction() {
		PyVal v = get();
		if(v.isRef()) return super.checkFunction();
		return v.checkFunction();
	}
	
	@Override
	public PyInt checkInt() {
		PyVal v = get();
		if(v.isRef()) return super.checkInt();
		return v.checkInt();
	}
	
	@Override
	public PyList checkList() {
		PyVal v = get();
		if(v.isRef()) return super.checkList();
		return v.checkList();
	}
	@Override
	public PyStr checkPyStr() {
		PyVal v = get();
		if(v.isRef()) return super.checkPyStr();
		return v.checkPyStr();
	}
	@Override
	public PyTuple checkTuple() {
		PyVal v = get();
		if(v.isRef()) return super.checkTuple();
		return v.checkTuple();
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
		JSONObject obj = new JSONObject();
		obj.put("type", "ref");
		obj.put("ref", ref);
		return obj;
	}
	
}

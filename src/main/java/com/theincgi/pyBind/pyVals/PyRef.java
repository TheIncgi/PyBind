package com.theincgi.pyBind.pyVals;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.theincgi.pyBind.NotImplementedException;
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
	public PyVal call(JSONArray args, JSONObject kwargs) throws PyBindException {
		try {
			return PyBind.getSocketHandler().call(ref, args, kwargs);
		} catch (JSONException | PyBindException | InterruptedException | ExecutionException | IOException e) {
			throw new PyBindException(e);
		}
	}
	
	@Override
	public PyVal invoke(JSONArray args, JSONObject kwargs) throws PyBindException {
		try {
			return PyBind.getSocketHandler().invoke(ref, args, kwargs);
		} catch (JSONException | PyBindException | InterruptedException | ExecutionException | IOException e) {
			throw new PyBindException(e);
		}
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
		if( PyBind.getSocketHandler().isCallable(ref) )
			return new PyFunc( ref );
		return super.checkFunction();
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
	public PyVal attrib(String name, boolean asRef) {
		return PyBind.getSocketHandler().attrib(ref, name, asRef);
	}

	@Override
	public int toInt() {
		throw new NotImplementedException();
	}

	@Override
	public boolean isInt() {
		throw new NotImplementedException();
	}

	@Override
	public String toStr() {
		throw new NotImplementedException();
	}

	@Override
	public String toString() {
		throw new NotImplementedException();
	}

	@Override
	public boolean isStr() {
		throw new NotImplementedException();
	}

	@Override
	public double toDouble() {
		throw new NotImplementedException();
	}
	
	
	@Override
	public boolean isFloat() {
		throw new NotImplementedException();
	}

	@Override
	public boolean toBool() {
		throw new NotImplementedException();
	}

	@Override
	public boolean isBool() {
		throw new NotImplementedException();
	}

	@Override
	public int len() {
		throw new NotImplementedException();
	}

	@Override
	public PyVal index(int a) {
		throw new NotImplementedException();
	}

	@Override
	public PyVal index(Integer a, Integer b) {
		throw new NotImplementedException();
	}

	@Override
	public LinkedHashMap<PyVal, PyVal> toMap() {
		throw new NotImplementedException();
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

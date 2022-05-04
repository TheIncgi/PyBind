package com.theincgi.pyBind.pyVals;

import static com.theincgi.pyBind.PyBindSockerHandler.Actions.CALL;
import static com.theincgi.pyBind.PyBindSockerHandler.ResultMode.COPY;
import static com.theincgi.pyBind.PyBindSockerHandler.ResultMode.REF;

import java.io.IOException;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.theincgi.pyBind.PyBind;
import com.theincgi.pyBind.PyBindException;
import com.theincgi.pyBind.PyBindSockerHandler;
import com.theincgi.pyBind.PyBindSockerHandler.Actions;
import com.theincgi.pyBind.PyBindSockerHandler.ResultMode;

public class PyFunc extends PyVal {
	public static final String TYPENAME = "function";
	private final long ref;
	
	
	public PyFunc(long refUUID) {
		this.ref = refUUID;
	}
	
	public PyFunc valueOf(String lib, String name) throws InterruptedException, ExecutionException, IOException {
		return PyBind.getSocketHandler().bind(lib, name).checkFunction();
	}
	
	
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
		return TYPENAME;
	}
	
	@Override
	public boolean isFunc() {
		return true;
	}
	
	@Override
	public PyFunc checkFunction() {
		return this;
	}
	
	@Override
	public String toStr() {
		return "<function>";
	}
	
	@Override
	public JSONObject asJsonValue() {
		JSONObject obj = new JSONObject();
		obj.put("type", TYPENAME);
		obj.put("ref", ref);
		return obj;
	}
	
}

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
	private final String refUUID;
	
	
	public PyFunc(String refUUID) {
		this.refUUID = refUUID;
	}
	
	public PyFunc valueOf(String lib, String name) throws InterruptedException, ExecutionException, IOException {
		return PyBind.getSocketHandler().bind(lib, name).checkFunction();
	}
	
	
	@Override
	public PyVal call(JSONArray args, JSONObject kwargs) throws PyBindException {
		try {
			return PyBind.getSocketHandler().call(args);
		} catch (JSONException | PyBindException | InterruptedException | ExecutionException | IOException e) {
			throw new PyBindException(e);
		}
	}
	
	@Override
	public PyVal invoke(JSONArray args, JSONObject kwargs) throws PyBindException {
		try {
			return PyBind.getSocketHandler().invoke(args, kwargs);
		} catch (JSONException | PyBindException | InterruptedException | ExecutionException | IOException e) {
			throw new PyBindException(e);
		}
	}
	
	@Override
	public String getType() {
		return "function";
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
	
}

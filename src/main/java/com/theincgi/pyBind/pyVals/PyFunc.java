package com.theincgi.pyBind.pyVals;

import static com.theincgi.pyBind.PyBindSockerHandler.Actions.CALL;
import static com.theincgi.pyBind.PyBindSockerHandler.ResultMode.COPY;
import static com.theincgi.pyBind.PyBindSockerHandler.ResultMode.REF;

import java.io.IOException;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutionException;

import com.theincgi.pyBind.PyBind;
import com.theincgi.pyBind.PyBindSockerHandler;
import com.theincgi.pyBind.PyBindSockerHandler.Actions;
import com.theincgi.pyBind.PyBindSockerHandler.ResultMode;

public class PyFunc extends PyVal {
	private final String refUUID;
	
	
	private PyFunc(String refUUID) {
		this.refUUID = refUUID;
	}
	
	public PyFunc valueOf(String lib, String name) throws InterruptedException, ExecutionException, IOException {
		return PyBind.getSocketHandler().bind(lib, name).checkFunction();
	}
	
	@Override
	public PyVal call(Object... values) {
		return PyBind.getSocketHandler().send(CALL, COPY, values).orElse(PyVal.NONE);
	}
	
	@Override
	public PyVal invoke(Object... values) {
		return PyBind.getSocketHandler().send(CALL, REF, values).orElse(PyVal.NONE);
	}
	
	
	@Override
	public PyFunc checkFunction() {
		return this;
	}
}

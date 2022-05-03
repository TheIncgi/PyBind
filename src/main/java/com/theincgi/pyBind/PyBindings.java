package com.theincgi.pyBind;

import com.theincgi.pyBind.pyVals.PyVal;

public interface PyBindings {
	@Py(lib="pybind",name="getVersion")
	public String getVersion();
	
	@Py(lib="pybind",name="getattr")
	public PyVal getAttrib(PyVal obj, String atrib);
}

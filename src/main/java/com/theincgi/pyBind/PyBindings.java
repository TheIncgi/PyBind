package com.theincgi.pyBind;

public interface PyBindings {
	@Py(lib="pybind",name="getVersion")
	public String getVersion();
	
	@Py(lib="pybind",name="getattr")
	public PyVal getAttrib(PyVal obj, String)
}

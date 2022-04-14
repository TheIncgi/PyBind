package com.theincgi.pyBind;

public interface PyBindings {
	@Py(lib="pybind",name="getVersion")
	public String getVersion();
}

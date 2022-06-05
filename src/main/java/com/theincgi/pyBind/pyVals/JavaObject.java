package com.theincgi.pyBind.pyVals;

import java.util.LinkedHashMap;

import org.json.JSONObject;

public class JavaObject extends PyVal {
	public static final String TYPENAME = "java_object"; 
	
	private long refID;
	
	public JavaObject( long refID ) {
		JavaBinds.checkExists( refID );
		refID = refID;
	}
	
	public JavaObject( Object obj ) {
		refID = JavaBinds.bind( obj );
	}
	
	public Object getObject() {
		return JavaBinds.get( refID );
	}
	
	public Class<?> getObjectType() {
		return getObject().getClass();
	}
	
	@Override
	public String getType() {
		return TYPENAME;
	}

	@Override
	public boolean isJavaObj() {
		return true;
	}
	
	@Override
	public JavaObject checkJavaObject() {
		return this;
	}
	
	@Override
	public String toStr() {
		return getObject().toString();
	}
	
	@Override
	public JSONObject asJsonValue() {
		JSONObject obj = new JSONObject();
		obj.put("type", TYPENAME);
		obj.put("ref", refID);
		return obj;
	}

}

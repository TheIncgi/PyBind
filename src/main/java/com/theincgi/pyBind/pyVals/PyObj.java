package com.theincgi.pyBind.pyVals;

import static com.theincgi.pyBind.PyBind.eval;
import static com.theincgi.pyBind.PyBindSockerHandler.Actions.CALL;
import static com.theincgi.pyBind.PyBindSockerHandler.ResultMode.COPY;
import static com.theincgi.pyBind.PyBindSockerHandler.ResultMode.REF;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.theincgi.pyBind.NotImplementedException;
import com.theincgi.pyBind.PyBind;

public class PyObj extends PyVal {
	private String refUUID;
	
	public PyObj(String ref) {
		refUUID = ref;
	}
	
	
	@Override
	public PyVal call(JSONArray args) {
		return PyBind.getSocketHandler().send(CALL, COPY, args).orElse(PyVal.NONE);
	}
	@Override
	public PyVal call(JSONObject kwargs) {
	}
	@Override
	public PyVal call(JSONArray args, JSONObject kwargs) {
	}
	@Override
	public PyVal call(Object... values) {
	}
	
	@Override
	public PyVal invoke(Object... values) {
		return PyBind.getSocketHandler().send(CALL, REF, values).orElse(PyVal.NONE);
	}
	
	@Override
	public String getType() {
		return eval("type( $1 )", this).checkPyStr().toStr();
	}
	
	@Override
	public boolean isNone() {
		return eval("$1 is None", this).checkBool().toBool();
	}
	
	@Override
	public boolean isObj() {
		return true;
	}
	
	@Override
	public boolean isRef() {
		return true;
	}
	
	@Override
	public boolean isFunc() {
		return eval("callable($1)", this).checkBool().toBool();
	}
	
	@Override
	public PyFunc checkFunction() {
		if( isFunc() )
			return new PyFunc(refUUID);
		return super.checkFunction();
	}
	
	@Override
	public int toInt() {
		return eval("int($1)", this).checkInt().toInt();
	}
	
	@Override
	public int toInt(int defValue) {
		try {
			return toInt();
		}catch (Exception e) {
			return defValue;
		}
	}
	
	@Override
	public boolean isInt() {
		return eval("isinstance($1, (int,))", this).checkBool().toBool();
	}
	
	@Override
	public PyInt checkInt() {
		if(isInt())
			return PyInt.valueOf(toInt());
		return super.checkInt();
	}
	
	@Override
	public PyInt intVal() {
		return PyInt.valueOf(toInt());
	}
	
	@Override
	public PyInt intVal(int defValue) {
		return PyInt.valueOf(toInt(defValue));
	}
	
	@Override
	public String toStr() {
		return eval("str($1)", this).checkPyStr().toStr();
	}
	
	@Override
	public boolean isStr() {
		return eval("isinstance($1, str)", this).checkBool().toBool();
	}
	
	@Override
	public double toDouble() {
		return eval("float($1)", this).checkDouble().toDouble();
	}
	
	@Override
	public double toDouble(double defValue) {
		try {
			return toDouble();
		}catch (Exception e) {
			return defValue;
		}
	}
	
	@Override
	public boolean isFloat() {
		return eval("isinstance($1, (float,))", this).checkBool().toBool();
	}
	
	@Override
	public PyFloat checkDouble() {
		if(isFloat())
			return new PyFloat(toDouble());
		return super.checkDouble();
	}
	
	@Override
	public PyFloat floatVal() {
		if(isFloat())
			return new PyFloat(toDouble());
		return super.floatVal();
	}
	
	@Override
	public boolean isTuple() {
		return eval("isinstance($1, (tuple,))",this).checkBool().toBool();
	}
	
	@Override
	public PyTuple checkTuple() {
		throw new NotImplementedException();
	}
	
	@Override
	public PyTuple tupleVal() {
		throw new NotImplementedException();
	}
	
	@Override
	public boolean isList() {
		throw new NotImplementedException();
	}
	
	@Override
	public PyList checkList() {
		throw new NotImplementedException();
	}
	
	@Override
	public PyList toList() {
		throw new NotImplementedException();
	}
	
	@Override
	public PyVal[] toArray() {
		throw new NotImplementedException();
	}
	
	@Override
	public boolean isIndexable() {
		throw new NotImplementedException();
	}
	
	@Override
	public boolean toBool() {
		throw new NotImplementedException();
	}
	
	@Override
	public PyBool checkBool() {
		throw new NotImplementedException();
	}
	
	@Override
	public PyBool boolVal() {
		throw new NotImplementedException();
	}
	
	@Override
	public int len() {
		throw new NotImplementedException();
	}
	
	@Override
	public PyVal attrib(String name) {
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
	public boolean isDict() {
		throw new NotImplementedException();
	}
	
	@Override
	public LinkedHashMap<PyVal, PyVal> toMap() {
		throw new NotImplementedException();
	}
	
	@Override
	public boolean isPyGen() {
		throw new NotImplementedException();
	}
	
	@Override
	public PyVal next() {
		throw new NotImplementedException();
	}
	
	@Override
	public Object asJsonValue() {
		throw new NotImplementedException();
	}
	
}

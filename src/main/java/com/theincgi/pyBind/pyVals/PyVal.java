package com.theincgi.pyBind.pyVals;

import static com.theincgi.pyBind.Common.expected;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

import com.theincgi.pyBind.Common;
import com.theincgi.pyBind.NotImplementedException;
import com.theincgi.pyBind.PyBind;
import com.theincgi.pyBind.PyBindException;
import com.theincgi.pyBind.PyTypeMismatchException;


//___Val() -> cast when possible to Py____
//check___ -> require type match, return as Py_____ else Type Exception
//to___    -> java representation or Type Exception
//to___(def)->java reperesentation with a default if it can't convert
//is___    -> type is exactly ____
public abstract class PyVal {
	public static final PyNone NONE = new PyNone();
	public static final PyBool TRUE = PyBool.TRUE,
						       FALSE = PyBool.FALSE;
	
	public PyVal() {
	}
	
	public final PyVal call(Object... args) {
		JSONArray json = new JSONArray();
		for(Object o : args) 
			json.put( toPyVal(o).asJsonValue() );
		return call( json );
	}
	public final PyVal call(JSONArray args)  {
		return call(args, new JSONObject());
	}
	public final PyVal call(JSONObject kwargs)  {
		return call(new JSONArray(), kwargs);
	}
	public PyVal call(JSONArray args, JSONObject kwargs)  {
		throw new PyBindException("Attempt to call "+getType());
	}
	
	public final PyVal invoke(Object... args) {
		JSONArray json = new JSONArray();
		for(Object o : args) 
			json.put( toPyVal(o).asJsonValue() );
		return call( json );
	}
	public final PyVal invoke(JSONArray args) {
		return invoke( args, new JSONObject() );
	}
	public final PyVal invoke(JSONObject kwargs) {
		return invoke( new JSONArray(), kwargs );
	}
	public PyVal invoke(JSONArray args, JSONObject kwargs) {
		throw new PyBindException("Attempt to invoke "+getType());
	}
	
	/**
	 * shortcut of call
	 */
	public final PyVal c(Object... args)  {
		return call(args);
	}
	/**
	 * shortcut of call
	 */
	public final PyVal c(JSONArray args)  {
		return call(args);
	}
	/**
	 * shortcut of call
	 */
	public final PyVal c(JSONObject kwargs)  {
		return call(kwargs);
	}
	/**
	 * shortcut of call
	 */
	public final PyVal c(JSONArray args, JSONObject kwargs)  {
		return call(args, kwargs);
	}
	
	/**
	 * shortcut of invoke
	 */
	public final PyVal i(Object... args) {
		return invoke(args);
	}
	/**
	 * shortcut of invoke
	 */
	public final PyVal i(JSONArray args) {
		return invoke(args);
	}
	/**
	 * shortcut of invoke
	 */
	public final  PyVal i(JSONObject kwargs) {
		return invoke(kwargs);
	}
	/**
	 * shortcut of invoke
	 */
	public final PyVal i(JSONArray args, JSONObject kwargs) {
		return invoke(args, kwargs);
	}
	
	
	/**
	 * Returns the type name of this value as described in<br>
	 * python's <code>type</code> function (without "class" or brackets)<br>
	 * same as <code>type( x ).__name__</code><br>
	 * Ex: NoneType
	 * */
	public abstract String getType();
	
	/**
	 * Returns true if this value is NoneType
	 * */
	public boolean isNone() {
		return false;
	}
	
	public boolean isObj() {
		return false;
	}
	
	/**
	 * Returns true if this is a reference to something in<br>
	 * the python environment.<br>
	 * */
	public boolean isRef() {
		return false;
	}
	
	/**
	 * if {@link #isRef()} then<br>
	 * return value as {@link PyRef}
	 * else throw {@link PyTypeMismatchException}
	 * */
	public PyRef checkPyRef() {
		throw new PyTypeMismatchException( Common.expected("ref", getType()) );
	}
	
	/**
	 * Returns true if using {@link #call(Object...)} or {@link #invoke(Object...)}<br>
	 * on this value will not cause a type error
	 * */
	public boolean isFunc() {
		return false;
	}
	
	/**
	 * Throw an error if this value is not a function<br>
	 * else return as PyFunc
	 * */
	public PyFunc checkFunction() {
		throw new PyTypeMismatchException(Common.expected(PyFunc.TYPENAME, getType()));
	}
	
	//int
	/**
	 * Result of python's <code>int( val )</code><br>
	 * if it can't convert {@link PyTypeMismatchException} is thrown
	 * */
	public int toInt() {
		throw new PyTypeMismatchException("Could not convert %s to int".formatted(getType()));
	}
	/**
	 * Result of python's <code>int( val )</code><br>
	 * if it can't convert {@link PyTypeMismatchException} is thrown
	 * */
	public int toInt(int defValue) {
		return defValue;
	}
	
	/**
	 * Returns true if type is 'int'<br>
	 * false for type 'float'
	 * 
	 * @see PyVal#isNum()
	 * */
	public boolean isInt() {
		return false;
	}
	/**
	 * If this value {@link #isInt} then<br>
	 * return as PyInt<br>
	 * else throw {@link PyTypeMismatchException}<br>
	 * <Br>
	 * @see #intVal
	 * */
	public PyInt checkInt() {
		throw new PyTypeMismatchException( Common.expected(PyInt.TYPENAME, getType()) );
	}
	
	/**
	 * If this value can be converted with <code>int( value )</code> then<br>
	 * return as PyInt<br>
	 * else throw {@link PyTypeMismatchException}
	 * <br>
	 * @see #checkInt()
	 * */
	public PyInt intVal() {
		throw new PyTypeMismatchException( Common.expected(PyInt.TYPENAME, getType()) );
	}
	
	/**
	 * If this value can be converted with <code>int( value )</code> then<br>
	 * return as PyInt<br>
	 * else return PyInt of defValue
	 * <br>
	 * @param defValue default value to use if conversion is invalid
	 * @see #checkInt()
	 * */
	public PyInt intVal( int defValue ) {
		return PyInt.valueOf(defValue);
	}
	
	
	/**
	 * String representation of this value with type info<br>
	 * This also uses the value from {@link #toStr()}<br>
	 * <b>Ex:</b><br>
	 * <code>class &lt;int&gt;[COPY] 3</code>
	 * */
	@Override
	public String toString() {
		return "class <%s>%S %s".formatted(getType(), isRef()?" [REF]":"[COPY]", toStr());
	}
	
	/**
	 * Representation of just the value as a String.<br>
	 * Unlike {@link #toString()} this will not show the type info<br>
	 * This returns the same result as str( value ) in python
	 * */
	public abstract String toStr();

	/**
	 * Returns true if value type is string.
	 * */
	public boolean isStr() {
		return false;
	}
	
	/**
	 * if {@link #isStr()} then<br>
	 * return value as {@link PyStr}
	 * else throw {@link PyTypeMismatchException}
	 * */
	public PyStr checkPyStr() {
		throw new PyTypeMismatchException( Common.expected("str", getType()) );
	}
	
	/**
	 * returns this value as a {@link PyStr} using {@link #toStr()}
	 * */
	public PyStr strVal() {
		return new PyStr(toStr());
	}
	
	//float
	/**
	 * if float( value ) is valid then<br>
	 * returns this value as a java double<br>
	 * else throws {@link PyTypeMismatchException}<br>
	 * <br>
	 * <b>Note</b>Python float is equivilant to a java double
	 * */
	public double toDouble() {
		throw new PyTypeMismatchException( Common.expected("float", getType()) );
	}
	/**
	 * if float( value ) is valid then<br>
	 * returns this value as a java double<br>
	 * else return defValue<br>
	 * <br>
	 * <b>Note</b>Python float is equivalent to a java double
	 * */
	public double toDouble(double defValue) {
		return defValue;
	}
	/**
	 * Python float is 8 bytes, use {@link #toDouble()} instead<br>
	 * this method will throw a {@link RuntimeException}
	 * */
	@Deprecated
	public final double toFloat() {
		throw new RuntimeException("See javadoc (use toDouble)");
	}
	/**
	 * Return true if this value is type float<br>
	 * (java double equivalent)
	 * @see #isNum()
	 * */
	public boolean isFloat() {
		return false;
	}
	
	/**
	 * If {@link #isFloat()} or {@link #isInt()} then<br>
	 * returns this as PyFloat<br>
	 * else throw {@link PyTypeMismatchException}<br>
	 * */
	public PyFloat checkDouble() {
		throw new PyTypeMismatchException( Common.expected("float", getType()) );
	}
	
	/**
	 * if float( value ) is valid<br>
	 * return this as a {@link PyFloat}<br>
	 * else throw {@link PyTypeMismatchException}
	 * */
	public PyFloat floatVal() {
		throw new PyTypeMismatchException( Common.expected("float", getType()) );
	}
	
	/**
	 * if float( value ) is valid<br>
	 * return this as a {@link PyFloat}<br>
	 * else return defValue as {@link PyFloat}
	 * */
	public PyFloat floatVal(double defValue) {
		throw new PyTypeMismatchException( Common.expected("float", getType()) );
	}
	
	//tuple
	/**
	 * Return true if this is a tuple
	 * */
	public boolean isTuple() {
		return false;
	}
	/**
	 * if {@link #isTuple()} then<br>
	 * return this as {@link PyTuple}<br>
	 * else throw {@link PyTypeMismatchException}
	 * */
	public PyTuple checkTuple() {
		throw new PyTypeMismatchException( Common.expected("tuple", getType()) );
	}
	
	/**
	 * if {@link #isTuple()} then<br>
	 * return this as {@link PyTuple}<br>
	 * else return a tuple containing only this value
	 * */
	public PyTuple tupleVal() {
		return new PyTuple(this);
	}
	
	//list
	/**
	 * Return true if this is a list<br>
	 * 
	 * @see #isIndexable()
	 * */
	public boolean isList() {
		return false;
	}
	
	/**
	 * if {@link #isList()} then <br>
	 * return as {@link PyList}<br> 
	 * else throw {@link PyTypeMismatchException}
	 * */
	public PyList checkList() {
		throw new PyTypeMismatchException( Common.expected("list", getType()) );
	}
	
	/**
	 * Returns equivilant of list( value )
	 * */
	public PyList toList() {
		return new PyList(this);
	}
	
	/**
	 * if {@link #isTuple()} or {@link #isList()}<br>
	 * return a array of PyVal<br>
	 * else throw {@link PyTypeMismatchException}
	 * */
	public PyVal[] toArray() {
		throw new PyTypeMismatchException( Common.expected("list or tuple", getType()) );
	}
	
	/**
	 * Return true if using [] operator on this value is valid
	 * */
	public boolean isIndexable() {
		return false;
	}
	//boolean
	
	/**
	 * returns equivalent of bool( value )
	 * */
	public boolean toBool() {
		return true;
	}
	
	/**
	 * return true if this value is a boolean
	 * */
	public boolean isBool() {
		return false;
	}
	
	/**
	 * if {@link #isBool()} then<br>
	 * return this as {@link PyBool}<br>
	 * else throw new {@link PyTypeMismatchException}
	 * */
	public PyBool checkBool() {
		throw new PyTypeMismatchException( Common.expected("bool", getType()) );
	}
	
	/**
	 * return this value as a PyBool<br>
	 * uses {@link #toBool()} if it is not already a boolean
	 * */	
	public PyBool boolVal() {
		return toBool() ? TRUE : FALSE;
	}
	
	/**
	 * returns true if the type is PyInt or PyFloat
	 * */
	public final boolean isNum() {
		return isInt() || isFloat();
	}
	
	/**
	 * return the result of len( value )<br>
	 * may through {@link PyBindException} if it is not valid for this type
	 * */
	public int len() {
		throw new PyTypeMismatchException("object of type '%s' has no len()".formatted(getType()));
	}
	
	/**
	 * return an attribute from an object (. operator)<br>
	 * may through {@link PyBindException} if it is not valid for this type 
	 * */
	public PyVal attrib(String name) {
		return PyBind.getSocketHandler().attrib(this, name);
	}
	
	/**
	 * this[a]<br>
	 * may through {@link PyBindException} if it is not valid for this type
	 * */
	public PyVal index(int a) {
		throw new PyTypeMismatchException( Common.expected("list or tuple", getType()) );
	}
	
	/**
	 * this[a]<br>
	 * may through {@link PyBindException} if it is not valid for this type<br>
	 * returns def if a is out of bounds or does not exist in dictionary
	 * */
	public PyVal index(int a, PyVal def) {
		throw new PyTypeMismatchException( Common.expected("list or tuple", getType()) );		
	}
	

	/**
	 * this[a:b]<br>
	 * may through {@link PyBindException} if it is not valid for this type<br>
	 * <b>a and b are both nullable</b>
	 * */
	public PyVal index(Integer a, Integer b) {
		throw new PyTypeMismatchException( Common.expected("list or tuple", getType()) );
	}
	
	/**
	 * this[a:b]<br>
	 * may through {@link PyBindException} if it is not valid for this type<br>
	 * returns def if a is out of bounds or does not exist in dictionary
	 * */
	public PyVal index(Integer a, Integer b, PyVal def) {
		throw new PyTypeMismatchException( Common.expected("list or tuple", getType()) );		
	}
	
	/**
	 * return true if this is a dictionary
	 * */
	public boolean isDict() {
		return false;
	}
	
	public PyDict checkDict() {
		throw new PyTypeMismatchException( Common.expected(PyDict.TYPENAME, getType()) );
	}
	
	
	public PyVal index(String str) {
		throw new PyTypeMismatchException( Common.expected("dict", getType()) );
	}
	public PyVal index(String str, PyVal def) {
		throw new PyTypeMismatchException( Common.expected("dict", getType()) );
	}
	
	/**
	 * this[ v ] or this.get( v ) for dict<br>
	 * if this and v are both ref, then v's value is retrieved
	 * */
	public PyVal index(PyVal v) {
		throw new PyTypeMismatchException( Common.expected("list, dict or tuple", getType()) );		
	}
	/**
	 * this[ v ] or this.get( v, def ) for dict<br>
	 * if this and v are both ref, then v's value is retrieved
	 * */
	public PyVal index(PyVal v, PyVal def) {
		throw new PyTypeMismatchException( Common.expected("list, dict or tuple", getType()) );		
	}
	
	/**
	 * For dictionaries, key value pairs are extracted<br>
	 * For objects, attribute key value pairs are extracted
	 * */
	public abstract LinkedHashMap<PyVal, PyVal> toMap();

	/**
	 * Return true if this is a generator
	 * */
	public boolean isPyGen() {
		return false;
	}
	
	/**
	 * for generators, gets the next value
	 * */
	public PyVal next() {
		throw new PyTypeMismatchException( Common.expected("generator", getType()) );
	}
	
	public abstract JSONObject asJsonValue();
	
	public static PyVal fromJson(JSONObject json) {
		//TODO DECODE JSON
		String           type = json.getString("type");
		String            val = json.optString("val");
		long              ref = json.optLong("ref",-1);
		
		boolean ordered = false;
		
		switch( type ) {
			case PyBool.TYPENAME:
				return PyBool.toPyVal( val.equals("True"));
			
			case PyFloat.TYPENAME:
				return new PyFloat( Double.parseDouble(val) );

			case PyFunc.TYPENAME:
				return new PyFunc( ref );
				
			case PyInt.TYPENAME:
				return PyInt.valueOf( Integer.parseInt(val) );
				
			case PyStr.TYPENAME:
				return new PyStr(val);
				
			case PyNone.TYPENAME:
				return NONE;
			
				
			case PyList.TYPENAME:
				return new PyList( json.getJSONArray("val") );
				
			case "odict":
				ordered = true;
			case PyDict.TYPENAME:
				return new PyDict( json.getJSONObject("val"), ordered );
				
			case PyTuple.TYPENAME:
			
			case PyGen.TYPENAME:
			default:
				if(ref < 0)
					break;
				System.err.println("couldn't convert "+type+", defaulting to ref");
			case "ref":
				return new PyRef(ref);
				//obj, ref, 
		}
		
		
		throw new RuntimeException("Not implemented");
	}
	
	public final static PyVal toPyVal( int i ) {
		return PyInt.valueOf(i);
	}
	public final static PyVal toPyVal( double d ) {
		return new PyFloat(d);
	}
	public final static PyVal toPyVal( boolean b ) {
		return b ? PyBool.TRUE : PyBool.FALSE;
	}
	public final static PyVal toPyVal( String s ) {
		return new PyStr( s );
	}
	public static PyVal toPyVal( List<?> l ) {
		return new PyList( l );
	}
	public static PyVal toPyVal( Map<?,?> m ) {
		return new PyDict( m );
	}
	
	/**
	 * Best py val match of obj
	 * */
	public static PyVal toPyVal( Object obj ) {
		if( obj instanceof PyVal v )
			return v;
		if( obj instanceof String s )
			return toPyVal( s );
		if( obj instanceof Integer i)
			return toPyVal((int)i);
		if( obj instanceof Double d)
			return toPyVal((double)d);
		if( obj instanceof Float d)
			return toPyVal((float)d);
		if( obj instanceof Boolean b)
			return toPyVal((boolean)b);
		if( obj instanceof List l)
			return toPyVal( l );
		if( obj instanceof Map<?, ?> m)
			return toPyVal( m );
		
		throw new NotImplementedException("type conversion for " + obj.getClass().toString() + " isn't setup!");
	}

	
	
	
}

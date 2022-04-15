package com.theincgi.pyBind.pyVals;

import static com.theincgi.pyBind.Common.expected;

import java.util.LinkedHashMap;
import java.util.Optional;

import org.json.JSONObject;

import com.theincgi.pyBind.Common;
import com.theincgi.pyBind.PyBindException;
import com.theincgi.pyBind.PyTypeMismatchException;


//___Val() -> cast when possible to Py____
//check___ -> require type match, return as Py_____ else Type Exception
//to___    -> java representation or Type Exception
//to___(def)->java reperesentation with a default if it can't convert
//is___    -> type is exactly ____
public abstract class PyVal {
	public static final PyVal NONE = new PyNone(),
						      TRUE = PyBool.TRUE,
						      FALSE = PyBool.FALSE;
	
	public PyVal() {
	}
	
	public abstract PyVal call(Object... values);
	
	public abstract PyVal invoke(Object... values);
	
	/**
	 * shortcut of call
	 */
	public final PyVal c(Object...values) {
		return call(values);
	}
	/**
	 * shortcut of invoke
	 */
	public final PyVal i(Object...values) {
		return invoke(values);
	}
	
	/**
	 * Returns the type name of this value as described in<br>
	 * python's <code>type</code> function (without "class" or brackets)<br>
	 * Ex: NoneType
	 * */
	public abstract String getType();
	
	/**
	 * Returns true if this value is NoneType
	 * */
	public abstract boolean isNone();
	
	/**
	 * Returns true if this is a reference to something in<br>
	 * the python environment.<br>
	 * Referenced values must be evaluated or set before they can be inspected.
	 * */
	public boolean isRef() {
		return false;
	}
	
	/**
	 * Returns true if using {@link #call(Object...)} or {@link #invoke(Object...)}<br>
	 * on this value will not cause a type error
	 * */
	public abstract boolean isFunc();
	
	/**
	 * Throw an error if this value is not a function<br>
	 * else return as PyFunc
	 * */
	public PyFunc checkFunction() {
		throw new PyTypeMismatchException(expected("int", getType()));
	}
	
	//int
	/**
	 * Result of python's <code>int( val )</code><br>
	 * if it can't convert {@link PyTypeMismatchException} is thrown
	 * */
	public abstract int toInt();
	/**
	 * Result of python's <code>int( val )</code><br>
	 * if it can't convert {@link PyTypeMismatchException} is thrown
	 * */
	public abstract int toInt(int defValue);
	
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
		throw new PyTypeMismatchException(expected("int", getType()));
	}
	
	/**
	 * If this value can be converted with <code>int( value )</code> then<br>
	 * return as PyInt<br>
	 * else throw {@link PyTypeMismatchException}
	 * <br>
	 * @see #checkInt()
	 * */
	public abstract PyInt intVal();
	
	/**
	 * If this value can be converted with <code>int( value )</code> then<br>
	 * return as PyInt<br>
	 * else return PyInt of defValue
	 * <br>
	 * @param defValue default value to use if conversion is invalid
	 * @see #checkInt()
	 * */
	public abstract PyInt intVal( int defValue );
	
	
	/**
	 * String representation of this value with type info<br>
	 * This also uses the value from {@link #toStr()}<br>
	 * <b>Ex:</b><br>
	 * <code>class &lt;int&gt;[COPY] 3</code>
	 * */
	@Override
	public String toString() {
		return "class <%s>%S %s".formatted(getType(), isRef()?" [REF]":"[COPY]", toJString());
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
	public abstract boolean isStr();
	
	/**
	 * if {@link #isStr()} then<br>
	 * return value as {@link PyStr}
	 * else throw {@link PyTypeMismatchException}
	 * */
	public abstract PyStr checkPyStr();
	
	/**
	 * returns this value as a {@link PyStr} using {@link #toStr()}
	 * */
	public abstract PyStr strVal();
	
	//float
	/**
	 * if float( value ) is valid then<br>
	 * returns this value as a java double<br>
	 * else throws {@link PyTypeMismatchException}<br>
	 * <br>
	 * <b>Note</b>Python float is equivilant to a java double
	 * */
	public abstract double toDouble();
	/**
	 * if float( value ) is valid then<br>
	 * returns this value as a java double<br>
	 * else return defValue<br>
	 * <br>
	 * <b>Note</b>Python float is equivalent to a java double
	 * */
	public double toDouble(double defValue);
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
	public abstract boolean isFloat();
	
	/**
	 * If {@link #isFloat()} or {@link #isInt()} then<br>
	 * returns this as PyFloat<br>
	 * else throw {@link PyTypeMismatchException}<br>
	 * */
	public abstract PyFloat checkDouble();
	
	/**
	 * if float( value ) is valid<br>
	 * return this as a {@link PyFloat}<br>
	 * else throw {@link PyTypeMismatchException}
	 * */
	public abstract PyFloat floatVal();
	
	/**
	 * if float( value ) is valid<br>
	 * return this as a {@link PyFloat}<br>
	 * else return defValue as {@link PyFloat}
	 * */
	public abstract PyFloat floatVal(double defValue);
	
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
	public PyTuple checkTuple();
	
	/**
	 * if {@link #isTuple()} then<br>
	 * return this as {@link PyTuple}<br>
	 * else return a tuple containing only this value
	 * */
	public abstract PyTuple tupleVal(); 
	
	//list
	/**
	 * Return true if this is a list<br>
	 * 
	 * @see #isIndexable()
	 * */
	public abstract boolean isList();
	
	/**
	 * if {@link #isList()} then <br>
	 * return as {@link PyList}<br> 
	 * else throw {@link PyTypeMismatchException}
	 * */
	public abstract PyList checkList();
	
	/**
	 * if {@link #isTuple()} or {@link #isList()}<br>
	 * return a array of PyVal<br>
	 * else throw {@link PyTypeMismatchException}
	 * */
	public abstract PyVal[] toArray();
	
	/**
	 * Return true if using [] operator on this value is valid
	 * */
	public abstract boolean isIndexable();
	
	//boolean
	
	/**
	 * returns equivalent of bool( value )
	 * */
	public abstract boolean toBool();
	
	/**
	 * return true if this value is a boolean
	 * */
	public abstract boolean isBool();
	
	/**
	 * if {@link #isBool()} then<br>
	 * return this as {@link PyBool}<br>
	 * else throw new {@link PyTypeMismatchException}
	 * */
	public abstract PyBool checkBool();
	
	/**
	 * return this value as a PyBool<br>
	 * uses {@link #toBool()} if it is not already a boolean
	 * */	
	public abstract PyBool boolVal();
	
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
	public abstract int len();
	
	/**
	 * return an attribute from an object (. operator)<br>
	 * may through {@link PyBindException} if it is not valid for this type 
	 * */
	public PyVal attrib(String name) {
		throw new PyBindException("Attempt to get an attribute ( ."+name+" ) from type '"+getType()+"'");
	}
	
	/**
	 * this[a]<br>
	 * may through {@link PyBindException} if it is not valid for this type
	 * */
	public abstract PyVal index(int a);

	/**
	 * this[a:b]<br>
	 * may through {@link PyBindException} if it is not valid for this type<br>
	 * <b>a and b are both nullable</b>
	 * */
	public abstract PyVal index(Integer a, Integer b);
	
	/**
	 * return true if this is a dictionary
	 * */
	public abstract boolean isDict();
	
	/**
	 * For dictionaries, key value pairs are extracted<br>
	 * For objects, attribute key value pairs are extracted
	 * */
	public abstract LinkedHashMap<PyVal, PyVal> toMap();

	/**
	 * Return true if this is a generator
	 * */
	public abstract boolean isPyGen();
	
	/**
	 * for generators, gets the next value
	 * */
	public abstract PyVal next();
	
	/**
	 * if {@link #isRef()} then<br>
	 * gets the current value<br>
	 * else no-op
	 * */
	public PyVal eval() {
		return this;
	}
	
	public abstract Object asJsonValue();
	
	public static PyVal fromJson(JSONObject json) {
		//TODO DECODE JSON
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
	public static PyVal toPyVal( Object obj ) {
		if( obj instanceof String s ) {
			
		}
	}

	
	
	
}

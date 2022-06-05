package com.theincgi.pyBind.utils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.theincgi.pyBind.Kwarg;
import com.theincgi.pyBind.NotImplementedException;
import com.theincgi.pyBind.pyVals.PyVal;

public class Common {
	public static String expected(String type, String got) {
		return "expected %s, got %s".formatted(type, got);
	}

	public static File findOnPath( String prgm ){
		String[] dirs = System.getenv("PATH").split(";");
		for( String dir: dirs ){
			File tmp = Paths.get( dir, prgm ).toFile();
			if( tmp.canExecute() ) 
				return tmp;
		}
		return null;
	}
	
	public static String findKwargName( Annotation[] ans ) {
		for (int i = 0; i < ans.length; i++) {
			if( ans[i] instanceof Kwarg k) {
				return k.arg();
			}
		}
		return null;
	}
	
	public static <T> boolean isAny( T a, T... any ) {
		for(var c : any)
			if( a.equals(any) )
				return true;
		return false;
	}
	
	public static Object coerce(PyVal val, Class to, Class... listType) {
		
		if(to.isArray()) {
			if(val.isNone()) return null;
			int len = val.len();
			Object x = Array.newInstance(to.arrayType(), len);
			for(int i = 0; i<len; i++)
				Array.set(x, i, coerce(val.index(i), to.arrayType()));
			return x;
		}
		if(to.equals(List.class)) {
			if(val.isNone()) return null;
			Class inner = listType[0];
			Class[] innerList = Arrays.copyOfRange(listType, 1, listType.length);
			ArrayList list = new ArrayList();
			
			for(int i = 0; i<val.len(); i++)
				list.add( coerce(val.index(i), inner, innerList) );
			
			return list;
		}
		if(to.equals(PyVal.class)) {
			return val;
		}
		if(to.equals(int.class)) {
			if(val.isNone()) return 0;
			return val.toInt();
		}
		if(to.equals(Integer.class)) {
			if(val.isNone()) return null;
			return val.toInt();
		}
		if(to.equals(float.class)) {
			if(val.isNone()) return 0;
			return (float)val.toDouble();
		}
		if(to.equals(Float.class)) {
			if(val.isNone()) return null;
			return (float)val.toDouble();
		}
		if(to.equals(double.class)) {
			if(val.isNone()) return 0;
			return val.toDouble();
		}
		if(to.equals(Double.class)) {
			if(val.isNone()) return null;
			return val.toDouble();
		}
		if(to.equals(long.class)) {
			if(val.isNone()) return 0;
			return (long)val.toDouble();
		}
		if(to.equals(Long.class)) {
			if(val.isNone()) return null;
			return (long)val.toDouble();
		}
		if(to.equals(String.class)) {
			if(val.isNone()) return null;
			return val.toStr();
		}
		if(Iterable.class.isAssignableFrom(to.getClass())) {
			throw new NotImplementedException("Iterable not implemented");
//			if(val.isNone()) return null;
			//TODO itterator of generator
		}
		
		throw new NotImplementedException(to.getName()+" not implemented");
		
	}
}

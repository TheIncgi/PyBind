package com.theincgi.pyBind;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.theincgi.pyBind.pyVals.PyFunc;
import com.theincgi.pyBind.pyVals.PyInt;
import com.theincgi.pyBind.pyVals.PyVal;

public class BindJava {
	
	PyFunc callThat = PyBind.bindPy("simple", "callThat");
	PyFunc sampleIterator = PyBind.bindPy("simple", "sampleIterator");
	PyFunc testObject = PyBind.bindPy("simple", "testObject");
	
	@Test
	public void runnable() {
		class Wrapped {
			boolean passed = false;			
		} final Wrapped wrapped = new Wrapped();
		
		PyVal javaFunc = PyBind.bindJava(()->{
			wrapped.passed = true;
		});
		callThat.c( javaFunc );
		assertTrue(wrapped.passed);
	}
	
	
	@Test
	public void Supplier() {
		PyVal javaFunc = PyBind.bindJava(()->{
			return PyInt.valueOf(1234);
		});
		PyVal retVal = callThat.c( javaFunc );
		
		assertEquals( 1234, retVal.checkInt().toInt() );
	}
	
	@Test
	public void consumer() {
		class Wrapped {
			int val = 0;			
		} final Wrapped wrapped = new Wrapped();
		
		PyVal javaFunc = PyBind.bindJava( v->{ wrapped.val = v.checkInt().toInt();} );
		callThat.c( javaFunc, 1234 );
		
		assertEquals(1234, wrapped.val);
	}
	
	@Test
	public void function() {
		PyVal javaFunc = PyBind.bindJava( v -> {
			return PyInt.valueOf( v.checkInt().toInt() + 1 );
		} );
		
		PyVal retVal = callThat.c( 24 );
		
		assertEquals(25, retVal.checkInt().toInt());
	}
	
	@Test
	public void iterator() {
		PyVal javaIter = PyBind.bindJava( new Iterator<PyVal>() {
			int i = 0;
			@Override
			public PyVal next() {
				return PyInt.valueOf( i++ );
			}
			
			@Override
			public boolean hasNext() {
				return true;
			}
		});
		
		PyVal samples = sampleIterator.c( javaIter, 10 );
		
		for(int i = 0; i<samples.len(); i++) {
			int actual = samples.index(i).checkInt().toInt();
			assertEquals(String.format("Expected %d at index %d, got %d", i, i, actual),i, actual);
		}
	}
	
	
	@Test
	public void object() {
		class Something {
			private int x = 10;
			@SuppressWarnings("unused")
			public void setX(int x) {
				this.x = x;
			}
			@SuppressWarnings("unused")
			public int getX() {
				return x;
			}
		} Something something = new Something();
		
		PyVal javaObj = PyBind.bindJava(something);
		
		testObject.c( javaObj, 25 );
		
		assertEquals(25, something.x);
	}
}

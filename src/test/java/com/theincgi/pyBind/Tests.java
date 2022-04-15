package com.theincgi.pyBind;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.junit.Before;
import org.junit.Test;

import com.theincgi.pyBind.pyVals.PyVal;

public class Tests {
	
	PyVal add;
	
	PyEx pyEx;
	
	@Before
	public void init() throws IOException, InterruptedException {
		System.out.println("Initalizing...");
		PyBind.init(); //random port and start python
		add = PyBind.bindPy("simple", "add");
		pyEx = PyBind.bindPy( PyEx.class );
	}

	@Test
	public void byInterface() {
		assertEquals(112, pyEx.add(90, 22));
	}
	
	@Test
	public void byField() {
		assertEquals(112, add.c(90, 22));
	}
	
	@Test
	public void threadTest() {
		Future<Integer> f = Executors.newSingleThreadExecutor().submit(()->{
			return ThreadTest.sub.c(50,9).toInt();
		});
		try {
			assertEquals(41, f.get().intValue());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	
	private interface PyEx {
		@Py(lib="simple",name="add")
		public int add(int a, int b);
	}
	
	/*
	 * interface {
	 *   @PyFunc(lib="numpy",val="foo")
	 *   public int add(int a, int b);
	 * }
	 * 
	 * Func func = PyBind.load("numpy","foo")
	 * */
	
}

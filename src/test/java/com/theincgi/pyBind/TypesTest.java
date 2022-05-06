package com.theincgi.pyBind;

import static com.theincgi.pyBind.PyBind.bindPy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import com.theincgi.pyBind.pyVals.PyDict;
import com.theincgi.pyBind.pyVals.PyList;

public class TypesTest {
	
	@Test
	public void ints() {
		assertTrue(bindPy("simple", "add").call(3, 4).isInt());
	}
	
	@Test
	public void strs() {
		var func = bindPy("simple", "concat");
		var result = func.call("hello", " ", "world");
		var str = result.checkPyStr().toStr();
		assertEquals("hello world", str);
	}
	
	@Test
	public void bool() {
		assertFalse( bindPy("simple","notBool").call(true).checkBool().toBool() );
	}
	
	@Test
	public void list() {
		ArrayList<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(2);
		list.add(3);
		
		PyList pyList = bindPy("simple","reverseList").call(list).checkList();
		for(int i = 0; i<list.size(); i++)
			assertEquals(list.get(i).intValue(), pyList.index(2-i).checkInt().toInt());
	}
	
	@Test
	public void dict() {
		HashMap<String, Integer> map = new HashMap<>();
		map.put("a", 1);
		map.put("b", 2);
		map.put("c", 3);
		
		PyDict dict = bindPy("simple","dictReverse").call(map).checkDict();
		assertEquals("a", dict.index(1).checkPyStr().toStr());
		assertEquals("b", dict.index(2).checkPyStr().toStr());
		assertEquals("c", dict.index(3).checkPyStr().toStr());
	}
	
}

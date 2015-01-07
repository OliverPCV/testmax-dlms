package com.testmax.samples.cassandra;




import org.junit.Test;

import junit.framework.TestCase;
import com.testmax.handler.BaseHandler;


public class CassandraTest extends TestCase {
	

	public static void main(String args[]) {
		junit.textui.TestRunner.run(CassandraTest.class);
	}
	@Override
	protected void setUp() {		
	}
	
	@Test
	public void testApiName() {
		String apiname=BaseHandler.getDeclaredVariable("apiname");
		String expected="mytest";
		assertTrue(apiname.contains(expected));
		
	}
	
	@Test
	public void testApiBasePath() {
		String basepath=BaseHandler.getDeclaredVariable("basepath");
		String expected="weather";
		assertTrue(basepath.contains(expected));
		
	}
	
	@Test
	public void testApiDesc() {
		String description=BaseHandler.getDeclaredVariable("description");
		String expected="Testing Weather";
		assertTrue(description.contains(expected));
		
	}

}

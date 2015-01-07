package com.testmax.samples.cassandra;



import org.testng.annotations.*;
import org.testng.Assert;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;

import com.testmax.handler.BaseHandler;



public class CassandraTestNgTest  {
	

	public static void main(String args[]) {
		TestListenerAdapter tla = new TestListenerAdapter();
		TestNG testng = new TestNG();
		testng.setTestClasses(new Class[] { CassandraTestNgTest.class });
		testng.addListener(tla);
		testng.run();
	}
	 @BeforeMethod
	protected void setUp() {		
	}
	@Test
	public void testApiName() {
		String apiname=BaseHandler.getDeclaredVariable("apiname");
		String expected="mytest";
		Assert.assertTrue(apiname.contains(expected));
		
	}
	
	@Test
	public void testApiBasePath() {
		String basepath=BaseHandler.getDeclaredVariable("basepath");
		String expected="weather";
		Assert.assertTrue(basepath.contains(expected));
		
	}
	
	@Test
	public void testApiDesc() {
		String description=BaseHandler.getDeclaredVariable("description");
		String expected="Testing Weather";
		Assert.assertTrue(description.contains(expected));
		
	}
	
}
package com.testmax.samples.testng.money;



import org.testng.annotations.*;
import org.testng.Assert;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;

import com.testmax.handler.BaseHandler;



public class QueryTest  {
	

	public static void main(String args[]) {
		TestListenerAdapter tla = new TestListenerAdapter();
		TestNG testng = new TestNG();
		testng.setTestClasses(new Class[] { MoneyTest.class });
		testng.addListener(tla);
		testng.run();
	}
	 @BeforeMethod
	protected void setUp() {		
	}
	@Test
	public void GlobalMessage() {
		String gmsg=BaseHandler.getDeclaredVariable("msg");
		String expected="Global Message";
		Assert.assertEquals(expected,gmsg); 
		
	}
	@Test
	public void testLocalMessage() {
		String msg=BaseHandler.getDeclaredVariable("localmsg");
		String expected="Local Message";
		Assert.assertEquals(expected,msg); 
		
	}
	@Test
	public void testOrderNo() {
		String msg=BaseHandler.getDeclaredVariable("order_no");
		String expected="00000";
		Assert.assertEquals(expected,msg); 
		
	}
	
}
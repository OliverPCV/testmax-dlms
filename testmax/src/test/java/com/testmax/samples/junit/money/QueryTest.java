package com.testmax.samples.junit.money;



import org.junit.Test;

import com.testmax.handler.BaseHandler;


import junit.framework.TestCase;

public class QueryTest extends TestCase {
	

	public static void main(String args[]) {
		junit.textui.TestRunner.run(QueryTest.class);
	}
	@Override
	protected void setUp() {		
	}
	@Test
	public void GlobalMessage() {
		String gmsg=BaseHandler.getDeclaredVariable("msg");
		String expected="Global Message";
		assertEquals(expected,gmsg); 
		
	}
	
	public void testLocalMessage() {
		String msg=BaseHandler.getDeclaredVariable("localmsg");
		String expected="Local Message";
		assertEquals(expected,msg); 
		
	}
	
	public void testOrderNo() {
		String msg=BaseHandler.getDeclaredVariable("order_no");
		String expected="00000";
		assertEquals(expected,msg); 
		
	}
	
}
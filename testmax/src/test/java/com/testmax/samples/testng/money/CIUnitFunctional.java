package com.testmax.samples.testng.money;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.testmax.handler.BaseHandler;

public class CIUnitFunctional extends CIUnitTest {
	
	@BeforeMethod
	protected void setUp() {
		 super.setUp();
		 globalmsg=BaseHandler.getDeclaredVariable("msg");
		 localmsg=BaseHandler.getDeclaredVariable("localmsg");
		 orderno=BaseHandler.getDeclaredVariable("order_no");		
	}	
	

}

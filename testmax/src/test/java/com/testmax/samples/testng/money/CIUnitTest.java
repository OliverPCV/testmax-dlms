package com.testmax.samples.testng.money;



import org.testng.annotations.*;
import org.testng.Assert;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;



public class CIUnitTest  {
	
	//declare protected variables for test data
	protected String globalmsg;
	
	protected String localmsg;
	
	protected String orderno;
	
	// Bo or Ws client
	private CIWSClient client;
	
	public static void main(String args[]) {
		TestListenerAdapter tla = new TestListenerAdapter();
		TestNG testng = new TestNG();
		testng.setTestClasses(new Class[] { CIUnitTest.class });
		testng.addListener(tla);
		testng.run();
	}
	 @BeforeMethod
	protected void setUp() {		 
		 client= new CIWSClient();
		 globalmsg="Global Message";
		 localmsg="Local Message";
		 orderno="88678900999";
	}
	@Test
	public void GlobalMessage() {
		String gmsg=client.getGlobalMessage();		
		Assert.assertEquals(globalmsg,gmsg); 
		
	}
	@Test
	public void testLocalMessage() {
		String lmsg=client.getLocalMessage();		
		Assert.assertEquals(localmsg,lmsg); 
		
	}
	@Test
	public void testOrderNo() {
		String order_no=client.getOrderNo();	
		Assert.assertEquals(orderno,order_no); 
		
	}
	
}
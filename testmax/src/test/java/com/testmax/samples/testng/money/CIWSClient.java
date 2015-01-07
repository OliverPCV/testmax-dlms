package com.testmax.samples.testng.money;


public class CIWSClient {
	
	
	private String result;
	
	public String getGlobalMessage() {
		// The below value will be filled up by business object or webservice o/p		
		
		 result="Global Message";  //result=WSClient.retriveGlobalMessage()
		return(result);
		
	}
	
	public String getLocalMessage() {
		// The below value will be filled up by business object or webservice o/p
		 result="Local Message";  //result=WSClient.retriveLocalMessage()
		 return(result);
		
	}
	
	public String getOrderNo() {
		// The below value will be filled up by business object or webservice o/p
		 result="88678900999";  //result=WSClient.retriveOrderNo()
		 return(result);		
		
	}
	
}
/*
 * Copyright (C) 2014 Artitelly Inc,
 *
 * Licensed under the Common Public Attribution License Version 1.0 (CPAL-1.0) (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/CPAL-1.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.testmax.uri;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.NameValuePair;
import org.dom4j.Element;
import org.w3c.dom.Document;
import com.testmax.framework.PerformerBase;
import com.testmax.framework.URLConfig;
import com.testmax.framework.WmLog;
import com.testmax.util.DomUtil;


public class HttpSoapWebservice extends PerformerBase {

	
	public HttpSoapWebservice(){}
	
	
	public String handleHTTPPostUnit() throws MalformedURLException,
    IOException {
		DomUtil util= new DomUtil();
		String resp=null;  
    	
    	String xmlInput="";
        
        URLConfig urlConf=this.page.getURLConfig();
        
        Set <String> s=urlConf.getUrlParamset();
        for(String param:s){ 
        	xmlInput=urlConf.getUrlParamValue(param);
        	System.out.println(urlConf.getUrlParamValue(param));
        }
       
        //set the time for this HTTP resuest
        this.startRecording();
        long starttime=this.getCurrentTime();        
       
		//Code to make a webservice HTTP request
		String responseString = "";
		String outputString = "";
		
		URL url = new URL(this.url);
		URLConnection connection = url.openConnection();
		HttpURLConnection httpConn = (HttpURLConnection)connection;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();		
		
		byte[] buffer = new byte[xmlInput.length()];
		buffer = xmlInput.getBytes();
		bout.write(buffer);
		byte[] b = bout.toByteArray();
		String SOAPAction =this.url;
		// Set the appropriate HTTP parameters.
		httpConn.setRequestProperty("Content-Length",String.valueOf(b.length));
		httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		httpConn.setRequestProperty("SOAPAction", SOAPAction);
		httpConn.setRequestMethod("POST");
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		OutputStream out = httpConn.getOutputStream();
		
		//Write the content of the request to the outputstream of the HTTP Connection.
		out.write(b);
		out.close();
		//Ready with sending the request.
		int status=httpConn.getResponseCode();
		//Read the response.
		InputStreamReader isr =	new InputStreamReader(httpConn.getInputStream());
		BufferedReader in = new BufferedReader(isr);
		
		//Write the SOAP message response to a String.
		while ((responseString = in.readLine()) != null) {
			outputString = outputString + responseString;
		}		
		
		//Parse the String output to a org.w3c.dom.Document and be able to reach every node with the org.w3c.dom API.
		Document document = util.parseXmlString(outputString);		
		
		// resp = formatXML(outputString);
		 resp = outputString;
		 // Measure the time passed between response
        long elaspedTime= this.getElaspedTime(starttime);
        this.stopRecording(status,elaspedTime);
      
        System.out.println("ElaspedTime: "+elaspedTime);
        WmLog.getCoreLogger().info("Response XML: "+resp);
        WmLog.getCoreLogger().info("ElaspedTime: "+elaspedTime);
        this.printRecording();
        this.printLog();
        if(status==200 &&this.validateAssert(urlConf,document)){
        	this.addThreadActionMonitor(this.action, true, elaspedTime);
    	}else{
    		this.addThreadActionMonitor(this.action, false, elaspedTime);
    		WmLog.getCoreLogger().info("Input XML: "+xmlInput);
    		WmLog.getCoreLogger().info("Response XML: "+resp);
    	}
		return resp;
	}
	
	/*
	 protected boolean validateAssert(URLConfig urlConf,Document xmlDoc){
	    	boolean isPassed=true;
	    	DomUtil uti=new DomUtil();
	    	ItemUtility itu=new ItemUtility();
	    	for(Object key:urlConf.getAssertKeyset()){
	    		HashMap<String,String> soapElmMap=null;
	    		String msg="";
	    		Element vAssert=urlConf.getAssertKeyValue(key.toString());
	    		String name=vAssert.attributeValue("name");
	    		String[] nodepath=vAssert.attributeValue("nodepath").split(":");
	    		String[] indexpath=vAssert.attributeValue("indexpath").split(":");
	    		String[] elements=vAssert.attributeValue("elements").split(",");
	    		String[] datatype=vAssert.attributeValue("datatype").split(",");
	    		String[] values=vAssert.attributeValue("values").split(",");
	    		String operator=vAssert.attributeValue("operator");
	    		if(nodepath.length>0 && indexpath.length>0 &&elements.length>0
	    			&&datatype.length>0 && values.length>0 && operator!=""){
	    			
	    			if(elements.length==values.length && datatype.length==elements.length){
			    		if(nodepath.length>0 && nodepath.length==indexpath.length){
			    			soapElmMap=uti.getNodeElementMapByPath(xmlDoc, vAssert.attributeValue("nodepath"), vAssert.attributeValue("indexpath"));
			    			uti.printNodeMap(soapElmMap, vAssert.attributeValue("nodepath"));
			    			for(int i=0;i<elements.length;i++){			    			
			    				String elmentVal=soapElmMap.get(elements[i]);
			    				isPassed=itu.validate(elmentVal, values[i], operator, datatype[i]);
				    		 	if(isPassed){
				    		 		msg+="\n<br> Passed Assert>>"+name+">>Expected>>"+values[i]+">>operator="+ operator+">>Actual="+elmentVal+"<br>";
				    		 	}else{
				    		 		msg+="\n<br> Failed Assert>>"+name+">>Expected>>"+values[i]+">>operator="+ operator+">>Actual="+elmentVal+"<br>";
				    		 	}
			    			}
			    		}else{
			    			isPassed=false;
			    			msg="Assert>>"+name+">>NOT Matching  number of : seperated nodepath & indexpath in assert XML";
			    		}
	    			}else{
	    				isPassed=false;
	    				msg="Assert>>"+name+">>NOT Matching  number of comma seperated Values with Elements in assert XML";
	    			}
	    		
	    		}
	    		if(!isPassed){
	    			WmLog.getCoreLogger().info("FAILED: VALIDATE assert >> "+msg);
	    			System.out.println("FAILED: VALIDATE assert >> "+msg);
	    			if(this.page.isUnitTest()){
	    				this.addUnitTestActionMonitor(this.getActionName(), msg, false);
	    			}
	    			return false;
	    		}else{
	    			WmLog.getCoreLogger().info("PASSED: VALIDATE assert >> "+msg);
	    			System.out.println("PASSED: VALIDATE assert >> "+msg);
	    			if(this.page.isUnitTest()){
	    				this.addUnitTestActionMonitor(this.getActionName(), msg, true);
	    			}
	    		}
	    	}
	    	return(isPassed);
	    }
	    */
	    public String handleHTTPPostPerformance() throws Exception {
	    	String resp=null;
	    	int startcount=5;
	    	boolean isItemIdReplaced=false;    	
	    	String replacedParam="";
	    	// Create a method instance.
			PostMethod method = new PostMethod(this.url);
	        System.out.println(this.url);
	        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
	       
	        URLConfig urlConf=this.page.getPageURL().getUrlConfig(this.action);
	        List<Element> globalItemList=urlConf.getItemListElement();
	        if(globalItemList.size()>0){
	        	urlConf.setItemList();
	        	isItemIdReplaced=true;
	        }
	        Set <String> s=urlConf.getUrlParamset();
	     
	        for(String param:s){ 
	        	
	        	if(!isItemIdReplaced){
	        		        		
		        		method.addParameter(param,urlConf.getUrlParamValue(param));
		        
	        	}else{
	        		replacedParam=urlConf.getUrlReplacedItemIdParamValue(param);   
	        		method.addParameter(param,replacedParam);
	        	}
	        	
	        }
	        org.apache.commons.httpclient.HttpClient  client = new org.apache.commons.httpclient.HttpClient();
	      
	         this.startRecording();
		     long starttime=this.getCurrentTime();
		     int statusCode=0;
			try {
				 
				// Execute the method.
				 statusCode = client.executeMethod(method);

				if (statusCode != HttpStatus.SC_OK) {
					WmLog.getCoreLogger().info(
							"Auth Server response received, but there was a ParserConfigurationException: ");
				}

				// Read the response body.
				byte[] responseBody = method.getResponseBody();
				
				resp = new String(responseBody, "UTF-8");

				// log the response
				 WmLog.getCoreLogger().info( "MDE Request  : " + resp);

			} catch (HttpException e) {
				 WmLog.getCoreLogger().info(					
						"Unable to retrieve response from MDE webservice : "
								+ e.getMessage());
				return null;
			} catch (IOException e) {
				 WmLog.getCoreLogger().info(					
						"Unable to retrieve response from MDE webservice : "
								+ e.getMessage());
				return null;
			} finally {
				// Release the connection.
				method.releaseConnection();
			}
	        
	        // Measure the time passed between response
	        long elaspedTime= this.getElaspedTime(starttime);
	        this.stopMethodRecording(statusCode,elaspedTime,startcount);
	      
	        //System.out.println(resp);
	        System.out.println("ElaspedTime: "+elaspedTime);
	        WmLog.getCoreLogger().info("Response XML: "+resp);
	        WmLog.getCoreLogger().info("ElaspedTime: "+elaspedTime);
	        this.printMethodRecording(statusCode,this.url);
	        this.printMethodLog(statusCode,this.url);
	       if(statusCode==200 &&validateAssert(urlConf,resp)){
	        	this.addThreadActionMonitor(this.action, true, elaspedTime);
	    	}else{
	    		this.addThreadActionMonitor(this.action, false, elaspedTime);
	    		WmLog.getCoreLogger().info("Input XML: "+replacedParam);
	    		WmLog.getCoreLogger().info("Response XML: "+resp);
	    	}
	      
	      
	        return(resp);
	            
	    }


		@Override
		protected String handleHTTPGetUnit() throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected String handleHTTPGetPerformance() throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
		
		 
}



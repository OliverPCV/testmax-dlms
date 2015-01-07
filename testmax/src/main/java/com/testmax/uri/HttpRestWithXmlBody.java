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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.http.NameValuePair;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Element;
import com.testmax.framework.PerformerBase;
import com.testmax.framework.URLConfig;
import com.testmax.framework.WmLog;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.copy.HierarchicalStreamCopier;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;


public class HttpRestWithXmlBody extends PerformerBase {
	
	 public  HttpRestWithXmlBody(){}   
	 
    
  
 public String handleHTTPPostUnit(){
        
       
        String xmlData="";
        String resp="";
        // Get target URL
        String strURL = this.url;      
        URLConfig urlConf=this.page.getURLConfig();
        
        // Prepare HTTP post
        PostMethod post = new PostMethod(strURL);
        
        // Request content will be retrieved directly
        // from the input stream
        // Per default, the request content needs to be buffered
        // in order to determine its length.
        // Request body buffering can be avoided when
        // content length is explicitly specified
        
        
        // Specify content type and encoding
        // If content encoding is not explicitly specified
        // ISO-8859-1 is assumed
        
        Set <String> s=urlConf.getUrlParamset();
        for(String param:s){ 
        	xmlData=urlConf.getUrlParamValue(param);
        	if(param.equalsIgnoreCase("body")){
        		//xmlData=urlConf.getUrlParamValue(param);
        		byte buf[] = xmlData.getBytes(); 
                ByteArrayInputStream in = new ByteArrayInputStream(buf); 
                post.setRequestEntity(new InputStreamRequestEntity(
                        new BufferedInputStream(in), xmlData.length()));
        		
        	 }else{
        		 post.setRequestHeader( param, xmlData);        		
        	 }
        	WmLog.printMessage(">>> Setting URL Param "+param+ "="+xmlData);
        }
        // Get HTTP client
        org.apache.commons.httpclient.HttpClient httpsclient = new org.apache.commons.httpclient.HttpClient();
        
        // Execute request
        try {
            
            int response=-1;
            //set the time for this HTTP request
            this.startRecording();
            long starttime=this.getCurrentTime();
			try {
				response = httpsclient.executeMethod(post);
				resp=post.getResponseBodyAsString();
				Header[] heads=post.getResponseHeaders();
				this.cookies=httpsclient.getState().getCookies();
				
				String header="";
				for (Header head:heads){
					header+=head.getName()+":"+head.getValue();
				}
				System.out.println("Header="+header);
				 
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Measure the time passed between response
            long elaspedTime= this.getElaspedTime(starttime);
            this.stopRecording(response,elaspedTime);
          
            System.out.println(resp);
            System.out.println("ElaspedTime: "+elaspedTime);
            WmLog.printMessage("Response XML: "+resp);
            WmLog.printMessage("ElaspedTime: "+elaspedTime);
            this.printRecording();
            this.printLog();
            if(response==200 ||response==400){
            	this.addThreadActionMonitor(this.action, true, elaspedTime);
        	}else{
        		this.addThreadActionMonitor(this.action, false, elaspedTime);
        		WmLog.printMessage("Input XML: "+xmlData);
        		WmLog.printMessage("Response XML: "+resp);
        	}
            if(resp.contains("{") &&resp.contains("}") &&resp.contains(":")){
   	         try {
   	        	 resp="{  \"root\": "+resp+"}";
   	        	 HierarchicalStreamCopier copier = new HierarchicalStreamCopier();
   	     		 HierarchicalStreamDriver jsonXmlDriver = new JettisonMappedXmlDriver();
   	     		 StringWriter strWriter = new StringWriter();
   	     		 copier.copy(jsonXmlDriver.createReader(new StringReader(resp)), new PrettyPrintWriter(strWriter));
   	     		 resp = strWriter.toString();
   				  System.out.println(resp);
   			} catch (Exception e) {
   				// TODO Auto-generated catch block
   				e.printStackTrace();
   			}
           }
           
        } finally {
            // Release current connection to the connection pool 
            // once you are done
            post.releaseConnection();
        }
        return resp;
    }
   
 
 public String handleHTTPGetUnit(){
	
     String xmlData="";
     String resp="";
     // Get target URL
     String strURL = this.url;
    
     URLConfig urlConf=this.page.getURLConfig();
    
     // Get HTTP client
     org.apache.commons.httpclient.HttpClient httpsclient= new org.apache.commons.httpclient.HttpClient();
     
     // Prepare HTTP post
      GetMethod get = new GetMethod(strURL);
     
     // Request content will be retrieved directly
     // from the input stream
     // Per default, the request content needs to be buffered
     // in order to determine its length.
     // Request body buffering can be avoided when
     // content length is explicitly specified
     
     
     // Specify content type and encoding
     // If content encoding is not explicitly specified
     // ISO-8859-1 is assumed
     
     Set <String> s=urlConf.getUrlParamset();
     for(String param:s){ 
    	 xmlData=urlConf.getUrlParamValue(param);
     	if(param.equalsIgnoreCase("body")){
     		byte buf[] = xmlData.getBytes(); 
             ByteArrayInputStream in = new ByteArrayInputStream(buf); 
            // get.setRequestEntity(new InputStreamRequestEntity(
             //        new BufferedInputStream(in), xmlData.length()));
     		
     	 }else{
     		 get.setRequestHeader( param, xmlData);        		
     	 }
     	
     	WmLog.printMessage(">>> Setting URL Param "+param+ "="+xmlData);
     }
    
     
     // Execute request
     try {
         
         int response=-1;
         //set the time for this HTTP request
         this.startRecording();
         long starttime=this.getCurrentTime();
			try {
				response = httpsclient.executeMethod(get);
				resp=get.getResponseBodyAsString();
				this.cookies=httpsclient.getState().getCookies();
				Header[] heads=get.getResponseHeaders();
				String header="";
				for (Header head:heads){
					header+=head.getName()+":"+head.getValue();
				}
				System.out.println("Header="+header);
				 
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Measure the time passed between response
         long elaspedTime= this.getElaspedTime(starttime);
         this.stopRecording(response,elaspedTime);
       
         System.out.println(resp);
         System.out.println("ElaspedTime: "+elaspedTime);
         WmLog.printMessage("Response XML: "+resp);
         WmLog.printMessage("ElaspedTime: "+elaspedTime);
         this.printRecording();
         this.printLog();
         if(response==200 ||response==400){
         	this.addThreadActionMonitor(this.action, true, elaspedTime);
     	}else{
     		this.addThreadActionMonitor(this.action, false, elaspedTime);
     		WmLog.printMessage("Input XML: "+xmlData);
     		WmLog.printMessage("Response XML: "+resp);
     	}
       
        if(resp.contains("{") &&resp.contains("}") &&resp.contains(":")){
	         try {
	        	 resp="{  \"root\": "+resp+"}";
	        	 HierarchicalStreamCopier copier = new HierarchicalStreamCopier();
	     		 HierarchicalStreamDriver jsonXmlDriver = new JettisonMappedXmlDriver();
	     		 StringWriter strWriter = new StringWriter();
	     		 copier.copy(jsonXmlDriver.createReader(new StringReader(resp)), new PrettyPrintWriter(strWriter));
	     		 resp = strWriter.toString();
				  System.out.println(resp);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
          
        
     } finally {
         // Release current connection to the connection pool 
         // once you are done
         get.releaseConnection();
     }
     return resp;
 }

    
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
        		//nvps.add(new BasicNameValuePair(param,urlConf.getUrlParamValue(param)));        		
        		method.addParameter(param,urlConf.getUrlParamValue(param)); 
        		
        	}else{
        		replacedParam=urlConf.getUrlReplacedItemIdParamValue(param);   
        		method.addParameter(param,replacedParam);
        		//System.out.println(replacedParam);        		
        		//nvps.add(new BasicNameValuePair(param,replacedParam));
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
				WmLog.printMessage(
						"Auth Server response received, but there was a ParserConfigurationException: ");
			}

			// Read the response body.
			byte[] responseBody = method.getResponseBody();
			
			resp = new String(responseBody, "UTF-8");

			// log the response
			 WmLog.printMessage( "MDE Request  : " + resp);

		} catch (HttpException e) {
			 WmLog.printMessage(					
					"Unable to retrieve response from MDE webservice : "
							+ e.getMessage());
			return null;
		} catch (IOException e) {
			 WmLog.printMessage(					
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
        WmLog.printMessage("Response XML: "+resp);
        WmLog.printMessage("ElaspedTime: "+elaspedTime);
        this.printMethodRecording(statusCode,this.url);
        this.printMethodLog(statusCode,this.url);
       if(statusCode==200 ||statusCode==400){
        	this.addThreadActionMonitor(this.action, true, elaspedTime);
    	}else{
    		this.addThreadActionMonitor(this.action, false, elaspedTime);
    		WmLog.printMessage("Input XML: "+replacedParam);
    		WmLog.printMessage("Response XML: "+resp);
    	}
      
      
        return(resp);
            
    }
 
   
  
/*
	@Override
	protected String handleHTTPGetUnit() throws Exception {
		String resp=null;
        HttpGet httpget = new HttpGet(this.url);
        //set the time for this HTTP resuest
        this.startRecording();
        long starttime=this.getCurrentTime();
        HttpResponse response = this.httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        // Measure the time passed between response
        long elaspedTime= this.getElaspedTime(starttime);
        this.stopRecording(response,elaspedTime);
        resp=this.getResponseBodyAsString(entity);
        this.printRecording();
        this.printLog(); 
        //System.out.println(this.urlResponse);
        this.closeEntity(entity);
        return(resp);
	}

*/

	@Override
	protected String handleHTTPGetPerformance() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}   
    
}

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


import java.io.IOException;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Element;
import com.testmax.framework.PerformerBase;
import com.testmax.framework.URLConfig;
import com.testmax.framework.WmLog;


public class ExecuteHttpRequest extends PerformerBase {
	
	 public  ExecuteHttpRequest(){}   
	 
    
    
    
    
   
    public String handleHTTPPostUnit() throws Exception {
    	String resp=null;  
    	boolean isItemIdReplaced=false;    	
    	String replacedParam="";
    	System.out.println(this.url);
        HttpPost httpost = new HttpPost(this.url);
        //System.out.println(this.url);
        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        URLConfig urlConf=this.page.getURLConfig();
        
        Set <String> s=urlConf.getUrlParamset();
        for(String param:s){ 
        	replacedParam=urlConf.getUrlParamValue(param);
        	System.out.println(urlConf.getUrlParamValue(param));        	
        	nvps.add(new BasicNameValuePair(param,urlConf.getUrlParamValue(param)));
        }
        
        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        //set the time for this HTTP resuest
        this.startRecording();
        long starttime=this.getCurrentTime();
        HttpResponse response = this.httpsclient.execute(httpost);
        HttpEntity entity = response.getEntity();
        // Measure the time passed between response
        long elaspedTime= this.getElaspedTime(starttime);
        this.stopRecording(response,elaspedTime);
        resp=this.getResponseBodyAsString(entity); 
        System.out.println(resp);
        System.out.println("ElaspedTime: "+elaspedTime);
        WmLog.getCoreLogger().info("Response XML: "+resp);
        WmLog.getCoreLogger().info("ElaspedTime: "+elaspedTime);
        this.printRecording();
        this.printLog();
        if(this.getResponseStatus()==200 &&validateAssert(urlConf,resp)){
        	this.addThreadActionMonitor(this.action, true, elaspedTime);
        }else{
    		this.addThreadActionMonitor(this.action, false, elaspedTime);
    		WmLog.getCoreLogger().info("Input XML: "+replacedParam);
    		WmLog.getCoreLogger().info("Response XML: "+resp);
    	}
        this.closeEntity(entity);
        return(resp);
            
    }
    
    public String handleHTTPPostPerformance() throws Exception {
    	String resp=null;  
    	boolean isItemIdReplaced=false;    	
    	String replacedParam="";
    	// Create a method instance.	
        
        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
       
        URLConfig urlConf=this.page.getPageURL().getUrlConfig(this.action);
        List<Element> globalItemList=urlConf.getItemListElement();
        if(globalItemList.size()>0){
        	urlConf.setItemList();
        	isItemIdReplaced=true;
        }
        String newurl=urlConf.replaceGlobalItemIdForUrl(this.url);
        PostMethod method = new PostMethod(newurl);
        System.out.println(newurl);
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
         int startcount=0;
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
        this.printMethodRecording(statusCode,newurl);
        this.printMethodLog(statusCode,newurl);
        
       if(statusCode==200  &&validateAssert(urlConf,resp)){
        	this.addThreadActionMonitor(this.action, true, elaspedTime);
      
    	}else{
    		this.addThreadActionMonitor(this.action, false, elaspedTime);
    		WmLog.getCoreLogger().info("Input XML: "+replacedParam);
    		WmLog.getCoreLogger().info("Response XML: "+resp);
    	}
      
      
        return(resp);
            
    }
    
  
    public String handleHTTPPostWithClientManager() throws Exception {
    	String resp=null;  
    	boolean isItemIdReplaced=false;    	
    	String replacedParam="";
        HttpPost httpost = new HttpPost(this.url);
        //System.out.println(this.url);
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
        		nvps.add(new BasicNameValuePair(param,urlConf.getUrlParamValue(param)));
        	}else{
        		replacedParam=urlConf.getUrlReplacedItemIdParamValue(param);        		
        		System.out.println(replacedParam);        		
        		nvps.add(new BasicNameValuePair(param,replacedParam));
        	}
        	
        }
        
        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        //set the time for this HTTP resuest
        this.startRecording();
        long starttime=this.getCurrentTime();
        HttpResponse response = this.httpsclient.execute(httpost);
        HttpEntity entity = response.getEntity();
        // Measure the time passed between response
        long elaspedTime= this.getElaspedTime(starttime);
        this.stopRecording(response,elaspedTime);
        resp=this.getResponseBodyAsString(entity); 
        System.out.println(resp);
        System.out.println("ElaspedTime: "+elaspedTime);
        WmLog.getCoreLogger().info("Response XML: "+resp);
        WmLog.getCoreLogger().info("ElaspedTime: "+elaspedTime);
        this.printRecording();
        this.printLog();
        if(this.getResponseStatus()==200){
        	validateAssert(urlConf,resp);
        }else{
        	WmLog.getCoreLogger().info("Input XML: "+replacedParam);
    		WmLog.getCoreLogger().info("Response XML: "+resp);
        }
       
        this.closeEntity(entity);
        return(resp);
            
    }
    

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


	@Override
	protected String handleHTTPGetPerformance() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}   
    
}

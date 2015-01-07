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

import java.util.List;
import java.util.Set;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.dom4j.Element;
import com.testmax.framework.BasePage;
import com.testmax.framework.PerformerBase;
import com.testmax.framework.URLConfig;
import com.testmax.framework.WmLog;
import com.testmax.util.JavaScriptUtl;


public class ExecuteJavaScriptRequest extends PerformerBase {
	
	
	
	 private boolean isUrlLoaded;
	 //private Object state = new Object();

	 
	 public  ExecuteJavaScriptRequest(){
		
	    this.isUrlLoaded=false;
	  
	 }  
    
    
	 public void setup(BasePage page, String action){
		
			 super.setup(page, action);
			 this.utl= new JavaScriptUtl();
			 this.webdriver=utl.initWebDriverDriver(this.browser);
		 
    }
   
    public String handleHTTPPostUnit() throws Exception {
    	
    	return(executeJavaScriptUnit());
            
    }
    
    public String handleHTTPPostPerformance() throws Exception {
    	  URLConfig urlConf=this.page.getPageURL().getUrlConfig(this.action);
         
    	if(!utl.isEmptyValue( urlConf.getUrlParamValue("loginurl"))) {
      		return(executeWithSeperateLoginURL());
    	}else if(!utl.isEmptyValue( urlConf.getUrlParamValue("trackerid")) &&utl.isEmptyValue( urlConf.getUrlParamValue("body"))){
    		return (this.executeURLWithTracker());
    	}
    	
    	return(executeJavaScript());
    
    }
    
    /*
     * Configuration Parameters
     * Mandatory parameters:  pageloadeach (yes/no) and body
 	  Optional parameter :  waittorender (yes/no)
 	 If you need to open the URL each time before you execute the java script 
 	 then pass <param name="pageloadeach">yes</param>
 	 Use the body as parameters name for javascript which will be executed
 	if you pass  param other than body and pageloadeach these parameters will be replaced within the body text javascript
 	
     */
     private String executeJavaScriptUnit() throws Exception {
     	
     	
     	String resp=null;
     	int startcount=0;
     	boolean isItemIdReplaced=false;    	
     	String replacedParam="";
     	 URLConfig urlConf=this.page.getURLConfig();
         //URLConfig urlConf=this.page.getPageURL().getUrlConfig(this.action);
         String newurl=urlConf.replaceGlobalItemIdForUrl(this.url);
         String trakerid=urlConf.getUrlParamValue("trackerid");
         String trakerval=urlConf.getUrlParamValue("trackerval");
         String pageloadeach=urlConf.getUrlParamValue("pageloadeach");
         String body=urlConf.getUrlParamValue("body");
         String waittorender=urlConf.getUrlParamValue("waittorender");
         
         List<Element> globalItemList=urlConf.getItemListElement();
         if(globalItemList.size()>0){
         	urlConf.setItemList();
         	isItemIdReplaced=true;
         }
         
         //unless pageloadeach=yes only once the page load will happen and click side chace will be invoked
         if(!this.isUrlLoaded){
         	utl.loadUrl(newurl);
         	isUrlLoaded=true;
         	
         }
        
         System.out.println(newurl);
         Set <String> s=urlConf.getUrlParamset();
         utl.addAttributeToElement();  
         
         this.startRecording();
 	    long starttime=this.getCurrentTime();
 	    int statusCode=0;
 	    
 	    
         if(body!=null &&!body.isEmpty()){
        	//replace all parameters in the java script
             for(String param:s){ 
             	if(!param.equalsIgnoreCase("pageloadeach") &&!param.equalsIgnoreCase("body")){
     	        	if(!isItemIdReplaced){
     	        		body=body.replaceAll("@"+param, urlConf.getUrlParamValue(param));
     	        		   		
     	        	}else{
     	        		replacedParam=urlConf.getUrlReplacedItemIdParamValue(param); 
     	        		body=body.replaceAll("@"+param, urlConf.getUrlParamValue(replacedParam));
     	        		
     	        	}
             	
             	}
             	
             }
         	resp=utl.executeScript(body,trakerid,trakerval) ; 
         }
         
         if(waittorender!=null &&waittorender.equalsIgnoreCase("yes")){
        	utl.loadUrl(newurl);
         	utl.waitToPageLoad();
         	Thread.sleep(750);
         }
         if(resp!=null && !resp.contains("ERROR")){
         	statusCode=200;
         }
         // Measure the time passed between response
         long elaspedTime= this.getElaspedTime(starttime)-utl.pagewaittime;
         this.stopMethodRecording(statusCode,elaspedTime,startcount);
       
         System.out.println(resp);
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
     
    
   /*
    * Configuration Parameters
    * Mandatory parameters:  pageloadeach (yes/no) and body
    * Mandatory Parameter : trackerval. Ex: DONE!, COMPLETE! etc.
	  Optional parameter :  waittorender (yes/no)
	  Optional parameter : trackerId (If you print a message after page load complete within your java script then you may not use tracker Id)
	  
	 If you need to open the URL each time before you execute the java script 
	 then pass <param name="pageloadeach">yes</param>
	
	 Use the body as parameters name for javascript which will be executed
	if you pass  param other than body and pageloadeach these parameters will be replaced within the body text javascript
	
    */
    private String executeJavaScript() throws Exception {
    	
    	
    	String resp=null;
    	
    	boolean isItemIdReplaced=false;    	
    	String replacedParam="";
        URLConfig urlConf=this.page.getPageURL().getUrlConfig(this.action);
        String newurl=urlConf.replaceGlobalItemIdForUrl(this.url);
        String trakerid=urlConf.getUrlParamValue("trackerid");
        String trakerval=urlConf.getUrlParamValue("trackerval");
        String pageloadeach=urlConf.getUrlParamValue("pageloadeach");
        String body=urlConf.getUrlParamValue("body");
        String waittorender=urlConf.getUrlParamValue("waittorender");
        
        List<Element> globalItemList=urlConf.getItemListElement();
        if(globalItemList.size()>0){
        	urlConf.setItemList();
        	isItemIdReplaced=true;
        }
        
        //unless pageloadeach=yes only once the page load will happen and click side chace will be invoked
        if(!this.isUrlLoaded){
        	utl.loadUrl(newurl);
        	isUrlLoaded=true;
        	
        }
        // This is for server side cache where in every call page load will happen
        if(pageloadeach!=null && pageloadeach.equalsIgnoreCase("yes")){
        	utl.loadUrl(newurl);
        }
       
        System.out.println(newurl);
        Set <String> s=urlConf.getUrlParamset();
        if(utl.isEmptyValue(trakerid)){
        	utl.addAttributeToElement(); 
        }
        
        this.startRecording();
        int startcount=5;
	    long starttime=this.getCurrentTime();
	    int statusCode=0;
	    
	    
        if(body!=null &&!body.isEmpty()){
        	//replace all parameters in the java script
            for(String param:s){ 
            	if(!param.equalsIgnoreCase("pageloadeach") &&!param.equalsIgnoreCase("body")){
    	        	if(!isItemIdReplaced){
    	        		body=body.replaceAll("@"+param, urlConf.getUrlParamValue(param));
    	        		   		
    	        	}else{
    	        		replacedParam=urlConf.getUrlReplacedItemIdParamValue(param); 
    	        		body=body.replaceAll("@"+param, urlConf.getUrlParamValue(replacedParam));
    	        		
    	        	}
            	
            	}
            	
            }
        	resp=utl.executeScript(body,trakerid,trakerval) ; 
        }
        
        if(waittorender!=null &&waittorender.equalsIgnoreCase("yes")&&utl.isEmptyValue(trakerid)){
        	utl.waitToPageLoad();
        	Thread.sleep(1000);
        }
        if(resp!=null && !resp.contains("ERROR")){
        	statusCode=200;
        }
        // Measure the time passed between response
        long elaspedTime= this.getElaspedTime(starttime)-utl.pagewaittime;
        this.stopMethodRecording(statusCode,elaspedTime,startcount);
      
        System.out.println(resp);
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
    
    /*
     * Configuration Parameters
     * Mandatory parameters:  pageloadeach (yes/no) and body
     * Mandatory Parameter : trackerval. Ex: DONE!, COMPLETE! etc.
 	  Optional parameter :  waittorender (yes/no)
 	  Optional parameter : trackerId (If you print a message after page load complete within your java script then you may not use tracker Id)
 	  
 	 If you need to open the URL each time before you execute the java script 
 	 then pass <param name="pageloadeach">yes</param>
 	
 	 Use the body as parameters name for javascript which will be executed
 	if you pass  param other than body and pageloadeach these parameters will be replaced within the body text javascript
 	
     */
     private String executeWithSeperateLoginURL() throws Exception {
     	
     
     	String resp=null;  
     	boolean isItemIdReplaced=false;    	
     	String replacedParam="";
         URLConfig urlConf=this.page.getPageURL().getUrlConfig(this.action);
         String newurl=urlConf.replaceGlobalItemIdForUrl(this.url);
         String trakerid=urlConf.getUrlParamValue("trackerid");
         String trakerval=urlConf.getUrlParamValue("trackerval");
         String loginurl=urlConf.getUrlParamValue("loginurl");
         String pageloadeach=urlConf.getUrlParamValue("pageloadeach");
         String body=urlConf.getUrlParamValue("body");
         String waittorender=urlConf.getUrlParamValue("waittorender");
         
         List<Element> globalItemList=urlConf.getItemListElement();
         if(globalItemList.size()>0){
         	urlConf.setItemList();
         	isItemIdReplaced=true;
         }
         
         //unless pageloadeach=yes only once the page load will happen and click side chace will be invoked
         if(!this.isUrlLoaded){
         	utl.loadUrl(loginurl);
         	utl.waitToPageLoad();
         	if(!utl.isEmptyValue(this.browser) &&this.browser.equalsIgnoreCase("htmlunit")){
         		Thread.sleep(2500);
         	}
         	utl.pagewaittime=0;
         	isUrlLoaded=true;
         	if(pageloadeach!=null && pageloadeach.equalsIgnoreCase("no")){
            	 utl.loadUrlWithAlert(newurl, trakerid, trakerval);
            }
         	
         }
         
         this.startRecording();
        int startcount=5;
  	    long starttime=this.getCurrentTime();
  	    int statusCode=0;
         // This is for server side cache where in every call page load will happen
         if(pageloadeach!=null && pageloadeach.equalsIgnoreCase("yes")){
        	 resp=utl.loadUrlWithAlert(newurl, trakerid, trakerval);
         }
        
         System.out.println(newurl);
         Set <String> s=urlConf.getUrlParamset();
         if(utl.isEmptyValue(trakerid)){
         	utl.addAttributeToElement(); 
         }
         
 	    
         if(body!=null &&!body.isEmpty()){
        	//replace all parameters in the java script
             for(String param:s){ 
             	if(!param.equalsIgnoreCase("pageloadeach") &&!param.equalsIgnoreCase("body")){
     	        	if(!isItemIdReplaced){
     	        		body=body.replaceAll("@"+param, urlConf.getUrlParamValue(param));
     	        		   		
     	        	}else{
     	        		replacedParam=urlConf.getUrlReplacedItemIdParamValue(param); 
     	        		body=body.replaceAll("@"+param, urlConf.getUrlParamValue(replacedParam));
     	        		
     	        	}
             	
             	}
             	
             }
         	resp=utl.executeScript(body,trakerid,trakerval) ; 
         }
         
         if(waittorender!=null &&waittorender.equalsIgnoreCase("yes") &&utl.isEmptyValue(trakerid)){
         	utl.waitToPageLoad();
         	Thread.sleep(1000);
         }
         if(resp!=null && !resp.contains("ERROR")){
         	statusCode=200;
         }
         // Measure the time passed between response
         long elaspedTime= this.getElaspedTime(starttime)-utl.pagewaittime;
         this.stopMethodRecording(statusCode,elaspedTime,startcount);
       
         System.out.println(resp);
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
    /*
     * Configuration Parameters
     * Mandatory parameters:  pageloadeach (yes/no) and body
 	  Optional parameter :  waittorender (yes/no)
 	 If you need to open the URL each time before you execute the java script 
 	 then pass <param name="pageloadeach">yes</param>
 	 Use the body as parameters name for javascript which will be executed
 	if you pass  param other than body and pageloadeach these parameters will be replaced within the body text javascript
 	
     */
     private String executeURLWithTracker() throws Exception {
     	
     	
     	String resp=null;  
       	
     	String replacedParam="";
     	
         URLConfig urlConf=this.page.getPageURL().getUrlConfig(this.action);
         String newurl=urlConf.replaceGlobalItemIdForUrl(this.url);
         String trakerid=urlConf.getUrlParamValue("trackerid");
         String trakerval=urlConf.getUrlParamValue("trackerval");
         
         this.startRecording();
         int startcount=5;
 	     long starttime=this.getCurrentTime();
 	     int statusCode=0;
         resp=utl.loadUrlWithTracker(newurl, trakerid, trakerval);
         
         if(resp!=null && resp.contains(trakerval)){
         	statusCode=200;
         }
         // Measure the time passed between response
         long elaspedTime= this.getElaspedTime(starttime)-utl.pagewaittime;
         this.stopMethodRecording(statusCode,elaspedTime,startcount);
       
         System.out.println(resp);
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

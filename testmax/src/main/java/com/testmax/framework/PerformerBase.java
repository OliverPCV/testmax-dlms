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

package com.testmax.framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.httpclient.Cookie;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.dom4j.Element;
import org.openqa.selenium.WebDriver;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import com.testmax.util.DomUtil;
import com.testmax.util.ItemUtility;
import com.testmax.util.JavaScriptUtl;


public abstract class PerformerBase {
    

   
    
    /*
     * store the current system time in this timer
     */
       
    protected BasePage page=null;
    
    protected  HttpClient httpclient=null;
    
    protected  HttpClient httpsclient=null;
    
    protected String browser=null;
    
    protected Cookie[] cookies=null;
        
    protected volatile WebDriver webdriver = null;
    /*
     * reponse counter, total number of execution during entire timeout period
     */
    
    protected static volatile int executionCount=0;
    
    
    /*
     * total execution time based on total number of response
     */
    
    protected static volatile long totalExecutionTime=0;    
    
    
    /*
     * total reponse OK time, total number of response status OK time
     */
    
    protected static volatile long totalOKTime=0;
    
    /*
     * reponse OK counter, total number of response status OK count
     */
    
    protected static volatile int responseOKCount=0;
    
    /*
     * reponse ERROR counter, total number of response status ERROR count
     */
    
    protected static volatile int responseERCount=0;
    
    /*
     * reponse counter per min, total number of execution during every min
     */
    
    protected static volatile int perminResp=0;
    
    /*
     * elasped time for individual call
     */
    
    protected static volatile double activeThread=0;
    
    /*
     * elasped time for individual call
     */
    
    protected static volatile double elaspedTime=0;
    
    /*
     * avarage time of execution per request for this PageURL, default=0 mili sec
     */
    
    protected static volatile double avgTime=0;
    
    
    /*
     * avarage OK response time of execution per request in mili sec
     */
    
    protected static volatile double avgOKTime=0;
    
  
    
    /*
     * stores action execution count for all performing threads
     */
    protected static volatile HashMap<String, Integer> actionExecutionCount=new HashMap <String, Integer>();    
    
    /*
     * stores action OK execution count for all performing threads
     */
    protected static volatile HashMap<String, Integer> actionOKCount=new HashMap<String, Integer>();    
    
    /*
     * stores action ERROR execution count for all performing threads
     */
    protected static volatile HashMap<String, Integer> actionErrorCount=new HashMap<String, Integer>();
    
    /*
     * stores action OK execution count for all performing threads
     */
    protected static volatile HashMap<String, Integer> validatorOKCount=new HashMap<String, Integer>();    
    
    /*
     * stores action ERROR execution count for all performing threads
     */
    protected static volatile HashMap<String, Integer> validatorErrorCount=new HashMap<String, Integer>();
    
    /*
     * stores action validator message for failed test case
     */
    protected static volatile HashMap<String, HashMap<String,String>> validatorMessage=new HashMap<String, HashMap<String,String>>();
    
    /*
     * stores action ERROR message for failed test case
     */
    protected static volatile HashMap<String, String> actionErrorMessage=new HashMap<String, String>();
    
    
    /*
     * stores action OK execution count for all performing threads
     */
    protected static volatile HashMap<String, Long> actionReposneTime=new HashMap<String, Long>();
    
    
    /*
     * stores average response trend every min for 1-60 for hr
     */
    private HashMap<Integer, Double> respTrend=null;
    
    /*
     * stores average request trend every min for 1-60 for hr
     */
    private HashMap<Integer, Double> reqTrend=null;    
    
    /*
     * stores average response trend in interval configured by User
     */
    private HashMap<Integer, Double> respTrendInterval=null;
    
    /*
     * stores average request trend in interval configured by User
     */
    private HashMap<Integer, Double> reqTrendInterval=null;  
    
    /*
     * HTTP Respose
     */    
    
    protected HashMap<String, String> nodeElmValueList=null ;
    
    protected  JavaScriptUtl utl=null;
    
    private HttpResponse response=null;
    
    private String dbResponse=null;

    private String statusCode=null;
    
    protected String action=null;
	
	protected String url=null;
	
	
	
	
	public PerformerBase(){
		
    }
    public void setup(BasePage page, String action){
        this.page=page;
        this.action=action;
        if(this.page.isUnitTest() &&page.getURLConfig()!=null &&page.getURLConfig().getUrl()!=null){        	
        	this.url=page.getURLConfig().getUrl();         	
        }else if(page.getPageURL().getUrlConfig(action)!=null &&page.getPageURL().getUrlConfig(action).getUrl()!=null){
        	this.url=page.getPageURL().getUrlConfig(action).getUrl();
        }
       
        this.browser=page.getBrowsers()[0];
       
       
        //if(this.htmlunitdriver==null){
        	//this.htmlunitdriver=new HtmlUnitDriver(true);
        //}
       
    }
    
    protected void addThreadActionMonitor(String action, boolean response, long elaspedtime){
    	int totalInvoked=(actionExecutionCount.get(action)==null?0:actionExecutionCount.get(action))+1;    	
    	actionExecutionCount.put(action, totalInvoked);
    	long totalTime=(actionReposneTime.get(action)==null?0:actionReposneTime.get(action))+elaspedtime;
    	actionReposneTime.put(action, totalTime);
    	if(response){
    		int totalOk=(actionOKCount.get(action)==null?0:actionOKCount.get(action))+1;    	
    		actionOKCount.put(action, totalOk);
    		String msg=(actionErrorMessage.get(action)==null?"PASSED":actionErrorMessage.get(action));
    		actionErrorMessage.put(action, msg);
    	}else{
    		int totalError=(actionErrorCount.get(action)==null?0:actionErrorCount.get(action))+1;    	
    		actionErrorCount.put(action, totalError);
    		
    	}
    }
    protected void addCursorOpenTime(String action, long elaspedtime){
    	long totalTime=(actionReposneTime.get(action)==null?0:actionReposneTime.get(action))+elaspedtime;
    	actionReposneTime.put(action, totalTime);
    }
    
   
   
    protected void addUnitTestActionMonitor(String action,String assertname,String msg, boolean response){
    	String amsg="";
    	String assertresult="";
    	int index=1;
    	HashMap<String,String> testresult=new HashMap<String,String>() ;
    	if(validatorMessage.get(action)!=null){
    		testresult=validatorMessage.get(action);
    		index=testresult.size()+1;
    	}
    	if(response){
    		int totalPassed=(validatorOKCount.get(action)==null?0:validatorOKCount.get(action))+1; 
    		validatorOKCount.put(action, totalPassed);    		
    		amsg=(actionErrorMessage.get(action)==null?"":actionErrorMessage.get(action));    		
    		actionErrorMessage.put(action, (amsg.equalsIgnoreCase("PASSED")?"":amsg)+"<br><font color=\"blue\" >PASSED </font> >>"+msg+"<br>");
    		testresult.put("TEST-"+index+": "+assertname,"PASSED -"+msg);
    	}else{
    		
    		amsg=(actionErrorMessage.get(action)==null?"":actionErrorMessage.get(action));
    		actionErrorMessage.put(action, (amsg.equalsIgnoreCase("PASSED")?"":amsg)+"<br><font color=\"red\" >FAILED </font> >>"+msg+"<br>");
    		int totalError=(validatorErrorCount.get(action)==null?0:validatorErrorCount.get(action))+1; 
    		validatorErrorCount.put(action, totalError);
    		testresult.put("TEST-"+index+": "+assertname,"FAILED -"+msg);
    	}
    	
    	validatorMessage.put(action,testresult);
		
    }
   
  //synchronized
    protected void stopDbRecording(boolean response, long elaspedtime){
    	this.dbResponse="Success!";
    	this.elaspedTime=Double.parseDouble(String.valueOf(elaspedtime));
        if(response){
            totalOKTime=totalOKTime+elaspedtime;
            responseOKCount=responseOKCount+1;
            avgOKTime=totalOKTime/responseOKCount;
            dbResponse="Success!";
        }else{
            responseERCount=responseERCount+1;
            dbResponse="Failed!";
        }
          
        executionCount=executionCount+1;
        
        totalExecutionTime=totalExecutionTime+ elaspedtime;
      
        avgTime=totalExecutionTime/executionCount;
        
    }
    
    
    protected boolean validateAssertByDom(Element vAssert, String resp, String validator,String wsname){
    	DomUtil uti=new DomUtil();
    	Document xmlDoc=uti.parseXmlString(resp);    	
    	boolean isPassed=true;
    	boolean isAllPassed=true;
    	ItemUtility itu=new ItemUtility();
    	nodeElmValueList= new HashMap<String,String>();
		HashMap<String,String> soapElmMap=null;
		String name=vAssert.attributeValue("name");
		String msg="<br>"+this.getActionName()+">>"+validator+">>"+wsname+">>"+name+">>";
		String[] attrs= new String[0];
		
		String[] nodepath=vAssert.attributeValue("nodepath").split(":");
		String[] indexpath=vAssert.attributeValue("indexpath").split(":");
		String[] elements=vAssert.attributeValue("elements").split(",");
	
		String[] datatype=vAssert.attributeValue("datatype").split(",");
		String[] values=vAssert.attributeValue("values").split(",");
		String operator=vAssert.attributeValue("operator");
		if(vAssert.attributeValue("attributes")!=null &&!vAssert.attributeValue("attributes").equals("")){
			attrs=vAssert.attributeValue("attributes").split(",");
		}
		if(nodepath.length>0 && indexpath.length>0 &&elements.length>0
			&&datatype.length>0 && values.length>0 && operator!=""){
			
			if(elements.length==values.length && datatype.length==elements.length){
	    		if(nodepath.length>0 && nodepath.length==indexpath.length){
	    			soapElmMap=uti.getNodeElementMapByPath(xmlDoc, vAssert.attributeValue("nodepath"), vAssert.attributeValue("indexpath"));
	    			if(soapElmMap!=null){
		    			uti.printNodeMap(soapElmMap, vAssert.attributeValue("nodepath"));
		    			for(int i=0;i<elements.length;i++){			    			
		    				String elmentVal=soapElmMap.get(elements[i]);
		    				nodeElmValueList.put(elements[i], elmentVal);
		    				isPassed=itu.validate(elmentVal, values[i], operator, datatype[i]);
			    		 	if(isPassed){
			    		 		msg+="<br>\nExpected>"+values[i]+">operator="+ operator+">Actual="+elmentVal;
			    		 	}else{
			    		 		isAllPassed=false;
			    		 		msg+="<br>\nExpected>"+values[i]+">operator="+ operator+">Actual="+elmentVal;
			    		 	}
		    			}
	    			}else{
	    				isAllPassed=false;
	    				isPassed=false;
		    			msg+="<br>could not find element or node : seperated nodepath &amp; indexpath in assert XML";
	    			}
	    		}else{
	    			isAllPassed=false;
	    			isPassed=false;
	    			msg+="<br>NOT Matching  number of : seperated nodepath &amp; indexpath in assert XML";
	    		}	    	
			}else if(attrs.length==values.length && datatype.length==attrs.length){
				
				for(int i=0;i<elements.length;i++){
    				NodeList nls = xmlDoc.getElementsByTagName(elements[i]);
    				for( int j = 0; j < nls.getLength(); j++ ) {
    				      // for every link tag
    					org.w3c.dom.Element elm = (org.w3c.dom.Element) nls.item(j );
    					  int idx=0;
    				      for(String attrb: attrs){
    				    	  String elmentVal=elm.getAttribute(attrb);
    				    	  if(elmentVal!=null){
    				    		  nodeElmValueList.put(attrb, elmentVal);
    			    				isPassed=itu.validate(elmentVal, values[idx], operator, datatype[idx]);
    			    				if(isPassed){
    				    		 		msg+="<br>\nExpected>"+values[idx]+">operator="+ operator+">Actual="+elmentVal;
    				    		 	}else{
    				    		 		isAllPassed=false;
    				    		 		msg+="<br>\nExpected>"+values[idx]+">operator="+ operator+">Actual="+elmentVal;
    				    		 	}
    				    	  }
    				    	  idx++;
    				      }
    				      if(isPassed) break;
    				       
    				   }
    				
    			}
				
			}else{
				isPassed=false;
				isAllPassed=false;
				msg+="<br>NOT Matching  number of : seperated nodepath &amp; indexpath in assert XML";
			}
		
		}
		if(!isAllPassed){
			WmLog.getCoreLogger().info("FAILED: VALIDATE assert >> "+msg);
			System.out.println("FAILED: VALIDATE assert >> "+msg);
			if(this.page.isUnitTest()){
				
				//this.addUnitTestActionMonitor(this.getActionName(), msg, false);
				this.addUnitTestActionMonitor(this.getActionName(),name, msg, false);
			}
			return false;
		}else{
			WmLog.getCoreLogger().info("PASSED: VALIDATE assert >> "+msg);
			System.out.println("PASSED: VALIDATE assert >> "+msg);
			if(this.page.isUnitTest()){
				//this.addUnitTestActionMonitor(this.getActionName(), msg, true);
				this.addUnitTestActionMonitor(this.getActionName(),name, msg, true);
			}
		}
    	
    	return(isPassed);
    }
	
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
	    				//this.addUnitTestActionMonitor(this.getActionName(), msg, false);
	    				this.addUnitTestActionMonitor(this.getActionName(), name,msg, false);
	    			}
	    			return false;
	    		}else{
	    			WmLog.getCoreLogger().info("PASSED: VALIDATE assert >> "+msg);
	    			System.out.println("PASSED: VALIDATE assert >> "+msg);
	    			if(this.page.isUnitTest()){
	    				//this.addUnitTestActionMonitor(this.getActionName(), msg, true);
	    				this.addUnitTestActionMonitor(this.getActionName(), name,msg, true);
	    			}
	    		}
	    	}
	    	return(isPassed);
	    }
	protected boolean checkXpathAttribute(URLConfig urlConf){
		
		for(Object key:urlConf.getAssertKeyset()){
    	
    		Element vAssert=urlConf.getAssertKeyValue(key.toString());
    		
    		if(vAssert.attributeValue("xpath")!=null &&
    				!vAssert.attributeValue("xpath").isEmpty()){
    			return(true);
    		}
		}
		
		return(false);
	}
    protected boolean validateAssert(URLConfig urlConf,String responseXml){
    	boolean isPassed=true;
    	int msg_length=Integer.parseInt(ConfigLoader.getConfig("DEFAULT_RESP_MSG_LENGTH"));    
    	String full_msg=ConfigLoader.getConfig("COMPLETE_RESP_MSG");
    	ItemUtility uti=new ItemUtility();
    	for(Object key:urlConf.getAssertKeyset()){
    		String elmentVal="";
    		String msg="";
    		Element vAssert=urlConf.getAssertKeyValue(key.toString());
    		String name=vAssert.attributeValue("name");
    		String xpath=vAssert.attributeValue("xpath");
    		String datatype=vAssert.attributeValue("datatype");
    		String value=vAssert.attributeValue("value");
    		String operator=vAssert.attributeValue("operator");
    		String index=(vAssert.attributeValue("index")==null||vAssert.attributeValue("index")==""?"0":vAssert.attributeValue("index"));
    		if(xpath!=null && xpath!=""){
    			elmentVal=uti.getElementValueByXpath(responseXml, xpath,index);
    		 	isPassed=uti.validate(elmentVal, value, operator, datatype);
    		 	msg="Assert>>"+name+">>Expected>>"+value+">>operator="+ operator+">>Actual="+ 
    		 		(full_msg.equalsIgnoreCase("yes")? elmentVal:elmentVal.substring(0, 
    		 				(elmentVal.length()> msg_length?msg_length:elmentVal.length())));
    		}else{
    			isPassed=uti.validate(responseXml, value, "Has", datatype);
    			msg="Assert>>"+name+">>Expected>>"+value+">>operator=Has>>Actual="+
    			(full_msg.equalsIgnoreCase("yes")? responseXml:responseXml.substring(0, 
    					(responseXml.length()> msg_length?msg_length:responseXml.length())));
    			
    		}
    		if(!isPassed){
    			WmLog.getCoreLogger().info("FAILED: VALIDATE assert >> "+msg);
    			System.out.println("FAILED: VALIDATE assert >> "+msg);
    			if(this.page.isUnitTest()){
    				//this.addUnitTestActionMonitor(this.getActionName(), msg, false);
    				this.addUnitTestActionMonitor(this.getActionName(),name, msg, false);
    			}
    			return false;
    		}else{
    			WmLog.getCoreLogger().info("PASSED: VALIDATE assert >> "+msg);
    			System.out.println("PASSED: VALIDATE assert >> "+msg);
    			if(this.page.isUnitTest()){
    				//this.addUnitTestActionMonitor(this.getActionName(), msg, true);
    				this.addUnitTestActionMonitor(this.getActionName(),name, msg, true);
    			}
    		}
    	}
    	return(isPassed);
    }
    
  //synchronized
    protected void stopMethodRecording(int statusCode, long elaspedtime, int startcount){
    	
    	this.elaspedTime=Double.parseDouble(String.valueOf(elaspedtime));
        this.executionCount=this.executionCount+1;
        if(this.executionCount>startcount){
            this.totalExecutionTime=totalExecutionTime+ elaspedtime;
          
            this.avgTime=this.totalExecutionTime/this.executionCount-startcount;
            
            if(statusCode==200){
                this.totalOKTime=this.totalOKTime+elaspedtime;
                responseOKCount=responseOKCount+1;
                this.avgOKTime=this.totalOKTime/this.responseOKCount;
            }else{
                this.responseERCount=responseERCount+1;
            }
        }
        
    }
    
    protected void printMethodRecording(int statusCode, String url){
    
        System.out.println("Performance Summary: ");
        System.out.println("Target URL: "+ url); 
        System.out.println("Response Status: "+ statusCode);
        System.out.println("URL Response: "+ this.page.getURLResponse());
        System.out.println("Total Execution Time (mil Sec): "+this.totalExecutionTime);
        System.out.println("Total Execution Count: "+this.executionCount);
        System.out.println("Average Response Time (mil Sec): "+this.avgTime);
        System.out.println("Total OK Response: "+this.responseOKCount);
        System.out.println("Average OK Response Time (mil Sec): "+this.avgOKTime);
        System.out.println("Total ERROR Response: "+this.responseERCount);
        
    }
    
    protected void printMethodLog(int statusCode, String url){
        WmLog.getCoreLogger().info("Performance Summary: ");  
        WmLog.getCoreLogger().info("Target URL: "+ url);
        WmLog.getCoreLogger().info("Response Status: "+ statusCode);
        WmLog.getCoreLogger().info("URL Response: "+ this.page.getURLResponse());
        WmLog.getCoreLogger().info("Total Execution Time (mil Sec): "+this.totalExecutionTime);
        WmLog.getCoreLogger().info("Total Execution Count: "+this.executionCount);
        WmLog.getCoreLogger().info("Average Response Time (mil Sec): "+this.avgTime);
        WmLog.getCoreLogger().info("Total OK Response: "+this.responseOKCount);
        WmLog.getCoreLogger().info("Average OK Response Time (mil Sec): "+this.avgOKTime);
        WmLog.getCoreLogger().info("Total ERROR Response: "+this.responseERCount);
        WmLog.getCoreLogger().info("******** Time Measurement ended **************"); 
    }
    public void startRecording(){
        WmLog.getCoreLogger().info("******** Time Measurement started **************");       
    }
    
    public String getActionName(){
 		return this.action;
 	}
    
    //synchronized
    public void stopRecording(HttpResponse response, long elaspedtime){
    	this.elaspedTime=Double.parseDouble(String.valueOf(elaspedtime));
        this.executionCount=this.executionCount+1;
        if(this.executionCount>5){
            this.totalExecutionTime=totalExecutionTime+ elaspedtime;
          
            this.avgTime=this.totalExecutionTime/this.executionCount-5;
            
            if(response.getStatusLine().getStatusCode()==200){
                this.totalOKTime=this.totalOKTime+elaspedtime;
                responseOKCount=responseOKCount+1;
                this.avgOKTime=this.totalOKTime/this.responseOKCount;
            }else{
                this.responseERCount=responseERCount+1;
            }
        }
        this.response=response;
    }
    
    //for soap test
    public void stopRecording(int status, long elaspedtime){
    	this.elaspedTime=Double.parseDouble(String.valueOf(elaspedtime));
        this.executionCount=this.executionCount+1;
        if(this.executionCount>5){
            this.totalExecutionTime=totalExecutionTime+ elaspedtime;
          
            this.avgTime=this.totalExecutionTime/this.executionCount-5;
            
            if(status==200){
                this.totalOKTime=this.totalOKTime+elaspedtime;
                responseOKCount=responseOKCount+1;
                this.avgOKTime=this.totalOKTime/this.responseOKCount;
            }else{
                this.responseERCount=responseERCount+1;
            }
        }
        this.statusCode=String.valueOf(status);
        //this.response=response;
    }
    
    
    public void printDbLog(){
        WmLog.getCoreLogger().info("Performance Summary: ");  
        WmLog.getCoreLogger().info("Target API: "+ this.page.getURLConfig().getUrlElement().getName());
        WmLog.getCoreLogger().info("Response Status: "+ this.dbResponse);
        WmLog.getCoreLogger().info("Total Execution Time (mil Sec): "+this.totalExecutionTime);
        WmLog.getCoreLogger().info("Total Execution Count: "+this.executionCount);
        WmLog.getCoreLogger().info("Average Response Time (mil Sec): "+this.avgTime);
        WmLog.getCoreLogger().info("Total OK Response: "+this.responseOKCount);
        WmLog.getCoreLogger().info("Average OK Response Time (mil Sec): "+this.avgOKTime);
        WmLog.getCoreLogger().info("Total ERROR Response: "+this.responseERCount);
        WmLog.getCoreLogger().info("******** Time Measurement ended **************"); 
    }
    public void printDbRecording(){
        System.out.println("Performance Summary: ");
        WmLog.getCoreLogger().info("Target API: "+ this.page.getURLConfig().getUrlElement().getName());
        System.out.println("Response Status: "+ this.dbResponse);
        System.out.println("Total Execution Time (mil Sec): "+this.totalExecutionTime);
        System.out.println("Total Execution Count: "+this.executionCount);
        System.out.println("Average Response Time (mil Sec): "+this.avgTime);
        System.out.println("Total OK Response: "+this.responseOKCount);
        System.out.println("Average OK Response Time (mil Sec): "+this.avgOKTime);
        System.out.println("Total ERROR Response: "+this.responseERCount);
        
    }
    public void printLog(){    	
        WmLog.getCoreLogger().info("Performance Summary: ");  
        WmLog.getCoreLogger().info("Target URL: "+ this.page.getURLConfig().getUrl());
        WmLog.getCoreLogger().info("Response Status: "+ ( this.statusCode!=null?this.statusCode: this.response.getStatusLine()));
        WmLog.getCoreLogger().info("URL Response: "+ this.page.getURLResponse());
        WmLog.getCoreLogger().info("Total Execution Time (mil Sec): "+this.totalExecutionTime);
        WmLog.getCoreLogger().info("Total Execution Count: "+this.executionCount);
        WmLog.getCoreLogger().info("Average Response Time (mil Sec): "+this.avgTime);
        WmLog.getCoreLogger().info("Total OK Response: "+this.responseOKCount);
        WmLog.getCoreLogger().info("Average OK Response Time (mil Sec): "+this.avgOKTime);
        WmLog.getCoreLogger().info("Total ERROR Response: "+this.responseERCount);
        WmLog.getCoreLogger().info("******** Time Measurement ended **************"); 
    }
    public void printRecording(){
        System.out.println("Performance Summary: ");
        System.out.println("Target URL: "+ this.page.getURLConfig().getUrl());
        System.out.println("Response Status: "+ ( this.statusCode!=null?this.statusCode: this.response.getStatusLine()));
        System.out.println("URL Response: "+ this.page.getURLResponse());
        System.out.println("Total Execution Time (mil Sec): "+this.totalExecutionTime);
        System.out.println("Total Execution Count: "+this.executionCount);
        System.out.println("Average Response Time (mil Sec): "+this.avgTime);
        System.out.println("Total OK Response: "+this.responseOKCount);
        System.out.println("Average OK Response Time (mil Sec): "+this.avgOKTime);
        System.out.println("Total ERROR Response: "+this.responseERCount);
        
    }
  
    
   
    /**
     * It returns a found match in the form of String.
     * @param str - This is a string to search.
     * @param matchPattern - This is a Regular Expression pattern such as "\\d+" or "\\m+" and so on.
     * @param caseInsensitive - This is a flag which indicates whether we want to
     * perform a case insensitive string matching. If it's set to 'true' case insensitive matching is used.
     * @return
     * Example: String value = getFoundMatch ("Blahaha went to the 444 Street.", "\\d+");
     *          value = "444";
     */
    protected  String getFoundMatch (String str, String regex, boolean caseInsensitive) {

        Pattern p = (caseInsensitive) ? Pattern.compile(regex, Pattern.CASE_INSENSITIVE) :
            Pattern.compile(regex);

        Matcher m = p.matcher(str);

        m.find();

        return String.valueOf(m.group());
    }
    
    
    
    /*
     * Set timer with system time
     */
    
    public long getTimer(){
       return(System.currentTimeMillis());
    }
    
    /*
     * get HttpClient
     */
    
    public HttpClient getHttpClient(){
       return(this.httpclient);
    }
    
    
    /*
     * Get elasped time after setting the timer in mili sec
     */
    
    public long getElaspedTime(long starttime){
        return(System.currentTimeMillis()-starttime);
    }
    /*
     * Close a connection for any HTTP POST
     */
    public void closeHttpClientConnection(){
        this.httpclient.getConnectionManager().shutdown();  
    }
    
    
    
    public long getCurrentTime(){
         return(System.currentTimeMillis());
    }
    
    public int getResponseStatus(){
        return(this.response.getStatusLine().getStatusCode());
   }
    /*
     * This method returns String representation of HTML response body
     */
    public String getResponseBodyAsString(HttpEntity entity){
        
        BufferedReader reader=null;
       
        StringBuilder builder = new StringBuilder();
           
            try {
                InputStream instream = entity.getContent();
                
                reader = new BufferedReader(
                        new InputStreamReader(instream));
                String line ="";
                while((line = reader.readLine())!=null){
              
                    builder.append(line);
                }
               
            } catch (IOException ex) {   
                
            } catch (RuntimeException ex) {
                
            }
            finally {
               
                if(reader != null) try { reader.close(); } catch (Exception fe) {}
              }
       
       return(builder.toString());
    }
   
    /*
     * This method returns String representation of HTML response body
     */
    public String getResponseBodyAsString(InputStream instream){
        
        BufferedReader reader=null;
       
        StringBuilder builder = new StringBuilder();
           
            try {               
                
                reader = new BufferedReader(
                        new InputStreamReader(instream));
                String line ="";
                while((line = reader.readLine())!=null){
              
                    builder.append(line);
                }
               
            } catch (IOException ex) {   
                
            } catch (RuntimeException ex) {
                
            }
            finally {
               
                if(reader != null) try { reader.close(); } catch (Exception fe) {}
              }
       
       return(builder.toString());
    }
    public void closeEntity(HttpEntity entity){
        if(entity!=null){
            try {
                entity.consumeContent();
            } catch (IOException e) {
               
            }
        }
    }
    /*
     * This method parse html content and returns an URL if available from Form submit action
     */
    
    public  String parseActionURL(String htmlcontent) throws Exception {
         String action=null;
          try{
              String getAction = getFoundMatch(htmlcontent, "action=.*?\\s+",false);
              
              action = getAction.split("action=")[1];
              action = action.replaceAll("\"", "");
              System.out.println("action="+action);
           
            } catch (Exception e) {
              System.err.println(e);
            } 
            return(action);
             
      }
    
    /*
     * This method prints all headers from HttpResponse
     */
    public void printHeader(HttpResponse response){
        for(int i=0;i<response.getAllHeaders().length; i++){            
            System.out.println("Headers form get: " + response.getAllHeaders()[i].getName());
        }
    }
      
  /*
      protected void printCookies(){
          List<Cookie> cookies = httpclient.getCookieStore().getCookies();
          if (cookies.isEmpty()) {
              System.out.println("None");
          } else {
              for (int i = 0; i < cookies.size(); i++) {
                  System.out.println("- " + cookies.get(i).toString());
              }
          }
      }
      */
    
    
    public String handleHTTPPost() throws Exception {
    	String resp="";
    	if(this.page.isUnitTest()){
    		//resp=this.handleHTTPPostWithClientManager();
    		resp=this.handleHTTPPostUnit();
    	}else{
    		resp=this.handleHTTPPostPerformance();
    	}    	
    	return (resp);
    }
    
    
    public String handleHTTPGet() throws Exception {
    	String resp="";
    	if(this.page.isUnitTest()){
    		//resp=this.handleHTTPPostWithClientManager();
    		resp=this.handleHTTPGetUnit();
    	}else{
    		resp=this.handleHTTPGetPerformance();
    	}    	
    	return (resp);
    }
    
    public void validateExpected(String assertname,String message,String actual, String value, String operator, String datatype){
    	
    	String msg=message+" "+actual;
    	if(actual!=null&&actual.contains("junit:")){
    		actual="null";
    	}
    	 if(!actual.isEmpty() && !value.isEmpty() && datatype.equalsIgnoreCase("NUMBER")){
    		 double expt=0;
    		 long val=0;
    		 try{
    			 expt=Double.valueOf(actual);    			
    		 }catch (Exception e){
    			 this.printMessage("ERROR - Wrong NUMBER in ASSERT for actual="+actual + " Message:"+e.getMessage());
    			 expt=-101;
    		 }
    		 try{
    			 val=Long.valueOf(value);
    		 }catch (Exception e){
    			 this.printMessage("ERROR - Wrong NUMBER in ASSERT for val="+val + " Message:"+e.getMessage());
    			 val=-102;
    		 }
	    	if(operator.equalsIgnoreCase("Gt")){
	    		if(expt>val){
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, true);
	    			this.printMessage("PASSED -"+actual);
	    		}else{
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, false);
	    			this.printMessage("FAILED -"+actual);
	    		}
	    	}else if (operator.equalsIgnoreCase("Eq")){
	    		if(expt==val){
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, true);
	    			this.printMessage("PASSED -"+actual);
	    		}else{
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, false);
	    			this.printMessage("FAILED -"+actual);
	    		}
	    		
	    	}else if (operator.equalsIgnoreCase("GtEq")){
	    		if(expt>=val){
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, true);
	    			this.printMessage("PASSED -"+actual);
	    		}else{
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, false);
	    			this.printMessage("FAILED -"+actual);
	    		}
	    		
	    	}else if (operator.equalsIgnoreCase("Lt")){
	    		if(expt<val){
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, true);
	    			this.printMessage("PASSED -"+actual);
	    		}else{
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, false);
	    			this.printMessage("FAILED -"+actual);
	    		}
	    		
	    	}else if (operator.equalsIgnoreCase("LtEq")){
	    		if(expt<=val){
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, true);
	    			this.printMessage("PASSED -"+actual);
	    		}else{
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, false);
	    			this.printMessage("FAILED -"+actual);
	    		}
	    	}else if (operator.equalsIgnoreCase("Nq")){
	    		if(expt!=val){
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, true);
	    			this.printMessage("PASSED -"+actual);
	    		}else{
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, false);
	    			this.printMessage("FAILED -"+actual);
	    		}
	    	}
	    	
    	}else if(!actual.isEmpty() && !value.isEmpty() && datatype.equalsIgnoreCase("VARCHAR")){
	    	if(operator.equalsIgnoreCase("Nq")){
	    		if(actual!=null &&!actual.equalsIgnoreCase(value)){
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, true);
	    			this.printMessage("PASSED -"+actual);
	    		}else{
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, false);
	    			this.printMessage("FAILED -"+actual);
	    		}
	    	}else if (operator.equalsIgnoreCase("Eq")){
	    		if(actual!=null &&actual.equalsIgnoreCase(value)){
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, true);
	    			this.printMessage("PASSED -"+actual);
	    		}else{
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, false);
	    			this.printMessage("FAILED -"+actual);
	    		}
	    	}else if (operator.equalsIgnoreCase("Has")){
	    		String row="";
	    		String coldata="";
	    		String tmpval="";
	    		if(actual.contains("&")){
	    			tmpval=actual.replaceAll("&", "and");
	    			actual=tmpval;
	    		}
	    		if(message.length()>1000){
	    			String[] msgs=message.split(">>");
	    			
	    			String[] datacolset=message.split("DataColSet:");
	    			
	    			if(datacolset.length>0){
	    				row=datacolset[1].split(">>")[0];
	    				coldata=" Query Dataset:"+datacolset[1].split(">>")[1];
	    				//msg=msg+"<br><br> Dataset:"+datacolset[1];
	    			}
	    			if(msgs.length>4){
	    				
	    				msg=msgs[0]+">>"+msgs[1]+">>"+msgs[3]+">>"+row+">> Matching Value="+actual;
	    			}
	    			
	    		}
	    		
	    		if(actual!=null &&(actual.contains(value)||value.contains(actual))){
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, true);
	    			this.printMessage("PASSED -"+row+" value= "+actual);
	    		}else if(actual!=null &&(actual.toLowerCase().contains(value.toLowerCase())||value.toLowerCase().contains(actual.toLowerCase()))){
		    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, true);
		    			this.printMessage("PASSED -"+row+" value= "+actual);
	    		}else if(actual!=null &&this.validateHas(actual, value)){
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, true);
	    			this.printMessage("PASSED -"+row+" value= "+actual);
	    		}else{
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg+"<br>"+coldata, false);
	    			this.printMessage("FAILED -"+row+" actual= "+actual +coldata + "\n value=" +value);
	    		}
	    	}else if (operator.equalsIgnoreCase("NotHas")||operator.equalsIgnoreCase("Not Has")){
	    		if(actual!=null &&(!actual.contains(value)||!value.contains(actual))){
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, true);
	    			this.printMessage("PASSED -"+actual);
	    		}else if(actual!=null &&(!actual.toLowerCase().contains(value.toLowerCase())||!value.toLowerCase().contains(actual.toLowerCase()))){
		    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, true);
		    			this.printMessage("PASSED -"+actual);
	    		}else{
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, false);
	    			this.printMessage("FAILED -"+actual);
	    		}
	    	}else if (operator.equalsIgnoreCase("NULL")){
	    		if(actual.equalsIgnoreCase("null") ){
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, true);
	    			this.printMessage("PASSED -"+actual);
	    		}else{
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, false);
	    			this.printMessage("FAILED -"+actual);
	    		}
	    	}else if (operator.equalsIgnoreCase("NOTNULL") ||operator.equalsIgnoreCase("NOT NULL")){
	    		if(actual!=null &&!actual.isEmpty()){
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, true);
	    			this.printMessage("PASSED -"+actual);
	    		}else{
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, false);
	    			this.printMessage("FAILED -"+actual);
	    		}
	    	}
    	}else{
    		if(operator.equalsIgnoreCase("Eq")){
	    		if(actual==null ||actual.isEmpty()){
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, true);
	    			this.printMessage("PASSED -"+actual);
	    		}else{
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, false);
	    			this.printMessage("FAILED -"+actual);
	    		}
	    	}else if (operator.equalsIgnoreCase("Nq")){
	    		if(actual!=null &&!actual.isEmpty()){
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, true);
	    			this.printMessage("PASSED -"+actual);
	    		}else{
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, false);
	    			this.printMessage("FAILED -"+actual);
	    		}
	    	}else if (operator.equalsIgnoreCase("NULL")){
	    		if(actual==null ||actual.isEmpty()||actual.equalsIgnoreCase("null")){
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, true);
	    			this.printMessage("PASSED -"+actual);
	    		}else{
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, false);
	    			this.printMessage("FAILED -"+actual);
	    		}
	    	}else if (operator.equalsIgnoreCase("NOTNULL")||operator.equalsIgnoreCase("NOT NULL")){
	    		if(actual!=null &&!actual.isEmpty()){
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, true);
	    			this.printMessage("PASSED -"+actual);
	    		}else{
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, false);
	    			this.printMessage("FAILED -"+actual);
	    		}
	    	}else if (operator.equalsIgnoreCase("NotHas")||operator.equalsIgnoreCase("Not Has")){
	    		if(actual!=null &&(!actual.contains(value)||!value.contains(actual))){
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, true);
	    			this.printMessage("PASSED -"+actual);
	    		}else if(actual!=null &&(!actual.toLowerCase().contains(value.toLowerCase())||!value.toLowerCase().contains(actual.toLowerCase()))){
		    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, true);
		    			this.printMessage("PASSED -"+actual);
	    		}else{
	    			this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, false);
	    			this.printMessage("FAILED -"+actual);
	    		}
	    	}else{
	    		this.addUnitTestActionMonitor(this.getActionName(),assertname, msg, false);
	    		this.printMessage("FAILED -"+actual);
	    	}
    	}
    }
    
    private boolean validateHas(String actual,String value){
    	String  value1=value.replaceAll("[\\n\\r\\t]+", "").replaceAll(" ", "");
    	String actual1=actual.replaceAll("[\\n\\r\\t]+", "").replaceAll(" ", "");
    	if(actual1.contains(value1)||value1.contains(actual1)){
    		 return true;
    	}
    	
    	return false;
    }
    public void closeDriver(){
    	try{
    		teardown();
    	}catch(Exception e){
    		this.printMessage("FAILED - teardown()"+e.getMessage());
    		e.printStackTrace();
    	}
    	if(this.webdriver!=null){
    		this.webdriver.quit();
    	}
    	
    }
    
    protected void teardown(){};
    
   
   
    
   
    protected void resetJenkinSignal(){}
    
    /*
     *  Override this method in your Performance Adaptor implementation
     */
    protected String getPerformanceSummaryReportData(){
    	return "";
    }
    
    public void printMessage(String msg){
		 WmLog.getCoreLogger().info(msg);						
		 System.out.println(" "+msg);
	}
    /*
     *  Implement this method for custom invocation of HTTP POST
     */
    abstract protected String handleHTTPPostUnit() throws Exception;
    
    
    
    abstract protected String handleHTTPPostPerformance() throws Exception;
    
    /*
     *  Implement this method for custom invocation of HTTP POST
     */
    abstract protected String handleHTTPGetUnit() throws Exception;
    
    
    
    abstract protected String handleHTTPGetPerformance() throws Exception;
    
    
  
}

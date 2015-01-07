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

package com.testmax.util;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Platform;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.testmax.Exception.TestMaxException;
import com.testmax.framework.ConfigLoader;
import com.testmax.framework.WmLog;
import com.testmax.runner.TestEngine;

public class JavaScriptUtl {

	private volatile WebDriver driver;
	private long timeout=3000;
	private int maxTimeToWait=120;
	private String trackId;
	private final String chrome_driver_path=ConfigLoader.getWmRoot()+"/lib/chromedriver.exe";	    	
	private final String ie_driver_path32bit=ConfigLoader.getWmRoot()+"/lib/IEDriverServer_32.exe";
	private final String ie_driver_path64bit=ConfigLoader.getWmRoot()+"/lib/IEDriverServer_64.exe";  	
	public long pagewaittime=50;
	public String browser=null;
	public JavaScriptUtl(){
		
	}
	
	public WebDriver initWebDriverDriver(String browser){
		
		this.browser=browser;
		
		File file= null; 
	    	if(browser==null||browser.isEmpty()){
	    		browser="firefox";
	    	}
		   try{
		    String driver_path="";
		    if(browser.equalsIgnoreCase("chrome")){
		    	if(TestEngine.suite!=null){
		    		String chrome_path=TestEngine.suite.getWorkspace()+TestEngine.suite.getJobname()+"/lib/chromedriver.exe";
		    		file = new File(chrome_path);
		    		if(!file.exists()){
		    			file = new File(TestEngine.suite.getWorkspace()+"/lib/chromedriver.exe");
		    		}
		    	}else{
		    		file = new File(driver_path!=null &&!driver_path.isEmpty()?driver_path:chrome_driver_path);
		    	}
		    	 WmLog.printMessage("Chrome Driver Path="+file.getAbsolutePath());
		    	System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
		    	if(System.getProperty("os.name").toLowerCase().contains("mac")){
		    		file = new File(ConfigLoader.getWmRoot()+"/lib/chromedriver_mac");
			    	System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
		    		DesiredCapabilities capability = DesiredCapabilities.chrome();
					capability.setCapability("platform", Platform.ANY);
					capability.setCapability("binary", "/Application/chrome"); //for linux
					driver = new ChromeDriver(capability);
		    		
		    	}else{
		    		driver= new ChromeDriver();
		    	}
		    }else if (browser.equalsIgnoreCase("ie")){
		    	if(is64bit()){
		    		file = new File(driver_path!=null &&!driver_path.isEmpty()?driver_path:ie_driver_path64bit);
		    	}else{
		    		file = new File(driver_path!=null &&!driver_path.isEmpty()?driver_path:ie_driver_path32bit);
		    	}
		    	 WmLog.printMessage("##### IE DRIVER PATH="+file.getAbsolutePath());
		    	System.setProperty("webdriver.ie.driver", file.getAbsolutePath());
		    	driver=new InternetExplorerDriver();
		    	maxTimeToWait=maxTimeToWait*5;
		    	
		    }else if(browser.equalsIgnoreCase("firefox")){
		    	DesiredCapabilities capability = DesiredCapabilities.firefox();
		        LoggingPreferences prefs = new LoggingPreferences();
		        prefs.enable(LogType.BROWSER, Level.ALL);
		        capability.setCapability(CapabilityType.LOGGING_PREFS, prefs);
		      
	    	  
		    	if(System.getProperty("os.name").toLowerCase().contains("mac")){
		    		//DesiredCapabilities capability = DesiredCapabilities.firefox();
					capability.setCapability("platform", Platform.ANY);
					capability.setCapability("binary", "/Application/firefox"); //for linux
					
					driver = new FirefoxDriver(capability);
		    		
		    	}else{
		    	
		    		driver = new FirefoxDriver(capability);
		    	}
		    }else if(browser.equalsIgnoreCase("safari")){
		    	  
			    	if(System.getProperty("os.name").toLowerCase().contains("mac")){
			    		 WmLog.printMessage("#####STARTING Safri in Mac ####");
			    		DesiredCapabilities capability = DesiredCapabilities.safari();
						capability.setCapability("platform", Platform.ANY);
						capability.setCapability("binary", "/Application/safari"); //for linux
						driver = new SafariDriver(capability);
			    		
			    	}else{
			    		// Read Instruction for Safari Extension
			    		//http://rationaleemotions.wordpress.com/2012/05/25/working-with-safari-driver/
			    		// Get certificate from https://docs.google.com/folder/d/0B5KGduKl6s6-ZGpPZlA0Rm03Nms/edit
			    		 WmLog.printMessage("#####STARTING Safri in Windows ####");
			    		String safari_install_path="C:\\Program Files (x86)\\Safari\\";
			    		DesiredCapabilities capability = DesiredCapabilities.safari();
						capability.setCapability("platform", Platform.ANY);
						capability.setCapability("binary", safari_install_path+"Safari.exe"); //for windows 
						//capability.setCapability(SafariDriver.DATA_DIR_CAPABILITY, "C:\\Program Files (x86)\\Safari\\SafariData");
						//System.setProperty("webdriver.safari.driver", safari_install_path+"SafariDriver.safariextension\\");
			    		driver = new SafariDriver(capability);
			    		
			    		
			    	}	
		    }else if (browser.equalsIgnoreCase("htmlunit")){
		    	DesiredCapabilities capability = DesiredCapabilities.htmlUnit();
		    	capability.setJavascriptEnabled(true);
		    	//capability.setCapability("browserName","chrome");
		    	//capability.setBrowserName(BrowserVersion.CHROME);
		    	driver = new HtmlUnitDriver(capability);
		    	
		    }else{
		    	file = new File(driver_path!=null &&!driver_path.isEmpty()?driver_path:chrome_driver_path);
		       	System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
		    	driver= new ChromeDriver();
		    	
		    }
		    driver.manage().timeouts().implicitlyWait(maxTimeToWait, TimeUnit.SECONDS);
		    
		   }catch(Exception e){
			   
			   WmLog.printMessage("******** FAILED to launch browser="+this.browser + " :"+e.getMessage());
			   e.printStackTrace();
		   }
		   
		    return driver;
	}
	public boolean performLogin( String url){
		   PrintTime timer= new PrintTime();
			     
		    try{
		    	this.driver.manage().timeouts().implicitlyWait(timeout+10000, TimeUnit.MILLISECONDS);
		    
		    	this.driver.get(url);
	    		 WmLog.printMessage("**** Loaded URL "+timer.getPrintTime()+" URL="+url);
	    		
		    	return true;
		    
		    	
		    }catch(Exception e){
		    	e.printStackTrace();
		    	WmLog.printMessage("**** Failed toLoad URL "+timer.getPrintTime()+" URL="+url +" "+e.getMessage());
		    }  
	   
	    return false;
	 }
	
	public boolean loadUrl( String url){
		   PrintTime timer= new PrintTime();
			     
		    try{
		    	this.driver.manage().timeouts().implicitlyWait(timeout+20000, TimeUnit.MILLISECONDS);
		    
		    	this.driver.get(url);
	    		 WmLog.printMessage("**** Loaded URL "+timer.getPrintTime()+" URL="+url);
	    		
		    	return true;
		    
		    	
		    }catch(Exception e){
		    	e.printStackTrace();
		    	WmLog.printMessage("**** Failed toLoad URL "+timer.getPrintTime()+" URL="+url +" "+e.getMessage());
		    }  
	   
	    return false;
	 }
	
	public String extractEid(){
		   PrintTime timer= new PrintTime();
			     
		    try{
		    	this.driver.manage().timeouts().implicitlyWait(timeout+20000, TimeUnit.MILLISECONDS);
		    
		    	String[] vals=this.driver.getCurrentUrl().split("eid=");
		    	if(vals.length>1){
		    		WmLog.printMessage("**** Loaded URL "+timer.getPrintTime()+" URL="+vals[1]);
		    		return vals[1];
		    	}
		    
		    	
		    }catch(Exception e){
		    	e.printStackTrace();
		    	WmLog.printMessage("**** Failed toLoad URL "+timer.getPrintTime()+" URL="+this.driver.getCurrentUrl() +" "+e.getMessage());
		    }  
	   
	    return "";
	 }
	public String loadUrlWithAlert( String url, String trakerId, String trackedVal){
		   PrintTime timer= new PrintTime();
		   String result="";     
		    try{
		    	this.driver.manage().timeouts().implicitlyWait(timeout+10000, TimeUnit.MILLISECONDS);
		    	
		    	try{
		    		this.driver.get(url);
		    	}catch(UnhandledAlertException e){
		    		this.driver.switchTo().alert().accept();
		    	}
		    	
	    		 WmLog.printMessage("**** Loaded URL "+timer.getPrintTime()+" URL="+url);
	    		
	    		 while(result!=null&& !result.contains(trackedVal)){
	    			 try{
	    			 WebElement elm=this.driver.findElement(By.id(trakerId));
	    			 result=elm.getText();
	    			 }catch(UnhandledAlertException e){
	 		    		this.driver.switchTo().alert().accept();
	 		    	}
	    			 Thread.sleep(pagewaittime);
	    			
	    			
	    		 }
		    	return result;
		    
		    	
		    }catch(Exception e){
		    	e.printStackTrace();
		    	WmLog.printMessage("**** Failed toLoad URL "+timer.getPrintTime()+" URL="+url +" "+e.getMessage());
		    }  
	   
	    return result;
	 }
	public String loadUrlWithTracker( String url, String trakerId, String trackedVal){
		   PrintTime timer= new PrintTime();
		   String result="";     
		    try{
		    	this.driver.manage().timeouts().implicitlyWait(timeout+10000, TimeUnit.MILLISECONDS);
		    
		    	try{
		    		this.driver.get(url);
		    	}catch(UnhandledAlertException e){
		    		this.driver.switchTo().alert().accept();
		    	}
		    	
		    	
	    		 WmLog.printMessage("**** Loaded URL "+timer.getPrintTime()+" URL="+url);
	    		
	    		 while(result!=null&& !result.contains(trackedVal)){
	    			 WebElement elm=this.driver.findElement(By.id(trakerId));
	    			 result=elm.getText();
	    			 Thread.sleep(pagewaittime);
	    			
	    		 }
		    	return result;
		    
		    	
		    }catch(Exception e){
		    	e.printStackTrace();
		    	WmLog.printMessage("**** Failed toLoad URL "+timer.getPrintTime()+" URL="+url +" "+e.getMessage());
		    }  
	   
	    return result;
	 }
	
	public String executeScript( String script, String trackerId, String trackVal){
		   PrintTime timer= new PrintTime();
		   String result="";
		   
		    try{
		    	this.driver.manage().timeouts().implicitlyWait(timeout+10000, TimeUnit.MILLISECONDS);
		    
	    		 ((JavascriptExecutor) this.driver).executeScript(script);
	    		 WmLog.printMessage("**** Executed Java Script "+timer.getPrintTime()+" Script="+script);
	    		 while(result!=null&& !result.contains(trackVal)){
	    			 WebElement elm=this.driver.findElement(By.id(isEmptyValue(trackerId)?this.trackId:trackerId));
	    			 result=elm.getText();
	    			 Thread.sleep(pagewaittime);
	    			 
	    		 }
		    	return result;
		    
		    	
		    }catch(Exception e){
		    	e.printStackTrace();
		    	WmLog.printMessage("**** Failed to Execute Java Script "+timer.getPrintTime()+" Script="+script +" "+e.getMessage());
		    }  
	   
	    return null;
	 }
	
	public boolean addAttributeToElement() throws NumberFormatException, TestMaxException{
		
		    PrintTime timer= new PrintTime();		 
		    this.trackId="TestMax_SeleniumTrackId";
			this.driver.manage().timeouts().implicitlyWait(timeout+10000, TimeUnit.MILLISECONDS);
			if(!this.isEmptyValue(this.browser) &&this.browser.equalsIgnoreCase("htmlunit")) return true;
			 try{ 
				 	
				   ((JavascriptExecutor) this.driver).executeScript("var testMaxElm= document.getElementsByTagName('body')[0]; "+						 
						   " var state=document.readyState; "+
						   " var testMaxNewElm = document.getElementById('"+this.trackId+"');  "+
						   " if(testMaxNewElm==null) {  " +
				   				" testMaxNewElm = document.createElement('div'); testMaxNewElm.setAttribute(\"id\",\""+this.trackId+"\"); " +
				   				" testMaxNewElm.setAttribute(\"status\",state);  testMaxElm.appendChild(testMaxNewElm); "+
				   			"}else{testMaxNewElm.setAttribute(\"status\",state);} testMaxNewElm.innerHTML='';" );
				   WmLog.printMessage("**** Added Trace"+timer.getPrintTime()+" DIV id="+this.trackId );
		        	
		        	return true;
		        }catch(Exception e){
		        	String msg="**** Failed to Add Trace "+timer.getPrintTime()+" DIV id="+this.trackId;
		        	WmLog.printMessage(msg);
		        	try{
		        		
		        		if(this.driver.switchTo().alert()!=null){
		        			this.driver.switchTo().alert().accept();
		        			msg="**** ALERT Traced "+timer.getPrintTime()+" DIV id="+this.trackId;
		        		}else{
		        			throw new TestMaxException(msg);
		        		}
		        		
		        	}catch(Exception a){
		        		throw new TestMaxException(msg);
		        	}
		        	
		        }
			     
		    
	   
	    return false;
	 }
	
	public boolean waitToPageLoad() { 
		   final PrintTime timer= new PrintTime(); 
		   int counter=0;
		   WebElement element=null;	  
		   long addTimeOut=500;
		   
		   if(!this.isEmptyValue(this.browser) &&this.browser.equalsIgnoreCase("htmlunit")) return true;
		   
		   if(ConfigLoader.getConfig("SELENIUM_DRIVER")!=null 
	   			&&ConfigLoader.getConfig("SELENIUM_DRIVER").equalsIgnoreCase("ie")){
			   addTimeOut=addTimeOut+
			   new Integer(ConfigLoader.getConfig("SELENIUM_IE_EXTRA_TIME")==null?"500":ConfigLoader.getConfig("SELENIUM_IE_EXTRA_TIME"));
		   } 
		   
		   try{
	      //Set up a WebDriverWait 
	       WebDriverWait driverWait = new WebDriverWait(driver, timeout+addTimeOut); 
	      
	      //Define an ExpectedCondition 
	               ExpectedCondition<WebElement> expectedCondition = new   ExpectedCondition<WebElement>() { 
	            	
	                      public WebElement apply(WebDriver d) { 
	                    	  
	                    	  WebElement element1 =null;
	                    	  boolean isTraceAdded=false;
	                    	  int timeElapsed=0;
	                    	  try { 
		                    	  while (!isTraceAdded){
		                    		  isTraceAdded=addAttributeToElement();
		                    		  timeElapsed=timeElapsed+200;
		                    		  Thread.sleep(0);
		                    	  }
		                    	  
	                    	  } catch (TestMaxException t) {
	                      	 	// TODO Auto-generated catch block
	         						t.printStackTrace();
	         						return null;
	                          
	                    	  } catch (InterruptedException e) {
	                        	 	// TODO Auto-generated catch block
	           						e.printStackTrace();
	                            } 
	                    	  if(isTraceAdded){
	                           
	                            	  driver.manage().timeouts().implicitlyWait(timeout+10000, TimeUnit.MILLISECONDS);
	                            	 
	                            	  WmLog.printMessage("**** LOCATING PAGE LOAD TRACE ELEMENT:"+timer.getPrintTime()+" with Id="+trackId);
	                            	  element1 = d.findElement(By.id(trackId)); 
	                            	
	                            	if(element1!=null){
	                            		WmLog.printMessage("**** PAGE LOAD TRACE ELEMENT LOCATED :"+timer.getPrintTime()+" Status="+element1.getAttribute("status")+" with Id="+trackId);
	                            		return element1.getAttribute("status").equalsIgnoreCase("complete")?  element1:null; 
	                            	}
	                           
	                    	  }
	                    	
	                    	  return null;
	                      }
	            	 
	               }; 

	              //Wait for page load
	               while(element==null){
	            	   counter++;
	            	   WmLog.printMessage("******WAITING FOR PAGE LOAD TRIAL="+counter+" Element Trace Time:"+timer.getPrintTime()+" with Id="+this.trackId);    
	            	   element = driverWait.until(expectedCondition); 
	               }
	               this.driver.manage().timeouts().implicitlyWait(timeout+10000, TimeUnit.MILLISECONDS);
	               WmLog.printMessage("####### Element Trace Time:"+timer.getPrintTime()+" with Id="+trackId); 
	               
	               this.pagewaittime=timer.getTime();
	               if(element==null){
	            	   throw new TestMaxException("$$$$$ FAILED to add trace element! Element Trace Time:"+timer.getPrintTime()+" with Id="+this.trackId);
	               }
	       
	      //True or false, did we return it? 
		   }catch (Exception e){
			   WmLog.printMessage("####### FAILED Element Trace Time:"+timer.getPrintTime()+" with Id="+this.trackId);
			   this.pagewaittime=timer.getTime();
			   return (element!=null); 
		   }
	       return (element!=null); 
	   
	   }
	public void closeDriver(){
		
		this.driver.quit();
	}
	public String retrieveOutput(){
		if(this.trackId!=null &&!this.trackId.isEmpty()){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try{
				this.driver.manage().timeouts().implicitlyWait(timeout+10000, TimeUnit.MILLISECONDS);
				WebElement elm=this.driver.findElement(By.id(this.trackId));
				return(elm.getText());
				
			}catch (NoSuchElementException e){
				 WmLog.printMessage("####### FAILED to retrieve value with Id="+this.trackId +" :"+e.getMessage());
					
			}catch (Exception e){
				   WmLog.printMessage("####### FAILED to retrieve value with Id="+trackId +" :"+e.getMessage());
				
			 }
		}
		return "";
	}
	
	public String retrieveOutput( String tracker){
		if(!this.isEmptyValue(tracker)){
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try{
				this.driver.manage().timeouts().implicitlyWait(timeout+10000, TimeUnit.MILLISECONDS);
				WebElement elm=this.driver.findElement(By.id(tracker));
				return(elm.getText());
			}catch (NoSuchElementException e){
				 WmLog.printMessage("####### FAILED to retrieve value with Id="+tracker +" :"+e.getMessage());
					
			}catch (Exception e){
				   WmLog.printMessage("####### FAILED to retrieve value with Id="+tracker +" :"+e.getMessage());
				
			 }
		}
		return "";
	}
	
	public boolean isEmptyValue(String val){
		if(val==null|| val.isEmpty()){
			return true;
		}
		return false;
	}
	
	public boolean is64bit(){
		boolean is64bit = false;
    	if (System.getProperty("os.name").contains("Windows")) {
    	    is64bit = (System.getenv("ProgramFiles(x86)") != null);
    	} else {
    	    is64bit = (System.getProperty("os.arch").indexOf("64") != -1);
    	}
    	return is64bit;
	}
	
	public HashMap<String, String> getDatasetExt(String datasetext){
		HashMap<String, String> data=new HashMap<String, String>();
	    	if(datasetext!=null &&!datasetext.isEmpty()){
 			  String[] overrides=datasetext.split(";");
 			 for(String override:overrides){
 				 if(override.contains("=")){
 					 String[] vals=override.split("=");
 					 data.put(vals[0], vals[1]);
 				 }
 			 }
 		  }
	    	return data;
	}
	
	public void afterMethod() {
        if (driver != null) {
            LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
            for (LogEntry eachEntry : logEntries.getAll()){
                System.out.println(eachEntry.toString());
            }
           
        }
    }

}

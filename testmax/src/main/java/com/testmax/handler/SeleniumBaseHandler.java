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
package com.testmax.handler;


import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.dom4j.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.testmax.Exception.TestMaxException;
import com.testmax.framework.ConfigLoader;
import com.testmax.framework.WmLog;
import com.testmax.runner.TestEngine;
import com.testmax.util.ClassLoaderUtil;
import com.testmax.util.ExcelSheet;
import com.testmax.util.FileDownLoader;
import com.testmax.util.FileUtility;
import com.testmax.util.ItemUtility;
import com.testmax.util.PrintTime;

public abstract class SeleniumBaseHandler extends TestCase{
	
	protected volatile HashMap<String,String> varmap=null;
	protected BaseHandler base=null;
	protected String threadIndex="1";
	protected boolean isMultiThreaded=false;	
	protected volatile WebDriver driver;
	protected  HashMap<String,WebDriver> driverList=new  HashMap<String,WebDriver>();
	protected volatile int taglibCounter;
	protected volatile boolean isByLocated=false;
	protected String baseUrl;
	protected String sendkeyValue="";		
	protected StringBuffer verificationErrors = new StringBuffer();
    protected final String chrome_driver_path=ConfigLoader.getWmRoot()+"/lib/chromedriver.exe";	    	
    protected final String ie_driver_path32bit=ConfigLoader.getWmRoot()+"/lib/IEDriverServer_32.exe";
    protected final String ie_driver_path64bit=ConfigLoader.getWmRoot().replace("\\.", "")+"/lib/IEDriverServer_64.exe";  
    final String ie_dialog=ConfigLoader.getWmRoot()+"/lib/Save_Dialog_IE.exe";
	final String fe_dialog=ConfigLoader.getWmRoot()+"/lib/Save_Dialog_FF.exe";
	final String logpath=ConfigLoader.getWmOutputWebServicePath()+File.separator+"logs"+File.separator+this.base.getActionName().replaceAll(" ", "_")+File.separator;
	protected volatile ArrayList<String> libs=null;
	BufferedWriter logfile=null;
	BufferedWriter commandlogfile=null;
	protected boolean isLogClosed=false;
	protected String v_driver="firefox";
	protected boolean isIE=false;
	
    protected int maxTimeToWait=new Integer(ConfigLoader.getConfig("SELENIUM_COMMAND_WAIT"));
    protected String trackId=null;
    protected ItemUtility utl= new ItemUtility();
    protected boolean isConditionalElement=false;
    protected boolean isBrowserOpen=false;
    protected boolean isWhileLoop=false;   
    protected PrintTime timer=null;
    protected String verifyPageLoadMethods=" click"; // executeOperation executeScript click";
    protected String bypassDisplayMethods=" asserttext mousemove sleep";
    protected String bypassCustomMethods="closewindow executeOperation executeScript get urlextract sleep";
    protected String customOperation=" acceptalert selectcaption read runscript";
    //control varaibles
    
    protected Element targetTag=null;
    protected String taglib="";
    protected String injectorId="";
    protected String injectorName="";
    protected String switchName="";	    
    protected String caseName="";
    protected String tagName="";
    protected String injectorDesc="";
    protected String switchDesc="";
    protected String caseDesc="";
    protected String whileDesc="";
    protected String ifDesc="";
    protected long timeTaken=0;
    protected String controlIdentifier="";
    protected String configTag="";
    protected String callrelate="";
    protected String description="";
    protected String tagdesc="";
    protected String id="";
    protected String name="";
    protected String iframe="";
    protected boolean isIframe=false;
    protected String cssSelector="";
    protected String xpath="";
    protected String className="";
    protected String linkText="";	   
    protected String partialLinkText="";	   
    protected String type="";
    protected String stripchar="";
    protected String method="";
    protected String url="";
    protected String timeout="";
    protected String operation="";
    protected String window="";
    protected String value="";
    protected String msg="";
    
    //use having tags (can be comma seperated list) along with closest Id, Name, cssLocator or xpath of the target elements
    protected String havingTag="";
    protected String havingText="";
    protected String havingAlt="";
    protected String havingImage="";
    protected String havingLinkText="";
    protected String havingName="";
    protected String havingClassName="";
    protected String havingOnClick="";
    protected String havingHref="";
    protected String havingAttribute="";
    
    //default settings
    final String default_timeout="1000";
    final String default_method="executeOperation";
    
    //add secondary prop
    protected HashMap<String,String> primary=null;
    protected HashMap<String,String> secondary=null;
    
    //mobile
    protected String uiAutomator="";
    protected String accessibility="";
    
    //mobile commands
    /*String RESET = "reset";
    String GET_STRINGS = "getStrings";
    String KEY_EVENT = "keyEvent";
    String CURRENT_ACTIVITY = "currentActivity";
    String SET_VALUE = "setValue";
    String PULL_FILE = "pullFile";
    String PUSH_FILE = "pushFile";
    String PULL_FOLDER = "pullFolder";
    String HIDE_KEYBOARD = "hideKeyboard";
    String RUN_APP_IN_BACKGROUND = "runAppInBackground";
    String PERFORM_TOUCH_ACTION = "performTouchAction";
    String PERFORM_MULTI_TOUCH = "performMultiTouch";
    String IS_APP_INSTALLED = "isAppInstalled";
    String INSTALL_APP = "installApp";
    String REMOVE_APP = "removeApp";
    String LAUNCH_APP = "launchApp";
    String CLOSE_APP = "closeApp";
    String END_TEST_COVERAGE = "endTestCoverage";
    String LOCK = "lock";
    String IS_LOCKED = "isLocked";
    String SHAKE = "shake";
    String COMPLEX_FIND = "complexFind";
    String OPEN_NOTIFICATIONS = "openNotifications";
    String GET_NETWORK_CONNECTION = "getNetworkConnection";
    String SET_NETWORK_CONNECTION = "setNetworkConnection";
    String GET_SETTINGS = "getSettings";
    String SET_SETTINGS = "setSettings";
    String START_ACTIVITY = "startActivity";
    String TOGGLE_LOCATION_SERVICES = "toggleLocationServices";
    */
    
	public static void main(String args[]) {
		junit.textui.TestRunner.run(SeleniumBaseHandler.class);
	}
	
	public void setBaseHandler(BaseHandler base){
		this.base=base;
	}
	
	public BaseHandler getBaseHandler(){
		return(this.base);
	}
	public String getDeclaredVariable(String key){
		return(this.varmap.get(key));
	}
	public void addTestResult(String key,String value){
		BaseHandler.addTestResult(base.getHandlerId(),"junit:"+key, value);
	}
	public String getResultVariable(String key){
		return(BaseHandler.getTestResult(base.getHandlerId(), key));
	}
	public String getThreadIndex(){
		return(this.threadIndex);
	}
	public HashMap<String,String> getVarMap() {
		// TODO Auto-generated method stub
		return this.varmap;
	}
	@Before
	public synchronized void setUp() throws Exception {
		//set default driver
			
			if(this.isMultiThreaded){				
				this.libs=null;
				this.createLogFile();
				this.varmap=BaseHandler.threadData.get(threadIndex);
				if(this.libs==null ||this.libs.isEmpty()){
					this.libs=this.parseTagLib();
				}
			 	
			}else{
				this.libs=this.parseTagLib();
				this.varmap=BaseHandler.getVarMap();
				this.threadIndex=this.varmap.get("datasetIndex");
			}
			
			this.printMessage("####### SELENIUM TEST STARTED #################");
			
			this.printMessage("####### Dataset:"+ this.varmap.values());
			
		    this.timer= new PrintTime();
			File file= null;  
		  //driver = new FirefoxDriver();
			
		    v_driver=getDeclaredVariable("driver");		    	
		    if(v_driver==null||v_driver.isEmpty()){
		    	v_driver=ConfigLoader.getConfig("SELENIUM_DRIVER");
		    	if(v_driver==null||v_driver.isEmpty()){
		    		v_driver="firefox";
		    	}
		    }
		    String driver_path=getDeclaredVariable("driver_path");
		    if(v_driver.equalsIgnoreCase("chrome")){
		    	if(TestEngine.suite!=null){
		    		String chrome_path=TestEngine.suite.getWorkspace()+TestEngine.suite.getJobname()+"/lib/chromedriver.exe";
		    		file = new File(chrome_path);
		    		if(!file.exists()){
		    			file = new File(TestEngine.suite.getWorkspace()+"/lib/chromedriver.exe");
		    		}
		    	}else{
		    		file = new File(driver_path!=null &&!driver_path.isEmpty()?driver_path:chrome_driver_path);
		    	}
		    	this.printMessage("Chrome Driver Path="+file.getAbsolutePath());
		    	System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
		    	DesiredCapabilities capability = DesiredCapabilities.chrome();
		    	//capability.setCapability("chrome.switches", Arrays.asList("--allow-running-insecure-content=true"));
		    	if(System.getProperty("os.name").toLowerCase().contains("mac")){
		    		file = new File(ConfigLoader.getWmRoot()+"/lib/chromedriver_mac");
			    	System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
					capability.setCapability("platform", Platform.ANY);
					capability.setCapability("binary", "/Application/chrome"); //for linux "chrome.switches", "--verbose"
					capability.setCapability("chrome.switches", "--verbose");
					driver = new ChromeDriver(capability);
		    		
		    	}else{
		    		driver= new ChromeDriver(capability);
		    	}
		    }else if (v_driver.equalsIgnoreCase("ie")){
		    	if(is64bit()){
		    		file = new File(driver_path!=null &&!driver_path.isEmpty()?driver_path:ie_driver_path64bit);
		    	}else{
		    		file = new File(driver_path!=null &&!driver_path.isEmpty()?driver_path:ie_driver_path32bit);
		    	}
		    	this.printMessage("##### IE DRIVER PATH="+file.getAbsolutePath());
		    	System.setProperty("webdriver.ie.driver", file.getAbsolutePath());		    	
		    	DesiredCapabilities capability = DesiredCapabilities.internetExplorer();
		    	capability.setCapability("acceptSslCerts", true);
		    	capability.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
		    	//capability.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
		    	driver=new InternetExplorerDriver(capability);
		    	maxTimeToWait=maxTimeToWait*5;
		    	this.isIE=true;
		    	
		    }else if(v_driver.equalsIgnoreCase("firefox")){
		    	
	    	   /*file = new File("firebug-1.8.1.xpi");
	    	   FirefoxProfile firefoxProfile = new FirefoxProfile();
		       firefoxProfile.setPreference("security.mixed_content.block_active_content", false);
		       firefoxProfile.setPreference("security.mixed_content.block_display_content", true);
		       firefoxProfile.setPreference("browser.cache.disk.enable", true);
	    	   firefoxProfile.addExtension(file);
	    	   firefoxProfile.setPreference("extensions.firebug.currentVersion", "1.8.1"); // Avoid startup screen
	    	   driver = new FirefoxDriver(firefoxProfile);
	    	   */
		    	if(System.getProperty("os.name").toLowerCase().contains("mac")){
		    		DesiredCapabilities capability = DesiredCapabilities.firefox();
					capability.setCapability("platform", Platform.ANY);
					capability.setCapability("binary", "/Application/firefox"); //for linux
					//capability.setCapability("binary", "/ms/dist/fsf/PROJ/firefox/16.0.0/bin/firefox"); //for linux
	
					//capability.setCapability("binary", "C:\\Program Files\\Mozilla  Firefox\\msfirefox.exe"); //for windows                
					driver = new FirefoxDriver(capability);
		    		
		    	}else{
		    	//
		    		driver = new FirefoxDriver();
		    	}
		    }else if(v_driver.equalsIgnoreCase("safari")){
		    	  
			    	if(System.getProperty("os.name").toLowerCase().contains("mac")){
			    		this.printMessage("#####STARTING Safri in Mac ####");
			    		DesiredCapabilities capability = DesiredCapabilities.safari();
						capability.setCapability("platform", Platform.ANY);
						capability.setCapability("binary", "/Application/safari"); //for linux
						driver = new SafariDriver(capability);
			    		
			    	}else{
			    		// Read Instruction for Safari Extension
			    		//http://rationaleemotions.wordpress.com/2012/05/25/working-with-safari-driver/
			    		// Get certificate from https://docs.google.com/folder/d/0B5KGduKl6s6-ZGpPZlA0Rm03Nms/edit
			    		this.printMessage("#####STARTING Safri in Windows ####");
			    		String safari_install_path="C:\\Program Files (x86)\\Safari\\";
			    		DesiredCapabilities capability = DesiredCapabilities.safari();
						capability.setCapability("platform", Platform.ANY);
						capability.setCapability("binary", safari_install_path+"Safari.exe"); //for windows 
						//capability.setCapability(SafariDriver.DATA_DIR_CAPABILITY, "C:\\Program Files (x86)\\Safari\\SafariData");
						//System.setProperty("webdriver.safari.driver", safari_install_path+"SafariDriver.safariextension\\");
			    		driver = new SafariDriver(capability);
			    		
			    		
			    	}	
		    }else if (v_driver.equalsIgnoreCase("htmlunit")){
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
						   
			baseUrl = ConfigLoader.getConfig("BASE_APPLICATION_URL");
			baseUrl=baseUrl.replace("[env]", ConfigLoader.getConfig("QA_TEST_ENV"));
			for (Cookie cookie:driver.manage().getCookies() ){
				printMessage("name="+cookie.getName());
				printMessage("domain="+cookie.getDomain());
				printMessage("path="+cookie.getPath());
				printMessage("value="+cookie.getValue());
			}
			java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			driver.manage().window().setSize(new Dimension(1020,(int)screenSize.getHeight()));
			String removecookie=getDeclaredVariable("removecookie");
			if(this.isEmptyValue(removecookie)||removecookie.equalsIgnoreCase("yes")){
				driver.manage().deleteAllCookies();
			}
			driver.manage().timeouts().implicitlyWait(maxTimeToWait, TimeUnit.SECONDS);
			driverList.put(this.threadIndex, driver);
			
		
	}
	
	private boolean is64bit(){
		boolean is64bit = false;
    	if (System.getProperty("os.name").contains("Windows")) {
    	    is64bit = (System.getenv("ProgramFiles(x86)") != null);
    	} else {
    	    is64bit = (System.getProperty("os.arch").indexOf("64") != -1);
    	}
    	return is64bit;
	}
	private WebDriver initMacDriver(){
		DesiredCapabilities capability = DesiredCapabilities.firefox();
		capability.setCapability("platform", Platform.ANY);
		capability.setCapability("binary", "/Application/firefox"); //for linux
		//capability.setCapability("binary", "/ms/dist/fsf/PROJ/firefox/16.0.0/bin/firefox"); //for linux

		//capability.setCapability("binary", "C:\\Program Files\\Mozilla  Firefox\\msfirefox.exe"); //for windows                
		WebDriver    currentDriver = new FirefoxDriver(capability);
		return currentDriver;
	}
	public void runMultiThreadApplication() throws TestMaxException{
		 
			   //try{
				boolean once=false;
				
				for(Object lib:libs){
					//ConfigLoader.printMatchedTagLibKey(lib.toString().trim().toLowerCase());
					//Element tagLib=null;
						 if(!once){
							printMessage("********"+this.threadIndex+" : EXECUTING TOTAL TAGLIBS ="+ libs.size() +"*********************");
							printMessage("********"+this.threadIndex+" : LIST OF TAGLIBS ="+ libs.toString() +"*********************");
							printMessage("********"+this.threadIndex+" : CURRENT TAGLIB TO EXECUTE ="+ lib.toString() +"*********************");
							
							//print commands
							printCommand("********"+this.threadIndex+" : EXECUTING TOTAL TAGLIBS ="+ libs.size() +"*********************");
							printCommand("********"+this.threadIndex+" : LIST OF TAGLIBS ="+ libs.toString() +"*********************");
							printCommand("********"+this.threadIndex+" : CURRENT TAGLIB TO EXECUTE ="+ lib.toString() +"*********************");
							printCommand("");
							once=true;
						 }	
						Element tagLib=ConfigLoader.getTagLibByKey(lib.toString().trim());						
						for(Object varName:varmap.keySet()){
							if(tagLib!=null){
								String value=getDeclaredVariable(varName.toString());
								tagLib=utl.replaceVariable(tagLib, varName.toString(), value);
							}
								
						}
						/*synchronized(this){
							String val=this.varmap.get(this.threadIndex);
							this.taglibCounter=Integer.valueOf(val!=null?val:"1");
							
							if(this.taglibCounter>libs.size()){								
								throw new TestMaxException("********"+this.threadIndex+" :END EXECUTING ALL TAGLIBS *********************");
							}
						}*/
							if(tagLib!=null &&this.driver!=null ){
								printMessage(" ");
								printMessage("********"+this.threadIndex+" : STARTED EXECUTING TAGLIB ="+ lib.toString() +"*********************");
								printCommand(" ");
								printCommand("********"+this.threadIndex+" : STARTED EXECUTING TAGLIB ="+ lib.toString() +"*********************");
								printCommand(" ");
								synchronized(this){
									this.taglibCounter++;
									this.varmap.put(this.threadIndex, String.valueOf(this.taglibCounter));
								}
								this.executeTagLib(tagLib);
								printMessage("********"+this.threadIndex+" :END EXECUTING TAGLIB ="+ lib.toString() +"*********************");
								printMessage(" ");
								
								printCommand(" ");
								printCommand("********"+this.threadIndex+" :END EXECUTING TAGLIB ="+ lib.toString() +"*********************");
								printCommand(" ");
															
							}
					}
				
				
					printMessage("********"+this.threadIndex+" :END EXECUTING ALL TAGLIBS *********************");
					printMessage(" ");
					
					printCommand(" ");
					printCommand("********"+this.threadIndex+" :END EXECUTING ALL TAGLIBS *********************");
					printCommand(" ");
				
				/*}catch(Exception e){
					printMessage("********ERROR IN RUNNING runMultiThreadApplication()"+ e.getMessage());
					return;
				}
				*/
			return;
			
		}
	
   @Test
	public void testApplication() throws Exception {
	   if(!this.isMultiThreaded){
		try{
			ArrayList<String> libs=this.parseTagLib();
			for(Object lib:libs){
				//ConfigLoader.printMatchedTagLibKey(lib.toString().trim().toLowerCase());
				//Element tagLib=null;
				Element tagLib=ConfigLoader.getTagLibByKey(lib.toString().trim());
				for(Object varName:varmap.keySet()){
					String value=getDeclaredVariable(varName.toString());
					tagLib=utl.replaceVariable(tagLib, varName.toString(), value);
					
						
				}
				if(tagLib!=null){
					this.printMessage(" ");
					this.printMessage("********STARTED EXECUTING TAGLIB ="+ lib.toString() +"*********************");
					this.printCommand(" ");
					this.printCommand("********STARTED EXECUTING TAGLIB ="+ lib.toString() +"*********************");
					this.printCommand(" ");
					this.executeTagLib(tagLib);
					this.printMessage("********END EXECUTING TAGLIB ="+ lib.toString() +"*********************");
					this.printMessage(" ");
					this.printCommand(" ");
					this.printCommand("********END EXECUTING TAGLIB ="+ lib.toString() +"*********************");
					this.printCommand(" ");
				}
			}
			
				this.printMessage("********END EXECUTING ALL TAGLIBS *********************");
				this.printMessage(" ");
				this.printCommand(" ");
				this.printCommand("********END EXECUTING ALL TAGLIBS *********************");
				this.printCommand(" ");
			}catch(Exception e){
				this.printMessage("********ERROR IN RUNNING testApplication()"+ e.getMessage());
				this.printCommand(" ");
				this.printCommand("********ERROR IN RUNNING testApplication()"+ e.getMessage());
				this.printCommand(" ");
			}
	   }
			return;
	}

	
	@After
	public  void tearDown() throws Exception {
		try{ 
			this.getScreenShot();
			if(!isLogClosed){
				printMessage("********ENDING DATASET= "+ this.threadIndex+" ********************");	
					
				Thread.sleep(3000);				
				printMessage("********QUITING SELENIUM TEST DRIVER********************");				
				driver.quit();				
				String verificationErrorString = verificationErrors.toString();
				if (!"".equals(verificationErrorString)) {
					fail(verificationErrorString);
				}
				printMessage("********COMPLTED SELENIUM TEST ********************");	
				printMessage("********CLOSING LOGFILE********************");	
				this.closeLogFile();
				isLogClosed=true;
				this.driver=null;
			}else{
				printMessage("********QUITING SELENIUM TEST DRIVER********************");
				if(driver!=null)
					driver.quit();
				this.driver=null;
				
				
			}
			
		}catch(Exception e){
			printMessage("********QUITING SELENIUM TEST DRIVER********************");
			if(driver!=null)
				driver.quit();
			this.driver=null;
			printMessage("********CLOSING LOGFILE********************");			
			printMessage("********COMPLTED SELENIUM TEST WITH TEARDOWN EXCEPTION ********************");
			if(!isLogClosed){
				this.closeLogFile();
			}
			System.out.println("#############ERROR calling TearDown"+e.getMessage());
		}
		
	}
	
	public  void cleanUpThread()  {
		try{
			if(this.v_driver!=null &&!this.v_driver.equalsIgnoreCase("htmlunit")){
				this.getScreenShot();
			}
			if(!isLogClosed){
				printMessage("********ENDING DATASET= "+ this.threadIndex+" ********************");						
				//Thread.sleep(3000);				
				printMessage("********QUITING SELENIUM TEST DRIVER********************");
				driver.quit();
				Thread.sleep(3000);	
				String verificationErrorString = verificationErrors.toString();
				if (!"".equals(verificationErrorString)) {
					fail(verificationErrorString);
				}
				printMessage("********COMPLTED SELENIUM TEST ********************");	
				printMessage("********CLOSING LOGFILE********************");	
				this.closeLogFile();
				isLogClosed=true;
				this.driver=null;
			}else{
				printMessage("********QUITING SELENIUM TEST DRIVER********************");		
				driver.quit();
				this.driver=null;
				
				
			}
			
		}catch(Exception e){
				
			printMessage("********CLOSING LOGFILE********************");			
			printMessage("********COMPLTED SELENIUM TEST WITH TEARDOWN EXCEPTION ********************");
			if(!isLogClosed){
				printMessage("********EXCEPTION OCCURED :"+e.getMessage());	
				printMessage("********QUITING SELENIUM TEST DRIVER********************");	
				this.closeLogFile();
			}
				
			//driver.quit();
			this.driver=null;
			System.out.println("#############ERROR calling TearDown"+e.getMessage());
		}
		
	}
	protected void createLogFile(){
		
		if(this.isMultiThreaded){
			try {
				 FileUtility.createDir(this.logpath);
				String path=this.logpath+this.threadIndex+"_"+this.getBaseHandler().getHandlerName()+"_"
						+ BaseHandler.getActionName().replace(" ", "_")+".log";
				File file = new File(path); 
				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				} 
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				this.logfile = new BufferedWriter(fw);
	 
			} catch (IOException e) {
				e.printStackTrace();
			}
			//add command file
			try {
				String commandpath=this.logpath+"command"+File.separator+this.threadIndex+File.separator;
				FileUtility.createDir(commandpath);
				String commandfilename=commandpath+
						this.base.getHandlerName()+"_"+this.base.getActionName().replaceAll(" ", "_")+".log";
						//this.getBaseHandler().getHandlerName()+"_command_"+ BaseHandler.getActionName().replace(" ", "_")
				//+"_"+this.threadIndex+"_"+System.currentTimeMillis()+".log";
				File commandfile = new File(commandfilename); 
				// if file doesnt exists, then create it
				if (!commandfile.exists()) {
					commandfile.createNewFile();
				} 
				FileWriter commandfw = new FileWriter(commandfile.getAbsoluteFile());
				this.commandlogfile = new BufferedWriter(commandfw);
	 
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		}
	}
	
	private void closeLogFile(){
		
		if(this.isMultiThreaded){
			try {
				this.logfile.flush();
				this.logfile.close();
				this.commandlogfile.flush();
				this.commandlogfile.close();
	 
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		}
	}
	protected  void executeTagLib(Element tagLib) throws TestMaxException{		
		this.taglib=tagLib.getName();
		this.callrelate=tagLib.attributeValue("callrelate");;
		this.description=tagLib.attributeValue("description");
		
	
		try{
			for (Object eachtag: tagLib.elements()){
				Element curTag=(Element) eachtag;
				//printMessage(curTag.asXML());
				this.setupTag(curTag);				
				
				this.value=curTag.getText();
				
				this.msg=" TAGLIB="+taglib;
				/*if(this.url==null &&this.method!=null &&this.method.equalsIgnoreCase("extract")){
					String[] currenturl=this.driver.getCurrentUrl().split("/");
					if(currenturl.length>1){
						String itemId=this.driver.getCurrentUrl().replaceAll(this.stripchar, "");
						
						this.addTestExtract(this.value, itemId);
					
						printMessage("#### EXTRACTED ITEM ID FROM URL="+itemId +" TARGET URL="+this.driver.getCurrentUrl());
					}
					continue;
					
				}
				*/
					
				if(url!=null &&!url.isEmpty()){
					String wwwserver=getDeclaredVariable("wwwserver");

					if(wwwserver!=null &&!wwwserver.isEmpty() && (wwwserver.contains("http:")||wwwserver.contains("https:"))){
						url=wwwserver;					
					}else if(!url.contains("http") &&!url.contains("file:")){
						url=this.baseUrl+url;
					}
					url=url.replace("[env]", ConfigLoader.getConfig("QA_TEST_ENV"));
					
					if(this.baseUrl.contains("https")){
						url=url.replace("http:", "https:");
					}
					this.printCommand(this.tagdesc +" for control with URL="+url );
					driver.get(url);
					this.printMessage("URL opened="+url);
					Thread.sleep(3000);
					
				}else{
					for(Object varName:varmap.keySet()){
						String value=getDeclaredVariable(varName.toString());
						curTag=utl.replaceVariable(curTag, varName.toString(), value);
							
					}
						
				    if(curTag.getName()!=null &&curTag.getName().equalsIgnoreCase("injector")){
				    	this.injectorId=this.id;
						Element injector=ConfigLoader.getTagLibByKey(this.id);
						if(injector!=null){
							for(Object varName:varmap.keySet()){
								String value=getDeclaredVariable(varName.toString());
								injector=utl.replaceVariable(injector, varName.toString(), value);
									
							}
							
							if(verifyInjectorConfigured(this.injectorId)){
								printMessage(" ");
								this.printCommand(" ");
								this.printCommand("********STARTED "+ timer.getPrintTime()+" INJECTOR ="+ this.injectorId +"*********************");
								this.printCommand(" ");
								printMessage("********STARTED "+ timer.getPrintTime()+" INJECTOR ="+ this.injectorId +"*********************");
								handleInjector(injector);
								this.printCommand(" ");
								this.printCommand("********END "+ timer.getPrintTime()+" INJECTOR ="+ this.injectorId +"*********************");
								this.printCommand(" ");
								printMessage("********END "+ timer.getPrintTime()+" INJECTOR ="+ this.injectorId +"*********************");								
								printMessage(" ");
							}
						}else{
							String restricted=curTag.attributeValue("restricted");
							boolean executeJava= false;
							if(restricted!=null &&!restricted.isEmpty() &&restricted.equalsIgnoreCase("yes") && verifyInjectorConfigured(this.injectorId)){
								executeJava=true;								
							}else if(restricted==null ||restricted.isEmpty()||restricted.equalsIgnoreCase("no")){
								executeJava=true;								
							}
							if(executeJava){
								this.printCommand(" ");
								this.printCommand("********STARTED "+ timer.getPrintTime()+" JAVA INJECTOR ="+ this.injectorId +"*********************");
								this.printCommand(" ");
								this.printMessage(" ");
								printMessage("********STARTED "+ timer.getPrintTime()+" JAVA INJECTOR ="+ this.injectorId +"*********************");
								synchronized (this){
									ClassLoaderUtil.invoke(this, this.id);
									this.waitToPageLoad(3000);
								}
								printCommand(" ");
								printCommand("********END "+ timer.getPrintTime()+" JAVA INJECTOR ="+ this.injectorId +"*********************");								
								printCommand(" ");
								printMessage("********END "+ timer.getPrintTime()+" JAVA INJECTOR ="+ this.injectorId +"*********************");								
								printMessage(" ");
							}else{
								printMessage(" ");
								printMessage("********BYPASSED "+ timer.getPrintTime()+" JAVA INJECTOR ="+ this.injectorId +" for restriction *********************");
							}
						}
						this.injectorId="";
					
				    }else if(curTag.getName()!=null &&curTag.getName().contains("if")){
				    	
				    	printMessage(" ");
						printMessage("********STARTED "+ timer.getPrintTime()+" EXECUTING IF within Taglib=="+ this.taglib +"*********************");
						handleIf(curTag);								
						printMessage("********END "+ timer.getPrintTime()+" EXECUTING IF within TagLib="+ this.taglib +"*********************");								
						printMessage(" ");
					

				    }else if(curTag.getName()!=null &&curTag.getName().equalsIgnoreCase("switch")){
				    	
				    	printMessage(" ");
						printMessage("********STARTED "+ timer.getPrintTime()+" EXECUTING SWITCH within Taglib=="+ this.taglib +"*********************");
						handleSwitch(curTag);								
						printMessage("********END "+ timer.getPrintTime()+" EXECUTING SWITCH within TagLib="+ this.taglib +"*********************");								
						printMessage(" ");
						
				    }else if(curTag.getName()!=null &&curTag.getName().equalsIgnoreCase("while")){
				    	
				    	printMessage(" ");
						printMessage("********STARTED "+ timer.getPrintTime()+" EXECUTING WHILE LOOP within Taglib=="+ this.taglib +"*********************");
						handleWhile(curTag);								
						printMessage("********END "+ timer.getPrintTime()+" EXECUTING WHILE LOOP within TagLib="+ this.taglib +"*********************");								
						printMessage(" ");
						
					}else if(curTag.getName()!=null &&curTag.getName().equalsIgnoreCase("tag")){						
					
						if(!value.isEmpty()){
							sendkeyValue=value;
						}
						this.execute();
						
					}
					
				}
			}
			
		}catch (Exception e){				
			// TODO Auto-generated catch block
			 msg=">>Dataset:"+this.threadIndex+e.getMessage() ;
			 e.printStackTrace();
			 throw new TestMaxException(msg);
			 //handleTimeOut(msg);
			
		}
	}
	
	
	protected void handleInjector(Element injector) throws TestMaxException{
		
		this.injectorName=injector.getName();			
		this.injectorDesc=injector.attributeValue("description");
		this.msg="TAGLIB="+taglib+" Injector="+this.injectorId;
		//this.setupTag(injector);
			try{
				
				for (Object eachtag: injector.elements()){
					Element curTag=(Element) eachtag;
					
					if(curTag.getName().equalsIgnoreCase("switch")){						
						this.handleSwitch(curTag);
					}else if(curTag.getName().equalsIgnoreCase("while")){						
						this.handleWhile(curTag);
					}else if(curTag.getName().contains("if")){						
						this.handleIf(curTag);
					}else if(curTag.getName().equalsIgnoreCase("tag")){							
						setupTag(curTag);
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						this.execute();
						
					}else if(curTag.getName()!=null &&curTag.getName().equalsIgnoreCase("injector")){
						String origininjector=this.injectorId;
						String origindesc=this.injectorDesc;
						setupTag(curTag);
				    	this.injectorId=this.id;
						Element anotherinjector=ConfigLoader.getTagLibByKey(this.id);
						if(anotherinjector!=null){
							for(Object varName:varmap.keySet()){
								String value=getDeclaredVariable(varName.toString());
								anotherinjector=utl.replaceVariable(anotherinjector, varName.toString(), value);
									
							}
							
							//if(verifyInjectorConfigured(this.injectorId)){
								printMessage(" ");
								printMessage("********STARTED INJECTOR ="+ this.injectorId +"*********************");
								handleInjector(anotherinjector);								
								printMessage("********END INJECTOR ="+ this.injectorId +"*********************");								
								printMessage(" ");
							//}
						}else{
							ClassLoaderUtil.invoke(this, this.id);
						}
						this.injectorId=origininjector;
						this.injectorDesc=origindesc;
				 }
				
					
					
				}
			
		}catch (Exception e){				
			// TODO Auto-generated catch block
			 msg=">>Dataset:"+this.threadIndex+" FAILED TO RUN INJECTOR >> "+this.injectorName+ ">> Description="+this.injectorDesc +" "+e.getMessage() ;
			 e.printStackTrace();
			 throw new TestMaxException(msg);
			 //handleTimeOut(msg);
			
		}
	}
	
	protected  void handleSwitch(Element switchElm) throws TestMaxException{
		
		this.switchName=switchElm.attributeValue("name");			
		this.switchDesc=switchElm.attributeValue("description");
		this.msg="TAGLIB="+taglib+">>INJECTOR="+this.injectorId+">>SWITCH="+this.switchName;
		List<WebElement> controls=null;
		boolean caseStatus=false;
		//this.setupTag(switchElm);
			try{
				//set driver to return faster if the webcontrol is null
				//control=this.getWebControl();
				
				for (Object eachtag: switchElm.elements()){
					Element curTag=(Element) eachtag;
					
					//validate <switch-element> tag to find out case condition before entering to the case
					//if you need to validate any text within the switch element as condition you can pass like
					//<switch-element>Text value to verify within control </sitch-element> 
					if(curTag.getName().equalsIgnoreCase("switch-element")){
						this.isConditionalElement=true;
						setupTag(curTag);
						value=value.trim();	
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						if(value.isEmpty()){
							caseStatus=this.isElementPresent(this.By());
						}else{
							try{
								controls=this.getWebElements();	
							}catch( TestMaxException t){
								this.printMessage(t.getMessage());
								t.printStackTrace();
							}
						}
						//this.waitToPageLoad(new Integer(timeout));
					}
					this.isConditionalElement=false;
					if(curTag.getName().equalsIgnoreCase("case") ){
						if(controls!=null &&controls.size()>0){
							for(WebElement control:controls){
								this.setImpilicitTimeInMiliSec(new Integer(timeout));
								if(control!=null && (control.getText()==null &&!value.trim().isEmpty() 
										||control.getText().isEmpty() &&!value.trim().isEmpty()||!control.getText().trim().contains(value.trim()))){
									if(this.handleCase(curTag, "false")){
										controls=null;
									}
									break;
								}else if(control!=null && (control.getText()==null &&value.trim().isEmpty() 
										||control.getText().isEmpty() &&value.trim().isEmpty()||control.getText().trim().contains(value.trim()))){
									if(this.handleCase(curTag, "true")){
										controls=null;
									}
									break;
								}else if(control!=null && (control.getText()!=null &&!control.getText().isEmpty() &&control.getText().equalsIgnoreCase(value.trim()))){
									if(this.handleCase(curTag, value)){
										controls=null;
									}
									break;
								}
							}
						}else if(caseStatus &&value.isEmpty()){
							this.handleCase(curTag, "true");
						
						}else if(!caseStatus &&value.isEmpty()){
							this.handleCase(curTag, "false");
						}else{
							this.handleCase(curTag, "false");
						}
					
					}
					
					//Execute any tags if exist outside of case statement after handling case
					if(curTag.getName().equalsIgnoreCase("tag")){							
						setupTag(curTag);
						value=value.trim();	
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						this.execute();
						//this.executeMethod(control, method, timeout,msg);
					}
					
					if(curTag.getName()!=null &&curTag.getName().equalsIgnoreCase("injector")){
						setupTag(curTag);
				    	this.injectorId=this.id;
						Element injector=ConfigLoader.getTagLibByKey(this.id);
						if(injector!=null){
							for(Object varName:varmap.keySet()){
								String value=getDeclaredVariable(varName.toString());
								injector=utl.replaceVariable(injector, varName.toString(), value);
									
							}
							
							//if(verifyInjectorConfigured(this.injectorId)){
								printMessage(" ");
								printMessage("********STARTED INJECTOR ="+ this.injectorId +"*********************");
								handleInjector(injector);								
								printMessage("********END INJECTOR ="+ this.injectorId +"*********************");								
								printMessage(" ");
							//}
						}else{
							ClassLoaderUtil.invoke(this, this.id);
						}
						this.injectorId="";
				 }
				
					
				}
			
		}catch (Exception e){				
			// TODO Auto-generated catch block
			 msg=">>Dataset:"+this.threadIndex+" FAILED TO RUN SWITCH "+addMessage();
			 e.printStackTrace();
			 throw new TestMaxException(msg);
			 //handleTimeOut(msg);
			
		}
	}
	protected void handleWhile(Element whileElm) throws TestMaxException{
		
		this.whileDesc=whileElm.attributeValue("description");
		String name=whileElm.attributeValue("name");
		Element whileCondition=null;
		List<WebElement> controls=null;
		isWhileLoop=true;
			try{		
				
				while (this.isWhileLoop){
					for (Object eachtag: whileElm.elements()){
						Element curTag=(Element) eachtag;
						
						//validate <while-element> tag to find out condition before entering to the while
						//if you need to validate any text within the while element as condition you can pass like
						//<while-element>Text value to verify within control </while-element> 
						if(curTag.getName().equalsIgnoreCase("while-element")||whileCondition!=null){	
							if(whileCondition==null){
								whileCondition=curTag;
							}
							
							this.isConditionalElement=true;
							setupTag(whileCondition);
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
							value=value.trim();
							try{
								controls=this.getWebElements();
							}catch( TestMaxException t){
								this.printMessage(t.getMessage());
								t.printStackTrace();
							}
							
							
							for(WebElement control:controls){
								if(control!=null &&control.getText()!=null &&!control.getText().isEmpty()){
									isWhileLoop=false;
								}
								if((control!=null &&value.isEmpty() 
										||control!=null &&!value.isEmpty() &&control.getText().trim().contains(value))){
									
									isWhileLoop=true;
									//this.waitToPageLoad(new Integer(timeout));
									break;
								}
								
							}
						}
						this.isConditionalElement=false;
						
						//Execute tags under the if after validating control
						if(curTag.getName().equalsIgnoreCase("tag") &&isWhileLoop ){							
							setupTag(curTag);
							value=value.trim();	
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
							this.execute();						
							
						}
						//Execute tags under the if after validating control
						if(curTag.getName().contains("if") &&isWhileLoop ){							
							setupTag(curTag);
							value=value.trim();						
							this.handleIf(curTag);						
						}
						
					}
				}
			
		}catch (Exception e){				
			// TODO Auto-generated catch block
			 msg=">>Dataset:"+this.threadIndex+" FAILED TO EXECUTE WHILE CONTROL IN "+addMessage();
			 e.printStackTrace();
			 throw new TestMaxException(msg);
			 //handleTimeOut(msg);
			
		}
	}
	
	protected void handleIf(Element ifElm) throws TestMaxException{
		PrintTime timer=new PrintTime();
		this.ifDesc=ifElm.attributeValue("description");
		String name=ifElm.attributeValue("name");
		this.msg="TAGLIB="+taglib+">>INJECTOR="+this.injectorId+">> IF="+name;
		List<WebElement> ifControls=null;
		boolean ifStatus=false;
		boolean isIfNot=ifElm.getName().equalsIgnoreCase("ifnot")?true:false;
		String ifValue="";
			try{
				for (Object eachtag: ifElm.elements()){
					Element curTag=(Element) eachtag;
					
					//validate <if-element> tag to find out case condition before entering to the case
					//if you need to validate any text within the switch element as condition you can pass like
					//<if-element>Text value to verify within control </if-element> 
					if(curTag.getName().equalsIgnoreCase("if-element")){
						this.isConditionalElement=true;
						setupTag(curTag);
						ifValue=value.trim();
						if(!ifValue.isEmpty()){
							try{
								ifControls=this.getWebElements();
							}catch( TestMaxException t){
								this.printMessage(t.getMessage());
								t.printStackTrace();
							}
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
							if(ifControls!=null&& ifControls.size()>0){
								for(WebElement ifControl:ifControls){
									if(isIfNot &&ifControl!=null &&!ifValue.isEmpty() &&!ifControl.getText().toLowerCase().trim().contains(ifValue.toLowerCase())){
										ifStatus=true;
										break;
									}else if((ifControl!=null &&ifValue.isEmpty())
											||ifControl!=null &&!ifValue.isEmpty() &&ifControl.getText().toLowerCase().trim().contains(ifValue.toLowerCase())){
										ifStatus=true;
										break;
										//this.waitToPageLoad(new Integer(timeout));
									}
								}
							}
						}else{
							ifStatus=this.isElementPresent(this.By());
						}
						//this.executeMethod(control, method, timeout,msg);
					}
					this.isConditionalElement=false;	
					
					//Execute tags under the if after validating control
					if(curTag.getName().equalsIgnoreCase("tag") &&ifStatus){							
						setupTag(curTag);
						value=value.trim();
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						this.execute();	
					}else if(curTag.getName().equalsIgnoreCase("if")&&ifStatus){						
						this.handleIf(curTag);
					}else if(curTag.getName().equalsIgnoreCase("switch")&&ifStatus){						
						this.handleSwitch(curTag);
					}else if(curTag.getName().equalsIgnoreCase("while")&&ifStatus){						
						this.handleWhile(curTag);
					}else if(curTag.getName()!=null &&curTag.getName().equalsIgnoreCase("injector") &&ifStatus){
							setupTag(curTag);
					    	this.injectorId=this.id;
							Element injector=ConfigLoader.getTagLibByKey(this.id);
							if(injector!=null){
								for(Object varName:varmap.keySet()){
									String value=getDeclaredVariable(varName.toString());
									injector=utl.replaceVariable(injector, varName.toString(), value);
										
								}
								
								//if(verifyInjectorConfigured(this.injectorId)){
									printMessage(" ");
									printMessage("********STARTED INJECTOR ="+ this.injectorId +"*********************");
									this.setImpilicitTimeInMiliSec(new Integer(timeout));
									handleInjector(injector);								
									printMessage("********END INJECTOR ="+ this.injectorId +"*********************");								
									printMessage(" ");
								//}
							}else{
								ClassLoaderUtil.invoke(this, this.id);
							}							
								this.injectorId="";
						}
					
				}
			
		}catch (Exception e){				
			// TODO Auto-generated catch block
			 msg=">>Dataset:"+this.threadIndex+" FAILED TO EXECUTE IF CONTROL IN "+addMessage();
			 e.printStackTrace();
			 throw new TestMaxException(msg);
			 //handleTimeOut(msg);
			
		}
	}
	protected boolean handleCase(Element caseElm, String value) throws TestMaxException{
		String condition=caseElm.attributeValue("condition");
		this.caseName=caseElm.getName();		
		this.caseDesc=caseElm.attributeValue("description");
		boolean isExecuted=false;
	
		try{
			 if(condition!=null &&condition.equalsIgnoreCase(value)){
				for (Object eachtag: caseElm.elements()){
					Element curTag=(Element) eachtag;
					this.url="";
					setupTag(curTag);
					if(url!=null &&!url.isEmpty()){
						url=this.baseUrl+url;
						String item_id=getDeclaredVariable("itemId");
						if(!this.isEmptyValue(item_id)){
							driver.get(url.replace("@itemId", item_id));
						}else{
							driver.get(url);
						}
						Thread.sleep(1000);
						
					}else{
					
						if(curTag.getName().equalsIgnoreCase("switch")){						
							this.handleSwitch(curTag);
						}else if(curTag.getName().contains("if")){						
							this.handleIf(curTag);
						}else if(curTag.getName().equalsIgnoreCase("tag")){
							this.setupTag(curTag);
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
							this.execute();							
						}else if(curTag.getName().equalsIgnoreCase("while")){						
							this.handleWhile(curTag);	
						}else if(curTag.getName()!=null &&curTag.getName().equalsIgnoreCase("injector")){
					    	this.injectorId=this.id;
							Element injector=ConfigLoader.getTagLibByKey(this.id);
							if(injector!=null){
								for(Object varName:varmap.keySet()){
									String injValue=getDeclaredVariable(varName.toString());
									injector=utl.replaceVariable(injector, varName.toString(), injValue);
								}
								
								//if(verifyInjectorConfigured(this.injectorId)){
									printMessage(" ");
									printMessage("********STARTED INJECTOR ="+ this.injectorId +"*********************");
									handleInjector(injector);								
									printMessage("********END INJECTOR ="+ this.injectorId +"*********************");								
									printMessage(" ");
								//}
								
							}else{
								ClassLoaderUtil.invoke(this, this.id);
							}
							this.injectorId="";
					 }
						
					
					}
					isExecuted=true;
				}
			 }
			
		}catch (Exception e){				
			// TODO Auto-generated catch block
			 msg=">>Dataset:"+this.threadIndex+" FAILED TO EXECUTE CASE IN "+addMessage();
			 e.printStackTrace();
			 throw new TestMaxException(msg);
			 //handleTimeOut(msg);
			
			
		}
		return isExecuted;
	}
	
	protected void setupTag(Element curTag) throws Exception {
		printMessage(curTag.asXML());
		PrintTime timer= new PrintTime();
		this.controlIdentifier="";
		this.targetTag=curTag;
		if(timeout!=null &&!timeout.isEmpty() 
				&&this.configTag!=null &&this.configTag.equalsIgnoreCase("tag")
				&&this.method!=null &&this.isVerifyPageLoadMethod() ){
				//&&this.doWait(curTag.attributeValue("operation"))){ 
			
				this.waitToPageLoad(new Integer(timeout));
		}
		
		primary=new HashMap<String,String>();
		secondary=new HashMap<String,String>();
		
		this.configTag=curTag.getName();		
		this.tagdesc=curTag.attributeValue("description");
		this.type=curTag.attributeValue("type");		
		this.method=curTag.attributeValue("method");
		if(this.isEmptyValue(this.method)){
			this.method=this.default_method;
		}
		this.stripchar=curTag.attributeValue("stripchar");
		this.timeout=curTag.attributeValue("timeout");
		if(this.isEmptyValue(this.timeout)){
			this.timeout=this.default_timeout;
		}
		this.operation=curTag.attributeValue("operation");
		this.window=curTag.attributeValue("window");
		
		this.id=curTag.attributeValue("id");
		this.setPrimaryProperty("id",this.id);
		this.iframe=curTag.attributeValue("iframe");
		this.setPrimaryProperty("iframe",this.iframe);
		this.name=curTag.attributeValue("name");
		this.setPrimaryProperty("name",this.name);
		this.cssSelector=curTag.attributeValue("cssSelector");
		this.setPrimaryProperty("cssSelector",this.cssSelector);
		this.xpath=curTag.attributeValue("xpath");
		this.setPrimaryProperty("xpath",this.xpath);		
		this.className=curTag.attributeValue("classname");
		this.setPrimaryProperty("classname",this.className);
		this.linkText=curTag.attributeValue("linktext");
		this.setPrimaryProperty("linktext",this.linkText);
		this.partialLinkText=curTag.attributeValue("partiallinktext");
		this.setPrimaryProperty("partiallinktext",this.partialLinkText);
		this.tagName=curTag.attributeValue("tagname"); //html tag name in the UI
		this.setPrimaryProperty("tagname",this.tagName);
		this.url=curTag.attributeValue("url");
		this.setPrimaryProperty("url",this.url);
		
		//Use having attribute with closeTo tag
		//havingTag has special meaning do not store in the this.setSecondaryProperty
		this.havingTag=curTag.attributeValue("havingTag");		
		this.havingText=curTag.attributeValue("havingText");
		this.setSecondaryProperty("havingText", this.havingText);
		this.havingAlt=curTag.attributeValue("havingAlt");
		this.setSecondaryProperty("havingAlt", this.havingAlt);
		this.havingImage=curTag.attributeValue("havingImage");
		this.setSecondaryProperty("havingImage", this.havingImage);
		this.havingLinkText=curTag.attributeValue("havingLinkText");
		this.setSecondaryProperty("havingLinkText", this.havingLinkText);
		this.havingName=curTag.attributeValue("havingName");
		this.setSecondaryProperty("havingName", this.havingName);
		this.havingClassName=curTag.attributeValue("havingClassName");
		this.setSecondaryProperty("havingClassName", this.havingClassName);
		this.havingOnClick=curTag.attributeValue("havingOnClick");
		this.setSecondaryProperty("havingOnClick", this.havingOnClick);
		this.havingHref=curTag.attributeValue("havingHref");
		this.setSecondaryProperty("havingHref", this.havingHref);
		this.havingAttribute=curTag.attributeValue("havingAttribute");
		this.setSecondaryProperty("havingAttribute", this.havingAttribute);
		
		
		this.value=curTag.getText();
		if(!value.isEmpty()){
			sendkeyValue=value;
			//initialize value which should be returned
			if(value.contains("@") &&value.indexOf("@")<2){
				addTestResult(value.replace("@", ""),null);
			}
		}
		
		
		//check whether element exists
		if(this.isBrowserOpen &&this.configTag!=null &&this.configTag.equalsIgnoreCase("tag") 
				&&!this.method.equalsIgnoreCase("extractItem") &&!this.isBypassCustomMethod()){
			this.verifyElementPresent();
		}
		this.isBrowserOpen=true;
		
	}
	
	private boolean doWait(String condition){
		if(condition==null ||!condition.equalsIgnoreCase("acceptalert"))
			return true;
		return false;
		
	}
	private void setPrimaryProperty(String key, String value){
		if(value!=null &&!value.isEmpty()){
			this.primary.put(key, value);
		}
	}
	
	private void setSecondaryProperty(String key, String value){
		if(value!=null &&!value.isEmpty()){
			this.secondary.put(key, value);
		}
	}
	protected String addMessage(){
		return (timer.getPrintTime()+">> Control="+this.controlIdentifier +">> Executing Tag="+this.targetTag.asXML());
	}
	protected String getExceptionMessage(Exception e){
		return((e.getMessage()!=null&&e.getMessage().length()>100?e.getMessage().substring(0, 100):e.getMessage()));
		
	}
	
	public void printMessage(String msg){
		WmLog.printMessage(msg);
		if(this.isMultiThreaded){
			try {
				if(!this.isLogClosed){
					this.logfile.append("\n"+ msg);
					this.logfile.flush();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public void printCommand(String msg){
		
		if(this.isMultiThreaded){
			try {
				if(!this.isLogClosed){
					this.commandlogfile.append("\n"+ msg);
					this.commandlogfile.flush();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	protected synchronized void addTestExtract(String var, String text){
		if(var!=null &&!var.isEmpty()){
			String key=var.split("@")[1];
			if(this.stripchar!=null &&!this.stripchar.isEmpty()){
				String[] strips=this.stripchar.split(";");
				for(String strip:strips){
					text=text.replace(strip, "");
				}
				if(this.stripchar.equalsIgnoreCase("$")){
					text=text.split("\n")[0];
				}
			}
			
			addTestResult(key, text);
			try {
				this.getScreenShot(key);
			} catch (WebDriverException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TestMaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			msg=msg+ " Expected:="+var +" Actual:="+text;	
			printMessage("ADDED TEST RESULT FOR VALIDATION >> Found value="+var +" in text="+text +" CONTROL=" +msg);
		}
		
	}
	protected void assertText(String var, String text){
		boolean condition=text.contains(var)?true:false;
		msg="PASSED ASSERT CONTROL >>"+ " Expected:="+var +" Actual:="+text+ msg;
		assertTrue(msg, condition);
		printMessage(msg);
		
	}
	
	protected  ArrayList parseTagLib(){
		//this.printMessage("############## parseTagLib() is called by "+ this.threadIndex +" #################");
		ArrayList libs=null;
		List<Element> taglibs=null;
		synchronized(this){
			libs= new ArrayList();
			if(base==null){
				taglibs=BaseHandler.getDefaultTagLibraries();
			}else{
				taglibs=base.getTagLibraries();
			}
			for(Element tagLib:taglibs){
				String[] tags=tagLib.getText().split("@");
				for(String tag:tags){
					if(!tag.trim().isEmpty()){
						libs.add(tag.trim());
					}
				}
			}
			//this.printMessage("############## parseTagLib() returned Taglib= "+ libs.toString() +" #################");
			return libs;
		}
		
	}
	
	protected boolean verifyInjectorConfigured(String injectorId){			
		for(Element injector:BaseHandler.getInjectors().get(this.base.handlerName)){
			String[] tags=injector.getText().split("@");
			for(String tag:tags){
				if(!tag.trim().isEmpty() && tag.trim().equalsIgnoreCase(injectorId)){
					return true;
				}
			}
		}
		return false;
	}
	
	
	protected WebElement findElementByAttribute(WebElement control,String attributeName, String attributeValue){
		List<WebElement> elems =null;			
		WebElement myElem=null;
		try{
			String[] targetTags=havingTag.split(";");
			
			for (String target:targetTags){
				if(myElem==null &&target!=null ){
					if(control!=null){
						elems =control.findElements(By.tagName(target));
					}else{
						elems =driver.findElements(By.tagName(target));
					}
			    	myElem=findElementByAttributeImpl(elems,attributeName,attributeValue);
			    	
			    	if(myElem!=null){
			    		return myElem;
			    	}
			    }
			    
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return myElem;
	}
 
  private List<WebElement> getElementListHavingText(List<WebElement> elems, String text){
	  List<WebElement> elmlist= new ArrayList<WebElement>();
	  for(WebElement elm:elems){
		  if(elm.getText()!=null &&elm.getText().contains(text)){
			  elmlist.add(elm);
		  }
	  }
	  
	  return(elmlist);
	  
  }
  protected WebElement findElementByTextImpl(List<WebElement> elems){
	   List<WebElement> elmlist=null;
		try{
		    String[] texts=havingText.split(";");
		    if(texts.length>0){
		    	elmlist=this.getElementListHavingText(elems, texts[0]);
		    }
		    if(!elmlist.isEmpty()&&texts.length>1){
		    	for(int i=1;i<texts.length;i++){
		    		elmlist=this.getElementListHavingText(elmlist, texts[i]);
		    	}
		    	
		    }
		   if(!elmlist.isEmpty()){
			   return elmlist.get(0);
		   }
		}catch (Exception e){
			e.printStackTrace();
		}
	   
		return null;
	}
  /*
	protected WebElement findElementByTextImpl(List<WebElement> elems){
		
		boolean textmatched=false;
		WebElement myElem=null;
		int startIdx=0;
		try{
		    String[] texts=havingText.split(";");
		    for (int k=0; k<texts.length; k++){		    	
				for (int i = startIdx; i <  elems.size(); i++) {
				this.setImpilicitTimeInMiliSec(new Integer(this.timeout));
				  if (!textmatched && elems.get(i).getText()!=null && elems.get(i).getText().contains(texts[k])) 
				  {   
					  textmatched=true;
					  myElem = elems.get(i); 
					  startIdx=i;
					   break;
				  }
				if(textmatched  && elems.get(i).getText()!=null&&elems.get(i).getText().contains(texts[k]) ){
						myElem = elems.get(i); 
					  	String actual=myElem.getText();
					  	return myElem;
					
				  }
				}
		    }
		}catch (Exception e){
			e.printStackTrace();
		}
	   
		return myElem;
	}
	*/
	protected WebElement findElementByAttributeImpl(List<WebElement> elems, String attributeName,String attributeValue){
		boolean textmatched=false;
		WebElement myElem=null;
		int startIdx=0;
	
		try{
			String[] attrvals=attributeValue.split(";");
		    for (int k=0; k<attrvals.length; k++){
				for (int i = startIdx; i <  elems.size(); i++) {
				  this.setImpilicitTimeInMiliSec(new Integer(this.timeout));
				  if (!textmatched &&elems.get(i).getAttribute(attributeName)!=null && elems.get(i).getAttribute(attributeName).toLowerCase().contains(attrvals[k].toLowerCase())) 
				  {   
					  textmatched=true;
					  myElem = elems.get(i); 
					  startIdx=i;
					  break;
				  }
				  if(textmatched && attrvals.length==k+1&&elems.get(i).getAttribute(attributeName)!=null &&elems.get(i).getAttribute(attributeName).toLowerCase().contains(attrvals[k].toLowerCase())){
					  myElem = elems.get(i); 					 
					  return myElem;
				  }
				}
		    }
		}catch (Exception e){
			e.printStackTrace();
		}
	   
		return myElem;
	}
	protected By By() throws TestMaxException {
		  
		   final PrintTime timer=new PrintTime();		  
		   printMessage("****LOCATING BY for Element with attributes for= "+this.tagdesc);
		   long start=System.currentTimeMillis();
		   int counter=0;
		   By by=null;
		   int addTimeOut=0;
		   isByLocated=false;
		   if(this.isIE){
			   addTimeOut=addTimeOut+
			   new Integer(ConfigLoader.getConfig("SELENIUM_IE_EXTRA_TIME")==null?"500":ConfigLoader.getConfig("SELENIUM_IE_EXTRA_TIME"));
		   } 		 
		   final int v_timeout= (this.timeout!=null &&!this.timeout.isEmpty()?new Integer(this.timeout):500)+addTimeOut;
		   if(this.isConditionalElement){
			   this.waitToPageLoad(new Integer(timeout));
			   while (by==null &&counter<5){
				   counter++;
			   //while ((System.currentTimeMillis()-start)<new Integer(timeout)){
				   setImpilicitTimeInMiliSec(v_timeout);
				   try {
					by=getBy();
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  
				    if(by!=null){
				    	printMessage("**** LOCATED CONDITINAL BY>> Time="+timer.getPrintTime()+" id="+this.controlIdentifier+" with attributes for= "+this.tagdesc);
				    	return by;
				    }
			   }
			   printMessage("****FAILED TO LOCATE CONDITINAL BY>> Time="+timer.getPrintTime()+" id="+this.controlIdentifier+" for conditional Element with for= "+this.tagdesc);
			   return null;
		   }
		   try{
		       //Set up a WebDriverWait to wait in second
		        WebDriverWait driverWait = new WebDriverWait(this.getDriver(),v_timeout,500); 
	     
	            //Define an ExpectedCondition 
                ExpectedCondition<By> expectedCondition = new   ExpectedCondition<By>() { 
                	
                   public By apply(WebDriver d)  {                       
                 	 
                           try {
                        	   if(timer.getTime()<maxTimeToWait*1000){
                        	   //if(!isByLocated){
		                           	setImpilicitTimeInMiliSec(v_timeout);
		                           	By by=getBy();
		                           	if(by!=null){
		                           		isByLocated=true;
		                           	}
		                           
		                           	return by;
                           
                        	   }else{
                        		   String msg="####### TIMEOUT EXCEPTION : while calling getBy() for Element Trace Time:"+timer.getPrintTime()+" with Id="+controlIdentifier 
                           	    	+"\n Please increase TIMEOUT setting in config file using SELENIUM_COMMAND_WAIT  parameter values in sec.";
                        		   printMessage(msg);
                        		   isByLocated=false;
                        		   return By.tagName("div");
                        	   }
                        	  
                        	 
                           } catch (TestMaxException t) {
                         	 	// TODO Auto-generated catch block
            						t.printStackTrace();
            						 return null;
            						
                           } catch (NoSuchElementException ex) {} 
                                return null;	                              
                 	  };
                    
                }; 		        
                
               while ((by==null||!isByLocated)&&counter<3){
            	   	counter++;
            	    printMessage("LOCATING BY TRIAL=" +counter+" >> Time="+timer.getPrintTime()+"'"+this.tagdesc+"' >> with Id="+this.controlIdentifier +addMessage());
                	by = driverWait.until(expectedCondition); 
               		
                }
             
		     	
		       
		   }catch (Exception e){
			   String msg= "***Dataset:"+this.threadIndex+" FAILED TO LOCATE BY >> Time="+timer.getPrintTime()+"'"+this.tagdesc+"' >> with Id="+this.controlIdentifier+" :"+this.getExceptionMessage(e);
			
			   throw new TestMaxException(msg);
			 
		   }
		   
		   this.timeTaken=this.timeTaken+System.currentTimeMillis()-start;
	     	printMessage("LOCATED BY >> Time="+timer.getPrintTime()+"'"+this.tagdesc+"' >> with Id="+this.controlIdentifier +addMessage());
	        if(!isByLocated){
	        	try {
					 if(this.value!=null &&this.value.contains("@")){
						this.getScreenShot(this.value.split("@")[1]);
					 }
					} catch (WebDriverException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TestMaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	 msg= "***ERROR:"+this.threadIndex+" FAILED TO LOCATE BY >> TIMEOUT DETECTED, Elasped Time="+timer.getPrintTime()+"' >> with Element Id="+this.controlIdentifier+" for action "+this.tagdesc;
				
				  throw new TestMaxException(msg);
	        	//return null;
	        }
	     	return by;
		  
		  
	}
	protected By getBy() throws NumberFormatException, TestMaxException{
		this.controlIdentifier="";
		for(Object key:primary.keySet()){
			String[] attrs=primary.get(key.toString()).split(";");
			for(String attr:attrs ){
				this.setImpilicitTimeInMiliSec(new Integer(timeout));
				if(key.toString().equalsIgnoreCase("id")){
					this.controlIdentifier=attr;
					if(this.isElementDisplayed(By.id(attr))||isBypassDisplay() &&isElementPresent(By.id(attr))){						
						return By.id(attr);
					}
				}else if(key.toString().equalsIgnoreCase("name")){
					this.controlIdentifier=attr;
					if(this.isElementDisplayed(By.name(attr))){						
						return By.name(attr);
					}					
				}else if(key.toString().equalsIgnoreCase("classname")){
					this.controlIdentifier=attr;
					if(this.isElementDisplayed(By.className(attr))){						
						return By.className(attr);
					}					
				}else if(key.toString().equalsIgnoreCase("cssSelector")){
					this.controlIdentifier=attr;
					if(this.isElementDisplayed(By.cssSelector(attr))){						
						return By.cssSelector(attr);
					}
				}else if(key.toString().equalsIgnoreCase("linkText")){
					this.controlIdentifier=attr;
					if(this.isElementDisplayed(By.linkText(attr))){						
						return By.linkText(attr);
					}	
				}else if(key.toString().equalsIgnoreCase("xpath")){
					this.controlIdentifier=attr;
					if(this.isElementDisplayed(By.xpath(attr))){						
						return By.xpath(attr);						
					}					
				}else if(key.toString().equalsIgnoreCase("tagname")){
					this.controlIdentifier=attr;
					return By.tagName(attr);
				}
			}
			//printMessage("****ERROR : No ATTRIBUTE matching for the Primary Element= "+this.tagdesc);
		}
		return null;
	}
	public  WebDriver getDriver()  {
		synchronized(this.driver){
			this.driver=driverList.get(this.threadIndex);
		}
		if(this.driver==null){
			return null;
		}
		try{
			boolean child=false;
			for (String handle : driver.getWindowHandles()) {
				try{
					driver.switchTo().window(handle);
					child=true;
					
					if(!this.isEmptyValue(window) &&!this.isEmptyValue(handle) && window.equalsIgnoreCase(handle)){
						//this.printMessage("#### Located Popup window with handle="+handle);
						break;
					}
				}catch (Exception e){
					this.printMessage("WARNING: Failed to locate Popup window with exception! "+e.getMessage());	
					child=false;
				}
			}
			if(!child){
				driver.switchTo().defaultContent();
			}
			if(this.iframe!=null &&!this.iframe.isEmpty()){
				String[] iframes=this.iframe.split(";");
				boolean hasException=false;
				for(String v_iframe:iframes){
					hasException=false;
					try{						
					WebElement v_frame=driver.findElement(By.id(v_iframe));
					driver.switchTo().frame(v_frame);
					isIframe=true;
					break;
					}catch (Exception e){
						hasException=true;
					}
				}
				if(hasException){
					driver.switchTo().defaultContent();
				}
				
				return this.driver;
			}
			
			
		}catch (Exception e){
			String msg="########### DRIVER OBJECT EXCEPTION, SWITCHING WINDOW ************************";
			this.printMessage(msg);	
			return null;
			//return this.driver;
		}
		return this.driver;
	}
	
	protected boolean verifyElementPresent() throws TestMaxException{
		 boolean isPresent=false;
		 long start=System.currentTimeMillis();
		 //look for conditional element for shorter time
		 if(this.isConditionalElement){
			 while(!isPresent &&(System.currentTimeMillis()-start)< new Integer(timeout)){
				
				try{
					this.setImpilicitTimeInMiliSec(new Integer(timeout));
					isPresent=this.isElementPresent(By());
					if(isPresent){
						return true;
					}
				}catch(Exception e){
					this.msg=this.getExceptionMessage(e);
				}
			}
		 }else{
			//look for other element for 1 min max
			 while(!isPresent &&(System.currentTimeMillis()-start)< 60000){
					
					try{
						this.setImpilicitTimeInMiliSec(60000);
						isPresent=this.isElementPresent(By());
						if(isPresent){
							return true;
						}
					}catch(Exception e){
						this.msg=this.getExceptionMessage(e);
					}
				}
			 
		 }
		return(false);
	}
	/*
	 protected boolean isElementPresent(By by) {
			try {
				//driver.findElement(by);
				List <WebElement> elms=driver.findElements(by);
				if(elms!=null)
				return true;
			} catch (NoSuchElementException e) {
				return false;
			}
			return false;
		}
		protected boolean isElementDisplayed(By by) {
			try {
				List <WebElement> elms=driver.findElements(by);
				//WebElement elm=driver.findElement(by);
				for(WebElement elm:elms){
					if(elm!=null &&elm.isDisplayed()){
						return true;
					}
				}
			} catch (NoSuchElementException e) {
				return false;
			}
			return false;
		}
		*/
	public boolean isElementPresent(By by) {
		try {
			this.getDriver().findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}
	public boolean isElementDisplayed(By by)  {
		 if (this.isConditionalElement||isBypassDisplay()){
			 return true;
		 }else{
			try {
				WebElement elm=this.getDriver().findElement(by);
				
				if(elm!=null &&elm.isDisplayed()){
					return true;
				}
			} catch (NoSuchElementException e) {
				
				this.printMessage("****Element id="+this.controlIdentifier+" is not Displayed!");
				return false;
			}
		 }
		return false;
	}
	public  WebElement waitForElement(WebElement elementToWaitFor, Integer waitTimeInSeconds) {
	    if (waitTimeInSeconds == null) {
	    	waitTimeInSeconds = 10;
	    }
	    
	    WebDriverWait wait = new WebDriverWait(this.driver, waitTimeInSeconds);
	    return wait.until(ExpectedConditions.visibilityOf(elementToWaitFor));
	}
	protected abstract WebElement execute() throws TestMaxException ;
	
	
	protected  List<WebElement> getPrimaryElement() throws NumberFormatException, TestMaxException{
		 String msg="";
		 PrintTime timer= new PrintTime();
		 List<WebElement> elms=null;
		 long start=System.currentTimeMillis();
		 while(elms==null &&(System.currentTimeMillis()-start)< new Integer(timeout)){
			this.setImpilicitTimeInMiliSec(new Integer(timeout));
			try{
				elms=this.getDriver().findElements(this.By());
				if(elms!=null){
					printMessage("**** Dataset:"+this.threadIndex+" PRIMARY ELEMENT FOUND "+timer.getPrintTime()+">> BY Identifier="+this.controlIdentifier+">>"+this.tagdesc  );
					this.setImpilicitTimeInMiliSec(new Integer(timeout));
					return elms;
				}
			}catch (TestMaxException t){
				throw t;
			}catch(Exception e){
				 msg="$$$$$ Dataset:"+this.threadIndex+" PRIMARY ELEMENT  NOT FOUND "+timer.getPrintTime()+">> BY Identifier="+this.controlIdentifier+">>"+this.tagdesc+" "+e.getMessage() ;
				printMessage(msg );
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		if(msg.isEmpty() &&elms.isEmpty()){
			msg="$$$$$ Dataset:"+this.threadIndex+" PRIMARY ELEMENT  NOT FOUND "+timer.getPrintTime()+">> BY Identifier="+this.controlIdentifier+">>"+this.tagdesc ;
			throw new TestMaxException(msg);
		}
		
		return elms;
		
	}
	
	protected  List<WebElement> getWebElements() throws NumberFormatException, TestMaxException{		
		 List<WebElement> elms=this.getPrimaryElement();
		 List<WebElement> selms=new ArrayList<WebElement>();
		 List<WebElement> havings=new ArrayList<WebElement>();
		 PrintTime timer= new PrintTime();
		 int msgcount=1;
		 if(elms!=null){
			 for(WebElement elm:elms){
				this.setImpilicitTimeInMiliSec(new Integer(timeout));
				try{
					havings=getElementByHaving(elm);				
				}catch(Exception e){
					printMessage(e.getMessage() );
				}
				
				if(havings!=null &&havings.size()>0){
					if(msgcount==elms.size()||msgcount==1){
						printMessage("**** ELEMENT WITH HAVING FOUND "+timer.getPrintTime()+">> BY Identifier="+this.controlIdentifier+">>"+this.tagdesc  );
						msgcount++;
					}
					 if(timer.getTime()>maxTimeToWait*1000){
						 String msg="****EXCEPTION: WAITING PERIOD EXCEEDS TO DISPLAY ELEMENT "+maxTimeToWait*1000+" mili (sec)>> BY Identifier="+this.controlIdentifier+">>"+this.tagdesc;
						 throw new TestMaxException(msg);
					 }
					for(WebElement target:havings){
						try{
						
						if(target.isDisplayed()){
							selms.add(target);
						}
						}catch (Exception e){
							 if(timer.getTime()>maxTimeToWait*1000){
								 String msg="****EXCEPTION: WAITING PERIOD EXCEEDS TO DISPLAY ELEMENT "+maxTimeToWait*1000+" mili (sec)>> BY Identifier="+this.controlIdentifier+">>"+this.tagdesc;
								 throw new TestMaxException(msg);
							 }
						}
					}
				}
				
			 }
		 }
		 if(secondary.keySet().isEmpty() &&(this.havingTag==null ||this.havingTag.isEmpty())){
			 return elms;
		 }
		 if(selms.size()<1){
			 try {
				 if(this.value!=null &&this.value.contains("@")){
					this.getScreenShot(this.value.split("@")[1]);
				 }
				} catch (WebDriverException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TestMaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 String msg="$$$$$ ELEMENT WITH HAVING NOT FOUND "+timer.getPrintTime()+">> BY Identifier="+this.controlIdentifier+">>"+this.tagdesc  ;
			 printMessage(msg);			
				
		 }
		 return selms;
	}
	
	private boolean verifyHavingAttribute(WebElement elm){
		boolean isHavingAttribute=this.havingAttribute!=null &&!this.havingAttribute.isEmpty();
		if(!isHavingAttribute) return true;
		if(isHavingAttribute){
			
			String[] attrs=this.havingAttribute.split(";");
			for (String attr:attrs){
				String[] attrprops=attr.split(":");
				if(attrprops.length<2) {
					 printMessage(">>>>>>>ERROR: Atrribute property not set correctly for the element tag id"+ this.controlIdentifier +" Atrribute="+this.havingAttribute);	
					 return false;
				}				
				String value=attrprops[1];
				String elmval="";
				if(attrprops[0].equalsIgnoreCase("text")){
					elmval=elm.getText();
				}else{
				     elmval=elm.getAttribute(attrprops[0]);
				}
				if(value.isEmpty()||elmval==null ||!elmval.trim().equals(value)){
					return false;
				}
			}
			return true;
		}
		
		return false;
	}
	private List<WebElement> getElementByHaving(WebElement elm) throws NumberFormatException, TestMaxException{
		List<WebElement> selms=null;
		List<WebElement> elms=new ArrayList<WebElement>();
		boolean isHavingText=secondary.get("havingText")!=null && !secondary.get("havingText").isEmpty();
		boolean isHavingTag=this.havingTag!=null &&!this.havingTag.isEmpty();
		try{
		if(secondary.keySet().size()<1 &&!isHavingText){
			elms.add(elm);
			return elms;
		}
		for(Object key: secondary.keySet()){
			this.controlIdentifier= " havingTag="+this.havingTag+" and "+key.toString()+ "="+secondary.get(key).toString();
			this.setImpilicitTimeInMiliSec(new Integer(timeout));			
			if(isHavingTag){
				String[] havings=this.havingTag.split(";");
				for (String having: havings){
					selms=elm.findElements(By.tagName(having));
					if(selms==null||selms.size()<1){
						continue;
					}
				}
				if(selms==null||selms.size()<1){
					elms.add(elm);
					return elms;
				}
				for(WebElement selm:selms){
					
					if(verifyHavingAttribute(selm)){
						this.setImpilicitTimeInMiliSec(new Integer(timeout));	
						if(key.toString().equalsIgnoreCase("havingImage")){
							if(selm.getAttribute("src")!=null &&selm.getAttribute("src").toLowerCase().contains(secondary.get(key).toString().toLowerCase())){
								elms.add(selm);
							}
						}else if(key.toString().equalsIgnoreCase("havingAlt")){
							if(selm.getAttribute("alt")!=null &&selm.getAttribute("alt").toLowerCase().contains(secondary.get(key).toString().toLowerCase())){
								elms.add(selm);
							}
						}else if(key.toString().equalsIgnoreCase("havingOnClick")){
							if(selm.getAttribute("onClick")!=null &&selm.getAttribute("onClick").toLowerCase().contains(secondary.get(key).toString().toLowerCase())){
								elms.add(selm);							
							}
						}else if(key.toString().equalsIgnoreCase("havingClassName")){
							if(selm.getAttribute("class")!=null &&selm.getAttribute("class").toLowerCase().contains(secondary.get(key).toString().toLowerCase())){
								elms.add(selm);							
							}
						}else if(key.toString().equalsIgnoreCase("havingName")){
							if(selm.getAttribute("name")!=null &&selm.getAttribute("name").toLowerCase().contains(secondary.get(key).toString().toLowerCase())){
								elms.add(selm);							
							}
						}else if(key.toString().equalsIgnoreCase("havingHref")){
							if(selm.getAttribute("href")!=null &&selm.getAttribute("href").toLowerCase().contains(secondary.get(key).toString().toLowerCase())){
								elms.add(selm);							
							}
						}else if(key.toString().equalsIgnoreCase("havingLinkText")){
							if(selm.getText()!=null &&selm.getText().toLowerCase().contains(secondary.get(key).toString().toLowerCase())){
								elms.add(selm);	
							}
						}else{						
							elms.add(selm);						
						}
					}
				  }
					
				 if(!isHavingText &&elms.size()>0){
					 return elms;
				 }
				}else if (verifyHavingAttribute(elm)){
					
					this.setImpilicitTimeInMiliSec(new Integer(timeout));
					if(key.toString().equalsIgnoreCase("havingImage")){
						if(elm.getAttribute("src")!=null &&elm.getAttribute("src").toLowerCase().contains(secondary.get(key).toString().toLowerCase())){
							elms.add(elm);							
														
						}
					}else if(key.toString().equalsIgnoreCase("havingAlt")){
						if(elm.getAttribute("alt")!=null &&elm.getAttribute("alt").toLowerCase().contains(secondary.get(key).toString().toLowerCase())){
							elms.add(elm);							
						}
					}else if(key.toString().equalsIgnoreCase("havingOnClick")){
						if(elm.getAttribute("onClick")!=null &&elm.getAttribute("onClick").toLowerCase().contains(secondary.get(key).toString().toLowerCase())){
							elms.add(elm);							
						}
					}else if(key.toString().equalsIgnoreCase("havingClassName")){
						if(elm.getAttribute("class")!=null &&elm.getAttribute("class").toLowerCase().contains(secondary.get(key).toString().toLowerCase())){
							elms.add(elm);							
						}
					}else if(key.toString().equalsIgnoreCase("havingName")){
						if(elm.getAttribute("name")!=null &&elm.getAttribute("name").toLowerCase().contains(secondary.get(key).toString().toLowerCase())){
							elms.add(elm);	
						}
					}else if(key.toString().equalsIgnoreCase("havingHref")){
						if(elm.getAttribute("href")!=null &&elm.getAttribute("href").toLowerCase().contains(secondary.get(key).toString().toLowerCase())){
							elms.add(elm);	
						}
					}else if(key.toString().equalsIgnoreCase("havingLinkText")){
						if(elm.getText()!=null &&elm.getText().toLowerCase().contains(secondary.get(key).toString().toLowerCase())){
							elms.add(elm);							
						}					
					}else{						
						elms.add(elm);						
					}
					
					if(!isHavingText &&elms.size()>0){
						 return elms;
					 }
				}
				
			}
		if(secondary.get("havingText")!=null && !secondary.get("havingText").isEmpty()){
			this.setImpilicitTimeInMiliSec(new Integer(timeout));
			this.havingText=secondary.get("havingText").toString();
			WebElement telm=this.findElementByTextImpl(elms);
			elms=new ArrayList<WebElement>();
			elms.add(telm);
			
		}
		}catch(Exception e){
			this.setImpilicitTimeInMiliSec(new Integer(timeout));
			elms=new ArrayList<WebElement>();
			elms.add(elm);
			
		}
		return elms;
	}
	public void setImpilicitTimeInMiliSec(int timeout) throws TestMaxException{
		 if(this.getDriver()==null){
			 throw new TestMaxException("ERROR : Can not reset DRIVER for browser= "+this.threadIndex + " which is NULL!");
		 }
		try{
			this.getDriver().manage().timeouts().implicitlyWait(timeout+10000, TimeUnit.MILLISECONDS);
		
		}catch(Exception e){
			String msg="Dataset:"+this.threadIndex+" ERROR: BROWSER DIED! TRYING to reset time!"+e.getMessage();
			this.printMessage(msg);
			int counter=0;
			//Try 10 times otherwise throw Exception
			while(counter<5){
				try {
					Thread.sleep(500);
					msg="Dataset:"+this.threadIndex+" TRYING DRIVER RESET TRIAL="+counter+" to reset time!"+e.getMessage();
					this.printMessage(msg);
					//retry
					this.getDriver().manage().timeouts().implicitlyWait(timeout+10000, TimeUnit.MILLISECONDS);
					return;
					
				} catch (InterruptedException e1) {
					 msg="Dataset:"+this.threadIndex+" DRIVER RESET TRIAL="+counter+" to reset time!"+e.getMessage();
					this.printMessage(msg);
					counter++;
					// TODO Auto-generated catch block
					//e1.printStackTrace();
				}catch(Exception e1){
					//e1.printStackTrace();
					msg="Dataset:"+this.threadIndex+" DRIVER RESET TRIAL EXCEPTION="+counter+" to reset time!"+e.getMessage();
					this.printMessage(msg);
					counter++;
				}
			}
			 /*if(counter>=9){
				 this.driver.quit();
				 this.driverList.put(this.threadIndex, this.driver);
			 }
			 */
					
		}
		return;
		
	}
	
	
	protected boolean clearElement(){
		   PrintTime timer= new PrintTime();
			     
		    try{
		    	setImpilicitTimeInMiliSec(new Integer(timeout));
		    	if(this.id!=null &&!this.id.isEmpty()){
		    		//((JavascriptExecutor) this.getDriver()).executeScript("document.getElementById('"+this.id+"').value; " );
		    		((JavascriptExecutor) this.getDriver()).executeScript("document.getElementById('"+this.id+"').style.display=\"block\";document.getElementById('"+this.id+"').value=''; " );
		    		printMessage("**** Cleared Text using Java Script "+timer.getPrintTime()+" Element id="+this.id);
		    		
		    		return true;
		    	}
		    	
		    }catch(Exception e){
		    	e.printStackTrace();
		    	printMessage("**** Failed to Clear Element using Java Script "+timer.getPrintTime()+" Element id="+this.id +" "+e.getMessage());
		    }  
	   
	    return false;
	 }
	private boolean selectCaption() throws TestMaxException{
		
	    if(this.className==null && !this.className.isEmpty() 
	    &&this.havingText==null &&!this.havingText.isEmpty()){
			  throw new TestMaxException("$$$$$$ERROR: Operation can not be executed since havingAttribute property for tag Element is Empty!! Please modify your XML configuration in Taglib.");
			 
	    }
		return false;
	}
	protected boolean executeOperation() throws TestMaxException{
		   PrintTime timer= new PrintTime();
		  String ieclass="";
		  String script="";
		  String outerif="";
		  String innerif="";
		  String verifytags="<a <span <div <img <option <td <li <ul";
		  boolean isInnerHTML=false;
		  String readvalue="";
		  if(this.havingAttribute!=null &&!this.havingAttribute.isEmpty()){
			  if(this.isEmptyValue(this.tagName)&&!this.isEmptyValue(this.havingTag)){
				  this.tagName=this.havingTag;
			  }
			  this.controlIdentifier= " tagname="+this.tagName+" and havingAttribute"+ "="+this.havingAttribute;
			  String[] attrs=this.havingAttribute.split(";");
			  for(String attr:attrs){
				 
				  String[] attrvs=attr.split(":");
				  if(attr.indexOf("=")>=0){
					  attrvs=attr.split("=");
				  }
				 
				  if(attrvs.length>=1){
					  ieclass=attrvs[0];
					  if((this.v_driver.equalsIgnoreCase("ie10")) &&attrvs[0].equalsIgnoreCase("class")){
						  attrvs[0]="className";
						  ieclass= attrvs[0];
					  }
					  isInnerHTML=(verifytags.contains("<"+this.tagName)&&attrvs[0].equalsIgnoreCase("text")?true:false);
					  String tmpattr=(isInnerHTML?"innerHTML":"getAttribute('"+attrvs[0]+"')");
					 
					  readvalue=(isInnerHTML|| this.v_driver.equalsIgnoreCase("firefox")?"arr[i].innerHTML" : "readvalue=arr[i].innerText;");
					  if(readvalue.contains("innerHTML")){
						  readvalue="\n\t\t\t\t if(arr[i].innerText!=null && arr[i].innerText!='' &&arr[i].innerText!='undefined'){\n\t\t\t\t readvalue='<div>'+arr[i].innerText+'</div>';\n\t\t\t\t}"+
								  "else{\n\t\t\t\treadvalue=arr[i].innerHTML;}\n";
					  }
					  if(this.tagName.contains("option") &&attrvs[0].equalsIgnoreCase("text")){
						  tmpattr="innerText";
					  }
					 
					  if(outerif.isEmpty()){
						  outerif="arr[i]."+tmpattr+"!=null";
					  }else{
						  outerif+="&& arr[i]."+tmpattr+"!=null";
					  }
					  if(attrvs.length==2){
						  if(attr.indexOf("=")>=0){
							  tmpattr=(this.v_driver.contains("firefox")?"textContent":"innerText");
							  if(innerif.isEmpty()){
								  innerif="arr[i]."+tmpattr+"==\""+attrvs[1]+"\"";
							  }else{
								  innerif+="&& arr[i]."+tmpattr+"==\""+attrvs[1]+"\"";
							  }
						  }else{
							  if(innerif.isEmpty()){
								  innerif="arr[i]."+tmpattr+".indexOf(\""+attrvs[1]+"\")>=0";
							  }else{
								  innerif+="&& arr[i]."+tmpattr+".indexOf(\""+attrvs[1]+"\")>=0";
							  }
						  }
					  }
				  }
			  }
		  
			  if(this.tagName!=null && !this.tagName.isEmpty()){
				  script="var arr = document.getElementsByTagName(\""+this.tagName+"\");";
			  }else  if(this.havingTag!=null && !this.havingTag.isEmpty()){
				  script="var arr =  document.getElementsByTagName(\""+this.havingTag+"\");";
			  }else  if(this.id!=null && !this.id.isEmpty()){
				  script="var arr = document.getElementById(\""+this.id+"\");";	
			  }else  if(this.name!=null && !this.name.isEmpty()){
				  script="var arr = document.getElementsByName(\""+this.name+"\");";
			  }else  if(this.className!=null && !this.className.isEmpty()){
				  script="var arr = document.getElementsByClassName(\""+this.className+"\");";	
			  }
		  }
		
		  
		  if(this.isCustomOperation() ){
			   try{
					 if( this.operation.equalsIgnoreCase("acceptalert")){
						  this.setImpilicitTimeInMiliSec(new Integer(timeout));
						  this.getDriver().switchTo().alert().accept();
						  this.printMessage("Invoked Custom Operation="+this.operation );
						  return true;
					 }else if( this.operation.equalsIgnoreCase("runscript")){
						  this.setImpilicitTimeInMiliSec(new Integer(timeout));						
							Thread.sleep(100);
							this.executeScript();
							this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier + "\n Script="+addMessage());
							printMessage("**** Executed Java Script= "+addMessage()+ " for Control="+this.controlIdentifier +":"+ addMessage());		
							return true;		
					 }else if(this.operation.equalsIgnoreCase("read")){
						 this.waitToPageLoad(new Integer(timeout));
						 String trackvalId="executeOperation_trackval_id";
						 String inIf=!innerif.isEmpty()? "\t\t\tif("+innerif+"){\n":"";
						 String closeIf=!innerif.isEmpty()? "\t\t\t}\n":"";
						 script="\n"+ script+"\n var elm; \n var readvalue;\n"+
						"\t for (var i = 0; i <arr.length; i++) {\n"+
							"\t\tif("+outerif+"){\n"+
							//"\t\t\t\t alert(readvalue);"+
				   					inIf+
				   					"\t\t\t\t "+readvalue+"\n"+
				   					"\t\t\t\t if(readvalue!=null &&readvalue!='' &&readvalue!='undefined'){\n"+
				   					"\t\t\t\t elm= document.createElement('div');\n" +
				   					"\t\t\t\t elm.setAttribute(\"id\",\""+trackvalId+"\"); \n"+
				   					"\t\t\t\t if(readvalue.indexOf('<')>=0){\n"+
				   					"\t\t\t\t\t elm.innerHTML=readvalue; \n\t\t\t\t}else{\n"+
				   					"\t\t\t\t\t elm.setAttribute(\"value\",readvalue); \n\t\t\t\t}\n"+
				   					//"\t\t\t\t elm.innerHTML=readvalue;\n"+
				   					"\t\t\t\t arr[i].appendChild(elm);\n"+
				   					"\t\t\t\t\t break;\n"+
				   					"\t\t\t\t}\n"+
				   					closeIf+
				   			"\t\t}\n"+  				
						"\t}\n";
				   	
						
						 try{   
					    		((JavascriptExecutor) this.getDriver()).executeScript(script);
					    		
					    		Thread.sleep(new Integer(timeout));
							 
						 }catch(WebDriverException e){
			    			printMessage("**** Executed Java Script With EXCEPTION "+timer.getPrintTime()+" "+e.getMessage()+" Script="+script);
						 }
						 printMessage("**** Executed Java Script "+timer.getPrintTime()+" Script="+script);
						 setImpilicitTimeInMiliSec(new Integer(timeout));
						 /*if(this.isIframe &&!this.v_driver.equalsIgnoreCase("chrome")){
							 driver.switchTo().defaultContent();
						 }
						 */
						 WebElement read=null;
						 try{
							 read=this.getDriver().findElement(By.id(trackvalId));
							
						 }catch(NoSuchElementException e){
							 if(read==null && this.v_driver.equalsIgnoreCase("ie") &&ieclass.equalsIgnoreCase("class")){
								 this.v_driver="ie10";
								 this.executeOperation();
								 this.v_driver="ie";
								 return true;
							 }
						 }
						 String valueextract="";
						 if(read!=null){
							 valueextract=read.getAttribute("value");
						 }
						 if(read!=null && this.isEmptyValue(valueextract)){
							 valueextract=read.getText();
						 }
						 if(!this.value.equals(valueextract)){
							 this.addTestExtract(this.value, valueextract);
						 }else{
							 printMessage("$$$$$$ ERROR Please change your tag variables which is having @<your_var> and should not be same as any column in your dataset XLS or any variable name in your validator "+timer.getPrintTime()+" tag Name="+this.injectorName+">>"+this.tagName);
						 }
						 
						 //finally remove id
						 String resetId=null;
						 try{   
							 //if(read!=null){
							     	resetId="document.getElementById('"+trackvalId+"').setAttribute('id',' ');";
							 	
							     	((JavascriptExecutor) this.getDriver()).executeScript(resetId);
							 //}
					    		
						 }catch(WebDriverException e){
			    			printMessage("**** Executed Java Script With EXCEPTION to reset id="+trackvalId+ " "+timer.getPrintTime()+" "+e.getMessage()+" Script="+resetId);
						 }
						 
						 return true;
					
					 }else if(this.operation.equalsIgnoreCase("selectcaption")){
						  
					  } 
			 
			   }catch(Exception e){
			    	e.printStackTrace();
			    	printMessage("**** script="+script);
			    	printMessage("**** Failed to Execute Java Script "+timer.getPrintTime()+" Element id="+this.id +" "+e.getMessage());
			    	return false;
			    } 
		  }
		  if(this.havingAttribute==null ||this.havingAttribute.isEmpty()){
			  throw new TestMaxException("$$$$$$ERROR: Operation can not be executed since havingAttribute property for tag Element is Empty!! Please modify your XML configuration in Taglib.");
		  }else{
			  String br=this.getOperation().contains("click")?"break;":"";
			  script="\n"+ script+"\n"+
					  
				"\t for (var i = 0; i <arr.length; i++) {\n"+
					"\t\tif("+outerif+"){\n"+
		   				"\t\t\tif("+innerif+"){\n"+
		   					"\t\t\t\t "+this.getOperation()+";\n"+
		   					"\t\t\t\t "+br+"\n"+
		   				"\t\t\t}\n"+
		   			"\t\t}\n"+  				
				"\t}\n";
				     
			    try{
			    	setImpilicitTimeInMiliSec(new Integer(timeout));
			    	if(script!=null &&!script.isEmpty()){
			    		try{
			    		((JavascriptExecutor) this.getDriver()).executeScript(script);
			    		//Thread.sleep(new Integer(timeout));
			    		if(this.getOperation().contains("click()")){
			    			this.waitToPageLoad(new Integer(timeout));
			    		}	    		
			    		
			    		}catch(WebDriverException e){
			    			printMessage("**** Executed Java Script With EXCEPTION "+timer.getPrintTime()+" "+e.getMessage()+" Script="+script);
			    			if( this.v_driver.equalsIgnoreCase("ie") &&ieclass.equalsIgnoreCase("class")){
			    				printMessage("**** Executed Java Script resetting IE class attribute "+timer.getPrintTime()+" "+e.getMessage()+" Script="+script);
								 this.v_driver="ie10";
								 this.executeOperation();
								 //this.v_driver="ie";
								 printMessage("**** Executed Java Script "+timer.getPrintTime()+" Script="+script);
								 return true;
							 }
			    		}
			    		printMessage("**** Executed Java Script "+timer.getPrintTime()+" Script="+script);
			    		
			    		return true;
			    	}
			    	
			    }catch(Exception e){
			    	e.printStackTrace();
			    	printMessage("**** script="+script);
			    	printMessage("**** Failed to Execute Java Script "+timer.getPrintTime()+" Element id="+this.id +" "+e.getMessage());
			    }  
		  }
	   
	    return false;
	 }
	
	protected String getOperation(){
		String op="";
		if(this.operation==null||this.operation.isEmpty()){
			op="arr[i].click()";
		}else if(this.operation.equalsIgnoreCase("click")){
			op="arr[i].click()";
		}else if(this.operation.equalsIgnoreCase("check")){
			op="if (arr[i].checked == false) arr[i].click()";
		}else if(this.operation.equalsIgnoreCase("uncheck")){
			op="if (arr[i].checked == true) arr[i].click()";
		}else if(this.operation.equalsIgnoreCase("write")){
			op="arr[i].value='"+this.sendkeyValue+"'";
		}
		
		return op;
		
	}
	
	protected void executeSql(){
		//this.base.getExecuteProc().executeWithDbmsOutput(qry)
	}
	protected boolean executeScript(){
		   PrintTime timer= new PrintTime();
			     
		    try{
		    	setImpilicitTimeInMiliSec(new Integer(timeout));
		    
	    		((JavascriptExecutor) this.getDriver()).executeScript(this.sendkeyValue);
	    		 printMessage("**** Executed Java Script "+timer.getPrintTime()+" Script="+this.sendkeyValue);
	    		 if(this.sendkeyValue.contains("click()")){
	    			 this.waitToPageLoad(new Integer(timeout));
	    		 }
	    		 //Thread.sleep(new Integer(timeout));
		    		return true;
		    
		    	
		    }catch(Exception e){
		    	e.printStackTrace();
		    	printMessage("**** Failed to Execute Java Script "+timer.getPrintTime()+" Element id="+this.id +" "+e.getMessage());
		    }  
	   
	    return false;
	 }
	/*
	 *  var testMaxElm = document.getElementById('"+byVal+"'); var testMaxNewElm = document.createElement('<div id=TestMaxAutomation></div>');testMaxElm.appendChild(testMaxNewElm);
	 */
	private boolean addAttributeToElement() throws NumberFormatException, TestMaxException{
		   PrintTime timer= new PrintTime();		  
		   //String [] trackingIds=ConfigLoader.getConfig("SELENIUM_TRACKING_ELEMENT_IDS").split(";");
		   String [] trackingIds={"SeleniumTrackId"};
			for(String v_trackId:trackingIds){
				setImpilicitTimeInMiliSec(new Integer(timeout));
			 try{ 
				 	this.trackId="TestMax_"+v_trackId;
				   ((JavascriptExecutor) this.getDriver()).executeScript("var testMaxElm= document.getElementsByTagName('body')[0]; "+						 
						   " var state=document.readyState; "+
						   " var testMaxNewElm = document.getElementById('"+this.trackId+"');  "+
						   " if(testMaxNewElm==null) {  " +
				   		" testMaxNewElm = document.createElement('div'); testMaxNewElm.setAttribute(\"id\",\""+this.trackId+"\"); " +
				   				"testMaxNewElm.setAttribute(\"status\",state);  testMaxElm.appendChild(testMaxNewElm);}else{testMaxNewElm.setAttribute(\"status\",state);}" );
		        	printMessage("**** Added Trace"+timer.getPrintTime()+" DIV id="+this.trackId+" under DIV ElementId= "+v_trackId +" Target Element Id="+ this.id);
		        	
		        	return true;
		        }catch(Exception e){
		        	String msg="**** Failed to Add Trace "+timer.getPrintTime()+" DIV id="+this.trackId+" under ElementId="+v_trackId+" Message: "+addMessage();
		        	printMessage(msg);
		        	try{
		        		//if(this.operation.equalsIgnoreCase("acceptalert")){
		        		
		        		if(this.driver.switchTo().alert()!=null){
		        			this.driver.switchTo().alert().accept();
		        			msg="**** ALERT Traced "+timer.getPrintTime()+" DIV id="+this.trackId+" under ElementId="+v_trackId+" Message: "+addMessage();
		        		}else{
		        			throw new TestMaxException(msg);
		        		}
		        		
		        	}catch(Exception a){
		        		throw new TestMaxException(msg);
		        	}
		        	
		        }
			}       
		    try{
		    	if(this.id!=null &&!this.id.isEmpty()){
		    		this.trackId="TestMax_"+id;
		    		((JavascriptExecutor) this.getDriver()).executeScript("var state=document.readyState; var testMaxElm = document.getElementById('"+this.id+"'); " +
		    				" var testMaxNewElm = document.createElement('div'); testMaxNewElm.setAttribute(\"id\",\""+this.trackId+"\"); " +
		    						"testMaxNewElm.setAttribute(\"status\",state);  testMaxElm.appendChild(testMaxNewElm);" );
		    		printMessage("**** Added Trace "+timer.getPrintTime()+" DIV id="+this.trackId+" under Target Element DIV Element Identifier="+this.id);
		    		
		    		return true;
		    	}
		    	
		    }catch(Exception e){
		    	String msg="****Dataset:"+this.threadIndex+" Failed to Add Trace "+timer.getPrintTime()+" DIV id="+this.trackId+" under Element ID="+id+" Message: "+addMessage();
		    	printMessage(msg);
		    	try{
	        		//if(this.operation.equalsIgnoreCase("acceptalert")){
		    		if(this.driver.switchTo().alert()!=null){
	        			this.driver.switchTo().alert().accept();
	        			msg="**** ALERT Traced "+timer.getPrintTime()+" DIV id="+this.trackId+" under Element ID="+id+" Message: "+addMessage();
	        		}else{
	        			throw new TestMaxException(msg);
	        		}
	        	}catch(Exception a){
	        		throw new TestMaxException(msg);
	        	}
		    }  
	   
	    return false;
	 }
	
	public boolean isAlertPresent() 
	{ 
	    try 
	    { 
	        this.driver.switchTo().alert(); 
	        return true; 
	    }   // try 
	    catch (NoAlertPresentException Ex) 
	    { 
	        return false; 
	    }   // catch 
	}  
   protected boolean waitToPageLoad(final int timeout) { 
	   final PrintTime timer= new PrintTime(); 
	   int counter=0;
	   WebElement element=null;	  
	   long addTimeOut=500;
	   if(isAlertPresent() ){
		   this.driver.switchTo().alert().accept();
	   }
	   if(this.isIE){
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
	                    		  Thread.sleep(200);
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
                              try { 
                            	  setImpilicitTimeInMiliSec(timeout);
                            	  //Thread.sleep(500);  
                            	  printMessage("**** LOCATING PAGE LOAD TRACE ELEMENT:"+timer.getPrintTime()+" with Id="+controlIdentifier);
                            	  element1 = d.findElement(By.id(trackId)); 
                              
                            	Thread.sleep(timeout==0?500:timeout-timeElapsed);                                  
                            	
                            	if(element1!=null){
                            		printMessage("**** PAGE LOAD TRACE ELEMENT LOCATED :"+timer.getPrintTime()+" Status="+element1.getAttribute("status")+" with Id="+controlIdentifier);
                            		return element1.getAttribute("status").equalsIgnoreCase("complete")?  element1:null; 
                            	}
                              } catch (TestMaxException t) {
                            	 	// TODO Auto-generated catch block
               						t.printStackTrace();
               						return element1;
                             } catch (InterruptedException e) {
                            	 	// TODO Auto-generated catch block
               						e.printStackTrace();
               				}
                    	  }
                    	
                    	  return null;
                      }
            	 
               }; 

              //Wait for page load
               while(element==null && counter<3){
            	   counter++;
            	   this.printMessage("******WAITING FOR PAGE LOAD TRIAL="+counter+" Element Trace Time:"+timer.getPrintTime()+" with Id="+this.controlIdentifier);    

               }
               setImpilicitTimeInMiliSec(timeout);
               this.printMessage("####### Element Trace Time:"+timer.getPrintTime()+" with Id="+this.controlIdentifier); 
               
               if(element==null){
            	   throw new TestMaxException("$$$$$ FAILED to add trace element! Element Trace Time:"+timer.getPrintTime()+" with Id="+this.controlIdentifier);
               }
       
      //True or false, did we return it? 
	   }catch (Exception e){
		   this.printMessage("####### FAILED Element Trace Time:"+timer.getPrintTime()+" with Id="+this.controlIdentifier+" :"+addMessage());
		  
		   return (element!=null); 
	   }
       return (element!=null); 
   
   }
  
   protected void mouseMove( WebElement elm){
	   Point coordinates = elm.getLocation();
	   Robot robot;
	try {
		robot = new Robot();
		robot.mouseMove(coordinates.getX(),coordinates.getY()+120);
	} catch (AWTException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
   }
   
   protected void clickBackButton(){
	   driver.navigate().back(); 
   }
   
   protected boolean isBypassDisplay(){
	   if(this.method!=null &&!this.method.isEmpty() &&this.bypassDisplayMethods.contains(this.method)){
		   return true;
	   }
	   return false;
   }
   
   protected boolean isVerifyPageLoadMethod(){
	   if(this.method!=null &&!this.method.isEmpty() &&this.verifyPageLoadMethods.contains(this.method)){
		   return true;
	   }
	   return false;
   }
   
   protected boolean isBypassCustomMethod(){
	   if(this.method!=null &&!this.method.isEmpty() &&this.bypassCustomMethods.contains(this.method)){
		   return true;
	   }
	   return false;
   }
   
   protected boolean isCustomOperation(){
	   if(this.operation!=null &&!this.operation.isEmpty() &&this.customOperation.contains(this.operation)){
		   return true;
	   }
	   return false;
   }
   
 
   protected boolean executeSleep() throws TestMaxException{
	   final PrintTime timer= new PrintTime(); 
	   long timetowait=60000;
		List<WebElement> controls=null;
			try {
				
				if(this.primary!=null &&!this.primary.isEmpty() &&this.isEmptyValue(this.havingAttribute)){
					if(!this.isEmptyValue(this.timeout) && new Integer(this.timeout)>timetowait){
						timetowait=new Integer(this.timeout);
					}
					this.timeout="3000";
					this.printMessage("******Starting SLEEP Time:"+timer.getPrintTime()+" for HTML Element Identifier="+this.controlIdentifier);
					while(timer.getTime()-500 < timetowait){
						controls=this.getPrimaryElement();
						if(!this.isEmptyValue(this.havingText) &&findElementByTextImpl(controls)!=null){
							this.printMessage("******Total SLEEP Time:"+timer.getPrintTime()+" with HTML Element Identifier="+this.controlIdentifier + " Having Text="+this.havingText);
							return true;
						}else if(controls!=null && controls.size()>0){
							this.printMessage("******Total SLEEP Time:"+timer.getPrintTime()+" with HTML Element Identifier="+this.controlIdentifier);
							return true;
						}else{
							Thread.sleep(500);
							this.printMessage("******Continue with SLEEP Time:"+timer.getPrintTime()+" Not found HTML Element Identifier="+this.controlIdentifier);
							continue;
						}
					}
					
					throw new TestMaxException("$$$$$ ERROR: Can not trace element with timeout="+this.timeout +" for HTML Element Identifier="+this.controlIdentifier + " Having Text="+this.havingText);
					
				}else{
					Thread.sleep(new Integer(timeout));
					this.printMessage("******Sleeped with SLEEP Time:"+timer.getPrintTime()+" with Timeout value set="+this.timeout);
				}
			} catch (NumberFormatException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		return false;
	}
   protected void getScreenShot(String filename) throws WebDriverException, TestMaxException {
	  // windowMaximize();
	   String filepath=ConfigLoader.getWmOutputWebServicePath()+File.separator+"/images/"+File.separator+this.base.getActionName().replaceAll(" ", "_")+"/"+this.threadIndex+File.separator+this.base.handlerName+File.separator;
	   FileUtility.createDir(filepath);  
	   /*if(this.v_driver.equalsIgnoreCase("ie")||this.v_driver.equalsIgnoreCase("ie10"))
	   {
		   try{
		        Thread.sleep(10000);
		        long id = Thread.currentThread().getId();
		        BufferedImage image = new Robot().createScreenCapture(new Rectangle(
		            Toolkit.getDefaultToolkit().getScreenSize()));
		        ImageIO.write(image, "png", new File(filepath+filename+".png"));
		    }
		    catch( Exception e ) {
		        e.printStackTrace();
		    }
	   }else{
	   */
		   File scrFile = ((TakesScreenshot)this.getDriver()).getScreenshotAs(OutputType.FILE);
		   this.printMessage("########## Absolute path for screen shot="+scrFile.getAbsolutePath());
		 
		   FileUtility.copyFile(scrFile.getAbsolutePath(),filepath+filename+".png");
	   //}
	  
	   //windowMinimize();
   }
   protected void getScreenShot() throws WebDriverException, TestMaxException {
	   //windowMaximize();
	   String filepath=ConfigLoader.getWmOutputWebServicePath()+File.separator+"/images/"+File.separator+this.base.getActionName().replaceAll(" ", "_")+"/"+this.threadIndex+File.separator+this.base.handlerName+File.separator;
	   String targetfile=this.base.getHandlerName()+"_"+this.base.getActionName().replaceAll(" ", "_")+".png";	  
	   FileUtility.createDir(filepath);
	  /* if(this.v_driver.equalsIgnoreCase("ie")||this.v_driver.equalsIgnoreCase("ie10"))
	   {
		   try{
		        Thread.sleep(10000);
		        long id = Thread.currentThread().getId();
		        BufferedImage image = new Robot().createScreenCapture(new Rectangle(
		            Toolkit.getDefaultToolkit().getScreenSize()));
		        ImageIO.write(image, "png", new File(filepath+targetfile+".png"));
		        this.printMessage("########## Absolute path for screen shot="+filepath+targetfile);
		    }
		    catch( Exception e ) {
		        e.printStackTrace();
		    }
	   }else{
	   */
		   File scrFile = ((TakesScreenshot)this.getDriver()).getScreenshotAs(OutputType.FILE);
		   this.printMessage("########## Absolute path for screen shot="+scrFile.getAbsolutePath());
		   //String targetfile="screen_"+this.threadIndex+"_"+this.base.getHandlerName()+"_"+this.base.getActionName().replaceAll(" ", "_");
		 
		   FileUtility.copyFile(scrFile.getAbsolutePath(),filepath+targetfile);
	   //}
	   //BaseHandler.addTestResult(this.threadIndex,"junit:"+targetfile, targetfile+".png");
	   //windowMinimize();
   }
  protected void downloadByAutoItFile(WebElement elm, String attr) {
	  String downloadLocation = elm.getAttribute(attr);
	 
      try {
    	  Object jx=((JavascriptExecutor) driver).executeScript(downloadLocation);
		  Thread.sleep(1000);
	      String[] dialog;
		  String browser = ConfigLoader.getConfig("SELENIUM_DRIVER");
		  if(browser.toLowerCase().contains("ie")){	
			  File file = new File(ie_dialog);
			  dialog =  new String[]{  file.getCanonicalPath(),"Download","Save" };
			  Runtime.getRuntime().exec(dialog);
		  }else if(browser.toLowerCase().contains("firefox")){
			  File file = new File(fe_dialog);
			  File file2 = new File(ConfigLoader.getWmOutputWebServicePath());
			  dialog = new String[] { file.getCanonicalPath(),"Opening","Save",file2.getCanonicalPath()};	
			  //dialog = new String[] { fe_dialog,"Opening","Save" };		  
			  Runtime.getRuntime().exec(dialog);
			  Thread.sleep(3000);
			  try{
				  Runtime.getRuntime().exec("taskkill /F /IM EXCEL.EXE");
			  }catch (Exception e){}
			  
		  }
		  
      } catch (InterruptedException e) {
  		// TODO Auto-generated catch block
  		e.printStackTrace();
  	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
  protected void downloadFile(WebElement elm){
	  String downloadLocation = elm.getAttribute("href");
	  String downloadFile=elm.getText();
	 
	  FileDownLoader fd=new FileDownLoader(this.driver);
	  fd.setDownloadPath(ConfigLoader.getWmOutputLogPath());
	  try {
		  if(this.havingHref!=null &&!this.havingHref.isEmpty()){
			  
			  if(downloadLocation.toLowerCase().contains("javascript")){
				  downloadByAutoItFile(elm,"href");
			  }else{
				  fd.fileDownloader(elm);
			  }
		  }else{
			   downloadLocation = elm.getAttribute("src");
			  if(downloadLocation.toLowerCase().contains("javascript")){
				  downloadByAutoItFile(elm,"src");
			  }else{
				  fd.imageDownloader(elm);
			  }
		  }
		  
		  //copy the file and delete test file
		  try{
			  String[] downloadPaths=ConfigLoader.getConfig("SELENIUM_DOWNLOAD_LOCATION").split(";");
			  for(String path:downloadPaths){
				  //String fileExtension=downloadFile.split(".")[1];
				  String dest=ConfigLoader.getWmOutputWebServicePath()+"test.xls";
				  File file = new File(dest);
				  FileUtility.copyFile(path+File.separator+downloadFile, file.getAbsolutePath());
				  FileUtility.deleteFile(path+File.pathSeparator+downloadFile);
			  }
		  }catch (Exception e){}
		  //close download window
		  int winIndex=0;
		  String masterWindow="";
		  for (String handle : driver.getWindowHandles()) {
			  printMessage("**** CLOSING WINDOW  :"+timer.getPrintTime()+" with Handle="+handle);
			  if(winIndex>0){
			    driver.switchTo().window(handle).close();
			  }else{
				  masterWindow= handle;
			  }			 
			  winIndex++;
			}
		   driver.switchTo().window(masterWindow);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
  protected void modifyexcel(String rowColFilter){
	  FileUtility fl=new FileUtility();
	  String source=ConfigLoader.getWmOutputWebServicePath()+"test.xls";
	  File file = new File(source);
	  try {
		  String sourcefile=file.getCanonicalPath();	
		  String dest=ConfigLoader.getWmOutputWebServicePath()+"modifytest.xls";
		  File mfile = new File(dest);
		  String modifyfile=mfile.getCanonicalPath();
		  if(!mfile.exists()){
			  fl.copyFile(sourcefile, modifyfile);			  
		  }
		  ExcelSheet excl= new ExcelSheet(modifyfile);
		  excl.modifyMultiRowExcel(0,rowColFilter);
	  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }
  
 
  protected void uploadFile(WebElement elm){
	  Actions builder = new Actions(driver);

	  Action myAction = builder.click(elm)
	        .release()
	        .build();

	     myAction.perform();

	     Robot robot;
		try {
			robot = new Robot();
			 robot.keyPress(KeyEvent.VK_CONTROL);
		     robot.keyPress(KeyEvent.VK_V);
		     robot.keyRelease(KeyEvent.VK_V);
		     robot.keyRelease(KeyEvent.VK_CONTROL);
		     robot.keyPress(KeyEvent.VK_ENTER);
		     robot.keyRelease(KeyEvent.VK_ENTER);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
  }

   public void setIframeText(String iFrame, String elmId, String text){
	   String jscript=" var s = document.getElementById('"+iFrame+"');"+
	   				" s.contentDocument.write('"+text+"');";
	   
	   ((JavascriptExecutor) driver).executeScript(jscript);
		printMessage("**** Executed Java Script for Iframe= "+iFrame+" "+timer.getPrintTime()+jscript);
		
   }
   
   public boolean isEmptyValue(String val){
		if(val==null|| val.isEmpty()){
			return true;
		}
		return false;
	}
   
   

}



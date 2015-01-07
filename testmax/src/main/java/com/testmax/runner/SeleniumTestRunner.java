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
package com.testmax.runner;
	


import java.io.File;
import java.util.HashMap;
import java.util.List;	
import org.openqa.selenium.*;	
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.testmax.Exception.TestMaxException;
import com.testmax.framework.ConfigLoader;
import com.testmax.framework.WmLog;
import com.testmax.handler.SeleniumBaseHandler;


	public class SeleniumTestRunner extends SeleniumBaseHandler {
		public void setThreadIndex(String threadIndex ){
			this.threadIndex=threadIndex;
			this.isMultiThreaded=true;
		}
		public void setVarMap(HashMap<String,String> varmap){
			this.varmap=varmap;
		}
		
		
		protected WebElement executeClick(final WebElement control) { 
			
			 long start=System.currentTimeMillis();
			   WebElement element=null;
			   int addTimeOut=0;
			   int counter=0;
			   if(ConfigLoader.getConfig("SELENIUM_DRIVER")!=null 
	       			&&ConfigLoader.getConfig("SELENIUM_DRIVER").equalsIgnoreCase("ie")){
				   addTimeOut=addTimeOut+
				   new Integer(ConfigLoader.getConfig("SELENIUM_IE_EXTRA_TIME")==null?"500":ConfigLoader.getConfig("SELENIUM_IE_EXTRA_TIME"));
			   } 
			 
			   final int v_timeout= (this.timeout!=null &&!this.timeout.isEmpty()?new Integer(this.timeout):500)+addTimeOut;
			   
			   try{
			       //Set up a WebDriverWait to wait in second
			        WebDriverWait driverWait = new WebDriverWait(this.driver,v_timeout,500); 
		     
		            //Define an ExpectedCondition 
	                 ExpectedCondition<WebElement> expectedCondition = new   ExpectedCondition<WebElement>() { 
	 
                        public WebElement apply(WebDriver d) {                       
                      	 
                                try {
                                	setImpilicitTimeInMiliSec(v_timeout);
                                	while(!control.isDisplayed()){
                                		setImpilicitTimeInMiliSec(v_timeout);
                                	}
                                	control.click();
                                	return control;
                                } catch (TestMaxException t) {
                              	 	// TODO Auto-generated catch block
                 						t.printStackTrace();
                                } catch (NoSuchElementException ex) {} 
                                return null;	                              
                      	  };	                      	 
	                 }; 
			        //Find our web element
	                 while (element==null &&counter<10){
	                	 element = driverWait.until(expectedCondition);
	                	 counter++;
	                	 printMessage("CLICKING CONTROL BY TRIAL="+counter+" >> Time=("+(System.currentTimeMillis()-start)+") mili Sec '"+this.tagdesc+"' >> with Id="+this.controlIdentifier +addMessage());
	                 }
			        //this.setImplicitTime();
			     	this.timeTaken=this.timeTaken+System.currentTimeMillis()-start;
			     	printMessage("EXECUTED CONTROL >> Time=("+(System.currentTimeMillis()-start)+") mili Sec '"+this.tagdesc+"' >> with Id="+this.controlIdentifier +addMessage());
			        return element;
			       
			   }catch (Exception e){				  
				   this.printMessage("***FAILED TO EXECUTE CONTROL >> Time=("+(System.currentTimeMillis()-start)+") mili Sec '"+this.tagdesc+"' >> with Id="+this.controlIdentifier+" :"+(e.getMessage()!=null&&e.getMessage().length()>100?e.getMessage().substring(0, 100):e.getMessage()));
				   return null;
			   }
			  
		}
		protected WebElement executeWhile() throws TestMaxException{
			long executionTime=System.currentTimeMillis();
			List<WebElement> controls=null;
			try {
				this.setImpilicitTimeInMiliSec(new Integer(timeout));
				controls=this.getWebElements();
				for(WebElement control:controls){
					this.setImpilicitTimeInMiliSec(new Integer(timeout));					
					if(method.equalsIgnoreCase("click")){
						try{	
							printMessage("**** Clicking "+this.controlIdentifier);					
							control.click();
							printMessage("**** Clicked "+this.controlIdentifier +":"+ addMessage());
							//do not return to continue with while
						}catch (Exception e){
							return control;
							//WebElement target=executeClick(control);							
							//do not return to continue with while
						}
					}else if(method.equalsIgnoreCase("selectByValue")){
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						new Select(control).selectByValue(sendkeyValue);
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Selected Value= "+addMessage()+ " for Control="+this.controlIdentifier +":"+ addMessage());
						return control;
					}else if(method.equalsIgnoreCase("deselectByVisibleText")){
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						new Select(control).deselectByVisibleText(sendkeyValue);
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Deselected Value= "+addMessage()+ " for Control="+this.controlIdentifier +":"+ addMessage());
						return control;
					}else if(method.equalsIgnoreCase("selectByVisibleText")){
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						new Select(control).selectByVisibleText(sendkeyValue);
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Selected Value= "+addMessage()+ " for Control="+this.controlIdentifier +":"+ addMessage());
						return control;
					}else if(method.equalsIgnoreCase("clear")){
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						Thread.sleep(100);
						control.clear();
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Cleared "+this.controlIdentifier  +":"+ addMessage());
						return control;
					}else if(method.equalsIgnoreCase("movemouse")){
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						Thread.sleep(100);
						this.mouseMove(control);						
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Moved Mouse "+this.controlIdentifier  +":"+ addMessage());
						return control;
					}else if(method.equalsIgnoreCase("submit")){
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						Thread.sleep(100);
						control.submit();
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Submitted "+this.controlIdentifier +":"+ addMessage());
						return control;
					}else if(method.equalsIgnoreCase("asserttext")){
						try{
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
							while(!control.isDisplayed()){
								this.setImpilicitTimeInMiliSec(new Integer(timeout));
							}
							Thread.sleep(100);
							String actual=control.getText();
							this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
							printMessage("**** verifying Text Value "+actual+" for "+this.controlIdentifier +":"+ addMessage());
							if(actual!=null &&!actual.isEmpty() &&actual.contains(this.stripchar)){
								if(value!=null &&value.contains("@")){
									this.addTestExtract(value, actual);
								}else{
									this.assertText(value, actual);
								}								
								return control;
							}
						}catch(Exception e){}
					}else if(method.equalsIgnoreCase("sendKeys")){
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						Thread.sleep(100);
						control.sendKeys(sendkeyValue);
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Entered Value= "+addMessage()+ " for Control="+this.controlIdentifier +":"+ addMessage());		
						return control;				
					}
						
				}
			} catch (NumberFormatException e) {
				
				// TODO Auto-generated catch block
				 this.printCommand(" FAILED  with Exception for Command="+this.tagdesc +" for control with identifier="+this.controlIdentifier + " file="+addMessage() +"\n Message="+e.getMessage());
				 this.printCommand(WmLog.getStackTrace(e));
				 String v_msg=">>Dataset:"+this.threadIndex+" FAILED TO EXECUTE CONTROL IN "+addMessage() +" "+(e.getMessage()!=null&&e.getMessage().length()>100?e.getMessage().substring(0, 100):e.getMessage());
				 e.printStackTrace();
				 throw new TestMaxException(v_msg);
				 //handleTimeOut(v_msg);
				
			} catch (InterruptedException e) {
				
				// TODO Auto-generated catch block
				 this.printCommand(" FAILED  with Exception for Command="+this.tagdesc +" for control with identifier="+this.controlIdentifier + " file="+addMessage() +"\n Message="+e.getMessage());
				 this.printCommand(WmLog.getStackTrace(e));
				 String v_msg=">>Dataset:"+this.threadIndex+" FAILED TO EXECUTE CONTROL IN "+addMessage() +" "+(e.getMessage()!=null&&e.getMessage().length()>100?e.getMessage().substring(0, 100):e.getMessage());
				 e.printStackTrace();
				 throw new TestMaxException(v_msg);
				//handleTimeOut(v_msg);
				
			}catch (Exception e) {				
				// TODO Auto-generated catch block
				 this.printCommand(" FAILED  with Exception for Command="+this.tagdesc +" for control with identifier="+this.controlIdentifier + " file="+addMessage() +"\n Message="+e.getMessage());
				 this.printCommand(WmLog.getStackTrace(e));
				 String v_msg=">>Dataset:"+this.threadIndex+" FAILED TO EXECUTE CONTROL IN "+addMessage() +" "+(e.getMessage()!=null&&e.getMessage().length()>100?e.getMessage().substring(0, 100):e.getMessage());
				 e.printStackTrace();
				 throw new TestMaxException(v_msg);
				 //handleTimeOut(v_msg);
			}
			
			//if you are here then quit			
			
			return null;
		}
		
		protected  WebElement executeMobileAll() throws TestMaxException{
			long executionTime=System.currentTimeMillis();
			List<WebElement> controls=null;
			try {
				this.setImpilicitTimeInMiliSec(new Integer(timeout));
				
				//handle custom operations
				if(this.isBypassCustomMethod()){
					if(method.equalsIgnoreCase("get")){
						this.setImpilicitTimeInMiliSec(new Integer(timeout));						
						this.waitToPageLoad(new Integer(timeout));
						if(!url.contains("http") &&!url.contains("file:")){
							url=this.baseUrl+url;
						}
						url=url.replace("[env]", ConfigLoader.getConfig("QA_TEST_ENV"));
						
						if(this.baseUrl.contains("https")){
							url=url.replace("http:", "https:");
						}
						this.printCommand(this.tagdesc +" URL="+url);
						driver.get(url);	
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Executed Open URL= "+url+ " for Control="+this.controlIdentifier +":"+ addMessage());	
						return null;
					 }else if(method.equalsIgnoreCase("closewindow")){
						  this.driver.close();
						  return null;
					 }else if(method.equalsIgnoreCase("sleep")){
						  this.executeSleep();
						  return null;
					}else if(method.equalsIgnoreCase("executescript")){
						this.setImpilicitTimeInMiliSec(new Integer(timeout));						
						Thread.sleep(100);
						this.executeScript();
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier + "\n Script="+addMessage());
						printMessage("**** Executed Java Script= "+addMessage()+ " for Control="+this.controlIdentifier +":"+ addMessage());		
						return null;				
					}else if(method.equalsIgnoreCase("executeoperation")){
						this.setImpilicitTimeInMiliSec(new Integer(timeout));						
						Thread.sleep(100);
						this.executeOperation();
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier + " value="+addMessage());
						printMessage("**** Executed Java Script= "+addMessage()+ " for Control="+this.controlIdentifier +":"+ addMessage());		
						return null;				
					}else if(this.method.equalsIgnoreCase("urlextract")){
						String currenturl=this.driver.getCurrentUrl();
						if(currenturl!=null && !currenturl.isEmpty()){
							
							this.addTestExtract(this.value, currenturl);
							printMessage("#### Extracted parameters from URL="+currenturl );
						}
						return null;
					
					}else if(method.equalsIgnoreCase("closewindow")){
						  this.driver.close();
						  return null;
					}
				}
				controls=this.getWebElements();
				
				for(WebElement control:controls){
					this.setImpilicitTimeInMiliSec(new Integer(timeout));
					if(!isBypassDisplay()){
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
					}
					if(method.equalsIgnoreCase("click")){
						try{	
							printMessage("**** Clicking "+this.controlIdentifier);
							this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier );
							control.click();							
							printMessage("**** Clicked "+this.controlIdentifier +":"+ addMessage());
							return control;
						}catch (Exception e){
							WebElement target=executeClick(control);
							if(target!=null ){
								return target;
							}
						}
					}else if(method.equalsIgnoreCase("selectByValue")){
						this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier + " value="+addMessage());
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						new Select(control).selectByValue(sendkeyValue);
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Selected Value= "+addMessage()+ " for Control="+this.controlIdentifier +":"+ addMessage());
						return control;	
					}else if(method.equalsIgnoreCase("deselectByVisibleText")){
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						new Select(control).deselectByVisibleText(sendkeyValue);
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Deselected Value= "+addMessage()+ " for Control="+this.controlIdentifier +":"+ addMessage());
						return control;
					}else if(method.equalsIgnoreCase("selectByVisibleText")){
						this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier + " value="+addMessage());
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						new Select(control).selectByVisibleText(sendkeyValue);
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Selected Value= "+addMessage()+ " for Control="+this.controlIdentifier +":"+ addMessage());
						return control;
					}else if(method.equalsIgnoreCase("clear")){
						try{
							this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier );
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
							while(!control.isDisplayed()){
								this.setImpilicitTimeInMiliSec(new Integer(timeout));
							}
							Thread.sleep(100);
							if(control.isEnabled())
							control.clear();
							this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
							printMessage("**** Cleared "+this.controlIdentifier  +":"+ addMessage());
							return control;
						}catch(Exception e){
							this.clearElement();
							Thread.sleep(500);
							return control;
						}
					}else if(method.equalsIgnoreCase("movemouse")){
						this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier );
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						Thread.sleep(100);
						this.mouseMove(control);						
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Moved Mouse "+this.controlIdentifier  +":"+ addMessage());
						return control;
					}else if(method.equalsIgnoreCase("download")){
						this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier );
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						Thread.sleep(100);
						this.downloadFile(control);	
						if(this.value!=null &&!this.value.isEmpty() &&this.value.contains("[")){
							this.modifyexcel(this.value);
						}
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Download File "+this.controlIdentifier  +":"+ addMessage());
						return control;
					}else if(method.equalsIgnoreCase("uploadfile")){
						
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						Thread.sleep(100);
					    String dest=ConfigLoader.getWmOutputWebServicePath()+"modifytest.xls";
					    File mfile = new File(dest);
					    String modifyfile=mfile.getCanonicalPath();
					    this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier + " file="+modifyfile);
						control.sendKeys(modifyfile);
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Entered Value= "+addMessage()+ " for Control="+this.controlIdentifier +":"+ addMessage());		
						return control;	
					}else if(method.equalsIgnoreCase("submit")){
						this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier);
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						Thread.sleep(100);
						control.submit();
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Submitted "+this.controlIdentifier +":"+ addMessage());
						return control;
					}else if(method.equalsIgnoreCase("asserttext")){
						try{
							this.setImpilicitTimeInMiliSec(new Integer(timeout));							
							Thread.sleep(100);
							String actual=control.getText();
							if(actual==null||actual.isEmpty()){
								actual=control.getAttribute("value");
								printMessage("**** Control won't have Text Value. Extracted Value using attribute Name=value. Found value= "+actual+" for "+this.controlIdentifier +":"+ addMessage());
							}
							this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
							this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier + " value="+addMessage() +", actual="+actual);
							printMessage("**** verifying Text Value= "+actual+" for "+this.controlIdentifier +":"+ addMessage());
							if(actual!=null &&!actual.isEmpty()){
								if(value!=null &&value.contains("@")){
									this.addTestExtract(value, actual);
								}else{
									this.assertText(value, actual);
								}								
								return control;
							}
						}catch(Exception e){}
					}else if(method.equalsIgnoreCase("sendKeys")){
						this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier + " value="+addMessage());
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						Thread.sleep(100);
						control.sendKeys(sendkeyValue);
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Entered Value= "+addMessage()+ " for Control="+this.controlIdentifier +":"+ addMessage());		
						return control;				
					}
						
				}
			} catch (NumberFormatException e) {
				
				 this.printCommand(" FAILED  with Exception for Command="+this.tagdesc +" for control with identifier="+this.controlIdentifier + " file="+addMessage() +"\n Message="+e.getMessage());
				 this.printCommand(WmLog.getStackTrace(e));
				// TODO Auto-generated catch block
				 String v_msg=">>Dataset:"+this.threadIndex+" FAILED TO EXECUTE CONTROL IN "+addMessage() +" "+(e.getMessage()!=null&&e.getMessage().length()>100?e.getMessage().substring(0, 100):e.getMessage());
				 e.printStackTrace();
				 throw new TestMaxException(v_msg);
				 //handleTimeOut(v_msg);
			} catch (InterruptedException e) {
				
				// TODO Auto-generated catch block
				this.printCommand(" FAILED  with Exception for Command="+this.tagdesc +" for control with identifier="+this.controlIdentifier + " file="+addMessage() +"\n Message="+e.getMessage());
				this.printCommand(WmLog.getStackTrace(e));
				 String v_msg=">>Dataset:"+this.threadIndex+" FAILED TO EXECUTE CONTROL IN "+addMessage() +" "+(e.getMessage()!=null&&e.getMessage().length()>100?e.getMessage().substring(0, 100):e.getMessage());
				 e.printStackTrace();
				 throw new TestMaxException(v_msg);
				 //handleTimeOut(v_msg);
				
			}catch (Exception e) {				
				// TODO Auto-generated catch block
				this.printCommand(" FAILED  with Exception for Command="+this.tagdesc +" for control with identifier="+this.controlIdentifier + " file="+addMessage() +"\n Message="+e.getMessage());
				this.printCommand(WmLog.getStackTrace(e));
				 String v_msg=">>Dataset:"+this.threadIndex+" FAILED TO EXECUTE CONTROL IN "+addMessage() +" "+(e.getMessage()!=null&&e.getMessage().length()>100?e.getMessage().substring(0, 100):e.getMessage());
				 e.printStackTrace();
				 throw new TestMaxException(v_msg);
				 //handleTimeOut(v_msg);
			}
			
			//if you are here then quit
			//driver.quit();
			
			return null;
		}
		
		protected  WebElement executeAll() throws TestMaxException{
			long executionTime=System.currentTimeMillis();
			List<WebElement> controls=null;
			try {
				this.setImpilicitTimeInMiliSec(new Integer(timeout));
				
				//handle custom operations
				if(this.isBypassCustomMethod()){
					if(method.equalsIgnoreCase("get")){
						this.setImpilicitTimeInMiliSec(new Integer(timeout));						
						this.waitToPageLoad(new Integer(timeout));
						if(!url.contains("http") &&!url.contains("file:")){
							url=this.baseUrl+url;
						}
						url=url.replace("[env]", ConfigLoader.getConfig("QA_TEST_ENV"));
						
						if(this.baseUrl.contains("https")){
							url=url.replace("http:", "https:");
						}
						this.printCommand(this.tagdesc +" URL="+url);
						driver.get(url);	
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Executed Open URL= "+url+ " for Control="+this.controlIdentifier +":"+ addMessage());	
						return null;
					 }else if(method.equalsIgnoreCase("closewindow")){
						  this.driver.close();
						  return null;
					 }else if(method.equalsIgnoreCase("sleep")){
						  this.executeSleep();
						  return null;
					}else if(method.equalsIgnoreCase("executescript")){
						this.setImpilicitTimeInMiliSec(new Integer(timeout));						
						Thread.sleep(100);
						this.executeScript();
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier + "\n Script="+addMessage());
						printMessage("**** Executed Java Script= "+addMessage()+ " for Control="+this.controlIdentifier +":"+ addMessage());		
						return null;				
					}else if(method.equalsIgnoreCase("executeoperation")){
						this.setImpilicitTimeInMiliSec(new Integer(timeout));						
						Thread.sleep(100);
						this.executeOperation();
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier + " value="+addMessage());
						printMessage("**** Executed Java Script= "+addMessage()+ " for Control="+this.controlIdentifier +":"+ addMessage());		
						return null;				
					}else if(this.method.equalsIgnoreCase("urlextract")){
						String currenturl=this.driver.getCurrentUrl();
						if(currenturl!=null && !currenturl.isEmpty()){
							
							this.addTestExtract(this.value, currenturl);
							printMessage("#### Extracted parameters from URL="+currenturl );
						}
						return null;
						
						
					}
				}
				controls=this.getWebElements();
				
				for(WebElement control:controls){
					this.setImpilicitTimeInMiliSec(new Integer(timeout));
					if(!isBypassDisplay()){
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
					}
					if(method.equalsIgnoreCase("click")){
						try{	
							printMessage("**** Clicking "+this.controlIdentifier);
							this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier );
							control.click();							
							printMessage("**** Clicked "+this.controlIdentifier +":"+ addMessage());
							return control;
						}catch (Exception e){
							WebElement target=executeClick(control);
							if(target!=null ){
								return target;
							}
						}
					}else if(method.equalsIgnoreCase("selectByValue")){
						this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier + " value="+addMessage());
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						new Select(control).selectByValue(sendkeyValue);
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Selected Value= "+addMessage()+ " for Control="+this.controlIdentifier +":"+ addMessage());
						return control;	
					}else if(method.equalsIgnoreCase("deselectByVisibleText")){
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						new Select(control).deselectByVisibleText(sendkeyValue);
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Deselected Value= "+addMessage()+ " for Control="+this.controlIdentifier +":"+ addMessage());
						return control;
					}else if(method.equalsIgnoreCase("selectByVisibleText")){
						this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier + " value="+addMessage());
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						new Select(control).selectByVisibleText(sendkeyValue);
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Selected Value= "+addMessage()+ " for Control="+this.controlIdentifier +":"+ addMessage());
						return control;
					}else if(method.equalsIgnoreCase("clear")){
						try{
							this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier );
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
							while(!control.isDisplayed()){
								this.setImpilicitTimeInMiliSec(new Integer(timeout));
							}
							Thread.sleep(100);
							if(control.isEnabled())
							control.clear();
							this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
							printMessage("**** Cleared "+this.controlIdentifier  +":"+ addMessage());
							return control;
						}catch(Exception e){
							this.clearElement();
							Thread.sleep(500);
							return control;
						}
					}else if(method.equalsIgnoreCase("movemouse")){
						this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier );
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						Thread.sleep(100);
						this.mouseMove(control);						
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Moved Mouse "+this.controlIdentifier  +":"+ addMessage());
						return control;
					}else if(method.equalsIgnoreCase("download")){
						this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier );
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						Thread.sleep(100);
						this.downloadFile(control);	
						if(this.value!=null &&!this.value.isEmpty() &&this.value.contains("[")){
							this.modifyexcel(this.value);
						}
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Download File "+this.controlIdentifier  +":"+ addMessage());
						return control;
					}else if(method.equalsIgnoreCase("uploadfile")){
						
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						Thread.sleep(100);
					    String dest=ConfigLoader.getWmOutputWebServicePath()+"modifytest.xls";
					    File mfile = new File(dest);
					    String modifyfile=mfile.getCanonicalPath();
					    this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier + " file="+modifyfile);
						control.sendKeys(modifyfile);
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Entered Value= "+addMessage()+ " for Control="+this.controlIdentifier +":"+ addMessage());		
						return control;	
					}else if(method.equalsIgnoreCase("submit")){
						this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier);
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						Thread.sleep(100);
						control.submit();
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Submitted "+this.controlIdentifier +":"+ addMessage());
						return control;
					}else if(method.equalsIgnoreCase("asserttext")){
						try{
							this.setImpilicitTimeInMiliSec(new Integer(timeout));							
							Thread.sleep(100);
							String actual=control.getText();
							if(actual==null||actual.isEmpty()){
								actual=control.getAttribute("value");
								printMessage("**** Control won't have Text Value. Extracted Value using attribute Name=value. Found value= "+actual+" for "+this.controlIdentifier +":"+ addMessage());
							}
							this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
							this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier + " value="+addMessage() +", actual="+actual);
							printMessage("**** verifying Text Value= "+actual+" for "+this.controlIdentifier +":"+ addMessage());
							if(actual!=null &&!actual.isEmpty()){
								if(value!=null &&value.contains("@")){
									this.addTestExtract(value, actual);
								}else{
									this.assertText(value, actual);
								}								
								return control;
							}
						}catch(Exception e){}
					}else if(method.equalsIgnoreCase("sendKeys")){
						this.printCommand(this.tagdesc +" for control with identifier="+this.controlIdentifier + " value="+addMessage());
						this.setImpilicitTimeInMiliSec(new Integer(timeout));
						while(!control.isDisplayed()){
							this.setImpilicitTimeInMiliSec(new Integer(timeout));
						}
						Thread.sleep(100);
						control.sendKeys(sendkeyValue);
						this.timeTaken=this.timeTaken+(System.currentTimeMillis()-executionTime);
						printMessage("**** Entered Value= "+addMessage()+ " for Control="+this.controlIdentifier +":"+ addMessage());		
						return control;				
					}
						
				}
			} catch (NumberFormatException e) {
				
				 this.printCommand(" FAILED  with Exception for Command="+this.tagdesc +" for control with identifier="+this.controlIdentifier + " file="+addMessage() +"\n Message="+e.getMessage());
				 this.printCommand(WmLog.getStackTrace(e));
				// TODO Auto-generated catch block
				 String v_msg=">>Dataset:"+this.threadIndex+" FAILED TO EXECUTE CONTROL IN "+addMessage() +" "+(e.getMessage()!=null&&e.getMessage().length()>100?e.getMessage().substring(0, 100):e.getMessage());
				 e.printStackTrace();
				 throw new TestMaxException(v_msg);
				 //handleTimeOut(v_msg);
			} catch (InterruptedException e) {
				
				// TODO Auto-generated catch block
				this.printCommand(" FAILED  with Exception for Command="+this.tagdesc +" for control with identifier="+this.controlIdentifier + " file="+addMessage() +"\n Message="+e.getMessage());
				this.printCommand(WmLog.getStackTrace(e));
				 String v_msg=">>Dataset:"+this.threadIndex+" FAILED TO EXECUTE CONTROL IN "+addMessage() +" "+(e.getMessage()!=null&&e.getMessage().length()>100?e.getMessage().substring(0, 100):e.getMessage());
				 e.printStackTrace();
				 throw new TestMaxException(v_msg);
				 //handleTimeOut(v_msg);
				
			}catch (Exception e) {				
				// TODO Auto-generated catch block
				this.printCommand(" FAILED  with Exception for Command="+this.tagdesc +" for control with identifier="+this.controlIdentifier + " file="+addMessage() +"\n Message="+e.getMessage());
				this.printCommand(WmLog.getStackTrace(e));
				 String v_msg=">>Dataset:"+this.threadIndex+" FAILED TO EXECUTE CONTROL IN "+addMessage() +" "+(e.getMessage()!=null&&e.getMessage().length()>100?e.getMessage().substring(0, 100):e.getMessage());
				 e.printStackTrace();
				 throw new TestMaxException(v_msg);
				 //handleTimeOut(v_msg);
			}
			
			//if you are here then quit
			//driver.quit();
			
			return null;
		}
		protected synchronized WebElement execute() throws TestMaxException{
			WebElement complete=null;
			/*if(this.isWhileLoop){
				complete=this.executeWhile();
			}else{
				complete=this.executeAll();
			}
			*/
			complete=this.executeAll();
			return complete;
		}
		
		
		
		private boolean scrollAndClick(WebElement control, int position){
			
			try{
				((JavascriptExecutor) driver).executeScript("window.scrollTo("+position+","+control.getLocation().y+")");
				printMessage("**** Clicking "+this.controlIdentifier+": Elapse Time:"+this.timeTaken+" :"+this.addMessage());
				control.click();
				printMessage("**** Clicked "+this.controlIdentifier+":"+this.addMessage());				
				
			}catch (Exception e){
				
				printMessage("**** Failed To Click "+this.controlIdentifier +" -"+e.getMessage().substring(0, 150));	
				return false;
			}
			return true;
		}
	 
		
	
	

}

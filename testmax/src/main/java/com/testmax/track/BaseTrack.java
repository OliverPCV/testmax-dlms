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
package com.testmax.track;


import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.testmax.handler.SeleniumBaseHandler;


public abstract class BaseTrack {
	protected WebDriver driver=null;
	protected SeleniumBaseHandler handler=null;
	public abstract void setSeleniumRunner(SeleniumBaseHandler handler);
	
	public void setImpilicitTimeInMiliSec(int timeout) {
		try {
			this.handler.getDriver().manage().timeouts().implicitlyWait(timeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}
	public boolean isElementDisplayed(By by) {
		try {
			List<WebElement> elms=driver.findElements(by);
			
			for(WebElement elm:elms){
				if(elm!=null  &&elm.isDisplayed() ){
					return true;
				}
			}
		} catch (NoSuchElementException e) {
			return false;
		}
		return false;
	}
	
	private boolean isElementVisible(String id){
		 try{ 
			 
			   ((JavascriptExecutor) driver).executeScript(" var elm = document.getElementById('"+id+"');  " +
			   		"var status = elm.offsetWidth > 0 || elm.offsetHeight > 0;  " +
			   				"elm.setAttribute(\"status\",state);" );
			   WebElement elm=driver.findElement(By.id(id));
			   String status=elm.getAttribute("status");
			   if(status.equalsIgnoreCase("true")){
	        	return true;
			   } 
	        }catch(Exception e){        	
	        }
	        return false;
			  
	}
	 	
}


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


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.dom4j.Element;

import com.testmax.framework.BasePage;
import com.testmax.framework.ConfigLoader;
import com.testmax.framework.PageURL;
import com.testmax.framework.WmLog;
import com.testmax.util.ItemUtility;

public class ExecutionEngin {
    private ConfigLoader config=null;
    private HashMap<String, PageURL> moduleMap= null;
    private HashMap <String, String> moduleClassMap= null;
    private HashMap <String, HashMap> testDataSet= null;
    private HashMap <String, HashMap> globalDataSet= null;
    private static Element currentTest=null;
    private static String dyanamicPath=new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date(System.currentTimeMillis()));;
    
    public ExecutionEngin(ConfigLoader config){
        this.config=config;
        this.moduleMap=config.getModuleMap();
        this.moduleClassMap=config.getModuleClassMap();
        this.testDataSet=config.getTestDataMap();
        this.globalDataSet=config.getGlobalDataMap();
    }
    
    public void execute(){
       
        //Extract each test data file from dataset
        for(String testfile: this.testDataSet.keySet()){
            HashMap <String, Element> testDataFileSet=this.testDataSet.get(testfile);
            
            //Extract each test case
            for(String testcase: testDataFileSet.keySet()){
            	WmLog.initCoreLogger(this.config.getWmOutputLogPath() + testcase+"_"+System.currentTimeMillis()+".log",testcase);        
                Element test=testDataFileSet.get(testcase);
                this.currentTest=test;
                executeTest(test);               
                dyanamicPath=new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date(System.currentTimeMillis()));
                WmLog.closeLogger();  
            }
           
         }
            
        }
    public static Element getCurrentTest(){
    	return currentTest;
    }
    
    public static String getDynamicPath(){  
    	
    	return dyanamicPath;
    }
    public static void setDynamicPath(String path){  
    	
    	 dyanamicPath=path;
    }
    private void executeTest(Element test) {
    	
        String testName=test.attributeValue("name");
        String page=test.attributeValue("page");
        String pageClass=this.moduleClassMap.get(page);
        String error="ERROR >>>> Please verify name attribute of your page tag in TestSuite configuration under test suite folder.\n"+
                "\t\t\t Please do not use duplicate page name or WRONG page="+page; 
        if(ItemUtility.isEmptyValue(pageClass)){
        	pageClass="com.testmax.handler.ApiHandler";
        }
        BasePage pageObj;
        try {
            pageObj = (BasePage)Class.forName(pageClass).newInstance();
            pageObj.setupTestData(test, this.moduleMap.get(page));
            pageObj.execute();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
        	      	
        	 WmLog.printMessage(error);        	
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (NullPointerException e) {
             WmLog.printMessage(error);  
             e.printStackTrace();
        }
   
    }
}

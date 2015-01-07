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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Element;

import com.testmax.util.ItemUtility;


public abstract class BaseHandler {
	
	protected  String handlerName="step1";	
	
	protected static String testClass=null;
	
	protected static String packageName=null;
	
	protected static List<String> testMethods=null;
	
	protected static HashMap<String,String> testResult=null;
	
	protected static HashMap<String,String> items=null;
	
	protected Element threadDataElement=null;
	
	protected Element validatorElm=null;
	
	protected static String actionName=null;
	
	protected String threadIndex=null;
	
	protected String validatorName=null;
	
	protected List<Element> testPackage=null;
	
	protected volatile  List<Element> tagLib=null;
	
	protected static  List<Element> defaultTagLib=null;
	
	protected static HashMap<String,List<Element>> injectors=null;
	
	private static HashMap<String, Connection> dbManager= null;
	
	protected static HashMap<String,String> varmap=null;
	
	protected static HashMap<String, HashMap<String,String>> threadData=new HashMap<String, HashMap<String,String>>();
	
	protected static HashMap<String, HashMap<String,String>> threadResult=new HashMap<String, HashMap<String,String>>();
	
	protected static ActionHandler exeProc=null;
	
	protected String logPath=null;
	
	protected List<Element> vAssert=null;
	
	public Boolean isMultiThreaded=false;
	
	/*
	 * Store all result dataset of SQL Queries from SQLTester
	 * can be accessed buy using their name given in the SQL configuration
	 */
	protected  HashMap<String, Object> resultDataSet= null;
	
	/*
	 * Store all result parameters and their data types information
	 */
	protected HashMap<String, String> resultParamSet= null;
	
	/* Test name in the configuration file */
	protected static String testConfigName=null;
	
	protected String elmConfigXml;
	
	protected  ItemUtility iu;
	
	public BaseHandler(){	
		synchronized(this){
			this.iu= new ItemUtility();
			this.items= new HashMap<String,String>();
			this.testPackage= new ArrayList();	
			this.tagLib=new ArrayList();
			this.defaultTagLib=new ArrayList();
			this.injectors=new HashMap<String,List<Element>>();
			this.testResult=new HashMap<String,String>();
		}
	}
	
	public  String getHandlerName() {
		return handlerName;
	}

	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}

	private void setDbManager(HashMap<String, Connection> dbManager){
		this.dbManager=dbManager;
	}	
	public void setActionName(String name){
		this.actionName=name;
	}
	
	public static String getActionName(){
		return(actionName);
	}
	public void setValidatorName(String name){
		this.validatorName=name;
	}
	public void setTestConfigName(String name){
		this.testConfigName=name;
	}
	
	public void setThreadDataElement(Element data){
		this.threadDataElement=data;
	}
	public Element getThreadDataElement(){
		return(this.threadDataElement);
	}
	public void setValidatorElement(Element validatorElm){
		this.validatorElm=validatorElm;
	}
	public Element getValidatorElement(){
		return(this.validatorElm);
	}
	public void setThreadIndex(String threadIndex){
		this.threadIndex=threadIndex;
	}
	public String getThreadIndex(){
		return(this.threadIndex);
	}
	public String getHandlerId(){
		return(this.threadIndex+this.handlerName);
	}
	public void setElementConfigXML(String xml){
		this.elmConfigXml=xml;
	}
	public void setAsserts(List<Element> vAssert){
		this.vAssert=vAssert;
	}
	
	public  List<Element> getAsserts(){
		return(vAssert);
	}
	public void setVarMap(HashMap<String, String> varmap){
		this.varmap=varmap;
	}
	
	public static HashMap<String, String> getVarMap(){
		return(varmap);
	}
	public  HashMap<String, String> getVarMapByThreadIndex(String threadIndex){
		return(threadData.get(threadIndex));
	}
	
	
	public void  registerThreadData(String threadIndex ,HashMap<String, String> datamap){
		
		threadData.put(threadIndex, datamap);
	}	 
	
	public static String getDeclaredVariable(String threadIndex, String key){
		return(threadData.get(threadIndex).get(key));
	}
	
	public static String getDeclaredVariable(String key){
		if(varmap==null)
			return "";
		return(varmap.get(key));
	}
	public  List<Element> getTagLibraries(){
		return(tagLib);
	}
	
	public static List<Element> getDefaultTagLibraries(){
		return(defaultTagLib);
	}
	public static HashMap<String,List<Element>> getInjectors(){
		return(injectors);
	}
	public void setExecuteProc(ActionHandler proc){
		this.exeProc=proc;
		setResultDataSet(proc.resultDataSet);
		setDbManager(proc.dbManager);
		setResultParamSet(proc.resultParamSet);
		setActionName(proc.getActionName());
	}
	
	public ActionHandler getExecuteProc(){
		return this.exeProc;
	}
	
	private void setResultDataSet(HashMap<String, Object> resultDataSet){
		this.resultDataSet=resultDataSet;
	}
	
	
	private void setResultParamSet(HashMap<String, String> resultParamSet){
		this.resultParamSet=resultParamSet;
	}	
	
	public  HashMap<String, String>  getTestResult(){
		return (testResult);
	}	
	public static void addTestResult(String key, String value){		
		exeProc.resultDataSet.put(key, value);
		
	}
	public static String getTestResult(String threadIndex,String key){
		if(threadResult.get(threadIndex)!=null){
			return(threadResult.get(threadIndex).get(key).toString());
		}
		return null;
	}
	
	public static synchronized void addTestResult(String handlerId,String key, String value){
		HashMap<String,String> result=threadResult.get(handlerId);
		if(result==null){
			result=new HashMap<String,String>();
		}
		result.put(key, value);
		threadResult.put(handlerId, result);
		addTestResult(key, value);
	}
	
	public void addItem(String key, String value){
		items.put(key, value);
	}
	
	public void addTestPackage( Element value){
		testPackage.add(value);
	}
	
	public static Connection getConnection(String dbTag){
		return dbManager.get(dbTag);
	}
	
	
	//Call this method to start the service and execute	
	protected abstract void handleService();
	
	

}

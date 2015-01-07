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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.dom4j.Element;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestNG;
import org.testng.reporters.*;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import com.testmax.framework.ConfigLoader;
import com.testmax.util.ClassLoaderUtil;
import com.testmax.util.FileUtility;


public class TestNGHandler extends BaseHandler{
   
	public TestNGHandler(){
		super();		
	}
	@Override
	protected void handleService() {
		// TODO Auto-generated method stub
		this.defaultTestNgRunner();
		
	}
	
	protected void defaultTestNgRunner() {

		
		 //ItemUtility iu= new ItemUtility();
			try {
				 this.logPath=ConfigLoader.getWmOutputWebServicePath()+File.separator+"testng"+File.separator;
				 FileUtility.createDir(this.logPath);
				for (Element key:this.testPackage){
					
					packageName=key.attributeValue("name");
					List<Element> tests= key.elements();
					for (Element test:tests){
						if(test.getName().equalsIgnoreCase("testClass")){
							this.testClass=test.attributeValue("class");
							List<Element> injectorlist= new ArrayList();
							List<Element> vars=test.elements();
							List<Element> variables=new ArrayList();
							List<Element> items=new ArrayList();
							for(Element variable:vars){
								if(variable.getName().equalsIgnoreCase("variables")){
									List <Element> elList=variable.elements();
									for(Element el:elList){
										if(el.getName().equalsIgnoreCase("var")){
											variables.add(el);
										}else if(el.getName().equalsIgnoreCase("item")){
											items.add(el);
										}
									}
									
								}
								if(variable.getName().equalsIgnoreCase("taglib")){
									tagLib.add(variable);
								}
								if(variable.getName().equalsIgnoreCase("injectors")){
									injectorlist.add(variable);
									injectors.put(this.handlerName,injectorlist);
								}
								
							}
							//set class level variables into varmap
							iu.setVariablesValue(variables, this.resultParamSet, this.resultDataSet, this.varmap);
							iu.setItemValue(items,  this.varmap);
						}
						if(this.testClass.equals("*")){
							ClassLoaderUtil cUtl= new ClassLoaderUtil();
							Class[] classes=cUtl.getClasses(packageName);
							for(Class currentTest:classes){
								this.testClass=currentTest.getSimpleName();
								//this.testClass=currentTest.getCanonicalName();
								this.executeTest();
							}
						}else{
							this.executeTest();
						}
					}
				
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		protected  void executeTest() throws FileNotFoundException{
			String dataset="";
			//fillup dataset
			for (Object key: varmap.keySet()){
				dataset+= key.toString()+"="+varmap.get(key)+" ,";
			}
			PrintStream oldoutps = System.out; //get the current output stream
			this.testMethods= new ArrayList();
			FileOutputStream outfos = new FileOutputStream(this.logPath+this.testClass+"_"+this.actionName.replace(" ", "_")+"_"+System.currentTimeMillis()+".log"); //create //new output stream
			
			PrintStream newoutps = new PrintStream(outfos); //create new output //stream
	
			System.setOut(newoutps); //set the output stream
	
			System.out.println("TESTNG TEST STARTED.");
			System.out.println("");
			System.out.println("");
			System.out.println("Dataset Executed:" );
			System.out.println("");
			System.out.println(dataset);
			System.out.println("");
			System.out.println("");
			DotTestListener listener= new DotTestListener();
			TestNGHandler.suite(listener);
			
			List<ITestContext> contexts=listener.getTestContexts();
			
			for(ITestContext context:contexts){	
				
				Set<ITestResult> methods=context.getPassedTests().getAllResults();
				for(ITestResult method:methods){
					String message="PASSED TEST:"+method.getMethod().getMethodName()+"("+packageName+"."+testClass+"), Matching Expected Result in TESTNG ASSERTs Status="+method.isSuccess() ;
					this.testResult.put(packageName+"."+testClass+"."+method.getMethod().getMethodName()+System.currentTimeMillis(), message);
					System.out.println(message);
					
				}
				
				methods=context.getFailedTests().getAllResults();
				for(ITestResult method:methods){
					String message="FAILED TEST:"+method.getMethod().getMethodName()+"("+packageName+"."+testClass+"), Not matching Expected Results in TESTNG ASSERTs, Status="+method.getStatus() 
					+ " ,Failed Message:"+method.getThrowable().getMessage();
					this.testResult.put(packageName+"."+testClass+"."+method.getMethod().getMethodName()+System.currentTimeMillis(), message);
					System.out.println(message);
				}
				
				methods=context.getSkippedTests().getAllResults();
				for(ITestResult method:methods){
					String message="SKIPPED TEST:"+method.getMethod().getMethodName()+"("+packageName+"."+testClass+"), Skipped TESTNG Tests Status="+method.getStatus();
					this.testResult.put(packageName+"."+testClass+"."+method.getMethod().getMethodName()+System.currentTimeMillis(), message);
					System.out.println(message);
				}
			}				
		
				
			System.out.println("TESTNG TEST COMPLETED.");
			System.out.println("");
			System.setOut(oldoutps); //for resetting the output stream
			
		}
		
		protected static DotTestListener suite(DotTestListener listener){
			XmlSuite suite = new XmlSuite();
			suite.setName(testClass);			
			Class unitTestClass;
			try {
				unitTestClass = Class.forName(packageName+"."+testClass);				
				Method[] methods=unitTestClass.getDeclaredMethods();
				for( Method m:methods){
					String methodName=m.getName();
					boolean isAnnoted=m.isAnnotationPresent((Class<? extends Annotation>) org.testng.annotations.Test.class);
					 if (isAnnoted || methodName.substring(0, 4).equalsIgnoreCase("test")){
						 testMethods.add(methodName);
						 System.out.println(m.getName());
					 }
					
				}
				XmlTest test = new XmlTest(suite);
				test.setName(testClass);
				List<XmlClass> classes = new ArrayList<XmlClass>();
				classes.add(new XmlClass(packageName+"."+testClass));
				test.setXmlClasses(classes) ;
				List<XmlSuite> suites = new ArrayList<XmlSuite>();
				suites.add(suite);
				TestNG tng = new TestNG();
				tng.setXmlSuites(suites);				
				tng.addListener(listener);
				tng.run();
				
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return listener;

		}
		

}

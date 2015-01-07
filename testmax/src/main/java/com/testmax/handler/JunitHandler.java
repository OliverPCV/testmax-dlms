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
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import org.dom4j.Element;
import com.testmax.framework.ConfigLoader;
import com.testmax.runner.SeleniumTestRunner;
import com.testmax.util.FileUtility;
import junit.framework.Test;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;


public class JunitHandler extends BaseHandler{
	
	
	public JunitHandler(){	
			super();		
		
	}
	@Override
	public void handleService() {
		// TODO Auto-generated method stub	
			this.tagLib=new ArrayList();
			this.defaultJunitRunner();
		
		
	}
	protected void defaultJunitRunner() {
		
		PrintStream oldoutps = System.out; //get the current output stream
		//ItemUtility iu= new ItemUtility();
		HashMap<String,String> localvarmap=this.getVarMapByThreadIndex(this.threadIndex);;
			try {
				 String path=ConfigLoader.getWmOutputWebServicePath()+File.separator+"logs"+File.separator+this.getActionName().replaceAll(" ", "_")+File.separator;
				 FileUtility.createDir(path);
				
				
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
							iu.setVariablesValue(variables, this.resultParamSet, this.resultDataSet,localvarmap);
							iu.setItemValue(items,  localvarmap);
							this.registerThreadData(this.threadIndex, localvarmap);
						}
						
						this.testMethods= new ArrayList();
						
						TestResult result= new TestResult();
						if(this.isMultiThreaded){
							synchronized(this){
								final SeleniumTestRunner runner=new SeleniumTestRunner();
								runner.setThreadIndex(this.threadIndex);
								runner.setBaseHandler(this);
								runTest(runner);
							}
							
						}else{
							this.defaultTagLib=this.tagLib;
							String driver=localvarmap.get("driver");
							if (driver==null||driver.isEmpty()){
								driver=ConfigLoader.getConfig("SELENIUM_DRIVER").toLowerCase();
							}
							FileOutputStream outfos = new FileOutputStream(path+this.actionName.replace(" ", "_")
									+"_"+driver+"_"+this.threadIndex+"_"+
									+System.currentTimeMillis()+".log"); //create //new output stream
							
							PrintStream newoutps = new PrintStream(outfos); //create new output //stream					
							System.setOut(newoutps); //set the output stream							
							System.out.println("JUNIT TEST STARTED.");
							System.out.println("");
							System.out.println("");
							System.out.println("Dataset Executed:" );
							System.out.println("");
							System.out.println(localvarmap.values());
							
							if(testClass!=null &&testClass.contains("SeleniumTestRunner")){
								final SeleniumTestRunner runner=new SeleniumTestRunner();
								runner.setThreadIndex(this.threadIndex);
								runner.setBaseHandler(this);
								runTest(runner);
							}else{
								suite().run(result);
								
							}
							System.out.println("Run:"+result.runCount());
							System.out.println("PASSED:"+(result.runCount()-result.failureCount()-result.errorCount()));
							System.out.println("FAILED:"+result.failureCount());
							System.out.println("ERROR:"+result.errorCount());
						}
											
						
						Enumeration<TestFailure> em=result.failures();
						
						while (em.hasMoreElements()){
							TestFailure tm=em.nextElement();
							int idx=tm.failedTest().toString().indexOf("(");
							String testName=tm.failedTest().toString().substring(0, idx);
							String message="FAILED TEST:"+tm.failedTest().toString()+ tm.exceptionMessage();
							this.testResult.put(testName, message);
							System.out.println(message);
						}
						
						for (String method: testMethods){
							String msg=this.testResult.get(method);
							if(msg==null ||msg.isEmpty()){
								msg="PASSED TEST:"+method+"("+packageName+"."+testClass+"), Matched Expected Results in JUNIT ASSERTs";
								this.testResult.put(method, msg);
								System.out.println(msg);
							}
						}
							
						System.out.println("JUNIT TEST COMPLETED.");
						System.out.println("");
						System.setOut(oldoutps); //for resetting the output stream
					
					}
				
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
	
	protected  TestSuite suite(){
		
		TestSuite suite = new TestSuite("RUNNING TEST CONFIG: -"+testConfigName);
		Class junitTestClass;		
		try {
			junitTestClass = Class.forName(packageName+"."+testClass);				
			Method[] methods=junitTestClass.getDeclaredMethods();
			for( Method m:methods){
				String methodName=m.getName();
				
				boolean isAnnoted=m.isAnnotationPresent((Class<? extends Annotation>) org.junit.Test.class);
				 if (isAnnoted || methodName.substring(0, 4).equalsIgnoreCase("test")){
					 testMethods.add(methodName);
					 System.out.println(m.getName());
				 }
				
			}
					
			suite.addTestSuite(junitTestClass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return suite;

	}
	protected void runTest(final SeleniumTestRunner runner){	
		
		try {	
			
			runner.setUp();
			runner.runMultiThreadApplication();
			runner.cleanUpThread();
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				runner.printMessage(e.getMessage());
				runner.cleanUpThread();
				
			}		
				
	}
		

	/*protected synchronized void runTest(String threadIndex){
		Class junitTestClass;
		Object obj=null;
		try {
			junitTestClass = Class.forName(packageName+"."+testClass);				
			Method[] methods=junitTestClass.getDeclaredMethods();
			for( Method m:methods){
				String methodName=m.getName();
				if(methodName.equalsIgnoreCase("setThreadIndex")){
					
					Class[] paramString = new Class[1];	
					paramString[0] = String.class;	
					try {	obj=junitTestClass.newInstance();
							Method setVarMap = junitTestClass.getDeclaredMethod("setThreadIndex", paramString);
							setVarMap.invoke(obj, threadIndex);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						try {	
							Method runMutiThraed = junitTestClass.getDeclaredMethod("runMutiThraed", null);
							runMutiThraed.invoke(obj, null);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						try {	
							Method runMutiThraed = junitTestClass.getDeclaredMethod("tearDownMultiThread", null);
							runMutiThraed.invoke(obj, null);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
					}
				
				
			}
				
			//suite.addTestSuite(junitTestClass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			obj=null;
			return;
		}
		obj=null;
		return ;

	}*/
		//suite method

		protected synchronized TestSuite suite(String threadIndex){
			
			TestSuite suite = new TestSuite("RUNNING TEST CONFIG: -"+testConfigName);
			Class junitTestClass;
			Object obj=null;
			try {
				junitTestClass = Class.forName(packageName+"."+testClass);				
				Method[] methods=junitTestClass.getDeclaredMethods();
				for( Method m:methods){
					String methodName=m.getName();
					if(methodName.equalsIgnoreCase("setVarMap")){
						
						Class[] paramString = new Class[1];	
						paramString[0] = String.class;	
						try {	obj=junitTestClass.newInstance();
								Method setVarMap = junitTestClass.getDeclaredMethod("setThreadIndex", paramString);
								setVarMap.invoke(obj, threadIndex);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
						}
					boolean isAnnoted=m.isAnnotationPresent((Class<? extends Annotation>) org.junit.Test.class);
					 if (isAnnoted || methodName.substring(0, 4).equalsIgnoreCase("test")){
						 testMethods.add(methodName);
						 System.out.println(m.getName());
					 }
					
				}
				suite.addTest((Test)obj);				
				//suite.addTestSuite(junitTestClass);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return suite;

		}
		

}

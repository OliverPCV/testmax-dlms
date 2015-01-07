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
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import com.testmax.framework.ConfigLoader;
import com.testmax.framework.WmLog;
import com.testmax.handler.SeleniumBaseHandler;


public class ClassLoaderUtil {
	
	/**
	 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	 *
	 * @param packageName The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Class[] getClasses(String packageName)
	        throws ClassNotFoundException, IOException {
	    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    assert classLoader != null;
	    String path = packageName.replace('.', '/');
	    Enumeration<URL> resources = classLoader.getResources(path);
	    List<File> dirs = new ArrayList<File>();
	    while (resources.hasMoreElements()) {
	        URL resource = resources.nextElement();
	        dirs.add(new File(resource.getFile()));
	    }
	    ArrayList<Class> classes = new ArrayList<Class>();
	    for (File directory : dirs) {
	        classes.addAll(findClasses(directory, packageName));
	    }
	    return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
	    List<Class> classes = new ArrayList<Class>();
	    if (!directory.exists()) {
	        return classes;
	    }
	    File[] files = directory.listFiles();
	    for (File file : files) {
	        if (file.isDirectory()) {
	            assert !file.getName().contains(".");
	            classes.addAll(findClasses(file, packageName + "." + file.getName()));
	        } else if (file.getName().endsWith(".class")) {
	            classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
	        }
	    }
	    return classes;
	}
	
	 private static Class getTagLibClass(String packageName,String className){
		    
		   try {
			Class[] classes=ClassLoaderUtil.getClasses(packageName);
			for(Class myclass:classes){
				if(myclass.getName().toLowerCase().contains(className)){
					return myclass;
				}
			}
			WmLog.printMessage(">>>>FAILED TO FIND CLASS "+packageName+ "."+className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			 WmLog.printMessage(">>>>FAILED to load class "+packageName+ "."+className);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			 WmLog.printMessage(">>>>FAILED to load class "+packageName+ "."+className);
			e.printStackTrace();
		}
		return null;
	   }
	 
	public static boolean invoke(SeleniumBaseHandler handler,String tag){
		 String classname="";
		 String packageName="";
		   try{
			   packageName=ConfigLoader.getConfig("USER_PACKAGE").toLowerCase();
			   String[] tags=tag.split("\\.");
			 //no paramater
				Class noparams[] = {};
				
				Class cls=getTagLibClass(packageName,tags[0]);
				classname=cls.getClass().getCanonicalName();
				Object obj=cls.newInstance();
				
				Class[] paramString = new Class[1];	
				paramString[0] = SeleniumBaseHandler.class;	
				
				Method setSeleniumRunner = cls.getDeclaredMethod("setSeleniumRunner", paramString);
				setSeleniumRunner.invoke(obj, handler);
			
			  //call the printIt method
				final Method method = cls.getDeclaredMethod(tags[1], noparams);
		    	AccessController.doPrivileged(new PrivilegedAction() {
		    		public Object run() {
					method.setAccessible(true);
		                return null; // nothing to return
		    		}
		    	});
				//Method method = cls.getDeclaredMethod(tags[1], noparams);
				method.invoke(obj, null);
		 
				return true;
	   }catch(NullPointerException e){	
		   WmLog.printMessage(">>>>FAILED to load class "+(classname!=null && !classname.isEmpty()?classname:packageName+"."+tag));
			e.printStackTrace();
			return false;
	   }catch(Exception ex){
		   WmLog.printMessage(">>>>FAILED to load class "+(classname!=null && !classname.isEmpty()?classname:packageName+"."+tag));
			ex.printStackTrace();
			return false;
		}
		  
	}
	private static boolean invokeWithStringParam(String packageName,String className, String methodName, String val){
		 
		   try{
			 
				//String parameter
				Class[] paramString = new Class[1];	
				paramString[0] = String.class;			 
				
				Class cls=getTagLibClass(packageName,className);
			    Object obj=cls.newInstance();
			  
				//call the printItString method, pass a String param 
				Method method = cls.getDeclaredMethod(methodName, paramString);
				method.invoke(obj, new String(val));
		 
				return true;
			    
	   }catch(Exception ex){
		   WmLog.printMessage(">>>>FAILED to load class "+packageName+ "."+className);
			ex.printStackTrace();
			return false;
		}
		  
	}
	
	private static boolean invokeWithIntParam(String packageName,String className, String methodName, int val){
		 
		   try{
			 
				//int parameter
				Class[] paramInt = new Class[1];	
				paramInt[0] = Integer.TYPE;
				
				Class cls=getTagLibClass(packageName,className);
			    Object obj=cls.newInstance();
			  
				//call the printItInt method, pass a int param
				Method method = cls.getDeclaredMethod(methodName, paramInt);
				method.invoke(obj, val);
				return true;
			    
	   }catch(Exception ex){
		   WmLog.printMessage(">>>>FAILED to load class "+packageName+ "."+className);
			ex.printStackTrace();
			return false;
		}
		  
	}
	

}

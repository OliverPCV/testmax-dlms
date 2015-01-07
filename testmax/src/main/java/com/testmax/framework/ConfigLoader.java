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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import com.testmax.runner.ExecutionEngin;
import com.testmax.runner.TestEngine;
import com.testmax.util.ExcelSheet;




public class ConfigLoader {
    
    private HashMap <String, PageURL> moduleMap= null;
    
    private HashMap <String, String> moduleClassMap= null;
    
    private HashMap <String, HashMap> testDataMap= null;
    
    private static HashMap <String, HashMap <String,Element>> dbEnvMap= null;
    
    private static HashMap <String, String> globalDataMap= null;
    
    private static HashMap <String, Element> globalDataSetMap= null;
    
    private static HashMap <String, String> sqlLibDataMap= null;
    
    private static HashMap <String, Element> tagLibDataMap= null;
    
    private  HashMap<String, Element> testPageList=new HashMap<String, Element>();
    
    private String moduleName=null;
    
    private String moduleClassName=null;   
   
    private String globalName=null;
    
    private String sqlLibName=null;
    
    private String tagLibName=null;
    
    private static Properties configProp=null;
    
    private static Properties dbProp=null;
    
 // WM home
    private static String WM_ROOT = ".";
    
 // WM executables directory
    private static String WM_BIN_ROOT = null;
    // WM configuration direcotry
    private static String WM_CONF_ROOT = null;
    // WM test cases direcotry
    private static String WM_TESTDATA_ROOT = null;
    // WM module  direcotry
    private static String WM_MODULE_ROOT = null;
    // WM global direcotry
    private static String WM_GLOBAL_ROOT = null;
    // WM global direcotry
    private static String WM_SQLLIB_ROOT = null;
    // Tag Lib directory
    private static String WM_TAGLIB_ROOT = null;
    // WM suites direcotry
    private static String WM_SUITES_ROOT = null;
    // WM output direcotry
    private static String WM_OUTPUT_ROOT = null;
    // dynamic part of the output dir name
    //private static final String OUTPUT_DIR_DYNAMIC_PART = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
   
    
    private PageURL urlObj=null;
    
    private String urlClass=null;
    
   
    
    
    public ConfigLoader(){
        setWMRoot();
        dbProp=new Properties();
        configProp=new Properties();
        this.moduleMap= new HashMap <String, PageURL>();
        this.moduleClassMap= new HashMap <String, String>();
        this.testDataMap= new HashMap <String, HashMap>();
        this.dbEnvMap= new HashMap <String, HashMap<String,Element>>();
        this.globalDataSetMap=new HashMap <String, Element>();
        this.globalDataMap=new HashMap <String, String>();
        this.sqlLibDataMap=new HashMap <String, String>();
        this.tagLibDataMap=new HashMap <String, Element>();
        this.testPageList=new HashMap<String, Element>();
        try {
            configProp.load(new FileInputStream(this.getWmConfigRoot()+ "/config.properties"));
           
        } catch (FileNotFoundException e) {
        	System.out.println(this.getClass().getName()+"Can not load config File on Path "+this.getWmConfigRoot()+ "/config/config.properties");
            e.printStackTrace();
        } catch (IOException e) {
        	System.out.println(this.getClass().getName()+"Can not load config File on Path "+this.getWmConfigRoot()+ "/config/config.properties");
            e.printStackTrace();
        }
        
        try {            
            dbProp.load(new FileInputStream(this.getWmConfigRoot()+ "/db.properties"));
        } catch (FileNotFoundException e) {
          System.out.println(this.getClass().getName()+"Can not load optional db config File on Path "+this.getWmConfigRoot()+ "/config/db.properties");
            
        } catch (IOException e) {
        	System.out.println(this.getClass().getName()+"Can not load optional db config File on Path "+this.getWmConfigRoot()+ "/config/db.properties");
          
        }   
 
    }
    public void parseGlobalDataFile(){
        parseGlobalDataFiles(this.getGlobalFileList());
        parseGlobalDataFiles(this.getGlobalXLsFileList());
        parseGlobalDataFiles(this.getGlobalCSVFileList());
        
    }
    
    private void parseGlobalDataFiles(List<File> globalDataFiles){       
        for(File globalDataFile : globalDataFiles){
        	loadGlobalDataFile(globalDataFile);
        }        
    }
    
   
    private void loadGlobalDataFile( File globalDataFile){
       try{
        List<Element> elms= scanGlobalDataFiles(globalDataFile);
      
        for (Element element:elms ){ 
            String elementName=element.getName();
            if(!elementName.isEmpty()){
            	 List<Element> data=element.elements();
            	if(data.size()>0){            		
        			 String globalDataSetName="global:"+this.globalName+"."+elementName;
        			 this.globalDataSetMap.put(globalDataSetName.toLowerCase(), element);            		
            	}else{
	                String value=element.attributeValue("value");
	                String globalFieldName="global:"+this.globalName+"."+elementName;
	                this.globalDataMap.put(globalFieldName.toLowerCase(), value);
            	}
            }
        }
       }catch(Exception e){
    	     String error=">>>Exception:<<<"+this.getClass().getName()+">>> Failed in reading file "+globalDataFile.getAbsolutePath()+" Message:"+e.getMessage();
	         WmLog.getCoreLogger().info(error);
	         System.out.println(error);          
	     }
         
    }
    
	public static void addGlobalXMLData(String key, String xml){
		try {
			Document doc=new SAXReader().read( new StringReader(xml));
			 Element rootEle=doc.getRootElement();
			 List<Element> elms=rootEle.elements();
			 for (Element element:elms ){ 
	            	 List<Element> data=element.elements();
	            	if(data.size()>0){            		
	        			 String globalDataSetName=(key.contains("global:")?key:"global:query."+key);
	        			 globalDataSetMap.put(globalDataSetName.toLowerCase(), element);            		
	            	}
		        }
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private List<Element> scanGlobalDataFiles(File globalDataFile){
	     List<Element> elements=new ArrayList<Element>();
	     Document doc=null;
	     try{
	    	 if(globalDataFile.getAbsolutePath().endsWith(".xls")||globalDataFile.getAbsolutePath().contains(".xls")){
	    		 ExcelSheet xl= new ExcelSheet(globalDataFile.getAbsolutePath());	 			
	 			 String xml=xl.getXMLDataset();
	 			 WmLog.getCoreLogger().info(xml);
	 			 doc=new SAXReader().read( new StringReader(xml));
	    	 }else if(globalDataFile.getAbsolutePath().endsWith(".csv")||globalDataFile.getAbsolutePath().contains(".csv")){
	    		 ExcelSheet xl= new ExcelSheet(globalDataFile.getAbsolutePath());	 			
	 			 String xml=xl.getXMLDatasetFromCSV();
	 			 WmLog.getCoreLogger().info(xml);
	 			 doc=new SAXReader().read( new StringReader(xml));
	    	 }else{
	    		 doc=new SAXReader().read(globalDataFile);
	    	 }
	     }catch(DocumentException e){
	    	 String error=">>>Exception:<<<"+this.getClass().getName()+">>> Failed in reading file "+globalDataFile.getAbsolutePath()+e.getMessage();
	         WmLog.getCoreLogger().info(error);
	         System.out.println(error);          
	     }catch(Exception e){
	    	 String error=">>>Exception:<<<"+this.getClass().getName()+">>> Failed in reading file "+globalDataFile.getAbsolutePath()+e.getMessage();
	         WmLog.getCoreLogger().info(error);
	         System.out.println(error); 
	      }      
	     Element rootEle=doc.getRootElement();
	     this.globalName=rootEle.attributeValue("name");
	     elements.addAll(rootEle.elements());
	     return elements;
	}
    public void parseTestDataFile(){
    	if(TestEngine.suite==null){
    		List<File> testDataFiles=this.getTestDataFileList();
    		parseTestDataFiles(testDataFiles);
    	}else{
    		HashMap<String, Element> testsuite= new HashMap<String, Element>();
    		testsuite.put(TestEngine.suite.getName(), TestEngine.suite.getTestSuite());
    		this.testDataMap.put(TestEngine.suite.getName(),testsuite);
    	}
        
    }
    public void parseTagLibDataFile(){
        List<File> TagLibDataFiles=this.getTagLibFileList();
        parseTagLibDataFiles(TagLibDataFiles);
        
    }
    
    private void parseTagLibDataFiles(List<File> TagLibDataFiles){       
        for(File TagLibDataFile : TagLibDataFiles){
        	loadTagLibDataFile(TagLibDataFile);
        }        
    }  
    private void loadTagLibDataFile( File TagLibDataFile){
        try{
	        List<Element> elms= scanTagLibDataFiles(TagLibDataFile);
	       
	        for (Element element:elms ){ 
	            String elementName=element.getName();
	            if(!elementName.isEmpty()){               
	                String tagLibFieldName="taglib:"+this.tagLibName+"."+elementName;
	                this.tagLibDataMap.put(tagLibFieldName.toLowerCase(), element);
	            }
	        }  
        }catch(Exception e){
        	 String error=">>>Exception:<<<"+this.getClass().getName()+">>> Failed in reading file "+TagLibDataFile.getAbsolutePath()+" Message:"+e.getMessage();
	         WmLog.getCoreLogger().info(error);
	         System.out.println(error); 
         } 
    }
    
    private List<Element> scanTagLibDataFiles(File tagLibDataFile){
        List<Element> elements=new ArrayList<Element>();
        Document doc=null;
        try{
            doc=new SAXReader().read(tagLibDataFile);
        }catch(DocumentException e){
        	 String error=">>>Exception:<<<"+this.getClass().getName()+">>> Failed in reading file "+tagLibDataFile.getAbsolutePath()+" Message:"+e.getMessage();
	         WmLog.getCoreLogger().info(error);
	         System.out.println(error); 
        }catch(Exception e){
        	 String error=">>>Exception:<<<"+this.getClass().getName()+">>> Failed in reading file "+tagLibDataFile.getAbsolutePath()+" Message:"+e.getMessage();
	         WmLog.getCoreLogger().info(error);
	         System.out.println(error); 
        }     
        Element rootEle=doc.getRootElement();
        this.tagLibName=rootEle.attributeValue("name");
        elements.addAll(rootEle.elements());
        return elements;
    }
    public void parseSqlLibDataFile(){
        List<File> sqlLibDataFiles=this.getSqlLibFileList();
        parseSqlLibDataFiles(sqlLibDataFiles);
        
    }
    
    private void parseSqlLibDataFiles(List<File> sqlLibDataFiles){       
        for(File sqlLibDataFile : sqlLibDataFiles){
        	loadSqlLibDataFile(sqlLibDataFile);
        }        
    }  
    private void loadSqlLibDataFile( File sqlLibDataFile){
       try{
        List<Element> elms= scanSqlLibDataFiles(sqlLibDataFile);
       
        for (Element element:elms ){ 
            String elementName=element.getName();
            if(!elementName.isEmpty()){
                String value=element.getText();
                String sqlLibFieldName="sqllib:"+this.sqlLibName+"."+elementName;
                this.sqlLibDataMap.put(sqlLibFieldName.toLowerCase(), value);
            }
        } 
        
       }catch(Exception e){
    	     String error=">>>Exception:<<<"+this.getClass().getName()+">>> Failed in reading file "+sqlLibDataFile.getAbsolutePath()+" Message:"+e.getMessage();
	         WmLog.getCoreLogger().info(error);
	         System.out.println(error); 
	   }
    }
    
    private List<Element> scanSqlLibDataFiles(File sqlLibDataFile){
        List<Element> elements=new ArrayList<Element>();
        Document doc=null;
        try{
            doc=new SAXReader().read(sqlLibDataFile);
        }catch(DocumentException e){
        	 String error=">>>Exception:<<<"+this.getClass().getName()+">>> Failed in reading file "+sqlLibDataFile.getAbsolutePath()+" Message:"+e.getMessage();
	         WmLog.getCoreLogger().info(error);
	         System.out.println(error); 
         }catch(Exception e){
        	 String error=">>>Exception:<<<"+this.getClass().getName()+">>> Failed in reading file "+sqlLibDataFile.getAbsolutePath()+" Message:"+e.getMessage();
	         WmLog.getCoreLogger().info(error);
	         System.out.println(error); 
         } 
        Element rootEle=doc.getRootElement();
        this.sqlLibName=rootEle.attributeValue("name");
        elements.addAll(rootEle.elements());
        return elements;
    }
    
    private void parseTestDataFiles(List<File> testDataFiles){       
        for(File testDataFile : testDataFiles){
            this.testDataMap.put(testDataFile.getAbsolutePath(),loadTestDataFile(testDataFile));
        }        
    }
    
    private HashMap loadTestDataFile( File testDataFile){
        
        List<Element> elms= scanTestDataFiles(testDataFile);
        HashMap<String, Element> data=new HashMap<String, Element> ();
        for (Element element:elms ){ 
            String elementName=element.getName();
            if(elementName.equalsIgnoreCase("test")){
                String testName=element.attributeValue("name");
                data.put(testName, element);
            }
        }
        return data;       
    }
    
    private List<Element> scanTestDataFiles(File configFile){
        List<Element> elements=new ArrayList<Element>();
        Document doc=null;
        try{
            doc=new SAXReader().read(configFile);
        }catch(DocumentException e){
        	 String error=">>>Exception:<<<"+this.getClass().getName()+">>> Failed in reading file "+configFile.getAbsolutePath()+" Message:"+e.getMessage();
	         WmLog.getCoreLogger().info(error);
	         System.out.println(error); 
        }catch(Exception e){
        	 String error=">>>Exception:<<<"+this.getClass().getName()+">>> Failed in reading file "+configFile.getAbsolutePath()+" Message:"+e.getMessage();
	         WmLog.getCoreLogger().info(error);
	         System.out.println(error); 
       }  
        Element rootEle=doc.getRootElement(); 
        elements.addAll(rootEle.elements());
        return elements;
    }
    
    public void parseDbDataFile(){
        List<File> dbDataFiles=this.getDbDataFileList();
        parseDbDataFiles(dbDataFiles);
        
    }
    
    private void parseDbDataFiles(List<File> dbDataFiles){       
        for(File dbDataFile : dbDataFiles){
            this.dbEnvMap.put(dbDataFile.getName().split("\\.")[0],loadDbDataFile(dbDataFile));
        }        
    }
    
    private HashMap loadDbDataFile( File dbDataFile){
       try{
	        List<Element> elms= scanDbDataFiles(dbDataFile);
	        HashMap<String, Element> data=new HashMap<String, Element> ();
	        for (Element element:elms ){ 
	            String elementName=element.getName();
	            if(!elementName.isEmpty()){                
	                data.put(elementName, element);
	            }
	        }
	        return data;   
        
       }catch(Exception e){
    	   	 String error=">>>Exception:<<<"+this.getClass().getName()+">>> Failed in reading file "+dbDataFile.getAbsolutePath()+" Message:"+e.getMessage();
	         WmLog.getCoreLogger().info(error);
	         System.out.println(error); 
      }
       return null;
    }
    
    private List<Element> scanDbDataFiles(File configFile){
        List<Element> elements=new ArrayList<Element>();
        Document doc=null;
        try{
            doc=new SAXReader().read(configFile);
        }catch(DocumentException e){
        	 String error=">>>Exception:<<<"+this.getClass().getName()+">>> Failed in reading file "+configFile.getAbsolutePath()+" Message:"+e.getMessage();
	         WmLog.getCoreLogger().info(error);
	         System.out.println(error); 
        }catch(Exception e){
        	 String error=">>>Exception:<<<"+this.getClass().getName()+">>> Failed in reading file "+configFile.getAbsolutePath()+" Message:"+e.getMessage();
	         WmLog.getCoreLogger().info(error);
	         System.out.println(error); 
        }     
        Element rootEle=doc.getRootElement(); 
        elements.addAll(rootEle.elements());
        return elements;
    }
    public void parseModuleDataFile(){
        List<File> configFiles=this.getModuleFileList();
        parseModuleDataFiles(configFiles);
    }
    
    private void parseModuleDataFiles(List<File> configFiles){       
        for(File configFile : configFiles){
            PageURL page=loadFile(configFile);
            if(this.moduleName!=null){
            	this.moduleMap.put(this.moduleName,page);
            }
        }        
    }
    

    private PageURL loadFile( File configFile){
        try{
	        List<Element> elms= scanModuleFiles(configFile);
	        PageURL page=new PageURL();
	        for (Element element:elms ){           
	            URLConfig url=new URLConfig(element);
	            String elementName=element.getName();            
	            String actionName=element.attributeValue("name");
	            //System.out.println("Action Name="+actionName);
	            page.addUrlConfig(actionName, url);
	            page.addActionList(element);
	        }
	        return page;
        }catch(Exception e){
        	 String error=">>>Exception:<<<"+this.getClass().getName()+">>> Failed in reading file "+configFile.getAbsolutePath()+" Message:"+e.getMessage();
	         WmLog.getCoreLogger().info(error);
	         System.out.println(error); 
       }
        return null;
       
    }
    
    
    private List<Element> scanModuleFiles(File configFile){
        List<Element> elements=new ArrayList<Element>();
        Document doc=null;
        try{
            doc=new SAXReader().read(configFile);
        }catch(DocumentException e){
        	String error=">>>Exception:<<<"+this.getClass().getName()+">>> Failed in reading file "+configFile.getAbsolutePath()+" Message:"+e.getMessage();
	         WmLog.getCoreLogger().info(error);
	         System.out.println(error); 
        }catch(Exception e){
    	   String error=">>>Exception:<<<"+this.getClass().getName()+">>> Failed in reading file "+configFile.getAbsolutePath()+" Message:"+e.getMessage();
	         WmLog.getCoreLogger().info(error);
	         System.out.println(error); 
       	}  
        Element rootEle=doc.getRootElement();
        this.moduleName=rootEle.attributeValue("name");
        //only load module files which need to be tested
        Element testEists=getTestCasePageList().get(this.moduleName);
        if (testEists!=null ){
	        this.moduleClassName=rootEle.attributeValue("class");
	        this.moduleClassMap.put(moduleName, moduleClassName);
	        //WmLog.getCoreLogger().info("Scaning moduleName="+moduleName);        
	        //System.out.println("Scaning moduleName="+moduleName);
	        elements.addAll(rootEle.elements());
        }else{
        	this.moduleName=null;
        }
        return elements;
    }
    
    /**
     * load  from "any.properties" 
     * @return the loaded property
     */    
    public static Properties loadAnyConfig(String dataFile) {
        Properties engine = new Properties();
        try {
            engine.load(new FileInputStream(dataFile));
        }

        catch (FileNotFoundException fe) {

            WmLog.getCoreLogger().error("engine files not found" + fe.getMessage());
            System.out.println("exception::engine files not found");

        }
        
        catch (IOException e){
            WmLog.getCoreLogger().error("engine files not found" + e.getMessage());
            System.out.println("exception::engine files not found");
            
        }
        return engine;
    }
   
    
    /**
     * Get a WM APP configuration attribute
     * @param key the attribute to get
     * @return the value or null if not existing
     */
    public static String getConfig(String key){
        String val = null;
        if(key!=null){
           val = configProp.getProperty(key);
           return  val;
        }       
        return null;
    } 
    
    /**
     * set a WM APP configuration attribute
     * @param key the attribute to get
     * @return the value or null if not existing
     */
    public static void setConfigProperty(String key, String value){
    	configProp.setProperty(key, value);
    } 
    
    /**
     * Get a WM APP configuration attribute
     * @param key the attribute to get
     * @return the value or null if not existing
     */
    public static String getDbProperty(String key){
        String val = null;
        
        if(key!=null){
           val = dbProp.getProperty(key);
           return  val;
        }
        return null;
    } 
    
    /** get WM_ROOT from system environment*/
    private static void setWMRoot(){
        try {
            WM_ROOT = System.getenv("WM_ROOT");
           
        } catch(Exception e) {
            System.out.println("Fatal error - can not get system enviroment variable \"WM_ROOT\" :" +e);
            System.out.println("Caused by: " + e.toString());
            System.exit(1);
        }
        if (WM_ROOT == null){
            //System.out.println("Warning - \"WM_ROOT\" not defined in the system environment!");
            File root=new File(".");
            if(root.isDirectory()){
            	  WM_ROOT=root.getAbsolutePath();
            }else{
            	 WM_ROOT=".";
            }
        }
        WM_BIN_ROOT = WM_ROOT + "/bin" + File.separator;
        WM_CONF_ROOT = WM_ROOT + "/data" + File.separator + "config" + File.separator;
        WM_MODULE_ROOT = WM_ROOT + "/data" + File.separator + "module" + File.separator;
        WM_GLOBAL_ROOT = WM_ROOT + "/data" + File.separator + "global" + File.separator;
        WM_SQLLIB_ROOT = WM_ROOT + "/data" + File.separator + "sqllib" + File.separator;
        WM_TAGLIB_ROOT = WM_ROOT + "/data" + File.separator + "taglib" + File.separator;
        WM_TESTDATA_ROOT = WM_ROOT + "/data" + File.separator + "TestSuite" + File.separator;       
        WM_OUTPUT_ROOT = WM_ROOT +  File.separator + "output" + File.separator;  

    }
    
    private ArrayList<File> getDbDataFileList(){
        return scanFiles(this.getWmConfigRoot(),".xml");
    }
    private ArrayList<File> getTestDataFileList(){
        return scanFiles(this.getWmTestDataRoot(),".xml");
    }
    
    private ArrayList<File> getModuleFileList(){
        return scanFiles(this.getWmModuleRoot(),".xml");
    }
    
    private ArrayList<File> getGlobalFileList(){
        return scanFiles(this.getWmGlobalDataRoot(),".xml");
    }
    
    private ArrayList<File> getGlobalCSVFileList(){
        return scanFiles(this.getWmGlobalDataRoot(),".csv");
    }
    private ArrayList<File> getGlobalXLsFileList(){
        return scanFiles(this.getWmGlobalDataRoot(),".xls");
    }
    
    private ArrayList<File> getSqlLibFileList(){
        return scanFiles(this.getWmSqlLibDataRoot(),".xml");
    }
    private ArrayList<File> getTagLibFileList(){
        return scanFiles(this.getWmTagLibDataRoot(),".xml");
    }
    private ArrayList<File> scanFiles(String testCaseRoot,String fileExtension){
        ArrayList<File> caseFiles=new ArrayList<File>();
        File root=new File(testCaseRoot);
        try{
       
	        if(root.exists()){
	            caseFiles=this.scanCaseFilesRecursively(root,fileExtension);
	        }
	        else{
	            System.out.println(">>> Can't find the path > "+testCaseRoot);
	        }
        }catch(Exception e){
            WmLog.getCoreLogger().info(">>>Exception:<<<"+this.getClass().getName()+">>> Failed in reading Data XML file "+root.getAbsolutePath());
            System.out.println(">>>Exception:<<<"+this.getClass().getName()+">>> Failed in Reading XML  Data file "+root.getAbsolutePath());          
        } 
        return caseFiles;
    }
    
    private ArrayList<File> scanCaseFilesRecursively(File rootFile,String fileExtension){
        ArrayList<File> files=new ArrayList<File>();
        if(rootFile.exists()&& !rootFile.isHidden()){
            if(rootFile.isFile()){
                if(this.isMatchedFile(rootFile,fileExtension)){
                    files.add(rootFile);
                }           
            }else if(rootFile.isDirectory()){
                for(File aFile:rootFile.listFiles()){
                    files.addAll(this.scanCaseFilesRecursively(aFile,fileExtension));
                }
            }
        }
        return files;
    }
    
    private boolean isMatchedFile(File file,String fileExtension){
        if(file.exists()&&file.isFile()&&(file.getName().endsWith(fileExtension)||file.getName().contains(fileExtension))){
            return true;
        }else{
            return false;
        }
    }
    public static void addGlobalField(String name, String value){
    	globalDataMap.put(name.toLowerCase(), value);
    }
    
    public HashMap<String, Element> getTestCasePageList(){
    	
    	if(this.testPageList.isEmpty()){
	    	HashMap <String, HashMap> testDataSet=this.getTestDataMap();
	    	//Extract each test data file from dataset
	        for(Object testfile: testDataSet.keySet()){
	            HashMap <String, Element> testDataFileSet=testDataSet.get(testfile);
	            //Extract each test case
	            for(String testcase: testDataFileSet.keySet()){
	                Element test=testDataFileSet.get(testcase);
	                String page=test.attributeValue("page");
	                this.testPageList.put(page,test);
	            }
	         }
    	}
        return this.testPageList;
    }
    public HashMap getModuleMap(){
        return this.moduleMap;
    }
    
    public HashMap getModuleClassMap(){
        return this.moduleClassMap;
    }    
    
    public HashMap getTestDataMap(){
        return this.testDataMap;
    }
    
    public HashMap getGlobalDataMap(){
        return this.globalDataMap;
    }
    
    public static String getGlobalDataFieldValue(String key){
    	return (globalDataMap.get(key.toLowerCase()));
    }
    
    public static Element getGlobalDataSet(String key){
    	return (globalDataSetMap.get(key.toLowerCase()));
    }
    
    public static String getSqlLibByKey(String key){
    	return (sqlLibDataMap.get(key.toLowerCase()));
    }
    public static Element getTagLibByKey(String key){
    	return (tagLibDataMap.get(key.toLowerCase()));
    }
    public static void printMatchedTagLibKey(String match){
    	for(Object key:tagLibDataMap.keySet()){
    		if(key.toString().equalsIgnoreCase(match)){
    			System.out.println("matched key="+key.toString());
    		}else{
    			System.out.println("not matched key="+key.toString() +" passed="+match);
    		}
    	}
    	
    }
    public static String getWmRoot(){
        return WM_ROOT;
    }
    
    public static String getWmConfigRoot(){
        return WM_CONF_ROOT;
    }
    
    public static String getWmTestDataRoot(){
        return WM_TESTDATA_ROOT;
    }
    
    public static String getWmGlobalDataRoot(){
        return WM_GLOBAL_ROOT;
    }
    
    public static String getWmSqlLibDataRoot(){
        return WM_SQLLIB_ROOT;
    }
    public static String getWmTagLibDataRoot(){
        return WM_TAGLIB_ROOT;
    }
    public static String getWmModuleRoot(){
        return WM_MODULE_ROOT;
    }
    
    public static String getWmOutputRoot(){
        return WM_OUTPUT_ROOT;
    }
    public static String getWmOutputLogPath(){
        //return WM_OUTPUT_ROOT+File.separator+OUTPUT_DIR_DYNAMIC_PART+File.separator;
    	return WM_OUTPUT_ROOT+ExecutionEngin.getDynamicPath()+File.separator;
    }
    public static String getWmOutputReportPath(){
        //return WM_OUTPUT_ROOT+File.separator+OUTPUT_DIR_DYNAMIC_PART+File.separator+"report"+File.separator;
    	return WM_OUTPUT_ROOT+ExecutionEngin.getDynamicPath()+File.separator+"report"+File.separator;
    }
    public static String getWmOutputWebServicePath(){
        //return WM_OUTPUT_ROOT+File.separator+OUTPUT_DIR_DYNAMIC_PART+File.separator+"ws"+File.separator;
    	return WM_OUTPUT_ROOT+ExecutionEngin.getDynamicPath()+File.separator+"ws"+File.separator;
    }
    
    public static String getWmOutputSqlPath(){
        //return WM_OUTPUT_ROOT+File.separator+OUTPUT_DIR_DYNAMIC_PART+File.separator+"sql"+File.separator;
    	return WM_OUTPUT_ROOT+ExecutionEngin.getDynamicPath()+File.separator+"sql"+File.separator;
    }
    /*
     * Returns database env Element from db_qa2 or db_qa3.xml based on the dbTag name supplied
     */
    public static Element getDatabaseEnv(String dbTag){
    	String prefix="db_";
    	if(TestEngine.suite!=null){
    		prefix="prod_";
    	}
    	HashMap<String,Element> db=dbEnvMap.get(prefix+getConfig("QA_TEST_ENV").toLowerCase());
    	 
    	if(db==null && ConfigLoader.getConfig("REPORT_SUMMARY").equalsIgnoreCase("ON")||dbTag.contains("automation")){
    		 WmLog.printMessage("#######Retrieving Database Connection Parameters="+prefix+dbTag);
    		 db=dbEnvMap.get(prefix+dbTag);
    		return(db.get(dbTag));  
    	}
    	
    	return(db.get(dbTag));    	
    }
    
    public static Element getProdDatabaseEnv(){
    	return(dbEnvMap.get("prod_automation").get("automation"));
 		
 	} 
    
    public static Element getLocalAutomationDatabaseEnv(){
    	return(dbEnvMap.get("prod_automation").get("automation_local"));
 		
 	} 
   
}

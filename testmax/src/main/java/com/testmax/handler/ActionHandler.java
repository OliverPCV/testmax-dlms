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
import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
*/

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultAttribute;

import com.testmax.framework.BasePage;
import com.testmax.framework.BrowserDatasetThread;
import com.testmax.framework.ConfigLoader;
import com.testmax.framework.PerformerBase;
import com.testmax.framework.URLConfig;
import com.testmax.framework.WmLog;
import com.testmax.runner.TestEngine;
import com.testmax.util.DbUtil;
import com.testmax.util.DomUtil;
import com.testmax.util.FileUtility;
import com.testmax.util.ItemUtility;
import com.testmax.util.PrintTime;



public class ActionHandler extends PerformerBase {
	
	protected  CallableStatement cs = null;
	
	protected PreparedStatement ps =null;
	
	private Connection dbConn = null;
	
	private String dbProvider = null;
	
	private String dbDriver=null;
	
	private Element actionItem=null;
	
	private List<Integer> cursorList=null;
	
	private List<Element> vlidatorList=null;
	
	private List<Integer> randomElmIndex=null;
	
	private List<Element> randomElm=null;
	
	//private List<StructDescriptor> structDescList=null;
	
	//private List<ArrayDescriptor> arrayDescList=null;	
    
	public String dbmsOutputKey="";
	
	public String globaldataset=""; 
	
	public String printresult="";
	
	private boolean isDatasetValue=false;
	
	private boolean isApiTest=false;
	
	private boolean isUnitTest=false;
	
	private boolean isItemSetup=false;
	
	private boolean isMultiThread=false;
	
	private int datasetIndex=0;
	
	public int junitAssertCounter=0;

	
	/*
     *  Name of the browser's driver based on the test suite configuration parameter
     */
    
    private String driver=null; 
	/*
	 * Store all result dataset
	 */
	static HashMap<String, Connection> dbManager= null;
	
	/*
	 * Store all result dataset
	 */
	HashMap<String, Object> resultDataSet= null;
	
	/*
	 * Store all itemId generated using setup
	 */
	private HashMap<String, String> setupDataSet= null;
	
	/*
	 * Store all result parameters and their data types information
	 */
	HashMap<String, String> resultParamSet= null;
	
	/*
	 * Store all result parameters and their data types information
	 */
	private HashMap<String, String> localResultParamSet= null;
	
	/*
	 * Store all result parameters and their index information
	 */
	private HashMap<String, Integer> resultParamIndex= null;
	
	private ItemUtility iu=null;
	
	private String action=null;
	
	private String wsName=null;
	
	public ActionHandler(BasePage page, String action) {
		
		try{
			super.setup(page,action);
			this.iu=new ItemUtility();
			this.action=action;
			this.isUnitTest=page.isUnitTest();
			this.actionItem=page.getPageURL().getUrlConfig(action).getUrlElement();
			this.resultDataSet=new HashMap<String, Object>();
			this.dbManager=new HashMap<String, Connection>();
			this.resultParamSet=new HashMap<String, String>();
			this.resultParamIndex=new HashMap<String, Integer>();
			this.vlidatorList=new ArrayList<Element>();	
			this.randomElmIndex=new ArrayList<Integer>();
			this.randomElm=new ArrayList<Element>();
			//this.structDescList=new ArrayList<StructDescriptor>();
			//this.arrayDescList=new ArrayList<ArrayDescriptor>();	
			this.setupDataSet=new HashMap<String, String>();
			this.dbProvider="oracle";
			init();
		}catch (Exception e){
			
			WmLog.getCoreLogger().error(">>ERROR: please check test suite configuration file for attribute = action or attribute =page which may not be correct!");
			
			System.out.println(">>ERROR: please check test suite configuration file for attribute= action or attribute=page which may not be correct!");
			e.printStackTrace();
		}
		
	}
	
	/*
	 * Initialize database and setup test data
	 */
	public void init(){
		//add report db connection for database=qa_sc
	    if(ConfigLoader.getConfig("REPORT_SUMMARY").equalsIgnoreCase("ON")){	    	
		   changeDbConnection(ConfigLoader.getConfig("REPORT_DB"));
		   this.page.setDbConnection(this.dbConn);
	    }
		//this.page.setDbConnection(this.dbConn);
		
		String dbName=this.actionItem.attributeValue("dbName");
		String dbHost=ConfigLoader.getDbProperty("DB_RAC");
		String dbService=ConfigLoader.getDbProperty("DB_SERVICE");		
		String dbUser=ConfigLoader.getDbProperty("DB_USER");
		String dbPass=ConfigLoader.getDbProperty("DB_PASSWORD");	
		String dbPort=ConfigLoader.getDbProperty("DB_PORT");		
		String url=null;
		Element dbEnv=null;
		if (dbName!=null&&!dbName.isEmpty()){
			dbEnv=ConfigLoader.getDatabaseEnv(dbName);
			if(dbEnv!=null){
				List <Element> elmlist=dbEnv.elements();
				for(Element elm:elmlist){
					if(elm.getName().equalsIgnoreCase("user")){
						dbUser=elm.getText();
					}else if(elm.getName().equalsIgnoreCase("pwd")){
						dbPass=elm.getText();
					}else if(elm.getName().equalsIgnoreCase("host") && !elm.getText().isEmpty()){
						dbHost=elm.getText();
					}else if(elm.getName().equalsIgnoreCase("service")){
						dbService=elm.getText();					
					}else if(elm.getName().equalsIgnoreCase("rac")&& !elm.getText().isEmpty()){
						dbHost=elm.getText();
					}else if(elm.getName().equalsIgnoreCase("url")&& !elm.getText().isEmpty()){
						url=elm.getText();
					}else if(elm.getName().equalsIgnoreCase("port") && !elm.getText().isEmpty()){
						dbPort=elm.getText();
					}else if(elm.getName().equalsIgnoreCase("db")&& !elm.getText().isEmpty()){
						this.dbProvider=elm.getText();
					}else if(elm.getName().equalsIgnoreCase("driver")&& !elm.getText().isEmpty()){
						this.dbDriver=elm.getText();
					}
					
				}
			}
		
			if(url==null ){
				url=getConnectionStr(dbHost,dbService,dbPort);		
			}
					
			if(this.isUnitTest){
				Connection con=this.dbManager.get(dbName);
				if(con==null){
					con=makeDbConnection(url,dbUser,dbPass);	
					this.dbManager.put(dbName,con);
				}
				this.dbConn=con;
			}else{
				this.dbConn=makeDbConnection(url,dbUser,dbPass);	
			}
				
			setupCallableStatement();	
		}
		this.setupData(this.actionItem.elements());
		
		//for setup system time in mili and conversion to oracle date
		this.setupTime();		
	}
	
	public void setupTime(){
		Format pip=new SimpleDateFormat("MM/dd/yyyy HH.mm.ss");
		Format regular=new SimpleDateFormat("MM/dd/yyyy HH.mm.ss");
		long time=System.currentTimeMillis();
		long pipextra=2*24*60*60*1000;
		long piptime=time+pipextra;
		Date regulardate = new Date(time);		
		String oracledate = regular.format(regulardate);
		
		Date pipdate=new Date(piptime);
		String piporacledate = pip.format(pipdate);
		ConfigLoader.addGlobalField("global:setup.time", String.valueOf(time));
		ConfigLoader.addGlobalField("global:setup.oracledate", String.valueOf(oracledate));
		ConfigLoader.addGlobalField("global:setup.piptime", String.valueOf(piptime));
		ConfigLoader.addGlobalField("global:setup.piporacledate", String.valueOf(piporacledate));
		this.setupDataSet.put("time", String.valueOf(time));
		this.setupDataSet.put("oracledate", String.valueOf(oracledate));
		this.setupDataSet.put("piptime", String.valueOf(piptime));
		this.setupDataSet.put("piporacledate", String.valueOf(piporacledate));

	}
	
	private Connection makeDbConnection(String url, String user, String password) {
		Connection con =null;
		DbUtil du= new DbUtil();
		 if(this.dbProvider.equalsIgnoreCase("oracle")){
			 con=du.makeOracleDbConnection(url, user, password);
		 }else if(this.dbProvider.equalsIgnoreCase("cassandra")){
			 con=du.makeCassandraDbConnection(url, user, password);
		 }else if(this.dbProvider.equalsIgnoreCase("postgresql")){
			 con=du.makePostgreSqlDbConnection(url, user, password);
		 }else if(this.dbProvider.equalsIgnoreCase("mysql")){
			 con=du.makeMysqlDbConnection(url, user, password);
		 }else{
			 con=du.makeOtherDbConnection(this.dbDriver,url, user, password);
		 }
		return con;
	}
	public void changeDbConnectionFromDbmanager(String dbName){
		this.dbConn=this.dbManager.get(dbName);
	}
	public void changeDbConnection(String dbName){
		Connection con=null;
		String dbHost=null;
		String dbService=null;		
		String dbUser=null;
		String dbPass=null;	
		String dbPort=(ConfigLoader.getDbProperty("DB_PORT")==null?"1521":ConfigLoader.getDbProperty("DB_PORT"));
		Element dbEnv=null;
		String url=null;
		if (dbName!=null&&!dbName.isEmpty()){
			dbEnv=ConfigLoader.getDatabaseEnv(dbName);
			if(dbEnv!=null){
				List <Element> elmlist=dbEnv.elements();
				for(Element elm:elmlist){
					if(elm.getName().equalsIgnoreCase("user")){
						dbUser=elm.getText();
					}else if(elm.getName().equalsIgnoreCase("pwd")){
						dbPass=elm.getText();
					}else if(elm.getName().equalsIgnoreCase("host") && !elm.getText().isEmpty()){
						dbHost=elm.getText();
					}else if(elm.getName().equalsIgnoreCase("service")){
						dbService=elm.getText();
					}else if(elm.getName().equalsIgnoreCase("rac")&& !elm.getText().isEmpty()){
						dbHost=elm.getText();
					}else if(elm.getName().equalsIgnoreCase("url")&& !elm.getText().isEmpty()){
						url=elm.getText();
					}else if(elm.getName().equalsIgnoreCase("port") && !elm.getText().isEmpty()){
						dbPort=elm.getText();
					}else if(elm.getName().equalsIgnoreCase("db")&& !elm.getText().isEmpty()){
						this.dbProvider=elm.getText();
					}
					
				}
			}
		}
		if(url==null){
			url=getConnectionStr(dbHost,dbService,dbPort);
		}
					
		if(this.isUnitTest){
				con=this.dbManager.get(dbName);
			if(con==null&&url!=null &&dbUser!=null &&dbPass!=null){
				con=makeDbConnection(url,dbUser,dbPass);	
				this.dbManager.put(dbName,con);
			}
			this.dbConn=con;
		}else{
			if(url!=null &&dbUser!=null &&dbPass!=null){
				this.dbConn=makeDbConnection(url,dbUser,dbPass);
			}
			else{
				WmLog.getCoreLogger().info(">>WARNING: Can not create database connection using url="+url +" and dbUser="+dbUser);
				System.out.println(">>WARNING: Can not create database connection using url="+url +" and dbUser="+dbUser);
			}
		}	
	}
	/*
	 *  Prepare default connection strings for Oracle Rac
	 */
	private String getConnectionStr(String rac, String service, String port) {
		 String url="jdbc:oracle:thin:@(DESCRIPTION ="+
		    "(ADDRESS_LIST ="+
		      "(ADDRESS = (PROTOCOL = TCP)(HOST = "+rac+")(PORT ="+port+"))"+
		    ")"+
		    "(CONNECT_DATA ="+
		      "(SERVICE_NAME = "+service+")"+
		    ")"+
		  ")";
		  if(this.dbProvider.equalsIgnoreCase("cassandra")){
			  url = "jdbc:cassandra://" + rac + ":" + port + "/" + service + "?version=3.0.0"; 
		  }else  if(this.dbProvider.equalsIgnoreCase("mysql")){
			  url = "jdbc:mysql://" + rac + ":" + port + "/" + service ; 
		  }else if(this.dbProvider.equalsIgnoreCase("postgresql")){
			  url = "jdbc:postgresql://" + rac + ":" + port + "/" + service ; 
		  }
		  
	        return url;
	    }

	public Connection getDbConnection(){
		return (this.dbConn);
	}
	
	public void setBrowserDriver(String driver){
		this.driver=driver;		
	}
	
	public HashMap getDbManager(){
		return (this.dbManager);
	}
	private void setupCallableStatement(){
		if(this.actionItem.getName().equalsIgnoreCase("action") && this.actionItem.attributeValue("type").equalsIgnoreCase("API")){
			 String apiName=this.actionItem.attributeValue("apiname");
			 if(!apiName.equals("")){
				 this.isApiTest=true;
				 int numberOfArguments=Integer.valueOf(this.actionItem.attributeValue("fields"));
				 this.cs=makeCallableStatement(apiName, numberOfArguments);	
			 }
		 }
	}
	private void setupData(List <Element> elmlist){
		 HashMap<String, String> globalApiData= new  HashMap<String, String>();
		 WmLog.getCoreLogger().info(">>>Setting up data for Test:<<<"+this.actionItem.getName());
		
		 this.localResultParamSet=new HashMap<String, String>();
		 int index=1;
		
		 ItemUtility iu= new ItemUtility();
	        for(Element vElm: elmlist){
	        	boolean isGlobal=false;
	        	Element elm=vElm;
	        	if(globalApiData.size()>0){	        		
	        		for(Object gTag:globalApiData.keySet()){
	        			elm=iu.replaceAllGlobalData(elm,gTag.toString(),globalApiData.get(gTag));
	        		}
	        		//System.out.println(elm.asXML());
	        	}
	        	if(elm.getName().equalsIgnoreCase("global")){
	        		isGlobal=true;
	        		List <Element> gList=elm.elements();
	        		for(Element gl:gList){
	        			String gVal=iu.getRamdomGlobalTagValue( gl.attributeValue("value"));
	        			globalApiData.put(gl.attributeValue("key"), gVal);
	        			System.out.println("Replaced Global "+gl.attributeValue("key")+"="+gVal);
	        		}
	        	}else if(elm.getName().equalsIgnoreCase("input")){
	            	setupInput(elm,  index);
	            }else if(elm.getName().equalsIgnoreCase("output")){
	            	setupOutput(elm,  index);
	            	this.resultParamIndex.put(elm.attributeValue("name"),index);
	            }else if(elm.getName().equalsIgnoreCase("inoutput")){
	            	setupInput(elm,  index);
	            	setupOutput(elm,  index);
	            	this.resultParamIndex.put(elm.attributeValue("name"),index);	            
	            }else if(this.page.isUnitTest() && elm.getName().equalsIgnoreCase("validator")){
	            	this.vlidatorList.add(elm);
	            	//System.out.println(elm.asXML());
	            }else if( elm.getName().equalsIgnoreCase("validator")){
	            	this.vlidatorList.add(elm);
	            }   
	        	if(!isGlobal){
	        		index++;
	        	}
	        }
	        
	    WmLog.getCoreLogger().info(">>>Data Setup Successful! :<<<"+this.actionItem.getName());
	}
	
	private void setupInput(Element elm, int index){
		
		try{
			String descriptor=elm.attributeValue("descriptor");	
			String arrayDesciptor=elm.attributeValue("arraydescriptor");
			
			/*
			if(elm.attributeValue("type")!=null && elm.attributeValue("type").equalsIgnoreCase("table")){
				if(elm.attributeValue("randomelement")==null || elm.attributeValue("randomelement")==""){
				 
				
				  List<Element> rows=elm.elements();
				 //Structure 
			      StructDescriptor structDesc = StructDescriptor.createDescriptor(descriptor, this.dbConn);

			      ArrayDescriptor arrayDesc = ArrayDescriptor.createDescriptor(arrayDesciptor, this.dbConn);
			          
			      STRUCT[] arrayOfRecords = new STRUCT[rows.size()];
			      
			      int i = 0;

			      for (Element row: rows) {
			        int attrCount= row.attributeCount();   
			        Object[] temp = new Object[attrCount];
			        for(int k=0; k<attrCount; k++){
			        	 String val=row.attribute(k).getValue();
			        	 if(val!=null &&val.equalsIgnoreCase("null")){
			        		 val=null;
			        	 }
			        	 temp[k]=val;
			        }
			                  
			        STRUCT javaSqlObject = new STRUCT(structDesc, dbConn, temp);

			        arrayOfRecords[i] = javaSqlObject;
			        i++;
			      }

			      ARRAY arr = new ARRAY(arrayDesc, dbConn, arrayOfRecords);
			      
			      this.cs.setArray(index, arr);
				}else{
					this.structDescList.add(StructDescriptor.createDescriptor(descriptor, this.dbConn));
					this.arrayDescList.add(ArrayDescriptor.createDescriptor(arrayDesciptor, this.dbConn));
					this.randomElmIndex.add(index);
					this.randomElm.add(elm);
					
				}
			      
		}else */
		if(elm.attributeValue("type")==null || elm.attributeValue("type")==""){
			 String value=elm.getText();
			 String lookuprow=elm.attributeValue("lookuprow");
			 ArrayList<ArrayList<Object>> rsData=null;	
			
			 if(value.indexOf("global")<0 && value.contains(".")){				
				 String prm=value.split("\\.")[0];
				 String field=value.split("\\.")[1];
				 String rsType=this.resultParamSet.get(prm);
				 if(rsType.equalsIgnoreCase("cursor")||rsType.equalsIgnoreCase("query")){					 
					 rsData= (ArrayList<ArrayList<Object>>) this.resultDataSet.get(prm);
					 int row=0;	
					 if(rsData!=null){
						 if(lookuprow.isEmpty()){
							 row=rsData.size()-1;
						 }else{
							 row= Integer.valueOf(lookuprow);
						 }					
						 value=this.getColumnValue(rsData,row,field);						 
					  
						 this.cs.setObject(index, value);					 				  
					 }
				 }
				 
			 }
			 if(value.indexOf("global:")>=0){
				 value=ConfigLoader.getGlobalDataFieldValue(value);
				 
			 }
			 /*
			 if(rsData==null){
				 if(descriptor.equalsIgnoreCase("NUMBER")){
					 if(value.equalsIgnoreCase("NULL")|| value.isEmpty()){
						 this.cs.setNull(index, OracleTypes.NUMBER);					 
					 }else{
						 this.cs.setLong(index, Long.valueOf(value));
					 }
					 
				 }else if(descriptor.equalsIgnoreCase("VARCHAR")||descriptor.equalsIgnoreCase("CHAR")){
					 if(value.equalsIgnoreCase("NULL")|| value.isEmpty()){
						 this.cs.setNull(index, OracleTypes.VARCHAR);					 
					 }else{
						 this.cs.setString(index, value);
					 }
					 
				 }else if(descriptor.equalsIgnoreCase("DATE")){
					 if(value.equalsIgnoreCase("NULL") || value.isEmpty()){
						 this.cs.setNull(index, OracleTypes.DATE);					 
					 }else{
						 if(value.equalsIgnoreCase("sysdate")){
							 
							 java.util.Date today = new java.util.Date();

							 this.cs.setDate(index,   new java.sql.Date (today.getTime()));
						 }else{							
						     SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
						     java.sql.Date fromdate=(java.sql.Date) dateFormat.parse(value);
							 this.cs.setObject(index,  fromdate);
						 }
						 
					 }
					 
				 }
			 } */
		}
		} catch (Exception x) {
	        x.printStackTrace();    
	     } 
	}
	
	private void setupQueryInput(PreparedStatement ps,Element elm, int index){
		
		try{
			String descriptor=elm.attributeValue("descriptor");	
			
			/*
			if(elm.attributeValue("type")!=null && elm.attributeValue("type").equalsIgnoreCase("table")){
				
				String arrayDesciptor=elm.attributeValue("arraydescriptor");
				
				List<Element> rows=elm.elements();
				 //Structure 
			      StructDescriptor structDesc = StructDescriptor.createDescriptor(descriptor, this.dbConn);

			      ArrayDescriptor arrayDesc = ArrayDescriptor.createDescriptor(arrayDesciptor, this.dbConn);
			          
			      STRUCT[] arrayOfRecords = new STRUCT[rows.size()];
			      
			      int i = 0;

			      for (Element row: rows) {
			        int attrCount= row.attributeCount();   
			        Object[] temp = new Object[attrCount];
			        for(int k=0; k<attrCount; k++){
			        	 temp[k]=row.attribute(k).getValue();
			        }
			                  
			        STRUCT javaSqlObject = new STRUCT(structDesc, dbConn, temp);

			        arrayOfRecords[i] = javaSqlObject;
			        i++;
			      }

			      ARRAY arr = new ARRAY(arrayDesc, dbConn, arrayOfRecords);
			      
			      ps.setArray(index, arr);
			      
		}else */
			
			if(elm.attributeValue("type")==null || elm.attributeValue("type")==""){
			 String value=elm.getText();
			 String lookuprow=elm.attributeValue("lookuprow");
			 ArrayList<ArrayList<Object>> rsData=null;	
			 
			 if(value.indexOf("global")<0 && value.contains(".")){				
				 String prm=value.split("\\.")[0];
				 String field=value.split("\\.")[1];
				 String rsType=this.resultParamSet.get(prm);
				 if(rsType.equalsIgnoreCase("cursor")||rsType.equalsIgnoreCase("query")){					 
					 rsData= (ArrayList<ArrayList<Object>>) this.resultDataSet.get(prm);
					 int row=0;	
					 if(rsData!=null){
						 if(lookuprow==null||lookuprow.isEmpty()){
							 row=rsData.size()-1;
						 }else{
							 row= Integer.valueOf(lookuprow);
						 }					
						 value=this.getColumnValue(rsData,row,field);						 
					  
						 ps.setObject(index, value);
						 
						 WmLog.getCoreLogger().info(" Set value for "+field+"="+value);
							
	        			 System.out.println(" Set value for "+field+"="+value);
	        		
					 				  
					 }
					
					
				 }
				 
			 }
			 if(value.indexOf("global:")>=0){
				 String param=value;
				 value=new ItemUtility().getRamdomGlobalTagValue(value);
				 
				 WmLog.getCoreLogger().info("Set value for "+param+"="+value);
					
    			 System.out.println("Set value for "+param+"="+value);
			 }
			 /*
			if(rsData==null){
				 if(descriptor.equalsIgnoreCase("NUMBER")){
					 if(value.equalsIgnoreCase("NULL")){
						 ps.setNull(index, OracleTypes.NUMBER);					 
					 }else{
						 ps.setLong(index, Long.valueOf(value));
					 }
					 
				 }else if(descriptor.equalsIgnoreCase("VARCHAR")||descriptor.equalsIgnoreCase("CHAR")){
					 if(value.equalsIgnoreCase("NULL")){
						 ps.setNull(index, OracleTypes.VARCHAR);					 
					 }else{
						 ps.setString(index, value);
					 }
					 
				 }
			 }
			 */
		}
		} catch (Exception x) {
	        x.printStackTrace();    
	     } 
	}
	
	private void setRandomElements(){
		/*
		int elmIndex=0;
		try {
		for(Element rElm:this.randomElm){
		String descriptor=rElm.attributeValue("descriptor");	
		String randomelement=rElm.attributeValue("randomelement");
	    int randomsize=Integer.valueOf(rElm.attributeValue("randomsize"));
	
		if(rElm.attributeValue("type")!=null && rElm.attributeValue("type").equalsIgnoreCase("table")){
		
			  String arrayDesciptor=rElm.attributeValue("arraydescriptor");
			
			  List<Element> rows=rElm.elements();
			 //Structure 
		      StructDescriptor 	structDesc = this.structDescList.get(elmIndex);		    	 
			
		      ArrayDescriptor arrayDesc = this.arrayDescList.get(elmIndex);
		      
		      int i = 0;
		      STRUCT[] arrayOfRecords=null;
		      for (Element row: rows) {
		    	WmLog.getCoreLogger().info(">>>Adding Random Element ="+row.getName()+" >>>Action="+this.action);
					
		        int attrCount= row.attributeCount();   
		       
		       arrayOfRecords = new STRUCT[randomsize];
		       
		       //search for global element
		        String randonElmValue=(row.attributeValue(randomelement).indexOf("global:")>=0?
		        		ConfigLoader.getGlobalDataFieldValue(row.attributeValue(randomelement)):row.attributeValue(randomelement));
		        String randomValues[]=randonElmValue.split(",");
		        Random generator = new Random();
		        for(int c=0;c<randomsize;c++){
		        	  
		        	String[] temp = new String[attrCount];
			        for(int k=0; k<attrCount; k++){
			        	
	        			if(row.attribute(k).getName().equalsIgnoreCase(randomelement)){
	        				int randomIndex = generator.nextInt( randomValues.length );
	        				temp[k]=randomValues[randomIndex];
	        				WmLog.getCoreLogger().info(randomelement+"="+randomValues[randomIndex].trim());
							
	        				System.out.println(randomelement+"="+randomValues[randomIndex].trim());
	        			}else{
	        				temp[k]=row.attribute(k).getValue();
	        			}
			        	
			        }
			        try{          
			        STRUCT javaSqlObject = new STRUCT(structDesc, this.dbConn, temp);
			        arrayOfRecords[i] = javaSqlObject;
			        }catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
			        i++;
		        }
		       
		      }

		      ARRAY arr = new ARRAY(arrayDesc, dbConn, arrayOfRecords);
		      
		      this.cs.setArray(this.randomElmIndex.get(elmIndex), arr);
		      elmIndex++;
		      }
			
		}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
	}
	private void setupOutput(Element elm, int index){
		/*String descriptor=elm.attributeValue("descriptor");	
		String recordtype=elm.attributeValue("recordtype");
		String value=elm.getText();
		//System.out.println("Attribute="+elm.attributeValue("name") +" Descriptor="+descriptor );
		this.cursorList=new ArrayList<Integer>();
		try{
		 if(descriptor.equalsIgnoreCase("CURSOR")){
			 //System.out.println(">>>Cursor Attribute="+elm.attributeValue("name") +" Descriptor="+descriptor );
			 this.cs.registerOutParameter(index, OracleTypes.CURSOR);
			 cursorList.add(index);
			 this.resultParamSet.put(elm.attributeValue("name"),"CURSOR");
			 this.localResultParamSet.put(elm.attributeValue("name"),"CURSOR");
		 }else if(descriptor.equalsIgnoreCase("NUMBER")){
			 this.cs.registerOutParameter(index, OracleTypes.NUMBER);
			 this.resultParamSet.put(elm.attributeValue("name"),"NUMBER");
			 this.localResultParamSet.put(elm.attributeValue("name"),"NUMBER");
		 }else if(descriptor.equalsIgnoreCase("VARCHAR")){
			 this.cs.registerOutParameter(index, OracleTypes.VARCHAR);	
			 this.resultParamSet.put(elm.attributeValue("name"),"VARCHAR");
			 this.localResultParamSet.put(elm.attributeValue("name"),"VARCHAR");
		 }else if(descriptor.equalsIgnoreCase("DATE")){
			 this.cs.registerOutParameter(index, OracleTypes.DATE);	
			 this.resultParamSet.put(elm.attributeValue("name"),"DATE");
			 this.localResultParamSet.put(elm.attributeValue("name"),"DATE");
		 }else if(descriptor.equalsIgnoreCase("table")){
			 this.cs.registerOutParameter(index, OracleTypes.PLSQL_INDEX_TABLE,recordtype);
			 //this.cs.registerOutParameter(index, OracleTypes.ARRAY);			 
			 this.resultParamSet.put(elm.attributeValue("name"),"TABLE");
			 this.localResultParamSet.put(elm.attributeValue("name"),"TABLE");
		 }
		} catch (Exception x) {
	        x.printStackTrace();    
	     } */
		
	}
	
	
	
    /*
     * Prepare a Sql Statement
     */
	private PreparedStatement makeSqlStatement(String sql, List<Element> elm){
		PreparedStatement ps=null;
		int index=1;
		try {
		
			ps=this.dbConn.prepareStatement(sql);
			for(Element vInput:elm){
				this.setupQueryInput(ps, vInput, index);
				index++;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ps;
	}
	/*
	 * Pass Name of API and number of bind variable
	 */
	 private CallableStatement makeCallableStatement(String ApiName, int numberOfArguments) {
		  
         String bindVars = null; 

         for(int i=0; i < numberOfArguments; i++) {

             if (bindVars == null) {
                 bindVars = "?";
             }
             else {
                 bindVars = bindVars + ",?";
             }
         }
                
         	 try {
         		 if (bindVars == null) {
         			this.cs=(CallableStatement) this.dbConn.prepareCall("{ call " + ApiName + "}");
         		 }else{
         			 this.cs=(CallableStatement) this.dbConn.prepareCall("{ call " + ApiName + "(" + bindVars + ")" + "}");
         		 }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.actionErrorMessage.put(action, "FAILED >>>"+e.getMessage());
				
			}
			
 	    return (this.cs);
    }
	
	private boolean execute() {
		 
	      try {	    	  
	    	  this.cs.execute();	    	  
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.actionErrorMessage.put(action, "FAILED >>>"+e.getMessage());
			return false;
		} 
	   
	     return true;
		
	}
	
	
	/*
	 * Reset the procedure
	 */
	private void resetProc(){
		 
		try {
			if(!this.cs.isClosed() && !this.page.isUnitTest() ){
				for(int i : cursorList){
					ResultSet rs=null;
					try {
							WmLog.getCoreLogger().info(">>>API Executed Successfuly! :<<< Record Effected: "+cs.getFetchSize());
							
							//add time for opening the cursor
							long starttime=this.getCurrentTime();
					
						    rs = (ResultSet)this.cs.getObject(i);
					
						    int count=rs.getRow();
						    long elaspedTime= this.getElaspedTime(starttime);						    
						    this.addCursorOpenTime(this.getActionName(),elaspedTime);
						    
							
						   //Print only first column
						    if(!this.page.isUnitTest()  && ConfigLoader.getConfig("PRINT_API_OUTPUT").equalsIgnoreCase("off")){
						    	printFirstColumn(rs);
						    }
						    
						    if(this.page.isUnitTest() || ConfigLoader.getConfig("PRINT_API_OUTPUT").equalsIgnoreCase("on")){
						    	printColumnValues(rs);
						    }
						    
							if(rs!=null)
								rs.close();
						
							
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	private void printColumnValues(ResultSet rs){
		try{
			WmLog.getCoreLogger().info(">>>Printing API Output:");
		    int columnCount= rs.getMetaData().getColumnCount();
			while( !rs.isClosed() &&rs.next()) {
				for(int col=1;col<=columnCount;col++){
					WmLog.getCoreLogger().info(" "+rs.getMetaData().getColumnName(col) +" ="+rs.getString(col));						
					System.out.println(" "+rs.getMetaData().getColumnName(col) +" ="+rs.getString(col));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void printFirstColumn(ResultSet rs){
		try{
			WmLog.getCoreLogger().info(">>>Printing API Output:");
		  
			while( !rs.isClosed() &&rs.next()) {
					WmLog.getCoreLogger().info(" "+rs.getMetaData().getColumnName(1) +" ="+rs.getString(1));						
					System.out.println(" "+rs.getMetaData().getColumnName(1) +" ="+rs.getString(1));
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
 	public void handleProc() throws Exception {      
 	   
 		this.setRandomElements();
        this.startRecording();
        if(this.cs!=null && !this.cs.isClosed()){        	
	        long starttime=this.getCurrentTime();
	        boolean response=this.execute();
	        // Measure the time passed between response
	        long elaspedTime= this.getElaspedTime(starttime);	        
	        this.stopDbRecording(response,elaspedTime);  
	        this.addThreadActionMonitor(this.action, response, elaspedTime);
	        this.printDbRecording();
	        this.printDbLog();
	        if(this.page.isUnitTest()){
	        	this.fillupResultDataset();
	        	this.executeValidate();
	        }
	       	this.resetProc();	        
        }
 	   if(!this.isApiTest){
 		  boolean status=false;
 		  long starttime=this.getCurrentTime();
 		  status=this.executeValidate(); 
 		  long elaspedTime= this.getElaspedTime(starttime); 
 		  this.stopDbRecording(status,elaspedTime);  
 		  this.addThreadActionMonitor(this.action, status, elaspedTime);
 	   }
 	  this.writeSetupDataFile() ;  
    }
 	private void fillupResultDataset(){
 		Set rsParam=localResultParamSet.keySet();
 		for(Object param:rsParam){
 			String type=localResultParamSet.get(param);
 			int index=this.resultParamIndex.get(param);
 			//System.out.println("Param="+param+ " Type="+type +" Index="+index);
 			try {
	 			if(type.equalsIgnoreCase("CURSOR")){
	 				ResultSet rs = (ResultSet)this.cs.getObject(index);
	 				this.resultDataSet.put((String)param,this.setResults2Array(rs,null));
	 				//set as global dataset
					this.setXMLDataset(param.toString());
	 				//this.resultDataSet.put((String)param,(Object) rs);
	 			}else{	 				
					this.resultDataSet.put((String) param,this.cs.getString(index));
	 			}
 			} catch (SQLException e) {
 				String msg="SQLException: CURSOR- "+param+ ">>"+e.getMessage() +". No Record exists!";
 				WmLog.getCoreLogger().info(msg);						
				System.out.println(msg);
			}
 		}
 	}
 	private boolean executeValidate() { 		
 		
 		Element globalDataSet=null; 		
 	    String dataset=this.actionItem.attributeValue("dataset");
 	    isDatasetValue=false;
 	    if(dataset!=null &&!dataset.isEmpty()){
 	    	isDatasetValue=true;
 	    	String queryName=dataset.replace("global:query.", "");
 	    	Object dbmsOutput=ConfigLoader.getGlobalDataFieldValue(queryName.trim());
 	    	if(dbmsOutput!=null &&!dbmsOutput.toString().isEmpty()){ 
 	    		this.resultParamSet.put(queryName,"query");
 	    		this.resultDataSet.put(queryName,this.setDbmsOutput2Array(dbmsOutput.toString()));
 	    	}
 	    	globalDataSet=ConfigLoader.getGlobalDataSet(dataset);
 	    }
 	    
 	    if(globalDataSet!=null){
 	    	this.executeDataSet();
 	    }else{
 	    	Element data=iu.getRootElementFromXML("<data></data>");
 	    	data=this.addRandomData(data); 	  
 	    	String attr_overrides=this.page.getOverrideAttributes();
 	    	if(attr_overrides!=null &&!attr_overrides.isEmpty()){
	 			  String[] overrides=attr_overrides.split(";");
	 			 for(String override:overrides){
	 				 if(override.contains("=")){
	 					 String[] pwds=override.split("=");
	 					 data.addAttribute(pwds[0], pwds[1]);
	 				 }
	 			 }
	 		  }
 	    	this.executeValidator(data);
 	    }
		return (true);
		
	}
 	
  private boolean executeDataSet() {
 		
 		Element globalDataSet=null;
 		ExecutorService exec=null;
 	    String dataset=this.actionItem.attributeValue("dataset");
 	    String datarowindex=this.actionItem.attributeValue("datarowindex");
 	    String attr_overrides=this.page.getOverrideAttributes();
 	    this.isMultiThread=this.page.isMultiBrowserTest();
 	    if(dataset!=null &&!dataset.isEmpty()){
 	    	if(this.page.getDatasetExtension()!=null && !this.page.getDatasetExtension().isEmpty()){
 	    		String[] tags=dataset.split("\\.");
 	    		globalDataSet=ConfigLoader.getGlobalDataSet(tags[0]+"."+this.page.getDatasetExtension()+tags[1]);
 	    		if(globalDataSet==null){
 	    			globalDataSet=ConfigLoader.getGlobalDataSet(tags[0]+"."+tags[1]+this.page.getDatasetExtension());
 	    		}
 	    	}
 	    	
 	    	if(globalDataSet==null){
 	    		globalDataSet=ConfigLoader.getGlobalDataSet(dataset);
 	    	}
 	    }
 	    
 	    if(globalDataSet!=null && !this.isMultiThread){
 	    	int datasetIndex=0;
 	    	List<Element> datas=globalDataSet.elements();
 	    	for(Element data:datas){
 	    		datasetIndex=datasetIndex+1;
 	    		data.addAttribute("datasetIndex", String.valueOf(datasetIndex));
 	    		data=this.addRandomData(data); 	    		
 	    		 //password override or override any column data from dataset
	 	 		  if(attr_overrides!=null &&!attr_overrides.isEmpty()){
	 	 			  String[] overrides=attr_overrides.split(";");
	 	 			 for(String override:overrides){
	 	 				 if(override.contains("=")){
	 	 					 String[] pwds=override.split("=");
	 	 					 data.addAttribute(pwds[0], pwds[1]);
	 	 				 }
	 	 			 }
	 	 		  }
 	    		if(datarowindex!=null &&!datarowindex.isEmpty()&& String.valueOf(datasetIndex).equals(datarowindex)){
 	    			executeValidator(this.replaceData(data));
 	    		}else if(datarowindex==null||datarowindex.isEmpty()){
 	    			executeValidator(this.replaceData(data));
 	    		}
 	    	}
 	    
 	    }else if(globalDataSet!=null &&this.isMultiThread ){	    	
	    	List<Element> datas=globalDataSet.elements();
	    	exec=executeDatasetThread(datas);
	    	
	    }
 	 
 	  try {
       	if(exec!=null){
       		exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);        		
       	}
           
       } catch (InterruptedException ex) {
          
       } 
		return (true);
		
	}
  /*
   * Executor for Frontend Unit Test
   */
  private ExecutorService executeDatasetThread(List<Element> datas){
	  String[] browsers=this.page.getBrowsers();
	  String attr_overrides=this.page.getOverrideAttributes();
	  int threadCount=this.page.getThreadCount();
	  String groupbythread=this.page.getGroupByThread();
	  String datarowindex=this.actionItem.attributeValue("datarowindex");
	  String groupbyvalue="";
	  String tmpgroupbyvalue="";
	  int dataSize=datas.size();
	  int i=0;
	  int datasetIndex=0;
	  if(dataSize<threadCount){
		  threadCount=dataSize;
	  }
	  //limit threadCount max to 10
	  if(browsers.length>=2 &&threadCount>2){
		  threadCount=2;
	  }else if(threadCount>10){
		  threadCount=10;
	  }
	  if(datarowindex!=null &&!datarowindex.isEmpty()&& String.valueOf(datasetIndex).equals(datarowindex)){
		  threadCount=1;
	  }
	  ArrayList<ArrayList<Element>> threadData= new ArrayList<ArrayList<Element>>();
	  for(Element data:datas){
		  ArrayList<Element> elmList= new ArrayList<Element>();
		 try{
		  if(threadData.get(i)==null){
			  threadData.add(elmList);
		  }
		 }catch(Exception e){
			 threadData.add(elmList);
		 }
		  datasetIndex=datasetIndex+1;
		  data.addAttribute("datasetIndex", String.valueOf(datasetIndex));
		  //System.out.println(data.asXML());
		  //password override or override any column data from dataset
		  if(attr_overrides!=null &&!attr_overrides.isEmpty()){
			  String[] overrides=attr_overrides.split(";");
			 for(String override:overrides){
				 if(override.contains("=")){
					 String[] pwds=override.split("=");
					 data.addAttribute(pwds[0], pwds[1]);
				 }
			 }
		  }
		  if(groupbythread!=null &&!groupbythread.isEmpty() ){
			  tmpgroupbyvalue=data.attributeValue(groupbythread);
		  }
		  if(tmpgroupbyvalue!=null &&!tmpgroupbyvalue.isEmpty()){
			 if(groupbyvalue.isEmpty()){
				 i=0;
				 groupbyvalue=tmpgroupbyvalue;
			 }else if(!groupbyvalue.equals(tmpgroupbyvalue)){
				 i++;
				 threadData.add(elmList);
				 groupbyvalue=tmpgroupbyvalue;
			 }
			 threadData.get(i).add(data);
			 threadCount=i+1;
		  }else{
			  threadData.get(i).add(data);
			  if(i==threadCount-1){
				  i=0;
			  }else{
				  i++;
			  }
		  }
		  
	  }
  	 
  	 ExecutorService exec = Executors.newFixedThreadPool(threadCount*browsers.length);
  	 for(String browser: browsers){
  		BrowserDatasetThread browserThread=null;
       for(int j=0;j<threadCount;j++){
    	   ArrayList<JunitHandler> junitHandlers= new ArrayList<JunitHandler>();
    	   int index=0;
    	   for(Element data:threadData.get(j)){
    		   index++;
    		   ArrayList<JunitHandler> tmpHandlers=null;
    		   if(datarowindex!=null &&!datarowindex.isEmpty()&& String.valueOf(index).equals(datarowindex)){
    			   this.executeValidator(data);
    	       		data.addAttribute("driver",  browser);       		
    	       		data.addAttribute("runstatus",  "available");
    	       		data=this.addRandomData(data);
    	       		tmpHandlers= this.executeJunitThreadData(replaceData(data));
    	       		int handlerIdx=1;
    	       		for(JunitHandler handler: tmpHandlers){
    	       			handler.setHandlerName("step"+handlerIdx++);
    	       			junitHandlers.add(handler);
    	       		}
	    		}else if(datarowindex==null||datarowindex.isEmpty()){
	    			this.executeValidator(data);
	           		data.addAttribute("driver",  browser);       		
	           		data.addAttribute("runstatus",  "available");
	           		data=this.addRandomData(data);
	           		tmpHandlers= this.executeJunitThreadData(replaceData(data));
	           		int handlerIdx=1;
	           		for(JunitHandler handler: tmpHandlers){
	           			handler.setHandlerName("step"+handlerIdx++);
	           			junitHandlers.add(handler);
	           		}
	    		}
    		
       		tmpHandlers=null;
       		//junitHandlers.add(this.executeJunitThreadData(replaceData(data)));
       	}
    	   browserThread=new BrowserDatasetThread(this,junitHandlers);
           exec.execute(browserThread);
           junitHandlers=null;
       }
  	 }
       exec.shutdown();
       return(exec);
  }
  
  private Element replaceData(Element data){
	  //ItemUtility iu=new ItemUtility();
	  String xml=data.asXML();
	  List<DefaultAttribute> attrs=data.attributes();
		for(DefaultAttribute attr:attrs){
			xml=xml.replaceAll("@"+attr.getQualifiedName(), attr.getStringValue().trim());
		}
		Element newElm=iu.getRootElementFromXML(xml);
		
		return newElm;
  }
  
  public boolean executeValidator(Element data) {
		
		String validator="";
		Element globalDataSet=null;
		//ItemUtility utl= new ItemUtility();
	    boolean hasVariables=false;
		for(Element elm: vlidatorList){
			//System.out.println(elm.asXML());
			List<Element> vElm=elm.elements();
			List<Element> vQuery=new ArrayList<Element>();
			List<Element> vApi=new ArrayList<Element>();
			List<Element> vInput=new ArrayList<Element>();
			List<Element> vAssert=new ArrayList<Element>();
			List<Element> vSetup=new ArrayList<Element>();
			List<Element> vWs=new ArrayList<Element>();
			List<Element> vJunit=new ArrayList<Element>();
			List<Element> vSpark=new ArrayList<Element>();
			List<Element> vCommand=new ArrayList<Element>();
			List<Element> variables=new ArrayList<Element>();
			validator=elm.attributeValue("name");
			for(Element vEach:vElm){
				if(vEach.getName().equalsIgnoreCase("sql")){
					
					//replace sql lib if included
					String includes=vEach.attributeValue("includes");
					if(includes!=null &&!includes.isEmpty()){
						vQuery.add(iu.replaceIncludeInSqlQuery(vEach));
					}else{
						vQuery.add(vEach);
					}
				}else if(vEach.getName().equalsIgnoreCase("ws")){
					vWs.add(vEach);
				}else if(vEach.getName().equalsIgnoreCase("junit")||vEach.getName().equalsIgnoreCase("testng")){
					vJunit.add(vEach);
				}else if(vEach.getName().equalsIgnoreCase("spark")){
					vSpark.add(vEach);
				}else if(vEach.getName().equalsIgnoreCase("command")){
					vCommand.add(vEach);
				}else if(vEach.getName().equalsIgnoreCase("api")){
					vApi.add(vEach);				
				}else if(vEach.getName().equalsIgnoreCase("input")){
					vInput.add(vEach);
				}else if(vEach.getName().equalsIgnoreCase("assert")){
					vAssert.add(vEach);
				}else if(vEach.getName().equalsIgnoreCase("setup")){
					//replace sql lib if included
					String includes=vEach.attributeValue("includes");
					if(includes!=null &&!includes.isEmpty()){
						vSetup.add(iu.replaceIncludeInSqlQuery(vEach));
					}else{
						vSetup.add(vEach);
					}					
				}else if(vEach.getName().equalsIgnoreCase("variables")){
					 hasVariables=true;
					//fillup as variable for each attribute
					 if(data!=null){						 
						List<DefaultAttribute> attrs=data.attributes();
						for(DefaultAttribute attr:attrs){
							validator=validator.replaceAll("@"+attr.getQualifiedName().trim(), data.attributeValue(attr.getQualifiedName()).trim());
						}
						
					 }else{
						
						 vEach=this.addRandomElementData(vEach);
						 
					 }
					
					variables.add(vEach);
				}				
			}
			//if dataset presents and variables are not defined
			//if(data!=null){
				Element vVars=iu.getRootElementFromXML("<variables></variables>");
				
				//fillup as variable for each attribute
				 if(data!=null){
					List<DefaultAttribute> attrs=data.attributes();
					for(DefaultAttribute attr:attrs){
						Element sData=vVars.addElement("var");
						sData.addAttribute("id", attr.getQualifiedName().trim());
						sData.addAttribute("value", data.attributeValue(attr.getQualifiedName()).trim());
						validator=validator.replaceAll("@"+attr.getQualifiedName().trim(), data.attributeValue(attr.getQualifiedName()).trim());
					}
					
				 }else{
					 vVars=this.addRandomElementData(vVars);
				 }
				variables.add(vVars);
				
			//}
			
			List<Element> itemlist=new ArrayList();
			List<Element> varlist=new ArrayList();
			for(Element wsProp:variables){
				if(wsProp.getName().equalsIgnoreCase("variables")){
					List <Element> elList=wsProp.elements();
					for(Element el:elList){
						if(el.getName().equalsIgnoreCase("var")){
							varlist.add(el);
						}else if(el.getName().equalsIgnoreCase("item")){
							itemlist.add(el);
						}
					}
					
				}
			}
			//replace items id in the validator level
			if(!itemlist.isEmpty()){
				if(!vWs.isEmpty())
					vWs=iu.replaceValidatorElementItemId(vWs, itemlist);
				if(!vQuery.isEmpty())
					vQuery=iu.replaceValidatorElementItemId(vQuery, itemlist);
				if(!vApi.isEmpty())
					vApi=iu.replaceValidatorElementItemId(vApi, itemlist);
				if(!vSpark.isEmpty())
					vSpark=iu.replaceValidatorElementItemId(vSpark, itemlist);		
				if(!vJunit.isEmpty())
					vJunit=iu.replaceValidatorElementItemId(vJunit, itemlist);		
				if(!vSetup.isEmpty())
					vSetup=iu.replaceValidatorElementItemId(vSetup, itemlist);
				if(!vAssert.isEmpty())
					vAssert=iu.replaceValidatorElementItemId(vAssert, itemlist);
			}
			//Replace variables
			if(!varlist.isEmpty()){
				if(!vWs.isEmpty())
					vWs=iu.replaceValidatorElementVariables(vWs, varlist, this.resultParamSet, this.resultDataSet);
				if(!vQuery.isEmpty())
					vQuery=iu.replaceValidatorElementVariables(vQuery, varlist, this.resultParamSet, this.resultDataSet);
				if(!vJunit.isEmpty())
					vJunit=iu.replaceValidatorElementVariables(vJunit, varlist, this.resultParamSet, this.resultDataSet);
				if(!vApi.isEmpty())
					vApi=iu.replaceValidatorElementVariables(vApi, varlist, this.resultParamSet, this.resultDataSet);				
				if(!vSetup.isEmpty())
					vSetup=iu.replaceValidatorElementVariables(vSetup, varlist, this.resultParamSet, this.resultDataSet);
				if(!vAssert.isEmpty())
					vAssert=iu.replaceValidatorElementVariables(vAssert, varlist, this.resultParamSet, this.resultDataSet);
			}
			
			//Execute Webservice call
			vQuery=this.executeWs(vWs,vQuery,varlist);
			
		    //Execute API call
			this.executeAPI(vApi);
			
			//Execute SQL / Validator Query
			this.executevQuery( vQuery, vInput,  validator);
			
			// execute Setup
			this.executeSetup(vSetup);
			
			if(!this.isMultiThread){
				//Execute dataset			 
				try{
					datasetIndex=new Integer(data.attributeValue("datasetIndex"));
				}catch (Exception e){
					datasetIndex=0;
				}
				//Execute Junit
				if(vJunit.size()>0){
					this.executeJunit(vJunit,varlist,itemlist,validator);
				}
				
				//Execute Spark
				if(vSpark.size()>0){
					this.executeSpark(vSpark,varlist,validator);
				}
				//Execute Command
				this.executeCommand(vCommand);
				
				//execute Asserts
				this.executeAssert( vAssert,  validator,  this.wsName,datasetIndex,null,null);
			}
			
			
	
		}
	    	
	return (true);
	
}
    
    private Element addRandomData(Element elm){
    	
    	 //ItemUtility iu= new ItemUtility();
    	 
	    	 //add big random
	    	 String randomnum=iu.getRandomNumber();	
	    	 elm.addAttribute("random",randomnum);	
	    	 elm=iu.replaceVariable(elm, "random", randomnum);
	    	 
	    	 //add small random
	    	 String smallrandomnum=iu.getSmallRandomNumber();	    	
	    
	    	 elm.addAttribute("smallrandom",smallrandomnum);
	    	 elm=iu.replaceVariable(elm, "smallrandom", smallrandomnum);
	    	 addTestSuiteData(elm);
    	
    	 return elm;
    	
    }
    
   
    private Element addTestSuiteData(Element elm){
    	File workspace=null;
    	if(TestEngine.suite!=null){
    	    workspace= new File(TestEngine.suite.getWorkspace());
    		if(workspace.isDirectory()){
    			elm.addAttribute("workspace",TestEngine.suite.getWorkspace().replace(".", "\\."));
    		}else{
    			elm.addAttribute("workspace",ConfigLoader.getWmRoot().replace(".", "\\."));
    		}
    	}else{
    		elm.addAttribute("workspace",ConfigLoader.getWmRoot().replace(".", "\\."));
    	}
    	
    	// add additional dates
    	PrintTime pt =new PrintTime();
    	elm.addAttribute("today", pt.getDateByFormat(0, "MM/dd/yyyy"));
    	elm.addAttribute("yesterday", pt.getDateByFormat(-1, "MM/dd/yyyy"));
    	elm.addAttribute("tomorrow", pt.getDateByFormat(1, "MM/dd/yyyy"));
    	elm.addAttribute("oneweekbefore", pt.getDateByFormat(-7, "MM/dd/yyyy"));
    	elm.addAttribute("onemonthbefore", pt.getDateByFormat(-30, "MM/dd/yyyy"));
    	elm.addAttribute("oneyearbefore", pt.getDateByFormat(-365, "MM/dd/yyyy"));
    	elm.addAttribute("oneweekafter", pt.getDateByFormat(7, "MM/dd/yyyy"));
    	elm.addAttribute("onemonthafter", pt.getDateByFormat(30, "MM/dd/yyyy"));
    	elm.addAttribute("oneyearafter", pt.getDateByFormat(365, "MM/dd/yyyy"));
    	elm.addAttribute("onehourlate", pt.getTimeInMiliFromHr(1));
    	elm.addAttribute("fivehourlate", pt.getTimeInMiliFromHr(5));
    	elm.addAttribute("fiftyhourlate", pt.getTimeInMiliFromHr(50));
    	elm.addAttribute("thousandhourlate", pt.getTimeInMiliFromHr(1000));
    	elm.addAttribute("fivethousandhourlate", pt.getTimeInMiliFromHr(5000));
    	elm.addAttribute("now", pt.getTimeInMiliFromMiniute(0));
    	elm.addAttribute("oneminlate", pt.getTimeInMiliFromMiniute(1));
    	elm.addAttribute("twominlate", pt.getTimeInMiliFromMiniute(2));
    	elm.addAttribute("threeminlate", pt.getTimeInMiliFromMiniute(3));
    	elm.addAttribute("fourminlate", pt.getTimeInMiliFromMiniute(4));
    	elm.addAttribute("fiveminlate", pt.getTimeInMiliFromMiniute(5));
    	elm.addAttribute("tenminlate", pt.getTimeInMiliFromMiniute(10));
    	elm.addAttribute("thirtyminlate", pt.getTimeInMiliFromMiniute(30));
   	
   	 return elm;
   	
   }
    
    private Element addRandomElementData(Element elm){
    	
   	 //ItemUtility iu= new ItemUtility();
   	 
	    	 //add big random
	    	 String randomnum=iu.getRandomNumber();	
	    	 Element sData=elm.addElement("var");
			 sData.addAttribute("id", "random");
			 sData.addAttribute("value", randomnum);
	    	 elm=iu.replaceVariable(elm, "random", randomnum);
	    	 
	    	 //add small random
	    	 String smallrandomnum=iu.getSmallRandomNumber();	    	
	    	 sData=elm.addElement("var");
			 sData.addAttribute("id", "smallrandom");
			 sData.addAttribute("value", smallrandomnum);
	    	 elm=iu.replaceVariable(elm, "smallrandom", smallrandomnum);
	    	 String xml=elm.asXML();
	    	 
	    	 File workspace=null;
	    	 sData=elm.addElement("var");
			 sData.addAttribute("id", "workspace");
			
	     	if(TestEngine.suite!=null){
	     	    workspace= new File(TestEngine.suite.getWorkspace());
	     		if(workspace.isDirectory()){
	     			sData.addAttribute("value", TestEngine.suite.getWorkspace().replace(".", "\\."));
	     			
	     		}else{
	     			sData.addAttribute("value",ConfigLoader.getWmRoot().replace(".", "\\."));
	     		}
	     	}else{
	     		sData.addAttribute("value",ConfigLoader.getWmRoot().replace(".", "\\."));
	     	}
	     	
	     	// add additional dates
	     	PrintTime pt =new PrintTime();
	     	 sData=elm.addElement("var");
			 sData.addAttribute("id", "today");
			 sData.addAttribute("value",  pt.getDateByFormat(0, "MM/dd/yyyy"));
			 
			 sData=elm.addElement("var");
			 sData.addAttribute("id", "yesterday");
			 sData.addAttribute("value",  pt.getDateByFormat(-1, "MM/dd/yyyy"));
			 
			 sData=elm.addElement("var");
			 sData.addAttribute("id", "tomorrow");
			 sData.addAttribute("value",  pt.getDateByFormat(1, "MM/dd/yyyy"));
			 
			 sData=elm.addElement("var");
			 sData.addAttribute("id", "oneweekbefore");
			 sData.addAttribute("value",  pt.getDateByFormat(-7, "MM/dd/yyyy"));
			 
			 sData=elm.addElement("var");
			 sData.addAttribute("id", "onemonthbefore");
			 sData.addAttribute("value",  pt.getDateByFormat(-30, "MM/dd/yyyy"));
			 
			 sData=elm.addElement("var");
			 sData.addAttribute("id", "oneyearbefore");
			 sData.addAttribute("value",  pt.getDateByFormat(-365, "MM/dd/yyyy"));
	     	
			 sData=elm.addElement("var");
			 sData.addAttribute("id", "oneweekafter");
			 sData.addAttribute("value",  pt.getDateByFormat(7, "MM/dd/yyyy"));
			 
			 sData=elm.addElement("var");
			 sData.addAttribute("id", "onemonthafter");
			 sData.addAttribute("value",  pt.getDateByFormat(30, "MM/dd/yyyy"));
			 
			 sData=elm.addElement("var");
			 sData.addAttribute("id", "oneyearafter");
			 sData.addAttribute("value",  pt.getDateByFormat(365, "MM/dd/yyyy"));
	     	
			 sData=elm.addElement("var");
			 sData.addAttribute("id", "onehourlate");
			 sData.addAttribute("value",  pt.getTimeInMiliFromHr(1));
	     	
			 sData=elm.addElement("var");
			 sData.addAttribute("id", "fivehourlate");
			 sData.addAttribute("value",  pt.getTimeInMiliFromHr(5));
			 
			 sData=elm.addElement("var");
			 sData.addAttribute("id", "fiftyhourlate");
			 sData.addAttribute("value",  pt.getTimeInMiliFromHr(50));
			 
			 sData=elm.addElement("var");
			 sData.addAttribute("id", "thousandhourlate");
			 sData.addAttribute("value",  pt.getTimeInMiliFromHr(1000));
			 
			 sData=elm.addElement("var");
			 sData.addAttribute("id", "fivethousandhourlate");
			 sData.addAttribute("value",  pt.getTimeInMiliFromHr(5000));
			 
			 sData=elm.addElement("var");
			 sData.addAttribute("id", "now");
			 sData.addAttribute("value", pt.getTimeInMiliFromMiniute(0));
			 
			 sData=elm.addElement("var");
			 sData.addAttribute("id", "oneminlate");
			 sData.addAttribute("value", pt.getTimeInMiliFromMiniute(1));
			 
			 sData=elm.addElement("var");
			 sData.addAttribute("id", "twominlate");
			 sData.addAttribute("value", pt.getTimeInMiliFromMiniute(2));
	     
			 sData=elm.addElement("var");
			 sData.addAttribute("id", "threeminlate");
			 sData.addAttribute("value", pt.getTimeInMiliFromMiniute(3));
			 
			 sData=elm.addElement("var");
			 sData.addAttribute("id", "fourminlate");
			 sData.addAttribute("value", pt.getTimeInMiliFromMiniute(4));
			 
			 sData=elm.addElement("var");
			 sData.addAttribute("id", "fiveminlate");
			 sData.addAttribute("value", pt.getTimeInMiliFromMiniute(5));
			 
			 sData=elm.addElement("var");
			 sData.addAttribute("id", "tenminlate");
			 sData.addAttribute("value", pt.getTimeInMiliFromMiniute(10));
			 
			 sData=elm.addElement("var");
			 sData.addAttribute("id", "thirtyminlate");
			 sData.addAttribute("value", pt.getTimeInMiliFromMiniute(30));
	     
   	
   	 return elm;
   	
   }
 	private List<Element>  executeWs(List<Element> vWs,List<Element> vQuery,List<Element> variables){
 		DomUtil util=new DomUtil();
 		List<Element> m_vQuery=vQuery;
		for(Element vWselm:vWs){
			ServiceHandler handler=null;
			String input="";
			String dbName=vWselm.attributeValue("dbName");
			if(dbName!=null &&!dbName.isEmpty()&&this.dbManager.get(dbName)==null){
				this.changeDbConnection(dbName);
			}else if(dbName!=null &&!dbName.isEmpty()){
				this.changeDbConnectionFromDbmanager(dbName);					
			}
			String wsHandler=vWselm.attributeValue("handler");
			this.wsName=vWselm.attributeValue("name");
			String globaldatanode=vWselm.attributeValue("globaldatanode");
			String stripchar=vWselm.attributeValue("stripchar");
			String globalcookies=vWselm.attributeValue("globalcookies");
			try {
				if(wsHandler!=null)
				 handler = (ServiceHandler)Class.forName(wsHandler).newInstance();
				//System.out.println("vWselm="+vWselm.asXML());
				
				List<Element> wsElms=vWselm.elements();
				List<Element> items=new ArrayList();
				for(Element wsProp:wsElms){
					if(wsProp.getName().equals("items")){
						List <Element> itemList=wsProp.elements();
						for(Element item:itemList){
							items.add(item);
						}
						break;
					}
				}
				
				for(Element item:items){
					//System.out.println("vWselm="+item.asXML());
					String itemId=item.attributeValue("id");
					String globalItemTag=item.attributeValue("value");
					String itemIndex=item.attributeValue("index");
					String itemIdValue=(globalItemTag.contains(":")? getRamdomItemId(item.attributeValue("value"),itemIndex):globalItemTag);
					if(globalItemTag!=null){
						vWselm=this.replaceItemId(vWselm,itemId, itemIdValue);
						m_vQuery=this.replaceItemIdForSqlQuery(m_vQuery, itemId, itemIdValue);
					}
				}
				//System.out.println(vWselm.asXML());
				URLConfig url=new URLConfig(vWselm);
				this.page.invokeWs(url,this.action);
				 Set <String> s=this.page.getURLConfig().getUrlParamset();
			        for(String param:s){  
			            input=input+this.page.getURLConfig().getUrlParamValue(param);
			        }
			    //System.out.println(input);
			    String ws=this.page.getURLConfig().getUrlElement().attributeValue("name");
			    String path=ConfigLoader.getWmOutputWebServicePath()+File.separator+ws+File.separator;
			    FileUtility.createDir(path);
			    if(handler!=null){
			    	handler.setOutputFilePath(path+ws);
				    handler.setServiceInputDoc(input);
				    handler.setServiceResponseDoc(this.page.getURLResponse());					   
				    handler.handle(this.dbConn);
			    }else{
			    	FileUtility.writeToFile(path+this.page.getURLConfig().getUrlElement().attributeValue("name")+"_input.xml", 
                            input);
			    	FileUtility.writeToFile(path+this.page.getURLConfig().getUrlElement().attributeValue("name")+"_response.xml", 
                            this.page.getURLResponse());

			    }
		        this.resultDataSet.put((String) "ws:"+this.wsName,this.page.getURLResponse());
		        if(globaldatanode!=null &&!globaldatanode.isEmpty()){
		        	HashMap<String,String> varmap=new HashMap<String,String>() ;
					//ItemUtility iu= new ItemUtility();
					//set validators level variables into varmap
					iu.setVariablesValue(variables, this.resultParamSet, this.resultDataSet, varmap);
		        	ConfigLoader.addGlobalXMLData("global:ws."+this.wsName, util.getGlobalWebserviceXMLDataset(varmap,this.page.getURLResponse(),this.wsName,stripchar,globaldatanode));
		        }
		        if(globalcookies!=null &&!globalcookies.isEmpty()){
		        	HashMap<String,String> varmap=new HashMap<String,String>() ;
					String[] keys=globalcookies.split(",");
					for(String key:keys){
						String cookiename="global:cookie."+key.trim();
						String cookievalue= this.page.getCookieByName(key);
						
						ConfigLoader.addGlobalField(cookiename, cookievalue);
						
						WmLog.printMessage(">>>Added Cookie to global list "+cookiename+"="+ConfigLoader.getGlobalDataFieldValue(cookiename));
					}
					//set validators level variables into varmap
				}
			
				 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.print(e.getMessage());
				
				e.printStackTrace();
			}
		}
		return m_vQuery;
 	}
 	private void executeAPI(List<Element> vApi){
 		 String apiName="";
 		for(Element vApielm:vApi){
			apiName=vApielm.attributeValue("name");
			String dbName=vApielm.attributeValue("dbName");
			
			if(dbName!=null &&!dbName.isEmpty()&&this.dbManager.get(dbName)==null){
				this.changeDbConnection(dbName);
			}else if(dbName!=null &&!dbName.isEmpty()){
				this.changeDbConnectionFromDbmanager(dbName);					
			}
			if(!apiName.equals("")){
				 //this.isApiTest=true;
				 int numberOfArguments=Integer.valueOf(vApielm.attributeValue("fields"));
				 this.cs=makeCallableStatement(apiName, numberOfArguments);
				 setupData(vApielm.elements());
				
			 }
			try {
				 boolean response=this.execute();
				 this.fillupResultDataset();
				 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.print(e.getMessage());
				
				e.printStackTrace();
			}
		}
 	}
 	
 	
 	public JunitHandler executeJunitAssertData(JunitHandler handler) {
		//JunitHandler handler=null;
 		HashMap<String, String> junittmpvars=null;
 		Element data=handler.getThreadDataElement();
 		String threadIndex=handler.getThreadIndex();
		String validator="";		
		//ItemUtility utl= new ItemUtility();
	    boolean hasVariables=false;
	    Element elm=handler.getValidatorElement();
		//for(Element elm: vlidatorList){
			System.out.println(elm.asXML());
			List<Element> vElm=elm.elements();			
			List<Element> vAssert=new ArrayList<Element>();	
			List<Element> variables=new ArrayList<Element>();
			validator=elm.attributeValue("name");			
			for(Element vEach:vElm){
				if(vEach.getName().equalsIgnoreCase("assert")){
					vAssert.add(vEach);								
				}else if(vEach.getName().equalsIgnoreCase("variables")){
					//System.out.println(vEach.asXML());
					variables.add(vEach);
				}
			}
			//if dataset presents and variables are not defined
			if(data!=null){
				Element vVars=iu.getRootElementFromXML("<variables></variables>");
				
				//fillup as variable for each attribute
				 if(data!=null){
					List<DefaultAttribute> attrs=data.attributes();
					for(DefaultAttribute attr:attrs){
						Element sData=vVars.addElement("var");
						sData.addAttribute("id", attr.getQualifiedName().trim());						
						sData.addAttribute("value", data.attributeValue(attr.getQualifiedName()).trim());
						validator=validator.replaceAll("@"+attr.getQualifiedName().trim(), data.attributeValue(attr.getQualifiedName()).trim());
					}
					
				 }
				//System.out.println(vVars.asXML());
				variables.add(vVars);
				
			}
			
			List<Element> itemlist=new ArrayList();
			List<Element> varlist=new ArrayList();
			for(Element wsProp:variables){
				if(wsProp.getName().equalsIgnoreCase("variables")){
					System.out.println(wsProp.asXML());
					List <Element> elList=wsProp.elements();
					for(Element el:elList){
						if(el.getName().equalsIgnoreCase("var")){
							varlist.add(el);
						}else if(el.getName().equalsIgnoreCase("item")){
							itemlist.add(el);
						}
					}
					//break;
				}
			}
			//replace items id in the validator level
			if(!itemlist.isEmpty()){
				if(!vAssert.isEmpty())
					vAssert=iu.replaceValidatorElementItemId(vAssert, itemlist);
			}
			//Replace variables
			if(!varlist.isEmpty()){				
				if(!vAssert.isEmpty()){
					vAssert=iu.replaceValidatorElementVariables(vAssert, varlist, this.resultParamSet, this.resultDataSet);					
				}
			}
						
			//Execute Asserts			 
			try{
				datasetIndex=new Integer(data.attributeValue("datasetIndex"));
			}catch (Exception e){
				datasetIndex=0;
			}
			
			synchronized (this.resultDataSet){
				if(BaseHandler.threadResult!=null){
					HashMap<String,String> resultdata=BaseHandler.threadResult.get(handler.getHandlerId());
					if(resultdata!=null &&resultdata.size()>0){
						junittmpvars=iu.extractOldJunitResultDataSet(resultDataSet, resultdata);
						if(junittmpvars!=null &&!junittmpvars.isEmpty()){
							for(String key:junittmpvars.keySet()){
								resultDataSet.remove(key);
							}
						}
						for(Object key:resultdata.keySet()){
							this.resultDataSet.put(key.toString(), resultdata.get(key));
						}
					}
			
				this.executeAssert( vAssert,  validator,  this.wsName,datasetIndex,threadIndex,handler.getHandlerName());
				this.junitAssertCounter++;
				itemlist=null;
				varlist=null;
				variables=null;
				
					//Reset resultdataset
					if(junittmpvars!=null &&!junittmpvars.isEmpty()){
						for(String key:junittmpvars.keySet()){
							this.resultDataSet.put(key.toString(), junittmpvars.get(key));
						}
					}
				}
			}
			
		
	   
		
	   return (handler);
	
	}	
 	
 public ArrayList<JunitHandler> executeJunitThreadData(Element data) {
		
	 	ArrayList<JunitHandler> junitHandlers= new ArrayList<JunitHandler>();
		String validator="";		
		//ItemUtility utl= new ItemUtility();
	    boolean hasVariables=false;
		for(Element elm: vlidatorList){
			JunitHandler handler=null;
			System.out.println(elm.asXML());
			List<Element> vElm=elm.elements();
			//List<Element> vQuery=new ArrayList<Element>();
			List<Element> vAssert=new ArrayList<Element>();
			List<Element> vJunit=new ArrayList<Element>();			
			List<Element> variables=new ArrayList<Element>();
			validator=elm.attributeValue("name");
			for(Element vEach:vElm){
				/*if(vEach.getName().equalsIgnoreCase("sql")){
					
					//replace sql lib if included
					String includes=vEach.attributeValue("includes");
					if(includes!=null &&!includes.isEmpty()){
						vQuery.add(utl.replaceIncludeInSqlQuery(vEach));
					}else{
						vQuery.add(vEach);
					}
				}else */
				if(vEach.getName().equalsIgnoreCase("junit")){
					vJunit.add(vEach);			
				}else if(vEach.getName().equalsIgnoreCase("assert")){
					vAssert.add(vEach);							
				}else if(vEach.getName().equalsIgnoreCase("variables")){
					variables.add(iu.replaceData(vEach,data));
				}				
			}
			//if dataset presents and variables are not defined
			if(data!=null){
				Element vVars=iu.getRootElementFromXML("<variables></variables>");
				
				//fillup as variable for each attribute
				 if(data!=null){
					List<DefaultAttribute> attrs=data.attributes();
					for(DefaultAttribute attr:attrs){
						Element sData=vVars.addElement("var");
						sData.addAttribute("id", attr.getQualifiedName().trim());										
						sData.addAttribute("value", data.attributeValue(attr.getQualifiedName()).trim());								
					}
				 }
				//System.out.println(vVars.asXML());
				variables.add(vVars);
			}
			
			List<Element> itemlist=new ArrayList();
			List<Element> varlist=new ArrayList();
			for(Element wsProp:variables){
				if(wsProp.getName().equalsIgnoreCase("variables")){
					List <Element> elList=wsProp.elements();
					for(Element el:elList){
						if(el.getName().equalsIgnoreCase("var")){
							varlist.add(el);
						}else if(el.getName().equalsIgnoreCase("item")){
							itemlist.add(el);
						}
					}
					
				}
			}
			//replace items id in the validator level
			if(!itemlist.isEmpty()){
				
				if(!vJunit.isEmpty()){
					vJunit=iu.replaceValidatorElementItemId(vJunit, itemlist);	
				}
			}
			
			//Execute Asserts	
			if(handler==null &&(vJunit.size()>0)){
				handler=this.getThreadHandler(vJunit,varlist,itemlist,validator);
				//handler.setValidatorElement(elm);
				//handler.setThreadDataElement(data);
				//handler.setThreadIndex(data.attributeValue("driver")+"-"+data.attributeValue("datasetIndex"));
			}
			if(handler==null &&vAssert.size()>0 &&vJunit.size()==0){
				handler=this.getAssertThreadHandler(varlist,itemlist);
				//handler.setValidatorElement(elm);
				//handler.setThreadDataElement(data);
				//handler.setThreadIndex(data.attributeValue("driver")+"-"+data.attributeValue("datasetIndex"));
			}
			if(handler!=null){
				handler.setValidatorElement(elm);
				handler.setThreadDataElement(data);
				handler.setThreadIndex(data.attributeValue("driver")+"-"+data.attributeValue("datasetIndex"));
				junitHandlers.add(handler);
			}
			
		}
	    	
	   return (junitHandlers);
	
	}
 
 	private JunitHandler getThreadHandler(List<Element> vJunit,List<Element> variables,List<Element> items, String validator){
 		JunitHandler handler=null;
		
		for(Element vJelm:vJunit){		
			String JunitHandler=vJelm.attributeValue("handler");
			if(JunitHandler==null ||JunitHandler.isEmpty()){
				JunitHandler="com.testmax.handler.JunitHandler";
			}
			
			String name=vJelm.attributeValue("name");
			
			try {
				if(JunitHandler!=null){
					
					HashMap<String,String> varmap=new HashMap<String,String>() ;
					//ItemUtility iu= new ItemUtility();
					//set validators level variables into varmap
					iu.setVariablesValue(variables, this.resultParamSet, this.resultDataSet, varmap);
					iu.setItemValue(items, varmap);
					handler = (JunitHandler)Class.forName(JunitHandler).newInstance();
					handler.setVarMap(varmap);
					if(this.isMultiThread){
						String threadDataIndex=varmap.get("driver")+"-"+varmap.get("datasetIndex");
						handler.registerThreadData(threadDataIndex,varmap);
						handler.isMultiThreaded=true;
					}else{
						handler.registerThreadData(this.page.getBrowsers()[0]+"-"+String.valueOf(this.datasetIndex),varmap);
					}
					handler.setElementConfigXML(vJelm.asXML());
					handler.setValidatorName(validator);		
					handler.setTestConfigName(name);
					handler.setExecuteProc(this);
				
					
					//Item setup using global item
					List<Element> jsElms=vJelm.elements();				
					for(Element jsProp:jsElms){
						if(jsProp.getName().equalsIgnoreCase("testPackage")){
							handler.addTestPackage(jsProp);
						}
					}				
					
					
				}
				 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.print(e.getMessage());
				
				e.printStackTrace();
			}
			
			
		}
		return handler;
		
	}
 	
 	private JunitHandler getAssertThreadHandler(List<Element> variables,List<Element> items){
 		HashMap<String,String> varmap=new HashMap<String,String>() ;
 		String JunitHandler="com.testmax.handler.JunitHandler";
		//ItemUtility iu= new ItemUtility();
		//set validators level variables into varmap
		iu.setVariablesValue(variables, this.resultParamSet, this.resultDataSet, varmap);
		iu.setItemValue(items, varmap);
		JunitHandler handler;
		try {
			handler = (JunitHandler)Class.forName(JunitHandler).newInstance();
			handler.setVarMap(varmap);
			if(this.isMultiThread){
				String threadDataIndex=varmap.get("driver")+"-"+varmap.get("datasetIndex");
				handler.registerThreadData(threadDataIndex,varmap);
				handler.isMultiThreaded=true;
			}else{
				handler.registerThreadData(this.page.getBrowsers()[0]+"-"+String.valueOf(this.datasetIndex),varmap);
			}
			
			handler.setExecuteProc(this);
			
			
			return handler;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	
 	}
 	
	private void executeJunit(List<Element> vJunit,List<Element> variables,List<Element> items, String validator){
		BaseHandler handler=null;
		
		for(Element vJelm:vJunit){
		
			String JunitHandler=vJelm.attributeValue("handler");
			if(!ItemUtility.isEmptyValue(vJelm.getName()) && vJelm.getName().equalsIgnoreCase("testng") ){
				JunitHandler="com.testmax.handler.TestNGHandler";
			}else if(JunitHandler==null ||JunitHandler.isEmpty()){
				JunitHandler="com.testmax.handler.JunitHandler";
			}
			
			String name=vJelm.attributeValue("name");
			
			try {
				if(JunitHandler!=null){
					String browser=ConfigLoader.getConfig("SELENIUM_DRIVER").toLowerCase();
					if(this.page.getBrowsers()!=null){
						browser=this.page.getBrowsers()[0];
					}
					HashMap<String,String> varmap=new HashMap<String,String>() ;
					//ItemUtility iu= new ItemUtility();
					//set validators level variables into varmap
					iu.setVariablesValue(variables, this.resultParamSet, this.resultDataSet, varmap);
					iu.setItemValue(items, varmap);
					varmap.put("datasetIndex", String.valueOf(this.datasetIndex));
					varmap.put("driver", browser);
					handler = (BaseHandler)Class.forName(JunitHandler).newInstance();
					handler.setVarMap(varmap);
					handler.registerThreadData(String.valueOf(this.datasetIndex),varmap);
					handler.setThreadIndex(String.valueOf(this.datasetIndex));
					handler.setElementConfigXML(vJelm.asXML());
					handler.setValidatorName(validator);		
					handler.setTestConfigName(name);
					handler.setExecuteProc(this);
				
					
					
					//Item setup using global item
					List<Element> jsElms=vJelm.elements();				
					for(Element jsProp:jsElms){
						if(jsProp.getName().equalsIgnoreCase("testPackage")){
							handler.addTestPackage(jsProp);
						}
					}				
					
					//Call Junit handler
					handler.handleService();
			        HashMap result=handler.getTestResult();
			        for (Object method:result.keySet()){
			        	String message=this.getActionName()+">>"+validator+">>Test Method>>";
			        	String actual=result.get(method).toString();		        	
			        	this.validateExpected(method.toString(), message, actual, "PASSED", "Has", "VARCHAR");
			        }
				}
				 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.print(e.getMessage());
				
				e.printStackTrace();
			}
			
		}
		
	}
	
	private void executeSpark(List<Element> vSpark,List<Element> variables, String validator){
		BaseHandler handler=null;
		HashMap<String,String> varmap=new HashMap<String,String>() ;
		//ItemUtility iu= new ItemUtility();
		//set validators level variables into varmap
		iu.setVariablesValue(variables, this.resultParamSet, this.resultDataSet, varmap);
		
		for(Element vSelm:vSpark){
			
			String SparkHandler="com.testmax.handler.SparkHandler";
			String name=vSelm.attributeValue("name");
		
			try {
				if(SparkHandler!=null)
				 handler = (BaseHandler)Class.forName(SparkHandler).newInstance();
				handler.setVarMap(varmap);
				handler.setElementConfigXML(vSelm.asXML());				
				handler.setTestConfigName(name);				
				handler.setValidatorName(validator);			
				handler.setExecuteProc(this);
			
				//Item setup using global item
				List<Element> sElms=vSelm.elements();				
				for(Element sProp:sElms){
					if(sProp.getName().equalsIgnoreCase("testSuite")){
						handler.addTestPackage(sProp);
					}
				}
				
				
				//Call Spark handler				
				handler.handleService();
				
		        HashMap result=handler.getTestResult();
		        for (Object method:result.keySet()){
		        	String message=this.getActionName()+">>"+validator+">>Test Method>>";
		        	String actual=result.get(method).toString();		        	
		        	this.validateExpected(method.toString(), message, actual, "PASSED", "Has", "VARCHAR");
		        }
			
				 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.print(e.getMessage());
				
				e.printStackTrace();
			}
			
		}
		
	}
	private void executeCommand(List<Element> vCommand){
		
		for(Element vCmd:vCommand){
			
		}
	}
	
 	private void executeSetup(List<Element> vSetup){
 		
 	// Validate Setup
		
		for(Element vSetelm:vSetup){
			
			if(vSetelm!=null ){	
				isItemSetup=true;
				String sql=vSetelm.getText();
				if(sql!=null &&!sql.isEmpty()){
					this.executeSetupQuery(vSetelm);
				}
			}
		}
 	}
 	
 	
   
	/*
	 * Validate Each query
	 * Attributes:
	 * name= SQL name (normally 1-5 letters and no space
	 * dbmsoutput=yes/no
	 * dbmstable=yes/no
	 * filetype=csv/txt
	 * includes=sqllib:<logical file Name>.variable (coma separated)
	 * replace= <var1,var2,..>=global:<logical file Name>.variable (coma separated)
	 */
 	private void executevQuery(List<Element> vQuery, List<Element> vInput, String validator){
 	
		String queryName="";
		String queryDesc="";
		String dbmsOutput="";
		
		for(Element vSql:vQuery){
			//System.out.println(vSql.asXML());
			queryName=vSql.attributeValue("name");
			queryDesc= ">>>Executing Query="+queryName+ ": "
				+(vSql.attributeValue("description")==null?"":vSql.attributeValue("description"));
			String sql=vSql.getText().replaceAll("@Lt", "<");
			String hasDbmsOutput=vSql.attributeValue("dbmsoutput");
			dbmsOutputKey=vSql.attributeValue("dbmsoutputkey");
			globaldataset=vSql.attributeValue("globaldataset");
			String dbmsTableOutput=vSql.attributeValue("dbmstable");
			String fileExtension=vSql.attributeValue("filetype");
			
			
			//change database connection				
			String dbName=vSql.attributeValue("dbname");
			if(dbName!=null &&dbName.isEmpty()){
				dbName=vSql.attributeValue("dbName");
			}
				
			if(dbName!=null &&!dbName.isEmpty() && this.dbManager.get(dbName)==null){
				this.changeDbConnection(dbName);
			}else if(dbName!=null &&!dbName.isEmpty()){					
				this.changeDbConnectionFromDbmanager(dbName);	
			}
			
			/*
			//replace sql lib if included
			String includes=vSql.attributeValue("includes");
			String replace=vSql.attributeValue("replace");
			if(includes!=null &&includes.split(",").length>=0){
				//String val=sql;
				for(Object key:includes.split(",")){
					sql=sql.replace("@"+key, ConfigLoader.getSqlLibByKey(key.toString()));
				}
				System.out.println(sql);
			}
			*/
			if(hasDbmsOutput!=null &&hasDbmsOutput.equalsIgnoreCase("yes")){
				dbmsOutput=this.executeWithDbmsOutput(sql);
				FileUtility.createDir(ConfigLoader.getWmOutputSqlPath());
				this.printMessage(queryDesc+ "\n"+sql);
				this.printMessage(this.printresult);
				/*WmLog.getCoreLogger().info(queryDesc+ "\n"+sql);	
				System.out.println(queryDesc+ "\n"+sql);
				WmLog.getCoreLogger().info(ExecuteProc.class.getName()+">>>Output:\n"+this.printresult);	
				System.out.println(this.printresult);
				*/
				FileUtility.writeToFile(ConfigLoader.getWmOutputSqlPath()
						+this.page.getURLConfig().getUrlElement().attributeValue("name")+"_"+queryName+"_input.sql",sql);
		    	FileUtility.writeToFile(ConfigLoader.getWmOutputSqlPath()
		    			+this.page.getURLConfig().getUrlElement().attributeValue("name")+"_"+queryName+"_output."
		    			+((fileExtension==null||fileExtension=="")?"txt":fileExtension),((fileExtension==null||fileExtension=="")? printresult:dbmsOutput));
		    	if(this.resultDataSet!=null ){
			    	if(dbmsTableOutput!=null &&dbmsTableOutput.equalsIgnoreCase("yes")){
				    		this.resultDataSet.put(queryName,this.setDbmsOutput2Array(dbmsOutput));
				    		ConfigLoader.addGlobalField(queryName, dbmsOutput);
				    		//set for global dataset
				    		setDbmsOutput2XMLDataset(queryName,dbmsOutput);
			    		
			    	}else{
			    		this.resultDataSet.put(queryName,this.setDbmsOutput2String(dbmsOutput));
			    	}					
					this.resultParamSet.put(queryName, "QUERY");
		    	}
				
			}else{
				try {
					WmLog.getCoreLogger().info(queryDesc+ "\n"+sql);	
					System.out.println(queryDesc+ "\n"+sql);
					
					this.ps=this.makeSqlStatement(sql, vInput);				
					
					ResultSet rs=this.executeSql();
					
					if(rs!=null){
						this.resultDataSet.put(queryName,this.setResults2Array(rs,validator));					   
						this.resultParamSet.put(queryName, "QUERY");
						//set as global dataset
						this.setXMLDataset(queryName);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.print(e.getMessage());					
					e.printStackTrace();
				}
			}
		}
 	}
 	private void executeAssert(List<Element> vAssert, String validator, String wsName, int datasetIndex,String threadIndex, String handlerName){
 		
 		//validate asserts
		for(Element vAs:vAssert){
			try{
				System.out.println(vAs.asXML());
				ArrayList<ArrayList<Object>> rsData=null;				
				String assertName=vAs.attributeValue("name");
				String output=vAs.attributeValue("output");
				String value=vAs.attributeValue("value");
				String uniquefields=vAs.attributeValue("uniquefields");
				String textmarker=vAs.attributeValue("textmarker");
				String png=vAs.attributeValue("png");
				String nodepath=vAs.attributeValue("nodepath");
				if(output!=null&&output.contains("junit:")){
					png=output.split(":")[1];
				}
				if(threadIndex!=null &&png!=null &&!png.isEmpty()){
					 String path=ConfigLoader.getWmOutputLogPath()+"ws"+File.separator+this.getActionName().replaceAll(" ", "_")+File.separator+threadIndex+File.separator+png+".png";
					 File pngfile=new File(path);
					 //if(!pngfile.exists())
					 //continue;
				}
				if (nodepath!=null &&!nodepath.isEmpty()){
					String xml=this.resultDataSet.get("ws:"+wsName).toString();
					if(xml!=null &&!xml.isEmpty()){
						this.validateAssertByDom(vAs, xml,validator,wsName);
						if(this.nodeElmValueList!=null){
							for(Object key:this.nodeElmValueList.keySet()){
								this.resultDataSet.put("ws:"+key.toString(),this.nodeElmValueList.get(key).toString());
							}
						}
					}
				}
				else{
				boolean is_double=true;
				
				try{ 									 
					 Double intval=Double.parseDouble(value);
				}catch (NumberFormatException e){
					is_double=false;
				}
				//this below attribute sets value to the global list
				String global=vAs.attributeValue("global");
				if(value.indexOf("global:")>=0){
					 value=ConfigLoader.getGlobalDataFieldValue(value);
					 
				}else if(!is_double && (value.contains("ws:")||value.contains("junit:"))){
					String valreplace=this.resultDataSet.get(value).toString();
					if(valreplace!=null &&!valreplace.isEmpty()){
						value=valreplace;
					}
					
				}else if(!is_double && value.contains(".")){
					try{
						 String prm=value.split("\\.")[0];
						 String field=value.split("\\.")[1];
						 String rsType=this.resultParamSet.get(prm);					
						 if(rsType.equalsIgnoreCase("query")||rsType.equalsIgnoreCase("cursor")){					 
							 rsData= (ArrayList<ArrayList<Object>>) this.resultDataSet.get(prm);
							 if(rsData!=null){
								    if(this.isDatasetValue){
								    	value=getColumnValue(rsData,datasetIndex,field);
								    }
								    if(value.contains(prm) && value.contains(".")){
								    	value=getColumnValue(rsData,rsData.size()-1,field);
								    }
							 }
						 }
					}catch (Exception e){
						
					}
				}
				String rowIdx=vAs.attributeValue("lookuprow");
				int row=1;
				if(rowIdx!=null && !rowIdx.equals("*")){
					row=Integer.valueOf(rowIdx);
				}
				
				String operator=vAs.attributeValue("operator");
				String actual=null;
				String prm=output;			
				String field=null;
				String allColvalue="";
				
					if(output!=null &&output.contains(".")){
						is_double=true;
						try{ 									 
							 Double intval=Double.parseDouble(output);
						}catch (NumberFormatException e){
							is_double=false;
						}
						try{
							if(!is_double){
							  prm=output.split("\\.")[0];
							  field=output.split("\\.")[1];
							}
						}catch (Exception e){						 	
							this.printMessage("ERROR>>"+this.getActionName()+">>"+validator+">>The Text value you are trying to compare having '.' dot at the end. Use  stripchar attribute for frontend tag or partial string and 'Has' operator to assert this value.");
						}
					}
					 String rsType=this.resultParamSet.get(prm);
					 String dataType=vAs.attributeValue("descriptor");
					 String commandfile=handlerName+"_"+this.getActionName().replaceAll(" ", "_");
					 String commandlog=" and <a href=\"../ws/logs/"+this.getActionName().replaceAll(" ", "_")+"/command/"+threadIndex+"/"+commandfile+".log\" target=\"_blank\">command log</a>";
					 String link=(threadIndex!=null?"<a href=\"../ws/images/"+this.getActionName().replaceAll(" ", "_")+"/"+threadIndex+"/"+handlerName+"/"+png+".png\" target=\"_blank\">screen shot</a>"+commandlog+" Expected":"Expected");
					 String failedlink=(handlerName!=null?"<a href=\"../ws/images/"+this.getActionName().replaceAll(" ", "_")+"/"+threadIndex+"/"+handlerName+"/"+commandfile+".png\" target=\"_blank\">screen shot</a>"+commandlog+" Expected":"Expected");
					 String msg=this.getActionName()+">>"+validator+ (datasetIndex>0?">>Dataset Row="+datasetIndex:"")+">>"+prm+">>"+assertName+">>"+(png==null?failedlink:link)+" "
					 +this.getOperatorTranslated(operator)+value+", Actual=";
					 if(rsType!=null && (rsType.equalsIgnoreCase("query")||rsType.equalsIgnoreCase("cursor"))){					 
						 rsData= (ArrayList<ArrayList<Object>>) this.resultDataSet.get(prm);
						
						 if(rsData!=null && (rowIdx==null ||rowIdx.isEmpty() ||!rowIdx.equals("*"))){			
							actual=getColumnValue(rsData,row,field);
							allColvalue=getAllColumnNameValue(rsData,row);
							if(global!=null &&global.equalsIgnoreCase("yes") &&!actual.isEmpty()){
								ConfigLoader.addGlobalField("global:"+output.toLowerCase(), actual);
							}
							//Validate Expected Value							
							this.validateExpected(assertName,msg +"DataColSet: "+ allColvalue, actual.trim(), value.trim(), operator, dataType);						 
						 }else if(rsData!=null && rowIdx.equals("*")){
							  for(int k=1; k<rsData.size();k++){
								allColvalue=getAllColumnNameValue(rsData,k);
								actual=getColumnValue(rsData,k,field);
								if(iu.isEmptyValue(actual)){
									msg=this.getActionName()+">>"+validator+ (datasetIndex>0?">>Dataset Row="+datasetIndex:"")+">>"+prm+">>"+assertName+">>"+output+"=NULL in the Query Result. ";
								}
								if(global!=null &&global.equalsIgnoreCase("yes") &&!actual.isEmpty()){
									ConfigLoader.addGlobalField("global:"+output.toLowerCase(), actual);
								}
								
								//below lines of code are used for PDF with text and column marker in the assert
								if(!iu.isEmptyValue(uniquefields) && !iu.isEmptyValue(textmarker)){
										String tmpval="";
										String[] ufldlist=uniquefields.split(",");
										
										if(ufldlist.length>0){
											String colval=getColumnValue(rsData,k,ufldlist[0]);
											if(!iu.isEmptyValue(colval)){
												String[] valuelist=value.split(colval);
												boolean isExists=false;
												tmpval="";
												for(String val:valuelist){
													for(int i=1;i<ufldlist.length;i++){
														String othercol=getColumnValue(rsData,k,ufldlist[i]);
														if(!iu.isEmptyValue(othercol)){
															isExists=false;
															if(val.contains(othercol)){
																isExists=true;
															}
														}
													}
													if(!isExists&&ufldlist.length==1){
														isExists=true;
													}
													if(isExists){
														tmpval+= textmarker+colval+" "+val.split(textmarker)[0]+" " ;
													}
												}
											}
										}
										if(iu.isEmptyValue(tmpval)){
											tmpval=value;
										}
										this.validateExpected(assertName,msg +"DataColSet:Query Row="+k+">> "+ allColvalue, actual.trim(), tmpval.trim(), operator, dataType);
											
								}else{
									//Validate Expected Value							
									this.validateExpected(assertName,msg +"DataColSet:Query Row="+k+">> "+ allColvalue, actual.trim(), value.trim(), operator, dataType);
								}
							 }
						 }
					 }else{
						 actual=(String) this.resultDataSet.get(prm);
						 if(actual==null||actual.isEmpty()){
							 actual=prm;
							 msg=this.getActionName()+">>"+validator+ (datasetIndex>0?">>Dataset Row="+datasetIndex:"")+">>"+prm+">>"+assertName+">>"+failedlink+" ";
						 }
						//Validate Expected Value
						 if(actual!=null && actual!=""){
							 this.validateExpected(assertName,msg, actual.trim(), value.trim(), operator, dataType);
						 }else{
							 String warning=" No value found for parameter "+prm +"\n WARNING: Validator FAILED!"+
							 				" Please look at if the parameter output="+prm+ " exists or mataches in Assert or Validator Tag in input XML!";
							 this.printMessage(" WARNING: Validator>> "+msg+warning );
							
						 }
					 }
					 
					 WmLog.getCoreLogger().info("Validator>> "+msg+actual);						
					 System.out.println(" "+"Validator>> "+msg+actual);
				
				}
			}catch (Exception e){
				this.printMessage("ERROR>>"+this.getActionName()+">>"+validator+">> Found Exception"+ e.getMessage());
			}
		}
	
 	}
 	private ResultSet executeSql(){
 		ResultSet rs=null;
		try {
			rs = this.ps.executeQuery();
		} catch (SQLException e) {
			System.out.print(e.getMessage());	
			System.out.print(e.getSQLState());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		return(rs);
 	}
 	private String getColumnValue(ArrayList<ArrayList<Object>> rsData,int rowId, String column){
 		int colIndex=0;
 		for (Object col:rsData.get(0)){
 			if(col.toString().equalsIgnoreCase(column))
 				break;
 			else 				
 				colIndex++;
 		}
 		int rowIdx=(rowId==0?1:rowId);
 		String val=null;
 		try{
 			val=rsData.get(rowIdx).get(colIndex).toString();
 		}catch (Exception e) { 			
 		}
 		return(val==null?"":val);
 		
 	}
 	
 	private String getColumnValue(ArrayList<ArrayList<Object>> rsData,int rowId, int colIdx){ 		
 		int rowIdx=(rowId==0?1:rowId);
 		String val=null;
 		try{
 			val=rsData.get(rowIdx).get(colIdx).toString();
 		}catch (Exception e) { 			
 		}
 		return(val==null?"":val);
 		
 	}
 	
 	private String getColumnNameValue(ArrayList<ArrayList<Object>> rsData,int rowId, int colIdx){ 		
 		int rowIdx=(rowId==0?1:rowId);
 		String val=null;
 		try{
 			val=rsData.get(0).get(colIdx).toString()+"="+ rsData.get(rowIdx).get(colIdx).toString();
 		}catch (Exception e) { 			
 		}
 		return(val==null?"":val);
 		
 	}
 	private String getAllColumnNameValue(ArrayList<ArrayList<Object>> rsData,int rowId){ 		
 		int rowIdx=(rowId==0?1:rowId);
 		String val="";
 		int colIndex=0;
 		try{
 			for (Object col:rsData.get(0)){
 				val+=col.toString()+"="+ rsData.get(rowIdx).get(colIndex).toString() +"<br>";
 				colIndex++;
 			}
 		}catch (Exception e) { 			
 		}
 		return(val==null?"":val);
 		
 	}
 	/*
 	 *  The column name = result. Whole data can be accesed with single row by the column
 	 */
 	private ArrayList<ArrayList<Object>> setDbmsOutput2String(String data)  {
 		
 		ArrayList<ArrayList<Object>> al = new ArrayList<ArrayList<Object>>();
		try {
		
 		ArrayList<Object> columnList = new ArrayList<Object>(); 
 		columnList.add("result");  
 		
 		al.add(columnList); 
 		
 		ArrayList<Object> record = new ArrayList<Object>();                  
			
		record.add(data==null?"":data);                 
		        
		al.add(record);  
 		
		} catch (Exception e) {
			WmLog.getCoreLogger().info(" Validator>> Can not convert DBMS output to arry of rows and columns");	
			System.out.println(" Validator>> Can not convert DBMS output to arry of rows and columns");
			// TODO Auto-generated catch block
				e.printStackTrace();
		} 
 		return al;  
 	}
 	/*
 	 * All columns are in lower case and replaced space with "_"
 	 */
 	private ArrayList<ArrayList<Object>> setDbmsOutput2Array(String data)  {
 		
 		ArrayList<ArrayList<Object>> al = new ArrayList<ArrayList<Object>>();
		try {
			
		String columns =data.split("\n")[0];   
 		int count = columns.split(",").length; 
 		ArrayList<Object> columnList = new ArrayList<Object>(); 
 		
 		for (Object col: columns.split(",")) {
				columnList.add(col.toString().toLowerCase().replaceAll(" ", "_"));                 
				} 
 		al.add(columnList); 
 		
 		int rows=data.split("\n").length;
 		for (int j=1;j<rows;j++){
 			String rowdata=data.split("\n")[j];
 			
 			ArrayList<Object> record = new ArrayList<Object>();                  
 			for (Object value:rowdata.split(",")) {   
 				record.add(value==null?"":value);                 
 				}                 
 			al.add(record);  
 		}
 		
 		
		} catch (Exception e) {
			WmLog.getCoreLogger().info(" Validator>> Can not convert DBMS output to arry of rows and columns");	
			System.out.println(" Validator>> Can not convert DBMS output to arry of rows and columns");
			// TODO Auto-generated catch block
				e.printStackTrace();
		} 
 		return al;  
 	}
	
 	private void setDbmsOutput2XMLDataset(String queryName,String dbmsdata)	
 	
	{
 		List cellColList=null;
 		if(this.globaldataset!=null &&this.globaldataset.equalsIgnoreCase("yes")){
			String xml="<global name=\"query\">\n\t<"+queryName+" description=\"Dynamic XML data from Query\">\n";
			List cellDataList=(ArrayList<ArrayList<Object>>)setDbmsOutput2Array(dbmsdata);
			if(cellDataList.size()>0){
			 cellColList = (List) cellDataList.get(0);
			}else{
				return ;
			}
			for (int i = 1; i < cellDataList.size(); i++)		{  
				String data="\t\t<data ";
				List cellTempList = (List) cellDataList.get(i);
				for (int j = 0; j < cellTempList.size(); j++)
				{	
					//get column as attribute
				
					String attr =cellColList.get(j).toString();
					
					//get Value
				
					String value = cellTempList.get(j).toString();
					
					data+=attr+ "=\""+value+"\" ";
				}
				xml+=data+"/>\n";
			//System.out.println();
			}
			xml+="\t</"+queryName+">\n</global>";
			System.out.print(xml);
			
			ConfigLoader.addGlobalXMLData(queryName, xml);
 		}
	}
	
 	private void setXMLDataset(String queryName)
	{
 		List cellColList=null;
		
 		if(this.globaldataset!=null &&this.globaldataset.equalsIgnoreCase("yes")){
			String xml="<global name=\"query\">\n\t<"+queryName+" description=\"Dynamic XML data from Query\">\n";
			List cellDataList=(ArrayList<ArrayList<Object>>)this.resultDataSet.get(queryName);
			if(cellDataList.size()>0){
			 cellColList = (List) cellDataList.get(0);
			}else{
				return ;
			}
			for (int i = 1; i < cellDataList.size(); i++)		{  
				String data="\t\t<data ";
				List cellTempList = (List) cellDataList.get(i);
				for (int j = 0; j < cellTempList.size(); j++)
				{	
					//get column as attribute
				
					String attr =cellColList.get(j).toString();
					
					//get Value
				
					String value = cellTempList.get(j).toString();
					
					data+=attr+ "=\""+value+"\" ";
				}
				xml+=data+"/>\n";
			//System.out.println();
			}
			xml+="\t</"+queryName+">\n</global>";
			System.out.print(xml);
			
			ConfigLoader.addGlobalXMLData(queryName, xml);
 		}
	}
	
 	private ArrayList<ArrayList<Object>> setResults2Array(ResultSet rs, String validator)  {
 		ResultSetMetaData metaData;
 		ArrayList<ArrayList<Object>> al = new ArrayList<ArrayList<Object>>();
		try {
			metaData = rs.getMetaData();
 		    
 		int columns = metaData.getColumnCount(); 
 		ArrayList<Object> columnList = new ArrayList<Object>(); 
 		for (int i = 1; i <= columns; i++) {                         
				Object value = metaData.getColumnName(i);                         
				columnList.add(value);                 
				} 
 		al.add(columnList);    
 		while (rs.next()) {                 
 			ArrayList<Object> record = new ArrayList<Object>();                  
 			for (Object col:columnList) {                         
 				Object value = rs.getString(col.toString()); 
 				
 				record.add(value==null?"":value);                 
 				}                 
 			al.add(record);         
 			}
		} catch (SQLException e) {
			//throw new DbProcessingException("Exception method >>setResults2Array");
			if(e.getMessage().contains("00900")){
				String msg=this.getActionName()+">> "+validator+">> Executed Query.";
				this.validateExpected("Query Result", msg, "0", "0", "Eq", "NUMBER");
				WmLog.getCoreLogger().info(" Validator>> "+msg+" No Recordset returned!");	
				System.out.println(" Validator>> "+msg+" No Recordset returned!");
			}else{
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
 		return al;  
 	}
 	

 	private String getOperatorTranslated(String operator){
 		String op=null;
 		if(operator.equalsIgnoreCase("Gt"))
 			op=">";
 		else if(operator.equalsIgnoreCase("Eq"))
 			op="=";
 		else if(operator.equalsIgnoreCase("Lt"))
 			op="<";
 		else if(operator.equalsIgnoreCase("GtEq"))
 			op=">=";
 		else if(operator.equalsIgnoreCase("LtEq"))
 			op="<=";
 		else if(operator.equalsIgnoreCase("NtEq"))
 			op="!=";
 		else if(operator.equalsIgnoreCase("Has"))
 			op=">Has >";
 		else if(operator.equalsIgnoreCase("Nq"))
 			op="!=";
 		return(op); 		
 		
 	}
 	
	public void closeDb(){
 	
 		 try {
 			  WmLog.getCoreLogger().info(ActionHandler.class.getName()+">>>Closing DB Statement!");	
	          if(this.cs!=null){
	        	  this.cs.close(); 
	          }
	     } catch (SQLException sx) {
	    	   WmLog.getCoreLogger().info(ActionHandler.class.getName()+">>>Failed to close statement");	
	           System.out.println("Failed to close statement.");        
	       }
	    
 		try {
 			WmLog.getCoreLogger().info(ActionHandler.class.getName()+">>>Closing Db connection");	
	        if(!this.isUnitTest){   
	        	this.dbConn.close();
	        }
        } catch (SQLException sx) {
        	WmLog.getCoreLogger().info(ActionHandler.class.getName()+">>>Failed to close DB connection");	
            System.out.println("Failed to close connection.");  
            
        }finally{
        	this.action=null;;
    		this.actionItem=null;
    		this.resultDataSet=null;
    		this.resultParamSet=null;
        }
        
 	}
 	
 	public String getActionName(){
 		return this.action;
 	}
 	
 	public HashMap getResultParamset(){
 		return this.resultParamSet;
 	}
 	
 	public HashMap getResultParamdata(){
 		return this.resultDataSet;
 	}
 	
 	/*
	 * Input Item Shipping Option Nodes
	 * @param itemNode= Item for which ship options
	 */
	private Element replaceItemId(Element elm, String itemId, String itemIdValue){
		Element elmNew=null;		
		String xml=elm.asXML().replace("@"+itemId, itemIdValue);
		//System.out.println(xml);
		try {
			elmNew = new SAXReader().read( new StringReader(xml)).getRootElement();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return(elmNew);
	}
	
	/*
	 * Input Item Shipping Option Nodes
	 * @param itemNode= Item for which ship options
	 */
	private List<Element> replaceItemIdForSqlQuery(List<Element> vQuery, String itemId, String itemIdValue ){
		List<Element> newQuery= new ArrayList();
		Element elmNew=null;
		for(Element elm:vQuery){
			
			String xml=elm.asXML().replace("@"+itemId, itemIdValue);
			//System.out.println(xml);
			try {
				elmNew = new SAXReader().read( new StringReader(xml)).getRootElement();
				newQuery.add(elmNew);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		return(newQuery);
	}
 	
 	private String getRamdomItemId(String globalItemTag, String itemIndex){
        //search for global element
     String globalItems=   ConfigLoader.getGlobalDataFieldValue(globalItemTag);
     String randomValues[]=globalItems.split(",");
     
     if(itemIndex!=null && !itemIndex.isEmpty()){
    	 return(randomValues[Integer.parseInt(itemIndex)].trim());     
     }
     Random generator = new Random();      
   int randomIndex = generator.nextInt( randomValues.length );
   return(randomValues[randomIndex].trim());                         
     
   }

 	public static void closeDbManager(){
 		if(dbManager!=null){
	 		//close all db Connections
	       	for(Object key: dbManager.keySet()){       		
	       		Connection con= dbManager.get(key);
	       		try {
	       			if(con!=null){
	       				con.close();
	       			}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	       /*	finally{
	       		dbManager=null;
	        }*/
	       }
 		}
 		dbManager=null;
 	}
 	
 	public String executeWithDbmsOutput(String qry){
 		String result="";
 		printresult="";
 	    try
 	            {
 	            
 	            Statement stmt = this.dbConn.createStatement();

 	            CallableStatement enable_stmt = this.dbConn.prepareCall( "begin dbms_output.enable(1000000); end;" );;
 	            CallableStatement disable_stmt = this.dbConn.prepareCall( "begin dbms_output.disable; end;" );
 	            CallableStatement show_stmt = 
 	            	this.dbConn.prepareCall( 
 	                      "declare " +
 	                      "    l_line varchar2(4000); " +
 	                      "    l_done number; " +
 	                      "    l_buffer long; " +
 	                      "begin " +
 	                      "  loop " +
 	                      "    exit when length(l_buffer)+4000 > :maxbytes OR l_done = 1; " +
 	                      "    dbms_output.get_line( l_line, l_done ); " +
 	                      "    l_buffer := l_buffer || l_line || chr(10); " +
 	                      "  end loop; " +
 	                      " :done := l_done; " +
 	                      " :buffer := l_buffer; " +
 	                      "end;" );
 	            
 	            
 	            enable_stmt.executeUpdate(); 	          
 	           
 	            //System.out.println(qry);
 	            stmt.execute(qry);

 	            stmt.close();

 	            int done = 0;

 	            show_stmt.registerOutParameter( 2, java.sql.Types.INTEGER );
 	            show_stmt.registerOutParameter( 3, java.sql.Types.VARCHAR );

 	            for(;;)
 	            {    
 	                show_stmt.setInt( 1, 32000 );
 	                show_stmt.executeUpdate();
 	                if(dbmsOutputKey!=null && !dbmsOutputKey.isEmpty()){
 	                      String [] outs=show_stmt.getString(3).split("\n");
 	                      int indx=0;
 	                      for(String out:outs){
 	                    	  if(out.contains(dbmsOutputKey)||indx==0){
 	                    		  result=result+out+"\n";
 	                    	  }
 	                    	  indx++;
 	                      }
 	                }else{
 	                	  result=result+show_stmt.getString(3);
 	                }
 	               printresult=printresult+show_stmt.getString(3);
 	                //System.out.println( show_stmt.getString(3).replaceAll("[\r\n]", " ") );
 	                if ( (done = show_stmt.getInt(2)) == 1 ) break;
 	            }
 	            
 	            disable_stmt.executeUpdate();
 	            
 	            enable_stmt.close();
 	            disable_stmt.close();
 	            show_stmt.close();
 	            
 	            //this.dbConn.close();
 	            } catch (SQLException e) {
 	                //e.printStackTrace();
 	            	System.out.println(e.getMessage());
 	            }
 	              catch (Exception e) {
 	                  //e.printStackTrace();
 	            	 System.out.println(e.getMessage());
 	            }
 	              return(result);
 	}
  
 	private void executeSetupQuery(Element vSql){
		
		//System.out.println(vSql.asXML());
 		String dbmsOutput="";
		String queryName=vSql.attributeValue("name");
		String queryDesc= ">>>Executing Query="+queryName+ ": "
			+(vSql.attributeValue("description")==null?"":vSql.attributeValue("description"));
		String sql=vSql.getText();
		String hasDbmsOutput=vSql.attributeValue("dbmsoutput");
		dbmsOutputKey=vSql.attributeValue("dbmsoutputkey");			
		//change database connection				
		String dbName=vSql.attributeValue("dbname");
		if(dbName!=null &&!dbName.isEmpty() && this.dbManager.get(dbName)==null){
			this.changeDbConnection(dbName);
		}else if(dbName!=null &&!dbName.isEmpty()){
			this.changeDbConnectionFromDbmanager(dbName);
		}
		
		if(hasDbmsOutput!=null &&hasDbmsOutput.equalsIgnoreCase("yes")){
			dbmsOutput=this.executeWithDbmsOutput(sql);
			ConfigLoader.addGlobalField(queryName, dbmsOutput);
			this.setupDataSet.put(queryName, dbmsOutput);
			WmLog.getCoreLogger().info(queryDesc+ "\n"+sql);	
			System.out.println(queryDesc+ "\n"+sql);
			WmLog.getCoreLogger().info(ActionHandler.class.getName()+">>>Output:\n"+this.printresult);	
			System.out.println(this.printresult);
			
		}
	}
    
 	private void writeSetupDataFile(){
 		String data=""; 	
 		for(Object key:this.setupDataSet.keySet()){
 			data=data+"<"+key.toString()+ "  value=\""+this.setupDataSet.get(key)+"\"/>\n"; 			
 		} 		
 		if(data!="" &&isItemSetup){
 			data="<global name=\"setup\">\n"+data+"\n</global>";
 			FileUtility.writeToFile(ConfigLoader.getWmGlobalDataRoot()+"setupdata.xml",data);
 		}
 	}
 	
 	
	@Override
	protected String handleHTTPPostUnit() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String handleHTTPPostPerformance() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String handleHTTPGetUnit() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String handleHTTPGetPerformance() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
 	 

}

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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.dom4j.Element;
import com.testmax.framework.ConfigLoader;
import com.testmax.framework.WmLog;


public class DbUtil {
	
	private ItemUtility ut= new ItemUtility();
	static HashMap<String, Connection> dbManager= null;
	
	public DbUtil(){
		dbManager=new  HashMap<String, Connection>();
	}
	public Connection makeOracleDbConnection(String url, String user, String password){
		Connection con =null;
		try{
		 try {
			 Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			
		 } catch (InstantiationException e) {
				String msg=">>ERROR: Can not create database connection using url="+url +" and dbUser="+user+">>>"+e.getMessage();
				printMessage(msg);
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				String msg=">>ERROR: Can not create database connection using url="+url +" and dbUser="+user+">>>"+e.getMessage();
				printMessage(msg);
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				String msg=">>ERROR: Can not create database connection using url="+url +" and dbUser="+user+">>>"+e.getMessage();
				printMessage(msg);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			con = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			String msg=">>ERROR: Can not create database connection using url="+url +" and dbUser="+user+">>>"+e.getMessage();
			printMessage(msg);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return con;
	}
	
	public Connection makeCassandraDbConnection(String url, String user, String password){
		Connection con =null;
		 try {
			 try {
				Class.forName("org.apache.cassandra.cql.jdbc.CassandraDriver");
			} catch (ClassNotFoundException e) {
				String msg=">>ERROR: Can not create database connection using url="+url +" and dbUser="+user+">>>"+e.getMessage();
				printMessage(msg);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(user==null||user.isEmpty()){
				con=DriverManager.getConnection(url);
			}else{
				con = DriverManager.getConnection(url,user,password);
			}
			
		} catch (SQLException e) {
			String msg=">>ERROR: Can not create database connection using url="+url +" and dbUser="+user+">>>"+e.getMessage();
			printMessage(msg);
			e.printStackTrace();
		}
		return con;
	}
	public Connection makePostgreSqlDbConnection(String url, String user, String password){
		Connection con =null;
		 try {
			 try {
				Class.forName("org.postgresql.Driver").newInstance();
			 } catch (InstantiationException e) {
					String msg=">>ERROR: Can not create database connection using url="+url +" and dbUser="+user+">>>"+e.getMessage();
					printMessage(msg);
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					String msg=">>ERROR: Can not create database connection using url="+url +" and dbUser="+user+">>>"+e.getMessage();
					printMessage(msg);
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					String msg=">>ERROR: Can not create database connection using url="+url +" and dbUser="+user+">>>"+e.getMessage();
					printMessage(msg);
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				con = DriverManager.getConnection(url, user, password);
			} catch (SQLException e) {
				String msg=">>ERROR: Can not create database connection using url="+url +" and dbUser="+user+">>>"+e.getMessage();
				printMessage(msg);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		return con;
	}
	public Connection makeMysqlDbConnection(String url, String user, String password){
		Connection con =null;
		 try {
			 try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
			 } catch (InstantiationException e) {
					String msg=">>ERROR: Can not create database connection using url="+url +" and dbUser="+user+">>>"+e.getMessage();
					printMessage(msg);
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					String msg=">>ERROR: Can not create database connection using url="+url +" and dbUser="+user+">>>"+e.getMessage();
					printMessage(msg);
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					String msg=">>ERROR: Can not create database connection using url="+url +" and dbUser="+user+">>>"+e.getMessage();
					printMessage(msg);
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				con = DriverManager.getConnection(url, user, password);
			} catch (SQLException e) {
				String msg=">>ERROR: Can not create database connection using url="+url +" and dbUser="+user+">>>"+e.getMessage();
				printMessage(msg);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return(con);
	}
	
	private void printMessage(String msg){
		WmLog.getCoreLogger().error(msg);
		System.out.println(msg);
	}

	public Connection makeOtherDbConnection(String dbDriver, String url,String user, String password) {
		Connection con =null;
		 try {
			 try {
				Class.forName(dbDriver).newInstance();
			 } catch (InstantiationException e) {
					String msg=">>ERROR: Can not create database connection using url="+url +" and dbUser="+user+">>>"+e.getMessage();
					printMessage(msg);
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					String msg=">>ERROR: Can not create database connection using url="+url +" and dbUser="+user+">>>"+e.getMessage();
					printMessage(msg);
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					String msg=">>ERROR: Can not create database connection using url="+url +" and dbUser="+user+">>>"+e.getMessage();
					printMessage(msg);
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				con = DriverManager.getConnection(url, user, password);
			} catch (SQLException e) {
				String msg=">>ERROR: Can not create database connection using url="+url +" and dbUser="+user+">>>"+e.getMessage();
				printMessage(msg);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return(con);
	}
	
	public Connection getDbConnection(String dbName){
		Connection con=null;
		String dbHost=null;
		String dbService=null;		
		String dbUser=null;
		String dbPass=null;	
		String dbPort=null;
		String provider=null;
		String driver=null;
		Element dbEnv=null;
		String url=null;
		if(dbManager.get(dbName)!=null){
			return dbManager.get(dbName);
		}
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
						provider=elm.getText();
					}else if(elm.getName().equalsIgnoreCase("driver")&& !elm.getText().isEmpty()){
						driver=elm.getText();
					}
				}
			}
		}
		
		
		if(url==null){
			url=getConnectionStr(provider,dbHost,dbService,dbPort);
		}
					
		 con=makeDbConnection( provider, driver, url,  dbUser,  dbPass);
		 dbManager.put(dbName, con);
		 return con;
	}

	public Connection getDbConnection(Element dbEnv, String dbName){
		Connection con=null;
		String dbHost=null;
		String dbService=null;		
		String dbUser=null;
		String dbPass=null;	
		String dbPort=null;
		String provider=null;
		String driver=null;
		String url=null;
		if(dbManager.get(dbName)!=null){
			return dbManager.get(dbName);
		}
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
					provider=elm.getText();
				}else if(elm.getName().equalsIgnoreCase("driver")&& !elm.getText().isEmpty()){
					driver=elm.getText();
				}
			}
		}
		
		
		
		if(url==null){
			url=getConnectionStr(provider,dbHost,dbService,dbPort);
		}
					
		 con=makeDbConnection( provider, driver, url,  dbUser,  dbPass);
		 dbManager.put(dbName, con);
		 
		 return con;
	}
	
	private Connection makeDbConnection(String provider,String driver,String url, String user, String password) {
		Connection con =null;
		DbUtil du= new DbUtil();
		 if(provider.equalsIgnoreCase("oracle")){
			 con=du.makeOracleDbConnection(url, user, password);
		 }else if(provider.equalsIgnoreCase("cassandra")){
			 con=du.makeCassandraDbConnection(url, user, password);
		 }else if(provider.equalsIgnoreCase("postgresql")){
			 con=du.makePostgreSqlDbConnection(url, user, password);
		 }else if(provider.equalsIgnoreCase("mysql")){
			 con=du.makeMysqlDbConnection(url, user, password);
		 }else{
			 if(driver!=null&&!driver.isEmpty()){
				 con=du.makeOtherDbConnection(driver,url, user, password);
			 }else{
				printMessage(">>>>ERROR To Connect Database. Please provide driver class name");
			 }
		 }
		return con;
	}
  
	public String getColumnValue(ArrayList<ArrayList<Object>> rsData,int rowId, String column){
 		
 		return(ut.getColumnValue(rsData, rowId, column));
 		
 	}
  public ArrayList<ArrayList<Object>> getQueryResult(String sql, String dbName){
    	
    	Statement st=null;
    	ArrayList<ArrayList<Object>> rsData=null;
    	
    	Connection con=this.getDbConnection(dbName);
		
    	if(con!=null){
    		try {
				st=con.createStatement();
				ResultSet rs=st.executeQuery(sql);
				rsData=ut.setResults2Array(rs, null);
				if(st!=null){
	    			try {
						st.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				printMessage("Problem occured while querying into database \n"+e.getMessage() +"\n" +sql);
				
				e.printStackTrace();
				if(!e.getMessage().contains("Duplicate")){
					dbManager.remove(dbName);
				}
			
			}finally{
	    		if(st!=null){
	    			try {
						st.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}
	    		
	    	}
    	}
    	
    
    	return(rsData);
    	
    }
	public boolean executeQuery(String sql, String dbName){
		 Connection con=null;
		if(dbManager.get(dbName)!=null){
			con= dbManager.get(dbName);
		}
		if(con==null){
		 con=this.getDbConnection(dbName);
		 dbManager.put(dbName, con);
		}
		 Statement st=null;
		try {
			st=con.createStatement();
			st.executeUpdate(sql);
		} catch (SQLException e){
			printMessage("##### Problem occured while executing query into database \n"+e.getMessage() +"\n" +sql);
			if(!e.getMessage().contains("Duplicate")){
				dbManager.remove(dbName);
			}
			//e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/*
	 *  Prepare default connection strings for Oracle Rac
	 */
	private String getConnectionStr(String provider, String rac, String service, String port) {
		 String url="jdbc:oracle:thin:@(DESCRIPTION ="+
		    "(ADDRESS_LIST ="+
		      "(ADDRESS = (PROTOCOL = TCP)(HOST = "+rac+")(PORT ="+port+"))"+
		    ")"+
		    "(CONNECT_DATA ="+
		      "(SERVICE_NAME = "+service+")"+
		    ")"+
		  ")";
		  if(provider.equalsIgnoreCase("cassandra")){
			  url = "jdbc:cassandra://" + rac + ":" + port + "/" + service + "?version=3.0.0"; 
		  }else  if(provider.equalsIgnoreCase("mysql")){
			  url = "jdbc:mysql://" + rac + ":" + port + "/" + service ; 
		  }else if(provider.equalsIgnoreCase("postgresql")){
			  url = "jdbc:postgresql://" + rac + ":" + port + "/" + service ; 
		  }
		  
	        return url;
	    }

	
}

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
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.dom4j.Element;
import org.dom4j.Attribute;
import org.jfree.chart.ChartUtilities;
import org.jfree.data.time.Millisecond;






import com.testmax.handler.ActionHandler;
import com.testmax.runner.ExecutionEngin;
import com.testmax.runner.TestEngine;
import com.testmax.util.DbUtil;
import com.testmax.util.FileUtility;
import com.testmax.util.ItemUtility;





public abstract class BasePage {

    
    /*
     *  Execution Count Graph
     */
     
    protected volatile CreateGraph executeGraph=null;
    
    /*
     *  Execution Count Graph
     */
    protected volatile CreateGraph responseGraph=null;
    
    /*
     *  Execution Count Graph
     */
    protected volatile CreateGraph elaspedTimeGraph=null;
    
    /*
     *  Execution Count Graph
     */
    protected volatile CreateGraph activeThreadGraph=null;
    
    /*
     * Use to synchronize charts
     */
    private Object draw = new Object();
    
    /*
     *  Use this to synchronize Request Object
     */
    private Object state = new Object();
           
    /*
     * PageURL for this page
     */
    private PageURL pageUrl=null;
    
    /*
     * URLConfig for this page
     */
    
    protected URLConfig urlConfig=null;
    
    /*
     * Action Result
     */
    
    private HashMap<String,Integer> actionResult=null;  
    /*
     * Name of the page associated with the test
     */
    
    private String pagename=null;  
    
    /*
     *  Name of the test associated with the page in test configuration XML file within testdata
     */
    
    private String testname=null; 
    
    
    /*
     *  Name of the browsers with the page in test configuration XML file within testdata
     */
    
    private String browsers=null;
    
    /*
     *  Group by column for the dataset browser thread
     */
    
    private String groupbythread=null;
    
    /*
     *  override passwords lke [columnname]@[password value] 
     *   
     * Returns password overide columns and value i.e.  overridepassword="adminpassword@Spigit1234;password@Spigit123"
     */
    
    private String overrideattributes=null; 
    
    /*
     *  extension use to access different XLS file by overriding current dataset in the action with a extension 
     */
    private String datasetextension=null;
    
    /*
     *  whether global dataset is executed once
     */
    
    private String globalExecution=null;; 
    
    
    /*
     *  default URL with the page in test configuration XML file within testdata
     */
    
    private String baseurl=null; 
   
    /*
     * Number of thread for this PageURL, default=1
     */
    
    private int threadCount=1;  
    
    
    /*
     * Number of thread for this PageURL exited after timeout, default=0
     */
    
    private int threadExit=0;   
   
    /*
     * Number of thread for this Test SUITE based of number of actions * threadCount set by the user, default=1
     */
    
    private int newThreadCount=1;
    
    /*
     * timeout of thread for this PageURL, default=1 Hr =60,000 mili sec
     */
    
    protected double timeout=60000;   
    
    /*
     * Whether the test i unit testing
     */
    private boolean isUnitTest=false;    
    
    
    /*
     * action which needs to be performed on this page based on testdata
     */
    
    protected String action=null;
    
    /*
     * HTTP Request object
     */
    
    public PerformerBase request=null;
    
    
    /*
     * API Request object
     */
    
    public ActionHandler proc=null;
    
    /*
     *  DbUtil for automation database connection
     */
    private DbUtil dutl=new DbUtil();
    /*
     * Testdata for each test
     */
    
    private Connection dbConn = null;	
    
    private Element testdata=null;
    
    
    private String urlResponse=null;
    
    /*
     *  If any specific summary data need to be customized based on performance Adaptor this variable can be used with a setter method 
     *  Method: setPerformanceSummaryReportData()
     */
    private String perfSummaryReportData=null;
    
    public String getURLResponse(){
    	return (this.urlResponse);
    }
    /*
     * Retrieves Page URL
     */
    public PageURL getPageURL(){
        return (this.pageUrl);
    }
    
    
    /*
     * Retrieves URLConfig for this URL object
     */
    public URLConfig getURLConfig(){
        return (this.urlConfig);
    }
    
    
    
    /*
     * Verifies if the current test is usnit test
     */
    public boolean isUnitTest(){
        return (this.isUnitTest);
    }
    
    /*
     * Retrieves timeout for performance
     */
    public double getTimeOut(){
        return (this.timeout);
    }

    /*
     * Retrieves number of thread counts for this URL object
     */
    public int getThreadCount(){
        return (this.threadCount);
    }
    
    /*
     * Returns password overide columns and value i.e.  overridepassword="adminpassword@Spigit1234;password@Spigit123"
     */
    public String getOverrideAttributes(){
        return (this.overrideattributes);
    }
    
    /*
     * Returns group by column for dataset thread
     */
    public String getGroupByThread(){
        return (this.groupbythread);
    }
    
    /*
     * Returns extension name for dataset
     */
    public String getDatasetExtension(){
        return (this.datasetextension);
    }
    
    /*
     * Set number of thread count for this URL object 
     */
    public void setThreadCount(int count){
        this.threadCount=count;
    }
    
    /*
     * Set db connection 
     */
    public void setDbConnection(Connection dbConn ){
        this.dbConn=dbConn;
    }
    
    /*
     * Retrieves name of the page within testdata configuration
     */
    public String getPageName(){
        return (this.pagename);
    }
    /*
     * Retrieves name of the test within testdata configuration
     */
    public String getTestName(){
        return (this.testname);
    }
    
    /*
     * Retrieves cookie by name
     */
    public String getCookieByName(String name){
    	if(this.request!=null &&this.request.cookies!=null){
    		
    		for (int i = 0; i < this.request.cookies.length; i++)
    		{
                   if (this.request.cookies[i].getName().equalsIgnoreCase(name))
    			{
                       
                     return this.request.cookies[i].getValue();
                       //this.request.cookies[i].getDomain();
                       //this.request.cookies[i].getPath();
                      //this.request.cookies[i].getExpiryDate();
                      //this.request.cookies[i].getSecure();
                   }
               }
    	}
    	return null;
    }
    /*
     * Retrieves browsers configuration
     */
    public String[] getBrowsers(){
    	if(this.browsers!=null){
    		return (this.browsers.split(";"));
    	}
    	return null;
    }
    
    /*
     * Retrieves Page URL
     */
    public Element getTestData(){
        return (this.testdata);
    }
    
    
    private void invokeUnitHttpGET(){
		try {
		    this.urlResponse=this.executeHttpGET();
		    int totalInvoked=(this.actionResult.get(proc.getActionName())==null?0:this.actionResult.get(proc.getActionName()))+1;        	
			this.actionResult.put(proc.getActionName(), totalInvoked);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}       
    }
    
    private void invokeUnitHttpPOST(){
		try {
			this.urlResponse=this.executeHttpPOST();
		    int totalInvoked=(this.actionResult.get(proc.getActionName())==null?0:this.actionResult.get(proc.getActionName()))+1;        	
			this.actionResult.put(proc.getActionName(), totalInvoked);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}       
    }
    
    private void invokeUnitHttpsGET(){
		try {
			this.urlResponse=this.executeHttpsGET();
		    int totalInvoked=(this.actionResult.get(proc.getActionName())==null?0:this.actionResult.get(proc.getActionName()))+1;        	
			this.actionResult.put(proc.getActionName(), totalInvoked);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}       
    }
    
    private void invokeUnitHttpsPOST(){
    	try {
    		this.urlResponse=this.executeHttpsPOST();
            int totalInvoked=(this.actionResult.get(proc.getActionName())==null?0:this.actionResult.get(proc.getActionName()))+1;        	
        	this.actionResult.put(proc.getActionName(), totalInvoked);
    
	    } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
        
    }
   
    private void invokeUnitTest(ActionHandler proc) {
    		String startMsg="\n\n********************** Started Testing of Action="+proc.getActionName()+"  *******************************\n\n";
    		String endMsg="\n**********************  Test Ends for Action="+proc.getActionName()+"  *******************************\n\n";
    	 	WmLog.getCoreLogger().info(startMsg);
    	 	System.out.println(startMsg);
            this.executeAPI(proc);  
            int totalInvoked=(this.actionResult.get(proc.getActionName())==null?0:this.actionResult.get(proc.getActionName()))+1;        	
        	this.actionResult.put(proc.getActionName(), totalInvoked);
        	if(this.proc!=null){
   			 this.proc.closeDb();
   		 	}
        	WmLog.getCoreLogger().info(endMsg);
    	 	System.out.println(endMsg);
	}
    
    private void invokeAPI(ActionHandler proc) {
    	long starttime=System.currentTimeMillis();
        long timer=0;  
        int invoked=0;
        try { 
        	
        	while (timer<=this.timeout){
                this.executeAPI(proc);
                timer=(System.currentTimeMillis()-starttime)/1000;
                synchronized (draw){ 
                	invoked++;
                	executeGraph.ts.addOrUpdate(new Millisecond(), PerformerBase.executionCount);
                	responseGraph.ts.addOrUpdate(new Millisecond(), PerformerBase.avgOKTime); 
                	elaspedTimeGraph.ts.addOrUpdate(new Millisecond(), PerformerBase.elaspedTime); 
                	activeThreadGraph.ts.addOrUpdate(new Millisecond(), PerformerBase.activeThread); 
                }
              }
        	
           synchronized (proc){ 
            	if(proc!=null){
        		
            	int totalInvoked=(this.actionResult.get(proc.getActionName())==null?0:this.actionResult.get(proc.getActionName()))+invoked;
        	
            	this.actionResult.put(proc.getActionName(), totalInvoked);
            	threadExit=threadExit+1;
            	WmLog.getCoreLogger().info("Exiting Thread="+threadExit +" >>> Total Thread Count="+newThreadCount +" >>> Action="+proc.getActionName());
        		System.out.println("Exiting Thread="+threadExit +" >>> Total Thread Count="+newThreadCount+" >>> Action="+proc.getActionName());
        	
        		proc.closeDb();
        		
            	}
            }
            
            WmLog.getCoreLogger().info("Time Elasped (sec) ="+timer +"(sec)");
            System.out.println("Time Elasped (sec)="+timer +"(sec)");
    		
            //Add graph for Execution Count           
            //if(this.threadCount-1== HttpThread.getThreadExit() && new File(ConfigLoader.getWmOutputReportPath()).mkdir()){
            if( threadExit==newThreadCount && new File(ConfigLoader.getWmOutputReportPath()).mkdir() ){
           
	            saveGraph(executeGraph,
	            		ConfigLoader.getWmOutputReportPath()+"execution.jpg",
	            		"Execution Graph",
	             	   "Time",
	             	   "Execution Count");
	            
	            //Add graph for Response Count
	            saveGraph(responseGraph,
	            		ConfigLoader.getWmOutputReportPath()+"response.jpg",
	            		"Response Graph",
	             	   "Time",
	             	   "Response Time (ms)");
	            
	          //Add graph for Elasped Count
	            saveGraph(elaspedTimeGraph,
	            		ConfigLoader.getWmOutputReportPath()+"elaspedtime.jpg",
	            		"Elasped Time Graph",
	             	   "Time",
	             	   "Elasped Time (ms)");
	            
	            createHtmlReport(this.action);
           
            }           
           
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
	}
    
    private void saveGraph(CreateGraph graph, String file, String title, String xname, String yname){
    	WmLog.getCoreLogger().info("Saving Graphs for "+title );
    	//Add graph for Execution Count
        try {
        	
            ChartUtilities.saveChartAsJPEG(new File(file), graph.getChart(), 700, 500);
           
            WmLog.getCoreLogger().info("Saved Graphs for "+title );
       
        } catch (IOException e) {
        	
        	WmLog.getCoreLogger().info("Problem Occured while saving the Graphs for "+title +" "+e.getMessage() );
           
        }
    }
    private void createHtmlReport(String title){
    	WmLog.getCoreLogger().info("Creating HTML report for "+title );
    	try {
			FileWriter file= new FileWriter(new File(ConfigLoader.getWmOutputReportPath()+"html_report.html"));
			file.write(getHeader());
			file.write(getExecutionData());
			if(!this.perfSummaryReportData.isEmpty()){
				file.write(this.perfSummaryReportData);
			}
			file.write(getGraphs());
			file.write(getFooter());
			file.close();
		 WmLog.getCoreLogger().info("Saved HTML Report for "+title );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			WmLog.getCoreLogger().info("Problem Occured while saving the HTML Report file html_report.html for "+title +" "+e.getMessage() );
		}
    	
    }
    
    private void createUnitTestHtmlReport(String title){
    	WmLog.getCoreLogger().info("Creating HTML report for "+title );
    	try {
			FileWriter file= new FileWriter(new File(ConfigLoader.getWmOutputReportPath()+"html_report.html"));
			file.write(getUnitTestHeader());
			file.write(getUnitTestExecutionData());			
			file.write(getFooter());
			file.close();
		 WmLog.getCoreLogger().info("Saved HTML Report for "+title );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			WmLog.getCoreLogger().info("Problem Occured while saving the HTML Report file html_report.html for "+title +" "+e.getMessage() );
		}
    	
    }
    
  public void printHtmlSummaryReport(){
	     //String link_prefix=TestEngine.suite.getJenkinsurl()+"job/"+TestEngine.suite.getJobname()+"/ws/output/"+ExecutionEngin.getDynamicPath()+"/report/"+TestEngine.suite.getEnv()+"_jenkin_summary_report_"+TestEngine.suite.getBuildid()+".html";
	     String link_prefix=TestEngine.suite.getJenkinsurl().replace(":8080", "")+"results/"+TestEngine.suite.getJobname()+"/output/"+ExecutionEngin.getDynamicPath()+"/report/"+TestEngine.suite.getEnv()+"_jenkin_summary_report_"+TestEngine.suite.getBuildid()+".html";
	    //WmLog.printMessage("\n<a href=\""+link_prefix.replaceAll(" ", "%20")+"\">Click Here To See Summary Report</a>");
	     WmLog.printMessage("\n GENERATED SUMMARY REPORT URL:");
	     WmLog.printMessage("\n"+link_prefix.replaceAll(" ", "%20"));
	     WmLog.printMessage("\n ACCESS ALL JOBS HISTORY REPORTS:");
	     WmLog.printMessage("\n"+TestEngine.suite.getJenkinsurl().replace(":8080", "")+"results");
	    //WmLog.printMessage(geTestSummaryHeader()+getUnitTestSummaryResultData()+getFooter());
    	
    }    
  public void createUnitTestHtmlSummaryReport(String title){
    	
    	if(TestEngine.suite!=null||ConfigLoader.getConfig("REPORT_SUMMARY").equalsIgnoreCase("on")){
    		String reportfile="html_summary_report.html";
    		if(TestEngine.suite!=null){
    			reportfile=TestEngine.suite.getEnv()+"_jenkin_summary_report_"+TestEngine.suite.getBuildid()+".html";
    		}
	    	WmLog.getCoreLogger().info("Creating HTML report for "+title );
	    	try {
				FileWriter file= new FileWriter(new File(ConfigLoader.getWmOutputReportPath()+reportfile.toLowerCase()));
				file.write(geTestSummaryHeader());
				file.write(getUnitTestSummaryResultData());			
				file.write(getFooter());
				file.close();
			 WmLog.getCoreLogger().info("Saved HTML Report for "+title );
			 if(TestEngine.suite!=null){
				 printHtmlSummaryReport();
			 }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Problem Occured while saving the HTML Summary Report file html_report.html for "+title +" "+e.getMessage());
				WmLog.getCoreLogger().info("Problem Occured while saving the HTML Report file html_report.html for "+title +" "+e.getMessage() );
			}
    	}
    	
    }    
    
    
    private String getUnitTestHeader(){
    	String header="<html><head><h2><center><font face=\"verdana\" size=4px color=\"black\"><b><u>Test Results</u></b></font></center></h2></head><body>"+
    	"<table border=0 width=60%><br><br><tr><TD><font face=\"verdana\" size=2px color=\"#1589FF\"><b>SUITE  Name</b> :</font> <font face=\"verdana\" size=2px color=\"#1589FF\">"+this.testname+"</font></TD>"+
    	"<TD><font face=\"verdana\" size=2px color=\"#1589FF\"><b>XML Name</b>&nbsp;:</font><font face=\"verdana\" size=2px color=\"#1589FF\">"+this.pagename+".xml</font> </TD></tr></table>"+
    	"<table width=\"100%\" border=\"1\" border-color=\"black\" halign=\"top\" >"+
    	"<tr bgcolor=\"#616D7E\" height=\"50\">"+
    	"<td  align=center width=\"10%\"><font face=\"verdana\" size=2px color=white><b>Action ID</b></font></td>" +
    	"<td align=center width=\"15%\"><font face=\"verdana\" size=2px color=white><b>Test ID</b></font></td>"+  
    	"<td align=center width=\"15%\"><font face=\"verdana\" size=2px color=white><b>Steps</b></font></td>"+    	
    	"<td align=center width=\"15%\"><font face=\"verdana\" size=2px color=white><b>Test Count</b></font></td>"+
    	"<td align=center width=\"25%\"><font face=\"verdana\" size=2px color=white><b>Total Validator</b></font></td>"+
    	"<td align=center width=\"25%\"><font face=\"verdana\" size=2px color=white><b>Validator Passed</b></font></td>"+
    	"<td align=center width=\"25%\"><font face=\"verdana\" size=2px color=white><b>Validator Failed</b></font></td>"+
    	"<td align=center width=\"25%\"><font face=\"verdana\" size=2px color=white><b>Average Response(ms)</b></font></td></tr>";
    	
    	return(header);
    	
    }
    
    private String geTestSummaryHeader(){
    	String joburl=this.testname;
    	String env=ConfigLoader.getConfig("QA_TEST_ENV");
    	if(TestEngine.suite!=null){
    		joburl="<a href=\""+TestEngine.suite.getJenkinsurl()+"\">Jenkins</a>";
    		env=TestEngine.suite.getEnv();
    	}
    	String header="<html><head><h2><center><font face=\"verdana\" size=4px color=\"black\"><b><u>Test Results</u></b></font></center></h2></head><body>"+
    	"<table border=0 width=60%><br><br><tr><TD><font face=\"verdana\" size=2px color=\"#1589FF\"><b>Summary Report URL</b> :</font> <font face=\"verdana\" size=2px color=\"#1589FF\">"+joburl+"</font></TD>"+
    	"<TD><font face=\"verdana\" size=2px color=\"#1589FF\"><b>Test Environment:</b>&nbsp;:</font><font face=\"verdana\" size=2px color=\"#1589FF\">"+env+"</font> </TD></tr></table>"+
    	"<table width=\"100%\" border=\"1\" border-color=\"black\" halign=\"top\" >"+
    	"<tr bgcolor=\"#616D7E\" height=\"50\">"+
    	"<td  align=center width=\"10%\"><font face=\"verdana\" size=2px color=white><b>Test SUITE</b></font></td>" +    	
    	"<td align=center width=\"15%\"><font face=\"verdana\" size=2px color=white><b>Date</b></font></td>"+    	
    	"<td align=center width=\"15%\"><font face=\"verdana\" size=2px color=white><b>Scenerio Count</b></font></td>"+
    	"<td align=center width=\"25%\"><font face=\"verdana\" size=2px color=white><b>Total Tests</b></font></td>"+
    	"<td align=center width=\"25%\"><font face=\"verdana\" size=2px color=white><b>Test Passed</b></font></td>"+
    	"<td align=center width=\"25%\"><font face=\"verdana\" size=2px color=white><b>Test Failed</b></font></td>"+
    	"<td align=center width=\"25%\"><font face=\"verdana\" size=2px color=white><b>Env</b></font></td>"+
    	"<td align=center width=\"25%\"><font face=\"verdana\" size=2px color=white><b>Browser</b></font></td></tr>";
    	
    	return(header);
    	
    }
    
    private void insertSummaryResult(){
    	String actionname=null;
    	//String customname=this.pagename;
    	if(TestEngine.suite!=null &&TestEngine.suite.getTestSuite()!=null){
    		actionname=TestEngine.suite.getTestSuite().attributeValue("action").toString();
    		/*if(!TestEngine.suite.getName().contains("$name")){
    			customname=TestEngine.suite.getName();
    		}*/
    	}
    	String suite=(actionname!=null &&actionname.contains("unit@")?this.testname+" : "+actionname.split("@")[1]:this.testname);
    	if(suite.length()>=500){
    		suite=suite.substring(0, 499);
    	}
    	String releaseno=ConfigLoader.getConfig("RELEASE");
    	String env=ConfigLoader.getConfig("QA_TEST_ENV");
    	String actioncount=String.valueOf(PerformerBase.executionCount);
    	String testcount=String.valueOf(getTotalValidator());
    	String passcount=String.valueOf(getTotalPassedValidator());
    	String failcount=String.valueOf(getTotalFailedValidator());
    	String pctpass=String.valueOf(getTotalPctPassed());
    	String pctfail=String.valueOf(getTotalPctFailed());
    	String jobname="";
    	String jenkinsurl="";
    	String browser="";
    	String dbdate="sysdate";
    	String runid=ExecutionEngin.getDynamicPath();
    	Statement st=null;
    	String insert_sql="";
    	if(TestEngine.suite!=null){
    		env=TestEngine.suite.getEnv();
    		jobname=TestEngine.suite.getJobname();
			jenkinsurl=TestEngine.suite.getJenkinsurl();
			browser=TestEngine.suite.getBrowser();
    	}
    	String delete_sql="delete from QA_AUTOMATION_SUMMARY where suite='"+suite+"' and env='"+env+"' and browser='"+browser+"'";
    	if(this.dbConn==null &&TestEngine.suite!=null){
    		this.dbConn=this.getDbConnection();
    	}
    	if(this.dbConn!=null){
    		try {
				st=this.dbConn.createStatement();
				st.executeUpdate(delete_sql);
			} catch (SQLException e){
				WmLog.getCoreLogger().info("Problem Occur for Saving Summary into database \n"+e.getMessage() +"\n" +delete_sql);
				
	    		
				try {
					Element dbEnv=ConfigLoader.getDatabaseEnv("automation");
					if(dbEnv.attributeValue("db")!=null &&dbEnv.attributeValue("db").equalsIgnoreCase("mysql")){
						dbdate="now()";
					}
					this.dbConn=dutl.getDbConnection(ConfigLoader.getDatabaseEnv("automation"),"automation");
					st=this.dbConn.createStatement();
					st.executeUpdate(delete_sql);// TODO Auto-generated catch block
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				e.printStackTrace();
			}
    		
			 insert_sql="insert into QA_AUTOMATION_SUMMARY(suite,releaseno,rundate,testcount,passcount,failcount,pctpass,pctfail,runby,env,runid,jobname,jenkinsurl,browser) values("+
							  "'"+suite+"','"+releaseno+"',"+dbdate+","+testcount+","+passcount+","+failcount+","+pctpass+","
							  +pctfail+",'Automation','"+env+"','"+runid+"','"+jobname+"','"+jenkinsurl+"','"+browser+"')";
			
			try {				
				st.executeUpdate(insert_sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				WmLog.getCoreLogger().info("Problem Occur for Saving Summary into database \n"+e.getMessage() +"\n" +insert_sql);
				e.printStackTrace();
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
    	
    }
    
    
    private void insertJbertTestCaseResult(){
    	ResultSet rs=null;
    	
    	String url=ConfigLoader.getDbProperty("mysqlDBUrl");
		String user=ConfigLoader.getDbProperty("mysqlUser");
		String password=ConfigLoader.getDbProperty("mysqlPassword");
    	Connection mysqlCon=dutl.makeMysqlDbConnection(url, user, password);
    	SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS");
    	String date=sf.format(new Date());
    	String suite="BE-"+this.testname+" (SQL Tester)";
    	String release="R"+ConfigLoader.getConfig("RELEASE");
    	String env=ConfigLoader.getConfig("QA_TEST_ENV");
    	String testtype=ConfigLoader.getConfig("TEST_TYPE");
    	String track=ConfigLoader.getConfig("TRACK");
    	String host="";
    	String runId="";
    	String testName="";
    	
    	if(this.action.indexOf("@")>0){
    		testName=this.action.substring(this.action.indexOf("@")+1,this.action.length());
    	}
		try {
			 host= "SQLTester-"+java.net.InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			host="localhost";
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
       
    	
    	Statement st=null;    	
    	
    	String runId_sql="SELECT TestrunID FROM jbert.summary where upper(releaseNumber) like upper('"
    		+release+"') and upper(Type)=upper('"+testtype+"') and host like ltrim('SQLTester%') order by starttime desc";
    	
    	//System.out.println(runId_sql);
    	if(mysqlCon!=null){
    		try {
				st=mysqlCon.createStatement();
				rs=st.executeQuery(runId_sql);
				if(rs.next()){
					runId=rs.getString("TestrunID");
				}
				if(runId==null||runId.isEmpty()){
					runId=date.toString();
					String insert_sql="INSERT INTO `jbert`.`summary`"+
							"("+
							"`TestRunID`,"+
							"`Host`,"+
							"`Environment`,"+
							"`Track`,"+
							"`ReleaseNumber`,"+
							"`Type`,"+
							"`StartTime`,"+
							"`EndTime`"+
							")"+
							"VALUES"+
							"('"+
							runId+"','"+
							 host+"','"+
							env+"','"+
							track+"','"+
							release+"','"+
							testtype+"',"+
							"now(),"+
							"now())";
					
					st.executeUpdate(insert_sql);

					
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//First delete if the same TestRunId and AreaName
			String delete_sql="Delete from jbert.testcase where upper(TestRunID)=upper('"+runId+"') and upper(AreaName)=upper('"+suite+"')";
			
			if(!testName.isEmpty()){
				delete_sql=delete_sql+" and TestcaseName like ltrim('"+testName+"%')";
			}
			try {
				st.executeUpdate(delete_sql);				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {	
			
		   
			System.out.println("---------Sending Data to JBERT Dashboard ---------------------------");
			WmLog.getCoreLogger().info("----------------Sending Data to JBERT Dashboard ---------------");
			
			
    		
			for(Object key: this.actionResult.keySet()){
				
				HashMap<String, String> results=PerformerBase.validatorMessage.get(key);
				
				for(Object test:results.keySet()){
					
					String status=((results.get(test).contains("FAILED") && !results.get(test).contains(":-PASSED")) ?"FAILED":"PASSED");
					String testcaseName=key+"-"+test;
					int resultlength=results.get(test).length();
					int available=500-testcaseName.length()-3;
					if( available>0){
						testcaseName=testcaseName+ ":-" +results.get(test).substring(0, (resultlength>available?available:resultlength));
					}
						
					String insert_sql= "INSERT INTO jbert.testcase"+
					"(`TestRunID`,"+
					"`AreaName`,"+
					"`TestcaseName`,"+
					"`TestcaseStatus`,"+
					"`MarkedAsPassed`,"+
					"`MarkedAsPassedReason`,"+
					"`Owner`,"+
					"`Browser`,"+
					"`PathToTestcaseInQualityCenter`,"+
					"`ScriptLogPath`,"+
					"`DrilldownInfo`,"+
					"`TimedOutMessage`,"+
					"`WebServerExceptionRetrieved`,"+
					"`ScreenshotCaptured`,"+
					"`StartTime`,"+
					"`EndTime`,"+
					"`ExceptionName`,"+
					"`ExceptionErrorMessage`,"+
					"`AttemptNumber`,"+
					"`TicketNo`,"+
					"`IssueType`,"+
					"`IssueSubType`)"+
					"VALUES"+
					"("+
					"'"+runId+"',"+  			//TestRunID
					"'"+suite+"',"+				//AreaName
					"'"+testcaseName+"',"+      //TestcaseName
					"'"+status+"',"+			//TestcaseStatus
					"0,"+						//MarkedAsPassed
					"' ',"+						//MarkedAsPassedReason
					"'SQLTester',"+				//Owner
					"' ',"+						//Browser
					"' ',"+						//PathToTestcaseInQualityCenter
					"' ',"+						//ScriptLogPath
					"'"+results.get(test).substring(0,(resultlength>999?999:resultlength))+"',"+ //DrilldownInfo
					"' ',"+						//TimedOutMessage
					"0,"+						//WebServerExceptionRetrieved
					"0,"+						//ScreenshotCaptured
					"now(),"+
					"now(),"+
					"' ',"+
					"' ',"+
					"1,"+
					"' ',"+
					"' ',"+
					"' '"+
					")";
					
					
		    		try{		    						
		    				st.executeUpdate(insert_sql);
		    		} catch (SQLException e) {	
		    			WmLog.getCoreLogger().info(insert_sql);
		        		System.out.println(insert_sql);
		    			System.out.println(e.getMessage());
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
					
				}
			}
			
			WmLog.getCoreLogger().info("JBERT URL= http://automationjst45:8080/jdash/layout_executive.jsp?testRunID="+runId);
    		System.out.println("JBERT URL= http://automationjst45:8080/jdash/layout_executive.jsp?testRunID="+runId);
    	
			} catch (Exception e) {			
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
			finally{
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
    }
    
    
 
    private String getUnitTestSummaryResultData(){
    	
    	Statement st=null;
    	ArrayList<ArrayList<Object>> rsData=null;
    	ItemUtility ut= new ItemUtility();
    	//String suite=this.testname;
    	String releaseno=ConfigLoader.getConfig("RELEASE");
    	String env=ConfigLoader.getConfig("QA_TEST_ENV");
    	String row="";
    	String actionRow="";
    	if(TestEngine.suite!=null){
    		env=TestEngine.suite.getEnv();
    	}
    	String sql="select sum(testcount) totaltest, sum(passcount) totalpass, sum(failcount) totalfail, trunc((sum(passcount)/sum(testcount))* 100,2) as pctpass,"+
    	"trunc((sum(failcount)/sum(testcount))* 100,2) as pctfail from QA_AUTOMATION_SUMMARY where env='"+env+"'";
    	if(this.dbConn==null){
    		this.dbConn=this.getDbConnection();
    	}
    	if(this.dbConn!=null){
    		try {
				st=this.dbConn.createStatement();
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
				this.proc.printMessage("Problem Occur for Saving Summary into database \n"+e.getMessage() +"\n" +sql);
				WmLog.getCoreLogger().info("Problem Occur for Saving Summary into database \n"+e.getMessage() +"\n" +sql);
				e.printStackTrace();
			}
			 actionRow="\n";    	
	    	 row="<tr bgcolor=\"#7D226D\">"+
	    	"<td align=left ><font face=\"verdana\" size=2px color=white> Test Env: "+ env+" (Grand Total)</font></td>"+    	  	
	    	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+new Date(System.currentTimeMillis())+"</font> </td>"+    	
	    	"<td  align=left ><font face=\"verdana\" color=white size=2px></font> </td>"+ 
	    	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+ut.getColumnValue(rsData, 1, "totaltest")+"</font> </td>"+
	    	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+ut.getColumnValue(rsData, 1, "totalpass")+"<br>("+ut.getColumnValue(rsData, 1, "pctpass")+"%)</font> </td>"+
	    	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+ut.getColumnValue(rsData, 1, "totalfail")+"<br>("+ut.getColumnValue(rsData, 1, "pctfail")+"%)</font> </td>"+
	    	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+env+"</font> </td>"+
	    	"<td  align=left ><font face=\"verdana\" color=white size=2px>All</font> </td></tr>"; 				
			
	    	String sql_detail=" select suite,releaseno,rundate,testcount,passcount,failcount,pctpass,pctfail,runby,env,runid,jobname,browser from QA_AUTOMATION_SUMMARY where env='"+env+"'";
			try {
				st=this.dbConn.createStatement();
				ResultSet rs=st.executeQuery(sql_detail);
				rsData=ut.setResults2Array(rs, null);
				
			} catch (SQLException e) {
				this.proc.printMessage("Problem Occur for Saving Summary into database \n"+e.getMessage() +"\n" +sql);
			
				// TODO Auto-generated catch block
				e.printStackTrace();
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
    	int rowCount=1;
    	String link_prefix="../../";
    	
    	while( rowCount <rsData.size()){ 
    		if(TestEngine.suite!=null){
        		link_prefix=TestEngine.suite.getJenkinsurl().replace(":8080", "")+ut.getColumnValue(rsData, rowCount, "jobname")+"/output/";
        	}
    		actionRow=actionRow+
    		"<tr bgcolor=\"#4AA02C\">"+
        	"<td align=left ><font face=\"verdana\" size=2px color=white><a href=\""+link_prefix+ut.getColumnValue(rsData, rowCount, "runid")+"/report/html_report.html\">"+ut.getColumnValue(rsData, rowCount, "suite")+"</a></font></td>"+    	  	
        	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+ut.getColumnValue(rsData, rowCount, "rundate")+"</font> <br></td>"+        	
        	"<td  valign=top ><font face=\"verdana\" color=white size=2px></font> </td>"+  
        	"<td  valign=top ><font face=\"verdana\" color=white size=2px>"+ut.getColumnValue(rsData, rowCount, "testcount")+"</font> </td>"+
        	"<td  valign=top ><font face=\"verdana\" color=white size=2px>"+ut.getColumnValue(rsData, rowCount, "passcount")+"<br>("+ut.getColumnValue(rsData, rowCount, "pctpass")+")%</font> </td>"+
        	"<td  valign=top ><font face=\"verdana\" color=white size=2px>"+ut.getColumnValue(rsData, rowCount, "failcount")+"<br>("+ut.getColumnValue(rsData, rowCount, "pctfail")+")%</font> </td>"+
        	"<td  valign=top ><font face=\"verdana\" color=white size=2px>"+ut.getColumnValue(rsData, rowCount, "env")+"</font> </td>"+
        	"<td  valign=top ><font face=\"verdana\" color=white size=2px>"+ut.getColumnValue(rsData, rowCount, "browser")+"</font> </td></tr>"; 
    		rowCount++;
    	}
    
    	return(row+actionRow);
    	
    }
    private String getUnitTestExecutionData(){
    	String actionRow="\n";
    	String testid="";
    	String tmptestval="";
    	String tmpteststep="";
    	String stepdiv="";
    	int stepindex=1;
    	
    	int totaltestcount=0;
    	
    	
    	for(Object key: this.actionResult.keySet()){
    		String[] messages= PerformerBase.actionErrorMessage.get(key).split("<br><br>");    		
    		int actiontestcount=0;
    		int msgcount=0;
    		//for(String message:messages){
    		for(int c=0;c<messages.length;c++){
    			String message=messages[c];
    			String tmphtml="";
    			msgcount++;
	    		String [] results=message.split(">>");
	    		if((results.length>=2&& (testid.isEmpty()||!testid.equals(results[2]))) ){	    			
	    			testid=results[2];
	    			stepdiv="<div id=\"step"+msgcount+"-"+stepindex+">";
	    			tmptestval="<div>"+results[2]+"</div>";
	    			stepindex=1;
	    			totaltestcount++;
	    			actiontestcount++;
	    			
	    		} 
	    		
	    		if(results.length>=2){
		    		tmpteststep="<div id=\"step"+msgcount+"-"+stepindex+"><div><font face=\"verdana\" size=2px color=white>Step-"+stepindex+":"+results[results.length-2]+"</div>"+
	    					"<div><font face=\"verdana\" size=2px color=white><br>"
	    					+results[0] +" See Details:"+results[results.length-1]+"</font></div>";
	    		
    					
	    		tmphtml=
		    		"<tr bgcolor=\"#4AA02C\">"+
		        	"<td align=left ><font face=\"verdana\" size=2px color=white>"+(msgcount==1?key:"")+"</font></td>"+    	  	
		        	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+tmptestval+"</font> <br></td>"+
		        	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+tmpteststep+"</font> <br></td>"+  
		        	"<td  valign=top ><font face=\"verdana\" color=white size=2px>"+(msgcount==1? "@actiontestcount":"")+"</font> </td>"+  
		        	"<td  valign=top ><font face=\"verdana\" color=white size=2px>"+(msgcount==1?getActionTotalValidator(key):"")+"</font> </td>"+
		        	"<td  valign=top ><font face=\"verdana\" color=white size=2px>"+(msgcount==1?(PerformerBase.validatorOKCount.get(key)==null?0:PerformerBase.validatorOKCount.get(key)):"")+"</font> </td>"+
		        	"<td  valign=top ><font face=\"verdana\" color=white size=2px>"+(msgcount==1?(PerformerBase.validatorErrorCount.get(key)==null?0:PerformerBase.validatorErrorCount.get(key)):"")+"</font> </td>"+
		        	"<td  valign=top ><font face=\"verdana\" color=white size=2px>"+(msgcount==1?(PerformerBase.actionReposneTime.get(key)/PerformerBase.actionExecutionCount.get(key)):"")+"</font> </td></tr>"; 
	    		}else{
	    			totaltestcount++;
	    			
	    			tmphtml=
	    		    		"<tr bgcolor=\"#4AA02C\">"+
	    		        	"<td align=left ><font face=\"verdana\" size=2px color=white>"+(msgcount==1?key:"")+"</font></td>"+    	  	
	    		        	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+key+"</font> <br></td>"+
	    		        	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+PerformerBase.actionErrorMessage.get(key)+"</font> <br></td>"+   
	    		        	"<td  valign=top ><font face=\"verdana\" color=white size=2px>"+msgcount+"</font> </td>"+  
	    		        	"<td  valign=top ><font face=\"verdana\" color=white size=2px>"+msgcount+"</font> </td>"+
	    		        	"<td  valign=top ><font face=\"verdana\" color=white size=2px>"+(message.contains("PASSED")?1:0)+"</font> </td>"+
	    		        	"<td  valign=top ><font face=\"verdana\" color=white size=2px>"+(message.contains("PASSED")?0:1)+"</font> </td>"+
	    		        	"<td  valign=top ><font face=\"verdana\" color=white size=2px>"+(PerformerBase.actionReposneTime.get(key)/PerformerBase.actionExecutionCount.get(key))+"</font> </td></tr>"; 
	    			
	    		}
	    		actionRow=actionRow+tmphtml;
	    		//System.out.println(actionRow);
	        	if(results.length>=2 &&testid.equals(results[2])){
	        		tmptestval="";
	        		stepindex++;
	    		}
    		}
    		
    		actionRow=actionRow.replaceAll("@actiontestcount", String.valueOf(actiontestcount));
    	}
    	
    	String row="<tr bgcolor=\"#7D226D\">"+
    	    	"<td align=left ><font face=\"verdana\" size=2px color=white>"+this.testname+"</font></td>"+
    	    	"<td align=left ><font face=\"verdana\" size=2px color=white></font></td>"+  
    	    	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+(PerformerBase.responseERCount==0?"PASSED":"FAILED")+"</font> </td>"+    	
    	    	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+totaltestcount+"</font> </td>"+ 
    	    	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+getTotalValidator()+"</font> </td>"+
    	    	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+getTotalPassedValidator()+"<br>("+getTotalPctPassed()+"%)</font> </td>"+
    	    	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+getTotalFailedValidator()+"<br>("+getTotalPctFailed()+"%)</font> </td>"+
    	    	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+PerformerBase.avgTime+"</font> </td></tr>"; 
    	return(row+actionRow);
    	
    }
    
    private int getActionTotalValidator(Object key){
    	int total=(PerformerBase.validatorErrorCount.get(key)==null?0:PerformerBase.validatorErrorCount.get(key))+
    	(PerformerBase.validatorOKCount.get(key)==null?0:PerformerBase.validatorOKCount.get(key));
    	return (total);
    	
    }
    
    private double getTotalPctPassed(){
    	double pct=((double) getTotalPassedValidator()/(double) getTotalValidator())*100;
    	return(Math.round(pct));
    }
    
    private double getTotalPctFailed(){
    	double pct=( (double)getTotalFailedValidator()/(double)getTotalValidator())*100;
    	return(Math.round(pct));
    }
    private int getTotalValidator(){
    	int total=getTotalFailedValidator()+getTotalPassedValidator();    	
    	return(total==0?1:total);
    }
    
    private int getTotalPassedValidator(){
    	int total=0;
    	for(Object key: this.actionResult.keySet()){
    	 total=total+((PerformerBase.validatorOKCount.get(key)==null?0:PerformerBase.validatorOKCount.get(key)));
    	}
    	return(total);
    }
    
    private int getTotalFailedValidator(){
    	int total=0;
    	for(Object key: this.actionResult.keySet()){
    	 total=total+((PerformerBase.validatorErrorCount.get(key)==null?0:PerformerBase.validatorErrorCount.get(key)));
    	}
    	return(total);
    }
    
    private String getHeader(){
    	String header="<html><head><h2><center><font face=\"verdana\" size=4px color=\"black\"><b><u>Test Results</u></b></font></center></h2></head><body>"+
    	"<table border=0 width=60%><br><br><tr><TD><font face=\"verdana\" size=2px color=\"#1589FF\"><b>Summary Report</b> :</font> <font face=\"verdana\" size=2px color=\"#1589FF\">"+this.testname+"</font></TD>"+
    	"<TD><font face=\"verdana\" size=2px color=\"#1589FF\"><b>XML Name</b>&nbsp;:</font><font face=\"verdana\" size=2px color=\"#1589FF\">"+this.pagename+".xml</font> </TD></tr></table>"+
    	"<table width=\"100%\" border=\"1\" border-color=\"black\" halign=\"top\" >"+
    	"<tr bgcolor=\"#616D7E\" height=\"50\">"+
    	"<td  align=center width=\"10%\"><font face=\"verdana\" size=2px color=white><b>Test Case ID</b></font></td>" +    	
    	"<td align=center width=\"15%\"><font face=\"verdana\" size=2px color=white><b>Threads</b></font></td>"+
    	"<td align=center width=\"10%\"><font face=\"verdana\" size=2px color=white><b>Run Time (Sec)</b></font></td>"+
    	"<td align=center width=\"15%\"><font face=\"verdana\" size=2px color=white><b>Total Execution</b></font></td>"+
    	"<td align=center width=\"25%\"><font face=\"verdana\" size=2px color=white><b>OK Count</b></font></td>"+
    	"<td align=center width=\"25%\"><font face=\"verdana\" size=2px color=white><b>Error Count</b></font></td>"+
    	"<td align=center width=\"25%\"><font face=\"verdana\" size=2px color=white><b>Average Response(ms)</b></font></td></tr>";
    	
    	return(header);
    	
    }
    
    private String getGraphs(){
    	String graph="<tr bgcolor=\"#4AA02C\">"+
    	"<table width=\"100%\" ><tr><td><image src=\"execution.jpg\"></image></td></tr></table></tr>"+
    	"<tr bgcolor=\"#4AA02C\">"+
    	"<table width=\"100%\" ><tr><td><image src=\"response.jpg\"></image></td></tr></table></tr>"+
    	"<tr bgcolor=\"#4AA02C\">"+
    	"<table width=\"100%\" ><tr><td><image src=\"elaspedtime.jpg\"></image></td></tr></table></tr>"+
    	"<tr bgcolor=\"#4AA02C\">"+
    	"<table width=\"100%\" ><tr><td><image src=\"activeuser.jpg\"></image></td></tr></table></tr>";
    	
    	return(graph);
    }
    private String getExecutionData(){
    	String actionRow="\n";
    	String row="<tr bgcolor=\"#7D226D\">"+
    	"<td align=left ><font face=\"verdana\" size=2px color=white>"+this.testname+"</font></td>"+    	  	
    	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+this.newThreadCount+"</font> </td>"+
    	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+this.timeout+"</font> </td>"+
    	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+PerformerBase.executionCount+"</font> </td>"+    	
    	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+PerformerBase.responseOKCount+"</font> </td>"+
    	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+PerformerBase.responseERCount+"</font> </td>"+
    	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+PerformerBase.avgTime+"</font> </td></tr>"; 
    	for(Object key: this.actionResult.keySet()){
    		actionRow=actionRow+
    		"<tr bgcolor=\"#4AA02C\">"+
        	"<td align=left ><font face=\"verdana\" size=2px color=white>"+key+"</font></td>"+    	  	
        	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+this.threadCount+"</font> </td>"+
        	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+this.timeout+"</font> </td>"+
        	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+PerformerBase.actionExecutionCount.get(key)+"</font> </td>"+    	
        	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+PerformerBase.actionOKCount.get(key)+"</font> </td>"+
        	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+(PerformerBase.actionErrorCount.get(key)==null?0:PerformerBase.actionErrorCount.get(key))+"</font> </td>"+
        	"<td  align=left ><font face=\"verdana\" color=white size=2px>"+(PerformerBase.actionReposneTime.get(key)/PerformerBase.actionExecutionCount.get(key))+"</font> </td></tr>"; 
    	}
    
    	return(row+actionRow);
    	
    }
   
    private String getFooter(){
    	return("</table></body></html>");
    }
    
    public void invokeWs(URLConfig urlConf,String action){
    
    	String url=urlConf.getUrl();
    	
    	
    	if(!urlConf.getUrl().contains("http")&&ConfigLoader.getConfig("WEB_SERVICE_URL")!=null){
		    url=(ConfigLoader.getConfig("WEB_SERVICE_URL").replace("[env]", ConfigLoader.getConfig("QA_TEST_ENV")))
		    + urlConf.getUrl();
		    urlConf.setUrl(url);
        }
    	
    	// Set new URLConfig 
    	this.urlConfig=urlConf;    	
    	if(this.urlConfig.getUrlElement().attributeValue("method")!=null){
    		
    		this.request=this.getRequestObject(action);
            
            
	        //this.request= new ExecuteHttpRequest(this,action);
	        //verify if this is unit test
	        if(this.isUnitTest && this.action.indexOf("unit")>=0){
	        	
	        	if(this.urlConfig.getUrl().contains("https") 
		                && this.urlConfig.getUrlElement().attributeValue("method").equalsIgnoreCase("GET")){
	        		this.invokeUnitHttpsGET();
		        }else if(this.urlConfig.getUrl().contains("http") 
		                && this.urlConfig.getUrlElement().attributeValue("method").equalsIgnoreCase("GET")){
		        	this.invokeUnitHttpGET();
		        }else if(this.urlConfig.getUrl().contains("https") 
		                && this.urlConfig.getUrlElement().attributeValue("method").equalsIgnoreCase("POST")){
		        	this.invokeUnitHttpsPOST();
		        }else if(this.urlConfig.getUrl().contains("http") 
		                &&this.urlConfig.getUrlElement().attributeValue("method").equalsIgnoreCase("POST")){
		        	this.invokeUnitHttpPOST();
		        }
	        	
	        }
    	}
    	
    }
    
    private void invokeWsPerformance( PerformerBase  httpResuest) {
    	long starttime=System.currentTimeMillis();
        long timer=0;  
        int invoked=0;
        try { 
        	
        	while (timer<=this.timeout){
                this.executeWsPerformance(httpResuest);
                timer=(System.currentTimeMillis()-starttime)/1000;
                synchronized (draw){ 
                	invoked++;
                	executeGraph.ts.addOrUpdate(new Millisecond(), PerformerBase.executionCount);
                	responseGraph.ts.addOrUpdate(new Millisecond(), PerformerBase.avgOKTime); 
                	elaspedTimeGraph.ts.addOrUpdate(new Millisecond(), PerformerBase.elaspedTime);
                	activeThreadGraph.ts.addOrUpdate(new Millisecond(), PerformerBase.activeThread); 
                }
              }
        	
           synchronized (httpResuest){ 
            	if(httpResuest!=null){
        		
            	int totalInvoked=(this.actionResult.get(httpResuest.getActionName())==null?0:this.actionResult.get(httpResuest.getActionName()))+invoked;
        	
            	this.actionResult.put(httpResuest.getActionName(), totalInvoked);
            	threadExit=threadExit+1;
            	httpResuest.closeDriver();
            	WmLog.getCoreLogger().info("Exiting Thread="+threadExit +" >>> Total Thread Count="+newThreadCount +" >>> Action="+httpResuest.getActionName());
        		System.out.println("Exiting Thread="+threadExit +" >>> Total Thread Count="+newThreadCount+" >>> Action="+httpResuest.getActionName());
        	
        		
        		
            	}
            }
            
            WmLog.getCoreLogger().info("Time Elasped (sec) ="+timer +"(sec)");
            System.out.println("Time Elasped (sec)="+timer +"(sec)");
    		
            //Add graph for Execution Count           
            //if(this.threadCount-1== HttpThread.getThreadExit() && new File(ConfigLoader.getWmOutputReportPath()).mkdir()){
            if( threadExit==newThreadCount && new File(ConfigLoader.getWmOutputReportPath()).mkdir() ){
           
	            saveGraph(executeGraph,
	            		ConfigLoader.getWmOutputReportPath()+"execution.jpg",
	            		"Execution Graph",
	             	   "Time",
	             	   "Execution Count");
	            
	            //Add graph for Response Count
	            saveGraph(responseGraph,
	            		ConfigLoader.getWmOutputReportPath()+"response.jpg",
	            		"Response Graph",
	             	   "Time",
	             	   "Response Time (ms)");
	            
	          //Add graph for Elasped Count
	            saveGraph(elaspedTimeGraph,
	            		ConfigLoader.getWmOutputReportPath()+"elaspedtime.jpg",
	            		"Elasped Time Graph",
	             	   "Time",
	             	   "Elasped Time (ms)");
	            
	            
	          //Add graph for Elasped Count
	            saveGraph(activeThreadGraph,
	            		ConfigLoader.getWmOutputReportPath()+"activeuser.jpg",
	            		"Active User Graph",
	             	   "Time",
	             	   "Elasped Time (ms)");
	            
	            //reset Jenkins Signals
	            httpResuest.resetJenkinSignal();
	            
	            //set any additional custom data retrieved during performance test
	            this.perfSummaryReportData=httpResuest.getPerformanceSummaryReportData();
	            
	            createHtmlReport(this.action);
	            
	          
	            
           
            }           
           
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
	}
    
private void executeWsPerformance(PerformerBase httpRequest){
    	
    	this.request= httpRequest;
    	String action=httpRequest.action;
    	URLConfig urlConf=httpRequest.page.getPageURL().getUrlConfig(action);
        if(urlConf.getUrl().contains("https") 
                && urlConf.getUrlElement().attributeValue("method").equalsIgnoreCase("GET")){
            this.executeHttpsGET();
        }else if(urlConf.getUrl().contains("http") 
                && urlConf.getUrlElement().attributeValue("method").equalsIgnoreCase("GET")){
            this.executeHttpGET();
        }else if(urlConf.getUrl().contains("https") 
                && urlConf.getUrlElement().attributeValue("method").equalsIgnoreCase("POST")){
            this.executeHttpsPOST();
        }else if(urlConf.getUrl().contains("http") 
                &&urlConf.getUrlElement().attributeValue("method").equalsIgnoreCase("POST")){
            this.executeHttpPOST();
        }
    	
        
    }
   
	protected void invokeBrowser(String action, String driver){
		
		ActionHandler mproc= new ActionHandler(this,action); 
		
		synchronized (mproc){ 
			mproc.setBrowserDriver(driver);
		    if(this.isUnitTest && this.action.indexOf("unit")>=0){		    	
		    	this.invokeUnitTest(mproc);
		    	this.proc= mproc;;
		    }else{
	    		//Invoke API test		    	
	    		this.invokeAPI(mproc);
	    		this.proc= mproc;
		    }
		}
		
	}
    protected void invoke(String action){
    	if(!this.isUnitTest && this.urlConfig.getUrlElement().attributeValue("type")==null){
    		WmLog.printMessage("Please check your Test page action config attribute = type. Provide type=WEBSERVICE or API based on your test as a action attribute.");
    		System.exit(0);
    	}
    	if(!this.isUnitTest &&!this.urlConfig.getUrlElement().attributeValue("type").equalsIgnoreCase("API") &&urlConfig.getUrl()!=null){ 
    	
    		invokeWsPerformance(this.getRequestObject(action));	      
    	}else{
    		this.proc= new ActionHandler(this,action);  
    		
    	    if(this.isUnitTest && this.action.indexOf("unit")>=0){
    	    	
    	    	this.invokeUnitTest(proc);
    	    }else{
	    		//Invoke API test
    	    
	    		this.invokeAPI(this.proc);
    	    }
    	}
    }
  
   


	protected void setupPage(Element testdata, PageURL page){
		this.actionResult=new  HashMap<String, Integer>();
        this.pageUrl=page;
        this.testdata=testdata;
        this.globalExecution="";
        List attributes=testdata.attributes(); 
       
        for(Object seter: attributes.toArray()){
            Attribute el= (Attribute)seter;
            String attrName=el.getName();
            if (attrName.equalsIgnoreCase("threads")){  
                this.setThreadCount(Integer.valueOf(testdata.attributeValue(attrName)));
            }else if (attrName.equalsIgnoreCase("timeout")){  
                this.timeout=Integer.valueOf(testdata.attributeValue(attrName));
            }else if (attrName.equalsIgnoreCase("action")){ 
                this.action=testdata.attributeValue(attrName);
            }else if (attrName.equalsIgnoreCase("name")){ 
                this.testname=testdata.attributeValue(attrName);
            }else if (attrName.equalsIgnoreCase("browsers")){ 
                this.browsers=testdata.attributeValue(attrName).trim().toLowerCase();
                if(this.browsers==null||this.browsers.isEmpty()){
                	this.browsers=ConfigLoader.getConfig("SELENIUM_DRIVER").toLowerCase();
                	if(this.browsers==null||this.browsers.isEmpty()){
                		this.browsers="firefox";
                	}
                }
            }else if (attrName.equalsIgnoreCase("page")){ 
                this.pagename=testdata.attributeValue(attrName);
            }else if (attrName.equalsIgnoreCase("datasetextension")){ 
                this.datasetextension=testdata.attributeValue(attrName);
            }else if (attrName.equalsIgnoreCase("groupbythread")){ 
                this.groupbythread=testdata.attributeValue(attrName);
            }else if (attrName.equalsIgnoreCase("overrideattributes")){ 
                this.overrideattributes=testdata.attributeValue(attrName);
            }else if (attrName.equalsIgnoreCase("baseurl")){ 
                this.baseurl=testdata.attributeValue(attrName).trim();
                if(this.baseurl!=null &&!this.baseurl.isEmpty() && this.baseurl.contains("http")){
                	ConfigLoader.setConfigProperty("BASE_APPLICATION_URL", this.baseurl);
                	ConfigLoader.setConfigProperty("WEB_SERVICE_URL", this.baseurl);
                }
            }else if (attrName.equalsIgnoreCase("env")){ 
                String env=testdata.attributeValue(attrName).trim();
                if(env!=null &&!env.isEmpty() ){
                	ConfigLoader.setConfigProperty("QA_TEST_ENV", env);
                }
            }
               
        }
        //initialize request object and create an execution session before invoke() is called
        //this.request= new ExecuteHttpRequest(this); 
        //initialize urlConfig
        this.urlConfig=this.pageUrl.getUrlConfig(this.action);
        
    }
    
   protected PerformerBase getRequestObject(String action){
	   
	   PerformerBase httprequest=null;
	   String httpclient=this.urlConfig.getUrlElement().attributeValue("httpclient");
		if(httpclient==null ||httpclient.equals("")){
			httpclient="scm.wm.uri.ExecuteHttpRequest";			
		}
	
       try {
    	   synchronized (state){ 
    		   httprequest = (PerformerBase)Class.forName(httpclient).newInstance();
    		   httprequest.setup(this,action);
    	   }
          
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
       return(httprequest);
   }
    /*
     * Setup test data
     */
    public void setupTestData(Element testdata, PageURL page) {
        this.setupPage(testdata,page);      
        // add any custom setup if needed
        
    }

    
    /*
     * Execute test and create threads based on the configuration
     */
    protected void executeTest(BasePage page) {
    	/*
    	 if(this.isMultiBrowserTest()){
    		 this.executeMultiBrowserTest(page);
    	 }else{
    		 this.executeRegularTest(page);
    	 }
    	 */
    	 
    	 this.executeRegularTest(page);
           
    }
    
    private void executeRegularTest(BasePage page) {
    	
    	threadExit=0;
    	ExecutorService exec=null;
        executeGraph= new CreateGraph("Execution Graph","Time","Execution Count");
        responseGraph=new CreateGraph("Response Graph","Time","Response Time (ms)");
        elaspedTimeGraph=new CreateGraph("Elasped Time Graph","Time","Elasped Time (ms)");
        activeThreadGraph=new CreateGraph("Active User Graph","User Count","Elasped Time (ms)");
        if(page.action.equalsIgnoreCase("performance")){        	
        	exec=setSuitePerformanceExecutor(page);
        	
        }else if(page.action.equalsIgnoreCase("unit")){
        	this.isUnitTest=true;
        	setUnitTestSuiteExecutor();
      
        }else if(page.action.toLowerCase().indexOf("unit@")>=0){
        	this.isUnitTest=true;
        	setUnitTestExecutor();
        	
        }else{        	
        	exec=setUnitPerformanceExecutor(page);
        }
        try {
        	if(!this.isUnitTest){
        		exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);        		
        	}
            
        } catch (InterruptedException ex) {
           
        } 
           
    }
 
   
    public void executeBrowserTest(BasePage page, String driver) {
    	
    	threadExit=0;
    	ExecutorService exec=null;
        executeGraph= new CreateGraph("Execution Graph","Time","Execution Count");
        responseGraph=new CreateGraph("Response Graph","Time","Response Time (ms)");
        elaspedTimeGraph=new CreateGraph("Elasped Time Graph","Time","Elasped Time (ms)");
        activeThreadGraph=new CreateGraph("Active User Graph","User Count","Elasped Time (ms)");
         if(page.action.equalsIgnoreCase("unit")){
        	this.isUnitTest=true;
        	setUnitBrowserTestSuiteExecutor(driver);
      
        }else if(page.action.toLowerCase().indexOf("unit@")>=0){
        	this.isUnitTest=true;
        	setUnitBrowserTestExecutor(driver);
        	
        }
         
           
    }
    
    /*
     * Executor for Single Unit Test
     */
    private void setUnitBrowserTestExecutor(String driver){
    	//get action string by removing "unit@" prefix
    	 String actiontest=this.action.substring(this.action.indexOf("@")+1);
    	 String[] actions=actiontest.split(";");
    	 for(String action:actions){
    		 this.urlConfig=this.pageUrl.getUrlConfig(action);  
    		 this.invokeBrowser(action, driver);
    	 }
  		 //insert execution result for debug  		
  	
  		// if( new File(ConfigLoader.getWmOutputReportPath()).mkdir()){
  		 if(FileUtility.createDir(ConfigLoader.getWmOutputReportPath())){
  			createUnitTestHtmlReport(this.action);
  			
  			String report_flag=ConfigLoader.getConfig("SEND_TO_JBERT_REPORT");
  			if(report_flag!=null&&report_flag.equalsIgnoreCase("ON")){
  				insertJbertTestCaseResult();
  			}
  			//No need to create summary for individual test
  			insertSummaryResult();  	 		 
  	 		createUnitTestHtmlSummaryReport("Summary Report");
  			
  			//reset new actionResult for multi suite run
           	this.actionResult=new  HashMap<String, Integer>();
           	PerformerBase.executionCount=0;
           	PerformerBase.responseERCount=0;
  		 }
  		 //Close db Manager
  		 ActionHandler.closeDbManager();
      }
    
    /*
     * Executor for Unit Test Suite
     */
    private void setUnitBrowserTestSuiteExecutor(String driver){
   	 	/*Set testset=this.getPageURL().getUrlKeySet();
       	 for(Object actionName: testset){       		
       		 this.urlConfig=this.pageUrl.getUrlConfig(actionName.toString());  
       		 this.invoke(actionName.toString());       	
        }*/
    	
    	synchronized (globalExecution){ 
    		if(globalExecution==null&&globalExecution.isEmpty()){
		       	for(Object actionName: this.getPageURL().getGlobalActionList()){       		
		      		 this.urlConfig=this.pageUrl.getUrlConfig(actionName.toString());  
		      		 this.invokeBrowser(actionName.toString(),driver);       	
		       	}
		       	globalExecution="executed";
    		}
    	}
       	
       	for(Object actionName: this.getPageURL().getNonGlobalActionList()){       		
     		 this.urlConfig=this.pageUrl.getUrlConfig(actionName.toString());  
     		 this.invokeBrowser(actionName.toString(),driver);       	
       	}
       	
       	if( new File(ConfigLoader.getWmOutputReportPath()).mkdir()){
       		
       		createUnitTestHtmlReport(this.action);
       		//insert execution result
    		 insertSummaryResult();
    		 //send data to jbert
    		 String report_flag=ConfigLoader.getConfig("SEND_TO_JBERT_REPORT");
   			 if(report_flag!=null&&report_flag.equalsIgnoreCase("ON")){
    			 insertJbertTestCaseResult();
    		 }
    		 
    		createUnitTestHtmlSummaryReport("Summary Report");
    		
    		//reset new actionResult for multi suite run
           	this.actionResult=new  HashMap<String, Integer>();
           	PerformerBase.executionCount=0;
           	PerformerBase.responseERCount=0;
 		 }
       	
       	//close all db Connections
        ActionHandler.closeDbManager();
        
       	
   }
    /*
     * Executor for Single Unit Test
     */
    private void setUnitTestExecutor(){
    	//get action string by removing "unit@" prefix
    	 String actiontest=this.action.substring(this.action.indexOf("@")+1);
    	 String[] actions=actiontest.split(";");
    	 for(String action:actions){
    		 this.urlConfig=this.pageUrl.getUrlConfig(action);  
    		 this.invoke(action);
    	 }
  		 //insert execution result for debug  		
  	
  		// if( new File(ConfigLoader.getWmOutputReportPath()).mkdir()){
  		 if(FileUtility.createDir(ConfigLoader.getWmOutputReportPath())){
  			createUnitTestHtmlReport(this.action);
  			
  			 //send data to jbert
  			String report_flag=ConfigLoader.getConfig("SEND_TO_JBERT_REPORT");
  			
  			if(report_flag!=null&&report_flag.equalsIgnoreCase("ON")){
  				insertJbertTestCaseResult();
  			}
  			//No need to create summary for individual test
  			insertSummaryResult();  	 		 
  	 		createUnitTestHtmlSummaryReport("Summary Report");
  			
  			//reset new actionResult for multi suite run
           	this.actionResult=new  HashMap<String, Integer>();
           	PerformerBase.executionCount=0;
           	PerformerBase.responseERCount=0;
  		 }
  		 //Close db Manager
  		 ActionHandler.closeDbManager();
      }
    
    /*
     * Executor for Unit Test Suite
     */
    private void setUnitTestSuiteExecutor(){
   	 	
    	synchronized (globalExecution){ 
    		if(globalExecution.isEmpty()){
		       	for(Object actionName: this.getPageURL().getGlobalActionList()){       		
		      		 this.urlConfig=this.pageUrl.getUrlConfig(actionName.toString());  
		      		 this.invoke(actionName.toString());       	
		       	}
		       	this.globalExecution="executed";
    		}
    	}
       	
       	for(Object actionName: this.getPageURL().getNonGlobalActionList()){       		
     		 this.urlConfig=this.pageUrl.getUrlConfig(actionName.toString());  
     		 this.invoke(actionName.toString());       	
       	}
       	
       	if( new File(ConfigLoader.getWmOutputReportPath()).mkdir()){
       		
       		createUnitTestHtmlReport(this.action);
       		//insert execution result
    		 insertSummaryResult();
    		 //send data to jbert
    		 String report_flag=ConfigLoader.getConfig("SEND_TO_JBERT_REPORT");
   			
   			 if(report_flag!=null&&report_flag.equalsIgnoreCase("ON")){
   				insertJbertTestCaseResult();
   			 }
    		 
    		createUnitTestHtmlSummaryReport("Summary Report");
    		
    		//reset new actionResult for multi suite run
           	this.actionResult=new  HashMap<String, Integer>();
           	PerformerBase.executionCount=0;
           	PerformerBase.responseERCount=0;
 		 }
       	
       	//close all db Connections
        ActionHandler.closeDbManager();
        
       	
   }
    /*
     * Executor for Performance Suite
     */
    private ExecutorService setSuitePerformanceExecutor(BasePage page){
    	 String url="";
    	 Set testset=this.getPageURL().getUrlKeySet();
    	 this.newThreadCount=this.threadCount*testset.size();
         ExecutorService exec = Executors.newFixedThreadPool(this.newThreadCount);         
         for(int count=0;count<this.getThreadCount(); count++){
        	 if(count==0){
                 executeGraph.createChart(this.timeout);
                 responseGraph.createChart(this.timeout);
                 elaspedTimeGraph.createChart(this.timeout);
                 activeThreadGraph.createChart(this.timeout);
             }
        	 for(Object actionName: testset){
        		 this.action= actionName.toString();
        		 //System.out.println("action="+this.action);
        		 this.urlConfig=this.pageUrl.getUrlConfig(actionName.toString());  
             	 url=this.getURLConfig().getUrl();
             	if(url!=null && url.indexOf("http")<0){
         		    url=(ConfigLoader.getConfig("WEB_SERVICE_URL").replace("[env]", ConfigLoader.getConfig("QA_TEST_ENV")))
         		    + this.getURLConfig().getUrl();
         		    
         		    this.getURLConfig().setUrl(url);
         		    //System.out.println(url);
                 }
                
        		 exec.execute(new HttpThread(this,actionName.toString()));
        	 }
         }
         exec.shutdown();
        
         return(exec);
    	
    }
   
    /*
     * Executor for Performance Unit Test
     */
    private ExecutorService setUnitPerformanceExecutor(BasePage page){
    	//get action string by removing "unit@" prefix
    	 String action=this.action.substring(this.action.indexOf("@")+1);	
 		 
    	 this.newThreadCount=this.threadCount;
    	 ExecutorService exec = Executors.newFixedThreadPool(this.getThreadCount());
         for(int count=0;count<this.getThreadCount(); count++){
        	 this.urlConfig=this.pageUrl.getUrlConfig(action);  
             exec.execute(new HttpThread(page, action));
             if(count==0){
                 executeGraph.createChart(this.timeout);
                 responseGraph.createChart(this.timeout);
                 elaspedTimeGraph.createChart(this.timeout);
                 activeThreadGraph.createChart(this.timeout);
             }
         }
         exec.shutdown();
         return(exec);
    }
    
   
   
    
    public boolean isMultiBrowserTest(){    	
    	Set testset=this.getPageURL().getUrlKeySet();
    	if(this.browsers!=null && !this.browsers.isEmpty()){
    		String[] browserlist=this.browsers.split(";");
    		if(browserlist.length>=1){
	    		for(Object action:testset){
	    			String elmXml=this.getPageURL().getUrlConfig(action.toString()).getUrlElement().asXML();
	    			if(elmXml.contains("@taglib:")){
	    				return true;
	    			}
	    		}
    		}
    			
    	}
    	return false;
    }
    
    public Connection getDbConnection(){
    	if(this.dbConn==null){
    		
			this.dbConn=dutl.getDbConnection("automation");
			
			if (this.dbConn==null && TestEngine.suite!=null){
				WmLog.getCoreLogger().info("####### Trying to Connect Database using automation_local tag");
    			this.dbConn=dutl.getDbConnection(ConfigLoader.getLocalAutomationDatabaseEnv(),"automation_local");
			}
    		
    	}
    	return(this.dbConn);
    }
   
    /*
     *  Implement this method for custom invocation for executing the test
     */
    abstract public void execute();
    
    /*
     *  Implement this method for custom invocation of HTTP POST
     */
    abstract protected String executeHttpPOST();
    
    /*
     *  Implement this method for custom invocation of HTTPS POST
     */
    abstract protected String executeHttpsPOST();
    
    /*
     *  Implement this method for custom invocation of HTTP GET
     */
    abstract protected String executeHttpGET();
    
    /*
     *  Implement this method for custom invocation of HTTPS GET
     */
    abstract protected String executeHttpsGET();
    
    /*
     *  Implement this method for custom invocation of Database API
     */
    abstract protected void executeAPI(ActionHandler proc);
    
}

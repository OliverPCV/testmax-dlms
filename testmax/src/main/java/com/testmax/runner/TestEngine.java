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


import java.sql.Connection;

import com.testmax.framework.ConfigLoader;
import com.testmax.framework.WmLog;
import com.testmax.handler.ApiHandler;
import com.testmax.util.DbUtil;
import com.testmax.util.FileUtility;

public class TestEngine {
    
    private ConfigLoader config=null;
    public static TestSuite suite= null;
    
    public TestEngine() { 
    	
        this.config=new ConfigLoader();        
     }

    public void start(String[] args) {
        // TODO Auto-generated method stub
    	String loggerName="fileloader";
    	if(args.length==0){
    		WmLog.initCoreLogger(this.config.getWmOutputLogPath() + "fileloader_"+System.currentTimeMillis()+".log",loggerName); 
	    	
    	}else if(args.length>=12){
    		ExecutionEngin.setDynamicPath( args[12].trim());
			WmLog.initCoreLogger(this.config.getWmOutputLogPath() + "fileloader_"+System.currentTimeMillis()+".log",loggerName); 
	    	suite= new TestSuite(args);
		
		}else if(args.length>0 &&args.length<12){
			
			String argsmsg="name=<Test Suite Name>\n"+
						 "page=<Page Name in Test Module File>\n" +      
						 "browsers=firefox, chrome, IE or Safari\n"+
						 "env=< QA Test Env>\n"+
						 "overrideattributes= optional override attributes if any i.e <password=Spigit1234;adminuser=admin;adminpassword=hip2hop>\n"+
						 "groupbythread= optional groupby thread attribute i.e community column in your XL dataset for grouping all test cases against each community\n"+
						 "baseurl= Base url i.e http://www.[env].spigit.com, [env] will be replaced in url by env attribute you set\n"+
						 "action=unit if you want to run single or multiple actions , provide action names as \";\" separated\n"+
						 "threads=1 number of threads you want to run parallel if grouping is not used\n" +
						 "timeout=60 i.e this parameter is used only for performance test , give default=60\n"+
						 "\n Example for Command Line Attributes:\n"+
						 "SetupCommunityUser SetupCommunityUser firefox qe36api api_ password=Spigit1234;adminuser=admin;adminpassword=hip2hop community http://www.[env].spigit.com unit 1 60";
			
			WmLog.printMessage("Please check test suite configuration parameters you are passing is correct!");
			WmLog.printMessage("Below Parameters you need to set to run using TestSuite command arguments");
			WmLog.printMessage(argsmsg);			
			WmLog.printMessage("Running Test Using TestSuite configuration XML file!");
		}
        this.config.parseTestDataFile();
        this.config.parseModuleDataFile();
        this.config.parseGlobalDataFile();
        this.config.parseSqlLibDataFile();
        this.config.parseDbDataFile();
        this.config.parseTagLibDataFile();
        WmLog.closeLogger();
        // Execute all test
        this.run();
        // Wait 5 sec to finish all jobs
        try {
			Thread.sleep(Long.valueOf(ConfigLoader.getConfig("TEST_ENGIN_TIMEOUT")));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
        System.exit(0);
        
    }
    
    public void startReport(String[] args) {
        // TODO Auto-generated method stub
    	String loggerName="fileloader";
    	WmLog.initCoreLogger(this.config.getWmOutputLogPath() + "fileloader_"+System.currentTimeMillis()+".log",loggerName); 
    	if(args.length>=10){
			suite= new TestSuite(args);
		}else if(args.length>0 &&args.length<10){
			String argsmsg="name=<Test Suite Name>\n"+
						 "page=<Page Name in Test Module File>\n" +      
						 "browsers=firefox, chrome, IE or Safari\n"+
						 "env=< QA Test Env>\n"+
						 "overrideattributes= optional override attributes if any i.e <password=Spigit1234;adminuser=admin;adminpassword=hip2hop>\n"+
						 "groupbythread= optional groupby thread attribute i.e community column in your XL dataset for grouping all test cases against each community\n"+
						 "baseurl= Base url i.e http://www.[env].spigit.com, [env] will be replaced in url by env attribute you set\n"+
						 "action=unit if you want to run single or multiple actions , provide action names as \";\" separated\n"+
						 "threads=1 number of threads you want to run parallel if grouping is not used\n" +
						 "timeout=60 i.e this parameter is used only for performance test , give default=60\n"+
						 "\n Example for Command Line Attributes:\n"+
						 "SetupCommunityUser SetupCommunityUser firefox qe36api api_ password=Spigit1234;adminuser=admin;adminpassword=hip2hop community http://www.[env].spigit.com unit 1 60";
			
			WmLog.printMessage("Please check test suite configuration parameters you are passing is correct!");
			WmLog.printMessage("Below Parameters you need to set to run using TestSuite command arguments");
			WmLog.printMessage(argsmsg);			
			WmLog.printMessage("Running Test Using TestSuite configuration XML file!");
		}
       
        this.config.parseDbDataFile();
        this.runReport();
        WmLog.closeLogger();
        // Execute all test
	
        System.exit(0);
        
    }
    
    private void run(){
        ExecutionEngin exe=new ExecutionEngin(this.config);
        exe.execute();
    }
    
    private void runReport(){
    	ApiHandler handler= new ApiHandler();
		ConfigLoader config=new ConfigLoader(); 
		DbUtil dutil= new DbUtil();
		config.parseDbDataFile();
		Connection dbCon=dutil.getDbConnection(config.getDatabaseEnv("automation"),"automation");
		handler.setDbConnection(dbCon);
		 if(FileUtility.createDir(ConfigLoader.getWmOutputReportPath())){
			 handler.createUnitTestHtmlSummaryReport("Summary Report");
		 }
    }
   

}

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

package com.testmax.uri;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import com.testmax.framework.BasePage;
import com.testmax.framework.PerformerBase;
import com.testmax.framework.URLConfig;
import com.testmax.framework.WmLog;
import com.testmax.runner.TestEngine;
import com.testmax.util.DbUtil;
import com.testmax.util.JavaScriptUtl;
import com.testmax.util.PrintTime;


public class ExamplePerformance extends PerformerBase {
	
	
	 private volatile static int userId;
	 private static String isSignalProcessed="no";
	 private volatile static String runId;
	 private volatile static String eid;
	 private int localuser=-1;
	 private int users=-1;
	 private int launchTime=-1;
	 private int voteRate=-1;
	 
	 private int pollId=-1;
	 private int groupId=-1;
	 private int duration;
	 private String matrixId;
	 private boolean isUrlLoaded;
	 private String datasetextension;
	 private String host;
	 HashMap<String,String> dataExt;
	 DbUtil dutl= new DbUtil();
	 PrintTime pt= new PrintTime();
	 
	 /*
	  * Pass below attribute with comma separated from Jenkin using datasetextension input
	  * 
	  *  Ex: users=500;startinguser=1;voteRate=20;pollInterval=20
	  *  
	  *  For Monitor Which you can run from your Desktop pass : deletematrix is optional if you need to delete the matrix
	  *  
	  *  users=400;startinguser=375;voteRate=10;pollInterval=20;signal=Yes;deletematrix=Yes
	  *  
	  *  @users  : Total Numbers of concurrent users in the Test run including all Test suites
	  *  @voteRate : number of votes per sec
	  *  @signal : use this optional flag only from the test suite where you need to provide signal
	  *  @startinguser : starting user Id in this test run
	  */
	 public  ExamplePerformance(){
		 this.utl=new JavaScriptUtl();
		 this.host="localhost";
		if(TestEngine.suite!=null &&TestEngine.suite.getTestSuite()!=null){
			 datasetextension=TestEngine.suite.getDatasetExtension();
			 WmLog.printMessage("datasetextension: "+datasetextension);
			int tmpuserId=0;
			if(!utl.isEmptyValue(datasetextension)){
				dataExt=utl.getDatasetExt(datasetextension);
				if(!utl.isEmptyValue(dataExt.get("startinguser"))){
					tmpuserId=new Integer(dataExt.get("startinguser"));
				}
				if(userId <=0 ){
					userId=tmpuserId;
				}
				if(tmpuserId>0){
					
					if(!utl.isEmptyValue(dataExt.get("launchTime"))){
						launchTime=new Integer(dataExt.get("launchTime"));
					}
					if(!utl.isEmptyValue(dataExt.get("voteRate"))){
						voteRate=new Integer(dataExt.get("voteRate"));
					}
					if(!utl.isEmptyValue(dataExt.get("users"))){
						users=new Integer(dataExt.get("users"));
					}
					host=TestEngine.suite.getNodeName();
					
				}
				
			}
		}
		//set starting user Id per Instance for each thread once
		if(dataExt==null){
			dataExt=new HashMap<String,String>();
		}
		//initiate localuser
	
		if(localuser<0){
			
			userId++;
			this.localuser=userId;
			dataExt.put("user",String.valueOf(userId));
			WmLog.printMessage("***** Setting localuser="+localuser);
			
		}
		
	    this.isUrlLoaded=false;
	  
	 }  
    
    
	 public void setup(BasePage page, String action){
		
			 super.setup(page, action);
			 this.utl= new JavaScriptUtl();
			 this.webdriver=utl.initWebDriverDriver(this.browser);
			
	          if(utl.isEmptyValue(this.datasetextension)){
	        	  this.datasetextension=this.page.getDatasetExtension();
	        	  WmLog.printMessage("***** Verifying datasetextension in setup() method ="+datasetextension);
	        	  HashMap <String,String> data=utl.getDatasetExt(datasetextension);
	        	  this.dataExt.putAll(data);
	        	  for(String key: data.keySet()){
	        		  //this.dataExt.put(key, data.get(key));
	        		  if(key.equalsIgnoreCase("startinguser")){
	        			  int tmpuserId=new Integer(dataExt.get("startinguser"));
	    	        	  if(this.userId <tmpuserId){
	    	        		  this.userId=tmpuserId;
	    	        		  this.localuser=tmpuserId;
	    	        		  dataExt.put("user",String.valueOf(tmpuserId));
	    	        		  WmLog.printMessage("***** Setting localuser in Setup="+localuser);
	    	        	  }
	        		  }
	        		//verify if signal is processed
	        		  this.matrixId=dataExt.get("users")+"-"+dataExt.get("voteRate")+"-"+dataExt.get("pollInterval");
	        		  
	        		  synchronized(isSignalProcessed){
		    	          if(this.isSignalProcessed.equalsIgnoreCase("no") && key.equalsIgnoreCase("signal") && data.get(key).equalsIgnoreCase("yes")){
		    	        	  eid=utl.isEmptyValue(dataExt.get("eid"))?"": dataExt.get("eid");
		    	        	  runId= pt.getTimeInMiliFromMiniute(0);
		    	        	  String sql="update TABLE_JENKIN_SIGNAL set isReady='Yes',runId='"+runId+"',eid='"+eid+"'";
		    	        	  dutl.executeQuery(sql, "automation");
		    	        	  this.isSignalProcessed="yes";
		    	        	  WmLog.printMessage("********************** TEST RUN ID="+this.runId +"************************");
		    	        	  
		    	        	
		    	        	 //delete 
		    	        	  sql="delete from TABLE_PERFORMANCE_MATRIX where MatrixId='"+this.matrixId+"' and Browser='"+this.browser+ "' and SuiteId='"+this.page.getPageName()+"'";
		    	        	  dutl.executeQuery(sql, "automation");
		    	        	  WmLog.printMessage("***** Deleted SUITE records in TABLE_PERFORMANCE_MATRIX for MatrixId="+this.matrixId);
		    	        	  
		    	        	 
		    	        	 sql="INSERT INTO `automation`.`TABLE_PERFORMANCE_MATRIX` (`SuiteId`, `RunId`, `Browser`, `KeyValues`, `Description`, `AppName`, `MatrixId`, `CpuHigh`, `CPULow`, `DbCpuMax`, `MemoryMax`) VALUES ('"+
		    	        	 this.page.getPageName()+"', '"+this.runId+"', '"+this.browser+ "', '"+ this.datasetextension+"', ' Test Matrix="+this.datasetextension+" ', '"+this.page.getPageName()+"', '"+this.matrixId+
		    	        	 "', '0', '0', '0', '0')";
		    	        	 dutl.executeQuery(sql, "automation");
		    	        	 WmLog.printMessage("***** Inserted Matrix records in TABLE_PERFORMANCE_MATRIX for MatrixId="+this.matrixId);

		    	          }
		    	          
		    	          if( key.equalsIgnoreCase("deletematrix") && data.get(key).equalsIgnoreCase("yes")){
		    	        	  String sql="delete from TABLE_PERFORMANCE_RESULT where MatrixId='"+this.matrixId+"' and Browser='"+this.browser+"'";
		    	        	  dutl.executeQuery(sql, "automation");
		    	        	  WmLog.printMessage("***** Deleted Matrix records in TABLE_PERFORMANCE_RESULT for MatrixId="+this.matrixId);
		    	        	  
		    	        	//delete History
		    	        	  //sql="delete from TABLE_PERFORMANCE_HISTORY where MatrixId='"+this.matrixId+"'";
		    	        	  sql="delete from TABLE_PERFORMANCE_HISTORY where MatrixId='"+this.matrixId+"' and Browser='"+this.browser+"'";
		    	        	  dutl.executeQuery(sql, "automation");
		    	        	  WmLog.printMessage("***** Deleted HISTORY records in TABLE_PERFORMANCE_RESULT for MatrixId="+this.matrixId);
		    	        	  
		    	        	 
		    	          }else if( key.equalsIgnoreCase("deletematrix") && data.get(key).equalsIgnoreCase("all")){
		    	        	  String sql="delete from TABLE_PERFORMANCE_RESULT where Id>0";
		    	        	  dutl.executeQuery(sql, "automation");
		    	        	  WmLog.printMessage("***** Deleted Matrix records in TABLE_PERFORMANCE_RESULT for all MatrixId");
		    	        	 
		    	          }
		    	          
		    	          
		    	         
	        		  }
	        		  
	        		 
	        	  }
	        	  
	          }
	        
	        
	          //Test Suite Thread should wait until signal is received
	          if(TestEngine.suite!=null &&TestEngine.suite.getTestSuite()!=null){
	        	 if(utl.isEmptyValue(this.matrixId)){
	        		//verify if signal is processed
	        		  this.matrixId=dataExt.get("users")+"-"+dataExt.get("voteRate")+"-"+dataExt.get("pollInterval");
	        		  
	        	 }
	        	  while ( utl.isEmptyValue(eid)){
	        		  WmLog.printMessage("******Waiting For JENKIN Signal to start test ********");
	        		  String  sql= "select RunId,Eid from TABLE_JENKIN_SIGNAL where IsReady='Yes'";
	        		  ArrayList<ArrayList<Object>> rsData= dutl.getQueryResult(sql, "automation");
	        		  runId=dutl.getColumnValue(rsData, 1, "RunId");
	        		  eid=dutl.getColumnValue(rsData, 1, "Eid");
	        		  WmLog.printMessage("********************** TEST RUN ID="+this.runId +"************************");
	        		 
	        		  try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	  }
	          }
		 
    }
   
    public String handleHTTPPostUnit() throws Exception {
    	
    	return(null);
            
    }
    
    public String handleHTTPPostPerformance() throws Exception {
    	 
    	return(executePosting());
    
    }
    
    
    
  
    
    /*
     * Configuration Parameters
     * Mandatory parameters:  pageloadeach (yes/no) and body
     * Mandatory Parameter : trackerval. Ex: DONE!, COMPLETE! etc.
 	  Optional parameter :  waittorender (yes/no)
 	  Optional parameter : trackerId (If you print a message after page load complete within your java script then you may not use tracker Id)
 	  
 	 If you need to open the URL each time before you execute the java script 
 	 then pass <param name="pageloadeach">yes</param>
 	
 	 Use the body as parameters name for javascript which will be executed
 	if you pass  param other than body and pageloadeach these parameters will be replaced within the body text javascript
 	
     */
     private String executePosting() throws Exception {
     	
     
     	String resp=null;  
     	
         URLConfig urlConf=this.page.getPageURL().getUrlConfig(this.action);
      
         String trakerid=urlConf.getUrlParamValue("trackerid");
         
         //load your test page once
         if(!this.isUrlLoaded){
        	 
        	 Set <String> s=dataExt.keySet();
             for(String param:s){ 
            	 this.url=this.url.replaceAll("@"+param, dataExt.get(param));
             }
           //perform login if necessary
             if(!utl.isEmptyValue(eid)){
            	 this.url=this.url+"&eid="+eid;
             }
         	utl.loadUrl(this.url);
         	utl.waitToPageLoad();
         	if(TestEngine.suite==null&& utl.isEmptyValue(eid)){
         		eid=utl.extractEid();
         		if(!utl.isEmptyValue(eid)){
         			 String sql="update TABLE_JENKIN_SIGNAL set eid='"+eid+"'";
         			 dutl.executeQuery(sql, "automation");
         		}
         	}
         	this.isUrlLoaded=true;
         }
        
     	 resp=utl.retrieveOutput(trakerid) ;
         
     	 if(!utl.isEmptyValue(resp)){
     		 String[] polls=resp.split("USER-POLL:");
     		 for(String poll:polls){
     			 handleResponse("USER-POLL:"+poll,"TABLE_PERFORMANCE_RESULT");
     		 }
     	 }
       
         this.activeThread=this.getTotalActiveThread()-this.getTotalThreadExit();
       
         return(resp);
             
     }
   
    
     private String handleResponse(String resp, String table){
    	 boolean status=false;
    	 boolean isHistory=false;
    	 int pidx;
    	 if(table.contains("HISTORY")){
    		 isHistory=true;
    	 }
    	 if(!utl.isEmptyValue(resp) ){
    		
    		 if(this.pollId!=-1){
    			 int nextpol=this.pollId+1;
    			 if(isHistory){
    				 nextpol=this.pollId-1;
    			 }
    			 
    			 pidx=resp.indexOf("USER-POLL:"+nextpol);
    			 if(pidx>=0){
    				 resp="USER-POLL:"+(nextpol)+resp.substring(pidx);
    			 }
    		 }else{
    			 pidx=resp.indexOf("USER-POLL:");
    		 }
    		
    		 if(pidx>=0){
    			 String[] items=resp.split("GROUP");
    			 for(String poll: items){
    				 if(poll.contains("USER-POLL:")){
    					 String[] targets=poll.substring(pidx+10).split(",");
    					 if(targets.length>=2){
    						 //this.localuser=new Integer(targets[0]);
    						 this.pollId=new Integer(targets[1].trim());
    						 break;
    					 }
    				 }
    			 }
    		 }
    		 
    	 }
    	 if(!utl.isEmptyValue(resp) &&  this.pollId!=-1){
    		 
    		 if(this.groupId!=-1 &&!isHistory){
    			 int nextgroup=this.groupId+1;
    			 
    			 pidx=resp.indexOf("GROUP:"+nextgroup);
    			 
    			 if(pidx>=0){
    				 resp="GROUP:"+(nextgroup)+resp.substring(pidx);
    			 }
    			
    		 }else{
    			 pidx=resp.indexOf("GROUP:");
    		 }
    		 if(pidx>=0){
    			 String[] items=resp.split("GROUP:");
    			 for(String item: items){
    				 String group=item;
    				 if(item.contains("USER-POLL:")){
    					 if(item.split("USER-POLL:").length>0){
    						 group=item.split("USER-POLL:")[0];
    					 }
    				 }
    				 if(!group.isEmpty()&&!group.contains("USER-POLL:")){
    					 String[] targets=group.split(",");
    					  if(targets.length==9){
    						 this.groupId=new Integer(targets[0].trim());
    						 if(utl.isEmptyValue(runId)){
    							 runId=pt.getTimeInMiliFromMiniute(0);;
    						 }
    						 String QuiteTime=targets[1].trim();
    						 String StartTime= targets[2].trim();
    						 String EndTime=targets[3].trim();
    						 String ElaspedTimeMili=targets[4].trim();
    						 String ExecutionCount=targets[5].trim();  // Number of ajax call
    						 String IdeaCount=targets[6].trim();
    						 String CommentCount=targets[7].trim();
    						 String VoteCount=targets[8].trim();
    						 
    						 String pref_sql=" INSERT INTO `automation`.`"+table+"` ( `RunId`, `Host`, `PoleId`, `GroupId`, "+
    						 " `ThreadId`, `MatrixId`,`Browser`, `StartTime`, `EndTime`, `QuiteTime`, `ExecutionCount`, `AvgRespTime`, `TestDuration`,"+
    								 " `KeyValues`, `RunDate`, `ElaspedTimeMili`, `IdeaCount`, `CommentCount`,`VoteCount`,`CurrentStatus`) VALUES"+
    						        "( '"+runId+"', '"+host+"', '"+this.pollId+"', '"+this.groupId+"', '"+this.localuser+"', '"+this.matrixId+"', '"+this.browser+"','"+
    						        StartTime+"', '"+EndTime+"', '"+QuiteTime+"',"+ExecutionCount+","+ this.avgTime+","+this.page.getTimeOut()*1000+
    						        ",' ',now(),'"+ElaspedTimeMili+"',"+IdeaCount+","+CommentCount+","+VoteCount+",'RUNNING')";
    						      
    						 status=dutl.executeQuery(pref_sql, "automation");
    						 if(status){
    						         int statusCode=200;
    						         
	    						     if(!isHistory){
	    						    	 this.stopMethodRecording(statusCode,Long.valueOf(ElaspedTimeMili),0);
	    						         System.out.println(resp);
	    						         System.out.println("ElaspedTime: "+ElaspedTimeMili);
	    						         WmLog.printMessage("Response XML: "+resp);
	    						         WmLog.printMessage("ElaspedTime: "+ElaspedTimeMili);
	    						         this.printMethodRecording(statusCode,this.url);
	    						         this.printMethodLog(statusCode,this.url);
	    						         
	    						        if(statusCode==200 ){
	    						         	this.addThreadActionMonitor(this.action, true, Long.valueOf(ElaspedTimeMili));
	    						       
	    						     	}else{
	    						     		this.addThreadActionMonitor(this.action, false, Long.valueOf(ElaspedTimeMili));
							     			WmLog.printMessage("Input URL: "+this.url);
							     			WmLog.printMessage("Response XML: "+resp);
	    						     		 
	    						     	}
	    						     }
    						        
    							 
    						 }
    					 }
    				 }
    			 }
    		 }
    		 
    	 }
    	 return resp;
     }
    

	@Override
	protected String handleHTTPGetUnit() throws Exception {
		String resp=null;
        HttpGet httpget = new HttpGet(this.url);
        //set the time for this HTTP resuest
        this.startRecording();
        long starttime=this.getCurrentTime();
        HttpResponse response = this.httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        // Measure the time passed between response
        long elaspedTime= this.getElaspedTime(starttime);
        this.stopRecording(response,elaspedTime);
        resp=this.getResponseBodyAsString(entity);        
        this.printRecording();
        this.printLog(); 
        //System.out.println(this.urlResponse);
        this.closeEntity(entity);
        return(resp);
	}


	@Override
	protected String handleHTTPGetPerformance() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}   

	
	
	/*
     *  Overridden  method in your Performance Adaptor implementation
     */
    protected String getPerformanceSummaryReportData(){
    	String summary="";
    	ArrayList<ArrayList<Object>> rsData=null;
    	String userscount="";
    	String total_idea_count="";
    	String total_vote_count="";
    	String total_comment_count="";
    	String elaspedTimeMax="";
    	String elaspedTimeMin="";
    	String cpuHigh="";
    	String cpuLow="";
    	String dbCpuMax="";
    	String memoryMax="";
    	
    	//User Count
    	String user_count_sql="select count(distinct ThreadId) as userscount from TABLE_PERFORMANCE_RESULT where RunId='"+this.runId+"'";
    	rsData= dutl.getQueryResult(user_count_sql, "automation");
    	if(rsData!=null && rsData.size()>0){
    		userscount=dutl.getColumnValue(rsData, 1, "userscount");
    	}
      //matrix , cpu and memory
    	rsData=null;
    	String sql="select * from TABLE_PERFORMANCE_MATRIX where RunId='"+this.runId+"'";
    	rsData= dutl.getQueryResult(sql, "automation");
    	if(rsData!=null && rsData.size()>0){
    		cpuHigh=dutl.getColumnValue(rsData, 1, "CpuHigh");
    		cpuLow=dutl.getColumnValue(rsData, 1, "CpuLow");
    		dbCpuMax=dutl.getColumnValue(rsData, 1, "DbCpuMax");
    		memoryMax=dutl.getColumnValue(rsData, 1, "MemoryMax");
    	}
    	
    	
    	//performance result
    	rsData=null;
    	String idea_count_sql="select sum(IdeaCount) as IdeaCount, sum(VoteCount) as VoteCount ,sum(CommentCount) as CommentCount,"+  
    	" max(ElaspedTimeMili) as ElaspedMax, min(ElaspedTimeMili) as ElaspedMin " +
    	" from TABLE_PERFORMANCE_RESULT where RunId='"+this.runId+"' and CurrentStatus='RUNNING'";
    	rsData= dutl.getQueryResult(idea_count_sql, "automation");
    	if(rsData!=null && rsData.size()>0){
	    	total_idea_count=dutl.getColumnValue(rsData, 1, "IdeaCount");
	    	total_vote_count=dutl.getColumnValue(rsData, 1, "VoteCount");
	    	total_comment_count=dutl.getColumnValue(rsData, 1, "CommentCount");
	    	elaspedTimeMax=dutl.getColumnValue(rsData, 1, "ElaspedMax");
	    	elaspedTimeMin=dutl.getColumnValue(rsData, 1, "ElaspedMin");
    	}
        summary=
        		"<tr bgcolor=\"#4AA02C\">"+
						"<table width=\"100%\">"+
						"<tr>"+
							"<td align=right width=\"30%\"><font face=\"verdana\" size=2px" +
							"color=white><b>Run ID:</b></font></td>"+
							"<td align=center width=\"10%\"><font face=\"verdana\" size=2px" +
							"color=white><b>"+runId+"</b></font></td>"+
						"</tr>"+
						"<tr>"+
							"<td align=right width=\"30%\"><font face=\"verdana\" size=2px" +
							"color=white><b>Total Users:</b></font></td>"+
							"<td align=center width=\"10%\"><font face=\"verdana\" size=2px" +
							"color=white><b>"+userscount+"</b></font></td>"+
						"</tr>"+
						"<tr>"+
							"<td align=right width=\"30%\"><font face=\"verdana\" size=2px" +
							"color=white><b>Duration (sec):</b></font></td>"+
							"<td align=center width=\"10%\"><font face=\"verdana\" size=2px" +
							"color=white><b>"+this.page.getTimeOut()+"</b></font></td>"+
						"</tr>"+
						"<tr>"+
							"<td align=right width=\"30%\"><font face=\"verdana\" size=2px" +
							"color=white><b>Total Ideas:</b></font></td>"+
							"<td align=center width=\"10%\"><font face=\"verdana\" size=2px" +
							"color=white><b>"+total_idea_count+"</b></font></td>"+
					    "</tr>"+
					    "<tr>"+
							"<td align=right width=\"30%\"><font face=\"verdana\" size=2px" +
							"color=white><b>Total Votes:</b></font></td>"+
							"<td align=center width=\"10%\"><font face=\"verdana\" size=2px" +
							"color=white><b>"+total_vote_count+"</b></font></td>"+
				        "</tr>"+
				        "<tr>"+
							"<td align=right width=\"30%\"><font face=\"verdana\" size=2px" +
							"color=white><b>Total Comments:</b></font></td>"+
							"<td align=center width=\"10%\"><font face=\"verdana\" size=2px" +
							"color=white><b>"+total_comment_count+"</b></font></td>"+
			            "</tr>"+
			            "<tr>"+
							"<td align=right width=\"30%\"><font face=\"verdana\" size=2px" +
							"color=white><b>Elasped Time (Max):</b></font></td>"+
							"<td align=center width=\"10%\"><font face=\"verdana\" size=2px" +
							"color=white><b>"+elaspedTimeMax+"</b></font></td>"+
						"</tr>"+
						 "<tr>"+
							"<td align=right width=\"30%\"><font face=\"verdana\" size=2px" +
							"color=white><b>Elasped Time (Min):</b></font></td>"+
							"<td align=center width=\"10%\"><font face=\"verdana\" size=2px" +
							"color=white><b>"+elaspedTimeMin+"</b></font></td>"+
						"</tr>"+
						"<tr>"+
						"<td align=right width=\"30%\"><font face=\"verdana\" size=2px" +
						"color=white><b>Server CPU High(%):</b></font></td>"+
						"<td align=center width=\"10%\"><font face=\"verdana\" size=2px" +
						"color=white><b>"+cpuHigh+"</b></font></td>"+
				    "</tr>"+
				    "<tr>"+
						"<td align=right width=\"30%\"><font face=\"verdana\" size=2px" +
						"color=white><b>Server CPU Low (%):</b></font></td>"+
						"<td align=center width=\"10%\"><font face=\"verdana\" size=2px" +
						"color=white><b>"+cpuLow+"</b></font></td>"+
			        "</tr>"+
			        "<tr>"+
						"<td align=right width=\"30%\"><font face=\"verdana\" size=2px" +
						"color=white><b>Database Server CPU Max(%):</b></font></td>"+
						"<td align=center width=\"10%\"><font face=\"verdana\" size=2px" +
						"color=white><b>"+dbCpuMax+"</b></font></td>"+
		            "</tr>"+
		            "<tr>"+
						"<td align=right width=\"30%\"><font face=\"verdana\" size=2px" +
						"color=white><b>Server Memory Max (%):</b></font></td>"+
						"<td align=center width=\"10%\"><font face=\"verdana\" size=2px" +
						"color=white><b>"+memoryMax+"</b></font></td>"+
					"</tr>"+
					 "<tr>"+
						"<td align=right width=\"30%\"><font face=\"verdana\" size=2px" +
						"color=white><b>Test Matrix:</b></font></td>"+
						"<td align=center width=\"10%\"><font face=\"verdana\" size=2px" +
						"color=white><b>"+this.matrixId+"</b></font></td>"+
					"</tr>"+
					"</table>"+
				"</tr>";
    	return summary;
    }
    
    @Override
	 protected void resetJenkinSignal(){
		String sql="update TABLE_JENKIN_SIGNAL set isReady='No', eid=''";
 	  	dutl.executeQuery(sql, "automation");
		
	}
    
   
	 protected int getTotalActiveThread(){
		ArrayList<ArrayList<Object>> rsData=null;
		String sql="select count(distinct ThreadId) as userscount from TABLE_PERFORMANCE_RESULT where RunId='"+this.runId+"'";
		rsData= dutl.getQueryResult(sql, "automation");
    	if(rsData!=null && rsData.size()>0){
    		
    		return(Integer.parseInt( dutl.getColumnValue(rsData, 1, "userscount")));
    	}
    	return 0;
		
	}
    
	 protected int getTotalThreadExit(){
			ArrayList<ArrayList<Object>> rsData=null;
			String sql="select count(distinct ThreadId) as userscount from TABLE_PERFORMANCE_RESULT where RunId='"+this.runId+"' and CurrentStatus='EXIT'";
			rsData= dutl.getQueryResult(sql, "automation");
	    	if(rsData!=null && rsData.size()>0){
	    		
	    		return(Integer.parseInt( dutl.getColumnValue(rsData, 1, "userscount")));
	    	}
	    	return 0;
			
		}
	 @Override
	protected void teardown(){
		 URLConfig urlConf=this.page.getPageURL().getUrlConfig(this.action);
		 String historyid=urlConf.getUrlParamValue("historyid");
		 String trakerid=urlConf.getUrlParamValue("trackerid");
		 
		 //print console
		 //utl.afterMethod();
		 
		 threadExit();
		 try{
			 String resp=utl.retrieveOutput(trakerid) ;
			 String history=utl.retrieveOutput(historyid) ;
			 this.printMessage("\nHISTORY-RESP :\n"+resp+"\nHISTORY:\n"+history);
			 this.pollId=-1;
			 this.groupId=-1;
			
			 if(!utl.isEmptyValue(resp)){
	     		 String[] polls=resp.split("USER-POLL:");
	     		 for(String poll:polls){
	     			 if(!utl.isEmptyValue(poll)){
	     				 this.printMessage("\n Total Row="+polls.length+"\nINSERTING  Data: "+"USER-POLL:"+poll);
	     				 handleResponse("USER-POLL:"+poll,"TABLE_PERFORMANCE_HISTORY");
	     			 }
	     		 }
	     	 }

			 if(!utl.isEmptyValue(history)){
	     		 String[] polls=history.split("USER-POLL:");
	     		 for(String poll:polls){
	     			 if(!utl.isEmptyValue(poll)){
	     				 this.printMessage("\n Total Row="+polls.length+"\nINSERTING  Data: "+"USER-POLL:"+poll);
	     				 handleResponse("USER-POLL:"+poll,"TABLE_PERFORMANCE_HISTORY");
	     			 }
	     		 }
	     	 }
			 
	        
		 }catch(Exception e){
			 this.printMessage("FAILED : to insert History"+ e.getMessage());
			 e.getStackTrace();
		 }
		
		
	} 
    private void threadExit(){
    	 String sql="";
    	try{
	    	//insert a record 
    		 this.groupId++;
	    	  sql=" INSERT INTO `automation`.`TABLE_PERFORMANCE_RESULT` ( `RunId`, `Host`, `PoleId`, `GroupId`, "+
					 " `ThreadId`, `MatrixId`,`Browser`, `StartTime`, `EndTime`, `QuiteTime`, `ExecutionCount`, `AvgRespTime`, `TestDuration`,"+
							 " `KeyValues`, `RunDate`, `ElaspedTimeMili`, `IdeaCount`, `CommentCount`,`VoteCount`,`CurrentStatus`) VALUES"+
					        "( '"+runId+"', '"+host+"', '"+this.pollId+"', '"+this.groupId+"', '"+this.localuser+"', '"+this.matrixId+"','"+this.browser+"', ' ', ' ', ' ',0,"+ this.avgTime+","+this.page.getTimeOut()*1000+
					        ",' ',now(),'0',0,0,0,'EXIT')";
					      
			boolean status=dutl.executeQuery(sql, "automation");
    	 }catch(Exception e){
			 this.printMessage("FAILED : to insert Thread EXIT :"+ sql+" \n" + e.getMessage());
			 e.getStackTrace();
		 }
    }
	
    
}

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

import org.dom4j.Element;

import com.testmax.framework.WmLog;
import com.testmax.util.ItemUtility;

public class TestSuite {
	
	private String name;
	private String jobname;
	private String buildno;
	private String buildid;
	private String jenkinsurl;
	private String workspace;
	private String env;
	private String browser;
	private String page;
	private String nodename;
	private String datasetextension;
	

	private Element testsuite;

	public TestSuite(String[] args) {
		WmLog.printMessage("Number Of Arguments Passed to Test Suite ="+args.length);
		WmLog.printMessage("Arguments:\n");
		for(String arg:args){
			WmLog.printMessage(arg);
		}
		ItemUtility iu=new ItemUtility();
		testsuite=iu.getRootElementFromXML("<Test></Test>");
		if(args[0]!=null){
			testsuite.addAttribute("name", args[0].trim());
			this.name= args[0];
			
		}
		if(args[1]!=null){
			testsuite.addAttribute("page", args[1].trim());
			this.page=args[1].trim();
			if(this.name==null||this.name.contains("$")){
				this.name=this.page;
				testsuite.addAttribute("name",args[1].trim());
			}
			
		}
		if(args[2]!=null){
			testsuite.addAttribute("browsers", args[2].trim());
			browser=args[2].trim();
		}
		if(args[3]!=null){
			testsuite.addAttribute("env", args[3].trim());
			this.env= args[3].trim();
		}
		if(args[4]!=null){
			testsuite.addAttribute("datasetextension", args[4].trim());
			this.datasetextension= args[4].trim();
			
		}
		if(args[5]!=null){
			testsuite.addAttribute("overrideattributes", args[5].trim().replaceAll(":", "="));
			
		}
		if(args[6]!=null){
			testsuite.addAttribute("groupbythread", args[6].trim());
			
		}
		if(args[7]!=null){
			testsuite.addAttribute("baseurl", args[7].trim());
			
		}
		if(args[8]!=null){
			testsuite.addAttribute("action", args[8].trim());
			
		}
		if(args[9]!=null){
			testsuite.addAttribute("threads", args[9].trim());
			
		}
		if(args[10]!=null){
			testsuite.addAttribute("timeout", args[10].trim());
			
		}
		if(args[11]!=null){
			this.buildno=args[11].trim();
			testsuite.addAttribute("buildno", args[11].trim());
			
		}
		if(args[12]!=null){
			this.buildid=args[12].trim();
			testsuite.addAttribute("buildid", args[12].trim());
			
		}
		if(args[13]!=null){
			this.jenkinsurl=args[13].trim();
			testsuite.addAttribute("jenkinsurl", args[13].trim());
			
		}
		if(args[14]!=null){
			this.jobname=args[14].trim();
			testsuite.addAttribute("jobname", args[14].trim());
			
		}
		if(args[15]!=null){
			this.workspace=args[15].trim();
			testsuite.addAttribute("workspace", args[15].trim());
			
		}
		if(args[16]!=null){
			this.nodename=args[16].trim();
			testsuite.addAttribute("nodename", args[16].trim());
			
		}
		
			
	
		// TODO Auto-generated constructor stub
	}
	public String getPage() {
		return page;
	}
	
	public String getBrowser() {
		return browser;
	}

	public String getEnv() {
		return env;
	}

	public String getWorkspace() {
		return workspace;
	}

	public String getJobname() {
		return jobname;
	}

	public String getBuildno() {
		return buildno;
	}

	public String getBuildid() {
		return buildid;
	}

	public String getJenkinsurl() {
		return jenkinsurl;
	}
	public String getNodeName() {
		return nodename;
	}
	public String getDatasetExtension() {
		return datasetextension;
	}

	

	public Element getTestSuite(){
		WmLog.printMessage(this.testsuite.asXML());
		return this.testsuite;
	}
	
	public String getName(){
		return this.name;
	}
	
	

}

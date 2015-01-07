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
import java.util.List;
import java.util.Set;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import com.testmax.framework.PerformerBase;
import com.testmax.framework.URLConfig;
import com.testmax.framework.WmLog;


public class HttpRestWebservice extends PerformerBase {

	
	public HttpRestWebservice(){}
	
	

    public String handleHTTPPostUnit() throws Exception {
    	String resp=null;  
    		
    	String replacedParam="";
    	System.out.println(this.url);
        HttpPost httpost = new HttpPost(this.url);
        //System.out.println(this.url);
        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        URLConfig urlConf=this.page.getURLConfig();
        
        Set <String> s=urlConf.getUrlParamset();
        for(String param:s){ 
        	replacedParam=urlConf.getUrlParamValue(param);
        	System.out.println(urlConf.getUrlParamValue(param));        	
        	nvps.add(new BasicNameValuePair(param,urlConf.getUrlParamValue(param)));
        }
        
        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        //set the time for this HTTP resuest
        this.startRecording();
        long starttime=this.getCurrentTime();
        HttpResponse response = this.httpsclient.execute(httpost);
        HttpEntity entity = response.getEntity();
        // Measure the time passed between response
        long elaspedTime= this.getElaspedTime(starttime);
        this.stopRecording(response,elaspedTime);
        resp=this.getResponseBodyAsString(entity); 
        System.out.println(resp);
        System.out.println("ElaspedTime: "+elaspedTime);
        WmLog.getCoreLogger().info("Response XML: "+resp);
        WmLog.getCoreLogger().info("ElaspedTime: "+elaspedTime);
        this.printRecording();
        this.printLog();
        if(this.getResponseStatus()==200 &&validateAssert(urlConf,resp)){
        	this.addThreadActionMonitor(this.action, true, elaspedTime);
       }else{
    		this.addThreadActionMonitor(this.action, false, elaspedTime);
    		WmLog.getCoreLogger().info("Input XML: "+replacedParam);
    		WmLog.getCoreLogger().info("Response XML: "+resp);
    	}
        this.closeEntity(entity);
        return(resp);
            
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



		@Override
		protected String handleHTTPPostPerformance() throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
		
		 
}



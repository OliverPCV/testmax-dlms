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

import java.io.StringReader;
import java.sql.Connection;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import com.testmax.framework.WmLog;
import com.testmax.util.FileUtility;

public abstract class ServiceHandler {
	
	
	private Document serviceInputdoc=null;
	
	private Document serviceResponseDoc=null;
	
	private Element serviceElm=null;
	
	private String path=null;
	
	protected FileUtility fu=new FileUtility() ;
	
	protected ActionHandler proc= null;
	
	/*
	 * This method implement the handler and 
	 * validate the response from the web service
	 */
	public abstract void handle(Connection con);

	
	protected Document getDocument(String xml){
	    
	     Document doc=null;
	 
	     try{
	         doc=new SAXReader().read( new StringReader(xml));	         
	         
	     }catch(DocumentException e){
	         WmLog.getCoreLogger().info(">>>Exception:<<<"+this.getClass().getName()+">>> Failed in reading XML Input data to the Webservice ");
	         System.out.println(">>>Exception:<<<"+this.getClass().getName()+">>> Failed in reading XML Input data to the Webservice. \n"+xml);          
	     } 
	     
	     return doc;
	 }
	
	public void setOutputFilePath(String path){
		
		this.path=path;
	}
	
    protected String getOutputFilePath(){
		
		return(this.path);
	}
    public void setExecuteProc(ActionHandler proc){		
		this.proc=proc;
	}
    
    public void setServiceElement(Element elm){		
		this.serviceElm=elm;
	}

	public void setServiceInputDoc(String wsInputXml){
		
		this.serviceInputdoc=this.getDocument(wsInputXml);
		fu.createTextFile(path+"_input.xml",wsInputXml);
	}
	 
	public void setServiceResponseDoc(String wsResponseXml){
		
		this.serviceResponseDoc=this.getDocument(wsResponseXml);
		fu.createTextFile(path+"_response.xml",wsResponseXml);
	}
	protected Document getServiceInputDoc(){
		
		return(this.serviceInputdoc);
	}
	
	protected Document getServiceResponseDoc(){
		
		return(this.serviceResponseDoc);
	}
	
	protected Element getServiceElement(){
		
		return(this.serviceElm);
	}
	
	
}

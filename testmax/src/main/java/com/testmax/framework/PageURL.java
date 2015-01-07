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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.dom4j.Element;

public class PageURL {

    /*
     * stores all config attributes for a given URL
     */
    private HashMap<String, URLConfig> urlstote=null;
    
    private List <String> globalList=null;
    
    private List <String> nonGlobalList=null;
    /*
     * Constructor
     */
    
    public PageURL(){
        this.urlstote=new HashMap<String, URLConfig>();
        this.globalList=new ArrayList<String>();
        this.nonGlobalList=new ArrayList<String>();
    }
    
   /*
    * Add all associated URL to the page
    * @param key -Name of the GET or POST action in this page
    * @param config -config object in name value pair
    */
    public void addUrlConfig(String actionName,URLConfig config){
        this.urlstote.put(actionName, config);
    }
    /*
     * Retrieve any URL associated to this page against its key
     */
    public URLConfig getUrlConfig(String actionName){
        return (URLConfig) (this.urlstote.get(actionName));
    }
    
    /*
     * Retrieve key set for all test case elements under Page URL
     */
    public Set getUrlKeySet(){
        return (Set) (this.urlstote.keySet());
    }
    
    public void addActionList(Element element){
    	if(element.getName().equals("action")){
	    	if(element.attributeValue("setglobal")!=null && element.attributeValue("setglobal").equalsIgnoreCase("yes")){
	    		this.globalList.add(element.attributeValue("name"));
	    	}else{
	    		this.nonGlobalList.add(element.attributeValue("name"));
	    	}
    	}
    }
    
    /*
     * Returns List of action which sets some global variables
     */
    public List getGlobalActionList(){
    	return (this.globalList);
    }
    
    /*
     * Returns List of action which does not set any global variables
     */
    public List getNonGlobalActionList(){
    	return (this.nonGlobalList);
    }
   
}

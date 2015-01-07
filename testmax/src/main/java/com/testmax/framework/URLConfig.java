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
import java.util.Random;
import java.util.Set;

import org.dom4j.Element;

/*
 * This class is used for each URL configuration
 */


public class URLConfig {
    /*
     * This variable stores all parameters for this URL config
     */
    private HashMap <String, String> paramlist=null;
    
    private HashMap <String, String> newParamlist=null;
    
    /*
     * This variable stores all verification needed after the page is retrieved
     */
    private HashMap <String, Element> assertion=null;
    
    private List<Element>  itemListElm=null;
   
    private HashMap <String, String> itemlist=null;
    
    private String  url=null;
        
    private Element urlElement=null;
    /*
     * Constructor
     */
    
    public URLConfig(Element element){        
        this.paramlist=new HashMap<String, String>();
        this.assertion=new HashMap<String, Element>(); 
        this.itemListElm=new ArrayList<Element>();        
        this.urlElement=element;
        setUpElement(element);
    }
    
    private void setUpElement(Element element){
    	
    	if(element.attributeValue("type")!=null &&
    			!element.attributeValue("type").equalsIgnoreCase("API")||
    			element.attributeValue("method")!=null ){
	        List <Element> elmlist=element.elements();
	        for(Element elm: elmlist){
	            if(elm.getName().equalsIgnoreCase("url")){	                
	                if(!elm.getText().contains("http")&&ConfigLoader.getConfig("WEB_SERVICE_URL")!=null){
	        		    this.url=(ConfigLoader.getConfig("WEB_SERVICE_URL").replace("[env]", ConfigLoader.getConfig("QA_TEST_ENV")))
	        		    + elm.getText();
	        		    
	                }else{
	                	 String vurl=elm.getText();
	                	 if(vurl!=null &&vurl.contains("[env]")){
	                		 this.url=vurl.replace("[env]", ConfigLoader.getConfig("QA_TEST_ENV"));
	                	 }else{
	                		 this.url=elm.getText();
	                	 }
	                }
	            }else  if(elm.getName().equalsIgnoreCase("param")){
	            	if(elm.hasContent() && elm.elements().size()>0){
	            		Element elParam=(Element)elm.elements().get(0);
	            		addUrlParam(elm.attributeValue("name"),elParam.asXML());
	            		
	            	}else{
	            		addUrlParam(elm.attributeValue("name"),elm.getText());
	            	}
	            }else  if(elm.getName().equalsIgnoreCase("assert")){
	                addUrlAseert(elm.attributeValue("name"), elm);
	            }else  if(elm.getName().equalsIgnoreCase("items")){	            	
	            	this.itemListElm.add(elm);	            	
	            }
	        }
    	}
    	
    }
   /*
    * Add all associated URL to the page
    * @param param - Parameter for the URL
    * @param config - value of the parameter
    */
    public void addUrlParam(String param,String value){
        this.paramlist.put(param, value);
    }
    
    /*
     * Retrieve all parameter set associated with this URL config
     */
    public Set getUrlParamset(){
        return (this.paramlist.keySet());
    }
    /*
     * Retrieve value of any parameter associated with this URL
     */
    public String getUrlParamValue(String param){
        return (String) (this.paramlist.get(param));
    }
    
    /*
     * Retrieve value of any parameter associated with this URL
     */
    public String getUrlReplacedItemIdParamValue(String param){
        return (String) (this.newParamlist.get(param));
    }
    /*
     * Retrieve all parameter set associated with this URL config
     */
    public Set getAssertKeyset(){
        return (this.assertion.keySet());
    }
    
    /*
     * Retrieve url element with this URL config
     */
    public Element getUrlElement(){
        return (this.urlElement);
    }
    
    
    /*
     * Retrieve itemlist element with this URL config
     */
    public List<Element> getItemListElement(){
        return (this.itemListElm);
    }
    
    /*
     * Add all associated URL to the page
     * @param param - Parameter for the URL
     * @param config - value of the parameter
     */
     public void addUrlAseert(String param,Element element){
         this.assertion.put(param, element);
     }
    /*
     * Retrieve value of any parameter associated with this URL
     */
    public Element getAssertKeyValue(String param){
        return (Element) (this.assertion.get(param));
    }
    
    /*
     * Retrieve any URL associated to this config 
     */
    public String getUrl(){
        return (url);
    }
    
    /*
     * Set URL associated to this config 
     */
    public void setUrl(String url){
        this.url=url;
    }
    
    /*
     * This method is called before invoking the URL if there is any item list exists
     */
    public void setItemList(){
    	this.itemlist=new HashMap<String, String>();
    	for(Element itemElm:this.itemListElm){
			List <Element> items=itemElm.elements();
			for(Element item:items){
				String id=item.attributeValue("id");
				String value=getRamdomItemId(item.attributeValue("value"));
				itemlist.put(id,value);
			}
    	}
    	replaceGlobalItemIdForUrlParam();
    }
    	
    private String getRamdomItemId(String globalItemTag){
        //search for global element
     String globalItems=   ConfigLoader.getGlobalDataFieldValue(globalItemTag);
     String randomValues[]=globalItems.split(",");
     Random generator = new Random();      
     int randomIndex = generator.nextInt( randomValues.length );
     return(randomValues[randomIndex].trim());                         
     
   }
   
    private void replaceGlobalItemIdForUrlParam(){
    	String newParmVal="";
    	this.newParamlist=new HashMap<String, String>(); 
    	for(Object param:paramlist.keySet()){
			String paramVal=paramlist.get(param);		  
			for(Object item: itemlist.keySet()){    		
				newParmVal=paramVal.replace("@"+item, itemlist.get(item)); 
				paramVal=newParmVal;
    		}
				//System.out.println(newParmVal);
				newParamlist.put(param.toString(), newParmVal);
		  
			
    	}
    	
    }
    
    public String replaceGlobalItemIdForUrl(String url){
    		String newurl="";
    		if(itemlist!=null){
				for(Object item: itemlist.keySet()){    		
					newurl=url.replace("@"+item, itemlist.get(item)); 
					url=newurl;
	    		}
    		}else{
    			return url;
    		}
			return newurl;
    	}
   
}

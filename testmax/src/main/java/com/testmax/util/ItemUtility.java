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

import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultAttribute;
import com.testmax.framework.ConfigLoader;
import com.testmax.framework.WmLog;

public class ItemUtility {
	
	/*
	 * Replace single item_id from the XML which is matching the name
	 * @param itemNode= Item for which ship options
	 */
	public Element replaceVariable(Element elm, String var, String value){
		Element elmNew=null;	
		
		try {
			String xml=elm.asXML().replaceAll("@"+var, value);
			elmNew = new SAXReader().read( new StringReader(xml)).getRootElement();
		} catch (DocumentException e) {
			WmLog.getCoreLogger().info(">>>Exception:<<<"+this.getClass().getName()+">>> Failed to replace parameter "+var +" for xml="+elm.asXML());
			System.out.println(">>>Exception:<<<"+this.getClass().getName()+">>> Failed to replace parameter "+var +" for xml="+elm.asXML());
			WmLog.getCoreLogger().info(">>>Exception:<<<"+this.getClass().getName()+">>> Failed to replace parameter "+var +" with value="+value);
	        System.out.println(">>>Exception:<<<"+this.getClass().getName()+">>> Failed to replace parameter "+var +" with value="+value);
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			WmLog.getCoreLogger().info(">>>Exception:<<<"+this.getClass().getName()+">>> Failed to replace parameter "+var +" for xml="+elm.asXML());
			System.out.println(">>>Exception:<<<"+this.getClass().getName()+">>> Failed to replace parameter "+var +" for xml="+elm.asXML());
			WmLog.getCoreLogger().info(">>>Exception:<<<"+this.getClass().getName()+">>> Failed to replace parameter "+var +" with value="+value +"for xml="+elm.asXML());
	        System.out.println(">>>Exception:<<<"+this.getClass().getName()+">>> Failed to replace parameter "+var +" with value="+value +"for xml="+elm.asXML());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return(elmNew);
	}
	public Element replaceTagLib(Element vTag ){
		
		Element elmNew=null;
		String prm="";
		//replace tag lib if included
			try {
				String includes=vTag.getText();		
				String xml=vTag.asXML();
				if(includes!=null &&includes.split("@").length>=0){
					
					for(Object key:includes.split("@")){
						prm=key.toString();
						xml=xml.replace("@"+key, ConfigLoader.getTagLibByKey(key.toString()).asXML());
					}
					//System.out.println(xml);
				}
				elmNew = new SAXReader().read( new StringReader(xml)).getRootElement();
				
			} catch (DocumentException e) {
				WmLog.getCoreLogger().info(">>>Exception:<<<"+this.getClass().getName()+">>> Failed to replace parameter "+prm);
		        System.out.println(">>>Exception:<<<"+this.getClass().getName()+">>> Failed to replace parameter "+prm);		     
				
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception e) {
				 WmLog.getCoreLogger().info(">>>Exception:<<<"+this.getClass().getName()+">>> Failed to replace parameter "+prm);
		         System.out.println(">>>Exception:<<<"+this.getClass().getName()+">>> Failed to replace parameter "+prm);		     
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
		
		return(elmNew);
	}
	public Element replaceIncludeInSqlQuery(Element vQuery ){
		
		Element elmNew=null;
		String prm="";
		//replace sql lib if included
			try {
				String includes=vQuery.attributeValue("includes");		
				String xml=vQuery.asXML();
				if(includes!=null &&includes.split(",").length>=0){
					
					for(Object key:includes.split(",")){
						prm=key.toString();
						xml=xml.replace("@"+key, ConfigLoader.getSqlLibByKey(key.toString()));
					
					}
					//System.out.println(xml);
				}
				elmNew = new SAXReader().read( new StringReader(xml)).getRootElement();
				
			} catch (DocumentException e) {
				 WmLog.getCoreLogger().info(">>>Exception:<<<"+this.getClass().getName()+">>> Failed to replace parameter "+prm);
		         System.out.println(">>>Exception:<<<"+this.getClass().getName()+">>> Failed to replace parameter "+prm);		     
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception e) {
				 WmLog.getCoreLogger().info(">>>Exception:<<<"+this.getClass().getName()+">>> Failed to replace parameter "+prm);
		         System.out.println(">>>Exception:<<<"+this.getClass().getName()+">>> Failed to replace parameter "+prm);		     
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		
		return(elmNew);
	}
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
				 WmLog.getCoreLogger().info(">>>Exception:<<<"+this.getClass().getName()+">>> Failed to replace itemId="+itemIdValue+" in XML="+xml);
		         System.out.println(">>>Exception:<<<"+this.getClass().getName()+">>> Failed to replace itemId="+itemIdValue+" in XML="+xml);		     
				
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception e) {
				 WmLog.getCoreLogger().info(">>>Exception:<<<"+this.getClass().getName()+">>> Failed to replace itemId="+itemIdValue+" in XML="+xml);
		         System.out.println(">>>Exception:<<<"+this.getClass().getName()+">>> Failed to replace itemId="+itemIdValue+" in XML="+xml);		     
				
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
		return(newQuery);
	}
	public Element replaceData(Element target,Element data){
		  //ItemUtility iu=new ItemUtility();
		  String xml=target.asXML();
		  List<DefaultAttribute> attrs=data.attributes();
			for(DefaultAttribute attr:attrs){
				xml=xml.replaceAll("@"+attr.getQualifiedName(), attr.getStringValue().trim());
			}
			Element newElm=this.getRootElementFromXML(xml);
			
			return newElm;
	  }
	public Element replaceData(Element target,HashMap<String,String> varmap){
		  //ItemUtility iu=new ItemUtility();
		  String xml=target.asXML();
		 
			for(String key :varmap.keySet()){
				xml=xml.replaceAll("@"+key, varmap.get(key).trim());
			}
			Element newElm=this.getRootElementFromXML(xml);
			
			return newElm;
	  }
 	
	/*
	 * Set variable with value
	 * @varlist itemNode= Item for which ship options
	 */
	public void setVariablesValue( List<Element> varlist,
			HashMap resultParamSet, HashMap resultDataSet, HashMap<String,String> varmap){
		
		ArrayList<ArrayList<Object>> rsData=null;	
		
			for(Element var:varlist){
				var=replaceData(var,varmap);
				Element elmChange=null;
				String id=var.attributeValue("id");
				String value=var.attributeValue("value");				
				String rowIndex=var.attributeValue("index");
				String seperator=var.attributeValue("seperator");
				String global=var.attributeValue("global");
				String stripchar=var.attributeValue("stripchar");
				boolean is_number=true;
				if(id!=null &&!id.isEmpty()){
					try{					 
						 Double intval=Double.parseDouble(value);
					}catch (NumberFormatException e){
						is_number=false;
					}
					//this below attribute sets value to the global list
					
					if(value.indexOf("global:")>=0){
						if(rowIndex!=null&&!rowIndex.isEmpty()){
							 value=new ItemUtility().getGlobalTagValueByIndex(value, new Integer(rowIndex),seperator);
						 }else{
							 value=ConfigLoader.getGlobalDataFieldValue(value);
						 }
						
					 }
					else if(!is_number && value.contains(".") && !value.contains("\\.") &&!value.contains("ws:")&&!value.contains("@") && !value.contains("http")){
						try{
							 String prm=value.split("\\.")[0];
							 String field=value.split("\\.")[1];
							 String rsType=resultParamSet.get(prm).toString();					
							 if(rsType.equalsIgnoreCase("query")||rsType.equalsIgnoreCase("cursor")){					 
								 rsData= (ArrayList<ArrayList<Object>>) resultDataSet.get(prm);
								 if(rsData!=null){
									 if(rowIndex==null||rowIndex.isEmpty()){
										value=getColumnValue(rsData,rsData.size()-1,field);
									 }else{
										 value=getColumnValue(rsData,Integer.valueOf(rowIndex),field);
									 }
								 }
							 }
						}catch (Exception e){
							WmLog.printMessage("###### ERROR: Please Chcek whether this '.' or '$' operator is used for file extension. Then use \\. or \\$ instead. In TestMax '.' is used to invoke object properties. USING VARIABLE with value="+value);
							//e.printStackTrace();
						}
					}else if(!is_number && value.contains("ws:")){
						String elmpath=value;
						try{
							value=resultDataSet.get(value).toString();
						}catch (Exception e){
							String [] objlist=elmpath.split(":"); 
							if(objlist[1]!=null &&!objlist[1].equalsIgnoreCase("ws")){
								String wsResponse=resultDataSet.get(objlist[1]).toString();
								DomUtil utl= new DomUtil();
								value=utl.getNodeElementValueXmlString(wsResponse,objlist[objlist.length-1],new Integer(rowIndex));							
								//System.out.println("wsResponse="+wsResponse);
								//System.out.println("value="+value);
							}
						}
					}else if(!is_number && value.contains("junit:")){
						  try{
							value=resultDataSet.get(value).toString();
						  }catch (Exception e){
						  }
					}
					if(value.contains("\\.")){
						value=value.replace("\\.", ".");
					}
					varmap.put(id,value);
					
					if(global!=null &&global.equalsIgnoreCase("yes")){
						if(stripchar!=null &&!stripchar.isEmpty()){
							value=value.replace(stripchar, "");
						}
						ConfigLoader.addGlobalField("global:"+id, value);
					}
				}
			}
	
	}
	
	/*
	 * Set variable with value
	 * @varlist itemNode= Item for which ship options
	 */
	public void setItemValue( List<Element> itemlist,
			 HashMap<String,String> varmap){
		
			for(Element var:itemlist){
			
				String id=var.attributeValue("id");
				String value=var.attributeValue("value");				
				String itemIndex=var.attributeValue("index");
				String itemIdValue=(value.contains(":")? getRamdomItemId(value,itemIndex):value);			
				
				varmap.put(id,itemIdValue);
			}
	
	}
	public HashMap<String, String> extractOldJunitResultDataSet(HashMap<String, Object> resultDataSet,HashMap<String, String> currentDataSet){
		HashMap<String, String> tmpjunitvars=new HashMap<String, String>();
		for(String key:resultDataSet.keySet()){
			Object result= resultDataSet.get(key);
			if(key.contains("junit:") &&result!=null){
				String value=currentDataSet.get(key);
				if(value==null||value.isEmpty()){
					tmpjunitvars.put(key, result.toString());
				}
			}
		}
		return tmpjunitvars;
	}
	/*
	 * Replace  variables from the XML which is matching the name
	 * @param itemNode= Item for which ship options
	 */
	public List<Element> replaceValidatorElementVariables(List<Element> elmList, List<Element> varlist,
			HashMap resultParamSet, HashMap resultDataSet){
		List<Element> newElms= new ArrayList();
		Element elmNew=null;
		ArrayList<ArrayList<Object>> rsData=null;
		String fail_msg="";
		String png="";
		
			for(Element elm:elmList){
				elmNew=elm;
				for(Element var:varlist){
					
					Element elmChange=null;
					String id=var.attributeValue("id");
					String value=var.attributeValue("value");				
					String rowIndex=var.attributeValue("index");
					String seperator=var.attributeValue("seperator");
					String global=var.attributeValue("global");
					String stripchar=var.attributeValue("stripchar");
						if(id!=null && !id.isEmpty()){
						boolean is_number=true;					try{					 
							 Double intval=Double.parseDouble(value);
						}catch (NullPointerException e){
							is_number=false;
						
						}catch (NumberFormatException e){
							is_number=false;
						}
						
						fail_msg="id="+id +" , value="+value +", rowIndex="+rowIndex ;
						//this below attribute sets value to the global list
						
						try{
							
							if(value!=null && value.indexOf("global:")>=0){							 
								 if(rowIndex!=null&&!rowIndex.isEmpty()){
									 value=new ItemUtility().getGlobalTagValueByIndex(value, new Integer(rowIndex),seperator);
								 }else{
									 value=ConfigLoader.getGlobalDataFieldValue(value);
								 }
								
							 }				
							
							else if(!is_number && value.contains(".") && !value.contains("\\.")&& !value.contains("@") &&!value.contains("ws:")){
								try{ 
									String prm=value.split("\\.")[0];
									 String field=value.split("\\.")[1];
									 String rsType=resultParamSet.get(prm).toString();					
									 if(rsType.equalsIgnoreCase("query")||rsType.equalsIgnoreCase("cursor")){					 
										 rsData= (ArrayList<ArrayList<Object>>) resultDataSet.get(prm);
										 if(rsData!=null){
											 if(rowIndex==null||rowIndex.isEmpty()){
												value=getColumnValue(rsData,rsData.size()-1,field);
											 }else{
												 value=getColumnValue(rsData,Integer.valueOf(rowIndex),field);
											 }
										 }
									 }
								}catch (Exception e){
									WmLog.printMessage("###### ERROR: Please Chcek whether this '.' or '$' operator is used for file extension. Then use \\. or \\$ instead. In TestMax '.' is used to invoke object properties.");
									//e.printStackTrace();
								}
							}else if(!is_number && value.contains("ws:")){
								String elmpath=value;
								try{
									value=resultDataSet.get(value).toString();
								}catch (Exception e){
									String [] objlist=elmpath.split(":"); 
									if(objlist[1]!=null &&!objlist[1].equalsIgnoreCase("ws")){
										String wsResponse=resultDataSet.get(objlist[1]).toString();
										DomUtil utl= new DomUtil();
										value=utl.getNodeElementValueXmlString(wsResponse,objlist[objlist.length-1],new Integer(rowIndex));							
										//System.out.println("wsResponse="+wsResponse);
										//System.out.println("value="+value);
									}
								}
							}else if(!is_number && value.contains("junit:")){						
							try{
								png=value.split(":")[1];							
								value=resultDataSet.get(value).toString();
								//System.out.println(value);
								if(value==null||value.isEmpty()){
									value="NULL";
								}
							}catch (Exception e){
								value="NULL";
							}
						}
						}catch(Exception e){
							 WmLog.getCoreLogger().info(">>>Exception:<<<"+this.getClass().getName()+">>> Failed to replace parameter.  Possible problem with '.' dot operator!"+fail_msg );
					         System.out.println(">>>Exception:<<<"+this.getClass().getName()+">>> Failed to replace parameter.  Possible problem with '.' dot operator! "+fail_msg);
					         e.printStackTrace();
					
						}
						//System.out.println("vWselm="+item.asXML());
						if(value!=null &&!value.isEmpty()){	
							Boolean hasVar=false;
							if(png!=null &&!png.isEmpty()){							
								hasVar=elmNew.asXML().contains(png);
							}
							elmChange=this.replaceVariable(elmNew,id, value);
							elmNew=elmChange;
							if(hasVar){
								elmNew.addAttribute("png", png);
							}
							if(global!=null &&global.equalsIgnoreCase("yes")){
								if(stripchar!=null &&!stripchar.isEmpty()){
									value=value.replace(stripchar, "");
								}
								ConfigLoader.addGlobalField("global:"+id, value);
							}
						}
					}
				}
				newElms.add(elmNew);
			}
		
		return(newElms);
	}
	/*
	 * Replace  item_id from the XML which is matching the name
	 * @param itemNode= Item for which ship options
	 */
	public List<Element> replaceValidatorElementItemId(List<Element> elmList, List<Element> itemlist){
		List<Element> newElms= new ArrayList();
		Element elmNew=null;
		for(Element elm:elmList){
			elmNew=elm;
			for(Element item:itemlist){
				Element elmChange=null;
				//System.out.println("vWselm="+item.asXML());
				String itemId=item.attributeValue("id");
				String globalItemTag=item.attributeValue("value");
				String itemIndex=item.attributeValue("index");
				String itemIdValue=(globalItemTag.contains(":")? getRamdomItemId(item.attributeValue("value"),itemIndex):globalItemTag);
				if(globalItemTag!=null){
					elmChange=this.replaceVariable(elmNew,itemId, itemIdValue);
					elmNew=elmChange;
				}
			}
			newElms.add(elmNew);
		}
		return(newElms);
	}
	
	private String getRamdomItemId(String globalItemTag, String itemIndex){
        //search for global element
	     String globalItems=   ConfigLoader.getGlobalDataFieldValue(globalItemTag);
	     String randomValues[]=globalItems.split(",");
	     if(itemIndex!=null &&!itemIndex.isEmpty()){
		     int index=Integer.parseInt(itemIndex);
		     if(randomValues.length<=Integer.parseInt(itemIndex)){
		    	 index=randomValues.length-1;
		     }
		     if(itemIndex!=null && !itemIndex.isEmpty() &&index>=0){
		    	 
		    	 return(randomValues[index].trim());     
		     }
	     }
	     Random generator = new Random();      
	     int randomIndex = generator.nextInt( randomValues.length );
	     
	     return(randomValues[randomIndex].trim());                         
     
   }
	
	/*
	 * Replace all item_id from the XML which is matching the name
	 * @param key= key should be with "@" key name in the XML. 
	 * While passing the key pass only key name without "@"
	 */
	public Element replaceAllGlobalData(Element elm, String key, String value){
		Element elmNew=null;		
		String xml=elm.asXML().replaceAll("@"+key, value);
		//System.out.println(xml);
		try {
			elmNew = new SAXReader().read( new StringReader(xml)).getRootElement();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return(elmNew);
	}
	
	public Document getDocument(String xml){
	    
	     Document doc=null;
	 
	     try{
	         doc=new SAXReader().read( new StringReader(xml));	         
	         
	     }catch(DocumentException e){
	         WmLog.getCoreLogger().info(">>>Exception:<<<"+this.getClass().getName()+">>> Failed in reading XML Input data to the Webservice ");
	         System.out.println(">>>Exception:<<<"+this.getClass().getName()+">>> Failed in reading XML Input data to the Webservice. \n"+xml);          
	     } 
	     
	     return doc;
	 }
	
	/*
	 * Root element from xml
	 * @param xml
	 */
	public Element getRootElementFromXML( String xml){
		Element elmNew=null;		
		
		try {
			elmNew = new SAXReader().read( new StringReader(xml)).getRootElement();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return(elmNew);
	}
	
	/*
	 * Element value from XML for a xpath
	 * @param xml & xpath of the node
	 */
	public String getElementValueByXpath( String xml, String xpath,String index){
		String val="";
		int idx=Integer.parseInt(index);
		int count=0;
		//System.out.println(xml);
		List<Node> nodes = this.getDocument(xml).selectNodes(xpath);
		for(Node n:nodes ){
			if(count==idx){
			 val=n.getText();
			 break;
			}
			count++;
		}
			
		return(val);
	}
	
	public String getGlobalTagValueByIndex(String globalDataTag, int index,String seperator){
	    //search for global element
	 if (seperator==null ||seperator.isEmpty()){
		 seperator=",";
	 }
	 String globalItems=   ConfigLoader.getGlobalDataFieldValue(globalDataTag);
	 String randomValues[]=globalItems.split(seperator);
	 if(index<randomValues.length){
		 return randomValues[index].trim();
	 }
	 if(randomValues!=null &&randomValues.length>1){
		 Random generator = new Random();      
		 int randomIndex = generator.nextInt( randomValues.length );
		 return(randomValues[randomIndex].trim());  
	 }
	 return(globalItems.trim());

	}
	public String getRamdomGlobalTagValue(String globalDataTag){
	    //search for global element
	 String globalItems=   ConfigLoader.getGlobalDataFieldValue(globalDataTag);
	 String randomValues[]=globalItems.split(",");
	 if(randomValues!=null &&randomValues.length>0){
		 Random generator = new Random();      
		 int randomIndex = generator.nextInt( randomValues.length );
		 return(randomValues[randomIndex].trim());  
	 }
	 return(globalItems.trim());

	}
	
	public String getRamdomItemId(String globalItemTag){
		    //search for global element
		 String globalItems=   ConfigLoader.getGlobalDataFieldValue(globalItemTag);
		 String randomValues[]=globalItems.split(",");
		 if(randomValues!=null &&randomValues.length>0){
			 Random generator = new Random();      
			 int randomIndex = generator.nextInt( randomValues.length );
			 return(randomValues[randomIndex].trim());  
		 }
		 return(globalItems);
 
	}
	public String getRandomNumber(){
		 Random generator = new Random();      
		return(String.valueOf(generator.nextInt(999999999)));
	}
	
	public String getSmallRandomNumber(){
		 Random generator = new Random();      
		return(String.valueOf(generator.nextInt(999)));
	}
	public String getColumnValue(ArrayList<ArrayList<Object>> rsData,int rowId, String column){
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
	 public boolean validate(String actual, String value, String operator, String datatype){
	    	
	    	 if(actual!=null&&!actual.isEmpty() 
	    			 &&value!=null && !value.isEmpty() 
	    			 &&datatype!=null && datatype.equalsIgnoreCase("NUMBER")){
	    		double expt=Double.valueOf(actual);
	    		long val=Long.valueOf(value);
	    		
		    	if(operator.equalsIgnoreCase("Gt")){
		    		if(expt>val){
		    			return true;
		    		}else{
		    			return false;
		    		}
		    	}else if (operator.equalsIgnoreCase("Eq")){
		    		if(expt==val){
		    			return true;
		    		}else{
		    			return false;
		    		}
		    		
		    	}else if (operator.equalsIgnoreCase("GtEq")){
		    		if(expt>=val){
		    			return true;
		    		}else{
		    			return false;
		    		}
		    		
		    	}else if (operator.equalsIgnoreCase("Lt")){
		    		if(expt<val){
		    			return true;
		    		}else{
		    			return false;
		    		}
		    		
		    	}else if (operator.equalsIgnoreCase("LtEq")){
		    		if(expt<=val){
		    			return true;
		    		}else{
		    			return false;
		    		}
		    	}else if (operator.equalsIgnoreCase("Nq")){
		    		if(expt!=val){
		    			return true;
		    		}else{
		    			return false;
		    		}
		    	}
		    	
	    	}else if(actual!=null &&!actual.isEmpty() 
	    			&&value!=null && !value.isEmpty() 
	    			&&datatype!=null && datatype.equalsIgnoreCase("STRING")){
		    	if(operator.equalsIgnoreCase("Nq")){
		    		if(actual!=null &&!actual.equalsIgnoreCase(value)){
		    			return true;
		    		}else{
		    			return false;
		    		}
		    	}else if (operator.equalsIgnoreCase("Eq")){
		    		if(actual!=null &&actual.equalsIgnoreCase(value)){
		    			return true;
		    		}else{
		    			return false;
		    		}
		    	}else if (operator.equalsIgnoreCase("Has")){
		    		if(actual!=null &&actual.contains(value)){
		    			return true;
		    		}else{
		    			return false;
		    		}
		    	}else if (operator.equalsIgnoreCase("NotHas")||operator.equalsIgnoreCase("Not Has")){
		    		if(actual!=null &&!actual.contains(value)){
		    			return true;
		    		}else{
		    			return false;
		    		}
		    	}else if (operator.equalsIgnoreCase("NOTNULL")){
		    		if(actual!=null&&!actual.isEmpty()){
		    			return true;
		    		}else{
		    			return false;
		    		}
		    	}else if (operator.equalsIgnoreCase("NULL")||operator.equalsIgnoreCase("NOT NULL")){
		    		if(actual==null  ||actual.isEmpty()){
		    			return true;
		    		}else{
		    			return false;
		    		}
		    	} 
	    	}
	    	else{
	    		if(operator.equalsIgnoreCase("Eq")){
		    		if(actual==null ||actual.isEmpty()){
		    			return true;
		    		}else{
		    			return false;
		    		}
		    	}else if (operator.equalsIgnoreCase("Nq")){
		    		if(actual!=null &&!actual.isEmpty()){
		    			return true;
		    		}else{
		    			return false;
		    		}
		    	}else if (operator.equalsIgnoreCase("Has")){
		    		if(actual!=null &&actual.contains(value)){
		    			return true;
		    		}else{
		    			return false;
		    		}
		    	}else if (operator.equalsIgnoreCase("NotHas")||operator.equalsIgnoreCase("Not Has")){
		    		if(actual!=null &&!actual.contains(value)){
		    			return true;
		    		}else{
		    			return false;
		    		}
		    	}else if (operator.equalsIgnoreCase("NOTNULL")||operator.equalsIgnoreCase("NOT NULL")){
		    		if(actual!=null&&!actual.isEmpty()){
		    			return true;
		    		}else{
		    			return false;
		    		}
		    	}else if (operator.equalsIgnoreCase("NULL")){
		    		if(actual==null  ||actual.isEmpty()){
		    			return true;
		    		}else{
		    			return false;
		    		}
		    	}else
		    		return false;
	    	}
	    	 return false;
	    }
	 
	 
	 
	 
	 public ArrayList<ArrayList<Object>> setResults2Array(ResultSet rs, String validator)  {
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
					String msg=this.getClass().getName()+">> Generating Summery Report, NULL Record.";
					
					WmLog.getCoreLogger().info(" Summary Report>> "+msg+" No Recordset returned!");	
					System.out.println(" Summary Report>> "+msg+" No Recordset returned!");
				}else{
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
	 		return al;  
	 	}
	 
	 public static boolean isEmptyValue(String val){
			if(val==null|| val.isEmpty()){
				return true;
			}
			return false;
		}
	   

}

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

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.testmax.framework.ConfigLoader;
import com.testmax.framework.WmLog;

public class DomUtil {
	
	
	public String getGlobalWebserviceXMLDataset(HashMap<String,String> varmap,String in, String wsname,String stripchar, String nodepath){
    	XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        NodeList nodes =null;
        String attrcount=null;
        String attributes=null;
        String gxml="<global name=\"ws\">\n\t<"+wsname+ " description=\"Dynamic XML data from Webservice Response Node="+nodepath+"\">\n";
       
        try {
        	 InputSource is = new InputSource(new StringReader(in));
			 nodes =(NodeList) xpath.evaluate("//"+nodepath, is, XPathConstants.NODESET);
			 
			 for (int i = 0; i < nodes.getLength(); i++) {
				
				 Node n=nodes.item(i);
				 HashMap<String, String> keymap=this.getAllNodeElementValueMap(n);
				 String data="\t\t<data ";
				 String val="";
				 for( String key:varmap.keySet()){
					 	String tmpval=varmap.get(key);
					 	if(!tmpval.contains(".")){
					 		data=data+key+"=\""+varmap.get(key)+"\" ";
					 	}
					
				 }
				 for( String key:keymap.keySet()){
					
					 if(!key.equals("#text")){
						 if(attrcount==null){
							 if(attributes==null){
								 attributes=key.toString().trim();
							 }else{
								 attributes=attributes+","+key.toString().trim();
							 }
						 }
						 if(stripchar!=null &&!stripchar.isEmpty()&&keymap.get(key)!=null){
							 data=data+key+"=\""+keymap.get(key).replace(stripchar, "")+"\" ";
						 }else{
							 data=data+key+"=\""+keymap.get(key)+"\" ";
						 }
					 }
				 }
				 if(attrcount==null &&attributes!=null){
					 attrcount=String.valueOf(attributes.split(",").length);
				 }
				 gxml+=data+"/>\n";
				 keymap=null;
					
			      
			      }
			 gxml=gxml+"\n\t</"+wsname+">\n</global>";
			
			 ConfigLoader.addGlobalField("global:"+wsname+".count", String.valueOf(nodes.getLength()));
			 ConfigLoader.addGlobalField("global:"+wsname+".attributes", String.valueOf(attributes));
			 ConfigLoader.addGlobalField("global:"+wsname+".attrcount", String.valueOf(attrcount));
			 System.out.println(gxml);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return gxml;
    }
	
	
	public HashMap getNodeListMap(String in, String nodepath){
		HashMap<String, String> valuemap= new HashMap<String, String>();
    	XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        NodeList nodes =null;
        try {
        	 InputSource is = new InputSource(new StringReader(in));
			 nodes =(NodeList) xpath.evaluate("//"+nodepath, is, XPathConstants.NODESET);
			 for (int i = 0, n = nodes.getLength(); i < n; i++) {
				 valuemap.put(String.valueOf(i),  nodes.item(i).getTextContent());
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return valuemap;
    }
	public Document parseXmlString(String in) {
	    try {
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        InputSource is = new InputSource(new StringReader(in));
	        return db.parse(is);
	    } catch (ParserConfigurationException e) {
	        throw new RuntimeException(e);
	    } catch (SAXException e) {
	        throw new RuntimeException(e);
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	}
	
	public Node getNodeFromXmlString(String xml, String nodename,int nodeindex){
		Node n=parseXmlString(xml).getElementsByTagName(nodename).item(nodeindex);
		return n;
	}
	
	public String getNodeElementValueXmlString(String xml, String nodename,int nodeindex){
		Node n=parseXmlString(xml).getElementsByTagName(nodename).item(nodeindex);
		return n.getTextContent();
	}
	public Map<String,String> parseSOAPMessage(String strXML,String[] keys) throws IOException
    {
      
      DOMParser parser = new DOMParser();
      Map<String,String> keymap = null;
      try
      {
      parser.parse(new InputSource(new StringReader(strXML))); 
      }
      catch(org.xml.sax.SAXException sex)
      {
       throw new IOException(sex.getLocalizedMessage());
      }
      Document document = parser.getDocument();
      //See if there is a Fault.
      NodeList list = document.getElementsByTagName ("env:Subcode");
      Node temp = null;
      if(list!=null)
      {
        //Don't process further and create IOException with the SOAP error message.
        StringBuffer message = new StringBuffer();
        temp = list.item(0);
        if(temp!=null && temp.getTextContent()!=null)
             message.append(temp.getTextContent());
        list = document.getElementsByTagName("env:Reason");
        temp = list.item(0); 
        if(temp!=null && temp.getTextContent()!=null)
            message.append(":"+temp.getTextContent());
        if(message.length()>0)
          throw new IOException(message.toString());
      }
      //No Exception thrown, retrieve all the keys.
      keymap = new HashMap<String,String>();
      for(int i=0;i<keys.length;i++)
      {
        String key = keys[i];
        list = document.getElementsByTagName(key); 
       temp = list.item(0);
       if(temp!=null && temp.getTextContent()!=null)
         keymap.put(key,temp.getTextContent());
      }
      return keymap;
    }
	
	public void printNodeMap(HashMap<String,String> elmmap, String nodepath){
		Set<String> keys=elmmap.keySet();
		
		System.out.println("***************************************************");
		System.out.println("                                                    ");
		System.out.println(">>>Printing node elements and values for nodepath="+nodepath+"<<<<<<<<");
		WmLog.getCoreLogger().info(">>>Printing node elements and values for nodepath="+nodepath+"<<<<<<<<");
		for(Object key:keys){
			System.out.println(key +"="+elmmap.get(key));
			WmLog.getCoreLogger().info(key +"="+elmmap.get(key));
		}
		
	}
	public HashMap<String, String> getNodeElementMapByPath(Document xmlDoc,String elmentpath,String elmIndexPath){
		HashMap<String,String> elmmap;
		String[] elmpaths=elmentpath.split(":");
		String[] indexpaths=elmIndexPath.split(":");
		Node node=null;
		
		if(elmpaths.length==0 && indexpaths.length==0){
			  WmLog.getCoreLogger().info(">>>Element and Index path attribute is missing of NULL!");
			  return null;
		}else if(elmpaths.length!=indexpaths.length){
			  WmLog.getCoreLogger().info(">>>No Of Elements does not match with number of indexes!");
			  return null;
		}
		//get first node
		int elmIndex= Integer.parseInt(indexpaths[0]);
		node = xmlDoc.getElementsByTagName(elmpaths[0]).item(elmIndex);
		elmmap= getAllNodeElementValueMap(node);
		if(elmmap!=null){
			for(int i=1;i<elmpaths.length;i++){
				elmIndex= Integer.parseInt(indexpaths[i]);
				Node target=this.getChildNode(node, elmpaths[i], elmIndex);
				if(target!=null){
					node=target;
				}
				if(node!=null &&i==elmpaths.length-1 && node.getChildNodes().getLength()>1){
					elmmap= getAllNodeElementValueMap(node);
				}
			}
		}
		
		return(elmmap);
	}
	
	public Node getChildNode(Node n, String childtag, int childIndex){
		
		int matchedIndex=0;
		NodeList elms=n.getChildNodes();
		  if(elms!=null &&elms.getLength()>1){
			  for(int i=0;i<elms.getLength();i++){
				  Node elm=elms.item(i);				  
				  NodeList childs=elm.getChildNodes();
				  if( elm.getNodeName().equalsIgnoreCase(childtag)){
					  matchedIndex++;
				  }
				  if(elm!=null && childs.getLength()>2 &&matchedIndex==childIndex && elm.getNodeName().equalsIgnoreCase(childtag)){			    				   
  				   return(elm);
  				  
				  }
			  }
		  }
		  return n;
		
	}
	
public HashMap<String, String> getAllNodeElementValueMap(Node n){
	if(n==null) return null;
		NodeList elms=n.getChildNodes();
		HashMap<String, String> keymap=new HashMap<String,String>();
			  if(elms!=null &&elms.getLength()>1){
    			  for(int i=0;i<elms.getLength();i++){
    				  Node elm=elms.item(i);
    				  String key=elm.getNodeName();
    				  String value=elm.getTextContent();
    				  NodeList childs=elm.getChildNodes();
    				  if(elm!=null && childs.getLength()<2){			    				   
	    				  //System.out.println("key="+key +" value="+value);
	    				  keymap.put(key,value);
    				  }
    			  }
			  }else{
					  keymap.put(n.getNodeName(),n.getNodeValue());
				 
			  } 
	    
	      return keymap;
	}
	public HashMap<String, String> getNodeValueByNodeIndex(NodeList list,int nodeno){
		
		Node temp = null;
		HashMap<String, String> keymap=new HashMap<String,String>();
		if(list!=null)	    	  
	      {
	    	  if(list.getLength()>=nodeno){
	    		  temp = list.item(nodeno);	    		  
	    		  if(temp!=null &&temp.getTextContent()!=null){
	    			  NodeList elms=temp.getChildNodes();
	    			  if(elms!=null){
		    			  for(int i=0;i<elms.getLength();i++){
		    				  Node elm=elms.item(i);
		    				  String key=elm.getNodeName();
		    				  String value=elm.getTextContent();
		    				  NodeList childs=elm.getChildNodes();
		    				  if(elm!=null && childs.getLength()<2){			    				   
			    				  //System.out.println("key="+key +" value="+value);
			    				  keymap.put(key,value);
		    				  }
		    			  }
	    			  }
	    		  }
	    	  }
	        
	      }
	      return keymap;
	}	
	
	/**
	   * It returns a found match in the form of String.
	   * @param str - This is a string to search.
	   * @param matchPattern - This is a Regular Expression pattern such as "\\d+" or "\\m+" and so on.
	   * @param caseInsensitive - This is a flag which indicates whether we want to
	   * perform a case insensitive string matching. If it's set to 'true' case insensitive matching is used.
	   * @return
	   * Example: String value = getFoundMatch ("Blahaha went to the 444 Street.", "\\d+");
	   *          value = "444";
	   * //<form name="createAccountForm" method="POST" action="/subflow/YourAccountLoginContext/578363540/sub_generic_login/loginModule.do?prefix=/site_login&page=/ProcessLogin.do" onsubmit="return doSubmit()" id="createAccountForm">
         
          System.out.println(builder.toString());
          String getAction = getFoundMatch(builder.toString(), "action=.*?\\s+",false);
	   */
	  public static String getFoundMatch (String str, String regex, boolean caseInsensitive) {

	      Pattern p = (caseInsensitive) ? Pattern.compile(regex, Pattern.CASE_INSENSITIVE) :
	          Pattern.compile(regex);

	      Matcher m = p.matcher(str);

	      m.find();

	      return String.valueOf(m.group());
	  }
	
}
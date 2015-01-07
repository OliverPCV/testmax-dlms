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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileWriter;
import java.io.IOException;



import com.testmax.framework.WmLog;

public class FileUtility {
	
	public static void  writeToFile(String path, String text)
	  {
	  try{
		  // Create file 
		  FileWriter fstream = new FileWriter(path);
		  BufferedWriter out = new BufferedWriter(fstream);
		  out.write(text);
		  //Close the output stream
		  out.close();
		  }catch (Exception e){//Catch exception if any
		  System.err.println("Error: " + e.getMessage());
		  }
	  }
	public static boolean createDir(String path){
		File f = new File(path);
		boolean success=false;
		if(!f.isFile()&&!f.exists() ){			
			f.mkdirs();
			success=true;
		}
		return(success);
		  
	}
	public static void deleteFile(String file){
		  File f1 = new File(file);
		  boolean success = f1.delete();
		  if (!success){
			  WmLog.getCoreLogger().info("Failed to delete file at: "+file );
			  System.out.println("Deletion failed.");
		  }else{
			  WmLog.getCoreLogger().info("Delete file at: "+file );
			  System.out.println("Delete file at: "+file );
		    }
		  }
	public static void createTextFile(String absolute_path, String text){
		System.out.println("Creating text file at: "+absolute_path );
    	WmLog.getCoreLogger().info("Creating text file at: "+absolute_path );
    	try {
			FileWriter file= new FileWriter(new File(absolute_path));
			file.write(text);			
			file.close();
		 WmLog.getCoreLogger().info("Saved text file at: "+absolute_path );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			WmLog.getCoreLogger().info("Problem Occured while saving the HTML Report file html_report.html for "+absolute_path +" "+e.getMessage() );
		}
    	
    }
	
	public static void renameFile( String oldFileName, String newFileName) {
		try{
			  File oldName = new File(oldFileName);
		      File newName = new File(newFileName);
		      if(oldName.renameTo(newName)) {
		    	  WmLog.getCoreLogger().info("Renamed file "+oldFileName+" to "+newFileName );	         
		      } else {
		    	  WmLog.getCoreLogger().info("Failed to renamed file "+oldFileName+" to "+newFileName );  
		      }
		}catch(Exception e){
			WmLog.getCoreLogger().info("Could not found file "+oldFileName );	 
		}
	   }
	
	 public static void copyFile(String srFile, String dtFile){
		  try{
			  File f1 = new File(srFile);
			  File f2 = new File(dtFile);
			  InputStream in = new FileInputStream(f1);
			  OutputStream out = new FileOutputStream(f2);
		
			  byte[] buf = new byte[1024];
			  int len;
			  while ((len = in.read(buf)) > 0){
				  out.write(buf, 0, len);
			  }
			  in.close();
			  out.close();
			  
		  }catch(FileNotFoundException ex){
			  WmLog.getCoreLogger().info(ex.getMessage() + " in the specified directory." );	        
			  System.out.println(ex.getMessage() + " in the specified directory.");
		  }
		  catch(IOException e){
			  WmLog.getCoreLogger().info(e.getMessage());	        
			  System.out.println(e.getMessage());  
		  }
	  }
		
	
	
 

}

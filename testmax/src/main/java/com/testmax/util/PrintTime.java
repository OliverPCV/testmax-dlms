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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PrintTime {
	
	private long starttime;
	
	public PrintTime(){
		this.starttime=System.currentTimeMillis();
		
	}
	public void reset(){
		starttime=System.currentTimeMillis();
	}
	public String getCurrentDate(){
		return(DateFormat.getDateInstance().format(new Date(System.currentTimeMillis())));
	}
	public long getStartTime(){
		return(this.starttime);
	}
	private long end(){
		return(System.currentTimeMillis());
	}
	public long getTime(){
		return(end()-getStartTime());
	}
	
	public String getPrintTime(){
		return(" ("+getTime()+") in mili Sec ");
	}
	
	public String getDateByFormat(long days,String format){
		 SimpleDateFormat ft = null;
		 Date day=new Date(System.currentTimeMillis()+days*24*60*60*1000);  
		 try{
			 if(format!=null){
				 ft=new SimpleDateFormat (format);
			 }else{
				 ft=new SimpleDateFormat ("MM/dd/yyyy hh:mm:ss");
			 }			 
			return(ft.format(day));
		 }catch (Exception e){
			 ft=new SimpleDateFormat ("MM/dd/yyyy hh:mm:ss");
			
		 }
		return(ft.format(day));
	}
  
	public String getTimeInMiliFromHr(long hrs){
		 long time=(System.currentTimeMillis()+hrs*60*60*1000)/1000;
		 return(String.valueOf(time)); 
	}
	public String getTimeInMiliFromMiniute(long min){
		 long time=(System.currentTimeMillis()+min*60*1000)/1000;
		 return(String.valueOf(time)); 
	}
}
